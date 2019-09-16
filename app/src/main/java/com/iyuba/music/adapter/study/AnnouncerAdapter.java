package com.iyuba.music.adapter.study;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.view.image.CircleImageView;
import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.widget.dialog.CustomDialog;

/**
 * Created by 10202 on 2015/10/10.
 */
public class AnnouncerAdapter extends CoreRecyclerViewAdapter<Announcer, AnnouncerAdapter.MyViewHolder> {
    public AnnouncerAdapter(Context context) {
        super(context);
        baseEntityOp = new AnnouncerOp();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_announcer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Announcer announcer = getDatas().get(position);
        holder.name.setText(announcer.getName());
        AppImageUtil.loadAvatar(announcer.getUid(), holder.photo);
        holder.photo.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
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

    static class MyViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView name;
        CircleImageView photo;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.announcer_username);
            photo = view.findViewById(R.id.announcer_photo);
        }
    }
}
