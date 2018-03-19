package com.jefeko.apptwoway.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.models.WayMms;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lhg1304 on 2017-11-28.
 */

public class WayTalkMmsListAdapter extends RecyclerView.Adapter<WayTalkMmsListAdapter.WayMmsHolder> {

    private ArrayList<WayMms> mmsList;
    private Context context = null;

    public WayTalkMmsListAdapter(Context context) {
        this.context = context;
        this.mmsList = new ArrayList<>();
    }

    public void addItem(WayMms wayMms) {
        this.mmsList.add(wayMms);
        notifyDataSetChanged();
    }

    public WayMms getItem(int position) {
        return this.mmsList.get(position);
    }

    @Override
    public WayMmsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waytalk_mms_item, parent, false);
        return new WayMmsHolder(view);
    }


    @Override
    public void onBindViewHolder(WayMmsHolder holder, int position) {
        WayMms wayMms = getItem(position);

        if(wayMms.getCompany_id().equals(PreferenceUtils.getPreferenceValueOfString(context, context.getString(R.string.COMPANY_ID)))){
            if (wayMms.getMsg_s_r_code().equals("Y")) {
                holder.mms_m_Layout.setVisibility(View.VISIBLE);
                holder.mms_e_Layout.setVisibility(View.GONE);
                holder.mms_m_text.setText(wayMms.getContents());
                holder.mms_m_time.setText(wayMms.getReg_date());
                if(wayMms.getRead_yn().equals("Y")) {
                    holder.mms_m_check.setVisibility(View.VISIBLE);
                }else{
                    holder.mms_m_check.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.mms_m_Layout.setVisibility(View.GONE);
                holder.mms_e_Layout.setVisibility(View.VISIBLE);
                holder.profile.setBackgroundResource(R.drawable.ic_profile_on);
                holder.mms_e_name.setText(wayMms.getCompany_name());

                holder.mms_e_text.setText(wayMms.getContents());
                holder.mms_e_time.setText(wayMms.getReg_date());
            }
        }else {
            if (wayMms.getMsg_s_r_code().equals("N")) {
                holder.mms_m_Layout.setVisibility(View.VISIBLE);
                holder.mms_e_Layout.setVisibility(View.GONE);
                holder.mms_m_text.setText(wayMms.getContents());
                holder.mms_m_time.setText(wayMms.getReg_date());
                if(wayMms.getRead_yn().equals("Y")) {
                    holder.mms_m_check.setVisibility(View.VISIBLE);
                }else{
                    holder.mms_m_check.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.mms_m_Layout.setVisibility(View.GONE);
                holder.mms_e_Layout.setVisibility(View.VISIBLE);
                holder.profile.setBackgroundResource(R.drawable.ic_profile_on);
                holder.mms_e_name.setText(wayMms.getCompany_name());

                holder.mms_e_text.setText(wayMms.getContents());
                holder.mms_e_time.setText(wayMms.getReg_date());
            }
        }

    }

    @Override
    public int getItemCount() {
        return mmsList.size();
    }

    public static class WayMmsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile)
        ImageView profile;

        @BindView(R.id.mms_e_name)
        TextView mms_e_name;

        @BindView(R.id.mms_e_text)
        TextView mms_e_text;

        @BindView(R.id.mms_e_time)
        TextView mms_e_time;

        @BindView(R.id.mms_m_text)
        TextView mms_m_text;

        @BindView(R.id.mms_m_time)
        TextView mms_m_time;

        @BindView(R.id.mms_e_Layout)
        LinearLayout mms_e_Layout;

        @BindView(R.id.mms_m_Layout)
        LinearLayout mms_m_Layout;

        @BindView(R.id.mms_m_check)
        TextView mms_m_check;


        private WayMmsHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected void openActivity(Class<?> activity) {
        context.startActivity(new Intent(context, activity));
    }
}
