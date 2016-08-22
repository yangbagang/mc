package com.ybg.rp.vm.thread;

import android.content.Context;
import android.os.SystemClock;

import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.serial.PushOpenTrackNoUtils;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by yangbagang on 16/8/22.
 */
public class OrderCheckThread extends Thread {

    private Context mActivity;

    private OrderInfo orderInfo;

    private String url = AppConstant.HOST + "orderInfo/queryOrderIsPay";

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
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("orderSn", orderInfo.getOrderNo());
        while (!interrupted()) {
            try {
                LogUtil.i("-----" + orderInfo.getOrderNo());
                String result = x.http().postSync(params, String.class);
                LogUtil.i("请求返回数据: " + result);
                isOpenTrack = xApplication.getIsOpenTrack(orderInfo.getOrderNo());
                JSONObject json = new JSONObject(result);
                String success = json.getString("success");
                String msg = json.getString("msg");
                if ("true".equals(success)) {
                    boolean isPay = json.getBoolean("isPay");
                    String deliveryStatus = json.getString("deliveryStatus");
                    String payWay = json.getString("payWay");
                    if (isPay && null != isOpenTrack && "0".equals(deliveryStatus)) {

                        xApplication.isOpenTrackRemove(orderInfo.getOrderNo());
                        //打开柜门 - 支付成功-本地没出货-服务器未发货
                        LogUtil.e("---线上支付----出货------");
                        orderInfo.setPayStatus(isPay);
                        PushOpenTrackNoUtils.shipmentLine(mActivity, orderInfo);
                        this.interrupt();
                    } else if (!isPay && null != isOpenTrack) {
                        //没有支付-查询 && 没有出货 递归线上支付状态
                        SystemClock.sleep(VMConstant.PAY_INTERVAL);
                    } else {
                        this.interrupt();
                        LogUtil.i("--------关闭递归-------");
                    }
                }
                findCount = 0;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                // 请求失败
                findCount++;
                if (null != isOpenTrack && findCount < VMConstant.HTTP_ERROR_REQUEST_COUNT) {
                    //没有支付-查询 && 没有出货 递归线上支付状态
                } else if (null == isOpenTrack) {
                    LogUtil.i("已经出货-停止轮询- " + orderInfo.getOrderNo());
                    this.interrupt();
                } else {
                    this.interrupt();
                }
            }

            SystemClock.sleep(VMConstant.PAY_INTERVAL);
        }
    }

}
