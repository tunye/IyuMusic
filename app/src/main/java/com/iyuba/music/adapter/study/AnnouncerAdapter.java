package com.iyuba.music.adapter.study;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;
import com.iyuba.music.widget.imageview.CircleImageView;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class AnnouncerAdapter extends RecyclerView.Adapter<AnnouncerAdapter.MyViewHolder> {
    private ArrayList<Announcer> announcers;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public AnnouncerAdapter(Context context) {
        this.context = context;
        announcers = new ArrayList<>();
    }

    public void setAnnouncerList(ArrayList<Announcer> announcers) {
        this.announcers = announcers;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_announcer, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.itemView, pos);
            }
        });
        final Announcer announcer = announcers.get(position);
        holder.name.setText(announcer.getName());
        ImageUtil.loadAvatar(announcer.getUid(), holder.photo);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(21)
            public void onClick(View v) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    SocialManager.getInstance().pushFriendId(announcer.getUid());
                    Intent intent = new Intent(context, PersonalHomeActivity.class);
                    intent.putExtra("needpop", true);
                    context.startActivity(intent);
                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            SocialManager.getInstance().pushFriendId(announcer.getUid());
                            Intent intent = new Intent(context, PersonalHomeActivity.class);
                            intent.putExtra("needpop", true);
                            context.startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcers.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView name;
        CircleImageView photo;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.announcer_username);
            photo = view.findViewById(R.id.announcer_photo);
        }
    }
}
