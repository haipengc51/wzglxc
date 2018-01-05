package com.jiekai.wzglxc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.base.MyBaseAdapter;
import com.jiekai.wzglxc.entity.DevicelogEntity;

import java.util.List;

import static com.jiekai.wzglxc.utils.CommonUtils.getDataIfNull;

/**
 * Created by LaoWu on 2018/1/5.
 */

public class RecordHistoryAdapter extends MyBaseAdapter {

    public RecordHistoryAdapter(Context context, List dataList) {
        super(context, dataList);
    }

    @Override
    public View createCellView(ViewGroup parent) {
        return mInflater.inflate(R.layout.adapter_record_history, parent, false);
    }

    @Override
    public BusinessHolder createCellHolder(View cellView) {
        return new MyViewHolder(cellView);
    }

    @Override
    public View buildData(int position, View cellView, BusinessHolder viewHolder) {
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        DevicelogEntity item = (DevicelogEntity) dataList.get(position);
        myViewHolder.recordType.setText(getDataIfNull(item.getJLZLMC()));
        myViewHolder.deviceId.setText(getDataIfNull(item.getSBBH()));
        myViewHolder.duihao.setText(getDataIfNull(item.getDH()));
        myViewHolder.jinghao.setText(getDataIfNull(item.getJH()));
        String shyj = item.getSHYJ();
        if ("1".equals(shyj)) {
            myViewHolder.checkResult.setText("通过");
        } else if ("0".equals(shyj)) {
            myViewHolder.checkResult.setText("未通过");
        } else {
            myViewHolder.checkResult.setText("待审核");
        }
        return null;
    }

    private class MyViewHolder extends BusinessHolder {
        private TextView recordType;
        private TextView deviceId;
        private TextView duihao;
        private TextView jinghao;
        private TextView checkResult;

        public MyViewHolder(View view) {
            recordType = (TextView) view.findViewById(R.id.record_type);
            deviceId = (TextView) view.findViewById(R.id.device_id);
            duihao = (TextView) view.findViewById(R.id.duihao);
            jinghao = (TextView) view.findViewById(R.id.jinghao);
            checkResult = (TextView) view.findViewById(R.id.check_result);
        }
    }
}
