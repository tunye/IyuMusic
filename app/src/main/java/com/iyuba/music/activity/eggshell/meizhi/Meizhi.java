package com.iyuba.music.activity.eggshell.meizhi;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2016/4/7.
 */
public class Meizhi {

    @JSONField(name = "_id")
    private String id;
    @JSONField(name = "who")
    private String author;
    @JSONField(name = "publishedAt")
    private String createDate;
    @JSONField(name = "desc")
    private String desc;
    @JSONField(name = "url")
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
