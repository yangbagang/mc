package com.ybg.rp.vm.utils;

import android.content.Context;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * Created by yangbagang on 16/8/25.
 */
public class ImageUtils {

    private static Context mContext;
    private static ImageUtils imageUtils;
    private static RequestManager manager;

    public static synchronized ImageUtils getInstance(Context mContext) {
        if (imageUtils == null || null == mContext) {
            synchronized (ImageUtils.class) {
                imageUtils = new ImageUtils();
                getInstanceRequest(mContext);
            }
        }
        return imageUtils;
    }

    /**
     * 图片加载器
     *
     * @param mContext 上下文
     * @return
     */
    public static synchronized RequestManager getInstanceRequest(Context mContext) {
        if (manager == null || null == ImageUtils.mContext) {
            synchronized (ImageUtils.class) {
                manager = Glide.with(mContext);
                ImageUtils.mContext = mContext;
            }
        }
        return manager;
    }

    /**
     * 图片加载
     *
     * @param url             图片地址
     * @param defaultResource 默认加载图
     * @return
     */
    public DrawableRequestBuilder getImage(String url, int defaultResource) {
        return manager.load(url).crossFade()
                .placeholder(defaultResource)
                .fallback(defaultResource)
                .error(defaultResource);
    }

}
