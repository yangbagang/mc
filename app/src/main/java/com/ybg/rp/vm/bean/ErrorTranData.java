package com.ybg.rp.vm.bean;

import android.text.TextUtils;

import com.ybg.rp.vmbase.utils.DateUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 订单支付后-部分轨道出错
 * 包            名:      com.cnpay.vending.yifeng.entity.db
 * 类            名:      ErrorTranData
 * 修 改 记 录:     // 修改历史记录，包括修改日期、修改者及修改内容
 * 版 权 所 有:     版权所有(C)2010-2015
 * 公             司:
 *
 * @author liyuanming
 * @version V1.0
 * @date 2016/4/25
 */
@Table(name = "error_tran_data")
public class ErrorTranData implements Serializable {

    private static final long serialVersionUID = -8638282016647021368L;

    @Column(name = "id", isId = true)
    private long id;

    @Column(name = "order_no")
    private String orderNo;


    @Column(name = "g_id")
    private String gid;

    /**
     * 名字
     */
    @Column(name = "goods_name")
    private String goodsName;
    /**
     * 价格
     */
    @Column(name = "price")
    private Double price;
    /**
     * 轨道
     */
    @Column(name = "track_no")
    private String trackNo;

    /**
     * 是否上传，默认 （0：未上传，1：已上传）
     */
    @Column(name = "is_upd")
    private int isUpd = 0;

    @Column(name = "create_date")
    private String createDate;


    @Override
    public String toString() {
        return "ErrorTranData[" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", gid='" + gid + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                ", trackNo='" + trackNo + '\'' +
                ", isUpd=" + isUpd +
                ", createDate='" + createDate + '\'' +
                ']';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo;
    }

    public int getIsUpd() {
        return isUpd;
    }

    public void setIsUpd(int isUpd) {
        this.isUpd = isUpd;
    }

    public String getCreateDate() {
        if (TextUtils.isEmpty(createDate)) {
            createDate = DateUtil.getCurrentDate(DateUtil.dateFormatYMDHMS);
        }
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
