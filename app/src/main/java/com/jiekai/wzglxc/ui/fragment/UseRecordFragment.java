package com.jiekai.wzglxc.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.config.IntentFlag;
import com.jiekai.wzglxc.test.NFCBaseActivity;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;
import com.jiekai.wzglxc.ui.fragment.base.MyNFCBaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by laowu on 2018/1/2.
 */

public class UseRecordFragment extends MyNFCBaseFragment implements View.OnClickListener{
    @BindView(R.id.device_id)
    TextView deviceId;
    @BindView(R.id.read_card)
    TextView readCard;
    @BindView(R.id.out_image)
    ImageView outImage;
    @BindView(R.id.choose_picture)
    TextView choosePicture;
    @BindView(R.id.duihao)
    EditText duihao;
    @BindView(R.id.jinghao)
    EditText jinghao;
    @BindView(R.id.commit)
    TextView commit;

    private String title;
    private boolean enableNfc = false;
    private AlertDialog alertDialog;

    public static UseRecordFragment getInstance(String title) {
        UseRecordFragment useRecordFragment = new UseRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentFlag.TITLE, title);
        useRecordFragment.setArguments(bundle);
        return useRecordFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(IntentFlag.TITLE);
        }
        View view = inflater.inflate(R.layout.fragment_use_record, container, false);
        init();
        return view;
    }

    /**
     * 获取到nfc的信息
     *
     * @param nfcString
     */
    public void getNfcData(String nfcString) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (enableNfc) {
            Toast.makeText(getActivity(), "--"+nfcString, Toast.LENGTH_SHORT).show();
        }
        enableNfc = false;
    }

    private void init() {
        readCard.setOnClickListener(this);

        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("")
                .setMessage(getResources().getString(R.string.please_nfc))
                .create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_card:
                ((NFCBaseActivity) getActivity()).nfcEnable = true;
                enableNfc = true;
                alertDialog.show();
                break;
        }
    }
}