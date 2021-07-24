package com.iyuba.music.entity.mainpanel;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2015/10/10.
 */
public class Announcer implements Parcelable {
    public static final Parcelable.Creator<Announcer> CREATOR = new Parcelable.Creator<Announcer>() {
        @Override
        public Announcer createFromParcel(Parcel source) {
            return new Announcer(source);
        }

        @Override
        public Announcer[] newArray(int size) {
            return new Announcer[size];
        }
    };
    @JSONField(name = "Name")
    public String name;
    @JSONField(name = "Img")
    public String imgUrl;
    @JSONField(name = "Uid")
    public String uid;
    @JSONField(name = "StarId")
    private int id;

    public Announcer() {
    }

    protected Announcer(Parcel in) {
        this.name = in.readString();
        this.imgUrl = in.readString();
        this.uid = in.readString();
        this.id = in.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.imgUrl);
        dest.writeString(this.uid);
        dest.writeInt(this.id);
    }
}
