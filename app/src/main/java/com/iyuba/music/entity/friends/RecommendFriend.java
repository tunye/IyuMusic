package com.iyuba.music.entity.friends;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2015/12/15.
 */
public class RecommendFriend extends Friend {
    @JSONField(name = "gender")
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}