package com.iyuba.music.adapter.study;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.CustomToast;
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

    public NewsAdapter(Context context) {
        this.context = context;
        newsList = new ArrayList<>();
        adPicUrl = new ArrayList<>();
    }

    private static void getAppointArticle(String id) {
        NewsesRequest.getInstance().exeRequest(NewsesRequest.getInstance().generateUrl(id), new IProtocolResponse() {
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
                    temp.setApp(ConstantManager.instance.getAppId());
                }
                new ArticleOp().saveData(netData);
                StudyManager.instance.setStartPlaying(true);
                StudyManager.instance.setListFragmentPos("SongCategoryFragment.class");
                StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                StudyManager.instance.setSourceArticleList(netData);
                StudyManager.instance.setCurArticle(netData.get(0));
                RuntimeManager.getContext().startActivity(new Intent(RuntimeManager.getContext(), StudyActivity.class));
            }
        });
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
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
    public void onBindViewHolder(RecycleViewHolder holder, final int position) {
        if (holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            final Article article = getItem(position);
            if (onRecycleViewItemClickListener != null) {
                MaterialRippleLayout rippleView = (MaterialRippleLayout) newsViewHolder.itemView;
                rippleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = newsViewHolder.getLayoutPosition();
                        onRecycleViewItemClickListener.onItemClick(newsViewHolder.itemView, pos);
                    }
                });
            }
            newsViewHolder.title.setText(article.getTitle());
            newsViewHolder.singer.setText(context.getString(R.string.article_singer, article.getSinger()));
            newsViewHolder.broadcaster.setText(context.getString(R.string.article_announcer, article.getBroadcaster()));
            newsViewHolder.time.setText(article.getTime().split(" ")[0]);
            newsViewHolder.readCount.setText(context.getString(R.string.article_read_count, article.getReadCount()));
            ImageUtil.loadImage("http://static.iyuba.com/images/song/" + article.getPicUrl(),
                    newsViewHolder.pic, R.drawable.default_music);
            newsViewHolder.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (DownloadTask.checkFileExists(article)) {
                    onRecycleViewItemClickListener.onItemClick(newsViewHolder.itemView, position);
//                    } else {
//                        new LocalInfoOp().updateDownload(article.getId(), "209", 2);
//                        DownloadFile downloadFile = new DownloadFile();
//                        downloadFile.id = article.getId();
//                        downloadFile.downloadState = "start";
//                        DownloadManager.Instance().fileList.add(downloadFile);
//                        new DownloadTask(article).start();
//                        CustomToast.INSTANCE.showToast(R.string.article_download_start);
//                    }
                }
            });
        } else if (holder instanceof AdViewHolder) {
            AdViewHolder headerViewHolder = (AdViewHolder) holder;
            headerViewHolder.setBannerData(adPicUrl);
        }
    }

    private static class NewsViewHolder extends RecycleViewHolder {

        TextView title, singer, broadcaster, time, readCount;
        ImageView pic;


        NewsViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.article_title);
            singer = (TextView) view.findViewById(R.id.article_singer);
            broadcaster = (TextView) view.findViewById(R.id.article_announcer);
            time = (TextView) view.findViewById(R.id.article_createtime);
            pic = (ImageView) view.findViewById(R.id.article_image);
            readCount = (TextView) view.findViewById(R.id.article_readcount);
        }
    }

    private static class AdViewHolder extends RecycleViewHolder {
        BannerView bannerView;

        AdViewHolder(View view) {
            super(view);
            bannerView = (BannerView) view.findViewById(R.id.banner);
            bannerView.setSelectItemColor(GetAppColor.instance.getAppColor(view.getContext()));
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
                            Article tempArticle = new ArticleOp().findById("209", Integer.parseInt(data.getName()));
                            if (tempArticle.getId() == 0) {
                                getAppointArticle(data.getName());
                            } else {
                                StudyManager.instance.setStartPlaying(true);
                                StudyManager.instance.setListFragmentPos("NewsFragment.class");
                                StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                                ArrayList<Article> articles = new ArrayList<>();
                                articles.add(tempArticle);
                                StudyManager.instance.setSourceArticleList(articles);
                                StudyManager.instance.setCurArticle(tempArticle);
                                view.getContext().startActivity(new Intent(view.getContext(), StudyActivity.class));
                            }
                            break;
                        case "2":
                            CustomToast.INSTANCE.showToast(R.string.app_intro);
                            break;
                    }
                }
            });
        }
    }
}
