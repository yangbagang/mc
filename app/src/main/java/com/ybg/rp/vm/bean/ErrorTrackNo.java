package com.ybg.rp.vm.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.Date;

@Table(name = "error_track_no")
public class ErrorTrackNo implements Serializable {

    private static final long serialVersionUID = -3548302270580753608L;

    @Column(name = "id", isId = true)
    private long id;

    /**
     * 轨道
     */
    @Column(name = "track_no")
    private String trackNo;

    /**
     * 错误信息
     */
    @Column(name = "err_msg")
    private String errMsg;

    /**
     * 是否上传 0:未 ,1 :已
     */
    @Column(name = "is_upd")
    private int isUpd = 0;


    /**
     * 类型： 0 ： 测试 ， 1： 订单
     */
    @Column(name = "type")
    private int type;

    /**
     * 订单号
     */
    @Column(name = "type")
    private String orderNo;

    @Column(name = "create_time")
    private Date date = new Date();


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getIsUpd() {
        return isUpd;
    }

    public void setIsUpd(int isUpd) {
        this.isUpd = isUpd;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "ErrorTrackNo[" +
                "id=" + id +
                ", trackNo='" + trackNo + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", isUpd=" + isUpd +
                ", type='" + type + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", date=" + date +
                ']';
    }
}
