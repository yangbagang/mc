package com.ybg.rp.vm.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by yangbagang on 16/8/20.
 */
@Table(name = "vending_track")
public class TrackBean implements Serializable {

    private static final long serialVersionUID = 2673387984351774984L;

    /**
     * 轨道-良好
     */
    public static final int FAULT_O = 0;
    /**
     * 轨道-故障
     */
    public static final int FAULT_E = 1;


    /**
     * 0：良好,1：故障
     */
    @Column(name = "fault")
    private Integer fault;

    /**
     * 1：格子柜,0：不是格子柜
     */
    @Column(name = "device_type")
    private Integer gridMark;

    /**
     * 层号
     */
    @Column(name = "layer_no")
    private String layerNo;

    /**
     * 轨道编号
     */
    @Column(name = "track_no", isId = true)
    private String trackNo;

    /**
     * 最大库存
     */
    @Column(name = "max_inventory")
    private Integer maxInventory;

    public Integer getFault() {
        return fault;
    }

    public void setFault(Integer fault) {
        this.fault = fault;
    }

    public Integer getGridMark() {
        return gridMark;
    }

    public void setGridMark(Integer gridMark) {
        this.gridMark = gridMark;
    }

    public String getLayerNo() {
        return layerNo;
    }

    public void setLayerNo(String layerNo) {
        this.layerNo = layerNo;
    }

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo;
    }

    public Integer getMaxInventory() {
        return maxInventory;
    }

    public void setMaxInventory(Integer maxInventory) {
        this.maxInventory = maxInventory;
    }

    @Override
    public String toString() {
        return "TrackBean{" +
                "fault=" + fault +
                ", gridMark=" + gridMark +
                ", layerNo='" + layerNo + '\'' +
                ", trackNo='" + trackNo + '\'' +
                ", maxInventory=" + maxInventory +
                '}';
    }
}
