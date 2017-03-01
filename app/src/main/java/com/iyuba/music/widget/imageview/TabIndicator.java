package com.iyuba.music.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.GetAppColor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by 10202 on 2015/12/16.
 */
public class TabIndicator extends LinearLayout {
    /**
     * 三角形的宽度为单个Tab的1/5
     */
    private static final float RADIO_TRIANGEL = 1.0f / 5;
    /**
     * 线的宽度为单个Tab的5/7
     */
    private static final float RADIO_LINE = 5.0f / 7;
    /**
     * 默认的Tab数量
     */
    private static final int COUNT_DEFAULT_TAB = 4;
    /**
     * 三角形的最大宽度
     */
    private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 3 * RADIO_TRIANGEL);
    /**
     * 线的最大宽度
     */
    private final int DIMENSION_LINE_WIDTH = (int) (getScreenWidth() / 3 * RADIO_LINE);
    /**
     * 与之绑定的ViewPager
     */
    public ViewPager viewPager;
    private int curPos;
    /**
     * 绘制三角形的画笔
     */
    private Paint paint;
    /**
     * path构成一个指示器形状
     */
    private Path path;
    /**
     * 形的宽度
     */
    private int shapeWidth;
    /**
     * 形的高度
     */
    private int shapeHeight;
    /**
     * 初始时，指示器的偏移量
     */
    private int initTranslationX;
    /**
     * 手指滑动时的偏移量
     */
    private float translationX;
    /**
     * tab数量
     */
    private int visibleTabCount = COUNT_DEFAULT_TAB;
    /**
     * tab上的内容
     */
    private List<String> tabTitles;
    /**
     * 标题正常时的颜色
     */
    private int normalColor = 0x77FFFFFF;
    /**
     * 标题选中时的颜色
     */
    private int highlightColor = 0xFFFFFFFF;
    /**
     * 指示器的颜色
     */
    private int paintColor = 0xFFFFFFFF;
    /**
     * 指示器的形状
     */
    @Shape
    private int drawShape = TRIANGLE;
    // 对外的ViewPager的回调接口
    private PageChangeListener onPageChangeListener;

    public TabIndicator(Context context) {
        this(context, null);
        initPaint();
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获得自定义属性，tab的数量
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TabIndicator);
        visibleTabCount = a.getInt(R.styleable.TabIndicator_tab_item_count, COUNT_DEFAULT_TAB);
        int style = a.getInt(R.styleable.TabIndicator_tab_shape, 0);
        if (style == 0) {
            drawShape = TRIANGLE;
        } else {
            drawShape = LINE;
        }
        normalColor = a.getColor(R.styleable.TabIndicator_tab_normal_color, context.getResources().getColor(R.color.text_gray_color));
        highlightColor = a.getColor(R.styleable.TabIndicator_tab_highlight_color, GetAppColor.getInstance().getAppColor(context));
        paintColor = a.getColor(R.styleable.TabIndicator_tab_indicator_color, GetAppColor.getInstance().getAppColor(context));
        if (visibleTabCount < 0)
            visibleTabCount = COUNT_DEFAULT_TAB;
        a.recycle();
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(paintColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(new CornerPathEffect(3));
    }

    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        // 画笔平移到正确的位置
        canvas.translate(initTranslationX + translationX, getHeight());
        canvas.drawPath(path, paint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    /**
     * 初始化三角形的宽度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (drawShape==TRIANGLE) {
            shapeWidth = (int) (w / visibleTabCount * RADIO_TRIANGEL);
            shapeWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, shapeWidth);
            // 初始化三角形
            initTriangle();
        } else if (drawShape==LINE) {
            shapeWidth = (int) (w / visibleTabCount * RADIO_LINE);
            shapeWidth = Math.min(DIMENSION_LINE_WIDTH, shapeWidth);
            // 初始化线
            initLine();
        }
        // 初始时的偏移量
        initTranslationX = getWidth() / visibleTabCount / 2 - shapeWidth / 2;
    }

    public void setDrawShape(@Shape int drawShape) {
        this.drawShape = drawShape;
    }

    public void setVisibleTabCount(int count) {
        this.visibleTabCount = count;
    }

    public void setHighLightColor(int color) {
        this.highlightColor = color;
        this.paintColor = color;
        highLightTextView(curPos);
        initPaint();
        invalidate();
    }

    public void setTabItemTitles(List<String> datas) {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.size() > 0) {
            this.removeAllViews();
            this.tabTitles = datas;

            for (String title : tabTitles) {
                // 添加view
                addView(generateTextView(title));
            }
            // 设置item的click事件
            setItemClickEvent();
        }
    }

    // 对外的ViewPager的回调接口的设置
    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }

    // 设置关联的ViewPager
    public void setViewPager(ViewPager viewPager, int pos) {
        this.viewPager = viewPager;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 设置字体颜色高亮
                resetTextViewColor();
                curPos = position;
                highLightTextView(position);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // 滚动
                scroll(position, positionOffset);
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position,
                            positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }

            }
        });
        // 设置当前页
        viewPager.setCurrentItem(pos);
        // 高亮
        highLightTextView(pos);
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    protected void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(highlightColor);
        }
    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(normalColor);
            }
        }
    }

    /**
     * 设置点击事件
     */
    public void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */
    private TextView generateTextView(String text) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / visibleTabCount;
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(normalColor);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 初始化三角形指示器
     */
    private void initTriangle() {
        path = new Path();

        shapeHeight = (int) (shapeWidth / 2 / Math.sqrt(2));
        path.moveTo(0, 0);
        path.lineTo(shapeWidth, 0);
        path.lineTo(shapeWidth / 2, -shapeHeight);
        path.close();
    }

    /**
     * 初始化线指示器
     */
    private void initLine() {
        path = new Path();
        shapeHeight = RuntimeManager.dip2px(2);
        path.moveTo(0, 0);
        path.lineTo(shapeWidth, 0);
        path.lineTo(shapeWidth, -shapeHeight);
        path.lineTo(0, -shapeHeight);
        path.lineTo(0, 0);
        path.close();
    }

    /**
     * 指示器跟随手指滚动，以及容器滚动
     *
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        /**
         * <pre>
         *  0-1:position=0 ;1-0:postion=0;
         * </pre>
         */
        // 不断改变偏移量，invalidate
        translationX = getWidth() / visibleTabCount * (position + offset);

        int tabWidth = getScreenWidth() / visibleTabCount;

        // 容器滚动，当移动到倒数最后一个的时候，开始滚动
        if (offset > 0 && position >= (visibleTabCount - 2)
                && getChildCount() > visibleTabCount) {
            if (visibleTabCount != 1) {
                this.scrollTo((position - (visibleTabCount - 2)) * tabWidth
                        + (int) (tabWidth * offset), 0);
            } else {// 为count为1时 的特殊处理
                this.scrollTo(position * tabWidth + (int) (tabWidth * offset), 0);
            }
        }
        invalidate();
    }

    /**
     * 设置布局中view的一些必要属性；如果设置了setTabTitles，布局中view则无效
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if (cCount == 0)
            return;

        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view
                    .getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / visibleTabCount;
            view.setLayoutParams(lp);
        }
        // 设置点击事件
        setItemClickEvent();

    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        return RuntimeManager.getWindowWidth();
    }

    public static final int LINE = 0x01;
    public static final int TRIANGLE = 0x02;

    @IntDef({LINE, TRIANGLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shape {
    }

    /**
     * 对外的ViewPager的回调接口
     */
    public interface PageChangeListener {
        void onPageScrolled(int position, float positionOffset,
                            int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }
}
