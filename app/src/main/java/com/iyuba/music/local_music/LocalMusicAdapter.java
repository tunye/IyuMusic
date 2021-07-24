package com.iyuba.music.local_music;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.view.image.CircleImageView;
import com.iyuba.music.R;
import com.iyuba.music.entity.article.Article;

/**
 * Created by 10202 on 2015/10/10.
 */
public class LocalMusicAdapter extends CoreRecyclerViewAdapter<Article, LocalMusicAdapter.MusicViewHolder> {
    private int lastPos = -1;

    LocalMusicAdapter(Context context) {
        super(context);
    }

    void setCurPos(int pos) {
        int tempPos = lastPos;
        lastPos = pos;
        if (tempPos != -1) {
            notifyItemChanged(tempPos);
        }
        notifyItemChanged(lastPos);
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(context).inflate(R.layout.item_musiclist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MusicViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Article article = getDatas().get(position);
        holder.title.setText(article.getTitle());
        holder.singer.setText(article.getSinger());
        holder.readCount.setText(context.getString(R.string.article_duration, article.getBroadcaster()));
        switch (position % 3) {
            case 0:
                holder.pic.setImageResource(R.drawable.default_music);
                break;
            case 1:
                holder.pic.setImageResource(R.mipmap.ic_launcher);
                break;
            case 2:
                holder.pic.setImageResource(R.drawable.default_photo);
                break;
        }
        if (position == lastPos) {
            startAnimation(holder.pic);
        } else {
            stopAnimation(holder.pic);
        }
    }

    private void startAnimation(CircleImageView rotate) {
        Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        rotate.startAnimation(operatingAnim);
    }

    private void stopAnimation(CircleImageView rotate) {
        rotate.clearAnimation();
    }

    static class MusicViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView title, singer, readCount;
        CircleImageView pic;

        MusicViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.article_title);
            singer = view.findViewById(R.id.article_announcer);
            pic = view.findViewById(R.id.article_image);
            readCount = view.findViewById(R.id.article_readcount);
        }
    }
}
