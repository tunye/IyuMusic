package com.iyuba.music.adapter.study;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.LocalInfoOp;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class AnnouncerNewsAdapter extends RecyclerView.Adapter<AnnouncerNewsAdapter.MyViewHolder> {
    private ArrayList<Article> newsList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;
    private int type;
    private boolean delete;

    public AnnouncerNewsAdapter(Context context) {
        this.context = context;
        newsList = new ArrayList<>();
        type = 0;
        this.delete = false;
    }


    public AnnouncerNewsAdapter(Context context, int type) {
        this.context = context;
        newsList = new ArrayList<>();
        this.type = type;
        this.delete = false;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
        notifyDataSetChanged();
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void removeData(int pos) {
        newsList.remove(pos);
        notifyItemChanged(pos);
    }

    public ArrayList<Article> getDataSet() {
        return newsList;
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
                    if (delete) {
                        holder.delete.setChecked(!holder.delete.isChecked());
                        newsList.get(position).setDelete(holder.delete.isChecked());
                    } else {
                        onRecycleViewItemClickListener.onItemClick(holder.itemView, position);
                    }
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
        if (article.getApp().equals("209")) {
            holder.singer.setText(context.getString(R.string.artical_singer, article.getSinger()));
            holder.broadcaster.setText(context.getString(R.string.artical_announcer, article.getBroadcaster()));
        } else {
            holder.singer.setText(article.getContent());
        }
        holder.time.setText(article.getTime().split(" ")[0]);
        switch (type) {
            case 0:
                holder.readCount.setText(context.getString(R.string.artical_readcount, article.getReadCount()));
                break;
            case 1:
                try {
                    holder.readCount.setText(DateFormat.showTime(context, DateFormat.parseTime(article.getExpireContent())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        if (delete) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsList.get(position).setDelete(holder.delete.isChecked());
            }
        });
        holder.delete.setChecked(article.isDelete());
        holder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DownloadTask.checkFileExists(article)) {
                    onRecycleViewItemClickListener.onItemClick(holder.itemView, position);
                } else {
                    new LocalInfoOp().updateDownload(article.getId(), article.getApp(), 2);
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.id = article.getId();
                    downloadFile.downloadState = "start";
                    DownloadManager.Instance().fileList.add(downloadFile);
                    new DownloadTask(article).start();
                    CustomToast.INSTANCE.showToast(R.string.artical_download_start);
                }
            }
        });
        if (article.getApp().equals("209")) {
            ImageUtil.loadImage("http://static.iyuba.com/images/song/" + article.getPicUrl(), holder.pic, R.drawable.default_music);
        } else {
            ImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class MyViewHolder extends RecycleViewHolder {

        TextView title, singer, broadcaster, time, readCount;
        ImageView pic;
        CheckBox delete;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.artical_title);
            singer = (TextView) view.findViewById(R.id.artical_singer);
            broadcaster = (TextView) view.findViewById(R.id.artical_announcer);
            time = (TextView) view.findViewById(R.id.artical_createtime);
            pic = (ImageView) view.findViewById(R.id.artical_image);
            readCount = (TextView) view.findViewById(R.id.artical_readcount);
            delete = (CheckBox) view.findViewById(R.id.item_delete);
        }
    }
}
