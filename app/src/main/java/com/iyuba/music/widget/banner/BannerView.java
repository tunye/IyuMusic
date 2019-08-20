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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by 102 on 2016/10/11.
 */

public class BannerView extends RelativeLayout {
    public static final int LEFT = 0x01;
    public static final int CENTER = 0x02;
    public static final int RIGHT = 0x03;
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
        myAdapter = new MyAdapter(this);
        bannerViewPager.setAdapter(myAdapter);// 设置填充ViewPager页面的适配器
        bannerViewPager.addOnPageChangeListener(new MyPageChangeListener(this));
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
        if (mDatas != null && mDatas.size() != 0) {
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
            scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(this), 1500, 1500, TimeUnit.MILLISECONDS);
        }
    }

    public void stopAd() {
        if (isLooping) {
            isLooping = false;
            scheduledExecutorService.shutdownNow();
        }
    }

    private void loadData(BannerEntity data, ImageView imageView) {
        if (data.getOwnerid().equals("2")) {
            imageView.setBackgroundResource(Integer.parseInt(data.getPicUrl()));
        } else {
            imageView.setBackgroundResource(R.drawable.default_music);
            ImageUtil.loadImage("http://app.iyuba.cn/dev/" + data.getPicUrl(), imageView, R.drawable.default_music);
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

    public boolean hasData() {
        return bannerData != null && bannerData.size() != 0;
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

    @IntDef({LEFT, CENTER, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Align {
    }

    private static class ScrollTask implements Runnable {
        private final WeakReference<BannerView> mWeakReference;

        public ScrollTask(BannerView bannerView) {
            mWeakReference = new WeakReference<>(bannerView);
        }

        @Override
        public void run() {
            if (mWeakReference.get() != null && mWeakReference.get().bannerImages.size() > 1) {
                final BannerView bannerView = mWeakReference.get();
                bannerView.currentItem = bannerView.currentItem % (bannerView.bannerImages.size() - 2) + 1;
                bannerView.bannerViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        bannerView.bannerViewPager.setCurrentItem(bannerView.currentItem, bannerView.currentItem != 0);
                    }
                });
            }
        }
    }

    private static class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        private final WeakReference<BannerView> mWeakReference;
        private int oldPosition = 0;

        private MyPageChangeListener(BannerView bannerView) {
            this.mWeakReference = new WeakReference<>(bannerView);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mWeakReference.get() != null && mWeakReference.get().bannerData.size() > 1) {
                BannerView bannerView = mWeakReference.get();
                if (position < 1) {
                    position = bannerView.bannerData.size();
                    bannerView.bannerViewPager.setCurrentItem(position, false);
                } else if (position > bannerView.bannerData.size()) {
                    bannerView.bannerViewPager.setCurrentItem(1, false);
                    position = 1;
                }
                bannerView.currentItem = position - 1 + bannerView.bannerData.size();
                bannerView.dots.get(oldPosition).setImageDrawable(bannerView.createPoint(bannerView.unselectedItemColor));
                oldPosition = (bannerView.currentItem) % bannerView.bannerData.size();
                bannerView.bannerTitle.setText(bannerView.bannerData.get(oldPosition).getDesc());
                bannerView.dots.get(oldPosition).setImageDrawable(bannerView.createPoint(bannerView.selectItemColor));
            }
        }
    }

    private static class MyAdapter extends PagerAdapter {
        private final WeakReference<BannerView> mWeakReference;

        public MyAdapter(BannerView bannerView) {
            mWeakReference = new WeakReference<>(bannerView);
        }

        @Override
        public int getCount() {
            if (mWeakReference.get() != null) {
                return mWeakReference.get().bannerImages.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final BannerView bannerView = mWeakReference.get();
            ImageView iv = bannerView.bannerImages.get(position);
            container.addView(iv);
            final int pos = position - 1 + bannerView.bannerData.size();
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (bannerView.onClickListener != null) {
                        bannerView.onClickListener.onClick(v, bannerView.bannerData.get(pos % bannerView.bannerData.size()));
                    }
                }
            });
            return iv;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            if (mWeakReference.get() != null) {
                mWeakReference.get().bannerImages.get(arg1).setOnClickListener(null);
                ((ViewPager) arg0).removeView(mWeakReference.get().bannerImages.get(arg1));
            }
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
