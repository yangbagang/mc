package com.ybg.rp.vm.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by yangbagang on 16/8/29.
 */
@Table(name = "layer_vending")
public class LayerBean implements Serializable {

    private static final long serialVersionUID = 2760026253384825448L;

    /**
     * 机器编号
     */
    @Column(name = "layer_no", isId = true)
    private String layerNo;
    /**
     * 轨道数,格子数
     */
    @Column(name = "track_num")
    private Integer trackNum;
    /**
     * 1：格子柜,0：不是格子柜
     */
    @Column(name = "device_type")
    private Integer deviceType;

    public String getLayerNo() {
        return layerNo;
    }

    public void setLayerNo(String layerNo) {
        this.layerNo = layerNo;
    }

    public Integer getTrackNum() {
        return trackNum;
    }

    public void setTrackNum(Integer trackNum) {
        this.trackNum = trackNum;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "LayerBean{" +
                "layerNo='" + layerNo + '\'' +
                ", trackNum=" + trackNum +
                ", deviceType=" + deviceType +
                '}';
    }

}
