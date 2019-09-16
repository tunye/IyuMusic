package com.iyuba.music.activity.study;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.addam.library.api.AddamBanner;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.network.NetWorkState;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.util.ImageUtil;
import com.buaa.ct.core.util.PermissionPool;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.WelcomeAdWebView;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.fragmentAdapter.StudyFragmentAdapter;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.ChangeUIBroadCast;
import com.iyuba.music.request.newsrequest.CommentCountRequest;
import com.iyuba.music.request.newsrequest.StudyAdRequest;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.StudyMore;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.imageview.MorphButton;
import com.iyuba.music.widget.imageview.PageIndicator;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.miaoze.sdk.AdSize;
import com.miaoze.sdk.AdView;
import com.umeng.socialize.UMShareAPI;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyActivity extends BaseActivity implements View.OnClickListener {
    public static final int START = 0x01;
    public static final int END = 0x02;
    public static final int NONE = 0x03;
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private StudyFragmentAdapter studyFragmentAdapter;
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
    private IyubaDialog waittingDialog;
    private LinearLayout root;
    IPlayerListener iPlayerListener = new IPlayerListener() {
        @Override
        public void onPrepare() {
            if (waittingDialog.isShowing()) {
                waittingDialog.dismiss();
            }
            int i = player.getDuration();
            seekBar.setMax(i);
            duration.setText(DateFormat.formatTime(i / 1000));
            handler.sendEmptyMessage(0);
            playSound.setState(MorphButton.PLAY_STATE);
        }

        @Override
        public void onBufferChange(int buffer) {
            seekBar.setSecondaryProgress(buffer * seekBar.getMax() / 100);
        }

        @Override
        public void onFinish() {
            checkNetWorkState();
            refresh(false);
        }

        @Override
        public void onError() {

        }
    };
    private FrameLayout adRoot;
    private View adView;
    private ImageView photoImage;
    private Timer timer;
    private TimerTask timerTask;
    private YouDaoNative youdaoNative;
    private AddamBanner addamBanner;
    private AdView sspAd;
    private StudyChangeUIBroadCast studyChangeUIBroadCast;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
        ((MusicApplication) getApplication()).getPlayerService().startPlay(
                StudyManager.getInstance().getCurArticle(), false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.study;
    }

    @Override
    public void afterSetLayout() {
        super.afterSetLayout();
        permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        initBroadCast();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player.isPrepared()) {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        if (youdaoNative != null) {
            youdaoNative.destroy();
        }
        if (addamBanner != null) {
            addamBanner.unLoad();
        }
        if (sspAd != null) {
            sspAd.CloseBannerCarousel();
        }
        if (timer != null) {
            timerTask.cancel();
            timer.purge();
        }
        unregisterReceiver(studyChangeUIBroadCast);
        ((MusicApplication) getApplication()).getPlayerService().setListener(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (studyMoreDialog != null && studyMoreDialog.isShown()) {
            studyMoreDialog.dismiss();
        } else if (!studyFragmentAdapter.getItem(viewPager.getCurrentItem()).onBackPressed()) {
            if (!mipush && !changeProperty) {
                if (((MusicApplication) getApplication()).noMain()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
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
                setPauseImage(true);
                break;
            case R.id.study_mode:
                int musicType = ConfigManager.getInstance().getStudyMode();
                musicType = (musicType + 1) % 2;
                ConfigManager.getInstance().setStudyMode(musicType);
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
                if (studyMoreDialog != null && studyMoreDialog.isShown()) {
                    studyMoreDialog.dismiss();
                } else {
                    if (studyMoreDialog == null) {
                        studyMoreDialog = new StudyMore(this);
                    }
                    studyMoreDialog.show();
                }
                break;
            case R.id.study_translate:
                int musicTranslate = ConfigManager.getInstance().getStudyTranslate();
                musicTranslate = (musicTranslate + 1) % 2;
                ConfigManager.getInstance().setStudyTranslate(musicTranslate);
                setStudyTranslateImage(musicTranslate, true);
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
        if (((MusicApplication) getApplication()).getProxy().isCached(url)) {
            return true;
        } else if (url.startsWith("http")) {
            if (!NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
                showNoNetDialog();
                return false;
            } else if (!NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G_3G)) {
                CustomSnackBar.make(root, context.getString(R.string.net_speed_slow)).warning(context.getString(R.string.net_set), new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
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
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.net_study_no_net);
        materialDialog.setMessage(R.string.net_study_no_net_message);
        boolean findFileFlg = false;
        if (ConfigManager.getInstance().getStudyMode() == 1) {
            File packageFile = new File(ConstantManager.originalFolder);
            if (packageFile.exists() && packageFile.list() != null) {
                for (String fileName : packageFile.list()) {
                    if (fileName.startsWith(String.valueOf(StudyManager.getInstance().getCurArticle().getId()))) {
                        materialDialog.setPositiveButton(R.string.net_study_lrc, new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                viewPager.setCurrentItem(2);
                                materialDialog.dismiss();
                            }
                        });
                        materialDialog.setNegativeButton(R.string.app_know, new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                finish();
                            }
                        });
                        findFileFlg = true;
                        break;
                    }
                }
            }
            if (!findFileFlg) {
                materialDialog.setPositiveButton(R.string.app_know, new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        finish();
                    }
                });
            }
        } else {
            File packageFile = new File(ConstantManager.musicFolder);
            if (packageFile.exists() && packageFile.list() != null) {
                for (String fileName : packageFile.list()) {
                    if (fileName.startsWith(String.valueOf(StudyManager.getInstance().getCurArticle().getId()))) {
                        materialDialog.setPositiveButton(R.string.net_study_lrc, new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                viewPager.setCurrentItem(2);
                                materialDialog.dismiss();
                            }
                        });
                        materialDialog.setNegativeButton(R.string.app_know, new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                finish();
                            }
                        });
                        findFileFlg = true;
                        break;
                    }
                }
            }
            if (!findFileFlg) {
                materialDialog.setPositiveButton(R.string.app_know, new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        finish();
                    }
                });
            }
        }
        materialDialog.show();
    }

    private void initBroadCast() {
        studyChangeUIBroadCast = new StudyChangeUIBroadCast(this);
        IntentFilter intentFilter = new IntentFilter("com.iyuba.music.study");
        registerReceiver(studyChangeUIBroadCast, intentFilter);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        root = findViewById(R.id.root);
        adRoot = findViewById(R.id.ad_stub);
        studyMore = findViewById(R.id.study_more);
        viewPager = findViewById(R.id.study_main);
        pageIndicator = findViewById(R.id.study_indicator);
        currTime = findViewById(R.id.study_current_time);
        duration = findViewById(R.id.study_duration);
        seekBar = findViewById(R.id.study_progress);

        playSound = findViewById(R.id.play);
        former = findViewById(R.id.formmer);
        latter = findViewById(R.id.latter);
        playMode = findViewById(R.id.play_mode);
        interval = findViewById(R.id.interval);
        comment = findViewById(R.id.study_comment);
        studyMode = findViewById(R.id.study_mode);
        studyTranslate = findViewById(R.id.study_translate);
        playSound.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(), PorterDuff.Mode.SRC_IN);
        waittingDialog = WaitingDialog.create(context, null);
        if (!DownloadUtil.checkVip() && NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            initAdTimer();
        } else {
            adRoot.setVisibility(View.GONE);
        }
    }

    private void setAdType(AdEntity adEntity) {
        adRoot.removeAllViews();
        if (addamBanner != null) {
            addamBanner.unLoad();
            addamBanner = null;
        }
        if (youdaoNative != null) {
            youdaoNative.destroy();
            youdaoNative = null;
        }
        if (sspAd != null) {
            sspAd.CloseBannerCarousel();
        }
        switch (adEntity.getType()) {
            case "addam":
                adView = LayoutInflater.from(context).inflate(R.layout.addam_ad_layout, null);
                adRoot.addView(adView);
                addamBanner = (AddamBanner) adView.findViewById(R.id.addam_ad_banner);
                addamBanner.setAdUnitID("a01c1754adf58704df15e929dc63b4ce");
                addamBanner.setAdSize(AddamBanner.Size.BannerAuto);
                addamBanner.load(); // 开始加载
                break;
            case "ssp":
                AdView.preLoad(this);
                AdView sspAd = new AdView(this, AdSize.Banner, "sb6458f4");
                adRoot.addView(sspAd, new RelativeLayout.LayoutParams(-1, -2));
                break;
            default:
            case "youdao":
                adView = LayoutInflater.from(context).inflate(R.layout.youdao_ad_layout, null);
                adRoot.addView(adView);
                photoImage = (ImageView) adView.findViewById(R.id.photoImage);
                initYouDaoAd();
                break;
            case "web":
                adView = LayoutInflater.from(context).inflate(R.layout.youdao_ad_layout, null);
                adRoot.addView(adView);
                photoImage = (ImageView) adView.findViewById(R.id.photoImage);
                refreshNativeAd(adEntity);
                break;
        }
    }

    private void getAdContent(final IOperationResult iOperationResult) {
        RequestClient.requestAsync(new StudyAdRequest(), new SimpleRequestCallBack<AdEntity>() {
            @Override
            public void onSuccess(AdEntity adEntity) {
                iOperationResult.success(adEntity);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }

    private void initAdTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                getAdContent(new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        handler.obtainMessage(3, object).sendToTarget();
                    }

                    @Override
                    public void fail(Object object) {
                    }
                });
            }
        };
        timer = new Timer(false);
        timer.scheduleAtFixedRate(timerTask, Calendar.getInstance().getTime(), 60000);
    }

    private void refreshNativeAd(final AdEntity adEntity) {
        if (!isDestroyed()) {
            adView.setOnClickListener(new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
                    WelcomeAdWebView.launch(context, TextUtils.isEmpty(adEntity.getLoadUrl()) ?
                            "http://app.iyuba.cn/android/" : adEntity.getLoadUrl(), -1);
                }
            });
            AppImageUtil.loadImage(adEntity.getPicUrl(), photoImage);
        }
    }

    private void initYouDaoAd() {
        youdaoNative = new YouDaoNative(context, "230d59b7c0a808d01b7041c2d127da95",
                new YouDaoNative.YouDaoNativeNetworkListener() {
                    @Override
                    public void onNativeLoad(final NativeResponse nativeResponse) {
                        adView.setOnClickListener(new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                nativeResponse.handleClick(adView);
                            }
                        });
                        ImageUtil.loadImage(nativeResponse.getMainImageUrl(), photoImage, null, new ImageUtil.OnBitmapLoaded() {
                            @Override
                            public void onImageLoaded(Bitmap bitmap) {
                                nativeResponse.recordImpression(photoImage);
                            }

                            @Override
                            public void onImageLoadFailed() {

                            }
                        });
                    }

                    @Override
                    public void onNativeFail(NativeErrorCode nativeErrorCode) {

                    }
                });
        Location location = new Location("appPos");
        location.setLatitude(AccountManager.getInstance().getLatitude());
        location.setLongitude(AccountManager.getInstance().getLongitude());
        location.setAccuracy(100);

        RequestParameters requestParameters = new RequestParameters.Builder()
                .location(location).build();
        youdaoNative.makeRequest(requestParameters);
    }

    @Override
    public void setListener() {
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
                    player.seekTo(progress);
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
    public void onActivityCreated() {
        super.onActivityCreated();
        if (StudyManager.getInstance().getCurArticle() == null)
            return;
        if (((MusicApplication) getApplication()).getPlayerService().getCurArticleId() == StudyManager.getInstance().getCurArticle().getId()) {
            int i = player.getDuration();
            seekBar.setMax(i);
            duration.setText(DateFormat.formatTime(i / 1000));
        } else {
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
        }
        checkNetWorkState();
    }

    @Override
    public void onActivityResumed() {
        setPlayModeImage(ConfigManager.getInstance().getStudyPlayMode());
        switch (StudyManager.getInstance().getMusicType()) {
            case 0:
                studyMode.setImageResource(R.drawable.study_annoucer_mode);
                studyTranslate.setVisibility(View.VISIBLE);
                setStudyTranslateImage(ConfigManager.getInstance().getStudyTranslate(), false);
                break;
            case 1:
                studyMode.setImageResource(R.drawable.study_singer_mode);
                studyTranslate.setVisibility(View.GONE);
                break;
        }
        setPauseImage(false);
        handler.sendEmptyMessage(2);
        setFuncImgShowState();
        if (viewPager.getAdapter() == null || studyFragmentAdapter == null) {
            studyFragmentAdapter = new StudyFragmentAdapter(getSupportFragmentManager());
            viewPager.setAdapter(studyFragmentAdapter);
            viewPager.setCurrentItem(1);
        } else {
            studyFragmentAdapter.refresh();
        }
    }

    @Override
    public void onAccreditFailure(int requestCode) {
        super.onAccreditFailure(requestCode);
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.storage_permission);
        materialDialog.setMessage(R.string.storage_permission_content);
        materialDialog.setPositiveButton(R.string.app_sure, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private void startPlay() {
        if (checkNetWorkState()) {
            ((MusicApplication) getApplication()).getPlayerService().startPlay(StudyManager.getInstance().getCurArticle(), false);
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
        }
    }

    private void refresh(boolean defaultPos) {
        handler.sendEmptyMessage(2);
        if (ConfigManager.getInstance().getStudyPlayMode() == 0 || StudyManager.getInstance().getCurArticleList().size() == 1) {
            if (defaultPos) {
                viewPager.setCurrentItem(1);
            } else {
                player.seekTo(0);
            }
            studyFragmentAdapter.refresh();
        } else {
            if (defaultPos) {
                studyFragmentAdapter.refresh();
                viewPager.setCurrentItem(1);
            } else {
                if (checkNetWorkState()) {
                    seekBar.setSecondaryProgress(0);
                    playSound.setState(MorphButton.PLAY_STATE);
                    duration.setText("00:00");
                }
                int currPage = viewPager.getCurrentItem();
                studyFragmentAdapter.refresh();
                viewPager.setCurrentItem(currPage);
            }
        }
        setFuncImgShowState();
    }

    private void setFuncImgShowState() {
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
                playSound.setState(MorphButton.PAUSE_STATE);
            } else if (player.isPlaying()) {
                playSound.setState(MorphButton.PLAY_STATE);
            } else {
                playSound.setState(MorphButton.PAUSE_STATE);
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
            playSound.setState(MorphButton.PLAY_STATE);
            duration.setText("00:00");
            ((MusicApplication) getApplication()).getPlayerService().startPlay(
                    StudyManager.getInstance().getCurArticle(), true);
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
            int currPage = viewPager.getCurrentItem();
            studyFragmentAdapter.refresh();
            viewPager.setCurrentItem(currPage);
        }
    }

    private void setStudyTranslateImage(int state, boolean click) {
        switch (state) {
            case 1:
                studyTranslate.setImageResource(R.drawable.study_translate);
                break;
            case 0:
                studyTranslate.setImageResource(R.drawable.study_no_translate);
                break;
        }
        if (click) {
            studyFragmentAdapter.changeLanguage();
        }
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
        RequestClient.requestAsync(new CommentCountRequest(StudyManager.getInstance().getCurArticle().getId()), new SimpleRequestCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                if (TextUtils.isEmpty(s)) {
                    comment.setText("0");
                } else {
                    comment.setText(s);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                comment.setText("0");
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
                        activity.currTime.setText(DateFormat.formatTime(pos / 1000));
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
                            if (activity.playSound.getState() == MorphButton.PAUSE_STATE) {
                                activity.playSound.setState(MorphButton.PLAY_STATE);
                            }
                        } else if (!activity.player.isPlaying()) {
                            if (activity.playSound.getState() == MorphButton.PLAY_STATE) {
                                activity.playSound.setState(MorphButton.PAUSE_STATE);
                            }
                        }
                    }
                    break;
                case 1:
                    activity.player.seekTo(activity.aPosition);// A-B播放
                    break;
                case 2:
                    activity.setIntervalImage(0);
                    break;
                case 3:
                    activity.setAdType((AdEntity) msg.obj);
                    break;
            }
        }
    }

    public static class StudyChangeUIBroadCast extends ChangeUIBroadCast {
        private final WeakReference<StudyActivity> mWeakReference;

        public StudyChangeUIBroadCast(StudyActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void refreshUI(String message) {
            if (mWeakReference.get() != null) {
                switch (message) {
                    case "change":
                        mWeakReference.get().refresh(true);
                        break;
                    case "pause":
                        mWeakReference.get().setPauseImage(false);
                        break;
                }
            }
        }
    }
}
