package com.ybg.rp.vm.activity.shopping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ybg.rp.vm.R;
import com.ybg.rp.vm.adapter.CartAdapter;
import com.ybg.rp.vm.adapter.GoodsAdapter;
import com.ybg.rp.vm.adapter.TypeOneAdapter;
import com.ybg.rp.vm.animation.GoodsAnimation;
import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.listener.LoadMoreListener;
import com.ybg.rp.vm.popup.ShopCartPopupWindow;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vm.utils.DialogUtil;
import com.ybg.rp.vm.views.AutoLoadRecyclerView;
import com.ybg.rp.vm.views.SpaceItemDecoration;
import com.ybg.rp.vmbase.bean.GoodsInfo;
import com.ybg.rp.vmbase.bean.OrderInfo;
import com.ybg.rp.vmbase.bean.TypeOne;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.GsonUtil;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangbagang on 16/8/20.
 */
public class GoodsWindowActivity extends Activity implements View.OnClickListener {

    private final Logger log = Logger.getLogger(GoodsWindowActivity.class);

    private LinearLayout ll_back;   //返回上一页
    private TextView tv_title;      //倒计时显示

    private TextView tv_count;      //购物车已选商品数量
    private TextView tv_total_money;    //购物车总金额

    private LinearLayout ll_weixin; // 微信支付
    private LinearLayout ll_zhifubao; // 支付宝支付

    private LinearLayout ll_no_data;    //加载失败
    private Button btn_reload;  //重新加载
    private RelativeLayout rl_cart;     //购物车

    private AutoLoadRecyclerView recyclerViewBig;     //大类数据
    private AutoLoadRecyclerView recyclerViewSmall;   //大类对应商品数据

    private ArrayList<TypeOne> typeOnes;       //大类数据
    private ArrayList<GoodsInfo> cartDatas;             //购物车数据

    private Long id;     //大类商品id

    private TypeOneAdapter typeOneAdapter;        //大类数据
    private GoodsAdapter goodsAdapter;      //小类数据adapter

    private ShopCartPopupWindow mPopupWindow;
    private CartAdapter mCartAdapter;

    /**
     * 购物车动画-点击
     */
    private GoodsAnimation goodsAnimation;

    private int time = 120;//倒计时

