package com.jefeko.apptwoway.ui.order;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.adapters.CompanyListAdapter;
import com.jefeko.apptwoway.adapters.ProductCheckAdapter;
import com.jefeko.apptwoway.adapters.ProductListAdapter;
import com.jefeko.apptwoway.adapters.ProductSelectAdapter;
import com.jefeko.apptwoway.models.Company;
import com.jefeko.apptwoway.models.Order;
import com.jefeko.apptwoway.models.Product;
import com.jefeko.apptwoway.ui.waytalk.WayTalkMmsActivity;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.NumberFormatUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment implements View.OnClickListener {

    Context mContext;


    @BindView(R.id.spinner_search) Spinner spinner;
    @BindView(R.id.btn_customer_search) Button btnCustomerSearch;
    @BindView(R.id.edt_search_text) EditText edtSearchText;

    @BindView(R.id.btn_select_my_company) Button btnSelectCompany;
    @BindView(R.id.rv_company_list) RecyclerView mCompanyListView;
    private CompanyListAdapter mCompanyListAdapter;
    private String[] spinner_items = {"전체", "사업자번호", "상호", "대표자", "전화번호", "주소"};
//    private String[] spinner_category_items = {"전체", "카테고리1", "카테고리2", "카테고리3", "카테고리4"};
    @BindView(R.id.btn_qr_code) Button btnQRCode;

    /* 사업자 정보 확인하기 */
    @BindView(R.id.layout_order_company_info) FrameLayout layoutCompanyInfo;
    @BindView(R.id.btn_search_again) Button btnSearchAgain;
    @BindView(R.id.btn_order) Button btnOrder;
    @BindView(R.id.tv_busi_no) TextView tvBusiNo;
    @BindView(R.id.tv_company_name) TextView tvCompanyName;
    @BindView(R.id.tv_employee_name) TextView tvEmployeeName;
    @BindView(R.id.tv_tel_no) TextView tvTelNo;
    @BindView(R.id.tv_address) TextView tvAddress;

    @BindView(R.id.layout_order_3) FrameLayout layoutOrder3;
    @BindView(R.id.spinner_category) Spinner spinnerCategory;
    @BindView(R.id.btn_prod_search) Button btnProdSearch;
    @BindView(R.id.edt_prod_search) EditText edtProdSearch;
    @BindView(R.id.rv_product_list) RecyclerView mProductListView;
    @BindView(R.id.rv_product_select) RecyclerView mProductSelectView;
    @BindView(R.id.btn_order_check) Button btnOrderCheck;
    @BindView(R.id.tv_seleted_company_1) TextView tvSeletedCompany_1;
    @BindView(R.id.tv_total_price_1) TextView tvTotalPrice_1;

    private ProductListAdapter mProductListAdapter;
    private ProductSelectAdapter mProductSelectAdapter;

    @BindView(R.id.layout_order_5) FrameLayout layoutOrder5;
    @BindView(R.id.rv_check_product_list) RecyclerView mProductCheckView;
    @BindView(R.id.tv_seleted_company_2) TextView tvSeletedCompany_2;
    @BindView(R.id.tv_total_price_2) TextView tvTotalPrice_2;
    @BindView(R.id.spinner_store) Spinner spinnerStore;
    @BindView(R.id.edt_request_message) EditText edtRequestMessage;
    @BindView(R.id.btn_order_confirm) Button btnOrderConfirm;
    @BindView(R.id.mms_btn) Button mms_btn;

    private ProductCheckAdapter mProductCheckAdapter;

    private Company mSelectedCompany = null;

    private int mSearchOptionIndex = 0;
    private int mProdCategoryIndex = 0;
    private int mStoreIndex = 0;

    public static final int SEARCH_BUSI_NO = 1;
    private boolean mIsUpdate = false;
    private Order mUpdateOrder = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, rootView);

        initialize();

        btnCustomerSearch.setOnClickListener(this);
        btnSelectCompany.setOnClickListener(this);
        btnSearchAgain.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnOrderCheck.setOnClickListener(this);
        btnOrderConfirm.setOnClickListener(this);
        btnQRCode.setOnClickListener(this);
        btnProdSearch.setOnClickListener(this);
        mms_btn.setOnClickListener(this);

        btnOrderCheck.setEnabled(false);
        btnOrderConfirm.setEnabled(false);

        return rootView;
    }

    /** 초기화 */
    private void initialize() {
        mContext = getActivity();

        // 스피너
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, spinner_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSearchOptionIndex = position;
                edtSearchText.setText("");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 사업자 검색 리스트
        mCompanyListAdapter = new CompanyListAdapter(this);
        mCompanyListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCompanyListView.setAdapter(mCompanyListAdapter);

        // 상품 검색 리스트
        mProductListAdapter = new ProductListAdapter(this);
        mProductListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductListView.setAdapter(mProductListAdapter);

        // 상품 선택 리스트
        mProductSelectAdapter = new ProductSelectAdapter(this);
        mProductSelectView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductSelectView.setAdapter(mProductSelectAdapter);

        // 상품 체크 리스트
        mProductCheckAdapter = new ProductCheckAdapter(this, false);
        mProductCheckView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductCheckView.setAdapter(mProductCheckAdapter);

    }

    public void addSelectedItem(Product Product) {
        if (mProductSelectAdapter.addItem(Product)){
            mProductSelectView.scrollToPosition(mProductSelectAdapter.getItemCount() - 1);
        }
    }

    public void updateSelectedItem(Product Product) {
        if (mProductSelectAdapter.updateItem(Product)){
            mProductSelectView.scrollToPosition(mProductSelectAdapter.getItemCount() - 1);
        }
    }

    public void updateTotalPrice(String price) {
        tvTotalPrice_1.setText(NumberFormatUtils.numberToCommaString(price)+" 원");
        tvTotalPrice_2.setText(NumberFormatUtils.numberToCommaString(price)+" 원");

        if (Integer.valueOf(price) > 0) {
            btnOrderCheck.setEnabled(true);
            btnOrderConfirm.setEnabled(true);
        } else {
            btnOrderCheck.setEnabled(false);
            btnOrderConfirm.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_customer_search:
                ((OrderManageActivity)getActivity()).requestCustomerList(mSearchOptionIndex, edtSearchText.getText().toString());
                CommonUtil.hideKeyboard(mContext, edtSearchText);
                break;
            case R.id.btn_select_my_company:    // 본사 선택하기
                ((OrderManageActivity)getActivity()).requestGetHeadQuarters();
                break;
            case R.id.btn_order:
                if (layoutOrder3.getVisibility() == View.GONE) {
                    layoutOrder3.setVisibility(View.VISIBLE);
                    ((OrderManageActivity)getActivity()).requestGetOrderTouchKeyCategoryList(mSelectedCompany.getCompany_id(), mSelectedCompany.getChain_yn());
                    ((OrderManageActivity)getActivity()).requestGetStoreList();
                }
                break;
            case R.id.btn_search_again:
                if (layoutCompanyInfo.getVisibility() == View.VISIBLE) {
                    layoutCompanyInfo.setVisibility(View.GONE);
                }
                initOrder();
                break;
            case R.id.btn_order_check:
                if (layoutOrder5.getVisibility() == View.GONE) {
                    layoutOrder5.setVisibility(View.VISIBLE);
                    mProductCheckAdapter.setProductList(mProductSelectAdapter.getProductList());
                }
                break;
            case R.id.btn_qr_code:
                new IntentIntegrator((OrderManageActivity)mContext).initiateScan();
                break;
            case R.id.btn_prod_search:
                if (mProdCategoryIndex == 0) {
                    ((OrderManageActivity)getActivity()).requestGetBuyProdList(mSelectedCompany.getCompany_id(), mSelectedCompany.getChain_yn(), edtProdSearch.getText().toString());
                } else {
                    ((OrderManageActivity)getActivity()).requestGetOrderTouchKeyProdList(mSelectedCompany.getCompany_id(), mSelectedCompany.getChain_yn(), mProdCategoryIndex-1, edtProdSearch.getText().toString());
                }
                CommonUtil.hideKeyboard(mContext, edtProdSearch);
                break;
            case R.id.btn_order_confirm:
                if (mIsUpdate) {
                    ((OrderManageActivity)getActivity()).requestSendOrderUpdate(mUpdateOrder.getOrder_id(), mSelectedCompany.getCompany_id(), edtRequestMessage.getText().toString(), String.valueOf(mProductSelectAdapter.getTotalCost()),  mUpdateOrder.getStore_id(), mProductSelectAdapter.getProductList());
                } else {
                    ((OrderManageActivity)getActivity()).requestSendOrder(mSelectedCompany.getCompany_id(), edtRequestMessage.getText().toString(), String.valueOf(mProductSelectAdapter.getTotalCost()),  mStoreIndex, mProductSelectAdapter.getProductList());
                }
                break;

            case R.id.mms_btn:
                openActivity(WayTalkMmsActivity.class, mSelectedCompany.getCompany_id());
                break;
        }
    }

    public void orderUpdate(Company company, Order order) {
        mIsUpdate = true;
        mUpdateOrder = order;
        initOrder();
        setCompanyInfo(company);
        if (layoutOrder3.getVisibility() == View.GONE) {
            layoutOrder3.setVisibility(View.VISIBLE);
            ((OrderManageActivity)getActivity()).requestGetOrderTouchKeyCategoryList(mSelectedCompany.getCompany_id(), mSelectedCompany.getChain_yn());
            ((OrderManageActivity)getActivity()).requestGetStoreList();
            for (int i = 0; i < order.getProductList().size(); i++) {
                updateSelectedItem(order.getProductList().get(i));
            }
        }
    }

    protected void openActivity(Class<?> activity,String pal_company_id) {
        Intent intent = new Intent(mContext, activity);

        intent.putExtra("company_id",PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.COMPANY_ID)));
        intent.putExtra("user_id",PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.USER_ID)));
        intent.putExtra("pal_company_id",pal_company_id);

        mContext.startActivity(intent);
    }

    private void selectMyCompany() {
        Company company = new Company();
        company.setBusi_no(PreferenceUtils.getPreferenceValueOfString(mContext, getString(R.string.BUSINESS_NUMBER)));
        company.setCompany_name(PreferenceUtils.getPreferenceValueOfString(mContext, getString(R.string.COMPANY_NAME)));
        company.setEmployee_name(PreferenceUtils.getPreferenceValueOfString(mContext, getString(R.string.REPRESENTATIVE_NAME)));
        company.setTel_no(PreferenceUtils.getPreferenceValueOfString(mContext, getString(R.string.TEL_NO)));
        company.setAddr1(PreferenceUtils.getPreferenceValueOfString(mContext, getString(R.string.ADDR1)));
        company.setAddr2(PreferenceUtils.getPreferenceValueOfString(mContext, getString(R.string.ADDR2)));
        setCompanyInfo(company);
    }

    public void setCompanyInfo(Company company) {
        mSelectedCompany = company;
        tvBusiNo.setText(company.getBusi_no());
        tvCompanyName.setText(company.getCompany_name());
        tvSeletedCompany_1.setText("선택된 사업자 : "+company.getCompany_name());
        tvSeletedCompany_2.setText("선택된 사업자 : "+company.getCompany_name());
        tvEmployeeName.setText(company.getEmployee_name());
        tvTelNo.setText(company.getTel_no());
        tvAddress.setText(company.getAddr1()+company.getAddr2());
        if (layoutCompanyInfo.getVisibility() == View.GONE) {
            layoutCompanyInfo.setVisibility(View.VISIBLE);
        }
    }

    public void updateBarcodeResult(String content) {
        spinner.setSelection(SEARCH_BUSI_NO);
        edtSearchText.setText(content);
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if ( requestCode == IntentIntegrator.REQUEST_CODE ) {
//            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//            if (result == null) {
//                Toast.makeText(mContext, "Cancelled", Toast.LENGTH_SHORT).show();
//            } else {
//                edtSearchText.setText(result.getContents());
//                spinner.setSelection(SEARCH_BUSI_NO);
////                Toast.makeText(mContext, "Scanned : "+result.getContents(), Toast.LENGTH_SHORT).show();
//
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    /** 더미 데이터 셋팅 */
    private void setDummyCompany() {
        Company company = new Company();
        company.setCompany_name("(주)마창식품");
        company.setBusi_no("123-45-6789");
        company.setTel_no("031-456-2444");
        mCompanyListAdapter.addItem(company);
        company = new Company();
        company.setCompany_name("(주)에이스식자재");
        company.setBusi_no("123-45-6789");
        company.setTel_no("031-456-2444");
        mCompanyListAdapter.addItem(company);
        company = new Company();
        company.setCompany_name("(주)SN푸드");
        company.setBusi_no("123-45-6789");
        company.setTel_no("031-456-2444");
        mCompanyListAdapter.addItem(company);
        company = new Company();
        company.setCompany_name("(주)농협식자재마트");
        company.setBusi_no("123-45-6789");
        company.setTel_no("031-456-2444");
        mCompanyListAdapter.addItem(company);
    }

    /** 더미 데이터 셋팅 */
    private void setDummyProd() {
        Product product = new Product();
        product.setProd_name("믹스커피");
        product.setUnit("1 box");
        product.setSell_price("5,000");
        mProductListAdapter.addItem(product);
        product = new Product();
        product.setProd_name("콜라");
        product.setUnit("1 box");
        product.setSell_price("1,000");
        mProductListAdapter.addItem(product);
        product = new Product();
        product.setProd_name("크림빵");
        product.setUnit("1 box");
        product.setSell_price("3,000");
        mProductListAdapter.addItem(product);
    }

    /** 더미 데이터 셋팅 */
    private void setDummyProdSelect() {
        Product product = new Product();
        product.setProd_name("믹스커피");
        product.setUnit("1 box");
        product.setSell_price("5,000");
        product.setPrice(5000);
        product.setCalAmount(1);
        mProductSelectAdapter.addItem(product);
        product = new Product();
        product.setProd_name("콜라");
        product.setUnit("1 box");
        product.setSell_price("1,000");
        product.setPrice(1000);
        product.setCalAmount(1);
        mProductSelectAdapter.addItem(product);
        product = new Product();
        product.setProd_name("크림빵");
        product.setUnit("1 box");
        product.setSell_price("3,000");
        product.setPrice(3000);
        product.setCalAmount(1);
        mProductSelectAdapter.addItem(product);
    }

    public void setCompany(ArrayList<Company> company) {
        mCompanyListAdapter.clearItem();;
        for(int i = 0; i < company.size(); i++) {
            mCompanyListAdapter.addItem(company.get(i));
        }
        if (company.size() <= 0) {
            CommonUtil.showAlertDialog(mContext,getString(R.string.search_not_found));
        }
    }

    public void setProd(ArrayList<Product> product) {
        mProductListAdapter.clearItem();
        for(int i = 0; i < product.size(); i++) {
            mProductListAdapter.addItem(product.get(i));
        }
        if (product.size() <= 0) {
            CommonUtil.showAlertDialog(mContext,getString(R.string.search_not_found));
        }
    }

    public void initOrder() {
        edtSearchText.setText("");
        mCompanyListAdapter.clearItem();
        mProductListAdapter.clearItem();
        mProductSelectAdapter.clearItem();
        mProductCheckAdapter.clearItem();
        layoutCompanyInfo.setVisibility(View.GONE);
        layoutOrder3.setVisibility(View.GONE);
        layoutOrder5.setVisibility(View.GONE);
    }

    public void setProdCategory(ArrayList<String> category) {
        category.add(0, "전체");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProdCategoryIndex = position;
//                ((OrderManageActivity)getActivity()).requestgetOrderTouchKeyProdList(mSelectedCompany.getCompany_id(), mSelectedCompany.getChain_yn(), "category_order");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void setStoreSpiner(ArrayList<String> store) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, store);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(adapter);

        spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStoreIndex = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
