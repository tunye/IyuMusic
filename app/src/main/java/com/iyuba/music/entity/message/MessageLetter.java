package com.iyuba.music.entity.message;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/12/15.
 */
public class MessageLetter {
    @SerializedName("friendid")
    private String friendid; // 对方id
    @SerializedName("pmnum")
    private int contentCount;// 当前互发私信数
    @SerializedName("lastmessage")
    private String lastmessage;// 最后一条私信内容
    @SerializedName("name")
    private String friendName;// 对方name
    @SerializedName("plid")
    private String messageid; // 私信id，设置未读私信为已读需要的参数
    @SerializedName("dateline")
    private String date;// 最后一条私信发送时间
    @SerializedName("isnew")
    private String isnew; // 1代表未读 0代表已读
    @SerializedName("vip")
    private int vip;

    public String getFriendid() {
        return friendid;
    }

    public void setFriendid(String friendid) {
        this.friendid = friendid;
    }

    public int getContentCount() {
        return contentCount;
    }

    public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsnew() {
        return isnew;
    }

    public void setIsnew(String isnew) {
        this.isnew = isnew;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }
}
