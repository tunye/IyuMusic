package com.iyuba.music.ground;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.network.NetWorkState;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.util.PermissionPool;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.request.newsrequest.ReadCountAddRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.ShareDialog;
import com.iyuba.music.widget.dialog.WordCard;
import com.iyuba.music.widget.imageview.MorphButton;
import com.iyuba.music.widget.original.HighLightTextCallBack;
import com.iyuba.music.widget.original.OriginalSynView;
import com.iyuba.music.widget.original.SeekToCallBack;
import com.iyuba.music.widget.original.TextSelectCallBack;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.player.VideoView;

import java.util.ArrayList;
import java.util.List;


public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {
    private static final int PLAY_HANGLER_WHAT = 0;
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
    private TextView subtitle;
    private ArrayList<Original> originalList;
    private ImageView largePause;
    private MorphButton playSound;
    private ImageView former, latter, playMode, studyTranslate, changescreen, subtitlteimg;
    private RelativeLayout video_layout;
    private View toorbar, menu, seekbar_layout, video_content_layout, ll_play_state_info;
    private boolean isfullscreen = false;
    private boolean isplay = true;
    private boolean isshowcontrol = true;
    private boolean isshowchinese = false;

    @Override
    public int getLayoutId() {
        return R.layout.videoplayer;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        StandardPlayer player = Utils.getMusicApplication().getPlayerService().getPlayer();
        if (player != null && player.isPlaying()) {
            sendBroadcast(new Intent("iyumusic.pause"));
            isSystemPlaying = true;
        } else {
            isSystemPlaying = false;
        }
        articles = (ArrayList<Article>) getIntent().getSerializableExtra("articleList");
        currPos = getIntent().getIntExtra("pos", 0);
    }

    @Override
    public void afterSetLayout() {
        super.afterSetLayout();
        permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onBackPressed() {
        if (wordCard.isShown()) {
            wordCard.dismiss();
        } else {
            StandardPlayer player = Utils.getMusicApplication().getPlayerService().getPlayer();
            if (player != null && isSystemPlaying) {
                sendBroadcast(new Intent("iyumusic.pause"));
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
            isplay = true;
        }
        setPauseImage();
        handler.removeMessages(PLAY_HANGLER_WHAT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
        handler.removeCallbacksAndMessages(null);
        wordCard.destroy();
    }

    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        currTime = findViewById(R.id.study_current_time);
        duration = findViewById(R.id.study_duration);
        seekBar = findViewById(R.id.study_progress);
        playSound = findViewById(R.id.play);
        former = findViewById(R.id.formmer);
        latter = findViewById(R.id.latter);
        playMode = findViewById(R.id.play_mode);
        studyTranslate = findViewById(R.id.translate);
        wordCard = findViewById(R.id.wordcard);
        videoView = findViewById(R.id.videoView_small);
        originalView = findViewById(R.id.original);
        subtitle = findViewById(R.id.tv_zimu);
        subtitle.setVisibility(View.GONE);
        largePause = findViewById(R.id.large_pause);
        playSound.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(), PorterDuff.Mode.SRC_IN);
        video_layout = findViewById(R.id.video_layout);
        changescreen = findViewById(R.id.change_screen);
        toorbar = findViewById(R.id.toolbar);
        menu = findViewById(R.id.meun_layout);
        seekbar_layout = findViewById(R.id.seekbar_layout);
        video_content_layout = findViewById(R.id.video_content_layout);
        subtitlteimg = findViewById(R.id.change_zimu);
        ll_play_state_info = findViewById(R.id.ll_play_state_info);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                ShareDialog shareDialog = new ShareDialog(VideoPlayerActivity.this, article);
                shareDialog.show();
            }
        });
        subtitlteimg.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                int musicTranslate = ConfigManager.getInstance().getStudyTranslate();
                musicTranslate = (musicTranslate + 1) % 2;
                ConfigManager.getInstance().setStudyTranslate(musicTranslate);
                setStudyTranslateImage(musicTranslate);
                if (musicTranslate == 1) {
                    originalView.setShowChinese(true);
                    isshowchinese = true;
                } else {
                    originalView.setShowChinese(false);
                    isshowchinese = false;
                }
                originalView.setOriginalList(originalList);
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setScreen();
                int i = videoView.getDuration();
                seekBar.setMax(i);
                duration.setText(DateFormat.formatTime(i / 1000));
                handler.sendEmptyMessage(PLAY_HANGLER_WHAT);
                videoView.start();
                largePause.setVisibility(View.GONE);
                playSound.setState(MorphButton.PLAY_STATE);
            }
        });
        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (percent > 1) {
                    findViewById(R.id.videoView_loading).setVisibility(View.GONE);
                }
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
        largePause.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                pause();
            }
        });
        findViewById(R.id.video_layout).setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                isshowcontrol = !isshowcontrol;
                if (isshowcontrol) {
                    ll_play_state_info.setVisibility(View.VISIBLE);
                    largePause.setVisibility(View.VISIBLE);
                } else {
                    ll_play_state_info.setVisibility(View.GONE);
                    largePause.setVisibility(View.GONE);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if (user) {
                    currTime.setTextColor(GetAppColor.getInstance().getAppColor());
                    duration.setTextColor(GetAppColor.getInstance().getAppColor());
                    videoView.seekTo(progress);
                } else {
                    currTime.setTextColor(GetAppColor.getInstance().getAppColorLight());
                    duration.setTextColor(GetAppColor.getInstance().getAppColorLight());
                }
                currTime.setText(DateFormat.formatTime(progress / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        originalView.setHighLightTextCallBack(new HighLightTextCallBack() {
            @Override
            public void getCurrentHighLightText(String content) {
                if (subtitle != null) {
                    String[] zimu = content.split("\n");
                    if (content != null && !"".equals(content)) {
                        if (isshowchinese)
                            subtitle.setText(content);
                        else
                            subtitle.setText(zimu[0]);
                    }
                }
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
                handler.removeMessages(PLAY_HANGLER_WHAT);
            }

            @Override
            public void onSeekTo(double time) {
                videoView.seekTo((int) (time * 1000));
                handler.sendEmptyMessage(PLAY_HANGLER_WHAT);
            }
        });
        changescreen.setOnClickListener(this);
        playMode.setOnClickListener(this);
        playSound.setOnClickListener(this);
        former.setOnClickListener(this);
        latter.setOnClickListener(this);
        studyTranslate.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        enableToolbarOper(R.string.study_share);
        refresh();
    }

    public void onActivityResumed() {
        setPauseImage();
        setPlayModeImage(ConfigManager.getInstance().getStudyPlayMode());
        setStudyTranslateImage(ConfigManager.getInstance().getStudyTranslate());
    }

    private void refresh() {
        article = articles.get(currPos);
        findViewById(R.id.videoView_loading).setVisibility(View.VISIBLE);
        LocalInfoOp localInfoOp = new LocalInfoOp();
        localInfoOp.updateSee(article.getId(), article.getApp());
        RequestClient.requestAsync(new ReadCountAddRequest(article.getId(), "music"), new SimpleRequestCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
        getOriginal();
        seekBar.setSecondaryProgress(0);
        videoView.reset();
        title.setText(article.getTitle_cn());
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            String append = "http://staticvip.iyuba.cn/video/voa/" + articles.get(currPos).getId() + ".mp4";
            videoView.setVideoPath(append);
        } else if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            CustomToast.getInstance().showToast(R.string.net_speed_slow);
        } else {
            CustomToast.getInstance().showToast(R.string.no_internet);
        }
    }

    private void setScreen() {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int width = RuntimeManager.getInstance().getScreenWidth() - RuntimeManager.getInstance().dip2px(20);
            int height = width * videoView.getVideoHeight() / videoView.getVideoWidth();
            videoView.setVideoScale(width, height);
        }
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
            isplay = false;
        } else {
            videoView.start();
            isplay = true;
        }
        Log.e("是否暂停：", isplay + "");
        setPauseImage();
    }

    private void setPauseImage() {
        if (videoView.isPlaying()) {
            playSound.setState(MorphButton.PLAY_STATE);
            largePause.setVisibility(View.GONE);
        } else {
            playSound.setState(MorphButton.PAUSE_STATE);
            largePause.setVisibility(View.VISIBLE);
        }
    }


    private void setStudyTranslateImage(int state) {
        switch (state) {
            case 1:
                studyTranslate.setImageResource(R.drawable.video_translate);
                subtitlteimg.setImageResource(R.drawable.change_select);
                break;
            case 0:
                studyTranslate.setImageResource(R.drawable.video_untranslate);
                subtitlteimg.setImageResource(R.drawable.change);
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
                if (ConfigManager.getInstance().getStudyTranslate() == 1) {
                    originalView.setShowChinese(true);
                } else {
                    originalView.setShowChinese(false);
                }
                originalView.setOriginalList(originalList);
                handler.sendEmptyMessage(PLAY_HANGLER_WHAT);
            }
        });
    }

    private void getWebLrc(final int id, final IOperationFinish finish) {
        RequestClient.requestAsync(new LrcRequest(id, 0), new SimpleRequestCallBack<BaseListEntity<List<Original>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Original>> listEntity) {
                originalList = (ArrayList<Original>) listEntity.getData();
                for (Original original : originalList) {
                    original.setArticleID(id);
                    if (TextUtils.isEmpty(original.getSentence_cn())) {
                        original.setSentence_cn(original.getSentence_cn_backup());
                    }
                }
                finish.finish();
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (INoDoubleClick.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.play_mode:
                int nextMusicType = ConfigManager.getInstance().getStudyPlayMode();
                nextMusicType = (nextMusicType + 1) % 3;
                ConfigManager.getInstance().setStudyPlayMode(nextMusicType);
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
                if (currPos < 0)
                    currPos = articles.size() - 1;
                refresh();
                break;
            case R.id.translate:
                int musicTranslate = ConfigManager.getInstance().getStudyTranslate();
                musicTranslate = (musicTranslate + 1) % 2;
                ConfigManager.getInstance().setStudyTranslate(musicTranslate);
                setStudyTranslateImage(musicTranslate);
                if (musicTranslate == 1) {
                    originalView.setShowChinese(true);
                    isshowchinese = true;
                } else {
                    originalView.setShowChinese(false);
                    isshowchinese = false;
                }
                originalView.setOriginalList(originalList);
                break;
            case R.id.change_screen:
                isplay = true;
                playSound.setState(MorphButton.PLAY_STATE);
                largePause.setVisibility(View.GONE);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //变成竖屏
                    isfullscreen = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //变成横屏了
                    isfullscreen = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //变成竖屏
                isfullscreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
        //继续执行父类其他点击事件
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //变成横屏了
            toorbar.setVisibility(View.GONE);
            subtitle.setVisibility(View.VISIBLE);
//            iv_change_subtitle_type.setVisibility(View.VISIBLE);
//            iv_fullscreen.setImageResource(R.drawable.small_screen);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //setVideoParams(videoView.getmMediaPlayer(), true);
            ViewGroup.LayoutParams rl_paramters = video_layout.getLayoutParams();
            rl_paramters.height = LinearLayout.LayoutParams.MATCH_PARENT;
            rl_paramters.width = LinearLayout.LayoutParams.MATCH_PARENT;
            video_layout.setLayoutParams(rl_paramters);
            originalView.setVisibility(View.GONE);
            video_content_layout.setBackgroundColor(Color.BLACK);
            seekbar_layout.setBackgroundColor(Color.parseColor("#65000000"));
            menu.setVisibility(View.GONE);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //变成竖屏了
//            iv_change_subtitle_type.setVisibility(View.GONE);
            subtitle.setVisibility(View.GONE);
            originalView.setVisibility(View.VISIBLE);
            toorbar.setVisibility(View.VISIBLE);
//            iv_fullscreen.setImageResource(R.drawable.full_screen);
            menu.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams rl_paramters = video_layout.getLayoutParams();
            rl_paramters.height = RuntimeManager.getInstance().dip2px(210.0f);
            rl_paramters.width = LinearLayout.LayoutParams.MATCH_PARENT;
            video_layout.setLayoutParams(rl_paramters);
            seekbar_layout.setBackgroundColor(Color.WHITE);
            video_content_layout.setBackgroundColor(Color.WHITE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //setVideoParams(videoView.getmMediaPlayer(), false);
        }

    }

    @Override
    public void onRequestPermissionDenied(String dialogContent, int[] codes, String[] permissions) {
        super.onRequestPermissionDenied(dialogContent, codes, permissions);
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.storage_permission);
        materialDialog.setMessage(R.string.storage_permission_content);
        materialDialog.setPositiveButton(R.string.app_sure, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                materialDialog.dismiss();
                permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        materialDialog.show();
    }

    public void setVideoParams(MediaPlayer mediaPlayer, boolean isLand) {
        //获取surfaceView父布局的参数
        ViewGroup.LayoutParams rl_paramters = video_layout.getLayoutParams();
        //获取SurfaceView的参数
        ViewGroup.LayoutParams sv_paramters = videoView.getLayoutParams();
        //设置宽高比为16/9
        float screen_widthPixels = getResources().getDisplayMetrics().widthPixels;
        float screen_heightPixels = getResources().getDisplayMetrics().widthPixels * 9f / 16f;
        //取消全屏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (isLand) {
            screen_heightPixels = getResources().getDisplayMetrics().heightPixels;
            //设置全屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        rl_paramters.width = (int) screen_widthPixels;
        rl_paramters.height = (int) screen_heightPixels;
        //获取MediaPlayer的宽高
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float video_por;
        try {
            video_por = videoWidth / videoHeight;
        } catch (Exception e) {
            video_por = 0;
        }
        float screen_por;
        try {
            screen_por = screen_widthPixels / screen_heightPixels;
        } catch (Exception e) {
            screen_por = 0;
        }

        //16:9    16:12
        if (screen_por > video_por) {
            sv_paramters.height = (int) screen_heightPixels;
            sv_paramters.width = (int) (screen_heightPixels * screen_por);
        } else {
            //16:9  19:9
            sv_paramters.width = (int) screen_widthPixels;
            sv_paramters.height = (int) (screen_widthPixels / screen_por);
        }
        video_layout.setLayoutParams(rl_paramters);
        videoView.setLayoutParams(sv_paramters);
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<VideoPlayerActivity> {
        @Override
        public void handleMessageByRef(final VideoPlayerActivity activity, Message msg) {
            activity.currTime.setText(DateFormat.formatTime(activity.videoView.getCurrentPosition() / 1000));
            activity.seekBar.setProgress(activity.videoView.getCurrentPosition());
            int current = activity.videoView.getCurrentPosition();
            if (activity.originalView != null)
                activity.originalView.synchroParagraph(activity.getCurrentPara(current / 1000.0));
            activity.handler.sendEmptyMessageDelayed(PLAY_HANGLER_WHAT, 200);
        }
    }
}
