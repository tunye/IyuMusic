package com.iyuba.music.adapter.study;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.banner.BannerView;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/10/10.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<Article> newsList;
    private ArrayList<BannerEntity> adPicUrl;
    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private LocalInfoOp localInfoOp;

    public NewsAdapter(Context context) {
        this.context = context;
        newsList = new ArrayList<>();
        adPicUrl = new ArrayList<>();
        localInfoOp = new LocalInfoOp();
    }

    private static void getAppointArticle(final Context context, String id) {
        NewsesRequest.exeRequest(NewsesRequest.generateUrl(id), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                for (Article temp : netData) {
                    temp.setApp(ConstantManager.appId);
                }
                new ArticleOp().saveData(netData);
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setListFragmentPos("SongCategoryFragment.class");
                StudyManager.getInstance().setLesson("music");
                StudyManager.getInstance().setSourceArticleList(netData);
                StudyManager.getInstance().setCurArticle(netData.get(0));
                context.startActivity(new Intent(context, StudyActivity.class));
            }
        });
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener onItemClickListener) {
        onRecycleViewItemClickListener = onItemClickListener;
    }

    public void setDataSet(ArrayList<Article> newses) {
        newsList = newses;
        notifyDataSetChanged();
    }

    public void setAdSet(ArrayList<BannerEntity> ads) {
        adPicUrl = ads;
        notifyItemChanged(0);
    }

    public void removeData(int position) {
        newsList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeData(int[] position) {
        for (int i : position) {
            newsList.remove(i);
            notifyItemRemoved(i);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private Article getItem(int position) {
        return newsList.get(position - 1);
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_newslist, parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new AdViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ad, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
        final int pos = position;
        if (holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            final Article article = getItem(position);
            if (onRecycleViewItemClickListener != null) {
                newsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecycleViewItemClickListener.onItemClick(newsViewHolder.itemView, pos - 1);
                    }
                });
            }
            newsViewHolder.title.setText(article.getTitle());
            newsViewHolder.singer.setText(context.getString(R.string.article_singer, article.getSinger()));
            newsViewHolder.broadcaster.setText(context.getString(R.string.article_announcer, article.getBroadcaster()));
            newsViewHolder.time.setText(article.getTime().split(" ")[0]);
            newsViewHolder.readCount.setText(context.getString(R.string.article_read_count, article.getReadCount()));
            ImageUtil.loadImage("http://static.iyuba.cn/images/song/" + article.getPicUrl(),
                    newsViewHolder.pic, R.drawable.default_music);
            newsViewHolder.downloadFlag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DownloadTask.checkFileExists(article)) {
                        onRecycleViewItemClickListener.onItemClick(newsViewHolder.itemView, pos);
                    } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        DownloadUtil.checkScore(article.getId(), new IOperationResult() {
                            @Override
                            public void success(Object object) {
                                new LocalInfoOp().updateDownload(article.getId(), "209", 2);
                                DownloadFile downloadFile = new DownloadFile();
                                downloadFile.id = article.getId();
                                downloadFile.downloadState = "start";
                                DownloadManager.getInstance().fileList.add(downloadFile);
                                new DownloadTask(article).start();
                                notifyItemChanged(pos);
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
            if (localInfoOp.findDataById(article.getApp(), article.getId()).getDownload() == 2) {
                final int id = article.getId();
                newsViewHolder.download.setVisibility(View.VISIBLE);
                newsViewHolder.pic.setVisibility(View.GONE);
                newsViewHolder.timeBackground.setVisibility(View.GONE);
                newsViewHolder.time.setVisibility(View.GONE);
                DownloadFile file;
                for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {
                    file = DownloadManager.getInstance().fileList.get(i);
                    if (file.id == id) {
                        switch (file.downloadState) {
                            case "start":
                                newsViewHolder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
                                newsViewHolder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                                if (file.fileSize != 0 && file.downloadSize != 0) {
                                    newsViewHolder.download.setMax(file.fileSize);
                                    newsViewHolder.download.setProgress(file.downloadSize);
                                } else {
                                    newsViewHolder.download.setMax(1);
                                    newsViewHolder.download.setProgress(0);
                                }
                                break;
                            case "half_finish":
                                newsViewHolder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColorAccent());
                                newsViewHolder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                                if (file.fileSize != 0 && file.downloadSize != 0) {
                                    newsViewHolder.download.setMax(file.fileSize);
                                    newsViewHolder.download.setProgress(file.downloadSize);
                                } else {
                                    newsViewHolder.download.setMax(1);
                                    newsViewHolder.download.setProgress(0);
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
                        newsViewHolder.itemView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(pos);
                            }
                        }, 500);
                        break;
                    }
                }
            } else {
                newsViewHolder.download.setVisibility(View.GONE);
                newsViewHolder.pic.setVisibility(View.VISIBLE);
                newsViewHolder.timeBackground.setVisibility(View.VISIBLE);
                newsViewHolder.time.setVisibility(View.VISIBLE);
            }
            if (DownloadTask.checkFileExists(article)) {
                newsViewHolder.downloadFlag.setImageResource(R.drawable.article_downloaded);
            } else {
                newsViewHolder.downloadFlag.setImageResource(R.drawable.article_download);
            }
        } else if (holder instanceof AdViewHolder) {
            AdViewHolder headerViewHolder = (AdViewHolder) holder;
            headerViewHolder.setBannerData(adPicUrl);
        }
    }

    private static class NewsViewHolder extends RecycleViewHolder {
        TextView title, singer, broadcaster, time, readCount;
        ImageView pic, downloadFlag;
        View timeBackground;
        RoundProgressBar download;

        NewsViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.article_title);
            singer = (TextView) view.findViewById(R.id.article_singer);
            broadcaster = (TextView) view.findViewById(R.id.article_announcer);
            time = (TextView) view.findViewById(R.id.article_createtime);
            pic = (ImageView) view.findViewById(R.id.article_image);
            downloadFlag = (ImageView) view.findViewById(R.id.article_download);
            readCount = (TextView) view.findViewById(R.id.article_readcount);
            timeBackground = view.findViewById(R.id.article_createtime_background);
            download = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        }
    }

    private static class AdViewHolder extends RecycleViewHolder {
        BannerView bannerView;

        AdViewHolder(View view) {
            super(view);
            bannerView = (BannerView) view.findViewById(R.id.banner);
            bannerView.setSelectItemColor(GetAppColor.getInstance().getAppColor());
        }

        void setBannerData(final List<BannerEntity> datas) {
            bannerView.initData(datas, new IOnClickListener() {
                @Override
                public void onClick(View view, Object message) {
                    BannerEntity data = (BannerEntity) message;
                    switch (data.getOwnerid()) {
                        case "0":
                            Intent intent = new Intent(view.getContext(), WebViewActivity.class);
                            intent.putExtra("url", data.getName());
                            view.getContext().startActivity(intent);
                            break;
                        case "1":
                            Article tempArticle = new ArticleOp().findById(ConstantManager.appId, Integer.parseInt(data.getName()));
                            if (tempArticle.getId() == 0) {
                                getAppointArticle(view.getContext(), data.getName());
                            } else {
                                StudyManager.getInstance().setStartPlaying(true);
                                StudyManager.getInstance().setListFragmentPos("NewsFragment.class");
                                StudyManager.getInstance().setLesson("music");
                                ArrayList<Article> articles = new ArrayList<>();
                                articles.add(tempArticle);
                                StudyManager.getInstance().setSourceArticleList(articles);
                                StudyManager.getInstance().setCurArticle(tempArticle);
                                view.getContext().startActivity(new Intent(view.getContext(), StudyActivity.class));
                            }
                            break;
                        case "2":
                            CustomToast.getInstance().showToast(R.string.app_intro);
                            break;
                    }
                }
            });
        }
    }
}
