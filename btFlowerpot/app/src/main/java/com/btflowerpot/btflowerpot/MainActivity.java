package com.btflowerpot.btflowerpot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.btflowerpot.btflowerpot.fragments.page_adapter;
import com.btflowerpot.btflowerpot.netWork.connection;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private   page_adapter adapter;
    private ViewPager viewPager;
    private static String TAG = "MainActivity";

    private View navigationView_headerView;
    private TextView userNameTextView;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //初始化侧边菜单栏
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView_headerView = navigationView.getHeaderView(0);
        userNameTextView = (TextView)navigationView_headerView.findViewById(R.id.userNameTextView);
        //设置菜单栏的用户名位置
        SharedPreferences mySharedPreferences=getSharedPreferences("UserInfo",Activity.MODE_WORLD_READABLE+Activity.MODE_WORLD_WRITEABLE );
        if(mySharedPreferences.contains("name")) {
            String name = mySharedPreferences.getString("name", null);
            userNameTextView.setText(name);
            navigationView.getMenu().getItem(0).setTitle(name);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("主页"));
        tabLayout.addTab(tabLayout.newTab().setText("上传文件"));
        tabLayout.addTab(tabLayout.newTab().setText("历史数据"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new page_adapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
    public synchronized void onPause() {
        super.onPause();
         Log.e(TAG, "-MainActivity ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
         Log.e(TAG, "-- MainActivity ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "--- MainActivity ON DESTROY ---");
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Login) {
            // Handle the camera action
            SharedPreferences mySharedPreferences=getSharedPreferences("UserInfo",Activity.MODE_WORLD_READABLE+Activity.MODE_WORLD_WRITEABLE );
            String name = mySharedPreferences.getString("name", null);
            String password = mySharedPreferences.getString("password",null);
            if(name == null && password == null){
                Log.i("MainActivity","start LoginActivity");
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
            else{
                new Thread(LoginTask).start();


            }
        } else if (id == R.id.nav_logout) {
            SharedPreferences mySharedPreferences=getSharedPreferences("UserInfo",Activity.MODE_WORLD_READABLE+Activity.MODE_WORLD_WRITEABLE );
            SharedPreferences.Editor myEditor = mySharedPreferences.edit();
            myEditor.clear();
            myEditor.apply();
            userNameTextView.setText("请您登录，体验完整功能。");
            navigationView.getMenu().getItem(0).setTitle("登录");

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    Runnable LoginTask = new Runnable() {

        @Override
        public void run() {
            SharedPreferences mySharedPreferences=getSharedPreferences("UserInfo",Activity.MODE_WORLD_READABLE+Activity.MODE_WORLD_WRITEABLE );
            String name = mySharedPreferences.getString("name", null);
            String password = mySharedPreferences.getString("password",null);
            int result = 0;
            try {
                // Simulate network access.
                String data = "user_name=" + URLEncoder.encode(name, "utf-8") + "&user_password="+ URLEncoder.encode(password,"utf-8");
                String url = "http://192.168.191.1:8000/login_regest/?"+ data;
                Log.i("MainActivity",url);
                result = connection.Login_Regest(url);
            } catch (InterruptedException e) {
                Log.i("MainActivity","login network error");
            }catch (Exception e) {
                e.printStackTrace();
                Log.i("MainActivity", "login network error");
            }

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("LoginResult", result);
            data.putString("name",name);
            msg.setData(data);
            LoginHandler.sendMessage(msg);
        }
    };
    Handler LoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int result = data.getInt("LoginResult");
            String name = data.getString("name");
            switch (result) {
                case 0:
                    Log.e("MainActivity", "login Unknown error");
                    break;
                case 1:
                    Log.i("MainActivity", "already Login");
                    userNameTextView.setText(name);
                    break;
                case 2:
                    Log.e("MainActivity", "login failed");
                    Log.i("MainActivity", "start LoginActivity");
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

}
