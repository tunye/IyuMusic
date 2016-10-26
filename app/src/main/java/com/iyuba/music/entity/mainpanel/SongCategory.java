package com.iyuba.music.entity.mainpanel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2016/5/16.
 */
public class SongCategory {
    @SerializedName("Id")
    private int id;
    @SerializedName("Name")
    private String text;
    @SerializedName("Pic")
    private String imgUrl;
    @SerializedName("SongCount")
    private String count;
    @SerializedName("Father")
    private int father;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getFather() {
        return father;
    }

    public void setFather(int father) {
        this.father = father;
    }
}
