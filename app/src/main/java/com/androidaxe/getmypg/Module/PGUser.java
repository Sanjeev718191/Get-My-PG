package com.androidaxe.getmypg.Module;

public class PGUser {
    private String uId, Name, profile, contact, userType, email;

    public PGUser() {
    }

    public PGUser(String uId, String name, String profile, String contact, String userType, String email) {
        this.uId = uId;
        Name = name;
        this.profile = profile;
        this.contact = contact;
        this.userType = userType;
        this.email = email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
