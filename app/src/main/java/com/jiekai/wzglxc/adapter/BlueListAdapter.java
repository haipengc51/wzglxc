package com.jiekai.wzglxc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bth.api.cls.CommBlueDev;
import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.base.BaseRecycleViewAdapter;
import com.jiekai.wzglxc.adapter.base.MyBaseAdapter;
import com.jiekai.wzglxc.adapter.base.ViewHolderHelper;
import com.jiekai.wzglxc.entity.BlueToothEntity;

import java.util.List;

/**
 * Created by laowu on 2018/5/6.
 */

public class BlueListAdapter extends BaseRecycleViewAdapter {
    private int selection = -1;
    private int whiteColor;
    private int mainColor;
    private int textColor;

    public BlueListAdapter(Context context, List dataList) {
        super(context, dataList);
        whiteColor = context.getResources().getColor(R.color.white);
        mainColor = context.getResources().getColor(R.color.main_color);
        textColor = context.getResources().getColor(R.color.text_content_color);
    }

    public void setSelection(int selection) {
        int oldSelection = this.selection;
        this.selection = selection;
        notifyItemChanged(oldSelection);
        notifyItemChanged(selection);
    }

    public int getCurrentPosition() {
        return selection;
    }

    @Override
    public void onMyBindView(final ViewHolderHelper holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        BlueToothEntity item = (BlueToothEntity) dataList.get(position);
        myViewHolder.blueName.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
        myViewHolder.blueAddr.setText(TextUtils.isEmpty(item.getAddress()) ? "" : item.getAddress());
        if (position == selection) {
            myViewHolder.blueLinear.setBackgroundColor(mainColor);
            myViewHolder.blueName.setTextColor(whiteColor);
            myViewHolder.blueAddr.setTextColor(whiteColor);
        } else {
            myViewHolder.blueLinear.setBackgroundColor(whiteColor);
            myViewHolder.blueName.setTextColor(textColor);
            myViewHolder.blueAddr.setTextColor(textColor);
        }
        myViewHolder.blueLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(position);
            }
        });
    }

    @Override
    public ViewHolderHelper onMyCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.adapter_blue_tooth, parent, false));
    }

    private class MyViewHolder extends ViewHolderHelper {
        private LinearLayout blueLinear;
        private TextView blueName;
        private TextView blueAddr;

        public MyViewHolder(View itemView) {
            super(itemView);
            blueLinear = (LinearLayout) itemView.findViewById(R.id.blue_linear);
            blueName = (TextView) itemView.findViewById(R.id.tv_blue_name);
            blueAddr = (TextView) itemView.findViewById(R.id.tv_blue_addr);
        }
    }
}
