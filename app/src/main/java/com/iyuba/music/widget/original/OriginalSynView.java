package com.iyuba.music.widget.original;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.iyuba.music.R;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/16.
 */
public class OriginalSynView extends ScrollView implements
        TextSelectCallBack {
    private Context context;
    //是否是拖拽状态
    private boolean canDrag;
    //是否画时间线
    private boolean drawTimeLine;
    //显示中文
    private boolean showChinese;
    //字号
    private int textSize;
    //线宽
    private float lineWidth;
    //拖拽页面变量
    private float rectPadding, rectMarginBottom, rectRadius;
    //线性布局，添加每一句话
    private LinearLayout subtitleLayout;
    //原文列表
    private ArrayList<Original> originalList;
    //拖拽使用变量
    private float[] oldXY;
    //当前段，上一段落
    private int currParagraph, lastParagraph;
    //整个类通用的textpage
    private TextPage textPage;
    //画时间线控件
    private Rect textBounds;
    private RectF rf;
    private Paint rectPaint, mPaintForTimeLine;
    //实现接口
    private TextSelectCallBack textSelectCallBack;
    private SeekToCallBack seekToCallBack;
    private HighLightTextCallBack highLightTextCallBack;

    public OriginalSynView(Context context) {
        super(context);
        this.context = context;
        defaultProperty();
        init();
    }

    public OriginalSynView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (attrs != null) {
            drawTimeLine = false;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.originalsynview);
            showChinese = a.getBoolean(R.styleable.originalsynview_ori_showchinese, true);
            textSize = a.getDimensionPixelSize(R.styleable.originalsynview_ori_textsize, RuntimeManager.sp2px(14));
            lineWidth = a.getFloat(R.styleable.originalsynview_ori_linewidth, 4);
            rectMarginBottom = a.getDimension(R.styleable.originalsynview_ori_timeline_margin, 10);
            rectPadding = a.getDimension(R.styleable.originalsynview_ori_timeline_padding, 20);
            rectRadius = a.getInt(R.styleable.originalsynview_ori_timeline_radius, 15);
            a.recycle();
        } else {
            defaultProperty();
        }
        init();
    }

    public OriginalSynView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        if (attrs != null) {
            drawTimeLine = false;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.originalsynview);
            showChinese = a.getBoolean(R.styleable.originalsynview_ori_showchinese, true);
            textSize = a.getDimensionPixelSize(R.styleable.originalsynview_ori_textsize, RuntimeManager.sp2px(14));
            lineWidth = a.getInt(R.styleable.originalsynview_ori_linewidth, 4);
            rectMarginBottom = a.getDimension(R.styleable.originalsynview_ori_timeline_margin, 10);
            rectPadding = a.getDimension(R.styleable.originalsynview_ori_timeline_padding, 20);
            rectRadius = a.getInt(R.styleable.originalsynview_ori_timeline_radius, 15);
            a.recycle();
        } else {
            defaultProperty();
        }
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawTimeLine) {
            if (originalList == null || originalList.size() == 0) {
                return;
            }
            float y = getHeight() / 2 + getScrollY();
            float itemY;
            for (int i = 1; i <= originalList.size(); i++) {
                itemY = subtitleLayout.getChildAt(i + 1).getTop();
                if (itemY >= y) {
                    currParagraph = i;
                    break;
                }
            }
            drawTimeCanvas(canvas, DateFormat.formatTime((int) originalList.get(currParagraph - 1).getStartTime()), y);
            changeHighLight(currParagraph);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (originalList == null || originalList.size() == 0) {
            performClick();
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (canDrag) {
                    float offset = event.getRawY() - oldXY[1];
                    scrollBy(getScrollX(), -(int) offset);
                    oldXY[1] = event.getRawY();
                    invalidate();
                } else {
                    if (Math.abs(event.getRawX() - oldXY[0]) > 8
                            && Math.abs(event.getRawY() - oldXY[1]) > 8) {
                        canDrag = true;
                        drawTimeLine = true;
                        oldXY[1] = event.getRawY();
                        if (seekToCallBack != null) {
                            seekToCallBack.onSeekStart();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (drawTimeLine) {
                    if (seekToCallBack != null) {
                        seekToCallBack.onSeekTo(originalList.get(currParagraph - 1).getStartTime());
                    }
                    drawTimeLine = false;
                    canDrag = false;
                    scrollToPosition();
                    invalidate();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldXY = new float[]{event.getRawX(), event.getRawY()};
                canDrag = false;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void defaultProperty() {
        showChinese = true;
        drawTimeLine = false;
        textSize = RuntimeManager.sp2px(14);
        lineWidth = 4;
        rectMarginBottom = 10;
        rectPadding = 20;
        rectRadius = 15;
    }

    private void init() {
        setVerticalScrollBarEnabled(false);
        currParagraph = lastParagraph = 1;
        subtitleLayout = new LinearLayout(context);
        subtitleLayout.setOrientation(LinearLayout.VERTICAL);
        //时间线画笔
        mPaintForTimeLine = new Paint();
        mPaintForTimeLine.setColor(GetAppColor.getInstance().getAppColorLight());
        mPaintForTimeLine.setTextSize(textSize + RuntimeManager.sp2px(2));
        mPaintForTimeLine.setStrokeWidth(lineWidth);
        //半透背景画笔
        rectPaint = new Paint();
        rectPaint.setColor(context.getResources().getColor(R.color.text_color));
        textBounds = new Rect();
        rf = new RectF();
    }

    private void initData() {
        subtitleLayout.removeAllViews();
        removeAllViews();
        if (originalList == null || originalList.size() == 0) {
            return;
        }
        int size = originalList.size();
        textPage = new TextPage(context);
        textPage.setHeight(RuntimeManager.getWindowHeight() / 2);
        subtitleLayout.addView(textPage);
        for (int i = 0; i < size; i++) {
            textPage = new TextPage(context);
            textPage.setTextColor(context.getResources().getColor(R.color.text_color));
            textPage.setTextSize(RuntimeManager.px2sp(textSize));
            if (isShowChinese()) {
                textPage.setText(originalList.get(i).getSentence() + "\n" + originalList.get(i).getSentence_cn());
            } else {
                textPage.setText(originalList.get(i).getSentence());
            }
            textPage.setTextpageSelectTextCallBack(this);
            subtitleLayout.addView(textPage);
        }
        textPage = new TextPage(context);
        textPage.setHeight(RuntimeManager.getWindowHeight() / 2);
        subtitleLayout.addView(textPage);
        addView(subtitleLayout);
    }

    private void scrollToPosition() {
        int center = changeHighLight(currParagraph);
        center = center - getHeight() / 2;
        if (center > 0) {
            smoothScrollTo(0, center + RuntimeManager.getWindowHeight() / 2);
        } else {
            smoothScrollTo(0, RuntimeManager.getWindowHeight() / 2);
        }
    }

    public void synchroParagraph(int paragraph) {
        currParagraph = paragraph;
        if (currParagraph == 0) {
            currParagraph = 1;
            scrollToPosition();
        } else if (currParagraph < subtitleLayout.getChildCount()) {
            scrollToPosition();
        }
    }

    public void synchroLanguage() {
        if (originalList == null || originalList.size() == 0) {
            return;
        }
        int size = originalList.size();
        for (int i = 0; i < size; i++) {
            textPage = (TextPage) subtitleLayout.getChildAt(i);
            if (isShowChinese()) {
                textPage.setText(originalList.get(i).getSentence() + "\n" +
                        originalList.get(i).getSentence_cn());
            } else {
                textPage.setText(originalList.get(i).getSentence());
            }
        }
        synchroParagraph(currParagraph);
    }

    private int changeHighLight(int current) {
        textPage = (TextPage) subtitleLayout.getChildAt(lastParagraph);
        if (textPage != null) {
            textPage.setTextColor(context.getResources().getColor(R.color.text_color));
        }
        textPage = (TextPage) subtitleLayout.getChildAt(current);
        if (textPage == null)
            return 0;
        textPage.setTextColor(GetAppColor.getInstance().getAppColorLight());
        lastParagraph = current;
        if (highLightTextCallBack != null) {
            if (originalList != null && current <= originalList.size() && current > 0) {
                highLightTextCallBack.getCurrentHighLightText(originalList.get(current - 1).getSentence()
                        + "\n" + originalList.get(current - 1).getSentence_cn());
            }
        }
        return textPage.getTop() + textPage.getHeight() / 2 - RuntimeManager.getWindowHeight() / 2;
    }

    private void drawTimeCanvas(Canvas canvas, String time, float y) {
        //时间线画笔
        mPaintForTimeLine = new Paint();
        mPaintForTimeLine.setColor(GetAppColor.getInstance().getAppColorLight());
        mPaintForTimeLine.setTextSize(textSize + RuntimeManager.sp2px(2));
        mPaintForTimeLine.setStrokeWidth(lineWidth);
        //半透背景画笔
        rectPaint = new Paint();
        rectPaint.setColor(context.getResources().getColor(R.color.text_color));

        textBounds = new Rect();
        rf = new RectF();
        //计算边距
        mPaintForTimeLine.getTextBounds(time, 0, time.length(), textBounds);
        rf.set(0, y - textBounds.height() - rectPadding * 2 - rectMarginBottom,
                textBounds.width() + rectPadding * 2, y - rectMarginBottom);
        //绘制
        canvas.drawRoundRect(rf, rectRadius, rectRadius, rectPaint);
        canvas.drawLine(0, y, getWidth(), y, mPaintForTimeLine);
        mPaintForTimeLine.setColor(context.getResources().getColor(R.color.background));
        canvas.drawText(time, rectPadding, y - rectPadding - rectMarginBottom - textBounds.height() / 2 + (textBounds.bottom - textBounds.top) / 2, mPaintForTimeLine);
    }

    public boolean isShowChinese() {
        return showChinese;
    }

    public void setShowChinese(boolean showChinese) {
        this.showChinese = showChinese;
    }

    public void setTextSize(int textSize) {
        this.textSize = RuntimeManager.sp2px(textSize);
    }

    public ArrayList<Original> getOriginalList() {
        return originalList;
    }

    public void setOriginalList(ArrayList<Original> originalList) {
        this.originalList = originalList;
        initData();
    }

    public int getCurrParagraph() {
        return currParagraph;
    }

    public void setCurrParagraph(int currParagraph) {
        this.currParagraph = currParagraph;
    }

    public void setTextSelectCallBack(TextSelectCallBack textSelectCallBack) {
        this.textSelectCallBack = textSelectCallBack;
    }

    public void setSeekToCallBack(SeekToCallBack seekToCallBack) {
        this.seekToCallBack = seekToCallBack;
    }

    public void setHighLightTextCallBack(HighLightTextCallBack highLightTextCallBack) {
        this.highLightTextCallBack = highLightTextCallBack;
    }

    @Override
    public void onSelectText(String text) {
        if (textSelectCallBack != null) {
            textSelectCallBack.onSelectText(text);
        }
    }

    public void destroy() {
        setSeekToCallBack(null);
        setTextSelectCallBack(null);
        View child;
        for (int i = 0; i < subtitleLayout.getChildCount(); i++) {
            child = subtitleLayout.getChildAt(i);
            if (child instanceof TextPage) {
                ((TextPage) child).setTextpageSelectTextCallBack(null);
            }
        }
        subtitleLayout.removeAllViews();
        removeAllViews();
    }
}