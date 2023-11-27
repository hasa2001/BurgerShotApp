package com.zaviron.burgershotapp.model;

public class Cart {
    private String cart_id;
    private String client_id;
    private String product_id;
    private int selected_qty;

    private String product_title;
    private String product_price;

    public Cart() {
    }

    public Cart(String cart_id, String client_id, String product_id, int selected_qty, String product_title, String product_price) {
        this.cart_id = cart_id;
        this.client_id = client_id;
        this.product_id = product_id;
        this.selected_qty = selected_qty;
        this.product_title = product_title;
        this.product_price = product_price;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getSelected_qty() {
        return selected_qty;
    }

    public void setSelected_qty(int selected_qty) {
        this.selected_qty = selected_qty;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }
}
