package com.jefeko.apptwoway.ui.common;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.db.DatabaseUtil;
import com.jefeko.apptwoway.http.VolleyHelper;
import com.jefeko.apptwoway.ui.LoginActivity;
import com.jefeko.apptwoway.ui.MainActivity;
import com.jefeko.apptwoway.ui.basic.BasicManageActivity;
import com.jefeko.apptwoway.ui.obtainorder.ObtainOrderManageActivity;
import com.jefeko.apptwoway.ui.order.OrderManageActivity;
import com.jefeko.apptwoway.ui.print.PrintActivity;
import com.jefeko.apptwoway.ui.setting.SettingActivity;
import com.jefeko.apptwoway.ui.waytalk.WayTalkActivity;
import com.jefeko.apptwoway.utils.BackPressCloseUtil;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
    private TextView user_info_name = null;                 //좌측 슬라이드 메뉴 회사명
    private TextView login_check = null;                    //좌측 슬라이드 메뉴 로그아웃
    private ImageView close = null;                          //좌측 슬라이드 메뉴 닫기 버튼
    private TextView top_company_name = null;              //상단 회사명
    private DrawerLayout drawer = null;
    private BackPressCloseUtil mBackPressUtil;
    private ProgressDialog progressDialog = null;
    private DatabaseUtil mDB = null;
    protected Context mContext = null;
    final int WRITE_REQUEST_CODE = 100;
    public VolleyHelper volley;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_blue_24dp);

        //슬라이드 메뉴 헤더 부분 추출
        View headerView = navigationView.getHeaderView(0);
        user_info_name = (TextView) headerView.findViewById(R.id.user_info_name);
        login_check = (TextView) headerView.findViewById(R.id.login_check);
        close = (ImageView) headerView.findViewById(R.id.close);
        top_company_name = (TextView) toolbar.findViewById(R.id.top_company_name);

        user_info_name.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.COMPANY_NAME)));
        top_company_name.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.COMPANY_NAME)));

        login_check.setOnClickListener(this);
        close.setOnClickListener(this);

        initialize();
    }


    public DatabaseUtil getMDB(){
        return mDB;
    }

    /**
     * 초기화
     */
    public void initialize() {
        mBackPressUtil = new BackPressCloseUtil(this);

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        } else {
            mDB = DatabaseUtil.getInstance(this);
        }

        volley = new VolleyHelper(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openTopActivity(SettingActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            openTopActivity(MainActivity.class);
        } else if (id == R.id.nav_obtain_order) {
            openTopActivity(ObtainOrderManageActivity.class);
        } else if (id == R.id.nav_order) {
            openTopActivity(OrderManageActivity.class);
        } else if (id == R.id.nav_way_talk) {
            openTopActivity(WayTalkActivity.class);
        } else if (id == R.id.nav_basic) {
            openTopActivity(BasicManageActivity.class);
        } else if (id == R.id.nav_setting) {
            openTopActivity(SettingActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_check:
                openActivity(LoginActivity.class);
                break;
            case R.id.close:
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.homepage:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.homepage)));

                startActivity(intent);
                break;
        }
    }

    /**
     * Activity open
     * */
    protected void openActivity(Class<?> activity) {
        if ( !getClass().getName().equals(activity.getName()) ) {
            startActivity(new Intent(this, activity));
        }
    }

    protected void openTopActivity(Class<?> activity) {
        startActivity(new Intent(this, activity).addFlags(FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!= null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ( getClass().getName().equals(MainActivity.class.getName()) ) {
                mBackPressUtil.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mDB = DatabaseUtil.getInstance(this);
                }
                else{
                }
                break;
        }
    }



    public void sendRequest(final String code, String url, Map<String, String> params) {
        if (!(getString(R.string.REQUEST_API_RECEIVEORDERLIST).equals(code) || getString(R.string.REQUEST_API_ORDERLIST).equals(code)
        || getString(R.string.REQUEST_API_ORDERPRODUCTLIST).equals(code))) {
            Log.e("ss99km01", "ss99km01 Base showProgress");
            dissmissProgress();
            showProgress();
        }
        volley.sendRequest(code, url, params,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(code, response);
                        if (!(getString(R.string.REQUEST_API_RECEIVEORDERLIST).equals(code) || getString(R.string.REQUEST_API_ORDERLIST).equals(code)
                                || getString(R.string.REQUEST_API_ORDERPRODUCTLIST).equals(code))) {
                            Log.e("ss99km01", "ss99km01 Base dissmissProgress");
                            dissmissProgress();
                        }
                    }
                }
        );
    }

    public String getJSONData(JSONObject obj, String name) throws JSONException {
        if(obj.isNull(name)) {
            return "";
        }else{
            return obj.getString(name);
        }
    }

    public void onSuccess(String code, String response) {};
    public void onSuccess(String code, NetworkResponse response) {};

    protected void showProgress(){
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("로딩중입니다...");
        progressDialog.show();
    }

    protected void dissmissProgress(){
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
