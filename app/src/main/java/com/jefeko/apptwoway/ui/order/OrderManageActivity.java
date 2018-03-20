package com.jefeko.apptwoway.ui.order;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bxl.BXLConst;
import com.bxl.config.editor.BXLConfigLoader;
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
import com.jefeko.apptwoway.ui.print.PrintActivity;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.LogUtils;
import com.jefeko.apptwoway.utils.NumberFormatUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.config.JposEntry;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteEvent;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

public class OrderManageActivity extends BaseActivity implements View.OnClickListener, StatusUpdateListener, OutputCompleteListener, ErrorListener {

    @BindView(R.id.order_tab)
    TabLayout mTabLayout;

    @BindView(R.id.order_viewpager)
    ViewPager mViewPager;

    ViewPagerAdapter mPagerAdapter;
    ArrayList<Company> mCompany = new ArrayList<Company>();
    ArrayList<ProductCategory> mProductCategory = new ArrayList<ProductCategory>();
    ArrayList<Product> mSearchProduct = new ArrayList<Product>();
    ArrayList<Store> mStore = new ArrayList<Store>();
    ArrayList<Order> mOrder = new ArrayList<Order>();

    public static final int FRAGMENT_INDEX_ORDER = 0;
    public static final int FRAGMENT_INDEX_ORDERLIST = 1;

//    public static final int SEARCH_ALL = 0;
    public static final int SEARCH_BUSI_NO = 1;
    public static final int SEARCH_COMPANY_NAME = 2;
    public static final int SEARCH_EMPLOYEE_NAME = 3;
    public static final int SEARCH_TEL_NO = 4;

    public static final String DEFAULT_ORDER_TYPE_CODE  = "001";
    public static final String DEFAULT_RECEIVABLE_PRICE  = "0";
    public static final String DEFAULT_ORDER_PAY_TYPE_CODE  = "009";
    public static final String DEFAULT_ORDER_STATUS_CODE  = "001";

    private static final int REQUEST_CODE_BLUETOOTH = 1;

    private static final String DEVICE_ADDRESS_START = " (";
    private static final String DEVICE_ADDRESS_END = ")";

    private final ArrayList<CharSequence> bondedDevices = new ArrayList<>();
    private ProgressDialog mProgressDialog = null;

    private BXLConfigLoader bxlConfigLoader;
    private POSPrinter posPrinter;
    private String logicalName;

