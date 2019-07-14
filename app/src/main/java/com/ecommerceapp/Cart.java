package com.ecommerceapp;

public class Cart {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    private String name;
    private String id;
    private String price;
    private String image;
    private String quantity;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    private String total;
    public Cart() {
    }


    public Cart(String name, String id, String price, String image, String quantity,String total) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
        this.total=total;
    }


}


