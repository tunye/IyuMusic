package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.discover.WordExpandableAdapter;
import com.iyuba.music.adapter.expandable.Adapter.ExpandableRecyclerAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.word.PersonalWordOp;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.entity.word.WordParent;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.OnExpandableRecycleViewClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.request.discoverrequest.DictSynchroRequest;
import com.iyuba.music.request.discoverrequest.DictUpdateRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 10202 on 2015/12/2.
 */
public class WordListActivity extends BaseActivity implements ExpandableRecyclerAdapter.ExpandCollapseListener, OnExpandableRecycleViewClickListener {
    private View statusBar;
    private TextView wordEdit, wordSet, wordStatistic;
    private RecyclerView wordList;
    private WordExpandableAdapter wordExpandableAdapter;
    private List<Word> wordArrayList;
    private List<WordParent> wordParents;
    private List<Word> deleteList;
    private IyubaDialog waitingDialog;
    private boolean deleteMode;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        wordParents = new ArrayList<>();
        wordArrayList = new ArrayList<>();
    }

    @Override
    public int getLayoutId() {
        return R.layout.word_list;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        statusBar = findViewById(R.id.statusbar);
        wordList = findViewById(R.id.wordlist);
        toolbarOper = findViewById(R.id.toolbar_oper);
        wordEdit = findViewById(R.id.word_edit);
        wordSet = findViewById(R.id.word_set);
        wordStatistic = findViewById(R.id.word_statistic);
        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordList.addItemDecoration(new DividerItemDecoration());
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.word_synchroing));
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (deleteMode) {
                    deleteList = wordExpandableAdapter.getTryTodeleteList();
                    PersonalWordOp personalWordOp = new PersonalWordOp();
                    StringBuilder sb = new StringBuilder();
                    for (Word temp : deleteList) {
                        personalWordOp.tryToDeleteWord(temp.getWord(), AccountManager.getInstance().getUserId());
                        sb.append(temp.getWord()).append(',');
                    }
                    synchroYun(sb.toString());
                    deleteToNormal();
                    getDataList();
                    buildAdapter();
                    if (deleteList.size() != 0) {
                        CustomToast.getInstance().showToast(R.string.wordlist_delete);
                        wordStatistic.setText(context.getString(R.string.word_statistic, wordArrayList.size()));
                    }
                } else {
                    if (AccountManager.getInstance().checkUserLogin()) {
                        synchroFromNet(1);
                    } else {
                        CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                            @Override
                            public void finish() {
                                synchroFromNet(1);
                            }
                        });
                    }
                }
            }
        });
        wordEdit.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                normalToDelete();
                buildDeleteAdapter();
            }
        });
        wordSet.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, WordSetActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        wordStatistic.setText(context.getString(R.string.word_statistic, wordArrayList.size()));
        enableToolbarOper(R.string.word_synchro);
        title.setText(R.string.word_list_title);
    }

    public void onActivityResumed() {
        getDataList();
        buildAdapter();
        wordStatistic.setText(context.getString(R.string.word_statistic, wordArrayList.size()));
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
        buildAdapter();
        enableToolbarOper(R.string.word_synchro);
        statusBar.setVisibility(View.VISIBLE);
    }

    private void normalToDelete() {
        deleteMode = true;
        enableToolbarOper(R.string.app_del);
        statusBar.setVisibility(View.GONE);
    }

    private void getPersonalDataList() {
        if (AccountManager.getInstance().checkUserLogin()) {
            wordArrayList = new PersonalWordOp().findDataByAll(AccountManager.getInstance().getUserId());
        } else {
            wordArrayList = new PersonalWordOp().findDataByAll("0");
        }
    }

    private void sortWordList(int order) {
        if (order == -1) {
            order = ConfigManager.getInstance().getWordOrder();
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
            CustomToast.getInstance().showToast(R.string.word_no_collect);
        } else {
            sortWordList(ConfigManager.getInstance().getWordOrder());
            wordParents = WordParent.generateWordParent(wordArrayList);
        }
    }

    private void synchroFromNet(final int page) {
        waitingDialog.show();
        RequestClient.requestAsync(new DictSynchroRequest(AccountManager.getInstance().getUserId(), page), new SimpleRequestCallBack<BaseListEntity<List<Word>>>() {
            @Override
            public void onSuccess(final BaseListEntity<List<Word>> baseListEntity) {
                waitingDialog.dismiss();
                wordArrayList.addAll(baseListEntity.getData());
                if (!baseListEntity.isLastPage()) {
                    final MyMaterialDialog dialog = new MyMaterialDialog(context);
                    dialog.setTitle(R.string.word_list_title);
                    dialog.setMessage(context.getString(R.string.word_synchro_finish, wordArrayList.size(), baseListEntity.getTotalCount() - wordArrayList.size()));
                    dialog.setPositiveButton(R.string.word_synchro_continue, new INoDoubleClick() {
                        @Override
                        public void activeClick(View view) {
                            synchroFromNet(baseListEntity.getCurPage() + 1);
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
                        @Override
                        public void activeClick(View view) {
                            saveData();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    CustomToast.getInstance().showToast(R.string.word_synchro_final);
                    saveData();
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                waitingDialog.dismiss();
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private void saveData() {
        //去重
        removeRepeated();
        sortWordList(ConfigManager.getInstance().getWordOrder());
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
        final String userid = AccountManager.getInstance().getUserId();
        RequestClient.requestAsync(new DictUpdateRequest(userid, "delete", keyword), new SimpleRequestCallBack<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                new PersonalWordOp().deleteWord(userid);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }
}
