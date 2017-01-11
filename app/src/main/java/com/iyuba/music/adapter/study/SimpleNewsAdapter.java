package com.iyuba.music.adapter.study;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
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
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/10.
 */
public class SimpleNewsAdapter extends RecyclerView.Adapter<SimpleNewsAdapter.MyViewHolder> {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private ArrayList<Article> newsList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private HashMap<String, RoundProgressBar> progresses = new HashMap<>();
    private Context context;
    private int type;
    private boolean delete;
    private LocalInfoOp localInfoOp;

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
            for (int i = 0; i < DownloadManager.sInstance.fileList.size(); i++) {
                file = DownloadManager.sInstance.fileList.get(i);
                if (file.id == id) {
                    progresses.put(String.valueOf(file.id),
                            holder.download);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = file;
                    handler.sendMessage(message);
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
                newsList.get(position).setDelete(holder.delete.isChecked());
            }
        });
        holder.delete.setChecked(article.isDelete());
        holder.downloadFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DownloadTask.checkFileExists(article)) {
                    onRecycleViewItemClickListener.onItemClick(holder.itemView, position);
                } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    new LocalInfoOp().updateDownload(article.getId(), article.getApp(), 2);
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.id = article.getId();
                    downloadFile.downloadState = "start";
                    DownloadManager.sInstance.fileList.add(downloadFile);
                    new DownloadTask(article).start();
                    notifyItemChanged(position);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
        if (article.getApp().equals("209")) {
            ImageUtil.loadImage("http://static.iyuba.com/images/song/" + article.getPicUrl(), holder.pic, R.drawable.default_music);
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

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.article_title);
            singer = (TextView) view.findViewById(R.id.article_singer);
            broadcaster = (TextView) view.findViewById(R.id.article_announcer);
            time = (TextView) view.findViewById(R.id.article_createtime);
            pic = (ImageView) view.findViewById(R.id.article_image);
            downloadFlag = (ImageView) view.findViewById(R.id.article_download);
            readCount = (TextView) view.findViewById(R.id.article_readcount);
            delete = (CheckBox) view.findViewById(R.id.item_delete);
            timeBackground = view.findViewById(R.id.article_createtime_background);
            download = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<SimpleNewsAdapter> {
        @Override
        public void handleMessageByRef(final SimpleNewsAdapter adapter, Message msg) {
            DownloadFile file;
            RoundProgressBar tempBar;
            switch (msg.what) {
                case 1:
                    file = (DownloadFile) msg.obj;
                    Message message = new Message();
                    if (file.downloadState.equals("start")) {
                        tempBar = adapter.progresses.get(String.valueOf(file.id));
                        tempBar.setCricleProgressColor(adapter.context.getResources().getColor(R.color.skin_app_color));
                        if (file.fileSize != 0 && file.downloadSize != 0) {
                            tempBar.setMax(file.fileSize);
                            tempBar.setProgress(file.downloadSize);
                        } else {
                            tempBar.setMax(1);
                            tempBar.setProgress(0);
                        }
                        message.what = 1;
                        message.obj = file;
                        adapter.handler.sendMessageDelayed(message, 1500);
                    } else if (file.downloadState.equals("half_finish")) {
                        tempBar = adapter.progresses.get(String.valueOf(file.id));
                        tempBar.setCricleProgressColor(adapter.context.getResources().getColor(R.color.skin_color_accent));
                        if (file.fileSize != 0 && file.downloadSize != 0) {
                            tempBar.setMax(file.fileSize);
                            tempBar.setProgress(file.downloadSize);
                        } else {
                            tempBar.setMax(1);
                            tempBar.setProgress(0);
                        }
                        message.what = 1;
                        message.obj = file;
                        adapter.handler.sendMessageDelayed(message, 1500);
                    } else if (file.downloadState.equals("finish")) {
                        message.what = 2;
                        message.obj = file;
                        adapter.handler.removeMessages(msg.what);
                        adapter.handler.sendMessage(message);
                    }
                    break;
                case 2:
                    file = (DownloadFile) msg.obj;
                    tempBar = adapter.progresses.get(String.valueOf(file.id));
                    tempBar.setVisibility(View.GONE);
                    DownloadManager.sInstance.fileList.remove(file);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
