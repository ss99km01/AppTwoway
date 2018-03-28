package com.jefeko.apptwoway.ui.obtainorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.ServiceCommon;
import com.jefeko.apptwoway.models.Company;
import com.jefeko.apptwoway.models.Order;
import com.jefeko.apptwoway.models.Product;
import com.jefeko.apptwoway.models.ProductCategory;
import com.jefeko.apptwoway.models.Store;
import com.jefeko.apptwoway.ui.common.BaseActivity;
import com.jefeko.apptwoway.ui.order.OrderFragment;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ObtainOrderManageActivity extends BaseActivity {

    @BindView(R.id.obtain_order_tab)
    TabLayout mTabLayout;

    @BindView(R.id.obtain_order_viewpager)
    ViewPager mViewPager;

    ViewPagerAdapter mPagerAdapter;
    ArrayList<Company> mCompany = new ArrayList<Company>();
    ArrayList<ProductCategory> mProductCategory = new ArrayList<ProductCategory>();
    ArrayList<Product> mSearchProduct = new ArrayList<Product>();
    ArrayList<Store> mStore = new ArrayList<Store>();
    ArrayList<Order> mOrder = new ArrayList<Order>();
    private Order mUpdateOrder = null;

    public static final int FRAGMENT_INDEX_ORDER = 0;
    public static final int FRAGMENT_INDEX_ORDERLIST = 1;

    public static final int SEARCH_BUSI_NO = 1;
    public static final int SEARCH_COMPANY_NAME = 2;
    public static final int SEARCH_EMPLOYEE_NAME = 3;
    public static final int SEARCH_TEL_NO = 4;

    public static final int SEARCH_INDEX_0 = 0;
    public static final int SEARCH_INDEX_1 = 1;
    public static final int SEARCH_INDEX_2 = 2;
    public static final int SEARCH_INDEX_3 = 3;

    public static final String DEFAULT_ORDER_TYPE_CODE  = "002";
    public static final String DEFAULT_RECEIVABLE_PRICE  = "0";
    public static final String DEFAULT_ORDER_PAY_TYPE_CODE  = "009";
    public static final String DEFAULT_ORDER_STATUS_CODE  = "002";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_order_manage);
        ButterKnife.bind(this);
        mContext = this;

        setToolbar();
        mTabLayout.setupWithViewPager(mViewPager);
        setUpViewPager();
    }

    private void setUpViewPager() {
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new ObtainOrderFragment(), getString(R.string.obtain_order_reg));
        mPagerAdapter.addFragment(new ObtainOrderListFragment(), getString(R.string.obtain_order_list));
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == FRAGMENT_INDEX_ORDERLIST) {
                    ((ObtainOrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).doSearch();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == IntentIntegrator.REQUEST_CODE ) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).updateBarcodeResult(result.getContents());
