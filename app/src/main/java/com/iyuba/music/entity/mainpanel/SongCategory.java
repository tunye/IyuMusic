package com.iyuba.music.entity.mainpanel;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2016/5/16.
 */
public class SongCategory {
    @JSONField(name = "Id")
    private int id;
    @JSONField(name = "Name")
    private String text;
    @JSONField(name = "Pic")
    private String imgUrl;
    @JSONField(name = "SongCount")
    private String count;
    @JSONField(name = "Father")
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
