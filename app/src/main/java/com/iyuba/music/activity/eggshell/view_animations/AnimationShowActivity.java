package com.iyuba.music.activity.eggshell.view_animations;

import android.animation.Animator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.view.CustomToast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.widget.animator.SimpleAnimatorListener;

public class AnimationShowActivity extends BaseActivity {
    private View mTarget;
    private YoYo.YoYoString rope;
    private EffectAdapter ownerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.eggshell_animation_activity_my;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        RecyclerView owner = findViewById(R.id.animation_list);
        mTarget = findViewById(R.id.animation_example);
        ownerAdapter = new EffectAdapter(this);
        setRecyclerViewProperty(owner);
        owner.setAdapter(ownerAdapter);
    }

    @Override
    public void setListener() {
        super.setListener();
        mTarget.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (rope != null) {
                    rope.stop(true);
                }
            }
        });
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Techniques technique = Techniques.values()[i];
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
