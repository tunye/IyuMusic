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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buaa.ct.skin.BaseSkinActivity;
import com.flyco.roundview.RoundLinearLayout;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.adapter.discover.WordSearchAdapter;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.entity.word.WordSetOp;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by 10202 on 2015/12/2.
 */
public class WordSearchActivity extends BaseSkinActivity {
    protected Context context;
    protected RelativeLayout searchBarLayout;
    private WordSearchAdapter wordSearchAdapter;
    private ArrayList<Word> wordArrayList;
    private RoundLinearLayout searchLayout;
    private MaterialEditText searchContent;
    private WordSetOp wordSetOp;
    private TextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(GetAppColor.instance.getAppColor(this));
            getWindow().setNavigationBarColor(GetAppColor.instance.getAppColor(this));
        }
        setContentView(R.layout.word_search);
        context = this;
        wordArrayList = new ArrayList<>();
        initWidget();
        setListener();
        changeUIByPara();
    }

    protected void initWidget() {
        searchBarLayout = (RelativeLayout) findViewById(R.id.search_bar);
        searchContent = (MaterialEditText) findViewById(R.id.search_content);
        searchContent.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchLayout = (RoundLinearLayout) findViewById(R.id.search_layout);
        search = (TextView) findViewById(R.id.search);
        RecyclerView wordList = (RecyclerView) findViewById(R.id.word_search_list);
        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordSearchAdapter = new WordSearchAdapter(context, wordArrayList);
        wordList.setAdapter(wordSearchAdapter);
        wordList.addItemDecoration(new DividerItemDecoration());
        wordList.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
    }

    protected void setListener() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getText().equals(context.getString(R.string.search_do))) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
                    searchWord(searchContent.getEditableText().toString());
                    searchContent.setText("");
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
        wordSearchAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                searchWord(wordArrayList.get(position).getWord());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    protected void changeUIByPara() {
        wordSetOp = new WordSetOp();
        search.setText(R.string.search_close);
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
