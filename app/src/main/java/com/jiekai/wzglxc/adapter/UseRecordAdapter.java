package com.jiekai.wzglxc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jiekai.wzglxc.entity.RecordFragmentEntity;
import com.jiekai.wzglxc.ui.fragment.UseRecordFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LaoWu on 2018/1/3.
 * 设备使用记录的viewpager适配器
 */

public class UseRecordAdapter extends FragmentPagerAdapter {
    private List dataList;
    private HashMap<String, Fragment> fragmentList = new HashMap<>();

    public UseRecordAdapter(FragmentManager fm, List dataList) {
        super(fm);
        this.dataList = dataList;
    }

    @Override
    public Fragment getItem(int position) {
        UseRecordFragment useRecordFragment;
        RecordFragmentEntity entity = (RecordFragmentEntity) dataList.get(position);
        String key = entity.getTitle();
        String bh = entity.getSBBH();
        if (fragmentList.containsKey(key)) {
            useRecordFragment = (UseRecordFragment) fragmentList.get(key);
        } else {
            useRecordFragment = UseRecordFragment.getInstance(key, bh);
            fragmentList.put(key, useRecordFragment);
        }
        return useRecordFragment;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void getNfcData(String nfcString) {
        for (String key : fragmentList.keySet()) {
            ((UseRecordFragment) fragmentList.get(key)).getNfcData(nfcString);
        }
    }
}
