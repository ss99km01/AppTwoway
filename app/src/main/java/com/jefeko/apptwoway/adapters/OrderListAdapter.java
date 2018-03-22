package com.jefeko.apptwoway.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.models.Order;
import com.jefeko.apptwoway.ui.obtainorder.ObtainOrderListFragment;
import com.jefeko.apptwoway.ui.order.OrderListFragment;
import com.jefeko.apptwoway.utils.NumberFormatUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ProductHolder> {

    private ArrayList<Order> mOrderList;

    private Fragment mFragment;

    public OrderListAdapter(Fragment fragment) {
        this.mFragment = fragment;
        this.mOrderList = new ArrayList<>();
    }

    public void addItem(Order order) {
        this.mOrderList.add(order);
        notifyDataSetChanged();
    }

    public void clearItem() {
        this.mOrderList.clear();
        notifyDataSetChanged();
    }

    public Order getItem(int position) {
        return this.mOrderList.get(position);
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, final int position) {
        Order order = getItem(position);
        holder.tvDate.setText(order.getOrder_ymd());
        holder.tvCompanyName.setText(order.getCompany_name());
        holder.tvProductName.setText(order.getProduct_name());
        holder.tvTotalPrice.setText(NumberFormatUtils.numberToCommaString(order.getOrder_price())+" 원");
        holder.tvStatus.setText(order.getOrder_status_name());
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragment instanceof OrderListFragment) {
                    ((OrderListFragment) mFragment).openDetailOrder(mOrderList.get(position));
                }
                if (mFragment instanceof ObtainOrderListFragment) {
                    ((ObtainOrderListFragment) mFragment).openDetailOrder(mOrderList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mOrderList == null) {
            return 0;
        }
        return mOrderList.size();
    }

    public static class ProductHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_list_item_date)
        TextView tvDate;

        @BindView(R.id.order_list_item_company_name)
        TextView tvCompanyName;

        @BindView(R.id.order_list_item_product_name)
        TextView tvProductName;

        @BindView(R.id.order_list_item_total_price)
        TextView tvTotalPrice;

        @BindView(R.id.order_list_item_status)
        TextView tvStatus;


        @BindView(R.id.order_list_item_more_btn)
        Button btnMore;

        private ProductHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
