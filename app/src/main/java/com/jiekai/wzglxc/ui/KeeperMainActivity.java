package com.jiekai.wzglxc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jiekai.wzglxc.AppContext;
import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.KeeperAdapter;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.KeeperEntity;
import com.jiekai.wzglxc.entity.UserRoleEntity;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;
import com.jiekai.wzglxc.ui.update.UpdateManager;
import com.jiekai.wzglxc.utils.dbutils.DBManager;
import com.jiekai.wzglxc.utils.dbutils.DbCallBack;
import com.jiekai.wzglxc.utils.dbutils.DbDeal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by laowu on 2017/12/7.
 * 库管的主界面
 */

public class KeeperMainActivity extends MyBaseActivity implements AdapterView.OnItemClickListener {
    private static final int HANDLER_CHENGE_UPDATE = 0;
    private final static int LOGOUT = 0;
    @BindView(R.id.grid_view)
    GridView gridView;

    private long mBackPressedTime = 0;

    private List<KeeperEntity> dataList = new ArrayList<KeeperEntity>();
    private KeeperAdapter adapter;

    private DbDeal dbDeal = null;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_CHENGE_UPDATE:     //延时检测是否有更新
                    UpdateManager updateManager = new UpdateManager(KeeperMainActivity.this);
                    updateManager.getRemoteVersion();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mHandler.sendEmptyMessageDelayed(HANDLER_CHENGE_UPDATE, 1000 * 2);
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_keeper_main);
    }

    @Override
    public void initData() {
//        dataList.add(new KeeperEntity(getResources().getString(R.string.device_bind), BindDeviceActivity_new.class));
//        dataList.add(new KeeperEntity(getResources().getString(R.string.device_output), DeviceOutputActivity.class));
//        dataList.add(new KeeperEntity(getResources().getString(R.string.device_out_history), DeviceOutPutHistoryActivity.class));
//        dataList.add(new KeeperEntity(getResources().getString(R.string.device_input), DeviceInputActivity.class));
//        dataList.add(new KeeperEntity(getResources().getString(R.string.device_repair), DeviceRepairActivity.class));
//        dataList.add(new KeeperEntity(getResources().getString(R.string.pan_ku), PanKuActivity.class));
        dataList.add(new KeeperEntity(getResources().getString(R.string.device_detail), DeviceDetailSimpleActivity.class));
        dataList.add(new KeeperEntity(getResources().getString(R.string.device_use_record), DeviceUseRecordActivity.class));
        dataList.add(new KeeperEntity(getResources().getString(R.string.device_scrap), DeviceScrapActivity.class));
        dataList.add(new KeeperEntity(getResources().getString(R.string.device_move), DeviceMoveActivity.class));
        dataList.add(new KeeperEntity(getResources().getString(R.string.device_inspection), DeviceInspectionActivity.class));
        dataList.add(new KeeperEntity(getResources().getString(R.string.device_applay), DeviceApplayActivity.class));
        dataList.add(new KeeperEntity(getResources().getString(R.string.record_check_result), RecordHistoryActivity.class));
        dataList.add(new KeeperEntity(getResources().getString(R.string.logout), LogOutActivity.class));
    }

    @Override
    public void initOperation() {
        if (adapter == null) {
            adapter = new KeeperAdapter(KeeperMainActivity.this, dataList);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(this);
        }
        AppContext.getUnCheckedData(mContext, userData.getUSERID());
    }

    @Override
    public void cancleDbDeal() {
        if (dbDeal != null) {
            dbDeal.cancleDbDeal();
            dismissProgressDialog();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final KeeperEntity keeperEntity = (KeeperEntity) parent.getItemAtPosition(position);
        if (keeperEntity != null) {
            if (keeperEntity.getName().equals(getResources().getString(R.string.logout))) {
                isAnimation = false;
                startActivityForResult(new Intent(mActivity, keeperEntity.getActivity()), LOGOUT);
            } else {
                isAnimation = true;
                if (keeperEntity.getActivity() == DeviceInspectionActivity.class) {
                    dbDeal = DBManager.dbDeal(DBManager.SELECT);
                            dbDeal.sql(SqlUrl.LoginRule)
                            .params(new String[]{userData.getUSERID()})
                            .clazz(UserRoleEntity.class)
                            .execut(mContext, new DbCallBack() {

                                @Override
                                public void onDbStart() {
                                    showProgressDialog(getResources().getString(R.string.loading_data));
                                }

                                @Override
                                public void onError(String err) {
                                    alert(err);
                                    dismissProgressDialog();
                                }

                                @Override
                                public void onResponse(List result) {
                                    dismissProgressDialog();
                                    if (result != null && result.size() != 0) {
                                        UserRoleEntity userRoleEntity;
                                        for (int i=0; i<result.size(); i++) {
                                             userRoleEntity = (UserRoleEntity) result.get(i);
                                            if ("008".equals(userRoleEntity.getROLEID())) {
                                                startActivity(new Intent(KeeperMainActivity.this, keeperEntity.getActivity()));
                                                return;
                                            }
                                        }
                                        alert(R.string.no_xunjian_permission);
                                    } else {
                                        alert(R.string.no_xunjian_permission);
                                    }
                                }
                            });
                } else {
                    startActivity(new Intent(KeeperMainActivity.this, keeperEntity.getActivity()));
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                isAnimation = false;
                finish();
            } else {
                mBackPressedTime = curTime;
                alert(R.string.click_again_finish);
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGOUT && resultCode == RESULT_OK) {
            startActivity(new Intent(mActivity, LoginActivity.class));
            finish();
        }
    }
}
