package com.androidaxe.getmypg.Module;

public class Offers {

    String image, id, type;

    public Offers(String image, String id, String type) {
        this.image = image;
        this.id = id;
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
