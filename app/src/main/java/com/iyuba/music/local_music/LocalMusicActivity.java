package com.iyuba.music.local_music;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.WelcomeActivity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.file.FilePosActivity;
import com.iyuba.music.listener.ChangeUIBroadCast;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.CreateAppLnkUtil;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.util.RandomUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.wnafee.vector.MorphButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2016/4/16.
 */
public class LocalMusicActivity extends BaseActivity implements IOnClickListener {
    private static final int WRITE_EXTERNAL_TASK_CODE = 1;
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private RecyclerView musicList;
    private ArrayList<Article> musics;
    private LocalMusicAdapter adapter;
    private StandardPlayer player;
    private TextView statistic, randomPlay, createLink;
    private ImageView playMode, next, before;
    private ProgressBar progressBar;
    private TextView currentTime;
    private MorphButton pause;
    private IPlayerListener iPlayerListener = new IPlayerListener() {
        @Override
        public void onPrepare() {
            player.start();
            pause.setState(MorphButton.MorphState.END);
            progressBar.setMax(player.getDuration());
            handler.sendEmptyMessage(0);
            refresh();
        }

        @Override
        public void onBufferChange(int buffer) {

        }

        @Override
        public void onFinish() {
            ((MusicApplication) getApplication()).getPlayerService().startPlay(
                    StudyManager.getInstance().getCurArticle(), false);
            player.start();
        }

        @Override
        public void onError() {
        }
    };
    private LocalMusicChangeUIBroadCast broadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eggshell_music_main);
        context = this;
        player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
        ((MusicApplication) getApplication()).getPlayerService().setListener(iPlayerListener);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onBackPressed() {
        if (((MusicApplication) getApplication()).noMain()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            if (!mipush) {
                super.onBackPressed();
            } else {
                startActivity(new Intent(context, MainActivity.class));
            }
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        statistic = (TextView) findViewById(R.id.music_statistic);
        randomPlay = (TextView) findViewById(R.id.music_random_play);
        createLink = (TextView) findViewById(R.id.music_link);
        currentTime = (TextView) findViewById(R.id.current_time);
        pause = (MorphButton) findViewById(R.id.play);
        before = (ImageView) findViewById(R.id.formmer);
        next = (ImageView) findViewById(R.id.latter);
        playMode = (ImageView) findViewById(R.id.play_mode);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        musicList = (RecyclerView) findViewById(R.id.music_recyclerview);
        musicList.setLayoutManager(new LinearLayoutManager(context));
        musicList.addItemDecoration(new DividerItemDecoration());
        ((SimpleItemAnimator) musicList.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new LocalMusicAdapter(context);
        adapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                playSelectItem(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        musicList.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        createLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAppLnkUtil.addLocalMusicLink(LocalMusicActivity.this, WelcomeActivity.class, "爱语吧音乐", R.mipmap.ic_launcher2);
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(LocalMusicActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_TASK_CODE);
                } else {
                    startActivityForResult(new Intent(context, FilePosActivity.class), 101);
                }
            }
        });
        randomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musics.size() != 0) {
                    playSelectItem(RandomUtil.getRandomInt(musics.size()));
                } else {
                    CustomToast.getInstance().showToast(R.string.eggshell_music_no);
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musics.size() != 0) {
                    if (StudyManager.getInstance().getApp().equals("101")) {
                        sendBroadcast(new Intent("iyumusic.pause"));
                    } else {
                        playSelectItem(RandomUtil.getRandomInt(musics.size()));
                    }
                } else {
                    CustomToast.getInstance().showToast(R.string.eggshell_music_no);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MusicApplication) getApplication()).getPlayerService().next(false);
                ((MusicApplication) getApplication()).getPlayerService().startPlay(
                        StudyManager.getInstance().getCurArticle(), false);
            }
        });
        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MusicApplication) getApplication()).getPlayerService().before();
                ((MusicApplication) getApplication()).getPlayerService().startPlay(
                        StudyManager.getInstance().getCurArticle(), false);
            }
        });
        playMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextMusicType = SettingConfigManager.getInstance().getStudyPlayMode();
                nextMusicType = (nextMusicType + 1) % 3;
                SettingConfigManager.getInstance().setStudyPlayMode(nextMusicType);
                StudyManager.getInstance().generateArticleList();
                setPlayModeImage(nextMusicType);
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_local_music);
        toolbarOper.setText(R.string.eggshell_music_scan);
        pause.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(), PorterDuff.Mode.SRC_IN);
        musics = new ArrayList<>();
        if (TextUtils.isEmpty(ConfigManager.getInstance().loadString("localMusicPath"))) {
            ConfigManager.getInstance().putString("localMusicPath", "/");
            musics = MusicUtils.getAllSongs(context, "/");
            statistic.setText(context.getString(R.string.eggshell_music_static, musics.size()));
            adapter.setDataSet(musics);
        } else {
            musics.addAll(MusicUtils.getAllSongs(context, ConfigManager.getInstance().loadString("localMusicPath")));
        }
        adapter.setDataSet(musics);
        statistic.setText(context.getString(R.string.eggshell_music_static, musics.size()));
        setPlayModeImage(SettingConfigManager.getInstance().getStudyPlayMode());
    }

    @Override
    public void onClick(View view, Object message) {
        musicList.scrollToPosition(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 1) {                               // 扫描的返回结果
            String path = data.getStringExtra("path");
            ConfigManager.getInstance().putString("localMusicPath", path);
            musics = MusicUtils.getAllSongs(context, path);
            statistic.setText(context.getString(R.string.eggshell_music_static, musics.size()));
            adapter.setDataSet(musics);
            if (musics.size() == 0) {
                CustomToast.getInstance().showToast(R.string.eggshell_music_no);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        broadCast = new LocalMusicChangeUIBroadCast(this);
        IntentFilter intentFilter = new IntentFilter("com.iyuba.music.localmusic");
        registerReceiver(broadCast, intentFilter);
        setPauseImage();
        if (player != null && StudyManager.getInstance().getApp().equals("101")) {
            progressBar.setMax(player.getDuration());
            handler.sendEmptyMessage(0);
        } else {
            progressBar.setMax(100);
            progressBar.setProgress(0);
            currentTime.setText("--:--");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadCast);
        handler.removeCallbacksAndMessages(null);
    }

    private void setPauseImage() {
        if (player == null || !StudyManager.getInstance().getApp().equals("101")) {
            pause.setState(MorphButton.MorphState.START);
        } else if (player.isPlaying()) {
            pause.setState(MorphButton.MorphState.END, true);
            refresh();
        } else {
            pause.setState(MorphButton.MorphState.START, true);
            adapter.setCurPos(-1);
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

    private void playSelectItem(int position) {
        StudyManager.getInstance().setSourceArticleList(musics);
        StudyManager.getInstance().setListFragmentPos(LocalMusicActivity.this.getClass().getName());
        StudyManager.getInstance().setCurArticle(musics.get(position));
        StudyManager.getInstance().setStartPlaying(true);
        musicList.scrollToPosition(position);
        adapter.setCurPos(position);
        ((MusicApplication) getApplication()).getPlayerService().startPlay(StudyManager.getInstance().getCurArticle(), false);
        ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(musics.get(position).getId());
    }

    private void refresh() {
        if (LocalMusicActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            Article article = StudyManager.getInstance().getCurArticle();
            if (article != null) {
                for (int i = 0; i < musics.size(); i++) {
                    if (musics.get(i).getId() == article.getId()) {
                        if (musics.get(i).getTitle().equals(article.getTitle())) {
                            musicList.scrollToPosition(i);
                            adapter.setCurPos(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_TASK_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(context, FilePosActivity.class), 101);
            } else {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setTitle(R.string.storage_permission);
                materialDialog.setMessage(R.string.storage_permission_content);
                materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(LocalMusicActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_TASK_CODE);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.show();
            }
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<LocalMusicActivity> {
        @Override
        public void handleMessageByRef(final LocalMusicActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.progressBar.setProgress(activity.player.getCurrentPosition());
                    activity.currentTime.setText(Mathematics.formatTime(activity.player.getCurrentPosition() / 1000));
                    if (activity.player.isPlaying()) {
                        if (activity.pause.getState().equals(MorphButton.MorphState.START)) {
                            activity.pause.setState(MorphButton.MorphState.END, false);
                        }
                    } else if (!activity.player.isPlaying()) {
                        if (activity.pause.getState().equals(MorphButton.MorphState.END)) {
                            activity.pause.setState(MorphButton.MorphState.START, false);
                        }
                    }
                    activity.handler.sendEmptyMessageDelayed(0, 500);
                    break;
            }
        }
    }

    public static class LocalMusicChangeUIBroadCast extends ChangeUIBroadCast {
        private final WeakReference<LocalMusicActivity> mWeakReference;

        public LocalMusicChangeUIBroadCast(LocalMusicActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void refreshUI(String message) {
            switch (message) {
                case "change":
                    if (mWeakReference.get() != null) {
                        mWeakReference.get().refresh();
                    }
                case "pause":
                    if (mWeakReference.get() != null) {
                        mWeakReference.get().setPauseImage();
                    }
                    break;
                case "randomPlay":
                    if (mWeakReference.get() != null) {
                        ArrayList<Article> musics = mWeakReference.get().musics;
                        if (musics != null) {
                            if (musics.size() != 0) {
                                mWeakReference.get().playSelectItem(RandomUtil.getRandomInt(musics.size()));
                            } else {
                                CustomToast.getInstance().showToast(R.string.eggshell_music_no);
                            }
                        }
                    }
                    break;
            }
        }
    }
}
