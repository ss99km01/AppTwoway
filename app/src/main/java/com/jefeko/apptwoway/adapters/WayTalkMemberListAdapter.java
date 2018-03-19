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
import com.jefeko.apptwoway.models.WayMember;
import com.jefeko.apptwoway.ui.waytalk.WayTalkMmsActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WayTalkMemberListAdapter extends RecyclerView.Adapter<WayTalkMemberListAdapter.MemberHolder> {

    private ArrayList<WayMember> memberList;
    private Context context = null;

    public WayTalkMemberListAdapter(Context context) {
        this.context = context;
        this.memberList = new ArrayList<>();
    }

    public void addItem(WayMember member) {
        this.memberList.add(member);
        notifyDataSetChanged();
    }

    public WayMember getItem(int position) {
        return this.memberList.get(position);
    }

    @Override
    public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waytalk_search_company_item, parent, false);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberHolder holder, int position) {
        WayMember member = getItem(position);
        holder.profile.setBackgroundResource(R.drawable.ic_profile_on);
        holder.company_info.setText(member.getMember_name()+" (휴대폰 번호 : "+member.getCell_no()+")");

        holder.list_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(WayTalkMmsActivity.class);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile)
        ImageView profile;

        @BindView(R.id.company_info)
        TextView company_info;

        @BindView(R.id.list_row)
        LinearLayout list_row;


        private MemberHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected void openActivity(Class<?> activity) {
        context.startActivity(new Intent(context, activity));
    }
}
