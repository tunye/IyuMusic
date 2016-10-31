package com.iyuba.music.adapter.me;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2015/10/10.
 */
public class DoingAdapter extends RecyclerView.Adapter<DoingAdapter.MyViewHolder> {
    private ArrayList<Doing> doings;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public DoingAdapter(Context context) {
        this.context = context;
        doings = new ArrayList<>();
    }

    public void setDoingList(ArrayList<Doing> doings) {
        this.doings = doings;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_doing, parent, false));
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
        Doing doing = doings.get(position);
        holder.doingUserName.setText(doing.getUsername());
        holder.doingCounts.setText(doing.getReplynum());
        holder.doingContent.setText(doing.getMessage());
        holder.doingTime.setText(DateFormat.showTime(context, new Date(Long.parseLong(doing.getDateline()) * 1000)));
        ImageUtil.loadAvatar(doing.getUid(), holder.doingPhoto);
    }

    @Override
    public int getItemCount() {
        return doings.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView doingUserName, doingTime, doingCounts;
        JustifyTextView doingContent;
        CircleImageView doingPhoto;

        public MyViewHolder(View view) {
            super(view);
            doingUserName = (TextView) view.findViewById(R.id.doing_username);
            doingCounts = (TextView) view.findViewById(R.id.doing_reply_num);
            doingContent = (JustifyTextView) view.findViewById(R.id.doing_content);
            doingTime = (TextView) view.findViewById(R.id.doing_dateline);
            doingPhoto = (CircleImageView) view.findViewById(R.id.doing_photo);
        }
    }
}
