package com.iyuba.music.activity.study;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.fragmentAdapter.StudyFragmentAdapter;
import com.iyuba.music.listener.ChangeUIBroadCast;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.request.newsrequest.CommentCountRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.StudyMore;
import com.iyuba.music.widget.imageview.PageIndicator;
import com.iyuba.music.widget.player.StandardPlayer;
import com.umeng.socialize.UMShareAPI;
import com.wnafee.vector.MorphButton;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyActivity extends BaseActivity implements View.OnClickListener {
    private static final int RECORD_AUDIO_TASK_CODE = 2;
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private StudyMore studyMoreDialog;
    private StandardPlayer player;
    private TextView currTime, duration;
    private ViewPager viewPager;
    private PageIndicator pageIndicator;
    private float lastChange = 0;
    private SeekBar seekBar;
    private MorphButton playSound;
    private ImageView former, latter, playMode, interval, studyMode, studyMore, studyTranslate;
    private RoundTextView comment;
    private int aPosition, bPosition;// 区间播放
    private IntervalState intervalState;
    private boolean isDestroyed = false;
    IPlayerListener iPlayerListener = new IPlayerListener() {
        @Override
        public void onPrepare() {
            int i = player.getDuration();
            seekBar.setMax(i);
            duration.setText(Mathematics.formatTime(i / 1000));
            handler.sendEmptyMessage(0);
            player.start();
            playSound.setState(MorphButton.MorphState.END, true);
        }

        @Override
        public void onBufferChange(int buffer) {
            seekBar.setSecondaryProgress(buffer * seekBar.getMax() / 100);
        }

        @Override
        public void onFinish() {
            startPlay();
            refresh(false);
        }

        @Override
        public void onError() {

        }
    };
    private StudyChangeUIBroadCast studyChangeUIBroadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.study);
        context = this;
        player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
        ((MusicApplication) getApplication()).getPlayerService().startPlay(
                StudyManager.instance.getCurArticle(), false);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_TASK_CODE);
        }
        initWidget();
        setListener();
        changeUIByPara();
        isDestroyed = false;
        initBroadCast();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
        if (player.isPrepared()) {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(studyChangeUIBroadCast);
        isDestroyed = true;
    }

    @Override
    public void onBackPressed() {
        if (studyMoreDialog.isShown()) {
            studyMoreDialog.dismiss();
        } else if (!((StudyFragmentAdapter) viewPager.getAdapter()).getCurrentFragment().onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_mode:
                int nextMusicType = SettingConfigManager.instance.getStudyPlayMode();
                nextMusicType = (nextMusicType + 1) % 3;
                SettingConfigManager.instance.setStudyPlayMode(nextMusicType);
                StudyManager.instance.generateArticleList();
                setPlayModeImage(nextMusicType);
                break;
            case R.id.play:
                setPauseImage(true);
                break;
            case R.id.study_mode:
                int musicType = SettingConfigManager.instance.getStudyMode();
                musicType = (musicType + 1) % 2;
                SettingConfigManager.instance.setStudyMode(musicType);
                setStudyModeImage(musicType);
                break;
            case R.id.interval:
                setIntervalImage(1);
                break;
            case R.id.latter:
                ((MusicApplication) getApplication()).getPlayerService().next(false);
                startPlay();
                refresh(false);
                break;
            case R.id.formmer:
                ((MusicApplication) getApplication()).getPlayerService().before();
                startPlay();
                refresh(false);
                break;
            case R.id.study_more:
                if (studyMoreDialog.isShown()) {
                    studyMoreDialog.dismiss();
                } else {
                    studyMoreDialog.show();
                }
                break;
            case R.id.study_translate:
                int musicTranslate = SettingConfigManager.instance.getStudyTranslate();
                musicTranslate = (musicTranslate + 1) % 2;
                SettingConfigManager.instance.setStudyTranslate(musicTranslate);
                setStudyTranslateImage(musicTranslate);
                break;
            case R.id.study_comment:
                if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
                    startActivity(new Intent(context, CommentActivity.class));
                } else {
                    CustomToast.INSTANCE.showToast(R.string.no_internet);
                }
                break;
        }
    }

    private void initBroadCast() {
        studyChangeUIBroadCast = new StudyChangeUIBroadCast();
        IntentFilter intentFilter = new IntentFilter("com.iyuba.music.study");
        registerReceiver(studyChangeUIBroadCast, intentFilter);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        studyMore = (ImageView) findViewById(R.id.study_more);
        viewPager = (ViewPager) findViewById(R.id.study_main);
        pageIndicator = (PageIndicator) findViewById(R.id.study_indicator);
        currTime = (TextView) findViewById(R.id.study_current_time);
        duration = (TextView) findViewById(R.id.study_duration);
        seekBar = (SeekBar) findViewById(R.id.study_progress);

        playSound = (MorphButton) findViewById(R.id.play);
        former = (ImageView) findViewById(R.id.formmer);
        latter = (ImageView) findViewById(R.id.latter);
        playMode = (ImageView) findViewById(R.id.play_mode);
        interval = (ImageView) findViewById(R.id.interval);
        comment = (RoundTextView) findViewById(R.id.study_comment);
        studyMode = (ImageView) findViewById(R.id.study_mode);
        studyTranslate = (ImageView) findViewById(R.id.study_translate);
        studyMoreDialog = new StudyMore(this);
        playSound.setForegroundColorFilter(GetAppColor.instance.getAppColor(context), PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void setListener() {
        super.setListener();
        ((MusicApplication) getApplication()).getPlayerService().setListener(iPlayerListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (lastChange != 0 && positionOffset != 0) {
                    if (lastChange > positionOffset) {//左滑
                        pageIndicator.setDirection(PageIndicator.Direction.LEFT);
                        pageIndicator.setMovePercent(position + 1, positionOffset);
                    } else {
                        pageIndicator.setDirection(PageIndicator.Direction.RIGHT);
                        pageIndicator.setMovePercent(position, positionOffset);
                    }
                }
                lastChange = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                pageIndicator.setDirection(PageIndicator.Direction.NONE);
                pageIndicator.setCurrentItem(viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if (user) {
                    currTime.setTextColor(GetAppColor.instance.getAppColor(context));
                    duration.setTextColor(GetAppColor.instance.getAppColor(context));
                    player.seekTo(progress);
                } else {
                    currTime.setTextColor(GetAppColor.instance.getAppColorLight(context));
                    duration.setTextColor(GetAppColor.instance.getAppColorLight(context));
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
        interval.setOnClickListener(this);
        playMode.setOnClickListener(this);
        playSound.setOnClickListener(this);
        former.setOnClickListener(this);
        latter.setOnClickListener(this);
        studyMore.setOnClickListener(this);
        studyMode.setOnClickListener(this);
        studyTranslate.setOnClickListener(this);
        comment.setOnClickListener(this);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        if (((MusicApplication) getApplication()).getPlayerService().getCurArticle().getId() == StudyManager.instance.getCurArticle().getId()) {
            int i = player.getDuration();
            seekBar.setMax(i);
            duration.setText(Mathematics.formatTime(i / 1000));
            handler.sendEmptyMessage(0);
        } else {
            ((MusicApplication) getApplication()).getPlayerService().setCurArticle(StudyManager.instance.getCurArticle());
        }
        setIntervalImage(0);
    }

    protected void changeUIResumeByPara() {
        setPlayModeImage(SettingConfigManager.instance.getStudyPlayMode());
        switch (StudyManager.instance.getMusicType()) {
            case 0:
                studyMode.setImageDrawable(context.getResources().getDrawable(R.drawable.study_annoucer_mode));
                studyTranslate.setVisibility(View.VISIBLE);
                break;
            case 1:
                studyMode.setImageDrawable(context.getResources().getDrawable(R.drawable.study_singer_mode));
                studyTranslate.setVisibility(View.GONE);
                break;
        }
        setPauseImage(false);
        refresh(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_TASK_CODE && grantResults.length == permissions.length
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

        } else {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(StudyActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RECORD_AUDIO_TASK_CODE);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
    }

    private void startPlay() {
        ((MusicApplication) getApplication()).getPlayerService().startPlay(
                StudyManager.instance.getCurArticle(), false);
        ((MusicApplication) getApplication()).getPlayerService().setCurArticle(StudyManager.instance.getCurArticle());
        player.start();
    }

    private void refresh(boolean defaultPos) {
        handler.sendEmptyMessage(2);
        if (SettingConfigManager.instance.getStudyPlayMode() == 0) {
            if (defaultPos) {
                viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
                viewPager.setCurrentItem(1);
            } else {
                player.seekTo(0);
            }
        } else {
            if (isDestroyed) {
            } else if (defaultPos) {
                viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
                viewPager.setCurrentItem(1);
            } else {
                int currPage = viewPager.getCurrentItem();
                viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
                viewPager.setCurrentItem(currPage);
            }
        }
        if (StudyManager.instance.getCurArticle().getSimple() == 1) {
            studyMode.setVisibility(View.GONE);
            comment.setVisibility(View.VISIBLE);
            studyTranslate.setVisibility(View.VISIBLE);
            getCommentCount();
        } else if (!StudyManager.instance.getApp().equals("209")) {
            studyMode.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
            studyTranslate.setVisibility(View.VISIBLE);
        } else {
            studyMode.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
            getCommentCount();
        }
    }

    private void setPlayModeImage(int state) {
        switch (state) {
            case 0:
                playMode.setImageDrawable(context.getResources().getDrawable(R.drawable.single_replay));
                break;
            case 1:
                playMode.setImageDrawable(context.getResources().getDrawable(R.drawable.list_play));
                break;
            case 2:
                playMode.setImageDrawable(context.getResources().getDrawable(R.drawable.random_play));
                break;
        }

    }

    private void setPauseImage(boolean click) {
        if (click) {
            sendBroadcast(new Intent("iyumusic.pause"));
        } else {
            if (player == null) {
                playSound.setState(MorphButton.MorphState.START, true);
            } else if (player.isPlaying()) {
                playSound.setState(MorphButton.MorphState.END, true);
            } else {
                playSound.setState(MorphButton.MorphState.START, true);
            }
        }
    }

    private void setStudyModeImage(int state) {
        switch (state) {
            case 0:
                studyMode.setImageDrawable(context.getResources().getDrawable(R.drawable.study_annoucer_mode));
                studyTranslate.setVisibility(View.VISIBLE);
                break;
            case 1:
                studyMode.setImageDrawable(context.getResources().getDrawable(R.drawable.study_singer_mode));
                studyTranslate.setVisibility(View.GONE);
                break;
        }
        ((MusicApplication) getApplication()).getPlayerService().startPlay(
                StudyManager.instance.getCurArticle(), true);
        handler.sendEmptyMessage(2);
        ((MusicApplication) getApplication()).getPlayerService().setCurArticle(StudyManager.instance.getCurArticle());
        int currPage = viewPager.getCurrentItem();
        viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currPage);
    }

    private void setStudyTranslateImage(int state) {
        switch (state) {
            case 1:
                studyTranslate.setImageDrawable(context.getResources().getDrawable(R.drawable.study_translate));
                break;
            case 0:
                studyTranslate.setImageDrawable(context.getResources().getDrawable(R.drawable.study_no_translate));
                break;
        }
        int currPage = viewPager.getCurrentItem();
        viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currPage);
    }

    private void setIntervalImage(int mode) {
        switch (mode) {
            case 0:
                intervalState = IntervalState.NONE;
                interval.setImageDrawable(context.getResources().getDrawable(R.drawable.interval_none));
                break;
            case 1:
                switch (intervalState) {
                    case NONE:
                        intervalState = IntervalState.START;
                        aPosition = player.getCurrentPosition();
                        CustomToast.INSTANCE.showToast(R.string.study_a_position);
                        interval.setImageDrawable(context.getResources().getDrawable(R.drawable.interval_start));
                        break;
                    case START:
                        intervalState = IntervalState.END;
                        bPosition = player.getCurrentPosition();
                        CustomToast.INSTANCE.showToast(R.string.study_b_position);
                        handler.sendEmptyMessage(1);
                        interval.setImageDrawable(context.getResources().getDrawable(R.drawable.interval_end));
                        break;
                    case END:
                        CustomToast.INSTANCE.showToast(R.string.study_ab_cancle);
                        handler.sendEmptyMessage(2);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void getCommentCount() {
        CommentCountRequest.getInstance().exeRequest(CommentCountRequest.getInstance().generateUrl(StudyManager.instance.getCurArticle().getId()), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                comment.setText("0");
            }

            @Override
            public void onServerError(String msg) {
                comment.setText("0");
            }

            @Override
            public void response(Object object) {
                if (TextUtils.isEmpty(object.toString())) {
                    comment.setText("0");
                } else {
                    comment.setText(object.toString());
                }
            }
        });
    }

    public enum IntervalState {START, END, NONE}

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<StudyActivity> {
        @Override
        public void handleMessageByRef(final StudyActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.currTime.setText(Mathematics.formatTime(activity.player.getCurrentPosition() / 1000));
                    activity.seekBar.setProgress(activity.player.getCurrentPosition());
                    activity.handler.sendEmptyMessageDelayed(0, 1000);
                    if (activity.intervalState.equals(IntervalState.END)) {
                        if (Math.abs(activity.player.getCurrentPosition() - activity.bPosition) <= 1000) {
                            activity.handler.sendEmptyMessage(1);
                        }
                    }
                    break;
                case 1:
                    activity.player.seekTo(activity.aPosition);// A-B播放
                    break;
                case 2:
                    activity.setIntervalImage(0);
                    break;
            }
        }
    }

    public class StudyChangeUIBroadCast extends ChangeUIBroadCast {
        @Override
        public void refreshUI(String message) {
            switch (message) {
                case "change":
                    refresh(false);
                    break;
                case "pause":
                    setPauseImage(false);
                    break;
            }
        }
    }
}
