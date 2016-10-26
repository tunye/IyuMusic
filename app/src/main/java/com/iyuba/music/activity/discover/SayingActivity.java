package com.iyuba.music.activity.discover;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.flyco.roundview.RoundTextView;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.word.Saying;
import com.iyuba.music.entity.word.SayingOp;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.widget.textview.JustifyTextView;
import com.nineoldandroids.animation.Animator;

import java.util.Random;

/**
 * Created by 10202 on 2015/12/2.
 */
public class SayingActivity extends BaseActivity {
    private TextView chinese;
    private JustifyTextView english;
    private RoundTextView next;
    private View sayingContent;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    YoYo.with(Techniques.FadeOutRight).duration(300).interpolate(new AccelerateDecelerateInterpolator()).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            handler.sendEmptyMessage(1);
                            YoYo.with(Techniques.FadeInLeft).duration(300).interpolate(new AccelerateDecelerateInterpolator()).playOn(sayingContent);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(sayingContent);
                    break;
                case 1:
                    Saying saying = new SayingOp().findDataById(getRandomId());
                    chinese.setText(saying.getChinese());
                    english.setText(saying.getEnglish());
                    if (SettingConfigManager.instance.getSayingMode() == 0) {
                        handler.removeMessages(0);
                    } else {
                        handler.sendEmptyMessageDelayed(0, 4500);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saying);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        english = (JustifyTextView) findViewById(R.id.saying_english);
        chinese = (TextView) findViewById(R.id.saying_chinese);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        sayingContent = findViewById(R.id.saying_content);
        next = (RoundTextView) findViewById(R.id.saying_next);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingConfigManager.instance.getSayingMode() == 1) {
                    SettingConfigManager.instance.setSayingMode(0);
                } else {
                    SettingConfigManager.instance.setSayingMode(1);
                }
                changeUIResumeByPara();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.word_saying_title);
    }

    protected void changeUIResumeByPara() {
        if (SettingConfigManager.instance.getSayingMode() == 1) {
            toolbarOper.setText(R.string.word_saying_manualchange);
            next.setVisibility(View.INVISIBLE);
        } else {
            toolbarOper.setText(R.string.word_saying_autochange);
            next.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1);
    }

    private int getRandomId() {
        Random rnd = new Random();
        return rnd.nextInt(1000) % 154 + 1;
    }
}
