package com.iyuba.music.adapter.study;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private ArrayList<Article> newsList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;

    public MusicAdapter(Context context) {
        this.context = context;
        newsList = new ArrayList<>();
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setDataSet(ArrayList<Article> newses) {
        newsList = newses;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_newslist, parent, false));
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
        final Article article = newsList.get(position);
        holder.title.setText(article.getTitle());
        holder.singer.setText(context.getString(R.string.artical_singer, article.getSinger()));
        holder.singer.setSingleLine(false);
        holder.singer.setMaxLines(2);
        holder.time.setText(article.getTime().split(" ")[0]);
        holder.readCount.setText(context.getString(R.string.artical_readcount, article.getReadCount()));
        holder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (DownloadTask.checkFileExists(article)) {
                onRecycleViewItemClickListener.onItemClick(holder.itemView, position);
//                } else {
//                    new LocalInfoOp().updateDownload(newsList.get(position).getId(), "209", 2);
//                    DownloadFile downloadFile = new DownloadFile();
//                    downloadFile.id = newsList.get(position).getId();
//                    downloadFile.downloadState = "start";
//                    DownloadManager.Instance().fileList.add(downloadFile);
//                    new DownloadTask(newsList.get(position)).start();
//                    CustomToast.INSTANCE.showToast(R.string.artical_download_start);
//                }
            }
        });
        ImageUtil.loadImage("http://static.iyuba.com/images/song/" + article.getPicUrl(),
                holder.pic, R.drawable.default_music);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class MyViewHolder extends RecycleViewHolder {

        TextView title, singer, broadcaster, time, readCount;
        ImageView pic;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.artical_title);
            singer = (TextView) view.findViewById(R.id.artical_singer);
            broadcaster = (TextView) view.findViewById(R.id.artical_announcer);
            time = (TextView) view.findViewById(R.id.artical_createtime);
            pic = (ImageView) view.findViewById(R.id.artical_image);
            readCount = (TextView) view.findViewById(R.id.artical_readcount);
        }
    }
}
