package com.iyuba.music.entity.ad;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2015/11/16.
 */
public class AdEntity {
    @JSONField(name = "startuppic")
    private String picUrl;
    @JSONField(name = "startuppic_Url")
    private String loadUrl;
    @JSONField(name = "type")
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
