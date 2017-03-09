package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.fragmentAdapter.MainFragmentAdapter;
import com.iyuba.music.listener.ChangeUIBroadCast;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.imageview.TabIndicator;
import com.iyuba.music.widget.player.StandardPlayer;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.iyuba.music.manager.RuntimeManager.getApplication;


/**
 * Created by 10202 on 2015/11/6.
 */
public class MainFragment extends BaseFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private Context context;
    private StandardPlayer player;
    private TabIndicator viewPagerIndicator;
    //控制栏
    private CircleImageView pic;
    private RoundProgressBar progressBar;
    private TextView curArticleTitle, curArticleInfo;
    private MorphButton pause;
    private Article curArticle;
    private Animation operatingAnim;
    private MainChangeUIBroadCast broadCast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, null);
        ArrayList<String> title = new ArrayList<>();
        title.addAll(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title)));
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPagerIndicator = (TabIndicator) view.findViewById(R.id.tab_indicator);
        viewPager.setAdapter(new MainFragmentAdapter(getActivity().getSupportFragmentManager()));
        viewPagerIndicator.setTabItemTitles(title);
        viewPagerIndicator.setViewPager(viewPager, 0);
        viewPagerIndicator.setHighLightColor(GetAppColor.getInstance().getAppColor(context));
        initPlayControl(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        broadCast = new MainChangeUIBroadCast();
        IntentFilter intentFilter = new IntentFilter("com.iyuba.music.main");
        context.registerReceiver(broadCast, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setContent();
        setImageState(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseAnimation();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(broadCast);
    }

    private void initPlayControl(View root) {
        pic = (CircleImageView) root.findViewById(R.id.song_image);
        progressBar = (RoundProgressBar) root.findViewById(R.id.progressbar);
        progressBar.setCricleProgressColor(GetAppColor.getInstance().getAppColor(context));
        progressBar.setMax(100);
        curArticleTitle = (TextView) root.findViewById(R.id.curarticle_title);
        curArticleInfo = (TextView) root.findViewById(R.id.curarticle_info);
        ImageView former = (ImageView) root.findViewById(R.id.main_former);
        ImageView latter = (ImageView) root.findViewById(R.id.main_latter);
        pause = (MorphButton) root.findViewById(R.id.main_play);
        pause.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(context), PorterDuff.Mode.SRC_IN);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseClick();
            }
        });
        former.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formerClick();
            }
        });
        latter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latterClick();
            }
        });
        root.findViewById(R.id.rotate_image_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("101".equals(StudyManager.getInstance().getApp())) {
                    context.startActivity(new Intent(context, LocalMusicActivity.class));
                } else {
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }
        });
        root.findViewById(R.id.song_info_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("101".equals(StudyManager.getInstance().getApp())) {
                    context.startActivity(new Intent(context, LocalMusicActivity.class));
                } else {
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }
        });
    }

    private void setContent() {
        curArticle = StudyManager.getInstance().getCurArticle();
        if (curArticle == null || TextUtils.isEmpty(curArticle.getTitle())) {
            curArticleTitle.setText(R.string.app_name);
            curArticleInfo.setText(R.string.app_intro);
            pic.setImageResource(R.mipmap.ic_launcher);
        } else {
            switch (StudyManager.getInstance().getApp()) {
                case "101":
                    curArticleTitle.setText(curArticle.getTitle());
                    curArticleInfo.setText(curArticle.getSinger());
                    pic.setImageResource(R.mipmap.ic_launcher);
                    break;
                case "209":
                    curArticleTitle.setText(curArticle.getTitle());
                    curArticleInfo.setText(curArticle.getSinger());
                    ImageUtil.loadImage("http://static.iyuba.com/images/song/" + curArticle.getPicUrl(), pic, R.mipmap.ic_launcher);
                    break;
                default:
                    curArticleTitle.setText(curArticle.getTitle());
                    curArticleInfo.setText(curArticle.getTitle_cn());
                    ImageUtil.loadImage("http://static.iyuba.com/images/song/" + curArticle.getPicUrl(), pic, R.mipmap.ic_launcher);
                    break;
            }
        }
        if (SettingConfigManager.getInstance().isAutoRound()) {
            initAnimation();
        }
    }

    private void initAnimation() {
        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }

    private void startAnimation() {
        if (SettingConfigManager.getInstance().isAutoRound()) {
            pic.startAnimation(operatingAnim);
        }
    }

    private void pauseAnimation() {
        if (SettingConfigManager.getInstance().isAutoRound()) {
            pic.clearAnimation();
        }
    }

    private void playNewSong() {
        curArticle = StudyManager.getInstance().getCurArticle();
        final PlayerService playerService = ((MusicApplication) getApplication()).getPlayerService();
        playerService.startPlay(curArticle, false);
        playerService.setCurArticle(StudyManager.getInstance().getCurArticle());
        player = playerService.getPlayer();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setContent();
                playerService.getPlayer().start();
                startAnimation();
                pause.setState(MorphButton.MorphState.END);
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void pauseClick() {
        if (player == null) {
            playNewSong();
        } else {
            context.sendBroadcast(new Intent("iyumusic.pause"));
        }
    }

    private void formerClick() {
        if (player == null) {
            playNewSong();
        } else {
            context.sendBroadcast(new Intent("iyumusic.before"));
            pause.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setContent();
                }
            }, 500);
            if (!player.isPlaying()) {
                startAnimation();
                pause.setState(MorphButton.MorphState.END, true);
                handler.sendEmptyMessage(0);
                context.sendBroadcast(new Intent("iyumusic.pause"));
            }
        }
    }

    private void latterClick() {
        if (player == null) {
            StudyManager.getInstance().next();
            playNewSong();
        } else {
            context.sendBroadcast(new Intent("iyumusic.next"));
            pause.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setContent();
                }
            }, 500);
            if (!player.isPlaying()) {
                startAnimation();
                pause.setState(MorphButton.MorphState.START, true);
                handler.sendEmptyMessage(0);
                context.sendBroadcast(new Intent("iyumusic.pause"));
            }
        }
    }

    private void setImageState(boolean animation) {
        if (((MusicApplication) getApplication()).getPlayerService() == null) {
            pause.setState(MorphButton.MorphState.START);
        } else {
            player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
            if (player == null) {
                pause.setState(MorphButton.MorphState.START);
            } else if (player.isPlaying()) {
                startAnimation();
                pause.setState(MorphButton.MorphState.END, animation);
                handler.sendEmptyMessage(0);
            } else {
                handler.removeMessages(0);
                pauseAnimation();
                pause.setState(MorphButton.MorphState.START, animation);
            }
        }
    }

    public void setShowItem(int pos) {
        viewPagerIndicator.setPosDirect(pos);
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<MainFragment> {
        @Override
        public void handleMessageByRef(final MainFragment fragment, Message msg) {
            switch (msg.what) {
                case 0:
                    fragment.progressBar.setProgress(fragment.player.getCurrentPosition() * 100 / fragment.player.getDuration());
                    fragment.handler.sendEmptyMessageDelayed(0, 1000);
                    break;
            }
        }
    }

    public class MainChangeUIBroadCast extends ChangeUIBroadCast {
        @Override
        public void refreshUI(String message) {
            switch (message) {
                case "change":
                    setContent();
                    break;
                case "pause":
                    setImageState(true);
                    break;
            }
        }
    }
}
