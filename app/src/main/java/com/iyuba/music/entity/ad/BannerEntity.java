package com.iyuba.music.entity.ad;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2016/3/9.
 */
public class BannerEntity {
    public static final String OWNER_WEB = "0";
    public static final String OWNER_ARTICLE = "1";
    public static final String OWNER_EMPTY = "2";
    @JSONField(name = "id")
    private String id;
    @JSONField(name = "name")
    private String name;
    @JSONField(name = "pic")
    private String picUrl;
    @JSONField(name = "desc1")
    private String desc;
    @JSONField(name = "ownerid")
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
