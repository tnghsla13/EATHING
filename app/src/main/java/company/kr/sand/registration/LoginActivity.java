package company.kr.sand.registration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nhn.android.naverlogin.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import company.kr.sand.R;
import company.kr.sand.rsibal.MainActivity;

/**
 * Created by Prattler on 2015-10-27.
 */

public class LoginActivity extends FragmentActivity {

    //request code
    public static final int CONNECTION_WITH_NAVER = 0;
    public static final int CONNECTION_WITH_FACEBOOK = 1;
    public static final int CONNECTION_FOR_REDUNDANCY_CHECK = 2;
    public static final int REG_COMPLETE = 3;

    //result string
    public static final String ACCOUNT_NOT_EXIST = "account_not_exist";
    public static final String ACCOUNT_EXIST = "account_exist";
    public static final String NICK_NOT_EXIST = "nick_not_exist";
    public static final String NICK_EXIST = "nick_exist";
    public static final String PERFECT = "perfect";

    //redundancy check
    public static boolean REDUNDANCY_CHECK = false;


    private String str_nick;
    private Context mContext; //application context

    private CallbackManager callbackManager; //facebook event callback
    private LoginManager loginManager; //facebook login management
    private OAuthLogin mOAuthLoginInstance; //naver login subject
    private OAuthLoginHandler mOAuthLoginHandler; //naver login management
    private NaverUserInfo naverUserInfo; //naver user profile info
    private FacebookUserInfo facebookUserInfo; //facebook user profile info
    private HttpURLConnection httpURLConnection;
    private ImageRegFragment fg_img;
    private LoginFragment fg_login;
    private NickRegFragment fg_nick;

    private FragmentManager.OnBackStackChangedListener backStackListener;

    //backstack에 추가 되는 시점을 잡는다.
    private void setBackStackListener() {

        backStackListener = new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                String name = null;
                int position = getSupportFragmentManager().getBackStackEntryCount();

                if (position != 0) {
                    FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(position - 1);
                    name = backEntry.getName();
                }

                if (position == 0) {
                    finish();

                } else if (name == "login_frag") {


                    FacebookSdk.sdkInitialize(mContext);
                    callbackManager = CallbackManager.Factory.create();
                    facebookUserInfo = new FacebookUserInfo();
                    facebookLoginInit();

                    // naver
                    mOAuthLoginInstance = OAuthLogin.getInstance();
                    mOAuthLoginInstance.init(mContext, getResources().getString(company.kr.sand.R.string.naver_app_id),
                            getResources().getString(company.kr.sand.R.string.naver_app_secret), getResources().getString(R.string.app_name));

                    naverUserInfo = new NaverUserInfo();
                    naverLoginPrecessInit();

                    REDUNDANCY_CHECK = false;


                } else if (name == "nick_frag") {

                    Button btn_next_alias = (Button) findViewById(R.id.btn_next_alias);
                    Button btn_redundancy = (Button) findViewById(R.id.btn_redunancy);
                    final EditText tf_nick = (EditText) findViewById(R.id.tf_nick);
                    tf_nick.setText("");

                    btn_next_alias.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (REDUNDANCY_CHECK) {

                                if (str_nick.equals(tf_nick.getText().toString()))
                                    setFragment(2);
                                else
                                    Toast.makeText(mContext, "중복확인이 필요합니다", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(mContext, "중복확인이 필요합니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btn_redundancy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            str_nick = tf_nick.getText().toString();

                            if (str_nick.length() < 2 || str_nick.length() > 8) {

                                Toast.makeText(mContext, "2글자 이상 8글자 이하 입력", Toast.LENGTH_SHORT).show();

                            } else {


                                REDUNDANCY_CHECK = true;
                                //connectWithServer(CONNECTION_FOR_REDUNDANCY_CHECK);

                                Toast.makeText(mContext, "사용가능한 닉네임", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                } else {

                    Button btn_next_img = (Button) findViewById(R.id.btn_next_image);

                    btn_next_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //connectWithServer(REG_COMPLETE);
                            Toast.makeText(mContext, "가입완료", Toast.LENGTH_SHORT).show();

                            //명환 코드로 이동
                            Intent it_next = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(it_next);
                            finish();
                        }
                    });

                }

            }
        };

        getSupportFragmentManager().addOnBackStackChangedListener(backStackListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);
        fg_img.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = getApplicationContext();

        fg_img = new ImageRegFragment();
        fg_login = new LoginFragment();
        fg_nick = new NickRegFragment();

        setBackStackListener();
        setFragment(0);

    }

