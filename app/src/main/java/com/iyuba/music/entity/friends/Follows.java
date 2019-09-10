package com.iyuba.music.entity.friends;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2015/12/15.
 */
public class Follows extends Friend {
    @JSONField(name = "dateline")
    private String dateline;// 添加关注的时间，系统秒数
    @JSONField(name = "mutual")
    private String mutual;// 是否互相关注，1为是

    @JSONField(name = "followuid")
    @Override
    public String getUid() {
        return super.getUid();
    }

    @JSONField(name = "followuid")
    @Override
    public void setUid(String uid) {
        super.setUid(uid);
    }

    @JSONField(name = "fusername")
    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @JSONField(name = "fusername")
    @Override
    public void setUsername(String username) {
        super.setUsername(username);
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
}
