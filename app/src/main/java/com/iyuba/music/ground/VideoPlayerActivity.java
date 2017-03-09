package com.iyuba.music.ground;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.request.newsrequest.ReadCountAddRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.ShareDialog;
import com.iyuba.music.widget.dialog.WordCard;
import com.iyuba.music.widget.original.OriginalSynView;
import com.iyuba.music.widget.original.SeekToCallBack;
import com.iyuba.music.widget.original.TextSelectCallBack;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.player.VideoView;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;


public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {
    private static final int WRITE_EXTERNAL_TASK_CODE = 1;
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private boolean isSystemPlaying;
    private int currPos;
    private Article article;
    private ArrayList<Article> articles;
    private VideoView videoView;
    private WordCard wordCard;
    private TextView currTime, duration;
    private SeekBar seekBar;
    private OriginalSynView originalView;
    private ArrayList<Original> originalList;
    private ImageView largePause;
    private MorphButton playSound;
    private ImageView former, latter, playMode, studyTranslate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer);
        context = this;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_CODE);
        }
        StandardPlayer player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
        if (player != null && player.isPlaying()) {
            sendBroadcast(new Intent("iyumusic.pause"));
            isSystemPlaying = true;
        } else {
            isSystemPlaying = false;
        }
        articles = (ArrayList<Article>) getIntent().getSerializableExtra("articleList");
        currPos = getIntent().getIntExtra("pos", 0);
        article = articles.get(currPos);
        initWidget();
        setListener();
        changeUIByPara();
        refresh();
    }


    @Override
    public void onBackPressed() {
        if (wordCard.isShown()) {
            wordCard.dismiss();
        } else {
            StandardPlayer player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
            if (player != null && isSystemPlaying) {
                sendBroadcast(new Intent("iyumusic.pause"));
            }
            videoView.stopPlayback();
            handler.removeCallbacksAndMessages(null);
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wordCard.destory();
    }

    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        currTime = (TextView) findViewById(R.id.study_current_time);
        duration = (TextView) findViewById(R.id.study_duration);
        seekBar = (SeekBar) findViewById(R.id.study_progress);
        playSound = (MorphButton) findViewById(R.id.play);
        former = (ImageView) findViewById(R.id.formmer);
        latter = (ImageView) findViewById(R.id.latter);
        playMode = (ImageView) findViewById(R.id.play_mode);
        studyTranslate = (ImageView) findViewById(R.id.translate);
        wordCard = (WordCard) findViewById(R.id.wordcard);
        videoView = (VideoView) findViewById(R.id.videoView_small);
        originalView = (OriginalSynView) findViewById(R.id.original);
        largePause = (ImageView) findViewById(R.id.large_pause);
        playSound.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(context), PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog(VideoPlayerActivity.this, article);
                shareDialog.show();
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setScreen();
                int i = videoView.getDuration();
                seekBar.setMax(i);
                duration.setText(Mathematics.formatTime(i / 1000));
                handler.sendEmptyMessage(0);
                videoView.start();
                largePause.setVisibility(View.GONE);
                playSound.setState(MorphButton.MorphState.END);
            }
        });
        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent * seekBar.getMax() / 100);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currPos = (currPos + 1) % articles.size();
                refresh();
            }
        });
        findViewById(R.id.video_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if (user) {
                    currTime.setTextColor(GetAppColor.getInstance().getAppColor(context));
                    duration.setTextColor(GetAppColor.getInstance().getAppColor(context));
                    videoView.seekTo(progress);
                } else {
                    currTime.setTextColor(GetAppColor.getInstance().getAppColorLight(context));
                    duration.setTextColor(GetAppColor.getInstance().getAppColorLight(context));
                }
                currTime.setText(Mathematics.formatTime(progress / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        originalView.setTextSelectCallBack(new TextSelectCallBack() {
            @Override
            public void onSelectText(String text) {
                if ("".equals(text)) {
                    CustomToast.getInstance().showToast(R.string.word_select_null);
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
                videoView.seekTo((int) (time * 1000));
                handler.sendEmptyMessage(0);
            }
        });
        playMode.setOnClickListener(this);
        playSound.setOnClickListener(this);
        former.setOnClickListener(this);
        latter.setOnClickListener(this);
        studyTranslate.setOnClickListener(this);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.study_share);
    }

    protected void changeUIResumeByPara() {
        setPauseImage(false);
        setPlayModeImage(SettingConfigManager.getInstance().getStudyPlayMode());
        setStudyTranslateImage(SettingConfigManager.getInstance().getStudyTranslate());
    }

    private void refresh() {
        LocalInfoOp localInfoOp = new LocalInfoOp();
        localInfoOp.updateSee(article.getId(), article.getApp());
        ReadCountAddRequest.exeRequest(ReadCountAddRequest.generateUrl(article.getId(), "music"), null);
        getOriginal();
        videoView.reset();
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            String append = "http://staticvip.iyuba.com/video/voa/" + articles.get(currPos).getId() + ".mp4";
            videoView.setVideoPath(append);
        } else if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            CustomToast.getInstance().showToast(R.string.net_speed_slow);
        } else {
            CustomToast.getInstance().showToast(R.string.no_internet);
        }
    }

    private void setScreen() {
        int width = RuntimeManager.getWindowWidth() - RuntimeManager.dip2px(20);// 700
        int height = width * videoView.getVideoHeight() / videoView.getVideoWidth();
        videoView.setVideoScale(width, height);
    }

    private void setPlayModeImage(int state) {
        switch (state) {
            case 0:
                playMode.setImageResource(R.drawable.single_replay);
                break;
            case 1:
                playMode.setImageResource(R.drawable.list_play);
                break;
            case 2:
                playMode.setImageResource(R.drawable.random_play);
                break;
        }
    }

    private void pause() {
        if (videoView.isPlaying()) {
            videoView.pause();
        } else {
            videoView.start();
        }
        setPauseImage(true);
    }

    private void setPauseImage(boolean delay) {
        if (videoView.isPlaying()) {
            playSound.setState(MorphButton.MorphState.END, delay);
            largePause.setVisibility(View.GONE);
        } else {
            playSound.setState(MorphButton.MorphState.START, delay);
            largePause.setVisibility(View.VISIBLE);
        }
    }


    private void setStudyTranslateImage(int state) {
        switch (state) {
            case 1:
                studyTranslate.setImageResource(R.drawable.video_translate);
                break;
            case 0:
                studyTranslate.setImageResource(R.drawable.video_untranslate);
                break;
        }
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

    private void getOriginal() {
        getWebLrc(article.getId(), new IOperationFinish() {
            @Override
            public void finish() {
                if (SettingConfigManager.getInstance().getStudyTranslate() == 1) {
                    originalView.setShowChinese(true);
                } else {
                    originalView.setShowChinese(false);
                }
                originalView.setOriginalList(originalList);
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void getWebLrc(final int id, final IOperationFinish finish) {
        LrcRequest.exeRequest(LrcRequest.generateUrl(id, 0), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
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
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_mode:
                int nextMusicType = SettingConfigManager.getInstance().getStudyPlayMode();
                nextMusicType = (nextMusicType + 1) % 3;
                SettingConfigManager.getInstance().setStudyPlayMode(nextMusicType);
                StudyManager.getInstance().generateArticleList();
                setPlayModeImage(nextMusicType);
                break;
            case R.id.play:
                pause();
                break;
            case R.id.latter:
                currPos = (currPos + 1) % articles.size();
                refresh();
                break;
            case R.id.formmer:
                currPos = (currPos - 1) % articles.size();
                refresh();
                break;
            case R.id.translate:
                int musicTranslate = SettingConfigManager.getInstance().getStudyTranslate();
                musicTranslate = (musicTranslate + 1) % 2;
                SettingConfigManager.getInstance().setStudyTranslate(musicTranslate);
                setStudyTranslateImage(musicTranslate);
                if (musicTranslate == 1) {
                    originalView.setShowChinese(true);
                } else {
                    originalView.setShowChinese(false);
                }
                originalView.setOriginalList(originalList);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_TASK_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(VideoPlayerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_TASK_CODE);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<VideoPlayerActivity> {
        @Override
        public void handleMessageByRef(final VideoPlayerActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.currTime.setText(Mathematics.formatTime(activity.videoView.getCurrentPosition() / 1000));
                    activity.seekBar.setProgress(activity.videoView.getCurrentPosition());
                    int current = activity.videoView.getCurrentPosition();
                    activity.originalView.synchroParagraph(activity.getCurrentPara(current / 1000.0));
                    activity.handler.sendEmptyMessageDelayed(0, 1000);
                    break;
            }
        }
    }
}
