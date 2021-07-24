package com.iyuba.music.activity.eggshell.meizhi;

import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.imageselector.view.OnlyPreviewActivity;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.entity.BaseListEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2016/4/7.
 */
public class MeizhiActivity extends BaseListActivity<Meizhi> {

    @Override
    public int getLayoutId() {
        return R.layout.eggshell_meizhi_main;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        owner = findViewById(R.id.meizhi_recyclerview);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        owner.setLayoutManager(gridLayoutManager);
        ownerAdapter = new MeizhiAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<String> meizhiUrls = new ArrayList<>();
                for (Meizhi meizhi : getData()) {
                    meizhiUrls.add(meizhi.getUrl());
                }
                OnlyPreviewActivity.startPreview(context, meizhiUrls, position);
            }
        });
        owner.setAdapter(ownerAdapter);
        onRefresh(0);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.eggshell_meizhi);
    }

    @Override
    public int getToastResource() {
        return R.string.eggshell_meizhi_loadall;
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new MeizhiRequest(curPage), new SimpleRequestCallBack<BaseListEntity<List<Meizhi>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Meizhi>> baseListEntity) {
                isLastPage = baseListEntity.isLastPage();
                onNetDataReturnSuccess(baseListEntity.getData());
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }
}
