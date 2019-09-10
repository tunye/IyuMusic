package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buaa.ct.core.listener.IOnClickListener;
import com.buaa.ct.core.listener.IOnDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.LrcMaker;
import com.iyuba.music.entity.original.LrcParser;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.entity.original.OriginalMaker;
import com.iyuba.music.entity.original.OriginalParser;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.request.newsrequest.OriginalRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.dialog.WordCard;
import com.iyuba.music.widget.original.OriginalSynView;
import com.iyuba.music.widget.original.SeekToCallBack;
import com.iyuba.music.widget.original.TextSelectCallBack;
import com.iyuba.music.widget.player.StandardPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/12/17.
 */
public class OriginalSynFragment extends BaseFragment implements IOnClickListener {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private OriginalSynView originalView;
    private List<Original> originalList;
    private WordCard wordCard;
    private Article article;
    private StandardPlayer player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = initView();
        refresh();
        return view;
    }

    private View initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.original_syn, null);
        player = Utils.getMusicApplication().getPlayerService().getPlayer();
        originalView = view.findViewById(R.id.original);
        originalView.setTextSize(ConfigManager.getInstance().getOriginalSize());
        originalView.setTextSelectCallBack(new TextSelectCallBack() {
            @Override
            public void onSelectText(String text) {
                if (TextUtils.isEmpty(text)) {
                    CustomToast.getInstance().showToast(R.string.word_select_suggest);
                } else {
                    if (!wordCard.isShowing()) {
                        wordCard.show();
                    }
                    wordCard.resetWord(text);
                }
            }
        });
        originalView.setSeekToCallBack(new SeekToCallBack() {
            @Override
            public void onSeekStart() {
                handler.removeMessages(0);
            }

            @Override
            public void onSeekTo(double time) {
                player.seekTo((int) (time * 1000));
                handler.sendEmptyMessage(0);
            }
        });
        wordCard = view.findViewById(R.id.wordcard);
        return view;
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
                                handler.obtainMessage(1, object).sendToTarget();
                            }

                            @Override
                            public void fail(Object object) {
                                getWebLrc(article.getId(), new IOperationFinish() {
                                    @Override
                                    public void finish() {
                                        if (ConfigManager.getInstance().getStudyTranslate() == 1) {
                                            originalView.setShowChinese(true);
                                        } else {
                                            originalView.setShowChinese(false);
                                        }
                                        originalView.setOriginalList(originalList);
                                        handler.sendEmptyMessage(0);
                                    }
                                });
                            }
                        });
                    }
                });
            } else {
                getWebLrc(article.getId(), new IOperationFinish() {
                    @Override
                    public void finish() {
                        if (ConfigManager.getInstance().getStudyTranslate() == 1) {
                            originalView.setShowChinese(true);
                        } else {
                            originalView.setShowChinese(false);
                        }
                        originalView.setOriginalList(originalList);
                        handler.sendEmptyMessage(0);
                    }
                });
            }
        } else {
            if (OriginalParser.getInstance().fileExist(article.getId())) {
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        OriginalParser.getInstance().getOriginal(article.getId(), new IOperationResult() {
                            @Override
                            public void success(Object object) {
                                handler.obtainMessage(2, object).sendToTarget();
                            }

                            @Override
                            public void fail(Object object) {
                                getWebOriginal(article.getId(), new IOperationFinish() {
                                    @Override
                                    public void finish() {
                                        originalView.setShowChinese(false);
                                        originalView.setOriginalList(originalList);
                                        handler.sendEmptyMessage(0);
                                    }
                                });
                            }
                        });
                    }
                });
            } else {
                getWebOriginal(article.getId(), new IOperationFinish() {
                    @Override
                    public void finish() {
                        originalView.setShowChinese(false);
                        originalView.setOriginalList(originalList);
                        handler.sendEmptyMessage(0);
                    }
                });
            }
        }

    }

    private void getWebLrc(final int id, final IOperationFinish finish) {
        int type;
        switch (StudyManager.getInstance().getApp()) {
            case "215":
            case "221":
            case "231":
                type = 1;
                break;
            case "209":
                type = 2;
                break;
            default:
                type = 0;
                break;
        }
        RequestClient.requestAsync(new LrcRequest(id, type), new SimpleRequestCallBack<BaseListEntity<List<Original>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Original>> listEntity) {
                originalList = listEntity.getData();
                for (Original original : originalList) {
                    original.setArticleID(id);
                    if (TextUtils.isEmpty(original.getSentence_cn())) {
                        original.setSentence_cn(original.getSentence_cn_backup());
                    }
                }
                finish.finish();
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        LrcMaker.getInstance().makeOriginal(id, originalList);
                    }
                });
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private void getWebOriginal(final int id, final IOperationFinish finish) {
        RequestClient.requestAsync(new OriginalRequest(id), new SimpleRequestCallBack<BaseListEntity<List<Original>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Original>> listEntity) {
                originalList = listEntity.getData();
                for (Original original : originalList) {
                    original.setArticleID(id);
                }
                finish.finish();
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        OriginalMaker.getInstance().makeOriginal(id, originalList);
                    }
                });
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private int getCurrentPara(double time) {
        int para = 0;
        if (originalList != null && originalList.size() != 0) {
            for (Original original : originalList) {
                if (time < original.getStartTime()) {
                    break;
                } else {
                    para++;
                }
            }
        }
        return para;
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
        if (wordCard.isShowing()) {
            wordCard.dismiss();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player.isPrepared()) {
            if (originalView.getOriginalList() != null && originalView.getOriginalList().size() != 0) {
                handler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wordCard.destroy();
        originalView.destroy();
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
        player.seekTo(0);
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<OriginalSynFragment> {
        @Override
        public void handleMessageByRef(final OriginalSynFragment fragment, Message msg) {
            switch (msg.what) {
                case 0:
                    if (fragment.player != null) {
                        int current = fragment.player.getCurrentPosition();
                        fragment.originalView.synchroParagraph(fragment.getCurrentPara(current / 1000.0));
                        fragment.handler.sendEmptyMessageDelayed(0, 500);
                    }
                    break;
                case 1:
                    fragment.originalList = (ArrayList<Original>) msg.obj;
                    if (ConfigManager.getInstance().getStudyTranslate() == 1) {
                        fragment.originalView.setShowChinese(true);
                    } else {
                        fragment.originalView.setShowChinese(false);
                    }
                    fragment.originalView.setOriginalList(fragment.originalList);
                    fragment.handler.sendEmptyMessage(0);
                    break;
                case 2:
                    fragment.originalList = (ArrayList<Original>) msg.obj;
                    fragment.originalView.setShowChinese(false);
                    fragment.originalView.setOriginalList(fragment.originalList);
                    fragment.handler.sendEmptyMessage(0);
                    break;
            }
        }
    }
}
