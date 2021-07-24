package com.iyuba.music.ground;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.image.RoundedImageView;
import com.iyuba.music.R;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.util.AppImageUtil;

import java.util.Locale;

/**
 * 简版新闻列表适配器
 *
 * @author 陈彤
 * @version 1.0
 */
public class GroundNewsAdapter extends CoreRecyclerViewAdapter<Article, GroundNewsAdapter.NewsViewHolder> {
    private boolean IscanDL = true;

    public GroundNewsAdapter(Context context) {
        super(context);
    }

    public GroundNewsAdapter(Context context, boolean IscanDL) {
        super(context);
        this.IscanDL = IscanDL;
        baseEntityOp = new ArticleOp();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_common_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Article article = getDatas().get(position);
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
            holder.pic.setOnClickListener(new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
                    if (DownloadTask.checkFileExists(article)) {
                        holder.itemView.performClick();
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
        AppImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music, ConstantManager.NEWSPIC_CORNER);
    }

    static class NewsViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView title, content, time, readCount;
        RoundedImageView pic;

        NewsViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.article_title);
            content = view.findViewById(R.id.article_content);
            time = view.findViewById(R.id.article_createtime);
            pic = view.findViewById(R.id.article_image);
            readCount = view.findViewById(R.id.article_readcount);
        }
    }
}
