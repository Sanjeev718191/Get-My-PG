package com.androidaxe.getmypg.Module;

public class OwnerPG {

    String name, description, locality, city, state, pin, seater1, seater2, seater3, image, electricityBill, totalUsers, paidUsers, revenue;

    public OwnerPG() {
    }

    public OwnerPG(String name, String description, String locality, String city, String state, String pin, String seater1, String seater2, String seater3, String image, String electricityBill, String totalUsers, String paidUsers, String revenue) {
        this.name = name;
        this.description = description;
        this.locality = locality;
        this.city = city;
        this.state = state;
        this.pin = pin;
        this.seater1 = seater1;
        this.seater2 = seater2;
        this.seater3 = seater3;
        this.image = image;
        this.electricityBill = electricityBill;
        this.totalUsers = totalUsers;
        this.paidUsers = paidUsers;
        this.revenue = revenue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSeater1() {
        return seater1;
    }

    public void setSeater1(String seater1) {
        this.seater1 = seater1;
    }

    public String getSeater2() {
        return seater2;
    }

    public void setSeater2(String seater2) {
        this.seater2 = seater2;
    }

    public String getSeater3() {
        return seater3;
    }

    public void setSeater3(String seater3) {
        this.seater3 = seater3;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getElectricityBill() {
        return electricityBill;
    }

    public void setElectricityBill(String electricityBill) {
        this.electricityBill = electricityBill;
    }

    public String getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(String totalUsers) {
        this.totalUsers = totalUsers;
    }

    public String getPaidUsers() {
        return paidUsers;
    }

    public void setPaidUsers(String paidUsers) {
        this.paidUsers = paidUsers;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }
}
