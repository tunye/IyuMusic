package com.iyuba.music.entity.user;

import android.content.Context;
import android.text.TextUtils;

import com.iyuba.music.R;
import com.iyuba.music.util.Mathematics;

/**
 * Created by 10202 on 2015/11/18.
 */
public class UserInfo {
    private String icoins;//积分
    private String uid;
    private String username;// 用户名
    private String doings;// 发布的心情数
    private String views;// 访客数
    private String gender;// 性别
    private String text;// 最近的心情签名
    private String follower;// 粉丝
    private String relation;// 与当前用户关系 百位我是否关注他十位特别关注 个位他是否关注我
    private String following;// 关注
    private String iyubi;
    private String vipStatus;
    private String distance;
    private String notification;
    private int studytime;
    private String position;
    private String deadline;
    private String userEmail;

    public static int getLevel(int score) {
        int[] scoreLevel = {0, 50, 200, 500, 1500, 3000, 6000, 12000, 30000, 80000};
        for (int i = 0; i < scoreLevel.length; i++) {
            if (i == scoreLevel.length - 1) {
                return scoreLevel.length;
            } else if (Mathematics.range(scoreLevel[i], scoreLevel[i + 1], score)) {
                return i + 1;
            }
        }
        return 1;
    }

    public static String getLevelName(Context context, int score) {
        String[] name = context.getResources()
                .getStringArray(R.array.level);
        return name[getLevel(score) - 1];
    }

    public String getIcoins() {
        return icoins;
    }

    public void setIcoins(String icoins) {
        this.icoins = icoins;
    }

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

    public String getDoings() {
        return doings;
    }

    public void setDoings(String doings) {
        this.doings = doings;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getIyubi() {
        return iyubi;
    }

    public void setIyubi(String iyubi) {
        this.iyubi = iyubi;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(String vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public int getStudytime() {
        return studytime;
    }

    public void setStudytime(int studytime) {
        this.studytime = studytime;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "icoins='" + icoins + '\'' +
                ", uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", doings='" + doings + '\'' +
                ", views='" + views + '\'' +
                ", gender='" + gender + '\'' +
                ", text='" + text + '\'' +
                ", follower='" + follower + '\'' +
                ", relation='" + relation + '\'' +
                ", following='" + following + '\'' +
                ", iyubi='" + iyubi + '\'' +
                ", vipStatus='" + vipStatus + '\'' +
                ", distance='" + distance + '\'' +
                ", notification='" + notification + '\'' +
                ", studytime=" + studytime +
                ", position='" + position + '\'' +
                ", deadline='" + deadline + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }

    public void update(UserInfo update) {
        if (!TextUtils.isEmpty(update.icoins)) {
            this.icoins = update.icoins;
        }
        if (!TextUtils.isEmpty(update.uid)) {
            this.uid = update.uid;
        }
        if (!TextUtils.isEmpty(update.username)) {
            this.username = update.username;
        }
        if (!TextUtils.isEmpty(update.doings)) {
            this.doings = update.doings;
        }
        if (!TextUtils.isEmpty(update.views)) {
            this.views = update.views;
        }
        if (!TextUtils.isEmpty(update.gender)) {
            this.gender = update.gender;
        }
        if (!TextUtils.isEmpty(update.text)) {
            this.text = update.text;
        }
        if (!TextUtils.isEmpty(update.follower)) {
            this.follower = update.follower;
        }
        if (!TextUtils.isEmpty(update.following)) {
            this.following = update.following;
        }
        if (!TextUtils.isEmpty(update.relation)) {
            this.relation = update.relation;
        }
        if (!TextUtils.isEmpty(update.iyubi)) {
            this.iyubi = update.iyubi;
        }
        if (!TextUtils.isEmpty(update.vipStatus)) {
            this.vipStatus = update.vipStatus;
        }
        if (!TextUtils.isEmpty(update.distance)) {
            this.distance = update.distance;
        }
        if (!TextUtils.isEmpty(update.notification)) {
            this.notification = update.notification;
        }
        if (!TextUtils.isEmpty(update.position)) {
            this.position = update.position;
        }
        if (!TextUtils.isEmpty(update.deadline)) {
            this.deadline = update.deadline;
        }
        if (!TextUtils.isEmpty(update.userEmail)) {
            this.userEmail = update.userEmail;
        }
    }
}
