package com.iyuba.music.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.adapter.SleepAdapter;

/**
 * Created by 10202 on 2015/11/30.
 */
public class SleepActivity extends BaseActivity {
    private SleepAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.sleep;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        RecyclerView listView = findViewById(R.id.sleep_list);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.addItemDecoration(new DividerItemDecoration());
        ((SimpleItemAnimator) listView.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new SleepAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    public void setListener() {
        super.setListener();
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ((MusicApplication) getApplication()).setSleepSecond(adapter.getMinute() * 60);
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.sleep_title);
    }
}
