package com.iyuba.music.local_music;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.file.FilePosActivity;
import com.iyuba.music.listener.ChangeUIBroadCast;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 10202 on 2016/4/16.
 */
public class LocalMusicActivity extends BaseActivity implements IOnClickListener, IPlayerListener {
    private RecyclerView musicList;
    private ArrayList<Article> musics;
    private LocalMusicAdapter adapter;
    private StandardPlayer player;
    private TextView statistic, randomPlay;
    private ImageView playMode, next, before;
    private ProgressBar progressBar;
    private TextView currentTime;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            progressBar.setProgress(player.getCurrentPosition());
            currentTime.setText(Mathematics.formatTime(player.getCurrentPosition() / 1000));
            handler.sendEmptyMessageDelayed(0, 1000);
            return false;
        }
    });
    private MorphButton pause;
    private LocalMusicChangeUIBroadCast broadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eggshell_music_main);
        context = this;
        if (((MusicApplication) getApplication()).onlyForeground("LocalMusicActivity")) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.putExtra("autoStart", false);
            startActivityForResult(intent, 102);
        } else {
            player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
            ((MusicApplication) getApplication()).getPlayerService().setListener(this);
        }
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onBackPressed() {
        if (((MusicApplication) getApplication()).onlyForeground("LocalMusicActivity")) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        statistic = (TextView) findViewById(R.id.music_statistic);
        randomPlay = (TextView) findViewById(R.id.music_random_play);
        currentTime = (TextView) findViewById(R.id.current_time);
        pause = (MorphButton) findViewById(R.id.play);
        before = (ImageView) findViewById(R.id.formmer);
        next = (ImageView) findViewById(R.id.latter);
        playMode = (ImageView) findViewById(R.id.play_mode);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        musicList = (RecyclerView) findViewById(R.id.music_recyclerview);
        musicList.setLayoutManager(new LinearLayoutManager(context));
        musicList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        adapter = new LocalMusicAdapter(context);
        adapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyManager.instance.setStartPlaying(true);
                StudyManager.instance.setListFragmentPos(LocalMusicActivity.this.getClass().getName());
                StudyManager.instance.setSourceArticleList(musics);
                StudyManager.instance.setCurArticle(musics.get(position));
                adapter.setCurPos(position);
                ((MusicApplication) getApplication()).getPlayerService().startPlay(
                        StudyManager.instance.getCurArticle(), false);
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
        findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, FilePosActivity.class), 101);
            }
        });
        randomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomPlay();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StudyManager.instance.getApp().equals("101")) {
                    sendBroadcast(new Intent("iyumusic.pause"));
                } else {
                    randomPlay();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(0);
                ((MusicApplication) getApplication()).getPlayerService().next(false);
                ((MusicApplication) getApplication()).getPlayerService().startPlay(
                        StudyManager.instance.getCurArticle(), false);
            }
        });
        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(0);
                ((MusicApplication) getApplication()).getPlayerService().before();
                ((MusicApplication) getApplication()).getPlayerService().startPlay(
                        StudyManager.instance.getCurArticle(), false);
            }
        });
        playMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextMusicType = SettingConfigManager.instance.getStudyPlayMode();
                nextMusicType = (nextMusicType + 1) % 3;
                SettingConfigManager.instance.setStudyPlayMode(nextMusicType);
                StudyManager.instance.generateArticleList();
                setPlayModeImage(nextMusicType);
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_local_music);
        toolbarOper.setText(R.string.eggshell_scan);
        pause.setForegroundColorFilter(GetAppColor.instance.getAppColor(context), PorterDuff.Mode.SRC_IN);
        musics = new ArrayList<>();
        if (!TextUtils.isEmpty(ConfigManager.instance.loadString("localMusicPath"))) {
            musics.addAll(MusicUtils.getAllSongs(context, ConfigManager.instance.loadString("localMusicPath")));
        }
        adapter.setDataSet(musics);
        statistic.setText(context.getString(R.string.eggshell_music_static, musics.size()));
        setPlayModeImage(SettingConfigManager.instance.getStudyPlayMode());
        refresh();
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
            musics = new ArrayList<>();
            musics.addAll(MusicUtils.getAllSongs(context, path));
            ConfigManager.instance.putString("localMusicPath", path);
            adapter.setDataSet(musics);
            statistic.setText(context.getString(R.string.eggshell_music_static, musics.size()));
        } else if (requestCode == 102) {
            player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
            ((MusicApplication) getApplication()).getPlayerService().setListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        broadCast = new LocalMusicChangeUIBroadCast();
        IntentFilter intentFilter = new IntentFilter("com.iyuba.music.localmusic");
        registerReceiver(broadCast, intentFilter);
        setPauseImage();
        if (player != null && StudyManager.instance.getApp().equals("101")) {
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
        handler.removeMessages(0);
    }

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
        handler.removeMessages(0);
        ((MusicApplication) getApplication()).getPlayerService().startPlay(
                StudyManager.instance.getCurArticle(), false);
        player.start();
    }

    @Override
    public void onError() {
    }

    private void setPauseImage() {
        if (player == null || !StudyManager.instance.getApp().equals("101")) {
            pause.setState(MorphButton.MorphState.START);
        } else if (player.isPlaying()) {
            pause.setState(MorphButton.MorphState.END, true);
            handler.sendEmptyMessage(0);
            refresh();
        } else {
            pause.setState(MorphButton.MorphState.START, true);
            handler.removeMessages(0);
            adapter.setCurPos(-1);
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

    private void randomPlay() {
        int position = new Random().nextInt(musics.size());
        StudyManager.instance.setSourceArticleList(musics);
        StudyManager.instance.setListFragmentPos(LocalMusicActivity.this.getClass().getName());
        StudyManager.instance.setCurArticle(musics.get(position));
        StudyManager.instance.setStartPlaying(true);
        musicList.scrollToPosition(position);
        adapter.setCurPos(position);
        ((MusicApplication) getApplication()).getPlayerService().startPlay(
                StudyManager.instance.getCurArticle(), false);
    }

    private void refresh() {
        if (LocalMusicActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
            Article article = StudyManager.instance.getCurArticle();
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

    public class LocalMusicChangeUIBroadCast extends ChangeUIBroadCast {
        @Override
        public void refreshUI(String message) {
            switch (message) {
                case "change":
                    refresh();
                    break;
                case "pause":
                    setPauseImage();
                    break;
            }
        }
    }
}
