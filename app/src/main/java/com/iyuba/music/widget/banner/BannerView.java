package com.iyuba.music.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.util.ImageUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by 102 on 2016/10/11.
 */

public class BannerView extends RelativeLayout {
    private ViewPager bannerViewPager;
    private MyAdapter myAdapter;
    private TextView bannerTitle;
    private LinearLayout dotLayout;
    private List<ImageView> bannerImages;
    private List<ImageView> dots;
    private List<BannerEntity> bannerData;
    private int currentItem = 0;
    private boolean isLooping = false;
    private ScheduledExecutorService scheduledExecutorService;

    private int selectItemColor = 0xffffffff, unselectedItemColor = 0xff808080;
    private int shadowColor = 0x66000000;
    private boolean isAutoStart;
    @Align
    private int align;
    private IOnClickListener onClickListener;

    public BannerView(Context context) {
        super(context);
        init();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttr(attrs);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttr(attrs);
    }

    public void setSelectItemColor(int selectItemColor) {
        this.selectItemColor = selectItemColor;
    }

    public void setUnselectedItemColor(int unselectedItemColor) {
        this.unselectedItemColor = unselectedItemColor;
    }

    private void setAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.bannerview);
        selectItemColor = a.getColor(R.styleable.bannerview_banner_select_color, selectItemColor);
        unselectedItemColor = a.getColor(R.styleable.bannerview_banner_unselected_color, unselectedItemColor);
        shadowColor = a.getColor(R.styleable.bannerview_banner_shadow_color, shadowColor);
        isAutoStart = a.getBoolean(R.styleable.bannerview_banner_auto_start, true);
        int alignInt = a.getInt(R.styleable.bannerview_banner_align, 1);
        switch (alignInt) {
            case 0:
                align = LEFT;
                break;
            case 1:
                align = CENTER;
                break;
            case 2:
                align = RIGHT;
                break;
        }
        a.recycle();
        init();
    }

    private void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.banner, this, true);
        bannerViewPager = (ViewPager) root.findViewById(R.id.banner_vp);
        bannerTitle = (TextView) root.findViewById(R.id.banner_title);
        View bannerShadow = root.findViewById(R.id.banner_shadow);
        bannerShadow.setBackgroundColor(shadowColor);
        dotLayout = (LinearLayout) root.findViewById(R.id.banner_dot);

        dots = new ArrayList<>();
        bannerImages = new ArrayList<>();
        myAdapter = new MyAdapter();
        bannerViewPager.setAdapter(myAdapter);// 设置填充ViewPager页面的适配器
        bannerViewPager.addOnPageChangeListener(new MyPageChangeListener());
        bannerViewPager.setPageTransformer(true, new DepthPageTransformer());

        RelativeLayout.LayoutParams params;
        switch (align) {
            case LEFT:
                bannerTitle.setVisibility(VISIBLE);
                params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                dotLayout.setLayoutParams(params);
                params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                bannerTitle.setLayoutParams(params);
                break;
            case RIGHT:
                bannerTitle.setVisibility(VISIBLE);
                params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                dotLayout.setLayoutParams(params);
                params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                bannerTitle.setLayoutParams(params);
                break;
            case CENTER:
                bannerTitle.setVisibility(GONE);
                params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                dotLayout.setLayoutParams(params);
                break;
        }
    }

    public void initData(List<BannerEntity> mDatas, IOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        bannerData = mDatas;
        dots = new ArrayList<>();
        bannerImages = new ArrayList<>();
        dotLayout.removeAllViews();
        if (mDatas.size() != 0) {
            ImageView imageView = new ImageView(getContext());
            loadData(bannerData.get(mDatas.size() - 1), imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bannerImages.add(0, imageView);
            if (mDatas.size() > 1) {
                ImageView pointView;
                for (int count = 0; count < mDatas.size(); count++) {
                    // 翻页指示的点
                    pointView = new ImageView(getContext());
                    pointView.setPadding(8, 0, 8, 0);
                    if (dots.isEmpty())
                        pointView.setImageDrawable(createPoint(selectItemColor));
                    else
                        pointView.setImageDrawable(createPoint(unselectedItemColor));
                    dots.add(pointView);
                    dotLayout.addView(pointView);
                    imageView = new ImageView(getContext());
                    loadData(bannerData.get(count), imageView);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    bannerImages.add(imageView);
                }
                imageView = new ImageView(getContext());
                loadData(bannerData.get(0), imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bannerImages.add(imageView);
                bannerTitle.setText(bannerData.get(0).getDesc());
                myAdapter.notifyDataSetChanged();
                bannerViewPager.setCurrentItem(1);
                if (isAutoStart) {
                    startAd();
                }
            } else {
                ImageView pointView = new ImageView(getContext());
                pointView.setPadding(8, 0, 8, 0);
                pointView.setImageDrawable(createPoint(selectItemColor));
                dotLayout.addView(pointView);
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    public void startAd() {
        if (!isLooping && bannerData.size() > 1) {
            isLooping = true;
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 3, 3, TimeUnit.SECONDS);
        }
    }

    public void stopAd() {
        if (isLooping) {
            isLooping = false;
            scheduledExecutorService.shutdown();
        }
    }

    private void loadData(BannerEntity data, ImageView imageView) {
        if (data.getOwnerid().equals("2")) {
            imageView.setBackgroundResource(Integer.parseInt(data.getPicUrl()));
        } else {
            imageView.setBackgroundResource(R.drawable.default_music);
            ImageUtil.loadImage("http://app.iyuba.com/dev/" + data.getPicUrl(), imageView, R.drawable.default_music);
        }
    }

    private Drawable createPoint(int color) {
        int size = (int) (getResources().getDisplayMetrics().density * 8 + 0.5f);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);

        canvas.drawCircle(size * 1.0f / 2, size * 1.0f / 2, size * 1.0f / 2, paint);
        drawable.draw(canvas);

        return drawable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            startAd();
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            stopAd();
        }
        return super.dispatchTouchEvent(ev);
    }

    public static final int LEFT = 0x01;
    public static final int CENTER = 0x02;
    public static final int RIGHT = 0x03;

    @IntDef({LEFT, CENTER, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Align {
    }

    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            if (bannerImages.size() > 1) {
                currentItem = currentItem % (bannerImages.size() - 2) + 1;
                bannerViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        bannerViewPager.setCurrentItem(currentItem, currentItem != 0);
                    }
                });
            }
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            if (bannerData.size() > 1) {
                if (position < 1) {
                    position = bannerData.size();
                    bannerViewPager.setCurrentItem(position, false);
                } else if (position > bannerData.size()) {
                    bannerViewPager.setCurrentItem(1, false);
                    position = 1;
                }
                currentItem = position - 1 + bannerData.size();
                dots.get(oldPosition).setImageDrawable(createPoint(unselectedItemColor));
                oldPosition = (currentItem) % bannerData.size();
                bannerTitle.setText(bannerData.get(oldPosition).getDesc());
                dots.get(oldPosition).setImageDrawable(createPoint(selectItemColor));
            }
        }
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return bannerImages.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = bannerImages.get(position);
            container.addView(iv);
            final int pos = position - 1 + bannerData.size();
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick(v, bannerData.get(pos % bannerData.size()));
                    }
                }
            });
            return iv;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(bannerImages.get(arg1));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
