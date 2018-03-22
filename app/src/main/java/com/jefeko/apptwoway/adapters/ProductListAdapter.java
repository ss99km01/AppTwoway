package com.jefeko.apptwoway.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.models.Product;
import com.jefeko.apptwoway.ui.obtainorder.ObtainOrderFragment;
import com.jefeko.apptwoway.ui.order.OrderFragment;
import com.jefeko.apptwoway.utils.NumberFormatUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductHolder> {

    private ArrayList<Product> productList;

    private Fragment mFragment;

    public ProductListAdapter(Fragment fragment) {
        this.mFragment = fragment;
        this.productList = new ArrayList<>();
    }

    public void addItem(Product product) {
        this.productList.add(product);
        notifyDataSetChanged();
    }

    public void clearItem() {
        this.productList.clear();
        notifyDataSetChanged();
    }

    public Product getItem(int position) {
        return this.productList.get(position);
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_product_item, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, final int position) {
        Product product = getItem(position);
        holder.tvProductName.setText(product.getProd_name());
        holder.tvUnit.setText(product.getUnit());
        holder.tvSellPrice.setText(NumberFormatUtils.numberToCommaString(product.getSell_price()));
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragment instanceof OrderFragment) {
                    ((OrderFragment) mFragment).addSelectedItem(productList.get(position));
                }
                if (mFragment instanceof ObtainOrderFragment) {
                    ((ObtainOrderFragment) mFragment).addSelectedItem(productList.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (productList == null) {
            return 0;
        }
        return productList.size();
    }

    public static class ProductHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.search_item_prod_name)
        TextView tvProductName;

        @BindView(R.id.search_item_unit)
        TextView tvUnit;

        @BindView(R.id.search_item_sell_price)
        TextView tvSellPrice;

        @BindView(R.id.search_item_select_btn)
        Button btnSelect;

        private ProductHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
