package com.iyuba.music.activity.discover;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.buaa.ct.appskin.BaseSkinActivity;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.adapter.discover.WordSearchAdapter;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.entity.word.WordSetOp;
import com.iyuba.music.util.ChangePropery;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by 10202 on 2015/12/2.
 */
public class WordSearchActivity extends BaseSkinActivity {
    protected Context context;
    protected View searchBarLayout;
    private WordSearchAdapter wordSearchAdapter;
    private List<Word> wordArrayList;
    private MaterialEditText searchContent;
    private WordSetOp wordSetOp;
    private TextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangePropery.setAppConfig(this);
        setContentView(R.layout.word_search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(GetAppColor.getInstance().getAppColor());
        }
        context = this;
        wordArrayList = new ArrayList<>();
        initWidget();
        setListener();
        onActivityCreated();
    }

    public void initWidget() {
        searchBarLayout = findViewById(R.id.search_bar);
        searchContent = findViewById(R.id.search_content);
        searchContent.setImeOptions(EditorInfo.IME_ACTION_DONE);
        search = findViewById(R.id.search);
        RecyclerView wordList = findViewById(R.id.word_search_list);
        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordSearchAdapter = new WordSearchAdapter(context);
        wordList.setAdapter(wordSearchAdapter);
        wordList.addItemDecoration(new DividerItemDecoration());
        wordList.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
    }

    public void setListener() {
        search.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                if (search.getText().equals(context.getString(R.string.search_do))) {
                    searchWord(searchContent.getEditableText().toString());
                    searchContent.setText("");
                } else {
                    finish();
                }
            }
        });
        searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                searchWord(v.getText().toString());
                searchContent.setText("");
                return true;
            }
        });

        searchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    search.setText(R.string.search_close);
                } else {
                    search.setText(R.string.search_do);
                    getLikeWord(s.toString());
                }
            }
        });
        wordSearchAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                searchWord(wordArrayList.get(position).getWord());
            }
        });
    }

    public void onActivityCreated() {
        wordSetOp = new WordSetOp();
        search.setText(R.string.search_close);
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

    private void getLikeWord(String word) {
        wordArrayList = wordSetOp.findDataByFuzzy(word);
        wordSearchAdapter.setDataSet(wordArrayList);
    }

    private void searchWord(String word) {
        Intent intent = new Intent(context, WordContentActivity.class);
        intent.putExtra("word", word);
        intent.putExtra("source", "wordsearch");
        startActivity(intent);
    }
}
