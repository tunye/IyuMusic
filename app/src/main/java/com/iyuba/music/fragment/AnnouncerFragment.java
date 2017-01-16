package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.activity.main.AnnouncerNewsList;
import com.iyuba.music.adapter.study.AnnouncerAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.request.mainpanelrequest.AnnouncerRequest;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/3/4.
 */
public class AnnouncerFragment extends BaseRecyclerViewFragment {
    private ArrayList<Announcer> announcerList;
    private AnnouncerAdapter anouncerAdapter;
    private AnnouncerOp announcerOp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announcerOp = new AnnouncerOp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        announcerList = new ArrayList<>();
        anouncerAdapter = new AnnouncerAdapter(context);
        anouncerAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, AnnouncerNewsList.class);
                intent.putExtra("announcer", String.valueOf(announcerList.get(position).getId()));
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        recyclerView.setAdapter(anouncerAdapter);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        disableSwipeLayout();
        getData();
    }

    private void getData() {
        AnnouncerRequest.exeRequest(new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                announcerList = announcerOp.findAll();
                anouncerAdapter.setAnnouncerList(announcerList);
            }

            @Override
            public void onServerError(String msg) {
                announcerList = announcerOp.findAll();
                anouncerAdapter.setAnnouncerList(announcerList);
            }

            @Override
            public void response(Object object) {
                announcerList = (ArrayList<Announcer>) ((BaseListEntity) object).getData();
                announcerOp.saveData(announcerList);
                anouncerAdapter.setAnnouncerList(announcerList);
            }
        });
    }
}
