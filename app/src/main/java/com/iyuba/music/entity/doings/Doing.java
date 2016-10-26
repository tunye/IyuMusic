package com.iyuba.music.entity.doings;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/12/15.
 */
public class Doing {
    @SerializedName("doid")
    private String doid;// 心情状态id
    @SerializedName("dateline")
    private String dateline;// 发表时间，为系统秒数
    @SerializedName("message")
    private String message;// (内层) 心情内容
    @SerializedName("ip")
    private String ip;// 发布时的ip
    @SerializedName("replynum")
    private String replynum;// 回复数
    private String username;
    private String uid;

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getDoid() {
        return doid;
    }

    public void setDoid(String doid) {
        this.doid = doid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReplynum() {
        return replynum;
    }

    public void setReplynum(String replynum) {
        this.replynum = replynum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
