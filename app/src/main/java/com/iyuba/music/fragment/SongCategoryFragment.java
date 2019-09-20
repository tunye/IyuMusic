package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.SPUtils;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.main.ClassifyNewsList;
import com.iyuba.music.adapter.study.SongCategoryAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.mainpanel.SongCategory;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.BannerPicRequest;
import com.iyuba.music.request.mainpanelrequest.SongCategoryRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.banner.BannerView;

import java.util.List;


/**
 * Created by 10202 on 2015/11/6.
 */
public class SongCategoryFragment extends BaseRecyclerViewFragment<SongCategory> {

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ownerAdapter = new SongCategoryAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, ClassifyNewsList.class);
                intent.putExtra("classify", getData().get(position).getId());
                intent.putExtra("classifyName", getData().get(position).getText());
                startActivity(intent);
            }

        });
        assembleRecyclerView();
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void getNetData() {
        if (getData().size() == 0) {
            if (!StudyManager.getInstance().getDailyLoadOnceMap().containsKey(getClassName())) {
                RequestClient.requestAsync(new BannerPicRequest("class.iyumusic.yuan"), new SimpleRequestCallBack<BaseListEntity<List<BannerEntity>>>() {
                    @Override
                    public void onSuccess(BaseListEntity<List<BannerEntity>> result) {
                        StudyManager.getInstance().getDailyLoadOnceMap().put(getClassName(), "qier");
                        List<BannerEntity> bannerEntities = result.getData();
                        BannerEntity bannerEntity = new BannerEntity();
                        bannerEntity.setOwnerid(BannerEntity.OWNER_EMPTY);
                        bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
                        bannerEntity.setDesc("全部原声歌曲列表");
                        bannerEntities.add(bannerEntity);
                        SPUtils.putString(ConfigManager.getInstance().getPreferences(), "songbanner", JSON.toJSONString(bannerEntities));
                        ((SongCategoryAdapter) ownerAdapter).setAdSet(bannerEntities);
                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {
                        loadLocalBannerData();
                    }
                });
            } else {
                loadLocalBannerData();
            }
        }
        RequestClient.requestAsync(new SongCategoryRequest(curPage), new SimpleRequestCallBack<BaseListEntity<List<SongCategory>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<SongCategory>> listEntity) {
                isLastPage = listEntity.isLastPage();
                swipeRefreshLayout.setRefreshing(false);
                if (isLastPage) {
                    CustomToast.getInstance().showToast(getToastResource());
                } else {
                    ownerAdapter.addDatas(listEntity.getData());
                    if (curPage != 1) {
                        owner.scrollToPosition(getYouAdPos(getData().size() + 1 - listEntity.getData().size()));
                    } else {
                        owner.scrollToPosition(0);
                    }
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadLocalBannerData() {
        ((SongCategoryAdapter) ownerAdapter).setAdSet(JSON.parseArray(SPUtils.loadString(ConfigManager.getInstance().getPreferences(), "songbanner"), BannerEntity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        BannerView bannerView = findBannerView();
        if (bannerView != null) {
            bannerView.startAd();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BannerView bannerView = findBannerView();
        if (bannerView != null) {
            bannerView.stopAd();
        }
    }

    private BannerView findBannerView() {
        if (((LinearLayoutManager) (owner.getLayoutManager())).findFirstVisibleItemPosition() != 0) {
            return null;
        }
        View view = owner.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                return bannerView;
        }
        return null;
    }
}
