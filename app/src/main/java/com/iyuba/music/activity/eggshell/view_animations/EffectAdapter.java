package com.iyuba.music.activity.eggshell.view_animations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.iyuba.music.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffectAdapter extends CoreRecyclerViewAdapter<String, EffectAdapter.MyHolderView> {

    private final static String[] effects = {"DropOutAnimator", "LandingAnimator", "TakingOffAnimator", "FlashAnimator",
            "PulseAnimator", "RubberBandAnimator", "ShakeAnimator", "SwingAnimator",
            "WobbleAnimator", "BounceAnimator", "TadaAnimator", "StandUpAnimator",
            "WaveAnimator", "HingeAnimator", "RollInAnimator", "RollOutAnimator",
            "BounceInAnimator", "BounceInDownAnimator", "BounceInLeftAnimator", "BounceInRightAnimator",
            "BounceInUpAnimator", "FadeInAnimator", "FadeInUpAnimator", "FadeInDownAnimator",
            "FadeInLeftAnimator", "FadeInRightAnimator", "FadeOutAnimator", "FadeOutDownAnimator",
            "FadeOutLeftAnimator", "FadeOutRightAnimator", "FadeOutUpAnimator", "FlipInXAnimator",
            "FlipOutXAnimator", "FlipOutYAnimator", "RotateInAnimator", "RotateInDownLeftAnimator",
            "RotateInDownRightAnimator", "RotateInUpLeftAnimator", "RotateInUpRightAnimator", "RotateOutAnimator",
            "RotateOutDownLeftAnimator", "RotateOutDownRightAnimator", "RotateOutUpLeftAnimator", "RotateOutUpRightAnimator",
            "SlideInLeftAnimator", "SlideInRightAnimator", "SlideInUpAnimator", "SlideInDownAnimator",
            "SlideOutLeftAnimator", "SlideOutRightAnimator", "SlideOutUpAnimator", "SlideOutDownAnimator",
            "ZoomInAnimator", "ZoomInDownAnimator", "ZoomInLeftAnimator", "ZoomInRightAnimator",
            "ZoomInUpAnimator", "ZoomOutAnimator", "ZoomOutDownAnimator", "ZoomOutLeftAnimator",
            "ZoomOutRightAnimator", "ZoomOutUpAnimator", "ZoomInDownAnimator",};

    public EffectAdapter(Context context) {
        super(context);
        setDataSet(new ArrayList<>(Arrays.asList(effects)));
    }

    @NonNull
    @Override
    public MyHolderView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyHolderView(LayoutInflater.from(context).inflate(R.layout.eggshell_animation_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolderView holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.textView.setText(effects[position]);
    }

    static class MyHolderView extends CoreRecyclerViewAdapter.MyViewHolder {
        TextView textView;

        MyHolderView(View view) {
            super(view);
            textView = view.findViewById(R.id.list_item_text);
        }
    }
}
