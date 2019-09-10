package com.iyuba.music.activity.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.adapter.me.FriendAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.Friend;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.FriendRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.roundview.RoundLinearLayout;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

/**
 * Created by 10202 on 2016/3/2.
 */
public class FindFriendActivity extends BaseListActivity<Friend> {
    private View search;
    private RoundLinearLayout searchLayout;
    private MaterialEditText searchContent;
    private IyubaDialog waittingDialog;

    @Override
    public int getLayoutId() {
        return R.layout.friend_search_recycle;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        owner = findViewById(R.id.friendlist);
        ownerAdapter = new FriendAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.getInstance().pushFriendId(getData().get(position).getUid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
            }
        });
        assembleRecyclerView();
        search = findViewById(R.id.friend_search);
        searchLayout = findViewById(R.id.search_layout);
        searchContent = findViewById(R.id.search_content);
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.friend_finding));
    }

    @Override
    public void setListener() {
        super.setListener();
        search.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
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
    public void onActivityCreated() {
        super.onActivityCreated();
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

    public @StringRes
    int getToastResource() {
        return R.string.friend_load_all;
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new FriendRequest(AccountManager.getInstance().getUserId(), FriendRequest.SEARCH_REQUEST_CODE, searchContent.getEditableText().toString(), curPage), new SimpleRequestCallBack<BaseListEntity<List<Friend>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Friend>> listEntity) {
                isLastPage = listEntity.isLastPage();
                if (!isLastPage && curPage != 1) {
                    CustomToast.getInstance().showToast(curPage + "/" + listEntity.getTotalPage(), 800);
                }
                if (!BaseListEntity.isSuccess(listEntity)) {
                    swipeRefreshLayout.setRefreshing(false);
                    CustomToast.getInstance().showToast(R.string.friend_find_error);
                } else {
                    onNetDataReturnSuccess(listEntity.getData());
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
