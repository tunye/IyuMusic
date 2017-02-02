package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.discover.WordExpandableAdapter;
import com.iyuba.music.entity.word.PersonalWordOp;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.entity.word.WordParent;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnExpandableRecycleViewClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.request.discoverrequest.DictSynchroRequest;
import com.iyuba.music.request.discoverrequest.DictUpdateRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.Dialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/2.
 */
public class WordListActivity extends BaseActivity implements ExpandableRecyclerAdapter.ExpandCollapseListener, OnExpandableRecycleViewClickListener {
    private RelativeLayout statusBar;
    private TextView wordEdit, wordSet, wordStatistic;
    private RecyclerView wordList;
    private WordExpandableAdapter wordExpandableAdapter;
    private ArrayList<Word> wordArrayList;
    private ArrayList<WordParent> wordParents;
    private ArrayList<Word> deleteList;
    private Dialog waitingDialog;
    private boolean deleteMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        context = this;
        wordParents = new ArrayList<>();
        wordArrayList = new ArrayList<>();
        deleteMode = false;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        statusBar = (RelativeLayout) findViewById(R.id.statusbar);
        wordList = (RecyclerView) findViewById(R.id.wordlist);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        wordEdit = (TextView) findViewById(R.id.word_edit);
        wordSet = (TextView) findViewById(R.id.word_set);
        wordStatistic = (TextView) findViewById(R.id.word_statistic);
        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordList.addItemDecoration(new DividerItemDecoration());
        waitingDialog =  WaitingDialog.create(context, context.getString(R.string.word_synchroing));
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteMode) {
                    deleteList = wordExpandableAdapter.getTryTodeleteList();
                    PersonalWordOp personalWordOp = new PersonalWordOp();
                    StringBuilder sb = new StringBuilder();
                    for (Word temp : deleteList) {
                        personalWordOp.tryToDeleteWord(temp.getWord(), AccountManager.instance.getUserId());
                        sb.append(temp.getWord()).append(',');
                    }
                    synchroYun(sb.toString());
                    deleteToNormal();
                    getDataList();
                    buildAdapter();
                    if (deleteList.size() != 0)
                        CustomToast.INSTANCE.showToast(R.string.wordlist_delete);
                } else {
                    if (AccountManager.instance.checkUserLogin()) {
                        synchroFromNet(1);
                    } else {
                        CustomDialog.showLoginDialog(context);
                    }
                }
            }
        });
        wordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalToDelete();
                buildDeleteAdapter();
            }
        });
        wordSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, WordSetActivity.class));
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        wordStatistic.setText(context.getString(R.string.word_statistic, wordArrayList.size()));
        toolbarOper.setText(R.string.word_synchro);
        title.setText(R.string.word_list_title);
    }

    protected void changeUIResumeByPara() {
        getDataList();
        buildAdapter();
        wordStatistic.setText(context.getString(R.string.word_statistic, wordArrayList.size()));
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        wordExpandableAdapter.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        wordExpandableAdapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onListItemExpanded(int position) {

    }

    @Override
    public void onListItemCollapsed(int position) {

    }

    @Override
    public void onItemClick(View view, Object object) {
        Intent intent = new Intent(context, WordContentActivity.class);
        intent.putExtra("word", object.toString());
        intent.putExtra("source", "wordlist");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (deleteMode) {
            deleteToNormal();
        } else {
            super.onBackPressed();
        }
    }

    private void buildAdapter() {
        wordExpandableAdapter = new WordExpandableAdapter(context, wordParents);
        wordExpandableAdapter.setExpandCollapseListener(this);
        wordExpandableAdapter.setItemClickListener(this);
        wordList.setAdapter(wordExpandableAdapter);
    }

    private void buildDeleteAdapter() {
        wordExpandableAdapter = new WordExpandableAdapter(context, true, wordParents);
        wordExpandableAdapter.setExpandCollapseListener(this);
        wordList.setAdapter(wordExpandableAdapter);
    }

    private void deleteToNormal() {
        deleteMode = false;
        toolbarOper.setText(R.string.word_synchro);
        statusBar.setVisibility(View.VISIBLE);
    }

    private void normalToDelete() {
        deleteMode = true;
        toolbarOper.setText(R.string.app_del);
        statusBar.setVisibility(View.GONE);
    }

    private void getPersonalDataList() {
        if (AccountManager.instance.checkUserLogin()) {
            wordArrayList = new PersonalWordOp().findDataByAll(AccountManager.instance.getUserId());
        } else {
            wordArrayList = new PersonalWordOp().findDataByAll("0");
        }
    }

    private void sortWordList(int order) {
        if (order == -1) {
            order = SettingConfigManager.instance.getWordOrder();
        }
        if (order == 1) {
            Collections.sort(wordArrayList, new Comparator<Word>() {
                public int compare(Word arg0, Word arg1) {
                    return arg1.getCreateDate().compareTo(arg0.getCreateDate());
                }
            });
        } else {
            Collections.sort(wordArrayList, new Comparator<Word>() {
                public int compare(Word arg0, Word arg1) {
                    return arg0.getWord().compareTo(arg1.getWord());
                }
            });
        }
    }

    private void getDataList() {
        getPersonalDataList();
        if (wordArrayList.size() == 0) {
            CustomToast.INSTANCE.showToast(R.string.word_no_collect);
        } else {
            sortWordList(-1);
            wordParents = WordParent.generateWordParent(wordArrayList);
        }
    }

    private void synchroFromNet(final int page) {
        waitingDialog.show();
        DictSynchroRequest.getInstance().exeRequest(DictSynchroRequest.getInstance().generateUrl(
                AccountManager.instance.getUserId(), page), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
                waitingDialog.dismiss();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
                waitingDialog.dismiss();
            }

            @Override
            public void response(Object object) {
                waitingDialog.dismiss();
                wordArrayList.addAll((ArrayList<Word>) object);
                if (DictSynchroRequest.getInstance().getTotalPage() != DictSynchroRequest.getInstance().getCurrentPage()) {
                    final MaterialDialog dialog = new MaterialDialog(context);
                    dialog.setTitle(R.string.word_list_title);
                    dialog.setMessage(context.getString(R.string.word_synchro_finish, wordArrayList.size(), DictSynchroRequest.getInstance().getCounts() - wordArrayList.size()));
                    dialog.setPositiveButton(R.string.word_synchro_continue, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            synchroFromNet(DictSynchroRequest.getInstance().getCurrentPage() + 1);
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveData();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    CustomToast.INSTANCE.showToast(R.string.word_synchro_final);
                    saveData();
                }
            }
        });
    }

    private void saveData() {
        //去重
        removeRepeated();
        sortWordList(-1);
        wordStatistic.setText(context.getString(R.string.word_statistic, wordArrayList.size()));
        wordParents = WordParent.generateWordParent(wordArrayList);
        buildAdapter();
        new PersonalWordOp().saveData(wordArrayList);
    }

    private void removeRepeated() {
        ArrayList<Word> tmpArr = new ArrayList<>();
        for (int i = 0; i < wordArrayList.size(); i++) {
            if (!tmpArr.contains(wordArrayList.get(i))) {
                tmpArr.add(wordArrayList.get(i));
            }
        }
        wordArrayList = new ArrayList<>();
        wordArrayList.addAll(tmpArr);
    }

    private void synchroYun(final String keyword) {
        final String userid = AccountManager.instance.getUserId();
        DictUpdateRequest.exeRequest(DictUpdateRequest.generateUrl(userid, "delete", keyword),
                new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {
                        CustomToast.INSTANCE.showToast(msg);
                    }

                    @Override
                    public void onServerError(String msg) {
                        CustomToast.INSTANCE.showToast(msg);
                    }

                    @Override
                    public void response(Object object) {
                        new PersonalWordOp().deleteWord(userid);
                    }
                });
    }
}
