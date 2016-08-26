package com.ybg.rp.vm.listener;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ybg.rp.vm.activity.shopping.GoodsWindowActivity;
import com.ybg.rp.vm.adapter.GoodsAdapter;
import com.ybg.rp.vmbase.bean.GoodsInfo;
import com.ybg.rp.vmbase.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/26.
 */
public class AddOperationListener implements View.OnClickListener {

    private ImageView image;
    private Activity mActivity;
    private ArrayList<GoodsInfo> data;
    private ArrayList<GoodsInfo> cartData;
    private Handler mHandler;
    private Integer position;
    private GoodsAdapter goodsAdapter;

    public AddOperationListener(ImageView image,Activity mActivity, ArrayList<GoodsInfo> data, ArrayList<GoodsInfo> cartData,
                               Handler mHandler, Integer position, GoodsAdapter goodsAdapter) {
        this.image = image;
        this.mActivity = mActivity;
        this.data = data;
        this.cartData = cartData;
        this.mHandler = mHandler;
        this.position = position;
        this.goodsAdapter = goodsAdapter;
    }

    @Override
    public void onClick(final View v) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //改变商品显示数量
                GoodsInfo goodsInfo = data.get(position);
                int count = goodsInfo.getNum();
                int kuCun = goodsInfo.getKucun();
                LogUtil.i("add:count------" + count);
                LogUtil.i("add:kuCun------" + kuCun);
                if (count == kuCun) {
                    Toast.makeText(mActivity, "没有更多库存!", Toast.LENGTH_SHORT).show();
                    return;
                }

                /**设置添加商品动画*/
                int[] startLocation = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
                v.getLocationInWindow(startLocation);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
                ImageView ball = new ImageView(mActivity);// buyImg是动画的图片，
                ball.setImageDrawable(image.getDrawable());// 设置buyImg的图片
                ball.setTag(startLocation);
                Message msg = new Message();
                msg.what = GoodsWindowActivity.SHOPPING_CART;
                msg.obj = ball;
                mHandler.sendMessage(msg);

                count++;
                goodsInfo.setNum(count);

                boolean isok = false;
                for (int i = 0; i < cartData.size(); i++) {
                    GoodsInfo goodsInfo1 = cartData.get(i);
                    //如果gid相同就+1
                    if (goodsInfo1.getGid().equals(goodsInfo.getGid())) {
                        goodsInfo1.setNum(count);
                        isok = true;
                    }
                }
                //有数据的情况下,gid不相同,还是添加进去
                if (!isok) {
                    goodsInfo.setNum(1);
                    cartData.add(goodsInfo);
                }
                goodsAdapter.notifyDataSetChanged();
                mHandler.sendEmptyMessage(GoodsWindowActivity.CHANGE_UI);
            }
        });
    }

}
