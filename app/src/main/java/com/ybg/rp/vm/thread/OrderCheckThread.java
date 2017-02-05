package com.ybg.rp.vm.thread;

import android.content.Context;
import android.os.SystemClock;

import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.serial.PushOpenTrackNoUtils;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by yangbagang on 16/8/22.
 */
public class OrderCheckThread extends Thread {

    private static Logger logger = Logger.getLogger(OrderCheckThread.class);

    private Context mActivity;

    private OrderInfo orderInfo;

    /**
     * 主动请求-计数
     */
    private int findCount = 0;

    public OrderCheckThread(Context mActivity, OrderInfo orderInfo) {
        super();
        this.mActivity = mActivity;
        this.orderInfo = orderInfo;
        this.setName("线上支付LinePay线程");

    }

    @Override
    public void run() {
        super.run();
        LogUtil.i("---" + orderInfo.toString());
        //是否打开柜门
        XApplication xApplication = (XApplication) mActivity.getApplicationContext();
        String isOpenTrack = xApplication.getIsOpenTrack(orderInfo.getOrderNo());
        String url = AppConstant.HOST + "orderInfo/queryOrderIsPay";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("orderSn", orderInfo.getOrderNo());
        while (!interrupted()) {
            try {
                LogUtil.i("-----" + orderInfo.getOrderNo());
                logger.info("开始查询订单："+orderInfo.getOrderNo()+" 是否己经支付。");
                String result = x.http().postSync(params, String.class);
                LogUtil.i("请求返回数据: " + result);
                logger.info("请求返回数据: " + result);
                isOpenTrack = xApplication.getIsOpenTrack(orderInfo.getOrderNo());
                JSONObject json = new JSONObject(result);
                String success = json.getString("success");
                String msg = json.getString("msg");
                if ("true".equals(success)) {
                    boolean isPay = json.getBoolean("isPay");
                    String deliveryStatus = json.getString("deliveryStatus");
                    //String payWay = json.getString("payWay");
                    if (isPay && null != isOpenTrack && "0".equals(deliveryStatus)) {

                        xApplication.isOpenTrackRemove(orderInfo.getOrderNo());
                        //打开柜门 - 支付成功-本地没出货-服务器未发货
                        LogUtil.e("---线上支付----出货------");
                        logger.info("订单："+orderInfo.getOrderNo()+" 己经支付，开始出货。");
                        orderInfo.setPayStatus(isPay);
                        PushOpenTrackNoUtils.shipmentLine(mActivity, orderInfo);
                        this.interrupt();
                    } else if (!isPay && null != isOpenTrack) {
                        //没有支付-查询 && 没有出货 递归线上支付状态
                        logger.info("订单："+orderInfo.getOrderNo()+" 未支付，1秒后继续查询。");
                        SystemClock.sleep(VMConstant.PAY_INTERVAL);
                    } else {
                        logger.info("订单："+orderInfo.getOrderNo()+" 己经出货。");
                        this.interrupt();
                        LogUtil.i("--------关闭递归-------");
                    }
                } else {
                    logger.info("订单："+orderInfo.getOrderNo()+" 查询结果 " + msg);
                }
                findCount = 0;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                logger.error("未出货-第"+findCount+"次轮询出错了，订单号：- " + orderInfo.getOrderNo(), throwable);
                // 请求失败
                findCount++;
                //2017.1.7 出错重试最大次数由5次改为20次。
                //2017.2.5 出错重试最大次数由20次改为30次。
                if (null != isOpenTrack && findCount < 30) {
                    //没有支付-查询 && 没有出货 递归线上支付状态
                    logger.info("未出货-第"+findCount+"次轮询- " + orderInfo.getOrderNo());
                    LogUtil.i("未出货-第"+findCount+"次轮询- " + orderInfo.getOrderNo());
                } else if (null == isOpenTrack) {
                    logger.info("已经出货-停止轮询- " + orderInfo.getOrderNo());
                    LogUtil.i("已经出货-停止轮询- " + orderInfo.getOrderNo());
                    this.interrupt();
                } else {
                    logger.info("未出货-最后一次查询失败 " + orderInfo.getOrderNo());
                    this.interrupt();
                }
            }

            SystemClock.sleep(VMConstant.PAY_INTERVAL);
        }
    }

}
