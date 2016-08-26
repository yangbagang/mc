package com.ybg.rp.vm.listener;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.ybg.rp.vm.activity.shopping.GoodsWindowActivity;
import com.ybg.rp.vm.adapter.CartAdapter;
import com.ybg.rp.vm.adapter.GoodsAdapter;
import com.ybg.rp.vmbase.bean.GoodsInfo;

import java.util.ArrayList;

public class CartAddShoppingListener implements View.OnClickListener {

    private Activity mActivity;
    private ArrayList<GoodsInfo> cartData;             //购物车数据
    private Handler mHandler;
    private Integer position;
    private CartAdapter baseAdapter;
    private GoodsAdapter mGoodsAdapter;

    public CartAddShoppingListener(Activity mActivity, ArrayList<GoodsInfo> cartData, Handler mHandler,
                                   Integer position, CartAdapter baseAdapter, GoodsAdapter goodsAdapter) {
        this.mActivity = mActivity;
        this.cartData = cartData;
        this.mHandler = mHandler;
        this.position = position;
        this.baseAdapter = baseAdapter;
        this.mGoodsAdapter = goodsAdapter;
    }

    @Override
    public void onClick(View v) {
        //改变商品显示数量
        GoodsInfo goodsInfo = cartData.get(position);
        int count = goodsInfo.getNum();
        if (count == goodsInfo.getKucun()) {
            Toast.makeText(mActivity, "没有更多库存!", Toast.LENGTH_SHORT).show();
            return;
        }

        count++;
        goodsInfo.setNum(count);

        baseAdapter.notifyDataSetChanged();
        mGoodsAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessage(GoodsWindowActivity.CHANGE_UI);
    }
}
