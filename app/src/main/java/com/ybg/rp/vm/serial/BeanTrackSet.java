package com.ybg.rp.vm.serial;

import java.io.Serializable;

public class BeanTrackSet implements Serializable {

    private static final long serialVersionUID = 2271000095234637023L;

    /**
     * 错误信息
     */
    public String errorInfo = "";

    /**
     * 1:正常,0:错误,2:不确定交易
     */
    public int trackStatus = 1;

    /**
     * 返回的数据
     */
    public String msg = "";

    @Override
    public String toString() {
        return "BeanTrackSet{" +
                "errorInfo='" + errorInfo + '\'' +
                ", trackStatus=" + trackStatus +
                ", msg='" + msg + '\'' +
                '}';
    }

}
