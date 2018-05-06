package com.jiekai.wzglxc.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bth.api.cls.CommBlueDev;
import com.bth.api.cls.Comm_Bluetooth;
import com.jiekai.wzglxc.AppContext;
import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.BlueListAdapter;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by laowu on 2018/5/6.
 * 蓝牙连接Activity
 */

public class BlueToothActivity extends MyBaseActivity implements View.OnClickListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.bt_search_blue)
    TextView btSearchBlue;
    @BindView(R.id.bt_search_reset)
    TextView btSearchReset;
    @BindView(R.id.lv_blue_list)
    RecyclerView lvBlueList;
    @BindView(R.id.tv_blue_connect)
    TextView tvBlueConnect;
    @BindView(R.id.tv_blue_break)
    TextView tvBlueBreak;
    private AppContext appContext;

    private BlueListAdapter adapter;
    private Map<String, CommBlueDev> blueDevice = new HashMap<>();
    private List<CommBlueDev> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = (AppContext) getApplication();
        appContext.comm_bluetooth = new Comm_Bluetooth(this);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_blue_tooth);
    }

    @Override
    public void initData() {
        btSearchBlue.setOnClickListener(this);
        btSearchReset.setOnClickListener(this);
        tvBlueConnect.setOnClickListener(this);
        tvBlueBreak.setOnClickListener(this);
    }

    @Override
    public void initOperation() {
        if (adapter == null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            adapter = new BlueListAdapter(this, dataList);
            lvBlueList.setLayoutManager(linearLayoutManager);
            lvBlueList.setAdapter(adapter);
        }
    }

    @Override
    public void cancleDbDeal() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search_blue:
                searchBlueTooth();
                break;
            case R.id.bt_search_reset:
                resetSearch();
                break;
            case R.id.tv_blue_connect:
                connectBlue();
                break;
            case R.id.tv_blue_break:
                disConnectBlue();
                break;
        }
    }

    private void searchBlueTooth() {
        int selectoption = 0;
        selectoption |= Comm_Bluetooth.SearchOption.Blue
                .value();
        selectoption |= Comm_Bluetooth.SearchOption.BLE.value();

        final int finalSelectoption = selectoption;
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Object doInBackground(Object[] params) {
                return appContext.comm_bluetooth.StartSearch(finalSelectoption, searchCallback);
            }

            @Override
            protected void onPostExecute(Object o) {
                int result = (int) o;
                if (result == 0) {
                    Toast.makeText(BlueToothActivity.this, "正在搜索蓝牙设备...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BlueToothActivity.this, "搜索蓝牙失败：" + result, Toast.LENGTH_SHORT).show();
                    appContext.comm_bluetooth.ResetBlueTooth();
                }
            }
        };
        asyncTask.execute();
    }

    private void resetSearch() {
        if (appContext.comm_bluetooth != null) {
            appContext.comm_bluetooth.ResetBlueTooth();
        }
    }

    private void connectBlue() {
        if (dataList.size() == 0) {
            alert(R.string.search_blue_first);
            return;
        }
        int currentPosition = adapter.getCurrentPosition();
        if (currentPosition == -1) {
            alert(R.string.please_choose_blue);
            return;
        }
        CommBlueDev dev = dataList.get(currentPosition);

    }

    private void disConnectBlue() {

    }

    private Comm_Bluetooth.SearchCallback searchCallback = new Comm_Bluetooth.SearchCallback() {
        @Override
        public void OnSearch(CommBlueDev commBlueDev) {
            if (!blueDevice.containsKey(commBlueDev.getAddress())) {
                blueDevice.put(commBlueDev.getAddress(), commBlueDev);
                dataList.add(commBlueDev);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        appContext.comm_bluetooth.StopSearch();
    }
}
