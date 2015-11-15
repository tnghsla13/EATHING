package company.kr.sand.rsibal;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import company.kr.sand.R;

public class MainActivity extends ActionBarActivity {

    private MenuItem searchMenuItem = null;
    private FragmentTabHost mTabHost;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        Bundle b;
        b = new Bundle();
        b.putString("key", "Home");
        mTabHost.addTab(mTabHost.newTabSpec("home").setIndicator("", getResources().getDrawable(R.drawable.home)), HomeFragment.class, b);

        b = new Bundle();
        b.putString("key", "Friends");
        mTabHost.addTab(mTabHost.newTabSpec("friends").setIndicator("",getResources().getDrawable(R.drawable.friends)),FriendsFragment.class,b);

        b = new Bundle();
        b.putString("key", "Write");
        mTabHost.addTab(mTabHost.newTabSpec("write").setIndicator("",getResources().getDrawable(R.drawable.write)),WriteFragment.class,b);

        b = new Bundle();
        b.putString("key", "Profile");
        mTabHost.addTab(mTabHost.newTabSpec("profile").setIndicator("",getResources().getDrawable(R.drawable.profile)),ProfileFragment.class,b);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if(searchManager != null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);

        if (searchMenuItem != null)
        {
            if (MenuItemCompat.expandActionView(searchMenuItem))
            {
                MenuItemCompat.collapseActionView(searchMenuItem);
            }
            else if (MenuItemCompat.collapseActionView(searchMenuItem))
            {
                MenuItemCompat.expandActionView(searchMenuItem);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }


    //추가 부분 노 신경
    private long backKeyPressedTime = 0;
    private Toast toast;

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            this.finish();
            toast.cancel();
        }
    }

    private void showGuide() {
        toast = Toast.makeText(this,
                " 종료를 원하시면 한번 더 눌러 주세요 ", Toast.LENGTH_SHORT);
        toast.show();
    }

}
