package com.ybg.rp.vm.utils;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.igexin.sdk.PushManager;
import com.ybg.rp.vm.bean.ErrorTrackNo;
import com.ybg.rp.vm.bean.ErrorTranData;
import com.ybg.rp.vm.bean.TransactionData;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vmbase.bean.VMOperator;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.DateUtil;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/24.
 */
public class VMRequest {

    private Context mContext;
    private Logger log = Logger.getLogger(VMRequest.class);
    private static VMRequest commVMResquest;
    private VMPreferences preferences = VMPreferences.getInstance();

    public static VMRequest getInstance(Context mContext) {
        if (null == commVMResquest) {
            commVMResquest = new VMRequest(mContext);
        }
        return commVMResquest;
    }

    private VMRequest(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 上传交易结果
     *
     * @param data       数据( 类型 0:取消，1卡交易，2线上交易)
     * @param errorGoods 错误的轨道信息
     */
    public void startSendSale(final TransactionData data, final ArrayList<ErrorTranData>
            errorGoods, final int cancelType) {
        LogUtil.i("[--启动-上传交易结果 (上传数据到VEM) 一直--]");
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**把交易记录保存到文件 0 取消 1 card 2 online*/
                LogUtil.i("[-save:" + data.toString() + "-]");
                //TODO --保存消费记录
                VMDBManager.getInstance().saveOrUpdate(data);
                /**上传*/
                sendSettleData(data, errorGoods, false, cancelType);
            }
        }).start();
    }

    /**
     * 上传交易数据到-售卖机后台
     * 同步请求到服务器
     *
     * @param data       交易记录 支付方式：0：离线，1：支付宝，2：微信支付
     * @param errorGoods 错误轨道信息
     * @param isDataUp   是否进行库存扣减？true 不扣库存，false 扣取库存
     */
    public void sendSettleData(final TransactionData data, final ArrayList<ErrorTranData> errorGoods,
                               boolean isDataUp, int cancelType) {
        log.info("保存与上传交易结果--   VEM  --- TranOnlineData=" + data.toString());
        String url = AppConstant.HOST + "orderInfo/updateOrderStatus";
        RequestParams params = new RequestParams(url);
        // 添加请求参数
        params.addBodyParameter("orderNo", data.getOrderNo());
        params.addBodyParameter("payType", data.getPayType());
        params.addBodyParameter("result", data.getSaleResult());
        params.addBodyParameter("transDate", DateUtil.getStringByFormat(data.getTransactionDate(), DateUtil
                .dateFormatYMDHMS));
        params.addBodyParameter("isDataUp", String.valueOf(isDataUp));
        params.addBodyParameter("cancelType", String.valueOf(cancelType));

        for (int i = 1; i <= VMConstant.HTTP_ERROR_REQUEST_COUNT; i++) {
            //同步请求
            try {
                String result = x.http().postSync(params, String.class);
                JSONObject json = new JSONObject(result);
                String success = json.getString("success");
                //String msg = json.getString("msg");
                if ("success".equals(success)) {
                    sendErrorData(errorGoods);
                    VMDBManager.getInstance().saveForTranUpdate(data.getOrderNo(), true);
                    break;
                }
                if (i == VMConstant.HTTP_ERROR_REQUEST_COUNT) {
                    VMDBManager.getInstance().saveForTranUpdate(data.getOrderNo(), false);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            SystemClock.sleep(VMConstant.UO_DATA_INTERVAL);
        }
    }

    /**
     * 订单-部分轨道错误后，进行数据上传
     * 同步请求到服务器
     *
     * @param goodsInfos 商品数据/轨道编号
     */
    public void sendErrorData(final ArrayList<ErrorTranData> goodsInfos) {
        if (null == goodsInfos || goodsInfos.size() <= 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConstant.HOST + "orderInfo/deliveryGoodsFail";
                for (int i = 1; i < goodsInfos.size(); i++) {
                    for (int count = 0; count <= VMConstant.HTTP_ERROR_REQUEST_COUNT; count++) {
                        ErrorTranData tranData = goodsInfos.get(i);
                        // 添加请求参数
                        RequestParams params = new RequestParams(url);
                        params.addBodyParameter("orderSn", tranData.getOrderNo());
                        params.addBodyParameter("trackNo", tranData.getTrackNo());
                        params.addBodyParameter("gid", tranData.getGid());
                        //同步请求
                        try {
                            String result = x.http().postSync(params, String.class);
                            JSONObject json = new JSONObject(result);
                            String success = json.getString("success");
                            if ("success".equals(success)) {
                                tranData.setIsUpd(1);
                                VMDBManager.getInstance().saveOrUpdate(tranData);
                                break;
                            }
                            if (count == VMConstant.HTTP_ERROR_REQUEST_COUNT) {
                                //上传失败-保存到本地
                                VMDBManager.getInstance().saveOrUpdate(tranData);
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        SystemClock.sleep(VMConstant.UO_DATA_INTERVAL);
                    }
                }
            }
        }).start();
    }

    /**
     * 售卖机错误信息上送
     * 以及把数据保存到本地数据库
     *
     * @param errorTrackNo    轨道错误信息
     */
    private int addFaultCount = 0;

    public void addFaultInfo(final VMOperator operator, final ErrorTrackNo errorTrackNo) {
        /**  轨道错误信息 记录错误信息到数据库 */
        VMDBManager.getInstance().saveFaultTrackNo(errorTrackNo.getTrackNo());
        VMDBManager.getInstance().saveLog(operator, "轨道：" + errorTrackNo.getTrackNo() + "-" +
                errorTrackNo.getErrMsg());
        String url = AppConstant.HOST + "vendMachineInfo/addErrorInfo";
        RequestParams params = new RequestParams(url);
        // 添加请求参数
        params.addBodyParameter("machineId", VMPreferences.getInstance().getVMId());//机器ID
        params.addBodyParameter("orbitalNo", errorTrackNo.getTrackNo());//轨道编号
        params.addBodyParameter("errorMsg", errorTrackNo.getErrMsg());//错误信息
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String success = json.getString("success");
                    if ("success".equals(success)) {
                        addFaultCount = 0;
                        LogUtil.i("--售卖机错误信息上送完成--");
                    } else {
                        addFaultCount++;
                        if (addFaultCount <= VMConstant.HTTP_ERROR_REQUEST_COUNT) {
                            addFaultInfo(operator, errorTrackNo);
                        } else {
                            // 添加-轨道的错误数据 - 记录未上传数据
                            VMDBManager.getInstance().saveOrUpdate(errorTrackNo);

                            addFaultCount = 0;
                            LogUtil.i("保存未上传的轨道错误数据");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(ex.getLocalizedMessage());
                addFaultCount++;
                if (addFaultCount <= VMConstant.HTTP_ERROR_REQUEST_COUNT) {
                    addFaultInfo(operator, errorTrackNo);
                } else {
                    // 添加-轨道的错误数据 - 记录未上传数据
                    VMDBManager.getInstance().saveOrUpdate(errorTrackNo);

                    addFaultCount = 0;
                    LogUtil.i("保存未上传的轨道错误数据");
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
