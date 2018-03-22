package com.jefeko.apptwoway.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.models.Company;
import com.jefeko.apptwoway.ui.obtainorder.ObtainOrderFragment;
import com.jefeko.apptwoway.ui.order.OrderFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.CompanyHolder> {

    private ArrayList<Company> companyList;

    private Fragment mFragment;

    public CompanyListAdapter(Fragment fragment) {
        this.mFragment = fragment;
        this.companyList = new ArrayList<>();
    }

    public void addItem(Company company) {
        this.companyList.add(company);
        notifyDataSetChanged();
    }

    public void clearItem() {
        this.companyList.clear();
        notifyDataSetChanged();
    }

    public Company getItem(int position) {
        return this.companyList.get(position);
    }

    @Override
    public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_company_item, parent, false);
        return new CompanyHolder(view);
    }

    @Override
    public void onBindViewHolder(CompanyHolder holder, final int position) {
        Company company = getItem(position);
        holder.tvCompanyName.setText(company.getCompany_name());
        holder.tvBusiNo.setText(company.getBusi_no());
        holder.tvTelNo.setText(company.getTel_no());

        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragment instanceof OrderFragment) {
                    ((OrderFragment) mFragment).setCompanyInfo(companyList.get(position));
                }
                if (mFragment instanceof ObtainOrderFragment) {
                    ((ObtainOrderFragment) mFragment).setCompanyInfo(companyList.get(position));
                }
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

        @BindView(R.id.search_item_company_name)
        TextView tvCompanyName;

        @BindView(R.id.search_item_busi_no)
        TextView tvBusiNo;

        @BindView(R.id.search_item_tel_no)
        TextView tvTelNo;

        @BindView(R.id.search_item_select_btn)
        Button btnSelect;

        private CompanyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
