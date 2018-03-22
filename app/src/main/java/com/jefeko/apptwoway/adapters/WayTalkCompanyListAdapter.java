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
import com.jefeko.apptwoway.models.WayTalkCompany;
import com.jefeko.apptwoway.ui.waytalk.WayTalkMmsActivity;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WayTalkCompanyListAdapter extends RecyclerView.Adapter<WayTalkCompanyListAdapter.CompanyHolder> {

    private ArrayList<WayTalkCompany> companyList;
    private Context context = null;

    public WayTalkCompanyListAdapter(Context context) {
        this.context = context;
        this.companyList = new ArrayList<>();
    }

    public void addItem(WayTalkCompany wayTalkCompany) {
        this.companyList.add(wayTalkCompany);
        notifyDataSetChanged();
    }

    public WayTalkCompany getItem(int position) {
        return this.companyList.get(position);
    }

    @Override
    public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waytalk_search_company_item, parent, false);
        return new CompanyHolder(view);
    }

    @Override
    public void onBindViewHolder(CompanyHolder holder, int position) {
        WayTalkCompany wayTalkCompany = getItem(position);

        final String company_id = PreferenceUtils.getPreferenceValueOfString(context, context.getString(R.string.COMPANY_ID));
        final String user_id = PreferenceUtils.getPreferenceValueOfString(context, context.getString(R.string.USER_ID));
        final String pal_company_id = wayTalkCompany.getCompany_id();


        holder.profile.setBackgroundResource(R.drawable.ic_profile_on);
        holder.company_info.setText(wayTalkCompany.getCompany_name()+" (사업자번호 : "+wayTalkCompany.getBusi_no()+")");

        holder.list_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(WayTalkMmsActivity.class,company_id,user_id,pal_company_id);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (companyList == null) {
            return 0;
        }
        return companyList.size();
    }

    public static class CompanyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile)
        ImageView profile;

        @BindView(R.id.company_info)
        TextView company_info;

        @BindView(R.id.list_row)
        LinearLayout list_row;

        private CompanyHolder(View itemView) {
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
