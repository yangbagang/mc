package com.ybg.rp.vm.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;
import com.ybg.rp.vm.R;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.SDUtil;

/**
 */
public class CustomCachingGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        ViewTarget.setTagId(R.id.glide_tag_id); // 设置别的get/set tag id，以免占用View默认的
        // set size & external vs. internal
        int cacheSize100MegaBytes = 104857600;

        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

        //设置内存缓存
        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));

        //设置位图池
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565); // 默认，比较8888 少耗一半内存
        // 设置SD卡缓存目录
        String path = SDUtil.getSDCardPath() + "/imageVmCache";
        LogUtil.e("----" + path);
        builder.setDiskCache(new DiskLruCacheFactory(path, cacheSize100MegaBytes));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // nothing to do here

    }
}
