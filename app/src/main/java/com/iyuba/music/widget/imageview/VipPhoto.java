package com.iyuba.music.widget.imageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iyuba.music.R;
import com.iyuba.music.util.ImageUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2017/2/27.
 */

public class VipPhoto extends RelativeLayout {
    private Context context;

    public VipPhoto(Context context) {
        super(context);
        this.context = context;
    }

    public VipPhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public VipPhoto(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(String userid, boolean isVip) {
        View root = LayoutInflater.from(context).inflate(R.layout.vip_photo, null);
        CircleImageView circleImageView = (CircleImageView) root.findViewById(R.id.vip_photo_img);
        ImageView vipStatus = (ImageView) root.findViewById(R.id.vip_photo_status);
        ImageUtil.loadAvatar(userid, circleImageView);
        if (isVip) {
            vipStatus.setVisibility(VISIBLE);
        } else {
            vipStatus.setVisibility(GONE);
        }
        addView(root);
    }
}
