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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductSelectAdapter extends RecyclerView.Adapter<ProductSelectAdapter.ProductHolder> {

    private ArrayList<Product> productList;

    private Fragment mFragment;

    public ProductSelectAdapter(Fragment fragment) {
        this.mFragment = fragment;
        this.productList = new ArrayList<>();
    }

    public boolean addItem(Product product) {
        if (checkItem(product)) {
            product.setAmount("0");
            product.setAmountIncrease();
            this.productList.add(product);
            if (mFragment instanceof OrderFragment) {
                ((OrderFragment) mFragment).updateTotalPrice(String.valueOf(getTotalCost()));
            }
            if (mFragment instanceof ObtainOrderFragment) {
                ((ObtainOrderFragment) mFragment).updateTotalPrice(String.valueOf(getTotalCost()));
            }
            notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    public Product getItem(int position) {
        return this.productList.get(position);
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    private boolean checkItem(Product product) {
        for (Product temp : productList) {
            if(temp.getCompany_id().equals(product.getCompany_id()) && temp.getProd_id().equals(product.getProd_id()))
                return false;
        }
        return true;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public int getTotalCost() {
        int totalCost = 0;
        for ( Product product : productList ) {
            totalCost += product.getOrder_price();
        }
        return totalCost;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_product_item, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, final int position) {
        final Product product = getItem(position);
        holder.tvProductName.setText(String.valueOf(product.getProd_name()+" "+product.getUnit()));
        holder.tvAmount.setText(String.valueOf(product.getAmount()));
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setAmountDecrease();
                notifyDataSetChanged();
                updateTotalPrice();
            }
        });
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setAmountIncrease();
                notifyDataSetChanged();
                updateTotalPrice();
            }
        });
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productList.remove(position);
                notifyDataSetChanged();
                updateTotalPrice();
            }
        });
    }

    private void updateTotalPrice() {
        if (mFragment instanceof OrderFragment) {
            ((OrderFragment) mFragment).updateTotalPrice(String.valueOf(getTotalCost()));
        }
        if (mFragment instanceof ObtainOrderFragment) {
            ((ObtainOrderFragment) mFragment).updateTotalPrice(String.valueOf(getTotalCost()));
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.select_item_prod_name)
        TextView tvProductName;

        @BindView(R.id.select_item_amount)
        TextView tvAmount;

        @BindView(R.id.select_item_btn_minus)
        Button btnMinus;

        @BindView(R.id.select_item_btn_plus)
        Button btnPlus;

        @BindView(R.id.select_item_btn_x)
        Button btnCancel;

        private ProductHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
