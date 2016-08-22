package com.ybg.rp.vm.listener;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ybg.rp.vm.activity.shopping.ShoppingWithOneGoodsActivity;
import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vm.utils.DialogUtil;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.utils.GsonUtil;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/20.
 */
public class NumInputChangedListener implements TextWatcher {

    private Context mContext;
    private TextView input_text;

    private EditText et_command;
    private VMDBManager vmdbManager;
    private ArrayList<TrackBean> allTrack;

    public NumInputChangedListener(Context mContext, EditText et_command, TextView input_text
            , ArrayList<TrackBean> allTrack) {
        this.et_command = et_command;
        this.input_text = input_text;
        this.mContext = mContext;
        this.allTrack = allTrack;
        this.vmdbManager = VMDBManager.getInstance();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            input_text.setText("");
            String str = s.toString().trim();
            if (null == allTrack || allTrack.size() <= 0) {
                input_text.setText("请先初始化基础信息");
                return;
            }
            if (str.length() >= 3) {
                LogUtil.i("input: str=" + str);
                String track = str.substring(0, 3);
                /**标准轨道只有3位*/
                checkInputTrack(track);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 轨道确认
     */
    private void checkInputTrack(String trackNo) {
        try {
            TrackBean first = vmdbManager.getDb().selector(TrackBean.class).where("track_no", "=", trackNo).findFirst();
            if (null != first) {
                if (first.getFault() == 1) {
                    et_command.setText("");
                    input_text.setText("该轨道已故障,请选择其他轨道");
                } else {
                    /**
                     * 选中了单个商品，进入支付流程。
                     */
                    inputTrackNo(trackNo, VMConstant.ZF_WX);
                }
            } else {
                et_command.setText("");
                input_text.setText("没有该编号,请重新输入");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 轨道编号查询商品
     *
     * @param trackNo 轨道编号
     * @param payType 支付类型 0：离线，1：支付宝，2：微信支付
     */
    public void inputTrackNo(final String trackNo, final String payType) {
        DialogUtil.showLoadding(mContext);
        // 添加请求参数
        String url = AppConstant.HOST + "orderInfo/createOrderWithMachineIdAndTrackNo";
        final XApplication xApplication = (XApplication) mContext.getApplicationContext();
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("machineId", xApplication.getPreference().getVMId());
        params.addBodyParameter("trackNo", trackNo);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    String success = json.getString("success");
                    String msg = json.getString("msg");
                    if ("success".equals(success)) {
                        OrderInfo orderInfo = GsonUtil.createGson().fromJson(json.getString("orderInfo"), OrderInfo.class);

                        xApplication.setIsOpenTrack(orderInfo.getOrderNo());//设置订单号

                        LogUtil.i("--输入编号生成的订单--" + orderInfo.toString());

                        /** 跳转支付单个页面*/
                        Intent payIntent = new Intent();
                        payIntent.setClass(mContext, ShoppingWithOneGoodsActivity.class);
                        payIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        payIntent.putExtra("orderInfo", orderInfo);
                        payIntent.putExtra("type", payType);
                        mContext.startActivity(payIntent);
                        /**清空文字*/
                        et_command.setText("");
                        input_text.setText("");
                    } else {
                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DialogUtil.removeDialog(mContext);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.e(throwable.getLocalizedMessage());
                et_command.setText("");
                input_text.setText("");
                Toast.makeText(mContext, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                DialogUtil.removeDialog(mContext);
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
