package com.iyuba.music.activity.eggshell.view_animations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.iyuba.music.R;

public class EffectAdapter extends BaseAdapter {

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
    private Context mContext;

    public EffectAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return Techniques.values().length;
    }

    @Override
    public Object getItem(int position) {
        return Techniques.values()[position].getAnimator();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.eggshell_animation_item, null, false);
        TextView t = (TextView) v.findViewById(R.id.list_item_text);
        Object o = getItem(position);
//        int start = o.getClass().getName().lastIndexOf(".") + 1;
//        String name = o.getClass().getName().substring(start);
        t.setText(effects[position]);
        v.setTag(Techniques.values()[position]);
        return v;
    }
}
