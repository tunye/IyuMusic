package com.iyuba.music.listener;

import android.view.View;

/**
 * Created by 10202 on 2015/10/12.
 */
public interface OnRecycleViewItemClickListener {

    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
