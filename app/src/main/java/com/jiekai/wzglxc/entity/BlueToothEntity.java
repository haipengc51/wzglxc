package com.jiekai.wzglxc.entity;

import com.bth.api.cls.CommBlueDev;
import com.jiekai.wzglxc.entity.base.BaseEntity;

/**
 * Created by laowu on 2018/5/8.
 */

public class BlueToothEntity extends BaseEntity {
    private String name;
    private String address;
    private CommBlueDev data;

    public BlueToothEntity(String name, String addr, CommBlueDev data) {
        this.name = name;
        this.address = addr;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CommBlueDev getData() {
        return data;
    }

    public void setData(CommBlueDev data) {
        this.data = data;
    }
}
