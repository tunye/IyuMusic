package com.iyuba.music.entity.artical;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 10202 on 2015/10/10.
 */
public class Article implements Serializable {
    @SerializedName("Singer")
    public String singer;
    @SerializedName("Announcer")
    public String broadcaster;
    @SerializedName("ReadCount")
    public String readCount;
    @SerializedName("SongId")
    private int id;
    @SerializedName("Title")
    private String title;
    private String title_cn;
    @SerializedName("DescCn")
    private String content;
    @SerializedName("PublishTime")
    private String time;
    @SerializedName("Pic")
    private String picUrl;
    @SerializedName("Songmp3")
    private String musicUrl;
    @SerializedName("Sound")
    private String soundUrl;
    @SerializedName("Style")
    private String category;
    private String app;
    @SerializedName("Star")
    private String star;
    @SerializedName("titleFind")
    private String titleFind;
    @SerializedName("textFind")
    private String textFind;
    private boolean delete;
    @SerializedName("Simpleflg")
    private int simple;

    private String expireContent;

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getReadCount() {
        return readCount;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getTitle_cn() {
        return title_cn;
    }

    public void setTitle_cn(String title_cn) {
        this.title_cn = title_cn;
    }

    public String getTitleFind() {
        return titleFind;
    }

    public void setTitleFind(String titleFind) {
        this.titleFind = titleFind;
    }

    public String getTextFind() {
        return textFind;
    }

    public void setTextFind(String textFind) {
        this.textFind = textFind;
    }

    public String getExpireContent() {
        return expireContent;
    }

    public void setExpireContent(String expireContent) {
        this.expireContent = expireContent;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public int getSimple() {
        return simple;
    }

    public void setSimple(int simple) {
        this.simple = simple;
    }
}