    private void setFragment(int kind) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        System.out.println("asdfasfd");
        switch (kind) {

            case 0:

                ft.replace(R.id.fragment_container, fg_login);
                ft.addToBackStack("login_frag");
                ft.commit();

                break;
            case 1:


                ft.replace(R.id.fragment_container, fg_nick);
                ft.addToBackStack("nick_frag");
                ft.commit();


                break;
            case 2:

                ft.replace(R.id.fragment_container, fg_img);
                ft.addToBackStack("img_frag");
                ft.commit();

                break;
        }

    }

    //Login manager Setting
    private void setLoginManager() {

        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        //To get user information - Graph Api
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),

                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        // Application code

                                        try {
                                            facebookUserInfo.id = object.getString("id");
                                            facebookUserInfo.age_range = object.getString("age_range");
                                            facebookUserInfo.email = object.getString("email");
                                            facebookUserInfo.gender = object.getString("gender");
                                            facebookUserInfo.name = object.getString("name");
                                            facebookUserInfo.last_name = object.getString("last_name");
                                            facebookUserInfo.first_name = object.getString("first_name");
                                            facebookUserInfo.link = object.getString("link");
                                            facebookUserInfo.locale = object.getString("locale");
                                            facebookUserInfo.updated_time = object.getString("updated_time");
                                            facebookUserInfo.timezone = object.getString("timezone");
                                            facebookUserInfo.verified = object.getString("verified");


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

//                                        SharedPreferences mPref = getSharedPreferences("ID", MODE_PRIVATE);
//                                        SharedPreferences.Editor se = mPref.edit();
//                                        se.commit();

                                        //connectWithServer(1);

                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, name, first_name, last_name, age_range, " +
                                "link, gender, locale, timezone, updated_time," +
                                "verified, email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

    }

    //facebook initialize
    private void facebookLoginInit() {

        setLoginManager();
        final Button btn_facebook = (Button) findViewById(R.id.btn_facebook);
        btn_facebook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                loginManager.logOut();
                ArrayList<String> permissionList = new ArrayList<String>();
                permissionList.add("public_profile");
                permissionList.add("email");
                loginManager.logInWithReadPermissions(LoginActivity.this, permissionList);

            }
        });
    }

    //AuthLoginhandler setting
    private void setMOAuthLoginHandler() {
        mOAuthLoginHandler = new OAuthLoginHandler() {

            @Override
            public void run(boolean success) {

                if (success) {

                    new RequestApiTask().execute(); //로그인이 성공하면  네이버에 계정값들을 가져온다.


                } else {// 실패 시


                }
            }
        };
    }

    //naver initialize
    private void naverLoginPrecessInit() {

        setMOAuthLoginHandler();
        final Button btn_naver = (Button) findViewById(R.id.btn_naver);
        btn_naver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mOAuthLoginInstance.logout(mContext);
//                SharedPreferences mPref = getSharedPreferences("ID", MODE_PRIVATE);
//                SharedPreferences.Editor se = mPref.edit();
//                se.remove("Naver");
//                se.commit();

                setFragment(1);
                //connectWithServer(1);

                //    mOAuthLoginInstance.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);

            }
        });
    }


    //To get user information - Async task
    private class RequestApiTask extends AsyncTask<Void, Void, Void> {

        String resultSet = null;

        @Override
        protected Void doInBackground(Void... params) {

            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            resultSet = mOAuthLoginInstance.requestApi(mContext, at, url);
            Pasingversiondata(resultSet);

            return null;
        }


        protected void onPostExecute(Void content) {

//            SharedPreferences mPref = getSharedPreferences("ID", MODE_PRIVATE);
//            SharedPreferences.Editor se = mPref.edit();
//            se.putString("Naver", naverUserInfo.id);
//            se.commit();

            //connectWithServer(0);

            //서버와 연결이 이루어져 가입화면으로 넘어간다고 가정


        }


        //XML parsing
        private void Pasingversiondata(String data) { // xml 파싱

            String arr_info[] = new String[9];

            try {

                XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserCreator.newPullParser();
                InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
                parser.setInput(input, "UTF-8");

                int parserEvent = parser.getEventType();
                String tag;
                boolean isText = false;
                int Idx = 0;


                while (parserEvent != XmlPullParser.END_DOCUMENT) {

                    switch (parserEvent) {

                        case XmlPullParser.START_TAG:

                            tag = parser.getName();

                            if (tag.compareTo("xml") == 0) {

                                isText = false;

                            } else if (tag.compareTo("data") == 0) {

                                isText = false;

                            } else if (tag.compareTo("result") == 0) {

                                isText = false;

                            } else if (tag.compareTo("resultcode") == 0) {

                                isText = false;

                            } else if (tag.compareTo("message") == 0) {

                                isText = false;

                            } else if (tag.compareTo("response") == 0) {

                                isText = false;

                            } else {

                                isText = true;

                            }

                            break;

                        case XmlPullParser.TEXT:

                            tag = parser.getName();

                            if (isText) {

                                if (parser.getText() == null) {

                                    arr_info[Idx] = "";

                                } else {

                                    arr_info[Idx] = parser.getText().trim();

                                }

                                Idx++;

                            }

                            isText = false;

                            break;

                        case XmlPullParser.END_TAG:

                            tag = parser.getName();

                            isText = false;

                            break;

                    }

                    parserEvent = parser.next();

                }

                naverUserInfo.email = arr_info[0];
                naverUserInfo.nickname = arr_info[1];
                naverUserInfo.enc_id = arr_info[2];
                naverUserInfo.profile_image = arr_info[3];
                naverUserInfo.age = arr_info[4];
                naverUserInfo.gender = arr_info[5];
                naverUserInfo.id = arr_info[6];
                naverUserInfo.name = arr_info[7];
                naverUserInfo.birthday = arr_info[8];


            } catch (Exception e) {

                Log.e("dd", "Error in network call", e);

            }
        }
    }

    // our server connection part
    public void connectWithServer(final int flag) {

        Thread thr_communication = new Thread() {

            @Override
            public void run() {

                String response = null;
                StringBuffer strBuf = new StringBuffer();
                String url = "서버 URL 입력 예시) http://localhost:5000/test.py";

                if (flag == CONNECTION_WITH_NAVER) {


                    strBuf.append("id=" + naverUserInfo.id);
                    strBuf.append("&name=" + naverUserInfo.name);
                    strBuf.append("&gender=" + naverUserInfo.gender);
                    strBuf.append("&age_range=" + naverUserInfo.age);
                    strBuf.append("&email=" + naverUserInfo.email);
                    strBuf.append("&type=" + "login");
                    strBuf.append("&kind=" + "naver");
                    //server


                } else if (flag == CONNECTION_WITH_FACEBOOK) {


                    strBuf.append("id=" + facebookUserInfo.id);
                    strBuf.append("&name=" + facebookUserInfo.name);
                    strBuf.append("&gender=" + facebookUserInfo.gender);
                    strBuf.append("&age_range=" + facebookUserInfo.age_range);
                    strBuf.append("&email=" + facebookUserInfo.email);
                    strBuf.append("&type=" + "login");
                    strBuf.append("&kind=" + "facebook");


                } else if (flag == CONNECTION_FOR_REDUNDANCY_CHECK) {

                    strBuf.append("nick=" + str_nick);
                    strBuf.append("&type=" + "nick_check");

                } else if (flag == REG_COMPLETE) {

                    //완료 메세지 및 이미지 전송
                }

                try {

                    URL urlCon = new URL(url);
                    httpURLConnection = (HttpURLConnection) urlCon.openConnection();

                    if (httpURLConnection != null) {

                        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setDefaultUseCaches(false);

                        OutputStream os = new BufferedOutputStream(httpURLConnection.getOutputStream());
                        os.write(strBuf.toString().getBytes("UTF-8"));

                        InputStreamReader isr = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
                        BufferedReader br = new BufferedReader(isr);
                        response = br.readLine();

                        httpURLConnection.disconnect();
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //각 response code에 맞는 string을 읽어 왔을때의 처리

            }
        };


        thr_communication.start();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }


}
