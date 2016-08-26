package com.ybg.rp.vm.listener;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.ybg.rp.vm.activity.shopping.GoodsWindowActivity;
import com.ybg.rp.vm.adapter.GoodsAdapter;
import com.ybg.rp.vmbase.bean.GoodsInfo;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/26.
 */
public class MinusOperationListener implements View.OnClickListener {

    private Activity mActivity;
    private ArrayList<GoodsInfo> data;
    private ArrayList<GoodsInfo> cartData;//购物车数据
    private Handler mHandler;
    private Integer position;
    private GoodsAdapter baseAdapter;

    public MinusOperationListener(Activity mActivity, ArrayList<GoodsInfo> data, ArrayList<GoodsInfo> cartData, Handler mHandler,
                                Integer position, GoodsAdapter baseAdapter) {
        this.mActivity = mActivity;
        this.data = data;
        this.cartData = cartData;
        this.mHandler = mHandler;
        this.position = position;
        this.baseAdapter = baseAdapter;
    }

    @Override
    public void onClick(final View v) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GoodsInfo goodsInfo = data.get(position);
                int count = goodsInfo.getNum();
                count--;
                goodsInfo.setNum(count);

                //删除购物车数据
                for (int i = 0; i < cartData.size(); i++) {
                    GoodsInfo goodsInfo1 = cartData.get(i);
                    //如果gid相同就-1
                    if (goodsInfo1.getGid().equals(goodsInfo.getGid())) {
                        goodsInfo1.setNum(count);
                        if (count < 1) {
                            cartData.remove(goodsInfo1);
                        }
                    }
                }
                baseAdapter.notifyDataSetChanged();
                mHandler.sendEmptyMessage(GoodsWindowActivity.CHANGE_UI);
            }
        });
    }

}
