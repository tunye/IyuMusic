package com.iyuba.music.entity.doings;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2016/4/20.
 */
public class Circle {
    @JSONField(name = "id")
    private int id;
    @JSONField(name = "uid")
    private String uid;
    @JSONField(name = "body")
    private String body;
    @JSONField(name = "feedid")
    private int feedid;
    @JSONField(name = "title")
    private String title;
    @JSONField(name = "username")
    private String username;
    @JSONField(name = "idtype")
    private String idtype;
    @JSONField(name = "replynum")
    private int replynum;
    @JSONField(name = "dateline")
    private long dateline;
    @JSONField(name = "image")
    private String image;
    @JSONField(name = "vip")
    private int vip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getFeedid() {
        return feedid;
    }

    public void setFeedid(int feedid) {
        this.feedid = feedid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public int getReplynum() {
        return replynum;
    }

    public void setReplynum(int replynum) {
        this.replynum = replynum;
    }

    public long getDateline() {
        return dateline;
    }

    public void setDateline(long dateline) {
        this.dateline = dateline;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }
}
