package com.jiekai.wzglxc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.entity.RecordFragmentEntity;
import com.jiekai.wzglxc.utils.CommonUtils;

import java.util.List;

/**
 * Created by LaoWu on 2018/1/3.
 * 标题适配器
 */

public class TitleAdapter extends RecyclerView.Adapter {
    private float textSizeSelect;
    private float textSizeNormal;
    private List dataList;
    private Context context;
    private LayoutInflater mInflater;
    private int mPosition;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public TitleAdapter(Context context, List dataList) {
        this.context = context;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(context);
        textSizeSelect = 17;
        textSizeNormal = 15;
    }

    public void setOnItemClickLisener(OnItemClickListener onItemClickLisener) {
        this.onItemClickListener = onItemClickLisener;
    }


    public boolean isSelect(int position) {
        return mPosition == position;
    }

    public void heightlight(int position) {
        int a = 0;
        if (mPosition != -1) {
            a = mPosition;
        }
        mPosition = position;
        notifyItemChanged(a);
        notifyItemChanged(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_title_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        RecordFragmentEntity item = (RecordFragmentEntity) dataList.get(position);
        myViewHolder.title.setText(item.getTitle());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (position == 0) {
            params.setMargins((int) context.getResources().getDimension(R.dimen.marginTop10), 0, (int) context.getResources().getDimension(R.dimen.marginTop10), 0);
            myViewHolder.linear.setLayoutParams(params);
        }else {
            params.setMargins((int) context.getResources().getDimension(R.dimen.marginTop10), 0, (int) context.getResources().getDimension(R.dimen.marginTop15), 0);
            myViewHolder.linear.setLayoutParams(params);
        }
        if (isSelect(position)) {
            myViewHolder.title.setTextColor(context.getResources().getColor(R.color.main_color));
            myViewHolder.title.setTextSize(textSizeSelect);
            myViewHolder.linear.setBackgroundResource(R.color.main_color);
        } else {
            myViewHolder.title.setTextColor(context.getResources().getColor(R.color.text_content_color));
            myViewHolder.title.setTextSize(textSizeNormal);
            myViewHolder.linear.setBackgroundResource(R.color.transparency);
        }
        if (onItemClickListener != null) {
            myViewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = myViewHolder.getLayoutPosition();
                    onItemClickListener.onItemClick(myViewHolder.title, pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private LinearLayout linear;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
        }
    }
}
