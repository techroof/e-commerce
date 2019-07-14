package com.ecommerceapp;

public class orders {
    public orders() {
    }

    public orders(String address, String city, String date, String time, String name, String phone, String status, String totalprice, String accountphnum) {
        this.address = address;
        this.city = city;
        this.date = date;
        this.time = time;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.totalprice = totalprice;
        this.accountphnum = accountphnum;
    }

    private String address;
    private String city;
    private String date;
    private String time;
    private String name;
    private String phone;
    private String status;
    private String totalprice;
    private String accountphnum;

    public String getAccountphnum() {
        return accountphnum;
    }

    public void setAccountphnum(String accountphnum) {
        this.accountphnum = accountphnum;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

}
