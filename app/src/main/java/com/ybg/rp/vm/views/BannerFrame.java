package com.ybg.rp.vm.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ybg.rp.vm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangbagang on 16/8/18.
 */
public class BannerFrame extends FrameLayout {

    private final static boolean isAutoPlay = true;
    private List<ImageView> imageViewsList;
    private List<ImageView> dotViewsList;
    private LinearLayout mLinearLayout;
    private ViewPager mViewPager;
    private int currentItem = 0;
    private ScheduledExecutorService scheduledExecutorService;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.setCurrentItem(currentItem);
        }
    };

    public BannerFrame(Context context) {
        this(context, null);
    }

    public BannerFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initUI(context);
    }

    private void initUI(Context context) {
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<ImageView>();
        LayoutInflater.from(context).inflate(R.layout.banner_layout, this, true);
        mLinearLayout = (LinearLayout) findViewById(R.id.banner_dots);
        mViewPager = (ViewPager) findViewById(R.id.banner_page);
    }

    public void setImageResorces(List<Integer> imageResorces) {
        for (Integer res : imageResorces) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);//铺满屏幕
            imageView.setImageResource(res);
            imageViewsList.add(imageView);
            ImageView viewDot = new ImageView(getContext());
            viewDot.setBackgroundResource(R.mipmap.page_indicator);
            dotViewsList.add(viewDot);
            mLinearLayout.addView(viewDot);
        }
        mViewPager.setFocusable(true);
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setOnPageChangeListener(new MyPageChangeListener());
    }


    public void startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new BannerTask(), 1, 4, TimeUnit.SECONDS);
    }

    public void stopPlay() {
        scheduledExecutorService.shutdown();
    }

    /**
     * 设置选中的圆点的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < dotViewsList.size(); i++) {
            if (i == selectItems) {
                dotViewsList.get(i).setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                dotViewsList.get(i).setBackgroundResource(R.mipmap.page_indicator);
            }
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }


    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        mViewPager.setCurrentItem(0);
                    }
                    //如果滑到头就从尾开始
                    else if (mViewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int pos) {
            setImageBackground(pos);
        }
    }


    private class BannerTask implements Runnable {
        @Override
        public void run() {
            synchronized (mViewPager) {
                currentItem = (currentItem + 1) % imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }
}
