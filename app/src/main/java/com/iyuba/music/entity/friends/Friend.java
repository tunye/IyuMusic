package com.iyuba.music.entity.friends;

import com.alibaba.fastjson.annotation.JSONField;

public class Friend {
    @JSONField(name = "uid")
    private String uid;// 我关注的uid
    @JSONField(name = "username")
    private String username;// 我关注的用户名
    @JSONField(name = "doing")
    private String doing;
    @JSONField(name = "vip")
    private int vip;

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

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }
}
