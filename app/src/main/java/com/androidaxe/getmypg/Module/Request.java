package com.androidaxe.getmypg.Module;

public class Request {
    String uid, oid, pgid, requestId, userName, type, roomType, status, date, businessName, userContact, ownerContact, userImage, businessImage, price;

    public Request() {
    }

    public Request(String uid, String oid, String pgid, String requestId, String userName, String type, String roomType, String status, String date, String businessName, String userContact, String ownerContact, String userImage, String businessImage, String price) {
        this.uid = uid;
        this.oid = oid;
        this.pgid = pgid;
        this.requestId = requestId;
        this.userName = userName;
        this.type = type;
        this.roomType = roomType;
        this.status = status;
        this.date = date;
        this.businessName = businessName;
        this.userContact = userContact;
        this.ownerContact = ownerContact;
        this.userImage = userImage;
        this.businessImage = businessImage;
        this.price = price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getPgid() {
        return pgid;
    }

    public void setPgid(String pqid) {
        this.pgid = pqid;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getBusinessImage() {
        return businessImage;
    }

    public void setBusinessImage(String businessImage) {
        this.businessImage = businessImage;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
