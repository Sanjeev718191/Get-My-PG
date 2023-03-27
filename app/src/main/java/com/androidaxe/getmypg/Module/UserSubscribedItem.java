package com.androidaxe.getmypg.Module;

public class UserSubscribedItem {
    String PGMessId, type, fromDate, toDate, uid, oid, roomType, roomNumber, note, price, Notice, currentlyActive;

    public UserSubscribedItem() {
    }

    public UserSubscribedItem(String PGMessId, String type, String fromDate, String toDate, String uid, String oid, String roomType, String roomNumber, String note, String price, String notice, String currentlyActive) {
        this.PGMessId = PGMessId;
        this.type = type;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.uid = uid;
        this.oid = oid;
        this.roomType = roomType;
        this.roomNumber = roomNumber;
        this.note = note;
        this.price = price;
        Notice = notice;
        this.currentlyActive = currentlyActive;
    }

    public String getPGMessId() {
        return PGMessId;
    }

    public void setPGMessId(String PGMessId) {
        this.PGMessId = PGMessId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
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

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNotice() {
        return Notice;
    }

    public void setNotice(String notice) {
        Notice = notice;
    }

    public String getCurrentlyActive() {
        return currentlyActive;
    }

    public void setCurrentlyActive(String currentlyActive) {
        this.currentlyActive = currentlyActive;
    }
}
