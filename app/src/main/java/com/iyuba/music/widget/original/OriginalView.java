package com.iyuba.music.widget.original;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.original.Original;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/16.
 */
public class OriginalView extends ScrollView implements
        TextSelectCallBack {
    private Context context;
    private boolean showChinese;
    //字号
    private int textSize;
    private LinearLayout subtitleLayout;
    private ArrayList<Original> originalList;
    private TextSelectCallBack textSelectCallBack;

    public OriginalView(Context context) {
        super(context);
        this.context = context;
        showChinese = true;
        textSize = RuntimeManager.getInstance().sp2px(14);
        init();
    }

    public OriginalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.originalsynview);
            showChinese = a.getBoolean(R.styleable.originalsynview_ori_showchinese, true);
            textSize = a.getDimensionPixelSize(R.styleable.originalsynview_ori_textsize, RuntimeManager.getInstance().sp2px(14));
            a.recycle();
        } else {
            showChinese = true;
            textSize = RuntimeManager.getInstance().sp2px(14);
        }
        init();
    }

    public OriginalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.originalsynview);
            showChinese = a.getBoolean(R.styleable.originalsynview_ori_showchinese, true);
            textSize = a.getDimensionPixelSize(R.styleable.originalsynview_ori_textsize, RuntimeManager.getInstance().sp2px(14));
            a.recycle();
        } else {
            showChinese = true;
            textSize = RuntimeManager.getInstance().sp2px(14);
        }
        init();
    }

    private void init() {
        setVerticalScrollBarEnabled(false);
        subtitleLayout = new LinearLayout(context);
        subtitleLayout.setOrientation(LinearLayout.VERTICAL);
    }

    private void initData() {
        subtitleLayout.removeAllViews();
        removeAllViews();
        int size = originalList.size();
        TextPage tp;
        for (int i = 0; i < size; i++) {
            tp = new TextPage(context);
            tp.setTextColor(context.getResources().getColor(R.color.text_color));
            tp.setTextSize(RuntimeManager.getInstance().px2sp(textSize));
            tp.setLineSpacing(0, 1.2f);
            if (isShowChinese()) {
                tp.setText(originalList.get(i).getSentence() + "\n" + originalList.get(i).getSentence_cn());
            } else {
                tp.setText(originalList.get(i).getSentence());
            }
            tp.setTextpageSelectTextCallBack(this);
            subtitleLayout.addView(tp);
        }
        addView(subtitleLayout);
    }

    public void synchroLanguage() {
        int size = originalList.size();
        TextPage textPage;
        for (int i = 0; i < size; i++) {
            textPage = (TextPage) subtitleLayout.getChildAt(i);
            if (isShowChinese()) {
                textPage.setText(originalList.get(i).getSentence() + "\n" + originalList.get(i).getSentence_cn());
            } else {
                textPage.setText(originalList.get(i).getSentence());
            }
        }
    }

    public boolean isShowChinese() {
        return showChinese;
    }

    public void setShowChinese(boolean showChinese) {
        this.showChinese = showChinese;
    }

    public ArrayList<Original> getOriginalList() {
        return originalList;
    }

    public void setOriginalList(ArrayList<Original> originalList) {
        this.originalList = originalList;
        initData();
    }

    public void setTextSize(int textSize) {
        this.textSize = RuntimeManager.getInstance().sp2px(textSize);
    }

    public void setTextSelectCallBack(TextSelectCallBack textSelectCallBack) {
        this.textSelectCallBack = textSelectCallBack;
    }

    @Override
    public void onSelectText(String text) {
        if (textSelectCallBack != null) {
            textSelectCallBack.onSelectText(text);
        }
    }

    public void destroy() {
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
