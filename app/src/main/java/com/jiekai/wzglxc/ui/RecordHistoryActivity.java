package com.jiekai.wzglxc.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.RecordHistoryAdapter;
import com.jiekai.wzglxc.config.Config;
import com.jiekai.wzglxc.config.IntentFlag;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.DeviceUnCheckEntity;
import com.jiekai.wzglxc.entity.DevicelogEntity;
import com.jiekai.wzglxc.entity.DevicemoveEntity;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;
import com.jiekai.wzglxc.utils.dbutils.DBManager;
import com.jiekai.wzglxc.utils.dbutils.DbCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by LaoWu on 2018/1/5.
 * 现场申请--申请记录
 */

public class RecordHistoryActivity extends MyBaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener{
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.list_view)
    ListView listView;

    private View headerView;
    private RecordHistoryAdapter adapter;
    private List<DeviceUnCheckEntity> dataList = new ArrayList<>();

    @Override
    public void initView() {
        setContentView(R.layout.activity_record_history);
    }

    @Override
    public void initData() {
        title.setText(getResources().getString(R.string.record_check_result));
        back.setOnClickListener(this);
    }

    @Override
    public void initOperation() {
        if (adapter == null) {
            headerView = LayoutInflater.from(mActivity).inflate(R.layout.adapter_record_history, null);
            listView.addHeaderView(headerView);
            adapter = new RecordHistoryAdapter(mActivity, dataList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
        setHeaderViewVisible(View.GONE);
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DevicelogEntity entity = (DevicelogEntity) parent.getItemAtPosition(position);
        if (entity != null) {
            Intent intent = new Intent(mActivity, RecordHistoryDetailActivity.class);
            intent.putExtra(IntentFlag.DATA, entity);
            startActivity(intent);
        }
    }

    private void getData() {
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.GET_RECORD_CHECK_LIST)
                .params(new String[]{userData.getUSERID()})
                .clazz(DevicelogEntity.class)
                .execut(new DbCallBack() {
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
                        if (result != null && result.size() != 0) {
                            dataList.clear();
                            for (int i=0; i<result.size(); i++) {
                                DevicelogEntity devicelogEntity = (DevicelogEntity) result.get(i);
                                DeviceUnCheckEntity deviceUnCheckEntity = new DeviceUnCheckEntity();
                                deviceUnCheckEntity.setType(Config.TYPE_JL);
                                deviceUnCheckEntity.setID(devicelogEntity.getSBBH());
                                deviceUnCheckEntity.setJLZL(devicelogEntity.getJLZLMC());
                                deviceUnCheckEntity.setYJ(devicelogEntity.getSHYJ());
                                deviceUnCheckEntity.setData(devicelogEntity);
                                dataList.add(deviceUnCheckEntity);
                            }
                            adapter.notifyDataSetChanged();
                            setHeaderViewVisible(View.VISIBLE);
                        } else {
                            alert(R.string.your_all_check_pass);
                            setHeaderViewVisible(View.GONE);
                        }
                        dismissProgressDialog();
                    }
                });
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.GET_MOVE_CHECK_LIST)
                .params(new String[]{userData.getUSERID()})
                .clazz(DevicemoveEntity.class)
                .execut(new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {

                    }

                    @Override
                    public void onResponse(List result) {
                        if (result != null && result.size() != 0) {
                            dataList.clear();
                            for (int i=0; i<result.size(); i++) {
                                DevicemoveEntity devicemoveEntity = (DevicemoveEntity) result.get(i);
                                DeviceUnCheckEntity deviceUnCheckEntity = new DeviceUnCheckEntity();
                                deviceUnCheckEntity.setType(Config.TYPE_MOVE);
                                deviceUnCheckEntity.setID(devicemoveEntity.getSBBH());
                                deviceUnCheckEntity.setJLZL("设备转场");
                                deviceUnCheckEntity.setYJ(devicemoveEntity.getSHYJ());
                                deviceUnCheckEntity.setData(devicemoveEntity);
                                dataList.add(deviceUnCheckEntity);
                            }
                            adapter.notifyDataSetChanged();
                            setHeaderViewVisible(View.VISIBLE);
                        } else {
                            alert(R.string.your_all_check_pass);
                            setHeaderViewVisible(View.GONE);
                        }
                    }
                });
    }

    private void setHeaderViewVisible(int visible) {
        if (headerView != null) {
            headerView.setVisibility(visible);
        }
    }
}
