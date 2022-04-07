package com.example.aves.Domain;

public class OrderDomain {
    private int order_id, user_id, items_count;
    private Double total_price;
    private String order_status, order_location, from_location;

    public OrderDomain(int order_id, int user_id, int items_count, Double total_price, String order_status, String order_location, String from_location) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.items_count = items_count;
        this.total_price = total_price;
        this.order_status = order_status;
        this.order_location = order_location;
        this.from_location = from_location;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getItems_count() {
        return items_count;
    }

    public void setItems_count(int items_count) {
        this.items_count = items_count;
    }

    public Double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Double total_price) {
        this.total_price = total_price;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_location() {
        return order_location;
    }

    public void setOrder_location(String order_location) {
        this.order_location = order_location;
    }

    public String getFrom_location() {
        return from_location;
    }

    public void setFrom_location(String from_location) {
        this.from_location = from_location;
    }
}
