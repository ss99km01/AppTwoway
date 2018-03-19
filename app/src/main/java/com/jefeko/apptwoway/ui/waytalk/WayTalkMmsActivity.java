package com.jefeko.apptwoway.ui.waytalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.adapters.WayTalkMmsListAdapter;
import com.jefeko.apptwoway.http.VolleyHelper;
import com.jefeko.apptwoway.models.WayMms;
import com.jefeko.apptwoway.ui.common.BaseActivity;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class WayTalkMmsActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.wayTalk_mms_listview) RecyclerView wayTalk_mms_listview;
    @BindView(R.id.gumaecheo) TextView gumaecheo;
    @BindView(R.id.sendMsg)  EditText sendMsg;
    @BindView(R.id.sendBtn)  Button sendBtn;
    @BindView(R.id.icon_group) Button icon_group;


    private WayTalkMmsListAdapter wayTalkMmsListAdapter = null;
    private String pCompany_id = "";
    private String pUser_id = "";
    private String pPal_company_id = "";


    public VolleyHelper volley;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way_talk_mms);

        ButterKnife.bind(this);

        setToolbar();

        volley = new VolleyHelper(this);

        sendBtn.setOnClickListener(this);
        icon_group.setOnClickListener(this);

        pCompany_id = getIntent().getStringExtra("company_id");
        pUser_id = getIntent().getStringExtra("user_id");
        pPal_company_id = getIntent().getStringExtra("pal_company_id");

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                LinearLayoutManager.HORIZONTAL);
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.divider));

        wayTalk_mms_listview.addItemDecoration(dividerItemDecoration);

        selectCompanyInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                CommonUtil.hideKeyboard(this,sendMsg);
                doSendMsg();
                break;
            case R.id.icon_group:
                openActivity(WayTalkActivity.class);
                break;
        }
    }

    public void selectCompanyInfo(){
        Map<String, String> values = new HashMap<>();
        if(pCompany_id.equals(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.COMPANY_ID)))) {
            values.put(getString(R.string.COMPANY_ID), pPal_company_id);
        }else {
            values.put(getString(R.string.COMPANY_ID), pCompany_id);
        }

        volley.sendRequest(getString(R.string.REQUEST_API_SELECTCOMPANYINFO), getString(R.string.api_selectCompanyInfo), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_SELECTCOMPANYINFO), response);
                    }
                }
        );
    }

    public void readMsg(){
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), pCompany_id);
        values.put(getString(R.string.PAL_COMPANY_ID), pPal_company_id);
        if(pCompany_id.equals(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.COMPANY_ID)))) {
            values.put(getString(R.string.MSG_S_R_CODE), "N");
        }else {
            values.put(getString(R.string.MSG_S_R_CODE), "Y");
        }

        volley.sendRequest(getString(R.string.REQUEST_API_READMSG), getString(R.string.api_readMsg), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_READMSG), response);
                    }
                }
        );
    }

    public void doSendMsg(){
        //쪽지함 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), pCompany_id);
        values.put(getString(R.string.USER_ID), pUser_id);
        values.put(getString(R.string.PAL_COMPANY_ID), pPal_company_id);
        if(pCompany_id.equals(PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.COMPANY_ID)))) {
            values.put(getString(R.string.MSG_S_R_CODE), "Y");
        }else{
            values.put(getString(R.string.MSG_S_R_CODE), "N");
        }
        values.put(getString(R.string.CONTENTS), sendMsg.getText().toString());

        volley.sendRequest(getString(R.string.REQUEST_API_SENDMSG), getString(R.string.api_sendMsg), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_SENDMSG), response);
                    }
                }
        );
    }

    public void doMsgList(){
        //쪽지함 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), pCompany_id);
        values.put(getString(R.string.USER_ID), pUser_id);
        values.put(getString(R.string.PAL_COMPANY_ID), pPal_company_id);

        volley.sendRequest(getString(R.string.REQUEST_API_MSGINFOLIST), getString(R.string.api_msgInfoList), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_MSGINFOLIST), response);
                    }
                }
        );
    }

    public void onSuccess(String code, String response) {
        LogUtils.e("[" + code + "] onSuccess = " + response);

        try {
            JSONObject obj = new JSONObject(response);

            if(code.equals(getString(R.string.REQUEST_API_MSGINFOLIST))) {
                JSONArray array = obj.getJSONArray("msgInfoList");
                wayTalkMmsListAdapter = new WayTalkMmsListAdapter(this);
                WayMms wayMms = null;
                for(int i=0; i<array.length(); i++) {
                    JSONObject o = (JSONObject) array.getJSONObject(i);

                    wayMms = new WayMms();
                    wayMms.setMsg_no(getJSONData(o, "msg_no"));
                    wayMms.setCompany_id(getJSONData(o, "company_id"));
                    wayMms.setCompany_name(getJSONData(o, "company_name"));
                    wayMms.setUser_id(getJSONData(o, "user_id"));
                    wayMms.setEmployee_name(getJSONData(o, "employee_name"));
                    wayMms.setPal_company_id(getJSONData(o, "pal_company_id"));
                    wayMms.setPal_company_name(getJSONData(o, "pal_company_name"));
                    wayMms.setMsg_s_r_code(getJSONData(o, "msg_s_r_code"));
                    wayMms.setContents(getJSONData(o, "contents"));
                    wayMms.setReg_date(getJSONData(o, "reg_date"));
                    wayMms.setRead_yn(getJSONData(o, "read_yn"));

                    wayTalkMmsListAdapter.addItem(wayMms);
                    wayTalk_mms_listview.setLayoutManager(new LinearLayoutManager(this));
                    wayTalk_mms_listview.setAdapter(wayTalkMmsListAdapter);



                    wayTalk_mms_listview.scrollToPosition(wayTalkMmsListAdapter.getItemCount() - 1);
                }

                readMsg();
            }else if(code.equals(getString(R.string.REQUEST_API_SENDMSG))) {
                String result = obj.getString("result");

                if(result.equals("FAIL")){
                    CommonUtil.showAlertDialog(this,"전송실패!");
                }else{
                    doMsgList();

                    sendMsg.setText("");
                }
            }else if(code.equals(getString(R.string.REQUEST_API_READMSG))){

            }else if(code.equals(getString(R.string.REQUEST_API_SELECTCOMPANYINFO))){
                JSONObject content = obj.getJSONObject("companyInfo");

                gumaecheo.setText(getJSONData(content, "company_name")+"(사업자번호 : "+getJSONData(content, "busi_no").substring(0,3)+"-"+getJSONData(content, "busi_no").substring(3,5)+"-"+getJSONData(content, "busi_no").substring(5,10)+")");

                doMsgList();
            }

        }catch (Exception e){
            Toast.makeText(this, getString(R.string.search_faild), Toast.LENGTH_SHORT).show();
            LogUtils.e("[" + code + "] error = " + e.getMessage());
        }

    }

    public String getJSONData(JSONObject obj, String name) throws JSONException {
        if(obj.isNull(name)) {
            return "";
        }else{
            return obj.getString(name);
        }
    }

    //WayTalkActivity
    protected void openActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("page","waytalkmms");

        startActivity(intent);
    }

}
