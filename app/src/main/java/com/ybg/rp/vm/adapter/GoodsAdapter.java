package com.ybg.rp.vm.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.ybg.rp.vm.R;
import com.ybg.rp.vm.listener.AddOperationListener;
import com.ybg.rp.vm.listener.GoodsItemListener;
import com.ybg.rp.vm.listener.LoadFinishCallBack;
import com.ybg.rp.vm.listener.MinusOperationListener;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vm.utils.DialogUtil;
import com.ybg.rp.vmbase.bean.GoodsInfo;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.GsonUtil;
import com.ybg.rp.vmbase.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangbagang on 16/8/26.
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsHolder> {

    private ArrayList<GoodsInfo> data;

    private Activity mActivity;
    private ArrayList<GoodsInfo> cartData;             //购物车数据
    private Handler mHandler;

    private LoadFinishCallBack mLoadFinisCallBack;
    private LinearLayout ll_no_data; //显示数据

    private int start = 1;
    private Long id;

    public GoodsAdapter(Activity mActivity, ArrayList<GoodsInfo> cartData, Handler mHandler
            , LoadFinishCallBack mLoadFinisCallBack, LinearLayout ll_no_data) {
        this.mActivity = mActivity;
        this.cartData = cartData;
        this.mHandler = mHandler;
        this.mLoadFinisCallBack = mLoadFinisCallBack;
        this.ll_no_data = ll_no_data;
        this.data = new ArrayList<>();
    }

    @Override
    public GoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goods, parent, false);
        return new GoodsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GoodsHolder holder, final int position) {
        /**设置数据*/
        GoodsInfo goodsInfo = data.get(position);
        holder.tv_name.setText(goodsInfo.getGoodsName());
        holder.tv_price.setText("¥ " + goodsInfo.getPrice());
        holder.tv_standard.setText(goodsInfo.getGoodsDesc());

        String goodsPic = goodsInfo.getGoodsPic();
        Glide.with(mActivity)
                .load(goodsPic)
                .placeholder(R.mipmap.icon_default_pic)
                .error(R.mipmap.icon_default_pic)
                .into(holder.iv_image);

        //如果购物车没有数据,将商品信息的数量设为0
        if (cartData.size() < 1) {
            //清空购物车后
            goodsInfo.setNum(0);
        } else {
            //购物车有数据
            for (GoodsInfo info : cartData) {
                String id = info.getGid();
                String id1 = goodsInfo.getGid();
                int num = info.getNum();
                if (id.equals(id1)) {
                    goodsInfo.setNum(num);
                }
            }
        }
        holder.tv_count.setText(goodsInfo.getNum() + "");

        //根据商品数量是否显示 删除按钮
        if (goodsInfo.getNum() > 0) {
            holder.tv_count.setVisibility(View.VISIBLE);
            holder.iv_minus.setVisibility(View.VISIBLE);
        } else {
            holder.tv_count.setVisibility(View.INVISIBLE);
            holder.iv_minus.setVisibility(View.INVISIBLE);
        }
        holder.iv_add.setOnClickListener(new AddOperationListener(holder.iv_image, mActivity, data,
                cartData, mHandler, position, this));
        holder.iv_minus.setOnClickListener(new MinusOperationListener(mActivity, data, cartData,
                mHandler, position, this));
        holder.ll_item.setOnClickListener(new GoodsItemListener(mActivity,goodsInfo,cartData,
                position));
        //检查是否可以用券
        if (goodsInfo.getYhEnable() == 1) {
            holder.yh_image.setVisibility(View.VISIBLE);
        } else {
            holder.yh_image.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 获取小类数据
     *
     * @param currPage 页码
     * @param id       大类商品ID
     */
    public void getSmallGoodsData(final int currPage, Long id) {
        LogUtil.e("请求分页  ---  currPage = " + currPage + "--- ID = " + id);
        //TbLog.i("----bid"+ id+"vid"+YFApplication.getInstance().initParams().getMachineId());
        this.start = currPage;
        this.id = id;
        String url = AppConstant.HOST + "vendLayerTrackGoods/queryGoodsByTypeOne";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("bid", "" + id);         //大类id
        params.addBodyParameter("vid", VMPreferences.getInstance().getVMId());          //机器id
        params.addBodyParameter("pageNum", ""+currPage);
        params.addBodyParameter("pageSize", ""+30);
        DialogUtil.showLoading(mActivity);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DialogUtil.hideLoading();
                LogUtil.i("---------GoodsAdapter/getSmallGoodsData: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    Type type = new TypeToken<List<GoodsInfo>>() {
                    }.getType();
                    List<GoodsInfo> list = GsonUtil.createGson().fromJson(json.getString("list"), type);
                    if (list.size() < 1) {
                        Toast.makeText(mActivity, "暂无更多数据", Toast.LENGTH_SHORT).show();
                        start--;
                    }
                    if (currPage == 1) {
                        data.clear();
                    }
                    data.addAll(list);
                    notifyDataSetChanged();
                    mLoadFinisCallBack.loadFinish();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (ll_no_data.getVisibility() == View.VISIBLE)
                        ll_no_data.setVisibility(View.GONE);//隐藏无数据图片
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLoadFinisCallBack.loadFinish();
                LogUtil.e("获取商品信息-请求失败-" + ex.getLocalizedMessage());
                DialogUtil.hideLoading();
                if (ll_no_data.getVisibility() == View.GONE)
                    ll_no_data.setVisibility(View.VISIBLE);//显示无数据图片
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
     * 加载更多
     */
    public void loadNextPage() {
        LogUtil.e("请求---loadNextPage");
        start++;
        getSmallGoodsData(start, id);
    }


    /**
     * 自定义holder类,继承RecyclerView的ViewHolder
     */
    public class GoodsHolder extends RecyclerView.ViewHolder {

        private CardView ll_item;   //单个item
        private ImageView iv_bg;
        private ImageView iv_image; //商品图片
        private ImageView yh_image; //是否可以用券
        private TextView tv_name;   //商品名
        private TextView tv_price;  //价格
        private TextView tv_standard;   //规格
        private TextView tv_count;      //商品数量

        private ImageView iv_add;   //添加
        private ImageView iv_minus;     //删除

        public GoodsHolder(View itemView) {
            super(itemView);
            //找id
            //iv_bg = (ImageView) itemView.findViewById(R.id.goods_iv_bg);
            ll_item = (CardView) itemView.findViewById(R.id.goods_card_item);
            iv_image = (ImageView) itemView.findViewById(R.id.goods_iv_image);
            yh_image = (ImageView) itemView.findViewById(R.id.yh_image);
            iv_minus = (ImageView) itemView.findViewById(R.id.goods_iv_minus);
            iv_add = (ImageView) itemView.findViewById(R.id.goods_iv_add);

            tv_name = (TextView) itemView.findViewById(R.id.goods_tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.goods_tv_price);
            tv_standard = (TextView) itemView.findViewById(R.id.goods_tv_standard);
            tv_count = (TextView) itemView.findViewById(R.id.goods_tv_count);
        }
    }

}
