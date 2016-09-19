package com.ybg.rp.vm.popup;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.activity.shopping.ShoppingWithOneGoodsActivity;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vmbase.bean.Coupon;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.utils.GsonUtil;
import com.ybg.rp.vmbase.utils.LogUtil;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by yangbagang on 16/9/18.
 */
public class CouponPopupWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private EditText couponNum;
    private TextView couponNotice;

    private boolean clickable = true;

    private CouponCallback couponCallback;

    public CouponPopupWindow(Context context, CouponCallback couponCallback) {
        super(context);
        this.context = context;
        this.couponCallback = couponCallback;

        View view = View.inflate(context, R.layout.coupon_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        couponNum = (EditText) view.findViewById(R.id.coupon_num);
        couponNotice = (TextView) view.findViewById(R.id.tv_input);
        couponNotice.setText("");
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 800);   //高度偏移量
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.num_00:
                inputNum("0");
                break;
            case R.id.num_01:
                inputNum("1");
                break;
            case R.id.num_02:
                inputNum("2");
                break;
            case R.id.num_03:
                inputNum("3");
                break;
            case R.id.num_04:
                inputNum("4");
                break;
            case R.id.num_05:
                inputNum("5");
                break;
            case R.id.num_06:
                inputNum("6");
                break;
            case R.id.num_07:
                inputNum("7");
                break;
            case R.id.num_08:
                inputNum("8");
                break;
            case R.id.num_09:
                inputNum("9");
                break;
            case R.id.bt_delete:
                removeLastNum();
                break;
            case R.id.bt_close:
                dismiss();
                break;
        }
    }

    private void inputNum(String num) {
        if (clickable) {
            couponNotice.append(num);
        }
        if (couponNotice.getText().length() == 10) {
            clickable = false;
            checkCoupon();
        }
    }

    private void removeLastNum() {
        if (!clickable) {
            clickable = true;
        }
        if (couponNotice.getText().length() > 0) {
            couponNotice.setText(couponNotice.getText().subSequence(0, couponNotice.length() - 1));
        }
    }

    private void checkCoupon() {
        String url = AppConstant.HOST + "coupon/check";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("code", couponNotice.getText().toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String success = json.getString("success");
                    String msg = json.getString("msg");
                    if ("true".equals(success)) {
                        Coupon coupon = GsonUtil.createGson().fromJson(json.getString("coupon"),
                                Coupon.class);

                        couponCallback.setCoupon(coupon);
                        dismiss();
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public interface CouponCallback {

        void setCoupon(Coupon coupon);

    }
}
