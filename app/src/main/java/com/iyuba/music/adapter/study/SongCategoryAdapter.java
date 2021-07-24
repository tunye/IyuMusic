package com.iyuba.music.adapter.study;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.IOnClickListener;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.main.MusicActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.mainpanel.SongCategory;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.widget.banner.BannerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/10/10.
 */
public class SongCategoryAdapter extends CoreRecyclerViewAdapter<SongCategory, CoreRecyclerViewAdapter.MyViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<BannerEntity> adPicUrl;

    public SongCategoryAdapter(Context context) {
        super(context);
        adPicUrl = new ArrayList<>();
    }

    private static void getAppointArticle(final Context context, String id) {
        RequestClient.requestAsync(new NewsesRequest(id), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Article>> listEntity) {
                List<Article> netData = listEntity.getData();
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

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setAdSet(List<BannerEntity> ads) {
        adPicUrl = ads;
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
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
        return getDatas().get(position - 1);
    }

    @NonNull
    @Override
    public CoreRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_songcategory, parent, false));
        } else {
            return new AdViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ad, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewAdapter.MyViewHolder holder, int position) {
        final int pos = position;
        if (holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            final SongCategory songCategory = getItem(position);
            if (onRecycleViewItemClickListener != null) {
                MaterialRippleLayout rippleView = (MaterialRippleLayout) newsViewHolder.itemView;
                rippleView.setOnClickListener(new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        onRecycleViewItemClickListener.onItemClick(newsViewHolder.itemView, pos);
                    }
                });
            }
            newsViewHolder.text.setText(songCategory.getText());
            newsViewHolder.count.setText(context.getString(R.string.article_list_count, songCategory.getCount()));
            AppImageUtil.loadImage("http://static.iyuba.cn/images/song/" + songCategory.getImgUrl(),
                    newsViewHolder.pic, R.drawable.default_music);
        } else if (holder instanceof AdViewHolder) {
            AdViewHolder headerViewHolder = (AdViewHolder) holder;
            headerViewHolder.setBannerData(adPicUrl);
        }
    }

    private static class NewsViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView text, count;
        ImageView pic;


        NewsViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.songlist_text);
            pic = view.findViewById(R.id.songlist_image);
            count = view.findViewById(R.id.songlist_count);
        }
    }

    private static class AdViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {
        BannerView bannerView;

        AdViewHolder(View view) {
            super(view);
            bannerView = view.findViewById(R.id.banner);
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
