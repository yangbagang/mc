package com.ybg.rp.vm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.igexin.sdk.PushConsts;
import com.ybg.rp.vm.activity.login.LoginActivity;
import com.ybg.rp.vm.activity.login.ScanSuccessActivity;
import com.ybg.rp.vm.activity.setting.ManageActivity;
import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.serial.PushOpenTrackNoUtils;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.bean.VMOperator;
import com.ybg.rp.vmbase.utils.GsonUtil;
import com.ybg.rp.vmbase.utils.LogUtil;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * 个推返回数据
 */
public class PushNoticeReceiver extends BroadcastReceiver {

    private Logger log = Logger.getLogger(PushNoticeReceiver.class);

    /** 打开柜门*/
    public static final String TYPE_OPEN = "1";
    /** 出货*/
    public static final String TYPE_SHIP = "2";

    /** 出货 21寸屏幕使用*/
    public static final String TYPE_SHIP_NEW = "10002";
    /** 推送登录*/
    public static final String TYPE_SIGN_IN = "10003";
    /** 扫描成功*/
    public static final String TYPE_SCAN_SUCCESS = "10004";

    @Override
    public void onReceive(final Context context, Intent intent) {
        final XApplication xApplication = (XApplication) context.getApplicationContext();
        Bundle bundle = intent.getExtras();
        try {
            switch (bundle.getInt(PushConsts.CMD_ACTION)) {
                case PushConsts.GET_MSG_DATA:
                    /**获取透传数据*/
                    // String appid = bundle.getString("appid");
                    byte[] payload = bundle.getByteArray("payload");
                    //String taskid = bundle.getString("taskid");
                    //String messageid = bundle.getString("messageid");
                    if (payload != null) {
                        String data = new String(payload);
                        LogUtil.i("[--Receive:" + data);
                        JSONObject json = new JSONObject(data);
                        String type = json.getString("type");
                        switch (type) {
                            case TYPE_OPEN:
                                /**打开柜门*/
                                final String trackNos = json.getString("data");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LogUtil.i("----" + trackNos);
                                        /**打开柜门*/
                                        try {
                                            PushOpenTrackNoUtils.operTrackNo(context, trackNos);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        VMDBManager.getInstance().saveLog(xApplication
                                                .getOperator(), "推送-开柜门:" + trackNos);
                                    }
                                }).start();
                                break;
                            case TYPE_SHIP_NEW:
                                /** 出货 21寸屏幕使用*/
                                final OrderInfo orderInfo = GsonUtil.createGson().fromJson(data, OrderInfo.class);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 数据不为NULL，说明柜门未打开
                                        String isOpenTrack = xApplication.getIsOpenTrack(orderInfo
                                                .getOrderNo());
                                        if (null != isOpenTrack && isOpenTrack.equals(orderInfo.getOrderNo())) {
                                            // 柜门打开后，进行数据初始化
                                            xApplication.isOpenTrackRemove(orderInfo.getOrderNo());
                                            LogUtil.e("----------线上支付--------PUSH");
                                            PushOpenTrackNoUtils.shipmentLine(context, orderInfo);
                                        } else {
                                            log.info("------柜门已打开/没有改订单数据---  " + orderInfo.getOrderNo());
                                        }
                                    }
                                }).start();
                                break;
                            case TYPE_SIGN_IN:
                                /** 推送登录*/
                                VMOperator operatorIn = GsonUtil.createGson().fromJson(data,
                                        VMOperator.class);
                                VMOperator yfOper = xApplication.getOperator();
                                if (operatorIn.getOperatorId().longValue() != yfOper.getOperatorId().longValue
                                        ()) {
                                    log.info("登录用户不一致 -推送的用户：" + operatorIn.toString() + "----当前：" + yfOper.toString());
                                    Toast.makeText(context, "请在'" + yfOper.getOperatorName() +
                                            "'的用户点击登录", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                VMDBManager.getInstance().saveLog(operatorIn, "登录");

                                Intent optIntent = new Intent();
                                optIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                optIntent.setClass(context, ManageActivity.class);
                                context.startActivity(optIntent);
                                /** 关闭二维码扫描页面*/
                                if (null != LoginActivity.loginActivity && !LoginActivity.loginActivity.isFinishing()) {
                                    LoginActivity.loginActivity.finish();
                                }
                                if (null != ScanSuccessActivity.scanSuccessActivity && !ScanSuccessActivity.scanSuccessActivity.isFinishing()) {
                                    ScanSuccessActivity.scanSuccessActivity.finish();
                                }
                                break;
                            case TYPE_SCAN_SUCCESS:
                                VMOperator operator = GsonUtil.createGson().fromJson(data,
                                        VMOperator.class);
                                xApplication.setOperator(operator);//保存到application
                                /** 获得推送 进入扫描成功页面*/
                                Intent scanIntent = new Intent();
                                scanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                scanIntent.setClass(context, ScanSuccessActivity.class);
                                context.startActivity(scanIntent);
                                break;
                        }
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
