package com.androidaxe.getmypg.Module;

public class OwnerMess {
    String name, description, locality, city, state, pin, feeMonthly, image, menu, totalUsers, paidUsers, revenue, search, id, oid, contact, deleted, stopRequests, deactivated, menuPDF;

    public OwnerMess() {
    }

    public OwnerMess(String name, String description, String locality, String city, String state, String pin, String feeMonthly, String image, String menu, String totalUsers, String paidUsers, String revenue, String search, String id, String oid, String contact, String deleted, String stopRequests, String deactivated, String menuPDF) {
        this.name = name;
        this.description = description;
        this.locality = locality;
        this.city = city;
        this.state = state;
        this.pin = pin;
        this.feeMonthly = feeMonthly;
        this.image = image;
        this.menu = menu;
        this.totalUsers = totalUsers;
        this.paidUsers = paidUsers;
        this.revenue = revenue;
        this.search = search;
        this.id = id;
        this.oid = oid;
        this.contact = contact;
        this.deleted = deleted;
        this.stopRequests = stopRequests;
        this.deactivated = deactivated;
        this.menuPDF = menuPDF;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getStopRequests() {
        return stopRequests;
    }

    public void setStopRequests(String stopRequests) {
        this.stopRequests = stopRequests;
    }

    public String getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(String deactivated) {
        this.deactivated = deactivated;
    }

    public String getMenuPDF() {
        return menuPDF;
    }

    public void setMenuPDF(String menuPDF) {
        this.menuPDF = menuPDF;
    }
}
