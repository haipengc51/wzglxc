package com.jiekai.wzglxc.entity;

import com.jiekai.wzglxc.entity.base.BaseEntity;

/**
 * Created by laowu on 2018/1/4.
 * 设备标题的实体类
 */

public class RecordFragmentEntity extends BaseEntity {
    private String title;
    private String SBBH;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSBBH() {
        return SBBH;
    }

    public void setSBBH(String SBBH) {
        this.SBBH = SBBH;
    }
}
