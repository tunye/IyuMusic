package com.iyuba.music.entity.friends;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/12/15.
 */
public class Fans {
    @SerializedName("uid")
    private String uid;// 我关注的uid
    @SerializedName("username")
    private String username;// 我关注的用户名
    @SerializedName("dateline")
    private String dateline;// 添加关注的时间，系统秒数
    @SerializedName("mutual")
    private String mutual;// 是否互相关注，1为是
    @SerializedName("doing")
    private String doing;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getMutual() {
        return mutual;
    }

    public void setMutual(String mutual) {
        this.mutual = mutual;
    }


    public String getDoing() {
        return doing;
    }

    public void setDoing(String doing) {
        this.doing = doing;
    }

}
