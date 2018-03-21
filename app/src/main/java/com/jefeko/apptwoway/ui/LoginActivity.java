package com.jefeko.apptwoway.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.ServiceCommon;
import com.jefeko.apptwoway.db.DatabaseUtil;
import com.jefeko.apptwoway.ui.common.BaseActivity;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity{
    @BindView(R.id.user_id) EditText user_id;                      //아이디
    @BindView(R.id.user_pass) EditText user_pass;                 //패스워드
    @BindView(R.id.id_save) ImageView id_save;                     //아이디 저장
    @BindView(R.id.auto_login) ImageView auto_login;              //자동로그인
    @BindView(R.id.login_btn) ImageButton login_btn;              //로그인 버튼
    @BindView(R.id.id_search) TextView id_search;     //아이디/패스워드 찾기
    @BindView(R.id.pass_search) TextView pass_search;     //아이디/패스워드 찾기
    @BindView(R.id.user_save) ImageButton user_save;              //회원가입
    @BindView(R.id.homepage) Button homepage;                     //홈페이지

    private DatabaseUtil db = null;
    private boolean id_save_check = false;        //아이디 저장 체크여부
    private boolean auto_login_check = false;     //자동로그인 체크여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mContext = this;

        initEvent();
        initialize();

        if(PreferenceUtils.getPreferenceValueOfBoolean(this, getString(R.string.AUTO_LOGIN))){
            user_id.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.USER_ID)));
            user_pass.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.USER_PASS)));
            id_save_check = true;
            auto_login_check = true;
            id_save.setBackgroundResource(R.drawable.ic_check_pressed);
            auto_login.setBackgroundResource(R.drawable.ic_check_pressed);
            doLogin();
        }else{
            if(PreferenceUtils.getPreferenceValueOfBoolean(this, getString(R.string.ID_SAVE))){
                user_id.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.USER_ID)));
                user_pass.setText("");
                id_save_check = true;
                auto_login_check = false;
                id_save.setBackgroundResource(R.drawable.ic_check_pressed);
                auto_login.setBackgroundResource(R.drawable.ic_check_normal);
            }else{
                user_id.setText("");
                user_pass.setText("");
                id_save_check = false;
                auto_login_check = false;
                id_save.setBackgroundResource(R.drawable.ic_check_normal);
                auto_login.setBackgroundResource(R.drawable.ic_check_normal);
            }
        }

//        user_id.setText("testh01");
//        user_pass.setText("testh01!");
//        user_id.setText("jfk3734");
//        user_pass.setText("posmoa3000*");


