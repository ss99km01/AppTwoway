package com.jefeko.apptwoway.models;

/**
 * Created by ss99km01 on 2018-03-06.
 */

public class ProductCategory {
    /**
     * 상품 카테고리 정보
     * */
    private String tkey_id;		    //터치키 아이디
    private String category_order;	//상품분류 순서
    private String category_name;	//상품분류 명

    public String getTkey_id() {
        return tkey_id;
    }

    public void setTkey_id(String tkey_id) {
        this.tkey_id = tkey_id;
    }

    public String getCategory_order() {
        return category_order;
    }

    public void setCategory_order(String category_order) {
        this.category_order = category_order;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
