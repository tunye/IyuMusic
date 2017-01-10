package com.iyuba.music.adapter.study;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class SearchNewsAdapter extends RecyclerView.Adapter<SearchNewsAdapter.MyViewHolder> {
    private ArrayList<Article> newsList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;

    public SearchNewsAdapter(Context context) {
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

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onRecycleViewItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
        final Article article = newsList.get(position);
        holder.title.setText(article.getTitle());
        holder.singer.setText(context.getString(R.string.article_singer, article.getSinger()));
        holder.broadcaster.setText(context.getString(R.string.article_announcer, article.getBroadcaster()));
        holder.time.setText(article.getTime().split(" ")[0]);
        holder.readCount.setText(context.getString(R.string.article_search_info, article.getTitleFind(), article.getTextFind()));
        ImageUtil.loadImage("http://static.iyuba.com/images/song/" + article.getPicUrl(),
                holder.pic, R.drawable.default_music);
        holder.downloadFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DownloadTask.checkFileExists(article)) {
                    onRecycleViewItemClickListener.onItemClick(holder.itemView, position);
                } else {
                    new LocalInfoOp().updateDownload(article.getId(), article.getApp(), 2);
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.id = article.getId();
                    downloadFile.downloadState = "start";
                    DownloadManager.sInstance.fileList.add(downloadFile);
                    new DownloadTask(article).start();
                    CustomToast.INSTANCE.showToast(R.string.article_download_start);
                }
            }
        });
        if (DownloadTask.checkFileExists(article)) {
            holder.downloadFlag.setImageResource(R.drawable.article_downloaded);
        } else {
            holder.downloadFlag.setImageResource(R.drawable.article_download);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView title, singer, broadcaster, time, readCount;
        ImageView pic,downloadFlag;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.article_title);
            singer = (TextView) view.findViewById(R.id.article_singer);
            broadcaster = (TextView) view.findViewById(R.id.article_announcer);
            time = (TextView) view.findViewById(R.id.article_createtime);
            pic = (ImageView) view.findViewById(R.id.article_image);
            downloadFlag = (ImageView) view.findViewById(R.id.article_download);
            readCount = (TextView) view.findViewById(R.id.article_readcount);
        }
    }
}
