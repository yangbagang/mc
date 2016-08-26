package com.ybg.rp.vmbase.bean;

import java.io.Serializable;

/**
 * Created by yangbagang on 16/8/25.
 */
public class TypeOne implements Serializable{

    private static final long serialVersionUID = -3351378689851579818L;

    private Long id;

    private String name;

    private Short status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TypeOne{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
