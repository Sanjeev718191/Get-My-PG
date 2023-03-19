package com.androidaxe.getmypg.Module;

public class Request {
    String uid, oid, pqid, requestId, userName, type, roomType, status, date;

    public Request(String uid, String oid, String pqid, String requestId, String userName, String type, String roomType, String status, String date) {
        this.uid = uid;
        this.oid = oid;
        this.pqid = pqid;
        this.requestId = requestId;
        this.userName = userName;
        this.type = type;
        this.roomType = roomType;
        this.status = status;
        this.date = date;
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

    public String getPqid() {
        return pqid;
    }

    public void setPqid(String pqid) {
        this.pqid = pqid;
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
}
