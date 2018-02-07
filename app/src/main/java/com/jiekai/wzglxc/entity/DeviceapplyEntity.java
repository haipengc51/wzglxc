package com.jiekai.wzglxc.entity;

import com.jiekai.wzglxc.entity.base.BaseEntity;

import java.sql.Date;

/**
 * Created by laowu on 2017/12/18.
 */

public class DeviceapplyEntity extends BaseEntity {
    private int SQID;
    private String SYDH;
    private String SYJH;
    private String SQR;
    private Date SQSJ;
    private Date SHSJ;
    private String SHR;
    private String SQBZ;
    private String SHBZ;
    private String SHYJ;

    public int getSQID() {
        return SQID;
    }

    public void setSQID(int SQID) {
        this.SQID = SQID;
    }

    public String getSYDH() {
        return SYDH;
    }

    public void setSYDH(String SYDH) {
        this.SYDH = SYDH;
    }

    public String getSYJH() {
        return SYJH;
    }

    public void setSYJH(String SYJH) {
        this.SYJH = SYJH;
    }

    public String getSQR() {
        return SQR;
    }

    public void setSQR(String SQR) {
        this.SQR = SQR;
    }

    public Date getSQSJ() {
        return SQSJ;
    }

    public void setSQSJ(Date SQSJ) {
        this.SQSJ = SQSJ;
    }

    public Date getSHSJ() {
        return SHSJ;
    }

    public void setSHSJ(Date SHSJ) {
        this.SHSJ = SHSJ;
    }

    public String getSHR() {
        return SHR;
    }

    public void setSHR(String SHR) {
        this.SHR = SHR;
    }

    public String getSQBZ() {
        return SQBZ;
    }

    public void setSQBZ(String SQBZ) {
        this.SQBZ = SQBZ;
    }

    public String getSHBZ() {
        return SHBZ;
    }

    public void setSHBZ(String SHBZ) {
        this.SHBZ = SHBZ;
    }

    public String getSHYJ() {
        return SHYJ;
    }

    public void setSHYJ(String SHYJ) {
        this.SHYJ = SHYJ;
    }
}
