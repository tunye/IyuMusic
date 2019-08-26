package com.iyuba.music.ground;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 简版新闻列表适配器
 *
 * @author 陈彤
 * @version 1.0
 */
public class GroundNewsAdapter extends RecyclerView.Adapter<GroundNewsAdapter.MyViewHolder> {
    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private ArrayList<Article> mList;
    private boolean IscanDL = true;

    public GroundNewsAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
    }

    public GroundNewsAdapter(Context context, boolean IscanDL) {
        this.context = context;
        this.IscanDL = IscanDL;
        mList = new ArrayList<>();
    }

    public void setData(ArrayList<Article> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_common_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final int pos = position;
        if (onRecycleViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
        final Article article = mList.get(position);
        if (ConfigManager.getInstance().getLanguage() == 2 ||
                (ConfigManager.getInstance().getLanguage() == 0 && Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage()))) {
            holder.title.setText(article.getTitle());
        } else {
            if (TextUtils.isEmpty(article.getTitle_cn())) {
                holder.title.setText(article.getTitle());
            } else {
                holder.title.setText(article.getTitle_cn());
            }
        }
        holder.title.setTextColor(GetAppColor.getInstance().getAppColor());
        holder.content.setText(article.getContent());
        holder.time.setText(article.getTime().split(" ")[0]);
        holder.readCount.setText(context.getString(R.string.article_read_count, article.getReadCount()));
        if (IscanDL) {
            holder.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DownloadTask.checkFileExists(article)) {
                        onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
                    } else {
                        new LocalInfoOp().updateDownload(article.getId(), article.getApp(), 2);
                        DownloadFile downloadFile = new DownloadFile();
                        downloadFile.id = article.getId();
                        downloadFile.downloadState = "start";
                        DownloadManager.getInstance().fileList.add(downloadFile);
                        new DownloadTask(article).start();
                        CustomToast.getInstance().showToast(R.string.article_download_start);
                    }
                }
            });
        }
        ImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView title, content, time, readCount;
        ImageView pic;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.article_title);
            content = view.findViewById(R.id.article_content);
            time = view.findViewById(R.id.article_createtime);
            pic = view.findViewById(R.id.article_image);
            readCount = view.findViewById(R.id.article_readcount);
        }
    }
}