    String printer_yn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manage);
        ButterKnife.bind(this);
        mContext = this;

        setToolbar();
        mTabLayout.setupWithViewPager(mViewPager);
        setUpViewPager();

        doSetting();

        setBondedDevices();
        printSet();
    }

    private void printSet(){
        if  (bondedDevices.size() <= 0 )
            return;

        bxlConfigLoader = new BXLConfigLoader(this);
        try {
            bxlConfigLoader.openFile();
        } catch (Exception e) {
            e.printStackTrace();
            bxlConfigLoader.newFile();
        }
        posPrinter = new POSPrinter(this);

        posPrinter.addErrorListener(this);
        posPrinter.addOutputCompleteListener(this);
        posPrinter.addStatusUpdateListener(this);

        String device = bondedDevices.get(0).toString();

        String name = device.substring(0, device.indexOf(DEVICE_ADDRESS_START));

        String address = device.substring(device.indexOf(DEVICE_ADDRESS_START)+ DEVICE_ADDRESS_START.length(),device.indexOf(DEVICE_ADDRESS_END));

        try {
            for (Object entry : bxlConfigLoader.getEntries()) {
                JposEntry jposEntry = (JposEntry) entry;
                bxlConfigLoader.removeEntry(jposEntry.getLogicalName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            logicalName = setProductName(name);
            bxlConfigLoader.addEntry(logicalName, BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER, logicalName, BXLConfigLoader.DEVICE_BUS_BLUETOOTH, address);

            bxlConfigLoader.saveFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViewPager() {
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new OrderFragment(), getString(R.string.order_reg));
        mPagerAdapter.addFragment(new OrderListFragment(), getString(R.string.order_list));
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Log.e("ss99km01", "ss99km01 setUpViewPager onPageSelected");
                if (position == FRAGMENT_INDEX_ORDERLIST) {
                    Log.e("ss99km01", "ss99km01 setUpViewPager onPageSelected   FRAGMENT_INDEX_ORDERLIST");
                    requestGetOrderList();
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
                ((OrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).updateBarcodeResult(result.getContents());
//                Toast.makeText(this, "Scanned : "+result.getContents(), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 설정조회
     */
    public void doSetting() {
        //로그인 api
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.USER_ID), PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.USER_ID)));
        sendRequest(getString(R.string.REQUEST_API_GETAPPSETTING), getString(R.string.api_getAppSetting), values);
    }

    public void requestCustomerList(int index, String text) {
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), ServiceCommon.COMPANY_ID);
//                values.put(getString(R.string.BUY_YN), "Y");
//        values.put(getString(R.string.SELL_YN), "Y");
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

    public void requestGetHeadQuarters() {
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.COMPANY_ID), ServiceCommon.COMPANY_ID);
        sendRequest(getString(R.string.REQUEST_API_HEADQUARTERS), getString(R.string.api_customerList), values);
    }

    public void requestGetOrderTouchKeyCategoryList(String selCompanyID, String chainYN) {
        Map<String, String> values = new HashMap<>();
        values.put("buy_company_id", ServiceCommon.COMPANY_ID);
        values.put("sell_company_id", selCompanyID);
        values.put("chain_yn", chainYN);
        sendRequest(getString(R.string.REQUEST_API_ORDERTOUCHKEYCATEGORYLIST), getString(R.string.api_orderTouchKeyCategoryList), values);
    }

    public void requestGetOrderTouchKeyProdList(String selCompanyID, String chainYN, int index, String prodName) {
        Map<String, String> values = new HashMap<>();
        values.put("buy_company_id", ServiceCommon.COMPANY_ID);
        values.put("sell_company_id", selCompanyID);
        values.put("chain_yn", chainYN);
        values.put("category_order", mProductCategory.get(index).getCategory_order());
        values.put("prod_name", prodName);
        sendRequest(getString(R.string.REQUEST_API_ORDERTOUCHKEYPRODLIST), getString(R.string.api_orderTouchKeyProdList), values);
    }

    public void requestGetBuyProdList(String selCompanyID, String chainYN, String prodName) {
        Map<String, String> values = new HashMap<>();
        values.put("buy_company_id", ServiceCommon.COMPANY_ID);
        values.put("sell_company_id", selCompanyID);
        values.put("chain_yn", chainYN);
        values.put("prod_name", prodName);
        sendRequest(getString(R.string.REQUEST_API_BUYPRODLIST), getString(R.string.api_buyProdList), values);
    }

    public void requestGetStoreList() {
        Map<String, String> values = new HashMap<>();
        values.put("company_id", ServiceCommon.COMPANY_ID);
        sendRequest(getString(R.string.REQUEST_API_STORELIST), getString(R.string.api_storeList), values);
    }

    public void requestSendOrder(String selCompanyID, String request, String orderPrice, int storeIndex, ArrayList<Product> prodList){
        Map<String, String> values = new HashMap<>();
        values.put("order_type_code", DEFAULT_ORDER_TYPE_CODE);
        values.put("buy_company_id", ServiceCommon.COMPANY_ID);
        values.put("sell_company_id", selCompanyID);
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

    public void requestSendOrder(String selCompanyID, String request, String orderPrice, String Store_id, ArrayList<Product> prodList){
        Map<String, String> values = new HashMap<>();
        values.put("order_type_code", DEFAULT_ORDER_TYPE_CODE);
        values.put("buy_company_id", ServiceCommon.COMPANY_ID);
        values.put("sell_company_id", selCompanyID);
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

    public void requestGetOrderList() {
        Log.e("ss99km01", "ss99km01 requestGetOrderList");
        Map<String, String> values = new HashMap<>();
        values.put("company_id", ServiceCommon.COMPANY_ID);
        showProgress();
        sendRequest(getString(R.string.REQUEST_API_ORDERLIST), getString(R.string.api_orderList), values);
    }

    public void requestGetOrderProductList(String orderID) {
        Map<String, String> values = new HashMap<>();
        values.put("order_id", orderID);
        sendRequest(getString(R.string.REQUEST_API_ORDERPRODUCTLIST), getString(R.string.api_orderProductList), values);
    }

    /**
     * 주문 수정
     * @param orderID
     */
    public void requestSetOrder(String orderID) {
        Map<String, String> values = new HashMap<>();
        values.put("order_id", orderID);
        values.put("order_status_code", "004");
        sendRequest(getString(R.string.REQUEST_API_SETORDER), getString(R.string.api_setOrder), values);
    }

    public String makeProdListJson(ArrayList<Product> prodList) {
        try {
            JSONArray jArray = new JSONArray();
            for (int i = 0; i < prodList.size(); i++) {
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
            } else if (getString(R.string.REQUEST_API_HEADQUARTERS).equals(code)) {
                processGetHeadQuarters(obj);
            } else if (getString(R.string.REQUEST_API_ORDERTOUCHKEYCATEGORYLIST).equals(code)) {
                processGetOrderTouchKeyCategoryList(obj);
            } else if (getString(R.string.REQUEST_API_ORDERTOUCHKEYPRODLIST).equals(code)) {
                processGetOrderTouchKeyProdList(obj);
            } else if (getString(R.string.REQUEST_API_BUYPRODLIST).equals(code)) {
                processGetBuyProdList(obj);
            } else if (getString(R.string.REQUEST_API_STORELIST).equals(code)) {
                processGetStoreList(obj);
            } else if (getString(R.string.REQUEST_API_ORDERLIST).equals(code)) {
                processGetOrderList(obj);
                dissmissProgress();
            } else if (getString(R.string.REQUEST_API_SENDORDER).equals(code)) {
                processSendOrder(obj);
            } else if (getString(R.string.REQUEST_API_ORDERPRODUCTLIST).equals(code)) {
                processGetOrderProductList(obj);
            } else if (getString(R.string.REQUEST_API_SETORDER).equals(code)) {
                processSetOrder(obj);
            } else if (getString(R.string.REQUEST_API_SENDORDER_UPDATE).equals(code)) {
                processSendOrderUpdate(obj);
            } else if(code.equals(getString(R.string.REQUEST_API_GETAPPSETTING))) {
                if (!obj.isNull("appSetting")) {
                    JSONObject content = obj.getJSONObject("appSetting");
                    if (content != null) {
                        printer_yn = content.getString("printer_yn");
                    } else {
                        printer_yn = "N";
                    }
                } else {
                    printer_yn = "N";
                }
            }
//            } else if (code.contains(getString(R.string.REQUEST_API_ORDERPRODUCTLIST))) {
//                code = code.replace(getString(R.string.REQUEST_API_ORDERPRODUCTLIST), "");
//                processInsertProdList(obj, Integer.valueOf(code));
//            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.faild), Toast.LENGTH_SHORT).show();
            LogUtils.e("[" + code + "] error = " + e.getMessage());
//            dissmissProgress();
        }
//        dissmissProgress();

    }

    private void processGetOrderTouchKeyProdList(JSONObject obj) throws JSONException {
        mSearchProduct.clear();

        JSONArray orderTouchKeyProdList = obj.getJSONArray("orderTouchKeyProdList");
        for (int i = 0; i < orderTouchKeyProdList.length(); i++) {
            JSONObject orderTouchKeyProd = orderTouchKeyProdList.getJSONObject(i);
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
        ((OrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setProd(mSearchProduct);
    }

    private void processGetOrderTouchKeyCategoryList(JSONObject obj) throws JSONException {
        mProductCategory.clear();
        ArrayList<String> categoryList = new ArrayList<String>();

        JSONArray orderTouchKeyCategoryList = obj.getJSONArray("orderTouchKeyCategoryList");
        for (int i = 0; i < orderTouchKeyCategoryList.length(); i++) {
            JSONObject orderTouchKeyCategory = orderTouchKeyCategoryList.getJSONObject(i);
            ProductCategory productcategory = new ProductCategory();
            productcategory.setTkey_id(getJSONData(orderTouchKeyCategory, "tkey_id"));
            productcategory.setCategory_order(getJSONData(orderTouchKeyCategory, "category_order"));
            productcategory.setCategory_name(getJSONData(orderTouchKeyCategory, "category_name"));
            mProductCategory.add(productcategory);
            categoryList.add(getJSONData(orderTouchKeyCategory, "category_name"));
        }
        ((OrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setProdCategory(categoryList);
    }

    private void processGetBuyProdList(JSONObject obj) throws JSONException {
        mSearchProduct.clear();

        JSONArray buyProdList = obj.getJSONArray("buyProdList");
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
        ((OrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setProd(mSearchProduct);
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
        ((OrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setCompany(mCompany);
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
                ((OrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setCompanyInfo(company);
                break;
            }
        }
        if (!isHeadQuarters) {
            CommonUtil.showAlertDialog(this,"본사가 존재하지 않습니다.");
        }
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
        ((OrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).setStoreSpiner(spinerList);
    }

    private void processGetOrderList(JSONObject obj) throws JSONException {
        mOrder.clear();

        JSONArray orderList = obj.getJSONArray("orderList");
        for (int i = 0; i < orderList.length(); i++) {
            JSONObject order = orderList.getJSONObject(i);
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
        ((OrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).setOrder(mOrder);
        ((OrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).closeDetailOrder();
    }

    private void processSendOrder(JSONObject obj) throws JSONException {
        String result = obj.getString("result");

        if ("SUCCESS".equals(result)) {
            CommonUtil.showAlertDialog(this, getString(R.string.order_success), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (mViewPager.getCurrentItem() != FRAGMENT_INDEX_ORDERLIST) {
                        mViewPager.setCurrentItem(FRAGMENT_INDEX_ORDERLIST);
                        ((OrderFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDER)).initOrder();
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
            CommonUtil.showAlertDialog(this, getString(R.string.order_success), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    requestGetOrderList();
                }
            });
        } else {
            CommonUtil.showAlertDialog(this, getString(R.string.order_fail));
        }
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
        ((OrderListFragment)mPagerAdapter.getItem(FRAGMENT_INDEX_ORDERLIST)).updateOrderList();
    }

    private void processSetOrder(JSONObject obj) throws JSONException {
        String result = obj.getString("result");

        if ("SUCCESS".equals(result)) {
            CommonUtil.showAlertDialog(this, getString(R.string.order_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    requestGetOrderList();
                }
            });
        } else {
            CommonUtil.showAlertDialog(this, getString(R.string.order_cancel_fail));
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (posPrinter != null)
                posPrinter.close();
        } catch (JposException e) {
            e.printStackTrace();
        }
    }

    private String setProductName(String name){
        String productName = BXLConfigLoader.PRODUCT_NAME_SPP_R200II;

        if((name.indexOf("SPP-R200II")>=0)){
            if(name.length() > 10){
                if(name.substring(10, 11).equals("I")){
                    productName = BXLConfigLoader.PRODUCT_NAME_SPP_R200III;
                }
            }
        }else if((name.indexOf("SPP-R210")>=0)){
            productName = BXLConfigLoader.PRODUCT_NAME_SPP_R210;
        }else if((name.indexOf("SPP-R310")>=0)){
            productName = BXLConfigLoader.PRODUCT_NAME_SPP_R310;
        }else if((name.indexOf("SPP-R300")>=0)){
            productName = BXLConfigLoader.PRODUCT_NAME_SPP_R300;
        }else if((name.indexOf("SPP-R400")>=0)){
            productName = BXLConfigLoader.PRODUCT_NAME_SPP_R400;
        }

        return productName;
    }

    private void setBondedDevices() {
        logicalName = null;
        bondedDevices.clear();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        Set<BluetoothDevice> bondedDeviceSet = bluetoothAdapter
                .getBondedDevices();

        for (BluetoothDevice device : bondedDeviceSet) {
            bondedDevices.add(device.getName() + DEVICE_ADDRESS_START + device.getAddress() + DEVICE_ADDRESS_END);
        }
    }

    public void print(Order order) {

        LogUtils.d("============================");

        try {
            posPrinter.open(logicalName);
            posPrinter.claim(0);
            posPrinter.setDeviceEnabled(true);

            posPrinter.setCharacterEncoding(BXLConst.CS_KS5601);
            String ESC = new String(new byte[]{0x1b, 0x7c});
            String LF = "\n";
            String jump1 = "    ";
            String jump2 = "             ";

            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT,  ESC + "!uC" + ESC + "cA" + ESC + "4C" + ESC + "bC" + "[영수증]"+ LF);
            //posPrinter.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "263036991401;4tk", POSPrinterConst.PTR_BCS_QRCODE, 8, 8, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  +  LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[매장명] " + order.getCompany_name() + LF);
            //posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[사업자] " + "305-32-73321"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[주소] " + order.getAddr1()+order.getAddr2()+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[대표자] " + order.getEmployee_name()+"  [TEL] "+order.getTel_no()+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[매출일] " + order.getOrder_ymd()+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[판매원] " + order.getEmployee_name()+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[영수증] " + order.getOrder_id()+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "================================"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "  상 품 명    단가   수량    금액"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            Product product = null;
            int total = 0;
            for (int i = 0; i < order.getProductList().size();i++) {
                product = new Product();
                product = order.getProductList().get(i);
                posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC" + " "+product.getProd_name()+"" + LF);
                total =  product.getPrice() * product.getAmount();

                posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC" + " 000015" + jump1 + product.getPrice() + jump1 + product.getAmount() + jump1 + total + LF);
            }
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "합 계 금 액"+jump2+ NumberFormatUtils.numberToCommaString(order.getOrder_price())+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "미 수 금 액"+jump2+NumberFormatUtils.numberToCommaString(order.getReceivable_price())+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "받 은 금 액"+jump2+NumberFormatUtils.numberToCommaString(order.getPay_price())+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + ""+ LF);
            if(order.getOrder_pay_type_code().equals("001")) {
                posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC" + "카       드" + jump2 + NumberFormatUtils.numberToCommaString(order.getPay_price()) + LF);
            }else {
                posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC" + "현       금" + jump2 + NumberFormatUtils.numberToCommaString(order.getPay_price()) + LF);
            }
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT,  ESC + "!uC" + ESC + "cA" + ESC + "bC" + "[구매자정보]"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[매장명] " + PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.COMPANY_NAME))+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[사업자] " + PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.BUSINESS_NUMBER)) + LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[주소] " + PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.ADDR1))+PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.ADDR2))+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[대표자] " + PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.REPRESENTATIVE_NAME))+"  [TEL] "+PreferenceUtils.getPreferenceValueOfString(this, getString(R.string.TEL_NO))+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, "\n");
        } catch (JposException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                posPrinter.close();
            } catch (JposException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void outputCompleteOccurred(final OutputCompleteEvent e) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "complete print", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void errorOccurred(final ErrorEvent arg0) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(getApplicationContext(), "Error status : " + getERMessage(arg0.getErrorCodeExtended()), Toast.LENGTH_SHORT).show();

                if(getERMessage(arg0.getErrorCodeExtended()).equals("Power off")){
                    try
                    {
                        posPrinter.close();
                    }
                    catch(JposException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    // port-close
                }else if(getERMessage(arg0.getErrorCodeExtended()).equals("Cover open")){
                    //re-print
                }else if(getERMessage(arg0.getErrorCodeExtended()).equals("Paper empty")){
                    //re-print
                }


            }
        });
    }

    @Override
    public void statusUpdateOccurred(final StatusUpdateEvent arg0) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "printer status : " + getSUEMessage(arg0.getStatus()), Toast.LENGTH_SHORT).show();

                if(getSUEMessage(arg0.getStatus()).equals("Power off")){
                    Toast.makeText(getApplicationContext(), "check the printer - Power off", Toast.LENGTH_SHORT).show();
                }else if(getSUEMessage(arg0.getStatus()).equals("Cover Open")){
                    //display message
                    Toast.makeText(getApplicationContext(), "check the printer - Cover Open", Toast.LENGTH_SHORT).show();
                }else if(getSUEMessage(arg0.getStatus()).equals("Cover OK")){
                    //re-print
                }else if(getSUEMessage(arg0.getStatus()).equals("Receipt Paper Empty")){
                    //display message
                    Toast.makeText(getApplicationContext(), "check the printer - Receipt Paper Empty", Toast.LENGTH_SHORT).show();
                }else if(getSUEMessage(arg0.getStatus()).equals("Receipt Paper OK")){
                    //re-print
                }
            }
        });
    }

    private static String getERMessage(int status){
        switch(status){
            case POSPrinterConst.JPOS_EPTR_COVER_OPEN:
                return "Cover open";

            case POSPrinterConst.JPOS_EPTR_REC_EMPTY:
                return "Paper empty";

            case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
                return "Power off";

            default:
                return "Unknown";
        }
    }


    private static String getSUEMessage(int status){
        switch(status){
            case JposConst.JPOS_SUE_POWER_ONLINE:
                return "Power on";

            case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
                return "Power off";

            case POSPrinterConst.PTR_SUE_COVER_OPEN:
                return "Cover Open";

            case POSPrinterConst.PTR_SUE_COVER_OK:
                return "Cover OK";

            case POSPrinterConst.PTR_SUE_REC_EMPTY:
                return "Receipt Paper Empty";

            case POSPrinterConst.PTR_SUE_REC_NEAREMPTY:
                return "Receipt Paper Near Empty";

            case POSPrinterConst.PTR_SUE_REC_PAPEROK:
                return "Receipt Paper OK";

            case POSPrinterConst.PTR_SUE_IDLE:
                return "Printer Idle";

            default:
                return "Unknown";
        }
    }
}
