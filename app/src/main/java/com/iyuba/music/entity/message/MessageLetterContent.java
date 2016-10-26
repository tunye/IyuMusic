package com.iyuba.music.entity.message;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/12/15.
 */
public class MessageLetterContent {
    private String messageid;// 私信id，设置未读私信为已读需要的参数
    @SerializedName("pmid")
    private String messageContentid;// 私信内容id，当删除私信的时候需要的参数
    @SerializedName("authorid")
    private String authorid;// 若与url的id相同则为发送，否则为接收
    @SerializedName("dateline")
    private String date;// 1362915420
    @SerializedName("message")
    private String content;
    private int direction;

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getMessageContentid() {
        return messageContentid;
    }

    public void setMessageContentid(String messageContentid) {
        this.messageContentid = messageContentid;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
