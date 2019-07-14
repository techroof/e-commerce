package com.ecommerceapp;

public class Products {
    private String pid,category,name,price,time,date,description,image;

    public Products(String pid, String category, String name, String price, String time, String date, String description, String image) {
        this.pid = pid;
        this.category = category;
        this.name = name;
        this.price = price;
        this.time = time;
        this.date = date;
        this.description = description;
        this.image = image;
    }

    public Products() {
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}