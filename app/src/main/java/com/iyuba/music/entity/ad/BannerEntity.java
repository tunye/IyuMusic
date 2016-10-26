package com.iyuba.music.entity.ad;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2016/3/9.
 */
public class BannerEntity {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("pic")
    private String picUrl;
    @SerializedName("desc1")
    private String desc;
    @SerializedName("ownerid")
    private String ownerid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    @Override
    public String toString() {
        return "BannerEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", ownerid='" + ownerid + '\'' +
                '}';
    }
}
