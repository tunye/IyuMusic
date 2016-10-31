package com.iyuba.music.adapter.study;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.LocalInfoOp;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/10.
 */
public class DownloadNewsAdapter extends RecyclerView.Adapter<DownloadNewsAdapter.MyViewHolder> {
    private HashMap<String, RoundProgressBar> progresses = new HashMap<>();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            DownloadFile file;
            RoundProgressBar tempBar;
            switch (msg.what) {
                case 1:
                    file = (DownloadFile) msg.obj;
                    Message message = new Message();
                    if (file.downloadState.equals("start")) {
                        tempBar = progresses.get(String.valueOf(file.id));
                        tempBar.setCricleProgressColor(context.getResources().getColor(R.color.skin_app_color));
                        if (file.fileSize != 0 && file.downloadSize != 0) {
                            tempBar.setMax(file.fileSize);
                            tempBar.setProgress(file.downloadSize);
                        } else {
                            tempBar.setMax(1);
                            tempBar.setProgress(0);
                        }
                        message.what = 1;
                        message.obj = file;
                        handler.sendMessageDelayed(message, 1500);
                    } else if (file.downloadState.equals("half_finish")) {
                        tempBar = progresses.get(String.valueOf(file.id));
                        tempBar.setCricleProgressColor(context.getResources().getColor(R.color.skin_color_accent));
                        if (file.fileSize != 0 && file.downloadSize != 0) {
                            tempBar.setMax(file.fileSize);
                            tempBar.setProgress(file.downloadSize);
                        } else {
                            tempBar.setMax(1);
                            tempBar.setProgress(0);
                        }
                        message.what = 1;
                        message.obj = file;
                        handler.sendMessageDelayed(message, 1500);
                    } else if (file.downloadState.equals("finish")) {
                        message.what = 2;
                        message.obj = file;
                        handler.removeMessages(msg.what);
                        handler.sendMessage(message);
                    }
                    break;
                case 2:
                    file = (DownloadFile) msg.obj;
                    tempBar = progresses.get(String.valueOf(file.id));
                    tempBar.setVisibility(View.GONE);
                    DownloadManager.sInstance.fileList.remove(file);
                    notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });
    private ArrayList<Article> newsList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;
    private LocalInfoOp localInfoOp;
    private boolean delete;

    public DownloadNewsAdapter(Context context) {
        this.context = context;
        this.delete = false;
        newsList = new ArrayList<>();
        localInfoOp = new LocalInfoOp();
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
        notifyDataSetChanged();
    }

    public void removeData(int pos) {
        newsList.remove(pos);
        notifyItemChanged(pos);
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
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
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_download_newslist, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Article article = newsList.get(position);
        if (onRecycleViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delete) {
                        holder.delete.setChecked(!holder.delete.isChecked());
                        newsList.get(position).setDelete(holder.delete.isChecked());
                    } else {
                        int pos = holder.getLayoutPosition();
                        onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onRecycleViewItemClickListener.onItemLongClick(holder.itemView, pos);
                    return true;
                }
            });
        }

        holder.title.setText(article.getTitle());
        if ("209".equals(article.getApp())) {
            holder.singer.setText(context.getString(R.string.artical_singer, article.getSinger()));
            holder.broadcaster.setText(context.getString(R.string.artical_announcer, article.getBroadcaster()));
        } else {
            holder.singer.setText(article.getContent());
        }
        holder.readCount.setText(context.getString(R.string.artical_readcount, article.getReadCount()));
        if (localInfoOp.findDataById(article.getApp(), article.getId()).getDownload() == 2) {
            final int id = article.getId();
            holder.download.setVisibility(View.VISIBLE);
            holder.pic.setVisibility(View.GONE);
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
        if (article.getApp().equals("209")) {
            ImageUtil.loadImage("http://static.iyuba.com/images/song/" + article.getPicUrl(),
                    holder.pic, R.drawable.default_music);
        } else {
            ImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView title, singer, broadcaster, readCount;
        ImageView pic;
        RoundProgressBar download;
        CheckBox delete;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.artical_title);
            singer = (TextView) view.findViewById(R.id.artical_singer);
            broadcaster = (TextView) view.findViewById(R.id.artical_announcer);
            download = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
            pic = (ImageView) view.findViewById(R.id.artical_image);
            readCount = (TextView) view.findViewById(R.id.artical_readcount);
            delete = (CheckBox) view.findViewById(R.id.item_delete);
        }
    }
}
