package com.iyuba.music.adapter.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.message.MessageLetter;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.Date;

/**
 * Created by 10202 on 2015/10/10.
 */
public class MessageAdapter extends CoreRecyclerViewAdapter<MessageLetter, MessageAdapter.MyViewHolder> {

    public MessageAdapter(Context context) {
        super(context);
    }

    public void setReaded(int position) {
        getDatas().get(position).setIsnew("0");
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final MessageLetter messageLetter = getDatas().get(position);
        holder.messageUserName.setText(messageLetter.getFriendName());
        holder.messageCounts.setText("(" + messageLetter.getContentCount() + ")");
        holder.messageLastContent.setText(messageLetter.getLastmessage());
        holder.messageTime.setText(DateFormat.showTime(context, new Date(Long.parseLong(messageLetter.getDate()) * 1000)));
        AppImageUtil.loadAvatar(messageLetter.getFriendid(), holder.messagePhoto);
        holder.messagePhoto.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                SocialManager.getInstance().pushFriendId(messageLetter.getFriendid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                context.startActivity(intent);
            }
        });
        if (messageLetter.getIsnew().equals("1")) {// 未读
            holder.isNew.setVisibility(View.VISIBLE);
        } else {// 已读
            holder.isNew.setVisibility(View.GONE);
        }
        if (messageLetter.getVip() == 1) {
            holder.vipState.setVisibility(View.VISIBLE);
        } else {
            holder.vipState.setVisibility(View.GONE);
        }
    }

    static class MyViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView messageUserName, messageTime, messageCounts;
        JustifyTextView messageLastContent;
        ImageView messagePhoto;
        ImageView isNew, vipState;

        MyViewHolder(View view) {
            super(view);
            messageUserName = view.findViewById(R.id.message_username);
            messageCounts = view.findViewById(R.id.message_pmnum);
            messageLastContent = view.findViewById(R.id.message_lastmessage);
            messageTime = view.findViewById(R.id.message_dateline);
            messagePhoto = view.findViewById(R.id.message_photo);
            isNew = view.findViewById(R.id.isNew);
            vipState = view.findViewById(R.id.vip_photo_status);
        }
    }
}