//        doLogin();
    }

    public void initEvent(){
        id_save.setOnClickListener(this);
        auto_login.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        user_save.setOnClickListener(this);
        id_search.setOnClickListener(this);
        pass_search.setOnClickListener(this);
        homepage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.id_save:
                if(id_save_check){
                    if(auto_login_check){
                        auto_login.setBackgroundResource(R.drawable.ic_check_normal);
                        auto_login_check = false;
                    }
                    id_save.setBackgroundResource(R.drawable.ic_check_normal);
                    id_save_check = false;
                }else{
                    id_save.setBackgroundResource(R.drawable.ic_check_pressed);
                    id_save_check = true;
                }
                break;
            case R.id.auto_login:
                if(auto_login_check){
                    auto_login.setBackgroundResource(R.drawable.ic_check_normal);
                    auto_login_check = false;
                    id_save.setBackgroundResource(R.drawable.ic_check_normal);
                    id_save_check = false;
                }else{
                    auto_login.setBackgroundResource(R.drawable.ic_check_pressed);
                    auto_login_check = true;
                    id_save.setBackgroundResource(R.drawable.ic_check_pressed);
                    id_save_check = true;
                }
                break;
            case R.id.login_btn:
                doLogin();
                break;
            case R.id.user_save:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.business_agree)));

                startActivity(intent);
                break;
            case R.id.id_search:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.findId)));

                startActivity(intent);
                break;
            case R.id.pass_search:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.findPassword)));

                startActivity(intent);
                break;
            case R.id.homepage:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.homepage)));

                startActivity(intent);
                break;

        }
    }

    public void doLogin() {
        String id = user_id.getText().toString();
        String userPW = user_pass.getText().toString();

        if(TextUtils.isEmpty(id)) {
            CommonUtil.showAlertDialog(this, getString(R.string.id_faild));
            return;
        }

        if(TextUtils.isEmpty(userPW)) {
            CommonUtil.showAlertDialog(this, getString(R.string.pass_faild));
            return;
        }

        //로그인 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.USER_ID), id);
        values.put(getString(R.string.USER_PASS), userPW);
        values.put(getString(R.string.PUSH_TOKEN), ServiceCommon.PUSH_TOKEN);
        sendRequest(getString(R.string.REQUEST_API_LOGIN), getString(R.string.api_login), values);
    }

    @Override
    public void onSuccess(String code, String response) {
        LogUtils.e("[" + code + "] onSuccess = " + response);

        try {
            JSONObject obj = new JSONObject(response);
            boolean result = obj.getBoolean("result");
            if(result) {
                JSONObject content = obj.getJSONObject("user");


                if(auto_login_check) {
                    PreferenceUtils.setPreferenceValue(this, getString(R.string.AUTO_LOGIN),true);
                    PreferenceUtils.setPreferenceValue(this, getString(R.string.ID_SAVE),true);
                }else {
                    if(id_save_check){
                        PreferenceUtils.setPreferenceValue(this, getString(R.string.AUTO_LOGIN),false);
                        PreferenceUtils.setPreferenceValue(this, getString(R.string.ID_SAVE),true);
                    }else{
                        PreferenceUtils.setPreferenceValue(this, getString(R.string.AUTO_LOGIN),false);
                        PreferenceUtils.setPreferenceValue(this, getString(R.string.ID_SAVE),false);
                    }
                }
                ServiceCommon.COMPANY_ID = getJSONData(content, getString(R.string.COMPANY_ID));
                ServiceCommon.EMPLOYEE_ID = getJSONData(content, "employee_id");
                PreferenceUtils.setPreferenceValue(this, getString(R.string.USER_ID),user_id.getText().toString());
                PreferenceUtils.setPreferenceValue(this, getString(R.string.USER_PASS),user_pass.getText().toString());
                PreferenceUtils.setPreferenceValue(this, getString(R.string.COMPANY_ID),getJSONData(content, getString(R.string.COMPANY_ID)));
                PreferenceUtils.setPreferenceValue(this, getString(R.string.COMPANY_NAME),getJSONData(content, getString(R.string.COMPANY_NAME)));
                PreferenceUtils.setPreferenceValue(this, getString(R.string.BUSINESS_NUMBER),getJSONData(content, getString(R.string.BUSINESS_NUMBER)));
                PreferenceUtils.setPreferenceValue(this, getString(R.string.REPRESENTATIVE_NAME),getJSONData(content, getString(R.string.REPRESENTATIVE_NAME)));
                PreferenceUtils.setPreferenceValue(this, getString(R.string.TEL_NO),getJSONData(content, getString(R.string.TEL_NO)));
                PreferenceUtils.setPreferenceValue(this, getString(R.string.ZIP),getJSONData(content, getString(R.string.ZIP)));
                PreferenceUtils.setPreferenceValue(this, getString(R.string.ADDR1),getJSONData(content, getString(R.string.ADDR1)));
                PreferenceUtils.setPreferenceValue(this, getString(R.string.ADDR2),getJSONData(content, getString(R.string.ADDR2)));
                openActivity(MainActivity.class);
                finish();
            }else{
                CommonUtil.showAlertDialog(this, getString(R.string.login_faild));
            }

        }catch (Exception e){
            Toast.makeText(this, getString(R.string.login_faild), Toast.LENGTH_SHORT).show();
            LogUtils.e("[" + code + "] error = " + e.getMessage());
        }

    }

    protected void openActivity(Class<?> activity) {
        startActivity(new Intent(this, activity));
    }
}
