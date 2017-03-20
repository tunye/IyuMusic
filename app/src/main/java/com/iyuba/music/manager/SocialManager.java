package com.iyuba.music.manager;

import com.iyuba.music.entity.doings.Doing;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class SocialManager {
    private ArrayList<String> friendId;
    private ArrayList<String> friendName;
    private ArrayList<Doing> doing;

    private SocialManager() {
        friendId = new ArrayList<>();
        friendName = new ArrayList<>();
        doing = new ArrayList<>();
    }

    public static SocialManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public String getFriendId() {
        return friendId.get(friendId.size() - 1);
    }

    public void pushFriendId(String friendId) {
        this.friendId.add(friendId);
    }

    public void popFriendId() {
        this.friendId.remove(this.friendId.size() - 1);
    }

    public String getFriendName() {
        return friendName.get(friendName.size() - 1);
    }

    public void pushFriendName(String friendName) {
        this.friendName.add(friendName);
    }

    public void popFriendName() {
        this.friendName.remove(this.friendName.size() - 1);
    }

    public Doing getDoing() {
        return doing.get(doing.size() - 1);
    }

    public void pushDoing(Doing doing) {
        this.doing.add(doing);
    }

    public void popDoing() {
        this.doing.remove(this.doing.size() - 1);
    }

    private static class SingleInstanceHelper {
        private static SocialManager instance = new SocialManager();
    }
}
