package com.jiekai.wzglxc.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.config.IntentFlag;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.DevicelogEntity;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;
import com.jiekai.wzglxc.utils.CommonUtils;
import com.jiekai.wzglxc.utils.dbutils.DBManager;

import butterknife.BindView;

/**
 * Created by LaoWu on 2018/1/5.
 * 未什么通过的申请的详情页
 */

public class RecordHistoryDetailActivity extends MyBaseActivity implements View.OnClickListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.record_type)
    TextView recordType;
    @BindView(R.id.device_id)
    TextView deviceId;
    @BindView(R.id.read_card)
    TextView readCard;
    @BindView(R.id.record_image)
    ImageView recordImage;
    @BindView(R.id.choose_picture)
    TextView choosePicture;
    @BindView(R.id.duihao)
    EditText duihao;
    @BindView(R.id.jinghao)
    EditText jinghao;
    @BindView(R.id.check_remark)
    TextView checkRemark;
    @BindView(R.id.recommit)
    TextView recommit;

    private DevicelogEntity currentDatas;

    @Override
    public void initView() {
        setContentView(R.layout.activity_record_history_detail);
    }

    @Override
    public void initData() {
        title.setText(getResources().getString(R.string.record_failed_detail));
        back.setOnClickListener(this);

        currentDatas = (DevicelogEntity) getIntent().getSerializableExtra(IntentFlag.DATA);
    }

    @Override
    public void initOperation() {
        if (currentDatas != null) {
            recordType.setText(CommonUtils.getDataIfNull(currentDatas.getJLZLMC()));
            deviceId.setText(CommonUtils.getDataIfNull(currentDatas.getSBBH()));
            duihao.setText(CommonUtils.getDataIfNull(currentDatas.getDH()));
            jinghao.setText(CommonUtils.getDataIfNull(currentDatas.getJH()));
            checkRemark.setText(CommonUtils.getDataIfNull(currentDatas.getSHBZ()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private void showCommitImage() {
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.)
    }
}
