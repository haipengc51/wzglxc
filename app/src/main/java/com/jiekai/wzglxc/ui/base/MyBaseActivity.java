package com.jiekai.wzglxc.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.config.ShareConstants;
import com.jiekai.wzglxc.entity.UserInfoEntity;
import com.jiekai.wzglxc.utils.AnimationUtils;
import com.jiekai.wzglxc.utils.JSONHelper;
import com.jiekai.wzglxc.utils.NetWorkUtils;
import com.jiekai.wzglxc.utils.StringUtils;

import butterknife.ButterKnife;

/**
 * Created by laowu on 2017/12/3.
 * Activity的基类
 */

public abstract class MyBaseActivity extends AppCompatActivity {
    public abstract void initView();
    public abstract void initData();
    public abstract void initOperation();
    public abstract void cancleDbDeal();

    public boolean isAnimation = true;
    private SharedPreferences sharedPreferences;
    public UserInfoEntity userData;
    public boolean isLogin = false;
    public Activity mActivity;
    protected Context mContext;

    private ProgressDialog progressDialog = null;
    private MyBroadcaseReceiver myBroadcaseReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        mActivity = this;
        mContext = getApplicationContext();
        ButterKnife.bind(this);
        getLoginData();
        initData();
        initOperation();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        myBroadcaseReceiver = new MyBroadcaseReceiver();
        registerReceiver(myBroadcaseReceiver, intentFilter);
    }

    private void getLoginData() {
        sharedPreferences = getSharedPreferences(ShareConstants.USERINFO, Context.MODE_PRIVATE);
        String userString = sharedPreferences.getString(ShareConstants.USERINFO, "");
        if (!StringUtils.isEmpty(userString)) {
            isLogin = true;
            userData = JSONHelper.fromJSONObject(userString, UserInfoEntity.class);
        }
    }

    public void clearLoginData() {
        sharedPreferences.edit().putString(ShareConstants.USERINFO, "").commit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (isAnimation) {
            AnimationUtils.setAnimationOfRight(this);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (isAnimation) {
            AnimationUtils.setAnimationOfRight(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (isAnimation) {
            AnimationUtils.setAnimationOfLeft(this);
        }
    }

    public void showProgressDialog(String msg) {
        dismissProgressDialog();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    cancleDbDeal();
                    return false;
                }
            });
        }
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void alert(int strId) {
        Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();
    }

    private class MyBroadcaseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (!NetWorkUtils.isNetworkConnected(context)) {    //没有网络情况
                    dismissProgressDialog();
                    alert(R.string.network_break);
                    cancleDbDeal();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        cancleDbDeal();
        if (myBroadcaseReceiver != null) {
            unregisterReceiver(myBroadcaseReceiver);
            myBroadcaseReceiver = null;
        }
    }
}
