package com.ybg.rp.vmbase.bean;

import java.io.Serializable;

/**
 * Created by yangbagang on 16/9/18.
 */
public class Coupon implements Serializable {

    private static final long serialVersionUID = 3896394399238288056L;

    private String code;//编号
    private Integer type;//类型1满减，满100减20；2折扣，7折；
    private Float discount;//折扣，具体几折。
    private Float minMoney;//起点金额，即从多少金额起可以使用。
    private Float yhMoney;//具体减多少
    private Short flag;//是否有效

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(Float minMoney) {
        this.minMoney = minMoney;
    }

    public Float getYhMoney() {
        return yhMoney;
    }

    public void setYhMoney(Float yhMoney) {
        this.yhMoney = yhMoney;
    }

    public Short getFlag() {
        return flag;
    }

    public void setFlag(Short flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "code='" + code + '\'' +
                ", type=" + type +
                ", discount=" + discount +
                ", minMoney=" + minMoney +
                ", yhMoney=" + yhMoney +
                ", flag=" + flag +
                '}';
    }
}
