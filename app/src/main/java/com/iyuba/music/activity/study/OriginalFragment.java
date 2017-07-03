package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.WordCard;
import com.iyuba.music.widget.original.OriginalView;
import com.iyuba.music.widget.original.TextSelectCallBack;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class OriginalFragment extends BaseFragment implements IOnClickListener {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
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
        View view = initView();
        refresh();
        return view;
    }

    private View initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.original, null);
        originalView = (OriginalView) view.findViewById(R.id.original);
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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wordCard.destroy();
        originalView.removeCallbacks(loadLocalLrcFile);
        loadLocalLrcFile = null;
        originalView.destroy();
    }

    public void refresh() {
        if (originalView == null) {
            initView();
        }
        originalView.setTextSize(ConfigManager.getInstance().getOriginalSize());
        article = StudyManager.getInstance().getCurArticle();
        getOriginal();
    }

    private void getOriginal() {
        if (StudyManager.getInstance().getMusicType() == 0) {//原唱
            if (LrcParser.getInstance().fileExist(article.getId())) {
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        LrcParser.getInstance().getOriginal(article.getId(), new IOperationResult() {
                            @Override
                            public void success(Object object) {
                                handler.obtainMessage(0, object).sendToTarget();
                            }

                            @Override
                            public void fail(Object object) {
                                reloadLocalData();
                            }
                        });
                    }
                });

            } else {
                reloadLocalData();
            }
        } else {
            if (OriginalParser.getInstance().fileExist(article.getId())) {
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        OriginalParser.getInstance().getOriginal(article.getId(), new IOperationResult() {
                            @Override
                            public void success(Object object) {
                                handler.obtainMessage(1, object).sendToTarget();
                            }

                            @Override
                            public void fail(Object object) {
                                reloadLocalData();
                            }
                        });
                    }
                });
            } else {
                reloadLocalData();
            }
        }
    }

    private void reloadLocalData() {
        originalView.postDelayed(loadLocalLrcFile, 1500);
    }

    public void changeLanguage() {
        if (ConfigManager.getInstance().getStudyTranslate() == 1) {
            originalView.setShowChinese(true);
        } else {
            originalView.setShowChinese(false);
        }
        originalView.synchroLanguage();
    }

    @Override
    public boolean onBackPressed() {
        originalView.removeCallbacks(loadLocalLrcFile);
        if (wordCard.isShowing()) {
            wordCard.dismiss();
            return true;
        } else {
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

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<OriginalFragment> {
        @Override
        public void handleMessageByRef(OriginalFragment originalFragment, Message msg) {
            switch (msg.what) {
                case 0:
                    originalFragment.originalList = (ArrayList<Original>) msg.obj;
                    if (ConfigManager.getInstance().getStudyTranslate() == 1) {
                        originalFragment.originalView.setShowChinese(true);
                    } else {
                        originalFragment.originalView.setShowChinese(false);
                    }
                    originalFragment.originalView.setOriginalList(originalFragment.originalList);
                    break;
                case 1:
                    originalFragment.originalList = (ArrayList<Original>) msg.obj;
                    originalFragment.originalView.setShowChinese(false);
                    originalFragment.originalView.setOriginalList(originalFragment.originalList);
                    break;
            }
        }
    }
}
