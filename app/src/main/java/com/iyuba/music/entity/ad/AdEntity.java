package com.iyuba.music.entity.ad;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created by 10202 on 2015/11/16.
 */
public class AdEntity {
    public static final String TYPE_YOUDAO = "youdao";
    public static final String TYPE_WEB = "web";
    @JSONField(name = "startuppic")
    private String picUrl;
    @JSONField(name = "startuppic_Url")
    private String loadUrl;
    @JSONField(name = "type")
    private String type;
    @JSONField(name = "startuppic_EndDate")
    private Date date;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public void setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean youDaoAd() {
        return TYPE_YOUDAO.equalsIgnoreCase(type);
    }

    public boolean webExpire() {
        return TYPE_WEB.equalsIgnoreCase(type) && new Date().getTime() < date.getTime();
    }
}
