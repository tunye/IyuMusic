package com.iyuba.music.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.flyco.roundview.RoundLinearLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.me.FriendAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.SearchFriend;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.SearchFriendRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/3/2.
 */
public class FindFriendActivity extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<SearchFriend> searchArrayList;
    private FriendAdapter friendAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int curPage;
    private boolean isLastPage = false;

    private View search;
    private RoundLinearLayout searchLayout;
    private MaterialEditText searchContent;
    private IyubaDialog waittingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_search_recycle);
        context = this;
        searchArrayList = new ArrayList<>();
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        recyclerView = (RecyclerView) findViewById(R.id.friendlist);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        searchArrayList = new ArrayList<>();
        friendAdapter = new FriendAdapter(context);
        friendAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.getInstance().pushFriendId(searchArrayList.get(position).getUid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        recyclerView.setAdapter(friendAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
        search = findViewById(R.id.friend_search);
        searchLayout = (RoundLinearLayout) findViewById(R.id.search_layout);
        searchContent = (MaterialEditText) findViewById(R.id.search_content);
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.friend_finding));
    }

    @Override
    protected void setListener() {
        super.setListener();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchContent.getEditableText().toString())) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(searchLayout);
                    CustomToast.getInstance().showToast(R.string.search_word_null);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                    onRefresh(0);
                    waittingDialog.show();
                }
            }
        });
        searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (TextUtils.isEmpty(searchContent.getEditableText().toString())) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(searchLayout);
                    CustomToast.getInstance().showToast(R.string.search_word_null);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                    onRefresh(0);
                    waittingDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.friend_find);
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        searchArrayList = new ArrayList<>();
        isLastPage = false;
        getFriendData(searchContent.getEditableText().toString());
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (searchArrayList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getFriendData(searchContent.getEditableText().toString());
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.friend_load_all);
        }
    }

    private void getFriendData(String s) {
        SearchFriendRequest.exeRequest(SearchFriendRequest.generateUrl(AccountManager.getInstance().getUserId(), s, curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                waittingDialog.dismiss();
                BaseListEntity listEntity = (BaseListEntity) object;
                if (BaseListEntity.isSuccess(listEntity)) {
                    isLastPage = listEntity.isLastPage();
                    searchArrayList.addAll((ArrayList<SearchFriend>) listEntity.getData());
                    friendAdapter.setFriendList(searchArrayList);
                    if (curPage == 1) {

                    } else {
                        CustomToast.getInstance().showToast(curPage + "/" + listEntity.getTotalPage(), 800);
                    }
                } else {
                    CustomToast.getInstance().showToast(R.string.friend_find_error);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View view, Object message) {
        recyclerView.scrollToPosition(0);
    }
}
