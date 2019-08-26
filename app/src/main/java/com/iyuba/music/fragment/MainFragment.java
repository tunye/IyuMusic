package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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

import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.fragmentAdapter.MainFragmentAdapter;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.ChangeUIBroadCast;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.imageview.MorphButton;
import com.iyuba.music.widget.imageview.TabIndicator;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.view.CircleImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by 10202 on 2015/11/6.
 */
public class MainFragment extends BaseFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private ViewPager viewPager;
    private Context context;
    private StandardPlayer player;
    private TabIndicator viewPagerIndicator;
    //控制栏
    private CircleImageView pic;
    private RoundProgressBar progressBar;
    private TextView curArticleTitle, curArticleInfo;
    private MorphButton pause;
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
        ArrayList<String> title = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title)));
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPagerIndicator = (TabIndicator) view.findViewById(R.id.tab_indicator);
        viewPagerIndicator.setTabItemTitles(title);
        viewPagerIndicator.setViewPager(viewPager, 0);
        viewPagerIndicator.setHighLightColor(GetAppColor.getInstance().getAppColor());
        initPlayControl(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainFragmentAdapter mainFragmentAdapter = new MainFragmentAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(mainFragmentAdapter);
        broadCast = new MainChangeUIBroadCast(this);
        IntentFilter intentFilter = new IntentFilter("com.iyuba.music.main");
        context.registerReceiver(broadCast, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setContent();
        setImageState();
        handler.sendEmptyMessage(0);
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
        pause.setOnClickListener(null);
//        mainFragmentAdapter.destroy();
    }

    private void initPlayControl(View root) {
        pic = root.findViewById(R.id.song_image);
        progressBar = root.findViewById(R.id.progressbar);
        progressBar.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
        progressBar.setMax(100);
        curArticleTitle = root.findViewById(R.id.curarticle_title);
        curArticleInfo = root.findViewById(R.id.curarticle_info);
        ImageView former = root.findViewById(R.id.main_former);
        ImageView latter = root.findViewById(R.id.main_latter);
        pause = root.findViewById(R.id.main_play);
        pause.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(), PorterDuff.Mode.SRC_IN);
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
        if (ConfigManager.getInstance().isAutoRound()) {
            initAnimation();
        }
    }

    private void setContent() {
        Article curArticle = StudyManager.getInstance().getCurArticle();
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
                    ImageUtil.loadImage("http://static.iyuba.cn/images/song/" + curArticle.getPicUrl(), pic, R.mipmap.ic_launcher);
                    break;
                default:
                    curArticleTitle.setText(curArticle.getTitle());
                    curArticleInfo.setText(curArticle.getTitle_cn());
                    ImageUtil.loadImage(curArticle.getPicUrl(), pic, R.mipmap.ic_launcher);
                    break;
            }
        }
    }

    private void initAnimation() {
        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }

    private void startAnimation() {
        if (ConfigManager.getInstance().isAutoRound()) {
            if (operatingAnim == null) {
                initAnimation();
            }
            pic.startAnimation(operatingAnim);
        }
    }

    private void pauseAnimation() {
        if (ConfigManager.getInstance().isAutoRound()) {
            pic.clearAnimation();
        }
    }

    private void pauseClick() {
        context.sendBroadcast(new Intent("iyumusic.pause"));
    }

    private void formerClick() {
        context.sendBroadcast(new Intent("iyumusic.before"));
        pause.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContent();
            }
        }, 500);
        if (!player.isPlaying()) {
            context.sendBroadcast(new Intent("iyumusic.pause"));
        }
    }

    private void latterClick() {
        context.sendBroadcast(new Intent("iyumusic.next"));
        pause.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContent();
            }
        }, 500);
        if (!player.isPlaying()) {
            context.sendBroadcast(new Intent("iyumusic.pause"));
        }
    }

    private void setImageState() {
        if (RuntimeManager.getApplication().getPlayerService() == null) {
            pause.setState(MorphButton.PAUSE_STATE);
        } else {
            player = RuntimeManager.getApplication().getPlayerService().getPlayer();
            if (player == null) {
                pause.setState(MorphButton.PAUSE_STATE);
            } else if (player.isPlaying()) {
                pause.setState(MorphButton.PLAY_STATE);
            } else {
                pause.setState(MorphButton.PAUSE_STATE);
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
                    if (fragment.player != null) {
                        fragment.progressBar.setProgress(fragment.player.getCurrentPosition() * 100 / fragment.player.getDuration());
                        if (fragment.player.isPlaying()) {
                            if (fragment.pic.getAnimation() == null) {
                                fragment.startAnimation();
                            }
                            if (fragment.pause.getState() == MorphButton.PAUSE_STATE) {
                                fragment.pause.setState(MorphButton.PLAY_STATE);
                            }
                        } else if (!fragment.player.isPlaying()) {
                            if (fragment.pic.getAnimation() != null) {
                                fragment.pauseAnimation();
                            }
                            if (fragment.pause.getState() == MorphButton.PLAY_STATE) {
                                fragment.pause.setState(MorphButton.PAUSE_STATE);
                            }
                        }
                    }
                    fragment.handler.sendEmptyMessageDelayed(0, 500);
                    break;
            }
        }
    }

    public static class MainChangeUIBroadCast extends ChangeUIBroadCast {
        private final WeakReference<MainFragment> mWeakReference;

        public MainChangeUIBroadCast(MainFragment fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void refreshUI(String message) {
            if (mWeakReference.get() != null) {
                switch (message) {
                    case "change":
                        mWeakReference.get().setContent();
                        break;
                    case "pause":
                        mWeakReference.get().setImageState();
                        break;
                }
            }
        }
    }
}
