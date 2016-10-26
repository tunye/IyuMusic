package com.iyuba.music.entity.comment;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/12/15.
 */
public class Comment {
    @SerializedName("id")
    private int id;
    @SerializedName("Userid")
    private String userid;
    @SerializedName("UserName")
    private String userName;
    @SerializedName("agreeCount")
    private int agreeCount;
    @SerializedName("againstCount")
    private int againstCount;
    @SerializedName("ShuoShuo")
    private String shuoshuo;
    @SerializedName("ShuoShuoType")
    private int shuoshuoType;
    @SerializedName("CreateDate")
    private String createDate;

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
}
