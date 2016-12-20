package com.iyuba.music.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.adapter.SleepAdapter;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

/**
 * Created by 10202 on 2015/11/30.
 */
public class SleepActivity extends BaseActivity {
    private SleepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView listView = (RecyclerView) findViewById(R.id.sleep_list);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.addItemDecoration(new DividerItemDecoration());
        adapter = new SleepAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ((MusicApplication) getApplication()).setSleepSecond(adapter.getMinute() * 60);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.sleep_title);
    }
}
