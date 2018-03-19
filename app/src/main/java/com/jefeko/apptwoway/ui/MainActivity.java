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
import android.view.View;
import android.widget.Button;

import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.db.DatabaseUtil;
import com.jefeko.apptwoway.ui.basic.BasicManageActivity;
import com.jefeko.apptwoway.ui.common.BaseActivity;
import com.jefeko.apptwoway.ui.obtainorder.ObtainOrderManageActivity;
import com.jefeko.apptwoway.ui.order.OrderManageActivity;
import com.jefeko.apptwoway.ui.waytalk.WayTalkActivity;
import com.jefeko.apptwoway.utils.BackPressCloseUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;

        setToolbar();

        initEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null) {
            doPushResult(intent);
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
