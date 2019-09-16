package com.iyuba.music.adapter.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.doings.DoingComment;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.Date;

/**
 * Created by 10202 on 2015/10/10.
 */
public class DoingCommentAdapter extends CoreRecyclerViewAdapter<DoingComment, DoingCommentAdapter.MyViewHolder> {

    public DoingCommentAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_doing_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final DoingComment doing = getDatas().get(position);
        holder.doingUserName.setText(doing.getUsername());
        holder.doingContent.setText(doing.getMessage());
        holder.doingTime.setText(DateFormat.showTime(context, new Date(Long.parseLong(doing.getDateline()) * 1000)));
        holder.doingPhoto.setVipStateVisible(doing.getUid(), doing.getVip() == 1);
        holder.doingPhoto.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                SocialManager.getInstance().pushFriendId(doing.getUid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                context.startActivity(intent);
            }
        });
    }

    static class MyViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView doingUserName, doingTime;
        JustifyTextView doingContent;
        VipPhoto doingPhoto;

        MyViewHolder(View view) {
            super(view);
            doingUserName = view.findViewById(R.id.doing_username);
            doingContent = view.findViewById(R.id.doing_content);
            doingTime = view.findViewById(R.id.doing_dateline);
            doingPhoto = view.findViewById(R.id.doing_photo);
        }
    }
}
