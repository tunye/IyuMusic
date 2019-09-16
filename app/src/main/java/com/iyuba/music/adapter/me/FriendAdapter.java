package com.iyuba.music.adapter.me;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.iyuba.music.R;
import com.iyuba.music.entity.friends.Fans;
import com.iyuba.music.entity.friends.Follows;
import com.iyuba.music.entity.friends.Friend;
import com.iyuba.music.entity.friends.RecommendFriend;
import com.iyuba.music.entity.friends.SearchFriend;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.textview.JustifyTextView;

/**
 * Created by 10202 on 2015/10/10.
 */
public class FriendAdapter extends CoreRecyclerViewAdapter<Friend, FriendAdapter.MyViewHolder> {

    public FriendAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Friend item = getDatas().get(position);
        if (item instanceof Fans) {
            Fans fan = (Fans) item;
            holder.userName.setText(fan.getUsername());
            holder.userState.setText(getDefaultDoing(fan.getDoing()));
            holder.userPhoto.setVipStateVisible(fan.getUid(), fan.getVip() == 1);
        } else if (item instanceof Follows) {
            Follows follow = (Follows) item;
            holder.userName.setText(follow.getUsername());
            holder.userState.setText(getDefaultDoing(follow.getDoing()));
            holder.userPhoto.setVipStateVisible(follow.getUid(), follow.getVip() == 1);
        } else if (item instanceof RecommendFriend) {
            RecommendFriend recommendFriend = (RecommendFriend) item;
            holder.userName.setText(recommendFriend.getUsername());
            holder.userState.setText(getDefaultDoing(recommendFriend.getDoing()));
            holder.userPhoto.setVipStateVisible(recommendFriend.getUid(), recommendFriend.getVip() == 1);
        } else if (item instanceof SearchFriend) {
            SearchFriend searchFriend = (SearchFriend) item;
            holder.userName.setText(searchFriend.getUsername());
            holder.userState.setText(searchFriend.getDoing());
            holder.userPhoto.setVipStateVisible(searchFriend.getUid(), searchFriend.getVip() == 1);
        }
    }

    private String getDefaultDoing(String doing) {
        if (TextUtils.isEmpty(doing)) {
            return context.getString(R.string.personal_nosign);
        }
        return doing;
    }

    static class MyViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView userName;
        JustifyTextView userState;
        VipPhoto userPhoto;

        MyViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.friend_username);
            userState = view.findViewById(R.id.friend_state);
            userPhoto = view.findViewById(R.id.friend_photo);
        }
    }
}
