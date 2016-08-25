package com.ybg.rp.vm.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by yangbagang on 16/8/25.
 */
@Table(name = "device_brand")
public class DeviceBrand implements Serializable {

    private static final long serialVersionUID = -347962368978767738L;

    public static final String yifeng = "yifeng";

    @Column(name = "id", isId = true)
    private long id;

    /**
     * 0：主机 1：格子柜, 2 副柜
     */
    @Column(name = "device_type")
    private Integer deviceType;

    /**
     * 品牌
     */
    @Column(name = "device_brand")
    private String brand;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "DeviceBrand{" +
                "id=" + id +
                ", deviceType=" + deviceType +
                ", brand='" + brand + '\'' +
                '}';
    }
}
