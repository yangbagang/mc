package com.ybg.rp.vm.bean;

import java.io.Serializable;
import java.util.List;

public class ChargeRefundCollection implements Serializable{

    private static final long serialVersionUID = -5280393843127098442L;
    private String object;
    private String url;
    private Boolean hasMore;
    private List<Refund> data;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<Refund> getData() {
        return data;
    }

    public void setData(List<Refund> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChargeRefundCollection[" +
                "object='" + object + '\'' +
                ", url='" + url + '\'' +
                ", hasMore=" + hasMore +
                ", data=" + data +
                ']';
    }
}
