package com.ybg.rp.vm.serial;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.ArrayMap;

import com.ybg.rp.vm.activity.shopping.PayFailedActivity;
import com.ybg.rp.vm.activity.shopping.PaySuccessActivity;
import com.ybg.rp.vm.activity.shopping.ShoppingWithCartActivity;
import com.ybg.rp.vm.activity.shopping.ShoppingWithOneGoodsActivity;
import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.bean.ErrorTrackNo;
import com.ybg.rp.vm.bean.ErrorTranData;
import com.ybg.rp.vm.bean.TransactionData;
import com.ybg.rp.vm.utils.VMRequest;
import com.ybg.rp.vmbase.bean.GoodsInfo;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.utils.CharacterUtil;
import com.ybg.rp.vmbase.utils.DateUtil;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class PushOpenTrackNoUtils {

    private static Logger log = Logger.getLogger(PushOpenTrackNoUtils.class);

    /**
     * 打开柜门
     *
     * @param mContext 上下文
     * @param trackNos 柜门数据
     */
    public synchronized static void operTrackNo(final Context mContext, final String trackNos) throws Exception {
        log.info("开柜门方法开始");
        log.info("接收到的轨道编号 :" + trackNos);
        List<String> tracks = CharacterUtil.getDataList(trackNos);
        if (null != tracks && tracks.size() > 0) {
            LogUtil.i("切割出来的轨道集合 :" + tracks.toString());
            log.info("切割出来的轨道集合 :" + tracks.toString());
            SerialManager manager = SerialManager.getInstance(mContext);
            for (int i = 0; i < tracks.size(); i++) {
                String trackNo = tracks.get(i);
                log.info("打开第个"+i+"柜门：" + trackNo);
                if (trackNo == null || trackNo.length() != 3) {
                    LogUtil.i("打开柜门有问题--柜门：" + trackNo + " -- 错误柜门数据");
                    log.info("打开柜门有问题--柜门：" + trackNo + " -- 错误柜门数据");
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
                    log.info("弹簧柜--- " + trackNo);
                } else if ("A".equals(ss) || "B".equals(ss) || "C".equals(ss)){
                    /** 格子柜*/
                    LogUtil.i("副柜--- " + trackNo);
                    manager.createSerial(3);
                    beanTrackSet = manager.openMachineTrack(trackNo);
                    log.info("格子柜--- " + trackNo);
                } else {
                    /** 格子柜*/
                    LogUtil.i("格子柜--- " + trackNo);
                    manager.createSerial(2);
                    beanTrackSet = manager.openMachineTrack(trackNo);
                    log.info("格子柜--- " + trackNo);
                }
                LogUtil.i("---" + beanTrackSet.toString());
                SystemClock.sleep(VMConstant.CYCLE_INTERVAL);
            }
            manager.closeSerial();
        } else {
            LogUtil.i("--推送打开机器柜门---空数据返回，不执行打开--");
            log.info("--推送打开机器柜门---空数据返回，不执行打开--");
        }
        log.info("开柜门方法结束");
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
                    if (beanTrackSet.trackStatus == 1) {
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
                        tranData.setGid(goodsInfo.getGid());
                        tranData.setGoodsName(goodsInfo.getGoodsName());
                        tranData.setIsUpd(0);
                        tranData.setPrice(goodsInfo.getPrice());
                        tranData.setOrderNo(orderInfo.getOrderNo());
                        errorGoods.add(tranData);

                        /** 提示错误信息*/
                        ErrorTrackNo errorTrackNo = new ErrorTrackNo();
                        errorTrackNo.setOrderNo(orderInfo.getOrderNo());
                        errorTrackNo.setErrMsg(beanTrackSet.errorInfo);
                        errorTrackNo.setTrackNo(trackNo);
                        errorTrackNo.setType(1);
                        LogUtil.e("--订单:" + orderInfo.getOrderNo() + " 出货失败--" + beanTrackSet
                                .errorInfo);
                        if (handler != null) {
                            handler.sendEmptyMessage(1005);
                        }
                        /** 上传轨道错误信息*/
                        LogUtil.i("记录轨道错误信息 trackNo=" + trackNo + " ,msg=" + beanTrackSet.errorInfo);
                        XApplication xApplication = (XApplication) mActivity
                                .getApplicationContext();
                        VMRequest.getInstance(mActivity).addFaultInfo(xApplication.getOperator(),
                                errorTrackNo);
                    }
                }
                SystemClock.sleep(VMConstant.CYCLE_INTERVAL);
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
        /** 关闭二维码扫描页面*/
        if (null != ShoppingWithCartActivity.activity && !ShoppingWithCartActivity.activity.isFinishing()) {
            ShoppingWithCartActivity.activity.finish();
        }
        if (null != ShoppingWithOneGoodsActivity.activity && !ShoppingWithOneGoodsActivity.activity.isFinishing()) {
            ShoppingWithOneGoodsActivity.activity.finish();
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


        LogUtil.i("线上支付上传 trackNos=" + trackNos + ", order=" + orderInfo.toString());
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
        TransactionData data = new TransactionData();
        data.setTransactionDate(DateUtil.getStringByFormat(new Date(), DateUtil.dateFormatYMDHMS));
        data.setOrderPrice(orderInfo.getOrderMoney());
        data.setOrderNo(orderInfo.getOrderNo());
        data.setTrackNo(trackNos);//轨道数据

        /** 交易结果 0 取消 1 成功 2 失败*/
        //        data.setSaleResult(order.isPayStatus() ? "1" : "2");
        data.setSaleResult("1"); // 默认成功
        /**付款方式0：com.ybg.rp.vm，1：支付宝，2：微信支付 3 pp钱包**/
        data.setPayType(String.valueOf(orderInfo.getPayWay()));
        //上传交易结果
        VMRequest.getInstance(context.getApplicationContext()).startSendSale(data, errorGoods, 0);
    }

}
