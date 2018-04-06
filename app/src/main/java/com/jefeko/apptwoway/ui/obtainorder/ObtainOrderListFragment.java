package com.jefeko.apptwoway.ui.obtainorder;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.ServiceCommon;
import com.jefeko.apptwoway.adapters.OrderListAdapter;
import com.jefeko.apptwoway.adapters.ProductCheckAdapter;
import com.jefeko.apptwoway.models.Order;
import com.jefeko.apptwoway.models.Product;
import com.jefeko.apptwoway.utils.CommonUtil;
import com.jefeko.apptwoway.utils.NumberFormatUtils;
import com.jefeko.apptwoway.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ObtainOrderListFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.rv_order_list) RecyclerView mOrderListView;
    private OrderListAdapter mOrderListAdapter;
    @BindView(R.id.spinner_search) Spinner spinner;
    @BindView(R.id.edt_search_text) EditText edtSearchText;
    @BindView(R.id.detail_obtain_order_scroll) ScrollView scroll;
    private String[] spinner_items = {"전체", "사업자번호", "상호", "대표자", "전화번호", "주소"};

    @BindView(R.id.layout_detail_obtain_order) FrameLayout layoutDetailOrder;
    @BindView(R.id.btn_detail_obtain_order_close) Button btnClose;
    @BindView(R.id.btn_date_start) Button btnDateStart;
    @BindView(R.id.btn_date_end) Button btnDateEnd;
    @BindView(R.id.btn_inquiry) Button btnSearch;
    @BindView(R.id.rv_check_product_list2) RecyclerView mProductCheckView;
    @BindView(R.id.radioGroup) RadioGroup mRadioGroup;
    @BindView(R.id.radioGroup2) RadioGroup mRadioGroup2;
    @BindView(R.id.tv_date_start) TextView tvDateStart;
    @BindView(R.id.tv_date_end) TextView tvDateEnd;
    @BindView(R.id.edt_company_name) EditText edtCompanyNmae;

    @BindView(R.id.tvCompanyType) TextView tvCompanyType;                   //처리상태
    @BindView(R.id.tvProcessStatus) TextView tvProcessStatus;               //구분
    @BindView(R.id.tvSelectedCompany) TextView tvSelectedCompany;           //선택업체명
    @BindView(R.id.tv_total_price) TextView tvTotalPrice;                   //합계금액
    @BindView(R.id.delivery_place_name) TextView delivery_place_name;       //딜리버리명
    @BindView(R.id.request) TextView request;                               //요구사항
    @BindView(R.id.jumun_cancel) Button jumun_cancel;                     //주문취소
    @BindView(R.id.jumun_confirm) Button jumun_confirm;                   //주문확인
    @BindView(R.id.jumun_update) Button jumun_update;                   //주문수정
    @BindView(R.id.jumun_change) Button jumun_change;                   //상품추가

    private ProductCheckAdapter mProductCheckAdapter;
    private Order order = null;
    private int mSearchOptionIndex = 0;
    private int mRadioIndex = 0;
    private String mOrderType = "000";
    private String strDateStart = "";
    private String strDateEnd = "";
    private DatePickerDialog dialogStart = null;
    private DatePickerDialog dialogEnd = null;
    private DatePickerDialog.OnDateSetListener listenerStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            strDateStart = getDateString(year, monthOfYear+1, dayOfMonth);
            tvDateStart.setText(strDateStart);
        }
    };
    private DatePickerDialog.OnDateSetListener listenerEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            strDateEnd = getDateString(year, monthOfYear+1, dayOfMonth);
            tvDateEnd.setText(strDateEnd);
        }
    };

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.radio0){
                mRadioIndex = 0;
            } else if(i == R.id.radio1){
                mRadioIndex = 1;
            } else if(i == R.id.radio2){
                mRadioIndex = 2;
            } else if(i == R.id.radio3){
                mRadioIndex = 3;
            }
        }
    };

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.radio4){
                mOrderType = "000";
            } else if(i == R.id.radio5) {
                mOrderType = "001";
            } else if(i == R.id.radio6) {
                mOrderType = "002";
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_obtain_order_list, container, false);
        ButterKnife.bind(this, rootView);

        initialize();

        btnClose.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnDateStart.setOnClickListener(this);
        btnDateEnd.setOnClickListener(this);
        jumun_cancel.setOnClickListener(this);
        jumun_confirm.setOnClickListener(this);
        jumun_update.setOnClickListener(this);
        jumun_change.setOnClickListener(this);

        return rootView;
    }

    /** 초기화 */
    private void initialize() {
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

        // 상품주문 내역
        mOrderListAdapter = new OrderListAdapter(this);
        mOrderListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderListView.setAdapter(mOrderListAdapter);

        // 상품 체크 리스트
        mProductCheckAdapter = new ProductCheckAdapter(this, true);
        mProductCheckView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductCheckView.setAdapter(mProductCheckAdapter);

        mRadioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
        mRadioGroup2.setOnCheckedChangeListener(radioGroupButtonChangeListener2);

        tvDateStart.setText(CommonUtil.getCurrentYYYYMMDD());
        tvDateEnd.setText(CommonUtil.getCurrentYYYYMMDD());

        Calendar c = Calendar.getInstance();

        dialogStart = new DatePickerDialog(getContext(), listenerStart, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialogEnd = new DatePickerDialog(getContext(), listenerEnd,  c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        strDateStart = CommonUtil.getCurrentYYYYMMDD();
        strDateEnd = CommonUtil.getCurrentYYYYMMDD();
    }

    private String getDateString(int year, int month, int day) {
        return  String.valueOf(year) + "-" + getMonthOrDayString(month) + "-"+ getMonthOrDayString(day);
    }

    private String getMonthOrDayString(int number) {
        String result = "";
        if(number < 10){
            result += "0";
        }
        result += String.valueOf(number);
        return result;
    }

    public void openDetailOrder(Order order) {
        this.order = order;
        mProductCheckAdapter.setProductList(order.getProductList());
        tvTotalPrice.setText(NumberFormatUtils.numberToCommaString(order.getOrder_price())+" 원");

        if (order.getOrder_status_code().equals(ServiceCommon.ORDER_STATUS_OBTAIN_ORDER_COMPLETED)){
            tvProcessStatus.setText("수주완료");
        } else if (order.getOrder_status_code().equals(ServiceCommon.ORDER_STATUS_OBTAIN_ORDER_CANCEL)){
            tvProcessStatus.setText("수주취소");
        } else  if (order.getOrder_status_code().equals(ServiceCommon.ORDER_STATUS_ORDER_COMPLETED)){
            tvProcessStatus.setText("발주완료");
        }

        if (PreferenceUtils.getPreferenceValueOfString(getActivity(), getString(R.string.COMPANY_ID)).equals(order.getCompany_id())){
            tvCompanyType.setText("직접주문");
        }else{
            tvCompanyType.setText("업체주문건");
        }

        tvSelectedCompany.setText("선택된 사업자 : "+order.getCompany_name());
        delivery_place_name.setText(order.getDelivery_place_name());
        request.setText(order.getRequest());

        if (layoutDetailOrder.getVisibility() == View.GONE) {
            layoutDetailOrder.setVisibility(View.VISIBLE);
        }
    }

    public void updateTotalPrice(String price) {
        tvTotalPrice.setText(NumberFormatUtils.numberToCommaString(price)+" 원");
    }

    public void closeDetailOrder() {
        if (layoutDetailOrder.getVisibility() == View.VISIBLE) {
            scroll.setScrollY(0);
            layoutDetailOrder.setVisibility(View.GONE);
        }
    }

    public void doSearch() {
        ((ObtainOrderManageActivity)getActivity()).requestGetReceiveOrderList(mRadioIndex, mOrderType, strDateStart, strDateEnd, edtCompanyNmae.getText().toString());
        edtCompanyNmae.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detail_obtain_order_close:
                closeDetailOrder();
                break;
            case R.id.btn_inquiry:
                doSearch();
                break;
            case R.id.btn_date_start:
                if (dialogStart != null)
                    dialogStart.show();
                break;
            case R.id.btn_date_end:
                if (dialogEnd != null)
                    dialogEnd.show();
                break;
            case R.id.jumun_cancel:
                ((ObtainOrderManageActivity)getActivity()).requestSetOrder(order.getOrder_id(), "003");
                break;
            case R.id.jumun_confirm:
                ((ObtainOrderManageActivity)getActivity()).requestSetOrder(order.getOrder_id(), "002");;
                break;
            case R.id.jumun_change:
                ((ObtainOrderManageActivity)getActivity()).requestCustomer(order);
                break;
            case R.id.jumun_update:
                ((ObtainOrderManageActivity)getActivity()).requestSendOrderUpdate(order.getOrder_id(), order.getCompany_id(), order.getRequest(), String.valueOf(mProductCheckAdapter.getTotalCost()),  order.getDelivery_place_id(), mProductCheckAdapter.getProductList());
                break;

        }
    }

    public void setOrder(ArrayList<Order> order) {
        mOrderListAdapter.clearItem();
        for(int i = 0; i < order.size(); i++) {
            mOrderListAdapter.addItem(order.get(i));
        }
    }

    public void updateOrderList() {
        mOrderListAdapter.notifyDataSetChanged();
    }



    /** 더미 데이터 셋팅 */
    private void setDummyOrder() {
//        Order order;
//        for (int i=0; i<10; i++) {
//            order = new Order();
//            order.setDate("2018-01-12");
//            order.setCompany_name("(주)SN푸드");
//            order.setProduct_name("크림빵 외 3개");
//            order.setTotal_price(91000);
//            order.setProductList(setDummyProdList());
//            mOrderListAdapter.addItem(order);
//
//        }
    }

    /** 더미 데이터 셋팅 */
    private ArrayList<Product> setDummyProdList() {
        ArrayList<Product> productList = new ArrayList<>();
        Product product = new Product();
        product.setProd_name("믹스커피");
        product.setUnit("1 box");
        product.setSell_price("5,000");
        product.setPrice(5000);
        product.setCalAmount(1);
        productList.add(product);
        product = new Product();
        product.setProd_name("콜라");
        product.setUnit("1 box");
        product.setSell_price("1,000");
        product.setPrice(1000);
        product.setCalAmount(2);
        productList.add(product);
        product = new Product();
        product.setProd_name("크림빵");
        product.setUnit("1 box");
        product.setSell_price("3,000");
        product.setPrice(3000);
        product.setCalAmount(3);
        productList.add(product);
        return productList;
    }

}
