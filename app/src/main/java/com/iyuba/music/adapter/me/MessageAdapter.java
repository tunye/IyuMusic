package com.iyuba.music.adapter.me;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.message.MessageLetter;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 10202 on 2015/10/10.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private ArrayList<MessageLetter> messages;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public MessageAdapter(Context context) {
        this.context = context;
        messages = new ArrayList<>();
    }

    public void setMessageList(ArrayList<MessageLetter> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setReaded(int position) {
        messages.get(position).setIsnew("0");
        notifyItemChanged(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message, parent, false));
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
        final MessageLetter messageLetter = messages.get(position);
        holder.messageUserName.setText(messageLetter.getFriendName());
        holder.messageCounts.setText("(" + messageLetter.getContentCount() + ")");
        holder.messageLastContent.setText(messageLetter.getLastmessage());
        holder.messageTime.setText(DateFormat.showTime(context, new Date(Long.parseLong(messageLetter.getDate()) * 1000)));
        ImageUtil.loadAvatar(messageLetter.getFriendid(), holder.messagePhoto);
        holder.messagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.instance.pushFriendId(messageLetter.getFriendid());
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
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView messageUserName, messageTime, messageCounts;
        JustifyTextView messageLastContent;
        ImageView messagePhoto;
        ImageView isNew;

        public MyViewHolder(View view) {
            super(view);
            messageUserName = (TextView) view.findViewById(R.id.message_username);
            messageCounts = (TextView) view.findViewById(R.id.message_pmnum);
            messageLastContent = (JustifyTextView) view.findViewById(R.id.message_lastmessage);
            messageTime = (TextView) view.findViewById(R.id.message_dateline);
            messagePhoto = (ImageView) view.findViewById(R.id.message_photo);
            isNew = (ImageView) view.findViewById(R.id.isNew);
        }
    }
}
