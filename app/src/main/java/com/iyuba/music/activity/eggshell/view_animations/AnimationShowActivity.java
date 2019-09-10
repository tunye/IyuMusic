package com.iyuba.music.activity.eggshell.view_animations;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.view.CustomToast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.widget.animator.SimpleAnimatorListener;

public class AnimationShowActivity extends BaseActivity {

    private ListView mListView;
    private EffectAdapter mAdapter;
    private View mTarget;
    private YoYo.YoYoString rope;

    @Override
    public int getLayoutId() {
        return R.layout.eggshell_animation_activity_my;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mListView = findViewById(R.id.animation_list);
        mTarget = findViewById(R.id.animation_example);
        EffectAdapter mAdapter = new EffectAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        super.setListener();
        mTarget.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                if (rope != null) {
                    rope.stop(true);
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Techniques technique = (Techniques) view.getTag();
                rope = YoYo.with(technique)
                        .duration(1200)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .withListener(new SimpleAnimatorListener() {

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                CustomToast.getInstance().showToast("last yoyo canceled");
                            }
                        })
                        .playOn(mTarget);
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText("Android Animation");
        rope = YoYo.with(Techniques.FadeIn).duration(1000).playOn(mTarget);
    }
}
