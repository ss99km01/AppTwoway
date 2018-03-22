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
import com.jefeko.apptwoway.ui.obtainorder.ObtainOrderListFragment;
import com.jefeko.apptwoway.ui.order.OrderListFragment;
import com.jefeko.apptwoway.utils.NumberFormatUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductCheckAdapter extends RecyclerView.Adapter<ProductCheckAdapter.ProductHolder> {

    private ArrayList<Product> productList;
    private Fragment mFragment;
    private boolean mIsModify = false;

    public ProductCheckAdapter(Fragment fragment, boolean modify) {
        this.mFragment = fragment;
        this.productList = new ArrayList<>();
        this.mIsModify = modify;
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

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (mIsModify) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_product_modify_item, parent, false);
            return new ProductModifyHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_product_item, parent, false);
            return new ProductHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(ProductHolder holder, final int position) {
        final Product product = getItem(position);
        holder.tvProductName.setText(product.getProd_name());
        holder.tvUnit.setText(product.getUnit());
        holder.tvSellPrice.setText(NumberFormatUtils.numberToCommaString(product.getSell_price()));
        holder.tvAmount.setText(NumberFormatUtils.numberToCommaString(product.getAmount()));

        if (holder instanceof ProductModifyHolder) {
            ((ProductModifyHolder)holder).check_item_btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    product.setAmountDecrease();
                    notifyDataSetChanged();
                    updateTotalPrice();
                }
            });
            ((ProductModifyHolder)holder).check_item_btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    product.setAmountIncrease();
                    notifyDataSetChanged();
                    updateTotalPrice();
                }
            });
            ((ProductModifyHolder)holder).check_item_btn_x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productList.remove(position);
                    notifyDataSetChanged();
                    updateTotalPrice();
                }
            });
        }
    }

    private void updateTotalPrice() {
        if (mFragment instanceof OrderListFragment) {
            ((OrderListFragment) mFragment).updateTotalPrice(String.valueOf(getTotalCost()));
        }
        if (mFragment instanceof ObtainOrderListFragment) {
            ((ObtainOrderListFragment) mFragment).updateTotalPrice(String.valueOf(getTotalCost()));
        }
    }

    public int getTotalCost() {
        int totalCost = 0;
        for ( Product product : productList ) {
            totalCost += product.getOrder_price();
        }
        return totalCost;
    }

    @Override
    public int getItemCount() {
        if (productList == null) {
            return 0;
        }
        return productList.size();
    }


    public static class ProductHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.check_item_prod_name)
        TextView tvProductName;

        @BindView(R.id.check_item_unit)
        TextView tvUnit;

        @BindView(R.id.check_item_sell_price)
        TextView tvSellPrice;

        @BindView(R.id.check_item_amount)
        TextView tvAmount;

        private ProductHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ProductModifyHolder extends ProductHolder {

        @BindView(R.id.check_item_btn_minus)
        Button check_item_btn_minus;

        @BindView(R.id.check_item_btn_plus)
        Button check_item_btn_plus;

        @BindView(R.id.check_item_btn_x)
        Button check_item_btn_x;

        private ProductModifyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
