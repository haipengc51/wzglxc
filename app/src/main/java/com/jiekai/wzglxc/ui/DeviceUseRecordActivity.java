package com.jiekai.wzglxc.ui;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.TitleAdapter;
import com.jiekai.wzglxc.adapter.UseRecordAdapter;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by laowu on 2018/1/2.
 * 设备使用记录界面（安装记录，回收记录，转井记录等）
 */

public class DeviceUseRecordActivity extends MyBaseActivity implements View.OnClickListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.title_navi)
    RecyclerView titleNavi;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private String[] titleStr = new String[]{"1", "2", "3", "4"};
    private List<String> titleList = new ArrayList<>();

    private UseRecordAdapter viewPagerAdapter;
    private TitleAdapter titleAdapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_device_use_record);
    }

    @Override
    public void initData() {
        back.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.device_use_record));

        back.setOnClickListener(this);
    }

    @Override
    public void initOperation() {
        for (int i=0; i<titleStr.length; i++) {
            titleList.add(titleStr[i]);
        }
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private void init() {
        if (titleAdapter == null) {
            titleAdapter = new TitleAdapter(mActivity, titleList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            titleNavi.setLayoutManager(linearLayoutManager);
            titleNavi.setAdapter(titleAdapter);
        }
        if (viewPagerAdapter == null) {
            viewPagerAdapter = new UseRecordAdapter(getSupportFragmentManager(), titleList);
            viewPager.setAdapter(viewPagerAdapter);
        }
    }
}