    /**
     * 更新购物车金额和商品显示
     */
    public static final int CHANGE_UI = 1000;
    public static final int SHOPPING_CART = 1001;
    public static final int SHOPPING_CLEAR_CART = 1002;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_UI:
                    changeUi();
                    break;
                case SHOPPING_CART:
                    ImageView ball = (ImageView) msg.obj;
                    goodsAnimation.setAnim(ball, tv_count, (int[]) ball.getTag());// 开始执行动画
                    break;
                case SHOPPING_CLEAR_CART:
                    if (null != cartDatas)
                        cartDatas.clear();
                    if (null != goodsAdapter)
                        goodsAdapter.notifyDataSetChanged();
                    if (mCartAdapter != null) {
                        mCartAdapter.notifyDataSetChanged();
                    }
                    changeUi();
                    if (null != mPopupWindow)
                        mPopupWindow.dismiss();
                    break;
                case 158:
                    tv_title.setText(time + "S");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** 硬件加速*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.goods_layout);

        findView();
        mHandler.postDelayed(runnable, 1000);
        goodsAnimation = new GoodsAnimation(GoodsWindowActivity.this);

        typeOnes = new ArrayList<TypeOne>();
        cartDatas = new ArrayList<>();
        init();

        //进入页面就获取大类数据
        getBigGoodsData();
    }

    private void findView() {
        tv_title = (TextView) findViewById(R.id.shopping_tv_title);
        ll_back = (LinearLayout) findViewById(R.id.shopping_ll_back);

        tv_count = (TextView) findViewById(R.id.shopping_tv_count);
        tv_total_money = (TextView) findViewById(R.id.shopping_tv_total_money);
        ll_weixin = (LinearLayout) findViewById(R.id.shopping_ll_weinxi);
        ll_zhifubao = (LinearLayout) findViewById(R.id.shopping_ll_zhifubao);
        ll_no_data = (LinearLayout) findViewById(R.id.shopping_ll_nodata);
        btn_reload = (Button) findViewById(R.id.shopping_btn_reload);
        recyclerViewBig = (AutoLoadRecyclerView) findViewById(R.id.shopping_recycler_view_big);
        recyclerViewSmall = (AutoLoadRecyclerView) findViewById(R.id.shopping_recycler_view_small);
        rl_cart = (RelativeLayout) findViewById(R.id.shopping_rl_cart);
    }

    /**
     * 倒计时
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            if (time == 0) {
                finish();
            } else {
                mHandler.sendEmptyMessage(158);
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(time<120) {
            time = 120;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(time<120) {
            time = 120;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        time = 120;
        mHandler.sendEmptyMessage(CHANGE_UI);
        goodsAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化
     */
    private void init() {
        ll_back.setOnClickListener(this);
        /** 支付方式-点击事件*/
        ll_weixin.setOnClickListener(this);
        ll_zhifubao.setOnClickListener(this);

        btn_reload.setOnClickListener(this);
        rl_cart.setOnClickListener(this);

        /** LOAD BIG DATA*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBig.setLayoutManager(layoutManager);
        recyclerViewBig.setHasFixedSize(true);
        recyclerViewBig.setOnPauseListenerParams(false, false);
        recyclerViewBig.setItemAnimator(new DefaultItemAnimator());
        recyclerViewBig.addItemDecoration(new SpaceItemDecoration(8));

        /**大类商品数据 (RecyclerView) */
        typeOneAdapter = new TypeOneAdapter(this, typeOnes);
        recyclerViewBig.setAdapter(typeOneAdapter);
        //大类点击获取小类商品数据
        typeOneAdapter.setOnItemClickListener(new TypeOneAdapter.ItemClickListener() {
            @Override
            public void onItemCLick(View view, int position) {
                TypeOne typeOne = typeOnes.get(position);
                id = typeOne.getId();
                /**根据大类id获取小类数据*/
                goodsAdapter.getSmallGoodsData(1, id);

                typeOneAdapter.setSelectIndex(position);
                typeOneAdapter.notifyDataSetChanged();
            }
        });

        /** LOAD GOODS INFO*/
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewSmall.setLayoutManager(gridLayoutManager);
        recyclerViewSmall.setHasFixedSize(true);
        recyclerViewSmall.setOnPauseListenerParams(false, false);

        goodsAdapter = new GoodsAdapter(this, cartDatas, mHandler, recyclerViewSmall, ll_no_data);
        recyclerViewSmall.setAdapter(goodsAdapter);

        recyclerViewSmall.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                goodsAdapter.loadNextPage();
            }
        });

        /** 购物车*/
        mCartAdapter = new CartAdapter(this, cartDatas, mHandler, goodsAdapter);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopping_ll_back:
                //返回上一页
                finish();
                break;
            case R.id.shopping_ll_weinxi:
                //微信支付
                generateOrder(VMConstant.ZF_WX);
                break;
            case R.id.shopping_ll_zhifubao:
                //支付宝支付
                generateOrder(VMConstant.ZF_ZFB);
                break;
            case R.id.shopping_btn_reload:
                //重新加载
                getBigGoodsData();

                break;

            case R.id.shopping_rl_cart:
                /**购物车弹窗*/
                if (cartDatas.size() > 0)
                    showCartPopwindow();
                break;

        }
    }

    /**
     * 获取大类商品数据
     */
    private void getBigGoodsData() {
        String url = AppConstant.HOST + "goodsTypeOne/listAll";
        RequestParams params = new RequestParams(url);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.i("---------AllCagetory/ShoppingActivity: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    Type type = new TypeToken<List<TypeOne>>() {
                    }.getType();
                    List<TypeOne> list = GsonUtil.createGson().fromJson(json.getString("list"), type);
                    if (list != null) {
                        typeOnes.addAll(list);
                        typeOneAdapter.notifyDataSetChanged();

                        //默认获取大类相对应的小类商品
                        TypeOne typeOne = typeOnes.get(0);
                        id = typeOne.getId();
                        goodsAdapter.getSmallGoodsData(1, id);
                    }
                    recyclerViewBig.loadFinish();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ll_no_data.getVisibility() == View.VISIBLE)
                        ll_no_data.setVisibility(View.GONE);//隐藏无数据图片
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("获取大类商品信息-请求失败-" + ex.getLocalizedMessage());
                Toast.makeText(GoodsWindowActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
     * 生成订单,    type :  1-支付宝,  2-微信
     */
    private void generateOrder(final String type) {
        if (cartDatas.size() > 0) {
            String jsonStr = GsonUtil.toJsonPropertiesDes(cartDatas, "gid", "num");
            LogUtil.i("---Shopping/:" + jsonStr);
            String url = AppConstant.HOST + "orderInfo/createOrderWithMachineIdAndGoodsJson";
            RequestParams params = new RequestParams(url);
            params.addBodyParameter("goodsJson", jsonStr);
            params.addBodyParameter("machineId", VMPreferences.getInstance().getVMId());
            //支付方式：默认 0：com.ybg.rp.vm，1：支付宝，2：微信支付
            DialogUtil.showLoading(GoodsWindowActivity.this);
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    DialogUtil.hideLoading();
                    LogUtil.i("---------makeOrder/ShoppingActivity: " + result);
                    try {
                        JSONObject json = new JSONObject(result);
                        OrderInfo orderInfo = GsonUtil.createGson().fromJson(json.getString("orderInfo"), OrderInfo.class);
                        XApplication xApplication = (XApplication) getApplication();
                        xApplication.setIsOpenTrack(orderInfo.getOrderNo());//设置订单号

                        LogUtil.d("----orderInfo----: " + orderInfo.toString());
                        log.info("--购物车生成的订单--"+orderInfo.toString());

                        Intent intent = new Intent(GoodsWindowActivity.this,
                                ShoppingWithCartActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putExtra("type", type);
                        bundle.putSerializable("orderInfo", orderInfo);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        cartDatas.clear();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    DialogUtil.hideLoading();
                    Toast.makeText(GoodsWindowActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        } else {
            Toast.makeText(this, "您还没有选择商品!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 购物车弹窗
     */
    private void showCartPopwindow() {
        if (null == mPopupWindow) {
            mPopupWindow = new ShopCartPopupWindow(GoodsWindowActivity.this, mCartAdapter, new ShopCartPopupWindow
                    .ClearAllCartListener() {
                @Override
                public void clearAllCart() {
                    mHandler.sendEmptyMessage(GoodsWindowActivity.SHOPPING_CLEAR_CART);
                }
            });
        }
        mPopupWindow.showPopupWindow(rl_cart);
    }

    /**
     * 计算总金额,和已选商品数量
     */
    private void changeUi() {
        int tvCount = 0;
        double totalMoney = 0d;
        //计算总金额数量
        if (null != cartDatas && cartDatas.size() > 0) {
            for (int i = 0; i < cartDatas.size(); ++i) {
                GoodsInfo cartInfo = cartDatas.get(i);
                int num = cartInfo.getNum();
                double money = cartInfo.getPrice();

                totalMoney = totalMoney + (money * num);
                tvCount = tvCount + num;
            }
        }
        if (tvCount == 0) {
            tv_count.setVisibility(View.INVISIBLE);
        } else {
            tv_count.setVisibility(View.VISIBLE);
        }
        tv_total_money.setText(String.format("%.2f", totalMoney));
        tv_count.setText(String.valueOf(tvCount));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler=null;

        cartDatas.clear();
        cartDatas = null;
        recyclerViewBig = null;
        recyclerViewSmall = null;
        if(typeOnes != null) {
            typeOnes = null;
        }
    }

}
