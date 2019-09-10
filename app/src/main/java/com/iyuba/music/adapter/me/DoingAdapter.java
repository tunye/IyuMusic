package com.iyuba.music.adapter.me;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.iyuba.music.R;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.Date;

/**
 * Created by 10202 on 2015/10/10.
 */
public class DoingAdapter extends CoreRecyclerViewAdapter<Doing, DoingAdapter.MyViewHolder> {
    private boolean isVip;

    public DoingAdapter(Context context) {
        super(context);
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_doing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Doing doing = getDatas().get(position);
        holder.doingUserName.setText(doing.getUsername());
        holder.doingCounts.setText(context.getString(R.string.person_reply_num, doing.getReplynum()));
        holder.doingContent.setText(doing.getMessage());
        holder.doingTime.setText(DateFormat.showTime(context, new Date(Long.parseLong(doing.getDateline()) * 1000)));
        holder.doingPhoto.setVipStateVisible(doing.getUid(), isVip);
    }

    static class MyViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView doingUserName, doingTime, doingCounts;
        JustifyTextView doingContent;
        VipPhoto doingPhoto;

        MyViewHolder(View view) {
            super(view);
            doingUserName = view.findViewById(R.id.doing_username);
            doingCounts = view.findViewById(R.id.doing_reply_num);
            doingContent = view.findViewById(R.id.doing_content);
            doingTime = view.findViewById(R.id.doing_dateline);
            doingPhoto = view.findViewById(R.id.doing_photo);
        }
    }
}
