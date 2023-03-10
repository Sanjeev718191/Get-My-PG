package com.androidaxe.getmypg.Module;

public class OwnerMess {
    String name, description, locality, city, state, pin, feeMonthly, image, totalUsers, paidUsers, revenue;

    public OwnerMess() {
    }

    public OwnerMess(String name, String description, String locality, String city, String state, String pin, String feeMonthly, String image, String totalUsers, String paidUsers, String revenue) {
        this.name = name;
        this.description = description;
        this.locality = locality;
        this.city = city;
        this.state = state;
        this.pin = pin;
        this.feeMonthly = feeMonthly;
        this.image = image;
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

    public String getFeeMonthly() {
        return feeMonthly;
    }

    public void setFeeMonthly(String feeMonthly) {
        this.feeMonthly = feeMonthly;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
