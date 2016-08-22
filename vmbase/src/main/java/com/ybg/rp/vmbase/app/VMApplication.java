package com.ybg.rp.vmbase.app;

import android.app.Activity;
import android.app.Application;

import com.ybg.rp.vmbase.bean.VMOperator;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.VMCache;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.apache.log4j.Level;

import java.io.File;
import java.io.IOException;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by yangbagang on 16/8/18.
 */
public class VMApplication extends Application {

    private VMPreferences preference = VMPreferences.getInstance();

    /*缓存二维码*/
    private static VMCache cache;

    private VMOperator operator;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!preference.hasInit()) {
            preference.init(getSharedPreferences(
                    VMConstant.PREFERENCE_FILE_NAME, Activity.MODE_PRIVATE));
        }

        cache = VMCache.get(this, VMConstant.CACHE_FILENAME);

        setLog4j();
    }

    public VMOperator getOperator() {
        return operator;
    }

    public void setOperator(VMOperator operator) {
        this.operator = operator;
    }

    /**
     * 初始化LOG日志保存
     */
    private void setLog4j() {
        try {
            File destDir = new File(VMConstant.LOG4J_PATH);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File file = new File(VMConstant.LOG4J_PATH + "vm_now_log.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(VMConstant.LOG4J_PATH + "vm_now_log.txt");
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5); //1MB一个文件
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
    }


    /**
     * 是否打开柜门
     * 默认 没开门-否
     * default: NULL
     *
     * @return 订单号
     */
    public synchronized String getIsOpenTrack(String isOpenTrack) {
        if (null == cache)
            cache = VMCache.get(this, VMConstant.CACHE_FILENAME);
        return cache.getAsString(isOpenTrack);
        //        return PreferencesUtil.getString(Config.FILE_NAME, mContext, Config.FILE_NAME_ORDER, null);
    }

    /**
     * 设置是否开门
     *
     * @param isOpenTrack 订单号
     */
    public synchronized void setIsOpenTrack(String isOpenTrack) {
        if (null == cache)
            cache = VMCache.get(this, VMConstant.CACHE_FILENAME);
        cache.put(isOpenTrack, isOpenTrack);
        //        PreferencesUtil.putString(Config.FILE_NAME, mContext, Config.FILE_NAME_ORDER, isOpenTrack);
    }

    /**
     * 清空
     *
     * @param isOpenTrack 订单号
     */
    public synchronized void isOpenTrackRemove(String isOpenTrack) {
        if (null == cache)
            cache = VMCache.get(this, VMConstant.CACHE_FILENAME);
        cache.remove(isOpenTrack);
    }

    public VMPreferences getPreference() {
        return preference;
    }

}
