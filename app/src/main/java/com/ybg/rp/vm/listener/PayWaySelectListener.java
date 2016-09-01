package com.ybg.rp.vm.listener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.ybg.rp.vm.R;
import com.ybg.rp.vm.bean.Charge;
import com.ybg.rp.vm.bean.TransactionData;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.thread.OrderCheckThread;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vm.utils.DialogUtil;
import com.ybg.rp.vm.utils.QRUtil;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.DateUtil;
import com.ybg.rp.vmbase.utils.GsonUtil;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMCache;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by yangbagang on 16/8/22.
 */
public class PayWaySelectListener implements RadioGroup.OnCheckedChangeListener {

    /**
     * 支付方式：默认 0：，1：支付宝，2：微信支付
     */
    public static String payType;
    public static final String AL = "1";
    public static final String WX = "2";


    private Activity mActivity;

    private OrderInfo orderInfo;

    private RelativeLayout ll_code_bg;//二维码白色背景
    private ImageView iv_code;      //二维码

    private RadioGroup radioGroupPayAll;    //支付方式组
    private RadioButton rb_weixin;          //微信支付
    private RadioButton rb_zhifubao;        //支付宝支付
    private TextView tv_hint;   //交易提示

    private OrderCheckThread orderCheckThread;
    private Handler mHandler;


    /*缓存二维码*/
    private VMCache cache;

    /*二维码显示*/
    private Bitmap bitmap;

    public PayWaySelectListener(Activity mActivity, OrderInfo orderInfo, ImageView iv_code, RelativeLayout ll_bg,
                                  Handler handler, RadioButton rb_weixin, RadioButton rb_zhifubao, TextView tv_hint) {
        this.mActivity = mActivity;
        this.orderInfo = orderInfo;
        this.mHandler = handler;
        this.iv_code = iv_code;
        this.ll_code_bg = ll_bg;
        this.rb_weixin = rb_weixin;
        this.rb_zhifubao = rb_zhifubao;
        this.tv_hint = tv_hint;
        this.cache = VMCache.get(mActivity, VMConstant.CACHE_FILENAME);

        /** 添加销售数据 - 初始化*/
        TransactionData data = new TransactionData();
        data.setOrderNo(orderInfo.getOrderNo());
        VMDBManager.getInstance().addObject(data);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            /**微信*/
            case R.id.payWay_rb_weixin:
                payType = WX;
                tv_hint.setVisibility(View.INVISIBLE);
                closeLine();
                selectPayLine();
                break;
            /**支付宝*/
            case R.id.payWay_rb_zhifubao:
                payType = AL;
                tv_hint.setVisibility(View.INVISIBLE);
                closeLine();
                selectPayLine();
                break;
        }
    }

    /**
     * 选择支付方式
     */
    private void selectPayLine() {
        ll_code_bg.setBackgroundColor(mActivity.getResources().getColor(android.R.color.white));
        iv_code.setVisibility(View.VISIBLE);
        bitmap = cache.getAsBitmap(orderInfo.getOrderNo() + payType);
        if (bitmap != null) {
            ll_code_bg.setVisibility(View.VISIBLE);

            iv_code.setImageBitmap(bitmap);
            if (null == orderCheckThread) {
                orderCheckThread = new OrderCheckThread(mActivity, orderInfo);
                orderCheckThread.start();
            }
        } else {
            ll_code_bg.setVisibility(View.INVISIBLE);
            payAlAndWx(orderInfo, payType);
        }
    }

    /**
     * 支付宝  微信  支付
     *
     * @param good    选择的商品
     * @param payType 1:支付宝 , 2:微信
     */
    private synchronized void payAlAndWx(final OrderInfo good, final String payType) {
        if (!PushManager.getInstance().isPushTurnedOn(mActivity)) {
            LogUtil.e("重新启动-个推链接");
            PushManager.getInstance().initialize(mActivity);
        }

        /** 将显示二维码的地方 - 设置未NULL*/
        iv_code.setImageBitmap(null);

        String url = AppConstant.HOST + "orderInfo/createPingPlusCharge";
        // 添加请求参数
        String machineId = VMPreferences.getInstance().getVMId();
        String orderNo = good.getOrderNo();
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("machineId", machineId);
        params.addBodyParameter("orderNo", orderNo);
        params.addBodyParameter("payType", payType);//1:支付宝 , 2:微信
        LogUtil.i("----machineId = " + machineId + "-- orderNo = " + orderNo + "-  payType = " + payType);
        DialogUtil.showLoading(mActivity);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DialogUtil.hideLoading();
                try {
                    JSONObject json = new JSONObject(result);
                    LogUtil.i("-----------strCharge------" + json.toString());

                    Charge charge = GsonUtil.createGson(DateUtil.dateFormatYMDHMS).fromJson(json.getString("charge"), Charge.class);
                    String qr = "";
                    if (payType.equals(AL)) {
                        LogUtil.i("-------------支付宝----支付");
                        /** 支付宝 二维码*/
                        qr = charge.getCredential().get("alipay_qr").toString();
                    } else if (payType.equals(WX)) {
                        LogUtil.i("-------------微信----支付");
                        /** 微信 二维码*/
                        qr = charge.getCredential().get("wx_pub_qr").toString();
                    }
                    /**  生成二维码 */
                    bitmap = QRUtil.create2DCode(qr);
                    iv_code.setImageBitmap(bitmap);

                    ll_code_bg.setVisibility(View.VISIBLE);
                    ll_code_bg.setBackgroundColor(mActivity.getResources().getColor(android.R.color
                            .white));
                    cache.put(good.getOrderNo() + payType, PayWaySelectListener.this.bitmap, 120);//缓存保存120秒  ACache
                    // .TIME_SECOND

                    orderInfo.setPayWay(payType);

                    if (null == orderCheckThread) {
                        orderCheckThread = new OrderCheckThread(mActivity, orderInfo);
                        orderCheckThread.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                DialogUtil.hideLoading();
                LogUtil.e(ex.getLocalizedMessage());
                Toast.makeText(mActivity, "网络连接故障,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 回收缓存
     */
    public void recycleCache() {
        cache.remove(orderInfo.getOrderNo() + AL);
        cache.remove(orderInfo.getOrderNo() + WX);
        QRUtil.recycleImageView(iv_code);
        if (bitmap != null) {
            bitmap.recycle();
        }
        System.gc();
    }

    /**
     * 关闭 支付宝/微信
     */
    public void closeLine() {
        /** 关闭图片缓存*/
        if (bitmap != null && !bitmap.isRecycled()) {
            iv_code.setImageBitmap(null);
            bitmap.recycle();
            bitmap = null;
            LogUtil.i("-关闭图片缓存-");
        }
        if (null != orderCheckThread) {
            orderCheckThread.interrupt();
            orderCheckThread = null;
            LogUtil.i("-关闭线上支付-");
        }

        // 提醒系统回收图片
        System.gc();
    }

    public void cloasCache() {
        if (cache != null) {
            cache.clear();
        }
    }

}
