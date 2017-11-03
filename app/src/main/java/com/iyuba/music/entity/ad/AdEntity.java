package com.iyuba.music.entity.ad;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/11/16.
 */
public class AdEntity {
    @SerializedName("startuppic")
    private String picUrl;
    @SerializedName("startuppic_Url")
    private String loadUrl;
    @SerializedName("type")
    private String type;

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
}
