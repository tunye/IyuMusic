package com.iyuba.music.local_music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2015/10/10.
 */
public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.MyViewHolder> {
    private static int lastPos = -1;
    private ArrayList<Article> musicList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;

    public LocalMusicAdapter(Context context) {
        this.context = context;
        musicList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setCurPos(int pos) {
        int tempPos = lastPos;
        lastPos = pos;
        if (tempPos != -1) {
            notifyItemChanged(tempPos);
        }
        notifyItemChanged(lastPos);
    }

    public void setDataSet(ArrayList<Article> newses) {
        musicList = newses;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_musiclist, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (onRecycleViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
        Article article = musicList.get(position);
        holder.title.setText(article.getTitle());
        holder.singer.setText(article.getSinger());
        holder.readCount.setText(context.getString(R.string.artical_duration, article.getBroadcaster()));
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

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class MyViewHolder extends RecycleViewHolder {

        TextView title, singer, readCount;
        CircleImageView pic;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.artical_title);
            singer = (TextView) view.findViewById(R.id.artical_announcer);
            pic = (CircleImageView) view.findViewById(R.id.artical_image);
            readCount = (TextView) view.findViewById(R.id.artical_readcount);
        }
    }
}
