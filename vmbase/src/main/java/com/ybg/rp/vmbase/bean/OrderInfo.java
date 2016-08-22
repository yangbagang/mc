package com.ybg.rp.vmbase.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/20.
 */
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = -3368302904519123474L;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单总额
     */
    private Double orderMoney;
    /**
     * 商品信息
     */
    private ArrayList<GoodsInfo> goodsInfo;

    /**
     * 支付类型
     */
    private String payWay;

    /**
     * 是否支付成功(本地使用)
     */
    private boolean payStatus;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Double getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Double orderMoney) {
        this.orderMoney = orderMoney;
    }

    public ArrayList<GoodsInfo> getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(ArrayList<GoodsInfo> goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public boolean isPayStatus() {
        return payStatus;
    }

    public void setPayStatus(boolean payStatus) {
        this.payStatus = payStatus;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "orderNo='" + orderNo + '\'' +
                ", orderMoney=" + orderMoney +
                ", goodsInfo=" + goodsInfo +
                ", payWay='" + payWay + '\'' +
                ", payStatus=" + payStatus +
                '}';
    }
}
