package com.ybg.rp.vm.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "tran_data_update")
public class TranDataUpdate implements Serializable {

    private static final long serialVersionUID = 501268615118414999L;
    /**
     * 已上传
     */
    public static int UPD_SUCCESS = 1;
    /**
     * 未上传
     */
    public static int UPD_FULT = 0;

    @Column(name = "order_no", isId = true)
    private String orderNo;


    @Column(name = "card_upd")
    private int cardUpd = 0;

    @Column(name = "service_upd")
    private int serviceUpd = 0;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getCardUpd() {
        return cardUpd;
    }

    public void setCardUpd(int cardUpd) {
        this.cardUpd = cardUpd;
    }

    public int getServiceUpd() {
        return serviceUpd;
    }

    public void setServiceUpd(int serviceUpd) {
        this.serviceUpd = serviceUpd;
    }

    @Override
    public String toString() {
        return "TranDataUpdate[" +
                "orderNo='" + orderNo + '\'' +
                ", cardUpd=" + cardUpd +
                ", serviceUpd=" + serviceUpd +
                ']';
    }
}
