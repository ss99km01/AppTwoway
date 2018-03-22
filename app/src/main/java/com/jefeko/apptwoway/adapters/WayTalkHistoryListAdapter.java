package com.jefeko.apptwoway.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.http.VolleyHelper;
import com.jefeko.apptwoway.models.WayHistory;
import com.jefeko.apptwoway.ui.waytalk.WayTalkHistoryFragment;
import com.jefeko.apptwoway.ui.waytalk.WayTalkMmsActivity;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lhg1304 on 2017-11-28.
 */

public class WayTalkHistoryListAdapter extends RecyclerView.Adapter<WayTalkHistoryListAdapter.HistoryHolder> {

    private ArrayList<WayHistory> historyList;
    private Context context = null;
    public VolleyHelper volley;
    public int idx = 0;

    public WayTalkHistoryListAdapter(Context context,VolleyHelper volley) {
        this.context = context;
        this.volley = volley;
        this.historyList = new ArrayList<>();
    }

    public void addItem(WayHistory wayHistory) {
        this.historyList.add(wayHistory);
        notifyDataSetChanged();
    }

    public WayHistory getItem(int position) {
        return this.historyList.get(position);
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waytalk_history_item, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        WayHistory wayHistory = getItem(position);

        final String company_id = wayHistory.getCompany_id();
        final String user_id = wayHistory.getUser_id();
        final String pal_company_id = wayHistory.getPal_company_id();
        idx = position;

        holder.profile.setBackgroundResource(R.drawable.ic_profile_on);

        if(wayHistory.getCompany_id().equals(PreferenceUtils.getPreferenceValueOfString(context, context.getString(R.string.COMPANY_ID)))){
            holder.wayTalk_employee_name.setText(wayHistory.getPal_company_name());

            if(wayHistory.getMsg_s_r_code().equals("N")) {
                if(wayHistory.getRead_yn().equals("N")){
                    holder.wayTalk_send_check.setText("신규");
                }
            }
        }else{
            holder.wayTalk_employee_name.setText(wayHistory.getCompany_name());

            if(wayHistory.getMsg_s_r_code().equals("Y")) {
                if(wayHistory.getRead_yn().equals("N")){
                    holder.wayTalk_send_check.setText("신규");
                }
            }
        }


        holder.wayTalk_send_date.setText(wayHistory.getReg_date());
        holder.wayTalk_mms.setText(wayHistory.getContents());

        holder.list_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(WayTalkMmsActivity.class,company_id,user_id,pal_company_id);
            }
        });

        holder.list_row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CommonUtil.showAlertDialog(context, "삭제 하시겠습니까?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delMsg(company_id,pal_company_id);
                    }
                },new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                return false;
            }
        });
    }

    public void delMsg(String company_id,String pal_company_id){
        Map<String, String> values = new HashMap<>();
        values.put(context.getString(R.string.COMPANY_ID), company_id);
        values.put(context.getString(R.string.PAL_COMPANY_ID), pal_company_id);

        volley.sendRequest(context.getString(R.string.REQUEST_API_DELMSG), context.getString(R.string.api_delMsg), values,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        onSuccess(context.getString(R.string.REQUEST_API_DELMSG), response);
                    }
                }
        );
    }

    public void onSuccess(String code, String response) {
        LogUtils.e("[" + code + "] onSuccess = " + response);

        try {
            JSONObject obj = new JSONObject(response);

            if(code.equals(context.getString(R.string.REQUEST_API_DELMSG))) {

                Boolean result = obj.getBoolean("result");

                if(result){
                    this.historyList.remove(idx-1);
                    notifyDataSetChanged();
                }else{
                    CommonUtil.showAlertDialog(context,"삭제실패!");
                }
            }

        }catch (Exception e){
            Toast.makeText(context, context.getString(R.string.search_faild), Toast.LENGTH_SHORT).show();
            LogUtils.e("[" + code + "] error = " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        if (historyList == null) {
            return 0;
        }
        return historyList.size();
    }

    public static class HistoryHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile)
        ImageView profile;

        @BindView(R.id.wayTalk_employee_name)
        TextView wayTalk_employee_name;

        @BindView(R.id.wayTalk_send_date)
        TextView wayTalk_send_date;

        @BindView(R.id.wayTalk_mms)
        TextView wayTalk_mms;

        @BindView(R.id.list_row)
        LinearLayout list_row;

        @BindView(R.id.wayTalk_send_check)
        TextView wayTalk_send_check;



        private HistoryHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected void openActivity(Class<?> activity,String company_id,String user_id,String pal_company_id) {
        Intent intent = new Intent(context, activity);

        intent.putExtra("company_id",company_id);
        intent.putExtra("user_id",user_id);
        intent.putExtra("pal_company_id",pal_company_id);

        context.startActivity(intent);
    }

}
