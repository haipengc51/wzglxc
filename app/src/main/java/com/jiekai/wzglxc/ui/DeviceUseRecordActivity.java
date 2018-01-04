package com.jiekai.wzglxc.ui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiekai.wzglxc.AppContext;
import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.TitleAdapter;
import com.jiekai.wzglxc.adapter.UseRecordAdapter;
import com.jiekai.wzglxc.config.Constants;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.DevicelogsortEntity;
import com.jiekai.wzglxc.entity.PankuDataNumEntity;
import com.jiekai.wzglxc.entity.RecordFragmentEntity;
import com.jiekai.wzglxc.entity.RecordRecentIdEntity;
import com.jiekai.wzglxc.test.NFCBaseActivity;
import com.jiekai.wzglxc.ui.popup.DeviceCodePopup;
import com.jiekai.wzglxc.utils.StringUtils;
import com.jiekai.wzglxc.utils.dbutils.DBManager;
import com.jiekai.wzglxc.utils.dbutils.DbCallBack;
import com.jiekai.wzglxc.utils.localDbUtils.PanKuDataNumColumn;
import com.jiekai.wzglxc.utils.localDbUtils.RecordRecentIDColumn;
import com.jiekai.wzglxc.utils.zxing.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by laowu on 2018/1/2.
 * 设备使用记录界面（安装记录，回收记录，转井记录等）
 */

public class DeviceUseRecordActivity extends NFCBaseActivity implements View.OnClickListener,
        DeviceCodePopup.OnDeviceCodeClick {
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
    @BindView(R.id.read_card)
    TextView readCard;
    @BindView(R.id.sao_ma)
    TextView saoMa;
    @BindView(R.id.button_layout)
    LinearLayout buttonLayout;
    @BindView(R.id.record_view)
    LinearLayout recordView;
    @BindView(R.id.choose_used_text)
    TextView chooseUsedText;

    private List<RecordFragmentEntity> titleList = new ArrayList<>();

    private UseRecordAdapter viewPagerAdapter;
    private TitleAdapter titleAdapter;

    private AlertDialog alertDialog;
    private boolean enableNfc = false;

    private DeviceCodePopup recentID;

    @Override
    public void initView() {
        setContentView(R.layout.activity_device_use_record);
    }

    @Override
    public void initData() {
        back.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.device_use_record));

        back.setOnClickListener(this);
        readCard.setOnClickListener(this);
        saoMa.setOnClickListener(this);
        chooseUsedText.setOnClickListener(this);


        alertDialog = new AlertDialog.Builder(mActivity)
                .setTitle("")
                .setMessage(getResources().getString(R.string.please_nfc))
                .create();
    }

    @Override
    public void initOperation() {
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
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (titleAdapter != null) {
                    titleAdapter.heightlight(position);
                    titleNavi.scrollToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        titleAdapter.setOnItemClickLisener(new TitleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                viewPager.setCurrentItem(position);
            }
        });

        recentID = new DeviceCodePopup(this, chooseUsedText, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.read_card:
                nfcEnable = true;
                enableNfc = true;
                alertDialog.show();
                break;
            case R.id.sao_ma:
                startActivityForResult(new Intent(mActivity, CaptureActivity.class), Constants.SCAN);
                break;
            case R.id.choose_used_text:
                getRecentID();
                break;
        }
    }

    @Override
    public void getNfcData(String nfcString) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (viewPagerAdapter != null) {
            viewPagerAdapter.getNfcData(nfcString);
        }
        if (enableNfc) {
            getRecordList(nfcString);
        }
        nfcEnable = false;
        enableNfc = false;
    }

    /**
     * 通过id获取记录列表
     *
     * @param cardId
     */
    private void getRecordList(String cardId) {
        if (StringUtils.isEmpty(cardId)) {
            alert(R.string.get_id_err);
            return;
        }
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.Get_Record_List)
                .params(new String[]{cardId, cardId, cardId})
                .clazz(DevicelogsortEntity.class)
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
                            titleList.clear();
                            for (int i = 0; i < result.size(); i++) {
                                RecordFragmentEntity recordFragmentEntity = new RecordFragmentEntity();
                                DevicelogsortEntity entity = (DevicelogsortEntity) result.get(i);
                                recordFragmentEntity.setTitle(entity.getJLZLMC());
                                recordFragmentEntity.setSBBH(entity.getBH());
                                titleList.add(recordFragmentEntity);
                            }
                            titleAdapter.notifyDataSetChanged();
                            viewPagerAdapter.notifyDataSetChanged();
                            buttonLayout.setVisibility(View.GONE);
                            recordView.setVisibility(View.VISIBLE);
                            addRecentId(((DevicelogsortEntity)result.get(0)).getBH());
                        } else {
                            alert(R.string.no_data);
                        }
                        dismissProgressDialog();
                    }
                });
    }

    private void addRecentId(String bh) {
        String sql = "SELECT * FROM " + RecordRecentIDColumn.TABLE_NAME + " WHERE " +
                RecordRecentIDColumn.BH + " = ? ";
        List result = AppContext.dbHelper.selectAll(sql, RecordRecentIdEntity.class,
                new String[]{bh});
        if (result != null && result.size() != 0) {

        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RecordRecentIDColumn.BH, bh);
            AppContext.dbHelper.insertSql(RecordRecentIDColumn.TABLE_NAME, contentValues);
        }
    }

    private void getRecentID() {
        String sql = "SELECT * FROM " + RecordRecentIDColumn.TABLE_NAME
                + " order by " + RecordRecentIDColumn.ID + " desc limit 0,5";
        List<RecordRecentIdEntity> result = AppContext.dbHelper.selectAll(sql, RecordRecentIdEntity.class,
                null);
        if (result != null && result.size() != 0) {
            List<String> recentList = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                recentList.add(result.get(i).getBH());
            }
            recentID.setPopListData(recentList);
            recentID.showCenter(chooseUsedText);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SCAN && resultCode == RESULT_OK) {
            String code = data.getExtras().getString("result");
            getRecordList(code);
        }
    }

    @Override
    public void OnDeviceCodeClick(String deviceCode) {
        getListByBH(deviceCode);
    }

    private void getListByBH(String BH) {
        if (StringUtils.isEmpty(BH)) {
            alert(R.string.get_bh_faild);
            return;
        }
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.Get_Record_List_by_BH)
                .params(new String[]{BH})
                .clazz(DevicelogsortEntity.class)
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
                            titleList.clear();
                            for (int i = 0; i < result.size(); i++) {
                                RecordFragmentEntity recordFragmentEntity = new RecordFragmentEntity();
                                DevicelogsortEntity entity = (DevicelogsortEntity) result.get(i);
                                recordFragmentEntity.setTitle(entity.getJLZLMC());
                                recordFragmentEntity.setSBBH(entity.getBH());
                                titleList.add(recordFragmentEntity);
                            }
                            titleAdapter.notifyDataSetChanged();
                            viewPagerAdapter.notifyDataSetChanged();
                            buttonLayout.setVisibility(View.GONE);
                            recordView.setVisibility(View.VISIBLE);
                        } else {
                            alert(R.string.no_data);
                        }
                        dismissProgressDialog();
                    }
                });
    }
}
