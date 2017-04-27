package com.iyuba.music.adapter.study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;

/**
 * Created by 10202 on 2015/10/10.
 */
public class ShareAdapter extends BaseAdapter {
    private static final int DURATION = 400;                        // 动画时长
    private static final int DIALOG_ANIMATION_DELAY = 180;          // 父dialog动画时长
    private static final int ITEM_DELAY = 50;                       // 同行子item延迟动画时长
    private static final float ITEM_SCALE_START = 0F;               // item初始大小
    private static final float ITEM_SCALE_END = 1.0F;               // item结束大小
    private static final float ITEM_ALPHA_START = 0.6F;             // item初始透明度
    private static final float ITEM_ALPHA_END = 1.0F;               // item初始透明度
    private static final int NUM_COLUMNS = 3;                       // 同行子item数量
    private int[] menuImageList;
    private String[] menuList;
    private Context context;

    public ShareAdapter(Context context) {
        this.context = context;
        menuImageList = new int[]{R.drawable.umeng_socialize_wxcircle, R.drawable.umeng_socialize_wechat,
                R.drawable.umeng_socialize_sina, R.drawable.umeng_socialize_qq,
                R.drawable.umeng_socialize_qzone, R.drawable.umeng_socialize_fav,};
        menuList = context.getResources().getStringArray(R.array.share);
    }

    @Override
    public Object getItem(int pos) {

        return menuList[pos];
    }

    @Override
    public int getCount() {

        return menuList.length;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate
                    (R.layout.item_share, parent, false);
            holder = new ViewHolder();
            holder.menuText = (TextView) convertView.findViewById(R.id.item_text);
            holder.menuImage = (ImageView) convertView.findViewById(R.id.item_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.menuText.setText(menuList[position]);
        holder.menuImage.setImageResource(menuImageList[position]);
        holder.menuImage.setScaleX(ITEM_SCALE_START);
        holder.menuImage.setScaleY(ITEM_SCALE_START);
        holder.menuImage.setAlpha(ITEM_ALPHA_START);
        holder.menuImage.animate()
                .scaleX(ITEM_SCALE_END)
                .scaleY(ITEM_SCALE_END)
                .alpha(ITEM_ALPHA_END)
                .setDuration(DURATION)
                .setStartDelay(DIALOG_ANIMATION_DELAY + (position % NUM_COLUMNS) * ITEM_DELAY)
                .setInterpolator(new OvershootInterpolator()).start();
        return convertView;
    }

    private static class ViewHolder {
        TextView menuText;
        ImageView menuImage;
    }
}
