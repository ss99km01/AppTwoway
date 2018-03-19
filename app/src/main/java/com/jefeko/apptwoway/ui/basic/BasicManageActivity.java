package com.jefeko.apptwoway.ui.basic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.ui.common.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BasicManageActivity extends BaseActivity {
    @BindView(R.id.warehouse_mgt)  ImageButton warehouse_mgt;                           //창고관리
    @BindView(R.id.classification_mgt)  ImageButton classification_mgt;               //분류관리
    @BindView(R.id.product_mgt)  ImageButton product_mgt;                                //상품관리
    @BindView(R.id.purchase_mgt)  ImageButton purchase_mgt;                             //구매처관리
    @BindView(R.id.salesdepartment_mgt)  ImageButton salesdepartment_mgt;             //판매처관리
    @BindView(R.id.homepage) Button homepage;                                             //홈페이지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_manage);
        ButterKnife.bind(this);
        mContext = this;

        setToolbar();

        initEvent();
    }

    public void initEvent(){
        homepage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.homepage:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.homepage)));

                startActivity(intent);
                break;
        }
    }
}
