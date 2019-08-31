package com.iyuba.music.adapter.study;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class SimpleNewsAdapter extends RecyclerView.Adapter<SimpleNewsAdapter.MyViewHolder> {
    private ArrayList<Article> newsList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;
    private int type;
    private boolean delete;
    private LocalInfoOp localInfoOp;
    private boolean deleteAll;

    public SimpleNewsAdapter(Context context) {
        this.context = context;
        newsList = new ArrayList<>();
        type = 0;
        this.delete = false;
        localInfoOp = new LocalInfoOp();
    }


    public SimpleNewsAdapter(Context context, int type) {
        this.context = context;
        newsList = new ArrayList<>();
        this.type = type;
        this.delete = false;
        localInfoOp = new LocalInfoOp();
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
        notifyDataSetChanged();
    }

    public void setDeleteAll() {
        if (deleteAll) {
            for (Article article : newsList) {
                article.setDelete(false);
            }
            deleteAll = false;
        } else {
            for (Article article : newsList) {
                article.setDelete(true);
            }
            deleteAll = true;
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener onItemClickListener) {
        onRecycleViewItemClickListener = onItemClickListener;
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_newslist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final int pos = position;
        if (onRecycleViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delete) {
                        holder.delete.setChecked(!holder.delete.isChecked());
                        newsList.get(holder.getAdapterPosition()).setDelete(holder.delete.isChecked());
                    } else {
                        onRecycleViewItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onRecycleViewItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
        final Article article = newsList.get(position);
        holder.title.setText(article.getTitle());
        if ("209".equals(article.getApp()) && !"401".equals(article.getCategory())) {
            holder.singer.setText(context.getString(R.string.article_singer, article.getSinger()));
            holder.broadcaster.setText(context.getString(R.string.article_announcer, article.getBroadcaster()));
        } else {
            holder.singer.setText(article.getContent());
        }
        holder.time.setText(article.getTime().split(" ")[0]);
        switch (type) {
            case 0:
                holder.readCount.setText(context.getString(R.string.article_read_count, article.getReadCount()));
                break;
            case 1:
                try {
                    holder.readCount.setText(DateFormat.showTime(context, DateFormat.parseTime(article.getExpireContent())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                holder.broadcaster.setVisibility(View.GONE);
                holder.readCount.setText(context.getString(R.string.article_search_info, article.getTitleFind(), article.getTextFind()));
                break;
        }
        if (localInfoOp.findDataById(article.getApp(), article.getId()).getDownload() == 2) {
            final int id = article.getId();
            holder.download.setVisibility(View.VISIBLE);
            holder.pic.setVisibility(View.GONE);
            holder.timeBackground.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            DownloadFile file;
            for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {
                file = DownloadManager.getInstance().fileList.get(i);
                if (file.id == id) {
                    switch (file.downloadState) {
                        case "start":
                            holder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
                            holder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                            if (file.fileSize != 0 && file.downloadSize != 0) {
                                holder.download.setMax(file.fileSize);
                                holder.download.setProgress(file.downloadSize);
                            } else {
                                holder.download.setMax(1);
                                holder.download.setProgress(0);
                            }
                            break;
                        case "half_finish":
                            holder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColorAccent());
                            holder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                            if (file.fileSize != 0 && file.downloadSize != 0) {
                                holder.download.setMax(file.fileSize);
                                holder.download.setProgress(file.downloadSize);
                            } else {
                                holder.download.setMax(1);
                                holder.download.setProgress(0);
                            }
                            break;
                        case "finish":
                            localInfoOp.updateDownload(file.id, article.getApp(), 1);
                            CustomToast.getInstance().showToast(R.string.article_download_success);
                            DownloadManager.getInstance().fileList.remove(file);
                            break;
                        case "fail":
                            localInfoOp.updateDownload(file.id, article.getApp(), 3);
                            CustomToast.getInstance().showToast(R.string.article_download_fail);
                            DownloadManager.getInstance().fileList.remove(file);
                            break;
                    }
                    holder.itemView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(pos);
                        }
                    }, 500);
                    break;
                }
            }
        } else {
            holder.download.setVisibility(View.GONE);
            holder.pic.setVisibility(View.VISIBLE);
            holder.timeBackground.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
        }
        if (delete) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsList.get(holder.getAdapterPosition()).setDelete(holder.delete.isChecked());
            }
        });
        holder.delete.setChecked(article.isDelete());
        holder.downloadFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DownloadTask.checkFileExists(article)) {
                    onRecycleViewItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    DownloadUtil.checkScore(article.getId(), new IOperationResult() {
                        @Override
                        public void success(Object object) {
                            new LocalInfoOp().updateDownload(article.getId(), article.getApp(), 2);
                            DownloadFile downloadFile = new DownloadFile();
                            downloadFile.id = article.getId();
                            downloadFile.downloadState = "start";
                            DownloadManager.getInstance().fileList.add(downloadFile);
                            new DownloadTask(article).start();
                            notifyItemChanged(holder.getAdapterPosition());
                        }

                        @Override
                        public void fail(Object object) {

                        }
                    });

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
        if (article.getApp().equals("209") && !"401".equals(article.getCategory())) {
            ImageUtil.loadImage("http://static.iyuba.cn/images/song/" + article.getPicUrl(), holder.pic, R.drawable.default_music);
        } else {
            ImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music);
        }
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
        ImageView pic, downloadFlag;
        CheckBox delete;
        View timeBackground;
        RoundProgressBar download;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.article_title);
            singer = view.findViewById(R.id.article_singer);
            broadcaster = view.findViewById(R.id.article_announcer);
            time = view.findViewById(R.id.article_createtime);
            pic = view.findViewById(R.id.article_image);
            downloadFlag = view.findViewById(R.id.article_download);
            readCount = view.findViewById(R.id.article_readcount);
            delete = view.findViewById(R.id.item_delete);
            timeBackground = view.findViewById(R.id.article_createtime_background);
            download = view.findViewById(R.id.roundProgressBar);
        }
    }
}
