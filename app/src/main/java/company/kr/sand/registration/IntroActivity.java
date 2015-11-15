package company.kr.sand.registration;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import company.kr.sand.R;
import company.kr.sand.rsibal.MainActivity;

/**
 * Created by Prattler on 2015-10-28.
 */
public class IntroActivity extends Activity {

    Intent it_next;//화면 전환을 위한 intent
    Thread thr_next = new Thread() {

        @Override
        public void run() {

            try {

                Thread.sleep(2500);

                SharedPreferences mPref = getSharedPreferences("ID", MODE_PRIVATE);
                String temp_facebook = mPref.getString("FaceBook", null);
                String temp_naver = mPref.getString("Naver", null);

                if (temp_facebook == null && temp_naver == null) {

                    it_next = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(it_next);

                } else {

                    it_next = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(it_next);
                }

            } catch (InterruptedException e) {

                e.printStackTrace();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

    }

    @Override
    protected void onResume() {

        super.onResume();
        thr_next.start();

    }
}
