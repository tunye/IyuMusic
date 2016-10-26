package com.iyuba.music.entity.mainpanel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/10/10.
 */
public class Announcer {
    @SerializedName("Name")
    public String name;
    @SerializedName("Img")
    public String imgUrl;
    @SerializedName("Uid")
    public String uid;
    @SerializedName("StarId")
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
