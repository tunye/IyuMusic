package com.iyuba.music.entity.comment;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2015/12/15.
 */
public class Comment {
    @JSONField(name = "id")
    private int id;
    @JSONField(name = "Userid")
    private String userid;
    @JSONField(name = "UserName")
    private String userName;
    @JSONField(name = "agreeCount")
    private int agreeCount;
    @JSONField(name = "againstCount")
    private int againstCount;
    @JSONField(name = "ShuoShuo")
    private String shuoshuo;
    @JSONField(name = "ShuoShuoType")
    private int shuoshuoType;
    @JSONField(name = "CreateDate")
    private String createDate;
    @JSONField(name = "vip")
    private int vip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(int agreeCount) {
        this.agreeCount = agreeCount;
    }

    public int getAgainstCount() {
        return againstCount;
    }

    public void setAgainstCount(int againstCount) {
        this.againstCount = againstCount;
    }

    public String getShuoshuo() {
        return shuoshuo;
    }

    public void setShuoshuo(String shuoshuo) {
        this.shuoshuo = shuoshuo;
    }

    public int getShuoshuoType() {
        return shuoshuoType;
    }

    public void setShuoshuoType(int shuoshuoType) {
        this.shuoshuoType = shuoshuoType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }
}
