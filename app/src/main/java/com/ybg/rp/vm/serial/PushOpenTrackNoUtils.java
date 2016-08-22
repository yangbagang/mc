package com.ybg.rp.vm.serial;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.ArrayMap;

import com.ybg.rp.vmbase.bean.GoodsInfo;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.utils.CharacterUtil;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 线上交易-推送和本地查询使用打开柜门
 * 包            名:      com.cnpay.ppvending.receiver
 * 类            名:      PushOpenTrackNoUtils
 * 修 改 记 录:     // 修改历史记录，包括修改日期、修改者及修改内容
 * 版 权 所 有:     版权所有(C)2010-2015
 * 公             司:
 *
 * @author liyuanming
 * @version V1.0
 * @date 2016/3/7
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class PushOpenTrackNoUtils {

    /**
     * 打开柜门
     *
     * @param mContext 上下文
     * @param trackNos 柜门数据
     */
    public synchronized static void operTrackNo(final Context mContext, final String trackNos) throws Exception {
        List<String> tracks = CharacterUtil.getDataList(trackNos);
        if (null != tracks && tracks.size() > 0) {
            LogUtil.i("切割出来的轨道集合 :" + tracks.toString());
            SerialManager manager = SerialManager.getInstance(mContext);
            for (int i = 0; i < tracks.size(); i++) {
                String trackNo = tracks.get(i);
                if (trackNo != null || trackNo.length() != 3) {
                    LogUtil.i("打开柜门有问题--柜门：" + trackNo + " -- 错误柜门数据");
                    return;
                }
                // 打开柜门
                BeanTrackSet beanTrackSet;
                String ss = trackNo.substring(0, 1);
                if (ss.equals("0")) {
                    LogUtil.i("弹簧柜--- " + trackNo);
                    /** 弹簧柜*/
                    manager.createSerial(1);//1:主机 2: 格子机
                    beanTrackSet = manager.openMachineTrack(trackNo);

                } else {
                    /** 格子柜*/
                    LogUtil.i("格子柜--- " + trackNo);
                    manager.createSerial(2);
                    beanTrackSet = manager.openMachineTrack(trackNo);
                }
                LogUtil.i("---" + beanTrackSet.toString());
                SystemClock.sleep(VMConstant.CYCLE_INTERVAL);
            }
            manager.closeSerial();
        } else {
            LogUtil.i("--推送打开机器柜门---空数据返回，不执行打开--");
        }

    }

    /**
     * 公共-购买打开门
     *
     * @param mActivity 上下文
     * @param orderInfo 订单
     * @return 返回的轨道信息和错误轨道信息
     */
    public synchronized static ArrayMap<String, Object> commOpen(Handler handler, Context mActivity, OrderInfo orderInfo) {
        ArrayMap<String, Object> arrayMap = new ArrayMap<String, Object>();
        if (orderInfo.getGoodsInfo() == null) {
            LogUtil.i("没有商品详情信息");
            return null;
        }
        ArrayList<ErrorTranData> errorGoods = new ArrayList<>();
        String trackNos = "";
        SerialManager manager = SerialManager.getInstance(mActivity);
        boolean isCabinet = false;
        boolean isVending = false;
        for (int i = 0; i < orderInfo.getGoodsInfo().size(); i++) {
            GoodsInfo goodsInfo = orderInfo.getGoodsInfo().get(i);
            for (int k = 0; k < goodsInfo.getNum(); k++) {
                String trackNo = goodsInfo.getTrackNo();
                trackNos += trackNo + ",";
                /** 出货中*/
                if (trackNo == null || trackNo.length() != 3) {
                    LogUtil.i("错误的轨道--线上交易出货--- " + trackNo);
                    return null;
                }
                LogUtil.i("-线上-打开柜门--" + trackNo + "-订单号为：" + orderInfo.getOrderNo());
                BeanTrackSet beanTrackSet;
                String ss = trackNo.substring(0, 1);
                if (ss.equals("0")) {
                    isVending = true;
                    LogUtil.i("弹簧柜--- " + trackNo);
                    /** 弹簧柜*/
                    manager.createSerial(1);//1:主机 2: 格子机
                    beanTrackSet = manager.openMachineTrack(trackNo);
                } else {
                    isCabinet = true;
                    /** 格子柜*/
                    LogUtil.i("格子柜--- " + trackNo);
                    manager.createSerial(2);
                    beanTrackSet = manager.openMachineTrack(trackNo);
                }
                LogUtil.i("- 线上交易出货-=" + beanTrackSet);
                if (null != beanTrackSet) {
                    LogUtil.i("- 线上交易出货-=" + beanTrackSet.toString());
                    if (beanTrackSet.trackstatus == 1) {
                        /** 提示出货成功*/
                        if (handler != null) {
                            //线上交易不传handler
                            handler.sendEmptyMessage(1003);
                        }
                        /**需要查询货架状态-成功出货后，进行上传数据*/
                        LogUtil.i("--轨道-:" + trackNo + " 出货成功--");
                    } else {
                        ErrorTranData tranData = new ErrorTranData();
                        tranData.setTrackNo(goodsInfo.getTrackNo());
                        tranData.setGid(goodsInfo.getgId());
                        tranData.setGoodsName(goodsInfo.getGoodsName());
                        tranData.setIsUpd(0);
                        tranData.setPrice(goodsInfo.getPrice());
                        tranData.setOrderNo(orderInfo.getOrderNo());
                        errorGoods.add(tranData);

                        /** 提示错误信息*/
                        ErrorTrackNo errorTrackNo = new ErrorTrackNo();
                        errorTrackNo.setOrderNo(orderInfo.getOrderNo());
                        errorTrackNo.setErrMsg(beanTrackSet.errorinfo);
                        errorTrackNo.setTrackNo(trackNo);
                        errorTrackNo.setType(1);
                        LogUtil.e("--订单:" + orderInfo.getOrderNo() + " 出货失败--" + beanTrackSet.errorinfo);
                        if (handler != null) {
                            handler.sendEmptyMessage(1005);
                        }
                        /** 上传轨道错误信息*/
                        log.info("记录轨道错误信息 trackNo=" + trackNo + " ,msg=" + beanTrackSet.errorinfo);
                        VMRequest.getInstance(mActivity).addFaultInfo(errorTrackNo);
                    }
                }
                SystemClock.sleep(Config.CYCLE_INTERVAL);
            }
        }
        manager.closeSerial();


        Intent intent = new Intent();

        /** 上传错误轨道数据*/
        if (errorGoods.size() > 0) {
            //跳转错误页面 - 部分轨道错误
            intent.setClass(mActivity, PayFailedActivity.class);
        } else {
            //跳转正确页面
            intent.setClass(mActivity, PaySuccessActivity.class);
        }
        String type = "3";
        //1：弹簧柜，2：格子柜，3：弹簧柜 + 格子柜
        if (isVending && isCabinet) {
            type = "3";
        } else if (isCabinet) {
            type = "2";
        } else if (isVending) {
            type = "1";
        }
        intent.putExtra("type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
        //进入支付成功或者失败页面,关闭Shopping 和支付的页面
        ExitShoppingActivity.getInstance().exit();

        arrayMap.put("trackNos", trackNos);
        arrayMap.put("errorGoods", errorGoods);
        return arrayMap;
    }


    /**
     * 线上交易出货-
     *
     * @param mActivity 上下文
     * @param orderInfo 订单
     */
    public static void shipmentLine(Context mActivity, OrderInfo orderInfo) {
        ArrayMap<String, Object> arrayMap = commOpen(null, mActivity, orderInfo);
        if (null == arrayMap) return;
        String trackNos = (String) arrayMap.get("trackNos");
        ArrayList<ErrorTranData> errorGoods = (ArrayList<ErrorTranData>) arrayMap.get("errorGoods");
        /*******************************************
         * 不管有没有打开柜门，都需要数据上传
         *******************************************/
        /*需要查询货架状态-，进行上传数据*/
        //线上支付上传


        LogUtil.i("[线上支付上传 trackNos=" + trackNos + ", order=" + orderInfo.toString());
        log.info("线上支付上传 trackNos=" + trackNos + ", order=" + orderInfo.toString());
        upLoadData(mActivity, orderInfo, trackNos, errorGoods);

        /**map置空释放内存*/
        arrayMap.clear();
    }

    /**
     * 上传交易数据 -支付宝/微信 交易系统
     *
     * @param context    上下文
     * @param orderInfo  订单
     * @param trackNos   轨道号
     * @param errorGoods 错误轨道
     */
    private static void upLoadData(Context context, OrderInfo orderInfo, String trackNos, ArrayList<ErrorTranData> errorGoods) {
        TranOnlineData data = new TranOnlineData();
        data.setTranDate(new Date());
        data.setOrderPrice(orderInfo.getOrderMoney());
        data.setOrderNo(orderInfo.getOrderNo());
        data.setTrackNos(trackNos);//轨道数据

        /** 交易结果 0 取消 1 成功 2 失败*/
        //        data.setSaleResult(order.isPayStatus() ? "1" : "2");
        data.setSaleResult("1"); // 默认成功
        /**付款方式0：com.ybg.rp.vm，1：支付宝，2：微信支付 3 pp钱包**/
        data.setPayType(String.valueOf(orderInfo.getPayWay()));
        //上传交易结果
        VMRequest.getInstance(context.getApplicationContext()).startSendSale(data, errorGoods, 0);
    }

}
