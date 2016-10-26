package com.iyuba.music.entity.ad;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/11/16.
 */
public class AdEntity {
    @SerializedName("startuppic_StartDate")
    private String startDate;
    @SerializedName("startuppic_EndDate")
    private String endDate;
    @SerializedName("startuppic")
    private String picUrl;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public String toString() {
        return "AdEntity{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", picUrl='" + picUrl + '\'' +
                '}';
    }
}
