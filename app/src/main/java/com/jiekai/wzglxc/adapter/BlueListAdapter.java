package com.jiekai.wzglxc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bth.api.cls.CommBlueDev;
import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.adapter.base.BaseRecycleViewAdapter;
import com.jiekai.wzglxc.adapter.base.MyBaseAdapter;
import com.jiekai.wzglxc.adapter.base.ViewHolderHelper;

import java.util.List;

/**
 * Created by laowu on 2018/5/6.
 */

public class BlueListAdapter extends BaseRecycleViewAdapter {
    private int selection = -1;

    public BlueListAdapter(Context context, List dataList) {
        super(context, dataList);
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
        CommBlueDev item = (CommBlueDev) dataList.get(position);
        myViewHolder.blueName.setText(item.getName());
        if (position == selection) {
            myViewHolder.blueName.setBackgroundColor(context.getResources().getColor(R.color.main_color));
            myViewHolder.blueName.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.blueName.setBackgroundColor(context.getResources().getColor(R.color.white));
            myViewHolder.blueName.setTextColor(context.getResources().getColor(R.color.text_content_color));
        }
        myViewHolder.blueName.setOnClickListener(new View.OnClickListener() {
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
        private TextView blueName;

        public MyViewHolder(View itemView) {
            super(itemView);
            blueName = (TextView) itemView.findViewById(R.id.tv_blue_name);
        }
    }
}
