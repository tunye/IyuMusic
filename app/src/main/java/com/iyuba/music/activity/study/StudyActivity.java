package com.iyuba.music.activity.study;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.fragmentAdapter.StudyFragmentAdapter;
import com.iyuba.music.listener.ChangeUIBroadCast;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.request.newsrequest.CommentCountRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.LocationUtil;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.StudyMore;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.imageview.PageIndicator;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.umeng.socialize.UMShareAPI;
import com.wnafee.vector.MorphButton;
import com.youdao.sdk.nativeads.ImageService;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyActivity extends BaseActivity implements View.OnClickListener {
    public static final int START = 0x01;
    public static final int END = 0x02;
    public static final int NONE = 0x03;
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
    @IntervalState
    private int intervalState;
    private boolean isDestroyed = false;
    private IyubaDialog waittingDialog;
    IPlayerListener iPlayerListener = new IPlayerListener() {
        @Override
        public void onPrepare() {
            if (waittingDialog.isShowing()) {
                waittingDialog.dismiss();
            }
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
    private YouDaoNative youdaoNative;
    private StudyChangeUIBroadCast studyChangeUIBroadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study);
        Log.e("aaa", "11");
        context = this;
        player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
        ((MusicApplication) getApplication()).getPlayerService().startPlay(
                StudyManager.getInstance().getCurArticle(), false);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_TASK_CODE);
        }
        initBroadCast();
        initWidget();
        setListener();
        changeUIByPara();
        isDestroyed = false;
        checkNetWorkState();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
        if (player.isPrepared()) {
            handler.sendEmptyMessage(0);
        }
        Log.e("aaa", "12");
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(studyChangeUIBroadCast);
        isDestroyed = true;
        if (youdaoNative != null) {
            youdaoNative.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (studyMoreDialog.isShown()) {
            studyMoreDialog.dismiss();
        } else if (!((StudyFragmentAdapter) viewPager.getAdapter()).list.get(viewPager.getCurrentItem()).onBackPressed()) {
            if (!mipush && !changeProperty) {
                if (((MusicApplication) getApplication()).onlyForeground("StudyActivity")) {
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    super.onBackPressed();
                }
            } else {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        }
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
                setPauseImage(true);
                break;
            case R.id.study_mode:
                int musicType = SettingConfigManager.getInstance().getStudyMode();
                musicType = (musicType + 1) % 2;
                SettingConfigManager.getInstance().setStudyMode(musicType);
                setStudyModeImage(musicType);
                break;
            case R.id.interval:
                setIntervalImage(1);
                break;
            case R.id.latter:
                ((MusicApplication) getApplication()).getPlayerService().next(false);
                startPlay();
                refresh(true);
                break;
            case R.id.formmer:
                ((MusicApplication) getApplication()).getPlayerService().before();
                startPlay();
                refresh(true);
                break;
            case R.id.study_more:
                if (studyMoreDialog.isShown()) {
                    studyMoreDialog.dismiss();
                } else {
                    studyMoreDialog.show();
                }
                break;
            case R.id.study_translate:
                int musicTranslate = SettingConfigManager.getInstance().getStudyTranslate();
                musicTranslate = (musicTranslate + 1) % 2;
                SettingConfigManager.getInstance().setStudyTranslate(musicTranslate);
                setStudyTranslateImage(musicTranslate);
                break;
            case R.id.study_comment:
                if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
                    startActivity(new Intent(context, CommentActivity.class));
                } else {
                    CustomToast.getInstance().showToast(R.string.no_internet);
                }
                break;
        }
    }

    private boolean checkNetWorkState() {
        String url = ((MusicApplication) getApplication()).getPlayerService().getUrl(StudyManager.getInstance().getCurArticle());
        if (((MusicApplication) getApplication()).getProxy(this).isCached(url)) {
            return true;
        } else if (url.startsWith("http") && !isDestroyed) {
            if (!NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
                showNoNetDialog();
                return false;
            } else if (!NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G_3G)) {
                CustomSnackBar.make(findViewById(R.id.root), context.getString(R.string.net_speed_slow)).warning(context.getString(R.string.net_set), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                });
                return false;
            } else {
                waittingDialog.show();
                return true;
            }
        } else {
            return true;
        }
    }

    private void showNoNetDialog() {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setTitle(R.string.net_study_no_net);
        materialDialog.setMessage(R.string.net_study_no_net_message);
        boolean findFileFlg = false;
        if (SettingConfigManager.getInstance().getStudyMode() == 1) {
            File packageFile = new File(ConstantManager.getInstance().getOriginalFolder());
            if (packageFile.exists() && packageFile.list() != null) {
                for (String fileName : packageFile.list()) {
                    if (fileName.startsWith(String.valueOf(StudyManager.getInstance().getCurArticle().getId()))) {
                        materialDialog.setPositiveButton(R.string.net_study_lrc, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewPager.setCurrentItem(2);
                                materialDialog.dismiss();
                            }
                        });
                        materialDialog.setNegativeButton(R.string.app_know, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        findFileFlg = true;
                        break;
                    }
                }
            }
            if (!findFileFlg) {
                materialDialog.setPositiveButton(R.string.app_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        } else {
            File packageFile = new File(ConstantManager.getInstance().getMusicFolder());
            if (packageFile.exists() && packageFile.list() != null) {
                for (String fileName : packageFile.list()) {
                    if (fileName.startsWith(String.valueOf(StudyManager.getInstance().getCurArticle().getId()))) {
                        materialDialog.setPositiveButton(R.string.net_study_lrc, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewPager.setCurrentItem(2);
                                materialDialog.dismiss();
                            }
                        });
                        materialDialog.setNegativeButton(R.string.app_know, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        findFileFlg = true;
                        break;
                    }
                }
            }
            if (!findFileFlg) {
                materialDialog.setPositiveButton(R.string.app_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        }
        materialDialog.show();
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
        playSound.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(context), PorterDuff.Mode.SRC_IN);
        waittingDialog = WaitingDialog.create(context, null);
        if (!DownloadService.checkVip()) {
            initAd();
        } else {
            youdaoNative = null;
        }
    }

    private void initAd() {
        final View adView = findViewById(R.id.youdao_ad);
        final ImageView photoImage = (ImageView) findViewById(R.id.photoImage);
        youdaoNative = new YouDaoNative(context, "230d59b7c0a808d01b7041c2d127da95",
                new YouDaoNative.YouDaoNativeNetworkListener() {
                    @Override
                    public void onNativeLoad(final NativeResponse nativeResponse) {
                        List<String> imageUrls = new ArrayList<>();
                        imageUrls.add(nativeResponse.getMainImageUrl());
                        adView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nativeResponse.handleClick(adView);
                            }
                        });
                        ImageService.get(context, imageUrls, new ImageService.ImageServiceListener() {
                            @TargetApi(Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onSuccess(final Map<String, Bitmap> bitmaps) {
                                if (nativeResponse.getMainImageUrl() != null) {
                                    Bitmap bitMap = bitmaps.get(nativeResponse.getMainImageUrl());
                                    if (bitMap != null) {
                                        photoImage.setImageBitmap(bitMap);
                                        photoImage.setVisibility(View.VISIBLE);
                                        nativeResponse.recordImpression(photoImage);
                                    }
                                }
                            }

                            @Override
                            public void onFail() {
                            }
                        });
                        adView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNativeFail(NativeErrorCode nativeErrorCode) {

                    }
                });
        Location location = new Location("appPos");
        location.setLatitude(LocationUtil.getInstance().getLatitude());
        location.setLongitude(LocationUtil.getInstance().getLongitude());
        location.setAccuracy(100);

        RequestParameters requestParameters = new RequestParameters.Builder()
                .location(location).build();
        youdaoNative.makeRequest(requestParameters);
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
                        pageIndicator.setDirection(PageIndicator.LEFT);
                        pageIndicator.setMovePercent(position + 1, positionOffset);
                    } else {
                        pageIndicator.setDirection(PageIndicator.RIGHT);
                        pageIndicator.setMovePercent(position, positionOffset);
                    }
                }
                lastChange = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                pageIndicator.setDirection(PageIndicator.NONE);
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
                    currTime.setTextColor(GetAppColor.getInstance().getAppColor(context));
                    duration.setTextColor(GetAppColor.getInstance().getAppColor(context));
                    player.seekTo(progress);
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
        if (((MusicApplication) getApplication()).getPlayerService().getCurArticleId() == StudyManager.getInstance().getCurArticle().getId()) {
            int i = player.getDuration();
            seekBar.setMax(i);
            duration.setText(Mathematics.formatTime(i / 1000));
            handler.sendEmptyMessage(0);
        } else {
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
        }
        setIntervalImage(0);
    }

    protected void changeUIResumeByPara() {
        setPlayModeImage(SettingConfigManager.getInstance().getStudyPlayMode());
        setStudyTranslateImage(SettingConfigManager.getInstance().getStudyTranslate());
        switch (StudyManager.getInstance().getMusicType()) {
            case 0:
                studyMode.setImageResource(R.drawable.study_annoucer_mode);
                studyTranslate.setVisibility(View.VISIBLE);
                break;
            case 1:
                studyMode.setImageResource(R.drawable.study_singer_mode);
                studyTranslate.setVisibility(View.GONE);
                break;
        }
        setPauseImage(false);
        refresh(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_TASK_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(StudyActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RECORD_AUDIO_TASK_CODE);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
    }

    private void startPlay() {
        if (checkNetWorkState()) {
            ((MusicApplication) getApplication()).getPlayerService().startPlay(StudyManager.getInstance().getCurArticle(), false);
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
        }
    }

    private void refresh(boolean defaultPos) {
        handler.sendEmptyMessage(2);
        if (SettingConfigManager.getInstance().getStudyPlayMode() == 0 || StudyManager.getInstance().getCurArticleList().size() == 1) {
            if (defaultPos) {
                viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
                viewPager.setCurrentItem(1);
            } else {
                player.seekTo(0);
            }
            player.start();
        } else {
            if (isDestroyed) {
            } else if (defaultPos) {
                viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
                viewPager.setCurrentItem(1);
            } else {
                if (checkNetWorkState()) {
                    seekBar.setSecondaryProgress(0);
                    playSound.setState(MorphButton.MorphState.START, true);
                    duration.setText("00:00");
                }
                int currPage = viewPager.getCurrentItem();
                viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
                viewPager.setCurrentItem(currPage);
            }
        }
        if (StudyManager.getInstance().getCurArticle().getSimple() == 1) {
            studyMode.setVisibility(View.GONE);
            comment.setVisibility(View.VISIBLE);
            studyTranslate.setVisibility(View.VISIBLE);
            getCommentCount();
        } else if (!StudyManager.getInstance().getApp().equals("209")) {
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

    private void setPauseImage(boolean click) {
        if (click) {
            if (player != null && player.isPrepared()) {
                sendBroadcast(new Intent("iyumusic.pause"));
            }
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
                studyMode.setImageResource(R.drawable.study_annoucer_mode);
                studyTranslate.setVisibility(View.VISIBLE);
                break;
            case 1:
                studyMode.setImageResource(R.drawable.study_singer_mode);
                studyTranslate.setVisibility(View.GONE);
                break;
        }
        if (checkNetWorkState()) {
            handler.sendEmptyMessage(2);
            seekBar.setSecondaryProgress(0);
            playSound.setState(MorphButton.MorphState.START, true);
            duration.setText("00:00");
            ((MusicApplication) getApplication()).getPlayerService().startPlay(
                    StudyManager.getInstance().getCurArticle(), true);
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
            int currPage = viewPager.getCurrentItem();
            viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
            viewPager.setCurrentItem(currPage);
        }
    }

    private void setStudyTranslateImage(int state) {
        switch (state) {
            case 1:
                studyTranslate.setImageResource(R.drawable.study_translate);
                break;
            case 0:
                studyTranslate.setImageResource(R.drawable.study_no_translate);
                break;
        }
        int currPage = viewPager.getCurrentItem();
        viewPager.setAdapter(new StudyFragmentAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currPage);
    }

    private void setIntervalImage(int mode) {
        switch (mode) {
            case 0:
                intervalState = NONE;
                interval.setImageResource(R.drawable.interval_none);
                break;
            case 1:
                switch (intervalState) {
                    case NONE:
                        intervalState = START;
                        aPosition = player.getCurrentPosition();
                        CustomToast.getInstance().showToast(R.string.study_a_position);
                        interval.setImageResource(R.drawable.interval_start);
                        break;
                    case START:
                        intervalState = END;
                        bPosition = player.getCurrentPosition();
                        CustomToast.getInstance().showToast(R.string.study_b_position);
                        handler.sendEmptyMessage(1);
                        interval.setImageResource(R.drawable.interval_end);
                        break;
                    case END:
                        CustomToast.getInstance().showToast(R.string.study_ab_cancle);
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
        CommentCountRequest.exeRequest(CommentCountRequest.generateUrl(StudyManager.getInstance().getCurArticle().getId()), new IProtocolResponse() {
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

    @IntDef({START, END, NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntervalState {
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<StudyActivity> {
        @Override
        public void handleMessageByRef(final StudyActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    if (activity.player != null) {
                        int pos = activity.player.getCurrentPosition();
                        activity.currTime.setText(Mathematics.formatTime(pos / 1000));
                        activity.seekBar.setProgress(pos);
                        activity.handler.sendEmptyMessageDelayed(0, 500);
                        if (activity.intervalState == END) {
                            if (Math.abs(pos - activity.bPosition) <= 1000) {
                                activity.handler.sendEmptyMessage(1);
                            }
                        }
                        if (pos != 0 && activity.waittingDialog.isShowing()) {
                            activity.waittingDialog.dismiss();
                        }
                        if (activity.player.isPlaying()) {
                            activity.playSound.setState(MorphButton.MorphState.END, false);
                        } else if (!activity.player.isPlaying()) {
                            activity.playSound.setState(MorphButton.MorphState.START, false);
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
                    refresh(true);
                case "pause":
                    setPauseImage(false);
                    break;
            }
        }
    }
}
