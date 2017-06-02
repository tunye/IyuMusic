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
import com.iyuba.music.activity.main.MusicActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.mainpanel.SongCategory;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.banner.BannerView;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/10/10.
 */
public class SongCategoryAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<SongCategory> newsList;
    private ArrayList<BannerEntity> adPicUrl;
    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public SongCategoryAdapter(Context context) {
        this.context = context;
        newsList = new ArrayList<>();
        adPicUrl = new ArrayList<>();
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
                    temp.setApp(ConstantManager.getInstance().getAppId());
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

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setDataSet(ArrayList<SongCategory> newses) {
        newsList = newses;
        notifyDataSetChanged();
    }

    public void setAdSet(ArrayList<BannerEntity> ads) {
        adPicUrl = ads;
        notifyItemChanged(0);
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

    public SongCategory getItem(int position) {
        return newsList.get(position - 1);
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_songcategory, parent, false));
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            return new AdViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ad, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
        final int pos = position;
        if (holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            final SongCategory songCategory = getItem(position);
            if (onRecycleViewItemClickListener != null) {
                MaterialRippleLayout rippleView = (MaterialRippleLayout) newsViewHolder.itemView;
                rippleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecycleViewItemClickListener.onItemClick(newsViewHolder.itemView, pos);
                    }
                });
            }
            newsViewHolder.text.setText(songCategory.getText());
            newsViewHolder.count.setText(context.getString(R.string.article_list_count, songCategory.getCount()));
            ImageUtil.loadImage("http://static.iyuba.com/images/song/" + songCategory.getImgUrl(),
                    newsViewHolder.pic, R.drawable.default_music);
        } else if (holder instanceof AdViewHolder) {
            AdViewHolder headerViewHolder = (AdViewHolder) holder;
            headerViewHolder.setBannerData(adPicUrl);
        }
    }

    private static class NewsViewHolder extends RecycleViewHolder {

        TextView text, count;
        ImageView pic;


        NewsViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.songlist_text);
            pic = (ImageView) view.findViewById(R.id.songlist_image);
            count = (TextView) view.findViewById(R.id.songlist_count);
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
                            Article tempArticle = new ArticleOp().findById("209", Integer.parseInt(data.getName()));
                            if (tempArticle.getId() == 0) {
                                getAppointArticle(view.getContext(), data.getName());
                            } else {
                                StudyManager.getInstance().setStartPlaying(true);
                                StudyManager.getInstance().setListFragmentPos("SongCategoryFragment.class");
                                StudyManager.getInstance().setLesson("music");
                                ArrayList<Article> articles = new ArrayList<>();
                                articles.add(tempArticle);
                                StudyManager.getInstance().setSourceArticleList(articles);
                                StudyManager.getInstance().setCurArticle(tempArticle);
                                view.getContext().startActivity(new Intent(view.getContext(), StudyActivity.class));
                            }
                            break;
                        case "2":
                            view.getContext().startActivity(new Intent(view.getContext(), MusicActivity.class));
                            break;
                    }
                }
            });
        }
    }
}
