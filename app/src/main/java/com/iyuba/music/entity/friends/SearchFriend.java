package com.iyuba.music.entity.friends;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/12/15.
 */
public class SearchFriend {
    @SerializedName("uid")
    private String uid;// 我关注的uid
    @SerializedName("username")
    private String username;// 我关注的用户名
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

    public String getDoing() {
        return doing;
    }

    public void setDoing(String doing) {
        this.doing = doing;
    }
}
