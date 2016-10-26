package com.iyuba.music.activity.study;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.original.LrcMaker;
import com.iyuba.music.entity.original.LrcParser;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.entity.original.OriginalMaker;
import com.iyuba.music.entity.original.OriginalParser;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.request.newsrequest.OriginalRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.WordCard;
import com.iyuba.music.widget.original.OriginalSynView;
import com.iyuba.music.widget.original.SeekToCallBack;
import com.iyuba.music.widget.original.TextSelectCallBack;
import com.iyuba.music.widget.player.StandardPlayer;

import java.util.ArrayList;

import static com.iyuba.music.manager.RuntimeManager.getApplication;

/**
 * Created by 10202 on 2015/12/17.
 */
public class OriginalSynFragment extends BaseFragment {
    private OriginalSynView originalView;
    private ArrayList<Original> originalList;
    private WordCard wordCard;
    private Context context;
    private Article article;
    private StandardPlayer player;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    int current = player.getCurrentPosition();
                    originalView.synchroParagraph(getCurrentPara(current / 1000.0));
                    handler.sendEmptyMessageDelayed(0, 500);
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.original_syn, null);
        player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
        originalView = (OriginalSynView) view.findViewById(R.id.original);
        originalView.setTextSize(SettingConfigManager.instance.getOriginalSize());
        originalView.setTextSelectCallBack(new TextSelectCallBack() {
            @Override
            public void onSelectText(String text) {
                if (TextUtils.isEmpty(text)) {
                    CustomToast.INSTANCE.showToast(R.string.word_select_suggest);
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
        wordCard = (WordCard) view.findViewById(R.id.wordcard);
        refresh();
        return view;
    }

    public void refresh() {
        article = StudyManager.instance.getCurArticle();
        getOriginal();
    }

    private void getOriginal() {
        if (StudyManager.instance.getMusicType() == 0) {//原唱
            if (LrcParser.getInstance().fileExist(article.getId())) {
                LrcParser.getInstance().getOriginal(article.getId(), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        originalList = (ArrayList<Original>) object;
                        if (SettingConfigManager.instance.getStudyTranslate() == 1) {
                            originalView.setShowChinese(true);
                        } else {
                            originalView.setShowChinese(false);
                        }
                        originalView.setOriginalList(originalList);
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void fail(Object object) {
                        getWebLrc(article.getId(), new IOperationFinish() {
                            @Override
                            public void finish() {
                                if (SettingConfigManager.instance.getStudyTranslate() == 1) {
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
            } else {
                getWebLrc(article.getId(), new IOperationFinish() {
                    @Override
                    public void finish() {
                        if (SettingConfigManager.instance.getStudyTranslate() == 1) {
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
                OriginalParser.getInstance().getOriginal(article.getId(), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        originalList = (ArrayList<Original>) object;
                        originalView.setShowChinese(false);
                        originalView.setOriginalList(originalList);
                        handler.sendEmptyMessage(0);
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
        if (StudyManager.instance.getApp().equals("215") || StudyManager.instance.getApp().equals("221") || StudyManager.instance.getApp().equals("231")) {
            type = 1;
        } else if (StudyManager.instance.getApp().equals("209")) {
            type = 2;
        } else {
            type = 0;
        }
        LrcRequest.getInstance().exeRequest(LrcRequest.getInstance().generateUrl(id, type), new IProtocolResponse() {
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
                BaseListEntity listEntity = (BaseListEntity) object;
                originalList = (ArrayList<Original>) listEntity.getData();
                for (Original original : originalList) {
                    original.setArticleID(id);
                    if (TextUtils.isEmpty(original.getSentence_cn())) {
                        original.setSentence_cn(original.getSentence_cn_backup());
                    }
                }
                finish.finish();
                LrcMaker.getInstance().makeOriginal(id, originalList);
            }
        });
    }

    private void getWebOriginal(final int id, final IOperationFinish finish) {
        OriginalRequest.getInstance().exeRequest(OriginalRequest.getInstance().generateUrl(id), new IProtocolResponse() {
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
                BaseListEntity listEntity = (BaseListEntity) object;
                originalList = (ArrayList<Original>) listEntity.getData();
                for (Original original : originalList) {
                    original.setArticleID(id);
                }
                finish.finish();
                OriginalMaker.getInstance().makeOriginal(id, originalList);
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

    @Override
    public boolean onBackPressed() {
        if (wordCard.isShowing()) {
            wordCard.dismiss();
            return true;
        } else {
            handler.removeMessages(0);
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
        handler.removeMessages(0);
    }
}
