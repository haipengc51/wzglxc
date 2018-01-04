package com.jiekai.wzglxc.entity;

import com.jiekai.wzglxc.entity.base.BaseEntity;

/**
 * Created by laowu on 2018/1/3.
 */

public class DevicelogsortEntity extends BaseEntity {
    private String JLZLBH;  //记录种类
    private String LBBH;    //设备类型编号
    private String JLZLMC;  //记录种类名称
    private String PXXH;    //排序序号
    private String BH;      //设备自编号

    public String getJLZLBH() {
        return JLZLBH;
    }

    public void setJLZLBH(String JLZLBH) {
        this.JLZLBH = JLZLBH;
    }

    public String getLBBH() {
        return LBBH;
    }

    public void setLBBH(String LBBH) {
        this.LBBH = LBBH;
    }

    public String getJLZLMC() {
        return JLZLMC;
    }

    public void setJLZLMC(String JLZLMC) {
        this.JLZLMC = JLZLMC;
    }

    public String getPXXH() {
        return PXXH;
    }

    public void setPXXH(String PXXH) {
        this.PXXH = PXXH;
    }

    public String getBH() {
        return BH;
    }

    public void setBH(String BH) {
        this.BH = BH;
    }
}
