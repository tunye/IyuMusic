package com.iyuba.music.entity.doings;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2015/12/15.
 */
public class DoingComment {
    @JSONField(name = "message")
    private String message;
    @JSONField(name = "uid")
    private String uid;// 回复人id
    @JSONField(name = "id")
    private String id;// 回复内容id标识
    @JSONField(name = "username")
    private String username;// 回复人
    @JSONField(name = "upid")
    private String upid;// 上一层回复标识(回复的id)
    @JSONField(name = "grade")
    private String grade;// 楼层
    @JSONField(name = "dateline")
    private String dateline;// 回复发布时间，系统秒数
    @JSONField(name = "ip")
    private String ip;
    @JSONField(name = "vip")
    private int vip;

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

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }
}
