package com.iyuba.music.activity.eggshell.meizhi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.CustomSwipeToRefresh;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/4/7.
 */
public class MeizhiActivity extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private RecyclerView meizhiRecyclerView;
    private MeizhiAdapter meizhiAdapter;
    private CustomSwipeToRefresh swipeRefreshLayout;
    private ArrayList<Meizhi> meizhis;
    private int curPage;
    private boolean isLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eggshell_meizhi_main);
        context = this;
        curPage = 1;
        isLastPage = false;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        meizhiRecyclerView = (RecyclerView) findViewById(R.id.meizhi_recyclerview);
        swipeRefreshLayout = (CustomSwipeToRefresh) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        meizhiRecyclerView.setLayoutManager(gridLayoutManager);
        meizhiAdapter = new MeizhiAdapter(context);
        meizhiAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, MeizhiPhotoActivity.class);
                intent.putExtra("url", meizhis.get(position).getUrl());
                context.startActivity(intent);
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                MeizhiPhotoFragment fragment = MeizhiPhotoFragment.newInstance(meizhis.get(position).getUrl());
//                fragment.show(fragmentManager, "fragment_girl_photo");
//                fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                meizhiAdapter.removeData(position);
            }
        });
        meizhiRecyclerView.setAdapter(meizhiAdapter);
        swipeRefreshLayout.measure(View.MEASURED_SIZE_MASK, View.MEASURED_HEIGHT_STATE_SHIFT);
        onRefresh(0);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText("妹纸");
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        meizhis = new ArrayList<>();
        getData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (isLastPage) {
            CustomToast.INSTANCE.showToast("妹纸加载完了，您可真爱看妹子~");
        } else {
            curPage++;
            getData();
        }
    }

    @Override
    public void onClick(View view, Object message) {
        meizhiRecyclerView.scrollToPosition(0);
    }

    private void getData() {
        MeizhiRequest.getInstance().exeRequest(MeizhiRequest.getInstance().generateUrl(curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity entity = (BaseListEntity) object;
                isLastPage = entity.isLastPage();
                meizhis.addAll((ArrayList<Meizhi>) entity.getData());
                meizhiAdapter.setDataSet(meizhis);
            }
        });
    }
}
