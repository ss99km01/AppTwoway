package com.jefeko.apptwoway.models;

/**
 * 상품 정보
 * */
public class Product {
    private String company_id;		//회사 아이디
    private String prod_id;			//상품 아이디
    private String prod_name;		//상품 명
    private String chain_buy_yn;	//가맹점 판매상품 여부
    private String chain_sell_yn;	//가맹점 소비자상품 여부
    private String standard;		//규격
    private String unit;			//단위
    private String min_order;		//최소주문
    private String img_id;			//이미지 아이디
    private String barcode;			//바코드
    private String price_type_code;	//단가 구분 코드
    private String price_type_name;	//단가 구분 명
    private String tax_yn;			//과세 여부
    private String vat_yn;			//VAT 포함 여부
    private String sell_price;		//판매가
    private String order_id;        // 주문 아이디
    private int amount = 0;         // 수량
    private int price = 0;          // 단가
    private int order_price = 0;    // 주문 금액


    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getChain_buy_yn() {
        return chain_buy_yn;
    }

    public void setChain_buy_yn(String chain_buy_yn) {
        this.chain_buy_yn = chain_buy_yn;
    }

    public String getChain_sell_yn() {
        return chain_sell_yn;
    }

    public void setChain_sell_yn(String chain_sell_yn) {
        this.chain_sell_yn = chain_sell_yn;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMin_order() {
        return min_order;
    }

    public void setMin_order(String min_order) {
        this.min_order = min_order;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPrice_type_code() {
        return price_type_code;
    }

    public void setPrice_type_code(String price_type_code) {
        this.price_type_code = price_type_code;
    }

    public String getPrice_type_name() {
        return price_type_name;
    }

    public void setPrice_type_name(String price_type_name) {
        this.price_type_name = price_type_name;
    }

    public String getTax_yn() {
        return tax_yn;
    }

    public void setTax_yn(String tax_yn) {
        this.tax_yn = tax_yn;
    }

    public String getVat_yn() {
        return vat_yn;
    }

    public void setVat_yn(String vat_yn) {
        this.vat_yn = vat_yn;
    }

    public String getSell_price() {
        return sell_price;
    }

    public void setSell_price(String sell_price) {
        this.sell_price = sell_price;
        this.price = Integer.valueOf(sell_price);
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setCalAmount(int amount) {
        this.amount = amount;
        setOrder_price();
    }

    public void setAmount(String amount) {
        this.amount = Integer.valueOf(amount);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOrder_price() {
        return order_price;
    }

//    public void setOrder_price(int order_price) {
//        this.order_price = order_price;
//    }

    public void setOrder_price(String order_price) {
        this.order_price = Integer.valueOf(order_price);
    }

    public void setOrder_price() {
        this.order_price = this.amount * this.price;
    }

    public void setAmountIncrease() {
        this.amount++;
        setOrder_price();
    }

    public void setAmountDecrease() {
        if ( this.amount > 1 ) {
            this.amount--;
            setOrder_price();
        }
    }


}
