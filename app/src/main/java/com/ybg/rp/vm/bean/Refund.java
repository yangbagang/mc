package com.ybg.rp.vm.bean;

import java.io.Serializable;
import java.util.Map;

public class Refund implements Serializable{

    private static final long serialVersionUID = 1919649977404679970L;
    private String id;
    private String object;
    private String orderNo;
    private Integer amount;
    private Long created;
    private Boolean succeed;
    private String status;
    private Long timeSucceed;
    private String description;
    private String failureCode;
    private String failureMsg;
    private Map<String, String> metadata;
    private String charge;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Boolean getSucceed() {
        return succeed;
    }

    public void setSucceed(Boolean succeed) {
        this.succeed = succeed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimeSucceed() {
        return timeSucceed;
    }

    public void setTimeSucceed(Long timeSucceed) {
        this.timeSucceed = timeSucceed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }

    public String getFailureMsg() {
        return failureMsg;
    }

    public void setFailureMsg(String failureMsg) {
        this.failureMsg = failureMsg;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    @Override
    public String toString() {
        return "Refund[" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", amount=" + amount +
                ", created=" + created +
                ", succeed=" + succeed +
                ", status='" + status + '\'' +
                ", timeSucceed=" + timeSucceed +
                ", description='" + description + '\'' +
                ", failureCode='" + failureCode + '\'' +
                ", failureMsg='" + failureMsg + '\'' +
                ", metadata=" + metadata +
                ", charge='" + charge + '\'' +
                ']';
    }
}
