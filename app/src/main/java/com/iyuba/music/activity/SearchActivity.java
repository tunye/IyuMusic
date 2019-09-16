package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buaa.ct.appskin.BaseSkinActivity;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.buaa.ct.core.view.swiperefresh.MySwipeRefreshLayout;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.study.RecommendSongActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SearchHistoryAdapter;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.article.SearchHistoryOp;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.SearchRequest;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/12/2.
 */
public class SearchActivity extends BaseSkinActivity implements MySwipeRefreshLayout.OnRefreshListener {
    protected Context context;
    protected RelativeLayout searchBarLayout;
    private TextView search;
    private MaterialEditText searchContent;
    private SimpleNewsAdapter searchNewsAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private TextView searchResult;
    private View showLayout;
    private int curPage;
    private boolean isLastPage;
    private ArrayList<Article> searchArrayList;

    private ListView historySearch;
    private SearchHistoryAdapter searchHistoryAdapter;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;

    private TextView adviceText;
    private RoundTextView adviceBtn;
    private View advice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangePropery.setAppConfig(this);
        setContentView(R.layout.search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(GetAppColor.getInstance().getAppColor());
        }
        context = this;
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        initWidget();
        setListener();
        onActivityCreated();
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    public void initWidget() {
        advice = findViewById(R.id.no_result);
        historySearch = findViewById(R.id.history_search);
        searchHistoryAdapter = new SearchHistoryAdapter(context);
        historySearch.setAdapter(searchHistoryAdapter);
        historySearch.addFooterView(initClearHistory());
        showLayout = findViewById(R.id.search_show_layout);
        searchBarLayout = findViewById(R.id.search_bar);
        search = findViewById(R.id.search_news);
        searchContent = findViewById(R.id.search_content);
        RecyclerView searchNewsRecycleView = findViewById(R.id.search_newslist);
        searchNewsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        searchNewsAdapter = new SimpleNewsAdapter(context, 2);
        searchNewsAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setListFragmentPos(SearchActivity.this.getClass().getName());
                StudyManager.getInstance().setSourceArticleList(searchArrayList);
                StudyManager.getInstance().setLesson("music");
                StudyManager.getInstance().setCurArticle(searchArrayList.get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }

        });
        searchNewsRecycleView.setAdapter(searchNewsAdapter);
        searchNewsRecycleView.addItemDecoration(new DividerItemDecoration());
        searchResult = findViewById(R.id.search_result);
        adviceText = findViewById(R.id.search_advice);
        adviceBtn = findViewById(R.id.search_advice_button);
    }

    public void setListener() {
        adviceBtn.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, RecommendSongActivity.class));
            }
        });
        historySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchContent.setText(searchHistoryAdapter.getItem(i).getContent());
                searchContent.setSelection(searchContent.getText().length());
                new SearchHistoryOp().saveData(searchContent.getEditableText().toString());
                onRefresh(0);
            }
        });
        searchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && count == 0) {
                    showLayout.setVisibility(View.GONE);
                    advice.setVisibility(View.GONE);
                    historySearch.setVisibility(View.VISIBLE);
                    searchHistoryAdapter.setList("");
                } else if (count == 1) {
                    searchHistoryAdapter.setList(searchContent.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    search.setText(R.string.search_close);
                } else {
                    search.setText(R.string.search_do);
                }
            }
        });
        search.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                if (searchContent.getEditableText().toString().startsWith("iyumusic://")) {
                    NullActivity.exePushData(context, searchContent.getEditableText().toString());
                } else {
                    if (search.getText().equals(context.getString(R.string.search_do))) {
                        new SearchHistoryOp().saveData(searchContent.getEditableText().toString());
                        onRefresh(0);
                    } else {
                        finish();
                    }
                }
            }
        });
        searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!searchContent.getEditableText().toString().startsWith("iyumusic://")) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                    new SearchHistoryOp().saveData(searchContent.getEditableText().toString());
                    showLayout.setVisibility(View.VISIBLE);
                    historySearch.setVisibility(View.GONE);
                    onRefresh(0);
                }
                return true;
            }
        });
    }

    public void onActivityCreated() {
        search.setText(R.string.search_close);
        showLayout.setVisibility(View.GONE);
        advice.setVisibility(View.GONE);
        historySearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
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
        searchMusic();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (!isLastPage) {
            curPage++;
            searchMusic();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.article_search_all);
        }
    }

    private void searchMusic() {
        RequestClient.requestAsync(new SearchRequest(searchContent.getText().toString(), curPage), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Article>> listEntity) {
                showLayout.setVisibility(View.VISIBLE);
                historySearch.setVisibility(View.GONE);
                advice.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                searchResult.setText(context.getString(R.string.search_result, searchContent.getText().toString(), listEntity.getTotalCount()));
                if (BaseListEntity.isFail(listEntity)) {
                    adviceText.setText(R.string.article_advice_2);
                    CustomToast.getInstance().showToast(R.string.article_no_search);
                    searchNewsAdapter.setDataSet(searchArrayList);
                    searchNewsAdapter.notifyDataSetChanged();
                } else {
                    isLastPage = listEntity.isLastPage();
                    if (isLastPage) {
                        CustomToast.getInstance().showToast(R.string.article_search_all);
                    } else {
                        adviceText.setText(R.string.article_advice_1);
                        ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                        searchArrayList.addAll(netData);
                        if (SearchActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                            StudyManager.getInstance().setSourceArticleList(searchArrayList);
                        }
                        searchNewsAdapter.setDataSet(searchArrayList);
                        if (curPage != 1) {
                            CustomToast.getInstance().showToast(curPage + "/" + listEntity.getTotalPage(), 800);
                        }
                        LocalInfo localinfo;
                        for (Article temp : netData) {
                            temp.setApp(ConstantManager.appId);
                            localinfo = localInfoOp.findDataById(temp.getApp(), temp.getId());
                            if (localinfo.getId() == 0) {
                                localinfo.setApp(temp.getApp());
                                localinfo.setId(temp.getId());
                                localInfoOp.saveData(localinfo);
                            }
                        }
                        articleOp.saveData(netData);
                    }
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                swipeRefreshLayout.setRefreshing(false);
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private LinearLayout initClearHistory() {
        LinearLayout footerParent = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(R.string.article_search_clear_all);
        textView.setTextSize(18);
        textView.setTextColor(context.getResources().getColor(R.color.text_color));
        textView.setPadding(0, 60, 0, 60);
        footerParent.setGravity(Gravity.CENTER_HORIZONTAL);
        footerParent.addView(textView);
        footerParent.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                clearAll();
            }
        });
        footerParent.setBackgroundResource(R.drawable.clear_search);
        return footerParent;
    }

    private void clearAll() {
        final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(context);
        mMaterialDialog.setTitle(R.string.search_word_do);
        mMaterialDialog.setMessage(R.string.article_search_clear_hint)
                .setPositiveButton(R.string.article_search_clear_sure, new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        mMaterialDialog.dismiss();
                        new SearchHistoryOp().deleteAll();
                        searchHistoryAdapter.setList("");
                    }
                })
                .setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }
}
