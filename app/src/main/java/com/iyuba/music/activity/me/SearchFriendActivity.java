package com.iyuba.music.activity.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

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
import com.iyuba.music.widget.recycleview.ListRequestAllState;
import com.iyuba.music.widget.roundview.RoundFrameLayout;
import com.iyuba.music.widget.roundview.RoundLinearLayout;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

/**
 * Created by 10202 on 2016/3/2.
 */
public class SearchFriendActivity extends BaseListActivity<Friend> {
    private View search;
    private RoundFrameLayout searchLayout;
    private MaterialEditText searchContent;
    private ListRequestAllState requestAllState;

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
                startActivity(intent);
            }
        });
        assembleRecyclerView();
        search = findViewById(R.id.friend_search);
        searchLayout = findViewById(R.id.search_layout);
        searchContent = findViewById(R.id.search_content);
        requestAllState = findViewById(R.id.list_request_all_state);
        requestAllState.setLoadingShowContent(R.string.friend_finding);
        requestAllState.setList(owner);
    }

    @Override
    public void setListener() {
        super.setListener();
        search.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                doSearch();
            }
        });
        searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                doSearch();
                return true;
            }
        });
    }

    public void doSearch() {
        if (TextUtils.isEmpty(searchContent.getEditableText().toString())) {
            YoYo.with(Techniques.Shake).duration(500).playOn(searchLayout);
            CustomToast.getInstance().showToast(R.string.friend_find_empty_string);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
            onRefresh(0);
            requestAllState.startLoad();
        }
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.friend_find);
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
                requestAllState.loadSuccess();
                onNetDataReturnSuccess(listEntity.getData());
                isLastPage = listEntity.isLastPage();
                if (!isLastPage && curPage != 1) {
                    CustomToast.getInstance().showToast(curPage + "/" + listEntity.getTotalPage(), Toast.LENGTH_SHORT);
                } else if (isLastPage && curPage == 1) {
                    requestAllState.show(new ErrorInfoWrapper(ErrorInfoWrapper.EMPTY_ERROR), null);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                if (curPage == 1) {
                    requestAllState.show(errorInfoWrapper, new ListRequestAllState.ListRequestListener() {
                        @Override
                        public void retryClick() {
                            getNetData();
                        }
                    });
                }
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
