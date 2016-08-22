package com.ybg.rp.vm.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by yangbagang on 16/8/20.
 */
@Table(name = "transaction_data")
public class TransactionData implements Serializable {

    private static final long serialVersionUID = 7379464738887003127L;

    @Column(name = "create_date")
    private String createDate;

    /**
     * 交易订单号
     */
    @Column(name = "order_no", isId = true)
    private String orderNo;

    /**
     * 交易时间
     */
    @Column(name = "transaction_date")
    private String transactionDate;

    /**
     * 支付方式：0：钱包，1：支付宝，2：微信支付
     */
    @Column(name = "pay_type")
    private String payType;

    /**
     * 交易结果 0 取消 1 成功 2 失败
     */
    @Column(name = "sale_result")
    private String saleResult;

    /**
     * 订单总额
     */
    @Column(name = "order_price")
    private Double orderPrice;

    /**
     * 轨道 s
     */
    @Column(name = "track_no")
    private String trackNo;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getSaleResult() {
        return saleResult;
    }

    public void setSaleResult(String saleResult) {
        this.saleResult = saleResult;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo;
    }

    @Override
    public String toString() {
        return "TransactionData{" +
                "createDate='" + createDate + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", payType='" + payType + '\'' +
                ", saleResult='" + saleResult + '\'' +
                ", orderPrice=" + orderPrice +
                ", trackNo='" + trackNo + '\'' +
                '}';
    }
}
