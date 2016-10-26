package com.iyuba.music.adapter.me;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.entity.friends.Fans;
import com.iyuba.music.entity.friends.Follows;
import com.iyuba.music.entity.friends.RecommendFriend;
import com.iyuba.music.entity.friends.SearchFriend;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2015/10/10.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
    private ArrayList<?> friends;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public FriendAdapter(Context context) {
        this.context = context;
        friends = new ArrayList<>();
    }

    public void setFriendList(ArrayList<?> friendList) {
        this.friends = friendList;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                itemClickListener.onItemClick(holder.itemView, pos);
            }
        });
        Object item = friends.get(position);
        if (item instanceof Fans) {
            Fans fan = (Fans) item;
            holder.userName.setText(fan.getUsername());
            holder.userState.setText(fan.getDoing());
            ImageUtil.loadAvatar(fan.getUid(), holder.userPhoto);
        } else if (item instanceof Follows) {
            Follows follow = (Follows) item;
            holder.userName.setText(follow.getUsername());
            holder.userState.setText(follow.getDoing());
            ImageUtil.loadAvatar(follow.getUid(), holder.userPhoto);
        } else if (item instanceof RecommendFriend) {
            RecommendFriend recommendFriend = (RecommendFriend) item;
            holder.userName.setText(recommendFriend.getUsername());
            holder.userState.setText(context.getString(R.string.friend_distance, formatDistance(recommendFriend.getDoing()))
                    + "\n" + context.getString(R.string.friend_lastlogin, DateFormat.showTime(context, new Date(Long.parseLong(recommendFriend.getLastLogin()) * 1000))));
            ImageUtil.loadAvatar(recommendFriend.getUid(), holder.userPhoto);
        } else if (item instanceof SearchFriend) {
            SearchFriend searchFriend = (SearchFriend) item;
            holder.userName.setText(searchFriend.getUsername());
            holder.userState.setText(searchFriend.getDoing());
            ImageUtil.loadAvatar(searchFriend.getUid(), holder.userPhoto);
        }
    }

    private String formatDistance(String distance) {
        double metres = Double.valueOf(distance);
        DecimalFormat df = new DecimalFormat("#.##");
        return String.valueOf(df.format(metres));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class MyViewHolder extends RecycleViewHolder {

        TextView userName;
        JustifyTextView userState;
        CircleImageView userPhoto;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.friend_username);
            userState = (JustifyTextView) view.findViewById(R.id.friend_state);
            userPhoto = (CircleImageView) view.findViewById(R.id.friend_photo);
        }
    }
}
