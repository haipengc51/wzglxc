package com.jiekai.wzglxc.ui;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bth.api.cls.BlueTooth4_C;
import com.bth.api.cls.CommBlueDev;
import com.bth.api.cls.Comm_Bluetooth;
import com.jiekai.wzglxc.AppContext;
import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.BlueListAdapter;
import com.jiekai.wzglxc.entity.BlueToothEntity;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;
import com.silionmodule.HardWareDetector;
import com.silionmodule.ParamNames;
import com.silionmodule.Reader;
import com.silionmodule.ReaderType;

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
    private static final int TOAST = 0;

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

    private MyHandler myHandler = new MyHandler();
    private BlueListAdapter adapter;
    private Map<String, CommBlueDev> blueDevice = new HashMap<>();
    private List<BlueToothEntity> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appContext = (AppContext) getApplication();
        if (appContext.CommBth == null) {
            appContext.CommBth = new Comm_Bluetooth(this);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_blue_tooth);
    }

    @Override
    public void initData() {
        title.setText(R.string.bule_connect);

        back.setOnClickListener(this);
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
        if (appContext.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {
            if (appContext.connectedBlue != null &&
                    appContext.connectedBlue.getAddress().equals(appContext.CommBth.GetConnectAddr())) {
                dataList.clear();
                blueDevice.clear();
                blueDevice.put(appContext.connectedBlue.getAddress(), appContext.connectedBlue);
                dataList.add(new BlueToothEntity(appContext.connectedBlue.getName(),
                        appContext.connectedBlue.getAddress(), appContext.connectedBlue));
                adapter.setSelection(0);
                adapter.notifyDataSetChanged();
                tvBlueConnect.setText(getResources().getString(R.string.connected));
            }
        }
    }

    @Override
    public void cancleDbDeal() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
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
                appContext.CommBth.StopSearch();
                return appContext.CommBth.StartSearch(finalSelectoption, searchCallback);
            }

            @Override
            protected void onPostExecute(Object o) {
                int result = (int) o;
                if (result == 0) {
                    Toast.makeText(BlueToothActivity.this, "正在搜索蓝牙设备...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BlueToothActivity.this, "搜索蓝牙失败：" + result, Toast.LENGTH_SHORT).show();
                    appContext.CommBth.ResetBlueTooth();
                }
            }
        };
        asyncTask.execute();
    }

    private class MyHandler extends android.os.Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TOAST:
                    alert((String) msg.obj);
                    if (msg.arg1 == 1) {
                        finish();
                    }
                    break;
            }
        }
    }

    private void resetSearch() {
        if (appContext.CommBth != null) {
//            appContext.CommBth.StopSearch();
            appContext.CommBth.ResetBlueTooth();
        }
        btSearchBlue.setEnabled(true);
        tvBlueConnect.setEnabled(true);
        tvBlueBreak.setEnabled(true);
        tvBlueConnect.setText(R.string.connect);
        adapter.setSelection(-1);
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
        final CommBlueDev dev = dataList.get(currentPosition).getData();

        appContext.CommBth.StopSearch();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                if (dev.getType() == 1) {
                    if (appContext.CommBth.ToMatch(dev.getAddress()) == BluetoothDevice.BOND_BONDED) {
                        int re = appContext.CommBth.Connect(dev.getAddress(), 2);
                        if (re == 0) {
                            ConnectEvent();
                        } else {
                            Message message = new Message();
                            message.what = TOAST;
                            message.obj = getResources().getString(R.string.connect_blue_faild);
                            myHandler.sendMessage(message);
                        }

                    } else {
                        Message message = new Message();
                        message.what = TOAST;
                        message.obj = getResources().getString(R.string.match_blue_faild);
                        myHandler.sendMessage(message);
                    }
                } else {
                    int re = -1;
                    if (appContext.CommBth.ConnectState() != Comm_Bluetooth.CONNECTED
                            && appContext.CommBth.GetConnectAddr() != dev.getAddress()) {
                        re = appContext.CommBth.Connect(dev.getAddress(), 4);
                    } else {
                        re = 0;
                    }
                    if (re == 0) {
                        String Addr = "", Uuid = "", Uuid_read = "", Uuid_write = "", Uuid_pass = "", Val_pass = "";
                        boolean needpwd = false;
                        Uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
                        Uuid_read = "0000ffe1-0000-1000-8000-00805f9b34fb";
                        Uuid_write = "0000ffe1-0000-1000-8000-00805f9b34fb";

                        List<BlueTooth4_C.BLEServices> lbe = appContext.CommBth
                                .FindServices(6000);
                        if (lbe != null && lbe.size() > 0) {
                            appContext.CommBth.SetServiceUUIDs(Uuid,
                                    Uuid_read, Uuid_write);

                            Message message = new Message();
                            message.what = TOAST;
                            message.obj = getResources().getString(R.string.Constr_connectblueokthentoreader);
                            myHandler.sendMessage(message);

                            appContext.BackResult = 1;
                            ConnectEvent();
                            return;
                        } else {
                            appContext.CommBth.DisConnect();
                            Message message = new Message();
                            message.what = TOAST;
                            message.obj = getResources().getString(R.string.connect_blue_service_failed);
                            myHandler.sendMessage(message);
                            return;
                        }
                    } else {
                        Message message = new Message();
                        message.what = TOAST;
                        message.obj = appContext.Constr_connectbluesetfail + String.valueOf(re);
                        myHandler.sendMessage(message);
                    }
                }
//            }
//        }).start();
    }

    private void disConnectBlue() {
        appContext.StopHandle();

        appContext.CommBth.StopSearch();
        if (appContext.Mreader != null) {
            appContext.Mreader.DisConnect();
//                    appContext.CommBth.DisConnect();
        }

        this.btSearchBlue.setEnabled(true);
        tvBlueBreak.setEnabled(false);
        tvBlueConnect.setEnabled(true);
        tvBlueConnect.setText(R.string.connect);
        adapter.setSelection(-1);
    }

    private Comm_Bluetooth.SearchCallback searchCallback = new Comm_Bluetooth.SearchCallback() {
        @Override
        public void OnSearch(CommBlueDev commBlueDev) {
            if (!blueDevice.containsKey(commBlueDev.getAddress())) {
                blueDevice.put(commBlueDev.getAddress(), commBlueDev);
                dataList.add(new BlueToothEntity(commBlueDev.getName(),
                        commBlueDev.getAddress(), commBlueDev));
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void ConnectEvent() {
        boolean isok = false;
        if (appContext.CommBth.getRemoveType() == 4) {
            isok = appContext.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED
                    && appContext.CommBth.IssetUUID() == true
                    && appContext.BackResult == 1;
        } else {
            isok = true;
        }
        if (isok) {
            try {
                appContext.Mode = 1;
                appContext.CommBth.Comm_SetParam(ParamNames.Communication_mode,
                        appContext.Mode);

                appContext.CommBth.SetFrameParams(20, 100);
                appContext.CommBth.Comm_SetParam(
                        ParamNames.Communication_module,
                        HardWareDetector.Module_Type.MODOULE_SLR5100);

                int antc = 1;
//                appContext.Rparams.antportc = antc;
                appContext.Mreader = Reader.Create(ReaderType.AntTypeE.valueOf(antc),
                        appContext.CommBth);
//                appContext.Mreader.paramSet(ParamNames.InitMode_READ_BANK,"2");
                ConnectHandleUI();

                Message message = new Message();
                message.what = TOAST;
                message.obj = AppContext.Constr_connectbluesetok;
                message.arg1 = 1;
                myHandler.sendMessage(message);
            } catch (Exception ex) {
                appContext.CommBth.DisConnect();
                Message message = new Message();
                message.what = TOAST;
                message.obj = AppContext.Constr_createreaderok;
                myHandler.sendMessage(message);
            }

        } else {
            appContext.CommBth.DisConnect();
            Message message = new Message();
            message.what = TOAST;
            message.obj = AppContext.Constr_connectblueserfail;
            myHandler.sendMessage(message);
        }
    }

    private void ConnectHandleUI() {
        btSearchBlue.setEnabled(false);
        this.tvBlueConnect.setEnabled(false);
        this.tvBlueBreak.setEnabled(true);
        if(appContext.CommBth.UUID_SERVICE.equals("0000fea0-0000-1000-8000-00805f9b34fb")) {
            appContext.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
                    "0000fea3-0000-1000-8000-00805f9b34fb", false);
            appContext.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
                    "0000fea4-0000-1000-8000-00805f9b34fb", false);
            appContext.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
                    "0000fea5-0000-1000-8000-00805f9b34fb", false);
            appContext.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
                    "0000fea6-0000-1000-8000-00805f9b34fb", false);
            appContext.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
                    "0000fea7-0000-1000-8000-00805f9b34fb", false);
        }

        appContext.connectok=true;
        appContext.readNfcData();

        int currentPosition = adapter.getCurrentPosition();
        if (currentPosition == -1) {
            Message message = new Message();
            message.what = TOAST;
            message.obj = getResources().getString(R.string.please_choose_blue);
            myHandler.sendMessage(message);
            return;
        }
        appContext.connectedBlue = dataList.get(currentPosition).getData();
        tvBlueConnect.setText(getResources().getString(R.string.connected));
    }

    @Override
    protected void onPause() {
        super.onPause();
        appContext.CommBth.StopSearch();
    }
}
