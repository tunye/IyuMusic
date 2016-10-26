package com.iyuba.music.entity.doings;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2016/4/20.
 */
public class Circle {
    @SerializedName("id")
    private int id;
    @SerializedName("uid")
    private String uid;
    @SerializedName("body")
    private String body;
    @SerializedName("feedid")
    private int feedid;
    @SerializedName("title")
    private String title;
    @SerializedName("username")
    private String username;
    @SerializedName("idtype")
    private String idtype;
    @SerializedName("replynum")
    private int replynum;
    @SerializedName("dateline")
    private long dateline;
    @SerializedName("image")
    private String image;

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
}
