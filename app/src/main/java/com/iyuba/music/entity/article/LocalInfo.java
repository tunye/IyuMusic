package com.iyuba.music.entity.article;

/**
 * Created by 10202 on 2015/12/15.
 */
public class LocalInfo {
    private int id;
    private int favourite;
    private int download;
    private int times;
    private int synchro;
    private String app;
    private String downTime;
    private String favTime;
    private String seeTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getSynchro() {
        return synchro;
    }

    public void setSynchro(int synchro) {
        this.synchro = synchro;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getDownTime() {
        return downTime;
    }

    public void setDownTime(String downTime) {
        this.downTime = downTime;
    }

    public String getFavTime() {
        return favTime;
    }

    public void setFavTime(String favTime) {
        this.favTime = favTime;
    }

    public String getSeeTime() {
        return seeTime;
    }

    public void setSeeTime(String seeTime) {
        this.seeTime = seeTime;
    }
}
