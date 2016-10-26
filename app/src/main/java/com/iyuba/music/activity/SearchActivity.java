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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buaa.ct.skin.BaseSkinActivity;
import com.flyco.roundview.RoundTextView;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.study.RecommendSongActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SearchHistoryAdapter;
import com.iyuba.music.adapter.study.SearchNewsAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.ArticleOp;
import com.iyuba.music.entity.artical.LocalInfo;
import com.iyuba.music.entity.artical.LocalInfoOp;
import com.iyuba.music.entity.artical.SearchHistoryOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.SearchRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/2.
 */
public class SearchActivity extends BaseSkinActivity implements MySwipeRefreshLayout.OnRefreshListener {
    protected Context context;
    protected RelativeLayout searchBarLayout;
    private TextView search;
    private MaterialEditText searchContent;
    private SearchNewsAdapter searchNewsAdapter;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(GetAppColor.instance.getAppColor(this));
            getWindow().setNavigationBarColor(GetAppColor.instance.getAppColor(this));
        }
        setContentView(R.layout.search);
        context = this;
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        initWidget();
        setListener();
        changeUIByPara();
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    protected void initWidget() {
        advice = findViewById(R.id.no_result);
        historySearch = (ListView) findViewById(R.id.history_search);
        searchHistoryAdapter = new SearchHistoryAdapter(context);
        historySearch.setAdapter(searchHistoryAdapter);
        historySearch.addFooterView(initClearHistory());
        showLayout = findViewById(R.id.search_show_layout);
        searchBarLayout = (RelativeLayout) findViewById(R.id.search_bar);
        search = (TextView) findViewById(R.id.search_news);
        searchContent = (MaterialEditText) findViewById(R.id.search_content);
        RecyclerView searchNewsRecycleView = (RecyclerView) findViewById(R.id.search_newslist);
        searchNewsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        searchNewsAdapter = new SearchNewsAdapter(context);
        searchNewsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyManager.instance.setStartPlaying(true);
                StudyManager.instance.setListFragmentPos(SearchActivity.this.getClass().getName());
                StudyManager.instance.setSourceArticleList(searchArrayList);
                StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                StudyManager.instance.setCurArticle(searchArrayList.get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        searchNewsRecycleView.setAdapter(searchNewsAdapter);
        searchNewsRecycleView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        searchResult = (TextView) findViewById(R.id.search_result);
        adviceText = (TextView) findViewById(R.id.search_advice);
        adviceBtn = (RoundTextView) findViewById(R.id.search_advice_button);
    }

    protected void setListener() {
        adviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RecommendSongActivity.class));
            }
        });
        searchHistoryAdapter.setItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                searchContent.setText(searchHistoryAdapter.getItem(position).getContent());
                searchContent.setSelection(searchContent.getText().length());
                new SearchHistoryOp().saveData(searchContent.getEditableText().toString());
                onRefresh(0);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        searchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 || count != 0) {
                    showLayout.setVisibility(View.GONE);
                    advice.setVisibility(View.GONE);
                    historySearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    search.setText(R.string.search_close);
                } else {
                    search.setText(R.string.search_do);
                }
                searchHistoryAdapter.setList(searchContent.getText().toString());
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getText().equals(context.getString(R.string.search_do))) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                    new SearchHistoryOp().saveData(searchContent.getEditableText().toString());
                    onRefresh(0);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    finish();
                }
            }
        });
        searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                new SearchHistoryOp().saveData(searchContent.getEditableText().toString());
                showLayout.setVisibility(View.VISIBLE);
                historySearch.setVisibility(View.GONE);
                onRefresh(0);
                return true;
            }
        });
    }

    protected void changeUIByPara() {
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
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
            CustomToast.INSTANCE.showToast(R.string.artical_search_all);
        }
    }

    private void searchMusic() {
        SearchRequest.getInstance().exeRequest(SearchRequest.getInstance().generateUrl(searchContent.getText().toString(), curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                swipeRefreshLayout.setRefreshing(false);
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                swipeRefreshLayout.setRefreshing(false);
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void response(Object object) {
                showLayout.setVisibility(View.VISIBLE);
                historySearch.setVisibility(View.GONE);
                advice.setVisibility(View.VISIBLE);
                BaseListEntity listEntity = (BaseListEntity) object;
                swipeRefreshLayout.setRefreshing(false);
                searchResult.setText(context.getString(R.string.search_result, searchContent.getText().toString(), listEntity.getTotalCount()));
                if (listEntity.getState().equals(BaseListEntity.State.FAIL)) {
                    adviceText.setText(R.string.artical_advice_2);
                    CustomToast.INSTANCE.showToast(R.string.artical_no_search);
                    searchNewsAdapter.setDataSet(searchArrayList);
                    searchNewsAdapter.notifyDataSetChanged();
                } else {
                    isLastPage = listEntity.isLastPage();
                    if (isLastPage) {
                        CustomToast.INSTANCE.showToast(R.string.artical_search_all);
                    } else {
                        adviceText.setText(R.string.artical_advice_1);
                        ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                        searchArrayList.addAll(netData);
                        if (SearchActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                            StudyManager.instance.setSourceArticleList(searchArrayList);
                        }
                        searchNewsAdapter.setDataSet(searchArrayList);
                        if (curPage != 1) {
                            CustomToast.INSTANCE.showToast(curPage + "/" + listEntity.getTotalPage(), 800);
                        }
                        LocalInfo localinfo;
                        for (Article temp : netData) {
                            temp.setApp(ConstantManager.instance.getAppId());
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
        });
    }

    private LinearLayout initClearHistory() {
        LinearLayout footerParent = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(R.string.atricle_search_clear_all);
        textView.setTextSize(18);
        textView.setTextColor(context.getResources().getColor(R.color.text_color));
        textView.setPadding(0, 60, 0, 60);
        footerParent.setGravity(Gravity.CENTER_HORIZONTAL);
        footerParent.addView(textView);
        footerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });
        footerParent.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.clear_search));
        return footerParent;
    }

    private void clearAll() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog.setTitle(R.string.search_word_do);
        mMaterialDialog.setMessage(R.string.atricle_search_clear_hint)
                .setPositiveButton(R.string.atricle_search_clear_sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        new SearchHistoryOp().deleteAll();
                        searchHistoryAdapter.setList("");
                    }
                })
                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }
}
