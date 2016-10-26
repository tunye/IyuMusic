package com.iyuba.music.ground;

import android.content.Context;
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
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.LocalInfoOp;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SettingConfigManager;
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
public class SimpleNewsAdapter extends RecyclerView.Adapter<SimpleNewsAdapter.MyViewHolder> {
    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private ArrayList<Article> mList = new ArrayList<>();

    public SimpleNewsAdapter(Context context) {
        this.context = context;
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_common_news, parent, false));
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
        final Article article = mList.get(position);
        if (SettingConfigManager.instance.getLanguage() == 2 ||
                (SettingConfigManager.instance.getLanguage() == 0 && Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage()))) {
            holder.title.setText(article.getTitle());
        } else {
            if (TextUtils.isEmpty(article.getTitle_cn())) {
                holder.title.setText(article.getTitle());
            } else {
                holder.title.setText(article.getTitle_cn());
            }
        }
        holder.title.setTextColor(GetAppColor.instance.getAppColor(context));
        holder.content.setText(article.getContent());
        holder.time.setText(article.getTime().split(" ")[0]);
        holder.readCount.setText(context.getString(R.string.artical_readcount, article.getReadCount()));
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
        ImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecycleViewHolder {

        TextView title, content, time, readCount;
        ImageView pic;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.artical_title);
            content = (TextView) view.findViewById(R.id.artical_content);
            time = (TextView) view.findViewById(R.id.artical_createtime);
            pic = (ImageView) view.findViewById(R.id.artical_image);
            readCount = (TextView) view.findViewById(R.id.artical_readcount);
        }
    }
}
