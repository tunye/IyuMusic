package com.iyuba.music.adapter.me;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.message.MessageLetterContent;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChattingAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MessageLetterContent> mList;
    private String uid;

    public ChattingAdapter(Context context, String uid) {
        this.context = context;
        this.uid = uid;
        mList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public MessageLetterContent getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setList(ArrayList<MessageLetterContent> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final MessageLetterContent message = mList.get(position);
        if (message.getAuthorid().equals(uid)) {
            message.setDirection(1);
        } else {
            message.setDirection(0);
        }
        if (convertView == null || !((holder = (ViewHolder) convertView.getTag()).flag == message.getDirection())) {
            holder = new ViewHolder();
            if (message.getDirection() == 0) {
                holder.flag = 0;
                convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from, null);
            } else {
                holder.flag = 1;
                convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to, null);
            }
            holder.text = (TextView) convertView.findViewById(R.id.chatting_content);
            holder.time = (TextView) convertView.findViewById(R.id.chatting_time);
            holder.userImageView = (CircleImageView) convertView.findViewById(R.id.chatting_photo);
            holder.timeLayout = convertView.findViewById(R.id.chatting_time_layout);
            convertView.setTag(holder);
        }
        holder.text.setText(message.getContent());
        String contentShowTime;
        if (position + 1 == getCount()) {
            if (getCount() == 1) {
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm", Locale.CHINA);
                contentShowTime = hour.format(new Date(Long.parseLong(message.getDate()) * 1000));
            } else {
                Date messageDate = new Date(Long.parseLong(message.getDate()) * 1000);
                Date lastMessageDate = new Date(Long.parseLong(getItem(position - 1).getDate()) * 1000);
                contentShowTime = getShowTimeString(messageDate, lastMessageDate);
            }
        } else if (position == 0) {
            contentShowTime = DateFormat.contentShowTime(new Date(Long.parseLong(message.getDate()) * 1000), new Date());
            if (TextUtils.isEmpty(contentShowTime)) {
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm", Locale.CHINA);
                contentShowTime = hour.format(new Date(Long.parseLong(message.getDate()) * 1000));
            }
        } else {
            Date messageDate = new Date(Long.parseLong(message.getDate()) * 1000);
            Date lastMessageDate = new Date(Long.parseLong(getItem(position - 1).getDate()) * 1000);
            contentShowTime = getShowTimeString(messageDate, lastMessageDate);
        }
        if (TextUtils.isEmpty(contentShowTime)) {
            holder.timeLayout.setVisibility(View.GONE);
        } else {
            holder.timeLayout.setVisibility(View.VISIBLE);
            holder.time.setText(contentShowTime);
        }
        ImageUtil.loadAvatar(message.getAuthorid(), holder.userImageView);
        holder.userImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SocialManager.instance.pushFriendId(message.getAuthorid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private String getShowTimeString(Date messageDate, Date lastMessageDate) {
        String contentShowTime;
        if (messageDate.getTime() - lastMessageDate.getTime() > System.currentTimeMillis() - messageDate.getTime()) {
            contentShowTime = DateFormat.contentShowTime(messageDate, new Date());
            if (TextUtils.isEmpty(contentShowTime) && messageDate.getTime() - lastMessageDate.getTime() > 300000) {
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm", Locale.CHINA);
                contentShowTime = hour.format(messageDate);
            }
        } else {
            contentShowTime = DateFormat.contentShowTime(messageDate, lastMessageDate);
        }
        return contentShowTime;
    }

    static class ViewHolder {
        TextView text;
        CircleImageView userImageView;
        TextView time;
        View timeLayout;
        int flag;
    }
}
