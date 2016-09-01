package com.ybg.rp.vmbase.utils;

/**
 * Created by yangbagang on 16/8/18.
 */
public class VMConstant {

    public static final String PREFERENCE_FILE_NAME = "ybg_vm_preference";

    public static final String ZF_WX = "2";//微信

    public static final String ZF_ZFB = "1";//支付宝

    /** 打开柜门*/
    public static final String TYPE_OPEN = "1";
    /** 出货*/
    public static final String TYPE_SHIP = "2";

    /** 出货 大屏幕使用*/
    public static final String TYPE_SHIP_YF = "10002";
    /** 推送登录*/
    public static final String TYPE_SIGN_IN = "10003";
    /** 扫描成功*/
    public static final String TYPE_SCAN_SUCCESS = "10004";

    /** 网路请求-返回JSON 关键字*/
    public static final String TRUE = "true";
    public static final String SUCCESS = "success";
    public static final String ERROR = "msg";

    /** 数据库名称*/
    public static final String DB_NAME = "YBG_MC_DB";
    /** 数据库初始版本*/
    public static final int DB_VERSION = 10;

    /**
     * 数据上传不成功，请求次数
     */
    public static final int HTTP_ERROR_REQUEST_COUNT = 5;
    /**
     * 数据上传间隔
     */
    public static final long UO_DATA_INTERVAL = 1000;

    /**
     * 机器打开，循环间隔
     */
    public static final long CYCLE_INTERVAL = 1000;

    /**
     * 缓存文件名
     */
    public static final String CACHE_FILENAME = "CACHE_VM";

    /**
     * 支付查询请求间隔时间 1秒
     */
    public static final long PAY_INTERVAL = 1000;


    public static final String BASE_PATH = SDUtil.getSDCardPath() + "/ybg_vm";
    public static final String PATH = BASE_PATH + "/lastestApk/";   //新版apk保存路劲
    public static final String LOG4J_PATH = BASE_PATH + "/log/info/";

}
