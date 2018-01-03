package com.jiekai.wzglxc.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.ui.fragment.base.MyBaseFragment;

/**
 * Created by laowu on 2018/1/2.
 */

public class UseRecordFragment extends MyBaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_use_record, container, false);
    }
}
