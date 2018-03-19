package com.jefeko.apptwoway.models;


import java.util.ArrayList;

public class Order {
    private String order_id;
    private String company_id;
    private String customer_id;
    private String company_name;
    private String employee_id;
    private String employee_name;
    private String request;
    private String order_price;
    private String receivable_price;
    private String pay_price;
    private String order_pay_type_code;
    private String order_pay_type_name;
    private String order_ymd;
    private String delivery_ymd;
    private String delivery_place_id;
    private String receive_order_ymd;
    private String delivery_place_name;
    private String order_status_code;
    private String order_status_name;
    private String store_id;
    private String tel_no;
    private String fax_no;
    private String zip;
    private String addr1;
    private String addr2;
    private String product_name;
    private ArrayList<Product> ProductList;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getDelivery_place_id() {
        return delivery_place_id;
    }

    public void setDelivery_place_id(String delivery_place_id) {
        this.delivery_place_id = delivery_place_id;
    }

    public String getDelivery_place_name() {
        return delivery_place_name;
    }

    public void setDelivery_place_name(String delivery_place_name) {
        this.delivery_place_name = delivery_place_name;
    }

    public String getOrder_status_code() {
        return order_status_code;
    }

    public void setOrder_status_code(String order_status_code) {
        this.order_status_code = order_status_code;
    }

    public String getOrder_status_name() {
        return order_status_name;
    }

    public void setOrder_status_name(String order_status_name) {
        this.order_status_name = order_status_name;
    }

    public String getOrder_ymd() {
        return order_ymd;
    }

    public void setOrder_ymd(String date) {
        this.order_ymd = date;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

//    public String getProduct_name() {
//        return product_name;
//    }
//
//    public void setProduct_name(String product_name) {
//        this.product_name = product_name;
//    }

    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
    }

    public ArrayList<Product> getProductList() {
        return ProductList;
    }

    public void setProductList(ArrayList<Product> ProductList) {
        this.ProductList = ProductList;

        if (ProductList.size() > 0) {
            this.product_name = ProductList.get(0).getProd_name();
        }
        if (ProductList.size() > 1) {
            this.product_name += "외 "+String.valueOf(ProductList.size()-1)+"개";
        }
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getReceivable_price() {
        return receivable_price;
    }

    public void setReceivable_price(String receivable_price) {
        this.receivable_price = receivable_price;
    }

    public String getPay_price() {
        return pay_price;
    }

    public void setPay_price(String pay_price) {
        this.pay_price = pay_price;
    }

    public String getOrder_pay_type_code() {
        return order_pay_type_code;
    }

    public void setOrder_pay_type_code(String order_pay_type_code) {
        this.order_pay_type_code = order_pay_type_code;
    }

    public String getOrder_pay_type_name() {
        return order_pay_type_name;
    }

    public void setOrder_pay_type_name(String order_pay_type_name) {
        this.order_pay_type_name = order_pay_type_name;
    }

    public String getDelivery_ymd() {
        return delivery_ymd;
    }

    public void setDelivery_ymd(String delivery_ymd) {
        this.delivery_ymd = delivery_ymd;
    }

    public String getReceive_order_ymd() {
        return receive_order_ymd;
    }

    public void setReceive_order_ymd(String receive_order_ymd) {
        this.receive_order_ymd = receive_order_ymd;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getTel_no() {
        return tel_no;
    }

    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }

    public String getFax_no() {
        return fax_no;
    }

    public void setFax_no(String fax_no) {
        this.fax_no = fax_no;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }
}
