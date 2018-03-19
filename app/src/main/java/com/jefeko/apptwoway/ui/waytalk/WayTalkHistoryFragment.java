package com.jefeko.apptwoway.ui.waytalk;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.adapters.WayTalkCompanyListAdapter;
import com.jefeko.apptwoway.adapters.WayTalkHistoryListAdapter;
import com.jefeko.apptwoway.adapters.WayTalkMemberListAdapter;
import com.jefeko.apptwoway.http.VolleyHelper;
import com.jefeko.apptwoway.models.WayHistory;
import com.jefeko.apptwoway.models.WayMember;
import com.jefeko.apptwoway.models.WayTalkCompany;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class WayTalkHistoryFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.history_listview) RecyclerView history_listview;

    public VolleyHelper volley;

    private WayTalkHistoryListAdapter wayTalkHistoryListAdapter = null;

    public WayTalkHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View wayTalkGroup = inflater.inflate(R.layout.fragment_waytalk_history, container, false);

        ButterKnife.bind(this, wayTalkGroup);

        volley = new VolleyHelper(getActivity());

        //doMsgList();

        return wayTalkGroup;
    }

    public void doMsgList(){
        //쪽지함 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.COMPANY_ID)));
        values.put(getString(R.string.USER_ID), PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.USER_ID)));

        volley.sendRequest(getString(R.string.REQUEST_API_MSGLIST), getString(R.string.api_msgList), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(getString(R.string.REQUEST_API_MSGLIST), response);
                    }
                }
        );
    }

    public void onSuccess(String code, String response) {
        LogUtils.e("[" + code + "] onSuccess = " + response);

        try {
            JSONObject obj = new JSONObject(response);

            if(code.equals(getString(R.string.REQUEST_API_MSGLIST))) {
                JSONArray array = obj.getJSONArray("msgList");
                wayTalkHistoryListAdapter = new WayTalkHistoryListAdapter(getContext(),volley);
                WayHistory wayHistory = null;
                for(int i=0; i<array.length(); i++) {
                    JSONObject o = (JSONObject) array.getJSONObject(i);

                    wayHistory = new WayHistory();
                    wayHistory.setMsg_no(getJSONData(o, "msg_no"));
                    wayHistory.setCompany_id(getJSONData(o, "company_id"));
                    wayHistory.setUser_id(getJSONData(o, "user_id"));
                    wayHistory.setPal_company_id(getJSONData(o, "pal_company_id"));
                    wayHistory.setCompany_name(getJSONData(o, "company_name"));
                    wayHistory.setPal_company_name(getJSONData(o, "pal_company_name"));
                    wayHistory.setContents(getJSONData(o, "contents"));
                    wayHistory.setReg_date(getJSONData(o, "reg_date").substring(0,10));
                    wayHistory.setRead_yn(getJSONData(o, "read_yn"));
                    wayHistory.setMsg_s_r_code(getJSONData(o, "msg_s_r_code"));

                    wayTalkHistoryListAdapter.addItem(wayHistory);
                    history_listview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    history_listview.setAdapter(wayTalkHistoryListAdapter);
                }
            }

        }catch (Exception e){
            Toast.makeText(getActivity(), getString(R.string.search_faild), Toast.LENGTH_SHORT).show();
            LogUtils.e("[" + code + "] error = " + e.getMessage());
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public String getJSONData(JSONObject obj, String name) throws JSONException {
        if(obj.isNull(name)) {
            return "";
        }else{
            return obj.getString(name);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        doMsgList();
    }
}
