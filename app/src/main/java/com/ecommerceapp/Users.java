package com.ecommerceapp;

public class Users {
    private String name;
    private String phone;
    private String password;
    private String address;
    private String image;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public Users(String name, String phone, String password, String address, String image) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.image = image;
    }




    public Users() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
