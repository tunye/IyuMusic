package com.iyuba.music.activity.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.adapter.me.FriendAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.SearchFriend;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.SearchFriendRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.roundview.RoundLinearLayout;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/3/2.
 */
public class FindFriendActivity extends BaseListActivity<SearchFriend> {
    private FriendAdapter friendAdapter;

    private View search;
    private RoundLinearLayout searchLayout;
    private MaterialEditText searchContent;
    private IyubaDialog waittingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_search_recycle);
        datas = new ArrayList<>();
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.friendlist);
        setRecyclerViewProperty(recyclerView);
        datas = new ArrayList<>();
        friendAdapter = new FriendAdapter(context);
        friendAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.getInstance().pushFriendId(datas.get(position).getUid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        recyclerView.setAdapter(friendAdapter);
        search = findViewById(R.id.friend_search);
        searchLayout = findViewById(R.id.search_layout);
        searchContent = findViewById(R.id.search_content);
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
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }

    protected @StringRes
    int getToastResource() {
        return R.string.friend_load_all;
    }

    @Override
    protected void getNetData() {
        SearchFriendRequest.exeRequest(SearchFriendRequest.generateUrl(AccountManager.getInstance().getUserId(), searchContent.getEditableText().toString(), curPage), new IProtocolResponse<BaseListEntity<ArrayList<SearchFriend>>>() {
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
            public void response(BaseListEntity<ArrayList<SearchFriend>> listEntity) {
                waittingDialog.dismiss();
                if (BaseListEntity.isSuccess(listEntity)) {
                    isLastPage = listEntity.isLastPage();
                    datas.addAll(listEntity.getData());
                    friendAdapter.setFriendList(datas);
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
}
