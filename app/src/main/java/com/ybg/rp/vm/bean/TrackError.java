package com.ybg.rp.vm.bean;

/**
 * Created by yangbagang on 16/8/29.
 */
public class TrackError {

    private String layerNo;
    private String trackNo;
    private boolean isSelect;

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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "TrackError{" +
                "layerNo='" + layerNo + '\'' +
                ", trackNo='" + trackNo + '\'' +
                ", isSelect=" + isSelect +
                '}';
    }
}
