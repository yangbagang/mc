package com.ybg.rp.vm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ybg.rp.vm.R;
import com.ybg.rp.vmbase.bean.GoodsInfo;

import org.apache.log4j.Logger;

import java.util.List;

public class PayGoodsAdapter extends RecyclerView.Adapter<PayGoodsAdapter.PayGoodsViewHolder> {

    private Context mContext;
    private List<GoodsInfo> data;

    private static Logger logger = Logger.getLogger(PayGoodsAdapter.class);

    public PayGoodsAdapter(Context context, List<GoodsInfo> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public PayGoodsAdapter.PayGoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_goods,
                parent, false);
        return new PayGoodsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PayGoodsViewHolder holder, int position) {
        //设置数据
        GoodsInfo goodsInfo = data.get(position);
        logger.debug("商品：" + goodsInfo);
        holder.tv_name.setText(goodsInfo.getGoodsName());
        holder.tv_standard.setText("规格: " + goodsInfo.getGoodsDesc());
        holder.tv_price.setText("单价: " + goodsInfo.getPrice());
        if ("".equals(goodsInfo.getTrackNo())) {
            holder.tv_rail.setVisibility(View.GONE);
        } else {
            holder.tv_rail.setText("轨道: " + goodsInfo.getTrackNo());
        }
        holder.tv_count.setText("数量: " + goodsInfo.getNum());

        String goodsPic = goodsInfo.getGoodsPic();
        Glide.with(mContext)
                .load(goodsPic)
                .placeholder(R.mipmap.icon_default_pic)
                .error(R.mipmap.icon_default_pic)
                .into(holder.iv_image);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(data.get(position).getGid());
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        return data.size();
    }

    class PayGoodsViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_image;
        private TextView tv_name;       //商品名
        private TextView tv_price;      //价格
        private TextView tv_standard;   //规格
        private TextView tv_count;      //数量
        private TextView tv_rail;   //轨道

        PayGoodsViewHolder(View view) {
            super(view);
            iv_image = (ImageView) view.findViewById(R.id.payGoods_iv_image);
            tv_name = (TextView) view.findViewById(R.id.payGoods_tv_name);
            tv_price = (TextView) view.findViewById(R.id.payGoods_tv_price);
            tv_standard = (TextView) view.findViewById(R.id.payGoods_tv_standard);
            tv_count = (TextView) view.findViewById(R.id.payGoods_tv_count);
            tv_rail = (TextView) view.findViewById(R.id.payGoods_tv_rail);
        }
    }
}
