package com.iyuba.music.local_music;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.ChangeUIBroadCast;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.PermissionPool;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.imageview.MorphButton;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by 10202 on 2016/4/16.
 */
public class LocalMusicActivity extends BaseActivity implements IOnClickListener {
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
            pause.setState(MorphButton.PLAY_STATE);
            progressBar.setMax(player.getDuration());
            handler.sendEmptyMessage(0);
            refresh();
        }

        @Override
        public void onBufferChange(int buffer) {

        }

        @Override
        public void onFinish() {
            refresh();
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
        toolbarOper = findViewById(R.id.toolbar_oper);
        statistic = findViewById(R.id.music_statistic);
        randomPlay = findViewById(R.id.music_random_play);
        createLink = findViewById(R.id.music_link);
        currentTime = findViewById(R.id.current_time);
        pause = findViewById(R.id.play);
        before = findViewById(R.id.formmer);
        next = findViewById(R.id.latter);
        playMode = findViewById(R.id.play_mode);
        progressBar = findViewById(R.id.progress);
        musicList = findViewById(R.id.music_recyclerview);
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
                Utils.addLocalMusicLink(LocalMusicActivity.this, WelcomeActivity.class, "爱语吧音乐", R.mipmap.ic_launcher2);
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        randomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musics.size() != 0) {
                    playSelectItem(Utils.getRandomInt(musics.size()));
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
                        playSelectItem(Utils.getRandomInt(musics.size()));
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
                int nextMusicType = ConfigManager.getInstance().getStudyPlayMode();
                nextMusicType = (nextMusicType + 1) % 3;
                ConfigManager.getInstance().setStudyPlayMode(nextMusicType);
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
        setPlayModeImage(ConfigManager.getInstance().getStudyPlayMode());
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
            pause.setState(MorphButton.PAUSE_STATE);
        } else if (player.isPlaying()) {
            pause.setState(MorphButton.PLAY_STATE);
            refresh();
        } else {
            pause.setState(MorphButton.PAUSE_STATE);
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
    public void onAccreditSucceed(int requestCode) {
        super.onAccreditSucceed(requestCode);
        startActivityForResult(new Intent(context, FilePosActivity.class), 101);
    }

    @Override
    public void onAccreditFailure(int requestCode) {
        super.onAccreditFailure(requestCode);
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.storage_permission);
        materialDialog.setMessage(R.string.storage_permission_content);
        materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<LocalMusicActivity> {
        @Override
        public void handleMessageByRef(final LocalMusicActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.progressBar.setProgress(activity.player.getCurrentPosition());
                    activity.currentTime.setText(DateFormat.formatTime(activity.player.getCurrentPosition() / 1000));
                    if (activity.player.isPlaying()) {
                        if (activity.pause.getState() == MorphButton.PAUSE_STATE) {
                            activity.pause.setState(MorphButton.PLAY_STATE);
                        }
                    } else if (!activity.player.isPlaying()) {
                        if (activity.pause.getState() == MorphButton.PLAY_STATE) {
                            activity.pause.setState(MorphButton.PAUSE_STATE);
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
                    break;
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
                                mWeakReference.get().playSelectItem(Utils.getRandomInt(musics.size()));
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
