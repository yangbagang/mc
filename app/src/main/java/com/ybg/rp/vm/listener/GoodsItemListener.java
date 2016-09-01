package com.ybg.rp.vm.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ybg.rp.vm.activity.shopping.ShoppingWithOneGoodsActivity;
import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vm.utils.DialogUtil;
import com.ybg.rp.vmbase.bean.GoodsInfo;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.GsonUtil;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/26.
 */
public class GoodsItemListener implements View.OnClickListener {

    private Activity mActivity;
    private GoodsInfo mGoodsInfo;
    ArrayList<GoodsInfo> mCartData;
    private int mPositon;

    private int mNum;

    public GoodsItemListener(Activity activity, GoodsInfo goodsInfo, ArrayList<GoodsInfo> cartData, int position) {
        this.mActivity = activity;
        this.mGoodsInfo = goodsInfo;
        this.mPositon = position;
        this.mCartData = cartData;
    }

    /**
     * 点击生成订单,并去支付单个商品的页面
     */
    @Override
    public void onClick(View v) {

        ArrayList<GoodsInfo> list = new ArrayList<>();
        mNum = mGoodsInfo.getNum();     //获取该商品加入购物车的数量,
        mGoodsInfo.setNum(1);       //将该商品信息 设置数量为1,去生成订单
        list.add(mGoodsInfo);

        String jsonStr = GsonUtil.toJsonPropertiesDes(list, "gid", "num");
        LogUtil.i("---Shopping/:" + jsonStr);
        String url = AppConstant.HOST + "orderInfo/createOrderWithMachineIdAndGoodsJson";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("goodsJson", jsonStr);
        params.addBodyParameter("machineId", VMPreferences.getInstance().getVMId());
        //支付方式：默认 0：com.ybg.rp.vm，1：支付宝，2：微信支付
        DialogUtil.showLoading(mActivity);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mGoodsInfo.setNum(mNum);    //不管有无库存,支付成功失败,都将num设置回为原来的num

                DialogUtil.removeDialog(mActivity);
                LogUtil.i("---------AllCagetory/ShoppingActivity: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    OrderInfo orderInfo = GsonUtil.createGson().fromJson(json.getString("orderInfo"), OrderInfo.class);
                    XApplication xApplication = (XApplication) mActivity.getApplicationContext();
                    xApplication.setIsOpenTrack(orderInfo.getOrderNo());//设置订单号

                    LogUtil.d("----orderInfo----: " + orderInfo.toString());
                    Intent intent = new Intent(mActivity, ShoppingWithOneGoodsActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra("type", VMConstant.ZF_WX);
                    bundle.putSerializable("orderInfo", orderInfo);
                    intent.putExtras(bundle);
                    mActivity.startActivity(intent);

                    //生成订单成功就清空购物车
                    mCartData.clear();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mGoodsInfo.setNum(mNum);  //不管有无库存,支付成功失败,都将num设置回为原来的num
                DialogUtil.removeDialog(mActivity);
                Toast.makeText(mActivity, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
