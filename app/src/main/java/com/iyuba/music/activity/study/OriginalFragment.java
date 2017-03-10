package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.LrcParser;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.entity.original.OriginalParser;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.WordCard;
import com.iyuba.music.widget.original.OriginalView;
import com.iyuba.music.widget.original.TextSelectCallBack;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class OriginalFragment extends BaseFragment implements IOnClickListener {
    private OriginalView originalView;
    private ArrayList<Original> originalList;
    private WordCard wordCard;
    private Article article;

    private Runnable loadLocalLrcFile = new Runnable() {
        @Override
        public void run() {
            getOriginal();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.original, null);

        originalView = (OriginalView) view.findViewById(R.id.original);
        originalView.setTextSize(SettingConfigManager.getInstance().getOriginalSize());
        originalView.setTextSelectCallBack(new TextSelectCallBack() {
            @Override
            public void onSelectText(String text) {
                if (TextUtils.isEmpty(text)) {
                    CustomToast.getInstance().showToast(R.string.word_select_null);
                } else {
                    if (!wordCard.isShowing()) {
                        wordCard.show();
                    }
                    wordCard.resetWord(text);
                }
            }
        });
        wordCard = (WordCard) view.findViewById(R.id.wordcard);
        refresh();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wordCard.destory();
    }

    public void refresh() {
        article = StudyManager.getInstance().getCurArticle();
        getOriginal();
    }

    private void getOriginal() {
        if (StudyManager.getInstance().getMusicType() == 0) {//原唱
            if (LrcParser.getInstance().fileExist(article.getId())) {
                LrcParser.getInstance().getOriginal(article.getId(), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        originalList = (ArrayList<Original>) object;
                        if (SettingConfigManager.getInstance().getStudyTranslate() == 1) {
                            originalView.setShowChinese(true);
                        } else {
                            originalView.setShowChinese(false);
                        }
                        originalView.setOriginalList(originalList);
                    }

                    @Override
                    public void fail(Object object) {
                        reloadLocalData();
                    }
                });
            } else {
                reloadLocalData();
            }
        } else {
            if (OriginalParser.getInstance().fileExist(article.getId())) {
                OriginalParser.getInstance().getOriginal(article.getId(), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        originalList = (ArrayList<Original>) object;
                        originalView.setShowChinese(false);
                        originalView.setOriginalList(originalList);
                    }

                    @Override
                    public void fail(Object object) {
                        reloadLocalData();
                    }
                });
            } else {
                reloadLocalData();
            }
        }
    }

    private void reloadLocalData() {
        originalView.postDelayed(loadLocalLrcFile, 1000);
    }

    @Override
    public boolean onBackPressed() {
        if (wordCard.isShowing()) {
            wordCard.dismiss();
            return true;
        } else {
            originalView.removeCallbacks(loadLocalLrcFile);
            return false;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getActivity() != null) {
            getActivity().findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, getActivity().getString(R.string.list_double)));
        }
    }

    @Override
    public void onClick(View view, Object message) {
        originalView.setScrollY(0);
    }
}
