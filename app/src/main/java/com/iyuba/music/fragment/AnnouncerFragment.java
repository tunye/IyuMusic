package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.activity.main.AnnouncerNewsList;
import com.iyuba.music.adapter.study.AnnouncerAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.request.mainpanelrequest.AnnouncerRequest;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/3/4.
 */
public class AnnouncerFragment extends BaseFragment implements IOnClickListener {
    private Context context;
    private RecyclerView announcerRecyclerView;
    private ArrayList<Announcer> announcerList;
    private AnnouncerAdapter anouncerAdapter;
    private AnnouncerOp announcerOp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        announcerOp = new AnnouncerOp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.recycleview, null);
        announcerRecyclerView = (RecyclerView) view.findViewById(R.id.listview);
        announcerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
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
        announcerRecyclerView.setAdapter(anouncerAdapter);
        announcerRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        getData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        announcerList = announcerOp.findAll();
        anouncerAdapter.setAnnouncerList(announcerList);
    }

    private void getData() {
        AnnouncerRequest.getInstance().exeRequest(new IProtocolResponse() {
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

    @Override
    public void onClick(View view, Object message) {
        announcerRecyclerView.scrollToPosition(0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getActivity().findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        }
    }
}
