package com.ybg.rp.vmbase.bean;

import java.io.Serializable;

/**
 * Created by yangbagang on 16/8/18.
 */
public class VMOperator implements Serializable {

    private static final long serialVersionUID = -3115697269274990708L;
    //操作员ID
    private Long operatorId;

    //操作员名称
    private String operatorName;

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public String toString() {
        return "VMOperator{" +
                "operatorId=" + operatorId +
                ", operatorName='" + operatorName + '\'' +
                '}';
    }

}
