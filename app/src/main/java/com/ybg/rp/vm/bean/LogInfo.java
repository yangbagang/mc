package com.ybg.rp.vm.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by yangbagang on 16/8/20.
 */
@Table(name = "log_info")
public class LogInfo implements Serializable {

    private static final long serialVersionUID = -8407351769906705708L;

    @Column(name = "id", isId = true)
    private long id;

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "operator_id")
    private long operatorId;

    @Column(name = "create_date")
    private String createDate;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LogInfo{" +
                "id=" + id +
                ", operatorName='" + operatorName + '\'' +
                ", operatorId=" + operatorId +
                ", createDate='" + createDate + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}
