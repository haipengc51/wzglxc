package com.jiekai.wzglxc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jiekai.wzglxc.ui.fragment.UseRecordFragment;

import java.util.List;

/**
 * Created by LaoWu on 2018/1/3.
 * 设备使用记录的viewpager适配器
 */

public class UseRecordAdapter extends FragmentPagerAdapter {
    private List dataList;

    public UseRecordAdapter(FragmentManager fm, List dataList) {
        super(fm);
        this.dataList = dataList;
    }

    @Override
    public Fragment getItem(int position) {
        return new UseRecordFragment();
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }
}
