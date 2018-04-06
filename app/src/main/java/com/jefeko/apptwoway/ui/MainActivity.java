package com.jefeko.apptwoway.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.adapters.WayTalkCompanyListAdapter;
import com.jefeko.apptwoway.adapters.WayTalkMemberListAdapter;
import com.jefeko.apptwoway.db.DatabaseUtil;
import com.jefeko.apptwoway.models.WayMember;
import com.jefeko.apptwoway.models.WayTalkCompany;
import com.jefeko.apptwoway.ui.basic.BasicManageActivity;
import com.jefeko.apptwoway.ui.common.BaseActivity;
import com.jefeko.apptwoway.ui.obtainorder.ObtainOrderManageActivity;
import com.jefeko.apptwoway.ui.order.OrderManageActivity;
import com.jefeko.apptwoway.ui.waytalk.WayTalkActivity;
import com.jefeko.apptwoway.utils.BackPressCloseUtil;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.card_view_obtain_order)
    CardView btn_obtain_order;
    @BindView(R.id.card_view_order)
    CardView btn_order;
    @BindView(R.id.card_view_talk)
    CardView btn_talk;
    @BindView(R.id.card_view_basic)
    CardView btn_basic;
    @BindView(R.id.homepage)
    Button homepage;
    @BindView(R.id.arlam_1)
    TextView arlam_1;
    @BindView(R.id.arlam_2)
    TextView arlam_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;

        setToolbar();

        initEvent();

        arlam_1.setVisibility(View.GONE);
        arlam_2.setVisibility(View.GONE);

        doCheckCount();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null) {
            doPushResult(intent);
        }
    }

    public void doCheckCount() {
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.COMPANY_ID)));
        values.put("order_ymd1", "");
        values.put("order_ymd2", "");
        values.put("delivery_ymd1", "");
        values.put("delivery_ymd2", "");
        values.put("receive_order_ymd1", "");
        values.put("receive_order_ymd2", "");
        values.put("order_status_code", "001");
        values.put("company_name", "");

        volley.sendRequest(getString(R.string.REQUEST_API_GETRECEIVEORDERLISTCOUNT), getString(R.string.api_getReceiveOrderListCount), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_GETRECEIVEORDERLISTCOUNT), response);
                    }
                }
        );
    }

    public void onSuccess(String code, String response) {
        LogUtils.e("[" + code + "] onSuccess = " + response);

        try {
            JSONObject obj = new JSONObject(response);

            if(code.equals(getString(R.string.REQUEST_API_GETRECEIVEORDERLISTCOUNT))) {

                if(obj.getInt("receiveOrderListCount") > 0){
                    arlam_1.setText(Integer.toString(obj.getInt("receiveOrderListCount")));
                    arlam_1.setVisibility(View.VISIBLE);
                }

            }


        }catch (Exception e){
            Toast.makeText(this, getString(R.string.search_faild), Toast.LENGTH_SHORT).show();
            LogUtils.e("[" + code + "] error = " + e.getMessage());
        }

    }

    public void doPushResult(Intent intent) {
        String pushTitle = intent.getStringExtra("title");

        if ("gumaecheo".equals(pushTitle)) {

        }

        if ("panmaecheo".equals(pushTitle)) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //push
        Intent intent = getIntent();
        if(intent != null) {
            doPushResult(intent);
        }
    }

    public void initEvent(){
        homepage.setOnClickListener(this);
    }

    /**
     * click event
     * @param v
     */
    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.card_view_obtain_order:
                openTopActivity(ObtainOrderManageActivity.class);
                break;
            case R.id.card_view_order:
                openTopActivity(OrderManageActivity.class);
                break;
            case R.id.card_view_talk:
                openTopActivity(WayTalkActivity.class);
                break;
            case R.id.card_view_basic:
                openTopActivity(BasicManageActivity.class);
                break;
            case R.id.homepage:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.homepage)));

                startActivity(intent);
                break;
        }
    }
}
