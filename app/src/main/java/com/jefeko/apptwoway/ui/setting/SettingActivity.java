package com.jefeko.apptwoway.ui.setting;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.ServiceCommon;
import com.jefeko.apptwoway.ui.MainActivity;
import com.jefeko.apptwoway.ui.common.BaseActivity;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.business_number) TextView business_number;                      //사업자번호
    @BindView(R.id.company_name) TextView company_name;                             //회사명
    @BindView(R.id.representative_name) TextView representative_name;             //대표자
    @BindView(R.id.tel_no) TextView tel_no;                                           //전화번호
    @BindView(R.id.juso) TextView juso;                                                //주소
    @BindView(R.id.push_btn) ImageView push_btn;                                      //푸쉬설정
    @BindView(R.id.print_btn) ImageView print_btn;                                   //프린터설정
    @BindView(R.id.option_btn) ImageView option_btn;                                 //옵션설정
    @BindView(R.id.more_btn) ImageView more_btn;                                     //기타설정
    @BindView(R.id.homepage) Button homepage;                                        //기타설정

    private boolean push_check = false;
    private boolean print_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mContext = this;

        setToolbar();

        initEvent();

        initDataSet();

        doSetting();
    }

    public void initDataSet(){
        business_number.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.BUSINESS_NUMBER)).substring(0,3)+"-"+PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.BUSINESS_NUMBER)).substring(3,5)+"-"+PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.BUSINESS_NUMBER)).substring(5,10));
        company_name.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.COMPANY_NAME)));
        representative_name.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.REPRESENTATIVE_NAME)));
        tel_no.setText(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.TEL_NO)));
        juso.setText("("+PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.ZIP))+") "+PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.ADDR1))+PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.ADDR2)));
    }

    public void initEvent(){
        push_btn.setOnClickListener(this);
        print_btn.setOnClickListener(this);
        option_btn.setOnClickListener(this);
        more_btn.setOnClickListener(this);
        homepage.setOnClickListener(this);
    }

    /**
     * 설정조회
     */
    public void doSetting() {
        //로그인 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.USER_ID), PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.USER_ID)));
        sendRequest(getString(R.string.REQUEST_API_GETAPPSETTING), getString(R.string.api_getAppSetting), values);
    }

    /**
     * 설정 변경
     */
    public void doSetPushSetting(){
        //로그인 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.USER_ID), PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.USER_ID)));
        if(push_check){
            values.put(getString(R.string.PUSH_YN), "N");
        }else{
            values.put(getString(R.string.PUSH_YN), "Y");
        }
        if(print_check) {
            values.put(getString(R.string.PRINTER_YN), "Y");
        }else{
            values.put(getString(R.string.PRINTER_YN), "N");
        }

        sendRequest(getString(R.string.REQUEST_API_SETAPPSETTING), getString(R.string.api_setAppSetting), values);
    }

    /**
     * 설정 변경
     */
    public void doSetPrintSetting(){
        //로그인 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.USER_ID), PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.USER_ID)));
        if(push_check){
            values.put(getString(R.string.PUSH_YN), "Y");
        }else{
            values.put(getString(R.string.PUSH_YN), "N");
        }
        if(print_check) {
            values.put(getString(R.string.PRINTER_YN), "N");
        }else{
            values.put(getString(R.string.PRINTER_YN), "Y");
        }

        sendRequest(getString(R.string.REQUEST_API_SETAPPSETTING), getString(R.string.api_setAppSetting), values);
    }

    @Override
    public void onSuccess(String code, String response) {
        LogUtils.e("[" + code + "] onSuccess = " + response);

        try {
            JSONObject obj = new JSONObject(response);

            if(code.equals(getString(R.string.REQUEST_API_GETAPPSETTING))) {
                JSONObject content = obj.getJSONObject("appSetting");

                if(content != null) {
                    if (content.getString("push_yn").equals("Y")) {
                        push_btn.setBackgroundResource(R.drawable.ic_on);
                        push_check = true;
                    } else {
                        push_btn.setBackgroundResource(R.drawable.ic_off);
                        push_check = false;
                    }

                    if (content.getString("printer_yn").equals("Y")) {
                        print_btn.setBackgroundResource(R.drawable.ic_on);
                        print_check = true;
                    } else {
                        print_btn.setBackgroundResource(R.drawable.ic_off);
                        print_check = false;
                    }
                }else{
                    push_btn.setBackgroundResource(R.drawable.ic_off);
                    push_check = false;
                    print_btn.setBackgroundResource(R.drawable.ic_off);
                    print_check = false;
                }
            }else if (code.equals(getString(R.string.REQUEST_API_SETAPPSETTING))){
                String result = obj.getString("result");

                if(result.equals("SUCCESS")) {
                    doSetting();
                }else{
                    CommonUtil.showAlertDialog(this, getString(R.string.push_setting_faild));
                }
            }

        }catch (Exception e){
            Toast.makeText(this, getString(R.string.faild), Toast.LENGTH_SHORT).show();
            LogUtils.e("[" + code + "] error = " + e.getMessage());
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.push_btn:
                LogUtils.d("푸쉬");


                doSetPushSetting();
                break;
            case R.id.print_btn:
                LogUtils.d("프린터");


                doSetPrintSetting();
                break;
            case R.id.option_btn:
                LogUtils.d("옵션");
                break;
            case R.id.more_btn:
                LogUtils.d("기타");
                break;
            case R.id.homepage:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.homepage)));

                startActivity(intent);
                break;
        }
    }
}
