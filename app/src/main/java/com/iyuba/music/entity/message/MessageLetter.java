package com.iyuba.music.entity.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2015/12/15.
 */
public class MessageLetter {
    @JSONField(name = "friendid")
    private String friendid; // 对方id
    @JSONField(name = "pmnum")
    private int contentCount;// 当前互发私信数
    @JSONField(name = "lastmessage")
    private String lastmessage;// 最后一条私信内容
    @JSONField(name = "name")
    private String friendName;// 对方name
    @JSONField(name = "plid")
    private String messageid; // 私信id，设置未读私信为已读需要的参数
    @JSONField(name = "dateline")
    private String date;// 最后一条私信发送时间
    @JSONField(name = "isnew")
    private String isnew; // 1代表未读 0代表已读
    @JSONField(name = "vip")
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
