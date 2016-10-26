package com.iyuba.music.widget.recycleview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by 10202 on 2016/3/5.
 */
public class StationaryGridview extends GridView {
    public StationaryGridview(Context context) {
        super(context);
    }

    public StationaryGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StationaryGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
