package com.iyuba.music.entity.doings;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/12/15.
 */
public class DoingComment {
    @SerializedName("message")
    private String message;
    @SerializedName("uid")
    private String uid;// 回复人id
    @SerializedName("id")
    private String id;// 回复内容id标识
    @SerializedName("username")
    private String username;// 回复人
    @SerializedName("upid")
    private String upid;// 上一层回复标识(回复的id)
    @SerializedName("grade")
    private String grade;// 楼层
    @SerializedName("dateline")
    private String dateline;// 回复发布时间，系统秒数
    @SerializedName("ip")
    private String ip;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUpid() {
        return upid;
    }

    public void setUpid(String upid) {
        this.upid = upid;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