//                Toast.makeText(this, "Scanned : "+result.getContents(), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void requestCustomerList(int index, String text) {
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), ServiceCommon.COMPANY_ID);
//                values.put(getString(R.string.BUY_YN), "Y");
        values.put(getString(R.string.SELL_YN), "Y");
        switch(index) {
            case SEARCH_BUSI_NO:
                values.put(getString(R.string.BUSINESS_NUMBER), text);
                break;
            case SEARCH_COMPANY_NAME:
                values.put(getString(R.string.COMPANY_NAME), text);
                break;
            case SEARCH_EMPLOYEE_NAME:
                values.put(getString(R.string.REPRESENTATIVE_NAME), text);
                break;
            case SEARCH_TEL_NO:
                values.put(getString(R.string.TEL_NO), text);
                break;
        }
        sendRequest(getString(R.string.REQUEST_API_CUSTOMERLIST), getString(R.string.api_customerList), values);
    }

    public void requestCustomer(Order order) {
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), ServiceCommon.COMPANY_ID);
//        values.put(getString(R.string.BUY_YN), "Y");
        values.put(getString(R.string.SELL_YN), "Y");
        values.put(getString(R.string.COMPANY_NAME), order.getCompany_name());
        mUpdateOrder = order;
        sendRequest(getString(R.string.REQUEST_API_CUSTOMER), getString(R.string.api_customerList), values);
    }

    public void requestGetHeadQuarters() {
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), ServiceCommon.COMPANY_ID);
        sendRequest(getString(R.string.REQUEST_API_HEADQUARTERS), getString(R.string.api_customerList), values);
    }

    public void requestGetReceiveOrderTouchKeyCategoryList(String buyCompanyID, String chainYN) {
        Map<String, String> values = new HashMap<>();
        values.put("buy_company_id", buyCompanyID);
        values.put("sell_company_id", ServiceCommon.COMPANY_ID);
        values.put("chain_yn", chainYN);
        sendRequest(getString(R.string.REQUEST_API_RECEIVEORDERTOUCHKEYCATEGORYLIST), getString(R.string.api_receiveOrderTouchKeyCategoryList), values);
    }

    public void requestGetReceiveOrderTouchKeyProdList(String buyCompanyID, String chainYN, int index, String prodName) {
        Map<String, String> values = new HashMap<>();
        values.put("buy_company_id", buyCompanyID);
        values.put("sell_company_id", ServiceCommon.COMPANY_ID);
        values.put("chain_yn", chainYN);
        values.put("category_order", mProductCategory.get(index).getCategory_order());
        values.put("prod_name", prodName);
        sendRequest(getString(R.string.REQUEST_API_RECEIVEORDERTOUCHKEYPRODLIST), getString(R.string.api_receiveOrderTouchKeyProdList), values);
    }

    public void requestGetSellProdList(String buyCompanyID, String chainYN, String prodName) {
        Map<String, String> values = new HashMap<>();
        values.put("buy_company_id", buyCompanyID);
        values.put("sell_company_id", ServiceCommon.COMPANY_ID);
        values.put("chain_yn", chainYN);
        values.put("prod_name", prodName);
        sendRequest(getString(R.string.REQUEST_API_SELLPRODLIST), getString(R.string.api_sellProdList), values);
    }

    public void requestGetStoreList() {
        Map<String, String> values = new HashMap<>();
        values.put("company_id", ServiceCommon.COMPANY_ID);
        sendRequest(getString(R.string.REQUEST_API_STORELIST), getString(R.string.api_storeList), values);
    }

    public void requestSendOrder(String buyCompanyID, String request, String orderPrice, int storeIndex, ArrayList<Product> prodList){
        Map<String, String> values = new HashMap<>();
        values.put("order_type_code", DEFAULT_ORDER_TYPE_CODE);
        values.put("buy_company_id", buyCompanyID);
        values.put("sell_company_id", ServiceCommon.COMPANY_ID);
        values.put("employee_id", ServiceCommon.EMPLOYEE_ID);
        values.put("request", request);
        values.put("order_price", orderPrice);
        values.put("receivable_price", DEFAULT_RECEIVABLE_PRICE);
        values.put("pay_price", orderPrice);
        values.put("order_pay_type_code", DEFAULT_ORDER_PAY_TYPE_CODE);
        values.put("order_ymd", CommonUtil.getCurrentYYYYMMDD());
        values.put("delivery_ymd", CommonUtil.getCurrentYYYYMMDD());
        values.put("delivery_place_id", mStore.get(storeIndex).getStore_id());
        values.put("order_status_code", DEFAULT_ORDER_STATUS_CODE);
        values.put("products", makeProdListJson(prodList));
        sendRequest(getString(R.string.REQUEST_API_SENDORDER), getString(R.string.api_sendOrder), values);
    }

    public void requestSendOrderUpdate(String orderID, String buyCompanyID, String request, String orderPrice, String Store_id, ArrayList<Product> prodList){
        Map<String, String> values = new HashMap<>();
        values.put("order_id", orderID);
        values.put("order_type_code", DEFAULT_ORDER_TYPE_CODE);
        values.put("buy_company_id", buyCompanyID);
        values.put("sell_company_id", ServiceCommon.COMPANY_ID);
        values.put("employee_id", ServiceCommon.EMPLOYEE_ID);
        values.put("request", request);
        values.put("order_price", orderPrice);
        values.put("receivable_price", DEFAULT_RECEIVABLE_PRICE);
        values.put("pay_price", orderPrice);
        values.put("order_pay_type_code", DEFAULT_ORDER_PAY_TYPE_CODE);
        values.put("order_ymd", CommonUtil.getCurrentYYYYMMDD());
        values.put("delivery_ymd", CommonUtil.getCurrentYYYYMMDD());
        values.put("delivery_place_id", Store_id);
        values.put("order_status_code", DEFAULT_ORDER_STATUS_CODE);
        values.put("products", makeProdListJson(prodList));
        sendRequest(getString(R.string.REQUEST_API_SENDORDER_UPDATE), getString(R.string.api_sendOrder), values);
    }

    /**
     * 주문 수정
     * @param orderID
     */
    public void requestSetOrder(String orderID, String orderStatusCode) {
        Map<String, String> values = new HashMap<>();
        values.put("order_id", orderID);
        values.put("order_status_code", orderStatusCode);
        sendRequest(getString(R.string.REQUEST_API_SETORDER), getString(R.string.api_setOrder), values);
    }

    public void requestGetReceiveOrderList(int searchIndex, String orderTypeCode, String start, String end) {
        Map<String, String> values = new HashMap<>();
        values.put("company_id", ServiceCommon.COMPANY_ID);
        if (!"000".equals(orderTypeCode)) {
            values.put("order_type_code", orderTypeCode);
        }
        values.put("order_ymd1", start);
        values.put("order_ymd2", end);
        switch(searchIndex) {
            case SEARCH_INDEX_0:
                break;
            case SEARCH_INDEX_1:
                values.put("order_status_code", "001");
                break;
            case SEARCH_INDEX_2:
                values.put("order_status_code", "002");
                break;
            case SEARCH_INDEX_3:
                values.put("order_status_code", "003");
                break;
        }
        showProgress();
        sendRequest(getString(R.string.REQUEST_API_RECEIVEORDERLIST), getString(R.string.api_receiveOrderList), values);
    }

    public void requestGetOrderProductList(String orderID) {
        Map<String, String> values = new HashMap<>();
        values.put("order_id", orderID);
        sendRequest(getString(R.string.REQUEST_API_ORDERPRODUCTLIST), getString(R.string.api_orderProductList), values);
    }

    public String makeProdListJson(ArrayList<Product> prodList) {
        try {
            JSONArray jArray = new JSONArray();
            for (int i = 0; i < prodList.size(); i++)
            {
                JSONObject sObject = new JSONObject();
                sObject.put("company_id", prodList.get(i).getCompany_id());
                sObject.put("prod_id", prodList.get(i).getProd_id());
                sObject.put("sell_price", prodList.get(i).getSell_price());
                sObject.put("amount", prodList.get(i).getAmount());
                sObject.put("price", prodList.get(i).getOrder_price());
                jArray.put(sObject);
            }
            return jArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onSuccess(String code, String response) {
        LogUtils.e("[" + code + "] onSuccess = " + response);

        try {
            JSONObject obj = new JSONObject(response);

            if (getString(R.string.REQUEST_API_CUSTOMERLIST).equals(code)) {
                processCustomerList(obj);
            } else if (getString(R.string.REQUEST_API_CUSTOMER).equals(code)) {
                processCustomer(obj);
            } else if (getString(R.string.REQUEST_API_HEADQUARTERS).equals(code)) {
                processGetHeadQuarters(obj);
            } else if (getString(R.string.REQUEST_API_RECEIVEORDERTOUCHKEYCATEGORYLIST).equals(code)) {
                processGetReceiveOrderTouchKeyCategoryList(obj);
            } else if (getString(R.string.REQUEST_API_RECEIVEORDERTOUCHKEYPRODLIST).equals(code)) {
                processGetReceiveOrderTouchKeyProdList(obj);
            } else if (getString(R.string.REQUEST_API_SELLPRODLIST).equals(code)) {
                processGetSellProdList(obj);
            } else if (getString(R.string.REQUEST_API_STORELIST).equals(code)) {
                processGetStoreList(obj);
            } else if (getString(R.string.REQUEST_API_RECEIVEORDERLIST).equals(code)) {
                processGetReceiveOrderList(obj);
                dissmissProgress();
            } else if (getString(R.string.REQUEST_API_SENDORDER).equals(code)) {
                processSendOrder(obj);
            } else if (getString(R.string.REQUEST_API_SENDORDER_UPDATE).equals(code)) {
                processSendOrderUpdate(obj);
            } else if (getString(R.string.REQUEST_API_SETORDER).equals(code)) {
                processSetOrder(obj);
            }else if (getString(R.string.REQUEST_API_ORDERPRODUCTLIST).equals(code)) {
                processGetOrderProductList(obj);
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.faild), Toast.LENGTH_SHORT).show();
            LogUtils.e("[" + code + "] error = " + e.getMessage());
        }
    }

    private void processCustomerList(JSONObject obj) throws JSONException {
        mCompany.clear();

        JSONArray customerList = obj.getJSONArray("customerList");
        for (int i = 0; i < customerList.length(); i++) {
            JSONObject customer = customerList.getJSONObject(i);
            Company company = new Company();
            company.setBuy_yn(getJSONData(customer, "buy_yn"));
            company.setSell_yn(getJSONData(customer, "sell_yn"));
            company.setChain_yn(getJSONData(customer, "chain_yn"));
            company.setUse_yn(getJSONData(customer, "use_yn"));
            company.setCompany_id(getJSONData(customer, "company_id"));
            company.setCompany_name(getJSONData(customer, "company_name"));
            company.setBusi_no(getJSONData(customer, "busi_no"));
            company.setCompany_type_code(getJSONData(customer, "company_type_code"));
            company.setCompany_type_name(getJSONData(customer, "company_type_name"));
            company.setBusi_type(getJSONData(customer, "busi_type"));
            company.setBusi_item(getJSONData(customer, "busi_item"));
            company.setTel_no(getJSONData(customer, "tel_no"));
            company.setFax_no(getJSONData(customer, "fax_no"));
            company.setZip(getJSONData(customer, "zip"));
            company.setAddr1(getJSONData(customer, "addr1"));
            company.setAddr2(getJSONData(customer, "addr2"));
            company.setEmployee_name(getJSONData(customer, "employee_name"));
            company.setCell_no(getJSONData(customer, "cell_no"));
            company.setEmail(getJSONData(customer, "email"));
            company.setBank_code(getJSONData(customer, "bank_code"));
            company.setBank_name(getJSONData(customer, "bank_name"));
            company.setAccounter(getJSONData(customer, "accounter"));
            company.setAccount_no(getJSONData(customer, "account_no"));
            mCompany.add(company);
        }
        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setCompany(mCompany);
    }

    private void processCustomer(JSONObject obj) throws JSONException {
        mCompany.clear();
        Company company = new Company();

        JSONArray customerList = obj.getJSONArray("customerList");
        for (int i = 0; i < customerList.length(); i++) {
            JSONObject customer = customerList.getJSONObject(i);
            company.setBuy_yn(getJSONData(customer, "buy_yn"));
            company.setSell_yn(getJSONData(customer, "sell_yn"));
            company.setChain_yn(getJSONData(customer, "chain_yn"));
            company.setUse_yn(getJSONData(customer, "use_yn"));
            company.setCompany_id(getJSONData(customer, "company_id"));
            company.setCompany_name(getJSONData(customer, "company_name"));
            company.setBusi_no(getJSONData(customer, "busi_no"));
            company.setCompany_type_code(getJSONData(customer, "company_type_code"));
            company.setCompany_type_name(getJSONData(customer, "company_type_name"));
            company.setBusi_type(getJSONData(customer, "busi_type"));
            company.setBusi_item(getJSONData(customer, "busi_item"));
            company.setTel_no(getJSONData(customer, "tel_no"));
            company.setFax_no(getJSONData(customer, "fax_no"));
            company.setZip(getJSONData(customer, "zip"));
            company.setAddr1(getJSONData(customer, "addr1"));
            company.setAddr2(getJSONData(customer, "addr2"));
            company.setEmployee_name(getJSONData(customer, "employee_name"));
            company.setCell_no(getJSONData(customer, "cell_no"));
            company.setEmail(getJSONData(customer, "email"));
            company.setBank_code(getJSONData(customer, "bank_code"));
            company.setBank_name(getJSONData(customer, "bank_name"));
            company.setAccounter(getJSONData(customer, "accounter"));
            company.setAccount_no(getJSONData(customer, "account_no"));
            mCompany.add(company);
        }
        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setCompany(mCompany);
        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).orderUpdate(company, mUpdateOrder);
        if (mViewPager.getCurrentItem() != FRAGMENT_INDEX_ORDER) {
            mViewPager.setCurrentItem(FRAGMENT_INDEX_ORDER);
        }
    }

    private void processGetHeadQuarters(JSONObject obj) throws JSONException {
        boolean isHeadQuarters = false;
        JSONArray customerList = obj.getJSONArray("customerList");
        for (int i = 0; i < customerList.length(); i++) {
            JSONObject customer = customerList.getJSONObject(i);
            Company company = new Company();
            company.setBuy_yn(getJSONData(customer, "buy_yn"));
            company.setSell_yn(getJSONData(customer, "sell_yn"));
            company.setChain_yn(getJSONData(customer, "chain_yn"));
            company.setUse_yn(getJSONData(customer, "use_yn"));
            company.setCompany_id(getJSONData(customer, "company_id"));
            company.setCompany_name(getJSONData(customer, "company_name"));
            company.setBusi_no(getJSONData(customer, "busi_no"));
            company.setCompany_type_code(getJSONData(customer, "company_type_code"));
            company.setCompany_type_name(getJSONData(customer, "company_type_name"));
            company.setBusi_type(getJSONData(customer, "busi_type"));
            company.setBusi_item(getJSONData(customer, "busi_item"));
            company.setTel_no(getJSONData(customer, "tel_no"));
            company.setFax_no(getJSONData(customer, "fax_no"));
            company.setZip(getJSONData(customer, "zip"));
            company.setAddr1(getJSONData(customer, "addr1"));
            company.setAddr2(getJSONData(customer, "addr2"));
            company.setEmployee_name(getJSONData(customer, "employee_name"));
            company.setCell_no(getJSONData(customer, "cell_no"));
            company.setEmail(getJSONData(customer, "email"));
            company.setBank_code(getJSONData(customer, "bank_code"));
            company.setBank_name(getJSONData(customer, "bank_name"));
            company.setAccounter(getJSONData(customer, "accounter"));
            company.setAccount_no(getJSONData(customer, "account_no"));
            if (ServiceCommon.COMPANY_TYPE_002.equals(getJSONData(customer, "company_type_code"))) {
                isHeadQuarters = true;
                ((ObtainOrderFragment) mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setCompanyInfo(company);
                break;
            }
        }
        if (!isHeadQuarters) {
            CommonUtil.showAlertDialog(this, "본사가 존재하지 않습니다.");
        }
    }

    private void processGetReceiveOrderTouchKeyCategoryList(JSONObject obj) throws JSONException {
        mProductCategory.clear();
        ArrayList<String> categoryList = new ArrayList<String>();

        JSONArray receiveOrderTouchKeyCategoryList = obj.getJSONArray("receiveOrderTouchKeyCategoryList");
        for (int i = 0; i < receiveOrderTouchKeyCategoryList.length(); i++) {
            JSONObject orderTouchKeyCategory = receiveOrderTouchKeyCategoryList.getJSONObject(i);
            ProductCategory productcategory = new ProductCategory();
            productcategory.setTkey_id(getJSONData(orderTouchKeyCategory, "tkey_id"));
            productcategory.setCategory_order(getJSONData(orderTouchKeyCategory, "category_order"));
            productcategory.setCategory_name(getJSONData(orderTouchKeyCategory, "category_name"));
            mProductCategory.add(productcategory);
            categoryList.add(getJSONData(orderTouchKeyCategory, "category_name"));
        }
        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setProdCategory(categoryList);
    }

    private void processGetReceiveOrderTouchKeyProdList(JSONObject obj) throws JSONException {
        mSearchProduct.clear();

        JSONArray receiveOrderTouchKeyProdList = obj.getJSONArray("receiveOrderTouchKeyProdList");
        for (int i = 0; i < receiveOrderTouchKeyProdList.length(); i++) {
            JSONObject orderTouchKeyProd = receiveOrderTouchKeyProdList.getJSONObject(i);
            Product product = new Product();
            product.setCompany_id(getJSONData(orderTouchKeyProd, "company_id"));
            product.setProd_id(getJSONData(orderTouchKeyProd, "prod_id"));
            product.setProd_name(getJSONData(orderTouchKeyProd, "prod_name"));
            product.setChain_buy_yn(getJSONData(orderTouchKeyProd, "chain_buy_yn"));
            product.setChain_sell_yn(getJSONData(orderTouchKeyProd, "chain_sell_yn"));
            product.setStandard(getJSONData(orderTouchKeyProd, "standard"));
            product.setUnit(getJSONData(orderTouchKeyProd, "unit"));
            product.setMin_order(getJSONData(orderTouchKeyProd, "min_order"));
            product.setImg_id(getJSONData(orderTouchKeyProd, "img_id"));
            product.setBarcode(getJSONData(orderTouchKeyProd, "barcode"));
            product.setPrice_type_code(getJSONData(orderTouchKeyProd, "price_type_code"));
            product.setPrice_type_name(getJSONData(orderTouchKeyProd, "price_type_name"));
            product.setTax_yn(getJSONData(orderTouchKeyProd, "tax_yn"));
            product.setVat_yn(getJSONData(orderTouchKeyProd, "vat_yn"));
            product.setSell_price(getJSONData(orderTouchKeyProd, "sell_price"));
            mSearchProduct.add(product);
        }
        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setProd(mSearchProduct);
    }

    private void processGetSellProdList(JSONObject obj) throws JSONException {
        mSearchProduct.clear();

        JSONArray buyProdList = obj.getJSONArray("sellProdList");
        for (int i = 0; i < buyProdList.length(); i++) {
            JSONObject buyProd = buyProdList.getJSONObject(i);
            Product product = new Product();
            product.setCompany_id(getJSONData(buyProd, "company_id"));
            product.setProd_id(getJSONData(buyProd, "prod_id"));
            product.setProd_name(getJSONData(buyProd, "prod_name"));
            product.setChain_buy_yn(getJSONData(buyProd, "chain_buy_yn"));
            product.setChain_sell_yn(getJSONData(buyProd, "chain_sell_yn"));
            product.setStandard(getJSONData(buyProd, "standard"));
            product.setUnit(getJSONData(buyProd, "unit"));
            product.setMin_order(getJSONData(buyProd, "min_order"));
            product.setImg_id(getJSONData(buyProd, "img_id"));
            product.setBarcode(getJSONData(buyProd, "barcode"));
            product.setPrice_type_code(getJSONData(buyProd, "price_type_code"));
            product.setPrice_type_name(getJSONData(buyProd, "price_type_name"));
            product.setTax_yn(getJSONData(buyProd, "tax_yn"));
            product.setVat_yn(getJSONData(buyProd, "vat_yn"));
            product.setSell_price(getJSONData(buyProd, "sell_price"));
            mSearchProduct.add(product);
        }
        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setProd(mSearchProduct);
    }

    private void processGetStoreList(JSONObject obj) throws JSONException {
        mStore.clear();
        ArrayList<String> spinerList = new ArrayList<String>();

        JSONArray storeList = obj.getJSONArray("storeList");
        for (int i = 0; i < storeList.length(); i++) {
            JSONObject store = storeList.getJSONObject(i);
            Store temp = new Store();
            temp.setCompany_id(getJSONData(store, "company_id"));
            temp.setStore_id(getJSONData(store, "store_id"));
            temp.setStore_name(getJSONData(store, "store_name"));
            temp.setTel_no(getJSONData(store, "tel_no"));
            temp.setFax_no(getJSONData(store, "fax_no"));
            temp.setZip(getJSONData(store, "zip"));
            temp.setAddr1(getJSONData(store, "addr1"));
            temp.setAddr2(getJSONData(store, "addr2"));
            temp.setUse_yn(getJSONData(store, "use_yn"));
            mStore.add(temp);
            spinerList.add(getJSONData(store, "store_name"));
        }
        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setStoreSpiner(spinerList);
    }

    private void processSendOrder(JSONObject obj) throws JSONException {
        String result = obj.getString("result");

        if ("SUCCESS".equals(result)) {
            CommonUtil.showAlertDialog(this, getString(R.string.order_success), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (mViewPager.getCurrentItem() != FRAGMENT_INDEX_ORDERLIST) {
                        mViewPager.setCurrentItem(FRAGMENT_INDEX_ORDERLIST);
                        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).initOrder();
                    }
                }
            });
        } else {
            CommonUtil.showAlertDialog(this, getString(R.string.order_fail));
        }
    }

    private void processSendOrderUpdate(JSONObject obj) throws JSONException {
        String result = obj.getString("result");

        if ("SUCCESS".equals(result)) {
            CommonUtil.showAlertDialog(this, getString(R.string.order_update), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ((ObtainOrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).doSearch();
                    ((ObtainOrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).closeDetailOrder();
                    if (mViewPager.getCurrentItem() != FRAGMENT_INDEX_ORDERLIST) {
                        mViewPager.setCurrentItem(FRAGMENT_INDEX_ORDERLIST);
                        ((ObtainOrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).initOrder();
                    }
                }
            });
        } else {
            CommonUtil.showAlertDialog(this, getString(R.string.order_fail));
        }
    }

    private void processSetOrder(JSONObject obj) throws JSONException {
        String result = obj.getString("result");

        if ("SUCCESS".equals(result)) {
            CommonUtil.showAlertDialog(this, getString(R.string.order_status_change), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ((ObtainOrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).doSearch();
                    ((ObtainOrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).closeDetailOrder();
                }
            });
        } else {
            CommonUtil.showAlertDialog(this, getString(R.string.order_cancel_fail));
        }
    }


    private void processGetReceiveOrderList(JSONObject obj) throws JSONException {
        mOrder.clear();

        JSONArray receiveOrderList = obj.getJSONArray("receiveOrderList");
        for (int i = 0; i < receiveOrderList.length(); i++) {
            JSONObject order = receiveOrderList.getJSONObject(i);
            Order temp = new Order();
            temp.setOrder_id(getJSONData(order, "order_id"));
            temp.setCompany_id(getJSONData(order, "company_id"));
            temp.setCustomer_id(getJSONData(order, "customer_id"));
            temp.setCompany_name(getJSONData(order, "company_name"));
            temp.setEmployee_id(getJSONData(order, "employee_id"));
            temp.setEmployee_name(getJSONData(order, "employee_name"));
            temp.setRequest(getJSONData(order, "request"));
            temp.setOrder_price(getJSONData(order, "order_price"));
            temp.setReceivable_price(getJSONData(order, "receivable_price"));
            temp.setPay_price(getJSONData(order, "pay_price"));
            temp.setOrder_pay_type_code(getJSONData(order, "order_pay_type_code"));
            temp.setOrder_pay_type_name(getJSONData(order, "order_pay_type_name"));
            temp.setOrder_ymd(getJSONData(order, "order_ymd"));
            temp.setDelivery_ymd(getJSONData(order, "delivery_ymd"));
            temp.setDelivery_place_id(getJSONData(order, "delivery_place_id"));
            temp.setReceive_order_ymd(getJSONData(order, "receive_order_ymd"));
            temp.setOrder_status_code(getJSONData(order, "order_status_code"));
            temp.setOrder_status_name(getJSONData(order, "order_status_name"));
            temp.setStore_id(getJSONData(order, "store_id"));
            temp.setDelivery_place_name(getJSONData(order, "delivery_place_name"));
            temp.setTel_no(getJSONData(order, "tel_no"));
            temp.setFax_no(getJSONData(order, "fax_no"));
            temp.setZip(getJSONData(order, "zip"));
            temp.setAddr1(getJSONData(order, "addr1"));
            temp.setAddr2(getJSONData(order, "addr2"));
            mOrder.add(temp);
        }
        for (int i = 0; i < mOrder.size(); i++) {
            requestGetOrderProductList(mOrder.get(i).getOrder_id());
        }
        ((ObtainOrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).setOrder(mOrder);
        ((ObtainOrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).closeDetailOrder();
    }

    private void processGetOrderProductList(JSONObject obj) throws JSONException {
        ArrayList<Product> prodList = new ArrayList<Product>();
        JSONArray orderProductList = obj.getJSONArray("orderProductList");
        for (int i = 0; i < orderProductList.length(); i++) {
            JSONObject orderProduct = orderProductList.getJSONObject(i);
            Product product = new Product();
            product.setOrder_id(getJSONData(orderProduct, "order_id"));
            product.setCompany_id(getJSONData(orderProduct, "company_id"));
            product.setProd_id(getJSONData(orderProduct, "prod_id"));
            product.setSell_price(getJSONData(orderProduct, "price"));
            product.setAmount(getJSONData(orderProduct, "amount"));
            product.setOrder_price(getJSONData(orderProduct, "order_price"));
            product.setProd_name(getJSONData(orderProduct, "prod_name"));
            product.setStandard(getJSONData(orderProduct, "standard"));
            product.setUnit(getJSONData(orderProduct, "unit"));
            product.setMin_order(getJSONData(orderProduct, "min_order"));
            product.setImg_id(getJSONData(orderProduct, "img_id"));
            product.setBarcode(getJSONData(orderProduct, "barcode"));
            product.setPrice_type_code(getJSONData(orderProduct, "price_type_code"));
            product.setTax_yn(getJSONData(orderProduct, "tax_yn"));
            product.setVat_yn(getJSONData(orderProduct, "vat_yn"));
            prodList.add(product);
        }
        insertOrderProdList(prodList);
        ((ObtainOrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).updateOrderList();
    }

    private void insertOrderProdList(ArrayList<Product> prodlist) {
        String orderID = "";
        for (int i = 0; i < prodlist.size(); i++) {
            orderID = ((Product)prodlist.get(i)).getOrder_id();
            break;
        }

        for (int i = 0; i < mOrder.size(); i++) {
            if (((Order)mOrder.get(i)).getOrder_id().equals(orderID)) {
                ((Order)mOrder.get(i)).setProductList(prodlist);
                break;
            }
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }
}
