package com.jefeko.apptwoway.ui.waytalk;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.adapters.WayTalkCompanyListAdapter;
import com.jefeko.apptwoway.adapters.WayTalkMemberListAdapter;
import com.jefeko.apptwoway.http.VolleyHelper;
import com.jefeko.apptwoway.models.WayMember;
import com.jefeko.apptwoway.models.WayTalkCompany;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WayTalkGroupFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.gumaecheo) Button gumaecheo;
    @BindView(R.id.panmaecheo) Button panmaecheo;
    @BindView(R.id.member) Button member;
    @BindView(R.id.search_gumaecheo_text) EditText search_gumaecheo_text;
    @BindView(R.id.search_gumaecheo_btn) ImageButton search_gumaecheo_btn;
    @BindView(R.id.search_panmaecheo_text) EditText search_panmaecheo_text;
    @BindView(R.id.search_panmaecheo_btn) ImageButton search_panmaecheo_btn;
    @BindView(R.id.search_member_text) EditText search_member_text;
    @BindView(R.id.search_member_btn) ImageButton search_member_btn;
    @BindView(R.id.gumaecheo_listview) RecyclerView gumaecheo_listview;
    @BindView(R.id.panmaecheo_listview) RecyclerView panmaecheo_listview;
    @BindView(R.id.member_listview) RecyclerView member_listview;
    @BindView(R.id.gumaecheo_search_layout) LinearLayout gumaecheo_search_layout;
    @BindView(R.id.panmaecheo_search_layout) LinearLayout panmaecheo_search_layout;
    @BindView(R.id.member_search_layout) LinearLayout member_search_layout;

    private WayTalkCompanyListAdapter wayTalkCompanyListAdapter = null;
    private WayTalkMemberListAdapter talkMemberListAdapter = null;

    ArrayList<WayTalkCompany> wayTalkCompanyArrayList = null;
    ArrayList<WayMember> memberArrayList = null;

    private int gubun = 0;

    public VolleyHelper volley;

    public WayTalkGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View wayTalkGroup = inflater.inflate(R.layout.fragment_waytalk_group, container, false);

        ButterKnife.bind(this, wayTalkGroup);


        initLayout();

        initEvent();

        volley = new VolleyHelper(getActivity());

        return wayTalkGroup;
    }

    public void initLayout(){
        gumaecheo_listview.setVisibility(View.VISIBLE);
        panmaecheo_listview.setVisibility(View.GONE);
        member_listview.setVisibility(View.GONE);
        gumaecheo_search_layout.setVisibility(View.VISIBLE);
        panmaecheo_search_layout.setVisibility(View.GONE);
        member_search_layout.setVisibility(View.GONE);
    }

    public void initEvent(){
        gumaecheo.setOnClickListener(this);
        panmaecheo.setOnClickListener(this);
        member.setOnClickListener(this);
        search_gumaecheo_btn.setOnClickListener(this);
        search_panmaecheo_btn.setOnClickListener(this);
        search_member_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gumaecheo:
                gumaecheo.setTextColor(getResources().getColor(R.color.colorTextBlack));
                panmaecheo.setTextColor(getResources().getColor(R.color.colorTextGray));
                member.setTextColor(getResources().getColor(R.color.colorTextGray));
                /*gumaecheo.setBackgroundResource(R.drawable.tab_1_22_pressed);
                panmaecheo.setBackgroundResource(R.drawable.tab_2_22_normal);
                member.setBackgroundResource(R.drawable.tab_3_22_normal);*/
                gumaecheo_listview.setVisibility(View.VISIBLE);
                panmaecheo_listview.setVisibility(View.GONE);
                member_listview.setVisibility(View.GONE);
                gumaecheo_search_layout.setVisibility(View.VISIBLE);
                panmaecheo_search_layout.setVisibility(View.GONE);
                member_search_layout.setVisibility(View.GONE);
                hideKeyBoard();
                gubun = 0;
                break;
            case R.id.panmaecheo:
                gumaecheo.setTextColor(getResources().getColor(R.color.colorTextGray));
                panmaecheo.setTextColor(getResources().getColor(R.color.colorTextBlack));
                member.setTextColor(getResources().getColor(R.color.colorTextGray));
                /*gumaecheo.setBackgroundResource(R.drawable.tab_1_22_normal);
                panmaecheo.setBackgroundResource(R.drawable.tab_2_22_pressed);
                member.setBackgroundResource(R.drawable.tab_3_22_normal);*/
                gumaecheo_listview.setVisibility(View.GONE);
                panmaecheo_listview.setVisibility(View.VISIBLE);
                member_listview.setVisibility(View.GONE);
                gumaecheo_search_layout.setVisibility(View.GONE);
                panmaecheo_search_layout.setVisibility(View.VISIBLE);
                member_search_layout.setVisibility(View.GONE);
                hideKeyBoard();

                gubun = 1;
                break;
            case R.id.member:
//                gumaecheo.setBackgroundResource(R.drawable.tab_1_22_normal);
//                panmaecheo.setBackgroundResource(R.drawable.tab_2_22_normal);
//                member.setBackgroundResource(R.drawable.tab_3_22_pressed);
//                gumaecheo_listview.setVisibility(View.GONE);
//                panmaecheo_listview.setVisibility(View.GONE);
//                member_listview.setVisibility(View.VISIBLE);
//                gumaecheo_search_layout.setVisibility(View.GONE);
//                panmaecheo_search_layout.setVisibility(View.GONE);
//                member_search_layout.setVisibility(View.VISIBLE);
//                hideKeyBoard();
//
//                gubun = 2;
                break;
            case R.id.search_gumaecheo_btn:

                doWayGumaecheoTalk();
                hideKeyBoard();
                
                break;
            case R.id.search_panmaecheo_btn:

                doWayPanmaecheoTalk();
                hideKeyBoard();

                break;
            case R.id.search_member_btn:

                doWayMemberTalk();
                hideKeyBoard();

                break;
        }
    }

    public void hideKeyBoard(){
        CommonUtil.hideKeyboard(getActivity(),search_gumaecheo_text);
        CommonUtil.hideKeyboard(getActivity(),search_panmaecheo_text);
        CommonUtil.hideKeyboard(getActivity(),search_member_text);
    }

    public void doWayGumaecheoTalk() {
        if(TextUtils.isEmpty(search_gumaecheo_text.getText().toString())) {
            CommonUtil.showAlertDialog(getActivity(), getString(R.string.search_field_input_faild));
            return;
        }

        LogUtils.d("COMPANY_ID = "+PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.COMPANY_ID)));
        LogUtils.d("search_gumaecheo_text = "+search_gumaecheo_text.getText().toString());

        //로그인 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.COMPANY_ID)));
        values.put(getString(R.string.REPRESENTATIVE_NAME), "");
        values.put(getString(R.string.TEL_NO), "");
        values.put(getString(R.string.BUY_YN), "");
        values.put(getString(R.string.SELL_YN), "");
        values.put(getString(R.string.CHAIN_YN), "");
        values.put(getString(R.string.COMPANY_NAME), "");
        values.put(getString(R.string.COMPANY_NAME), search_gumaecheo_text.getText().toString());
        values.put(getString(R.string.BUSINESS_NUMBER), "");
        values.put(getString(R.string.USE_YN), "");

        volley.sendRequest(getString(R.string.REQUEST_API_CUSTOMERLIST), getString(R.string.api_customerList), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_CUSTOMERLIST), response);
                    }
                }
        );
    }

    public void doWayPanmaecheoTalk() {
        if(TextUtils.isEmpty(search_panmaecheo_text.getText().toString())) {
            CommonUtil.showAlertDialog(getActivity(), getString(R.string.search_field_input_faild));
            return;
        }


        //로그인 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.COMPANY_ID)));
        values.put(getString(R.string.REPRESENTATIVE_NAME), "");
        values.put(getString(R.string.TEL_NO), "");
        values.put(getString(R.string.BUY_YN), "");
        values.put(getString(R.string.SELL_YN), "Y");
        values.put(getString(R.string.CHAIN_YN), "");
        values.put(getString(R.string.COMPANY_NAME), search_panmaecheo_text.getText().toString());
        values.put(getString(R.string.BUSINESS_NUMBER), "");
        values.put(getString(R.string.USE_YN), "Y");

        volley.sendRequest(getString(R.string.REQUEST_API_CUSTOMERLIST), getString(R.string.api_customerList), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_CUSTOMERLIST), response);
                    }
                }
        );
    }

    public void doWayMemberTalk() {
        if(TextUtils.isEmpty(search_member_text.getText().toString())) {
            CommonUtil.showAlertDialog(getActivity(), getString(R.string.search_field_input_faild));
            return;
        }


        //로그인 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.COMPANY_ID)));
        values.put(getString(R.string.MEMBER_NAME), search_member_text.getText().toString());
        values.put(getString(R.string.USE_YN), "Y");

        volley.sendRequest(getString(R.string.REQUEST_API_MEMBERLIST), getString(R.string.api_memberList), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_MEMBERLIST), response);
                    }
                }
        );
    }

    public void onSuccess(String code, String response) {
        LogUtils.e("[" + code + "] onSuccess = " + response);

        try {
            JSONObject obj = new JSONObject(response);

            if(code.equals(getString(R.string.REQUEST_API_CUSTOMERLIST))){
                JSONArray array = obj.getJSONArray("customerList");

                if(gubun == 0){
                    LogUtils.d("gumaecheo_search_layout = 활성화");

                    wayTalkCompanyListAdapter = new WayTalkCompanyListAdapter(getContext());
                    WayTalkCompany wayTalkCompany = null;
                    for(int i=0; i<array.length(); i++) {
                        JSONObject o = (JSONObject) array.getJSONObject(i);

                        wayTalkCompany = new WayTalkCompany();
                        wayTalkCompany.setBuy_yn(getJSONData(o, "buy_yn"));
                        wayTalkCompany.setSell_yn(getJSONData(o, "sell_yn"));
                        wayTalkCompany.setChain_yn(getJSONData(o, "chain_yn"));
                        wayTalkCompany.setUse_yn(getJSONData(o, "use_yn"));
                        wayTalkCompany.setCompany_id(getJSONData(o, "company_id"));
                        wayTalkCompany.setCompany_name(getJSONData(o, "company_name"));
                        wayTalkCompany.setBusi_no(getJSONData(o, "busi_no").substring(0,3)+"-"+getJSONData(o, "busi_no").substring(3,5)+"-"+getJSONData(o, "busi_no").substring(5,10));
                        wayTalkCompany.setCompany_type_code(getJSONData(o, "company_type_code"));
                        wayTalkCompany.setCompany_type_name(getJSONData(o, "company_type_name"));
                        wayTalkCompany.setBusi_type(getJSONData(o, "busi_type"));
                        wayTalkCompany.setBusi_item(getJSONData(o, "busi_item"));
                        wayTalkCompany.setTel_no(getJSONData(o, "tel_no"));
                        wayTalkCompany.setFax_no(getJSONData(o, "fax_no"));
                        wayTalkCompany.setZip(getJSONData(o, "zip"));
                        wayTalkCompany.setAddr1(getJSONData(o, "add1"));
                        wayTalkCompany.setAddr2(getJSONData(o, "addr2"));
                        wayTalkCompany.setEmployee_name(getJSONData(o, "employee_name"));
                        wayTalkCompany.setCell_no(getJSONData(o, "cell_no"));
                        wayTalkCompany.setEmail(getJSONData(o, "email"));
                        wayTalkCompany.setBank_code(getJSONData(o, "bank_code"));
                        wayTalkCompany.setBank_name(getJSONData(o, "bank_name"));
                        wayTalkCompany.setAccounter(getJSONData(o, "accounter"));
                        wayTalkCompany.setAccount_no(getJSONData(o, "account_no"));
                        wayTalkCompanyListAdapter.addItem(wayTalkCompany);
                    }

                    gumaecheo_listview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    gumaecheo_listview.setAdapter(wayTalkCompanyListAdapter);


                }else if (gubun == 1){
                    wayTalkCompanyListAdapter = new WayTalkCompanyListAdapter(getContext());

                    LogUtils.d("panmaecheo_search_layout = 활성화");
                    WayTalkCompany wayTalkCompany = null;
                    for(int i=0; i<array.length(); i++) {
                        JSONObject o = (JSONObject) array.getJSONObject(i);

                        wayTalkCompany = new WayTalkCompany();
                        wayTalkCompany.setBuy_yn(getJSONData(o, "buy_yn"));
                        wayTalkCompany.setSell_yn(getJSONData(o, "sell_yn"));
                        wayTalkCompany.setChain_yn(getJSONData(o, "chain_yn"));
                        wayTalkCompany.setUse_yn(getJSONData(o, "use_yn"));
                        wayTalkCompany.setCompany_id(getJSONData(o, "company_id"));
                        wayTalkCompany.setCompany_name(getJSONData(o, "company_name"));
                        wayTalkCompany.setBusi_no(getJSONData(o, "busi_no").substring(0,3)+"-"+getJSONData(o, "busi_no").substring(3,5)+"-"+getJSONData(o, "busi_no").substring(5,10));
                        wayTalkCompany.setCompany_type_code(getJSONData(o, "company_type_code"));
                        wayTalkCompany.setCompany_type_name(getJSONData(o, "company_type_name"));
                        wayTalkCompany.setBusi_type(getJSONData(o, "busi_type"));
                        wayTalkCompany.setBusi_item(getJSONData(o, "busi_item"));
                        wayTalkCompany.setTel_no(getJSONData(o, "tel_no"));
                        wayTalkCompany.setFax_no(getJSONData(o, "fax_no"));
                        wayTalkCompany.setZip(getJSONData(o, "zip"));
                        wayTalkCompany.setAddr1(getJSONData(o, "add1"));
                        wayTalkCompany.setAddr2(getJSONData(o, "addr2"));
                        wayTalkCompany.setEmployee_name(getJSONData(o, "employee_name"));
                        wayTalkCompany.setCell_no(getJSONData(o, "cell_no"));
                        wayTalkCompany.setEmail(getJSONData(o, "email"));
                        wayTalkCompany.setBank_code(getJSONData(o, "bank_code"));
                        wayTalkCompany.setBank_name(getJSONData(o, "bank_name"));
                        wayTalkCompany.setAccounter(getJSONData(o, "accounter"));
                        wayTalkCompany.setAccount_no(getJSONData(o, "account_no"));
                        wayTalkCompanyListAdapter.addItem(wayTalkCompany);
                    }

                    panmaecheo_listview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    panmaecheo_listview.setAdapter(wayTalkCompanyListAdapter);
                }

            }else if(code.equals(getString(R.string.REQUEST_API_MEMBERLIST))){
                LogUtils.d("member_search_layout = 활성화");
                WayMember wayMember = new WayMember();
                wayMember.setCompany_id("20");
                wayMember.setMember_id("testn01");
                wayMember.setMember_name("이름");
                wayMember.setCell_no("01012345678");
                wayMember.setEmail("test@email.net");
                wayMember.setBirth_ymd("800223");
                wayMember.setZip("12345");
                wayMember.setAddr1("주소1");
                wayMember.setAddr2("주소2");
                wayMember.setPoint("1000");
                wayMember.setReg_ymd("20180203");
                wayMember.setUse_yn("Y");


                talkMemberListAdapter = new WayTalkMemberListAdapter(getContext());
                talkMemberListAdapter.addItem(wayMember);
                member_listview.setLayoutManager(new LinearLayoutManager(getActivity()));
                member_listview.setAdapter(talkMemberListAdapter);
            }


        }catch (Exception e){
            Toast.makeText(getActivity(), getString(R.string.search_faild), Toast.LENGTH_SHORT).show();
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
}

