package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.iyuba.music.activity.main.AnnouncerNewsList;
import com.iyuba.music.adapter.study.AnnouncerAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.AnnouncerRequest;

import java.util.List;

/**
 * Created by 10202 on 2016/3/4.
 */
public class AnnouncerFragment extends BaseRecyclerViewFragment<Announcer> {
    private AnnouncerOp announcerOp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announcerOp = new AnnouncerOp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ownerAdapter = new AnnouncerAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, AnnouncerNewsList.class);
                intent.putExtra("announcer", String.valueOf(getData().get(position).getId()));
                context.startActivity(intent);
            }
        });
        assembleRecyclerView();
        swipeRefreshLayout.setEnabled(false);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        disableSwipeLayout();
        if (!StudyManager.getInstance().getSingleInstanceRequest().containsKey(this.getClass().getSimpleName())) {
            getNetData();
        }
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new AnnouncerRequest(), new SimpleRequestCallBack<BaseListEntity<List<Announcer>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Announcer>> result) {
                StudyManager.getInstance().getSingleInstanceRequest().put(this.getClass().getSimpleName(), "qier");
                ownerAdapter.setDataSet(result.getData());
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                ownerAdapter.setDataSet(announcerOp.findAll());
            }
        });
    }
}
