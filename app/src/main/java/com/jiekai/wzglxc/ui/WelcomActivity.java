package com.jiekai.wzglxc.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.config.ShareConstants;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.UserInfoEntity;
import com.jiekai.wzglxc.entity.UserRoleEntity;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;
import com.jiekai.wzglxc.utils.JSONHelper;
import com.jiekai.wzglxc.utils.StringUtils;
import com.jiekai.wzglxc.utils.dbutils.DBManager;
import com.jiekai.wzglxc.utils.dbutils.DbCallBack;
import com.jiekai.wzglxc.utils.dbutils.DbDeal;

import java.util.List;

/**
 * Created by laowu on 2017/12/5.
 * 欢迎页面
 */

public class WelcomActivity extends MyBaseActivity {
    private boolean isTime = false;     //欢迎页时间到标志
    private boolean myLogin = false;    //后台登录成功标志

    private UserInfoEntity userInfoEntity;

    private DbDeal dbDeal = null;

    @Override
    public void initView() {
        setContentView(R.layout.activity_welcom);
        isAnimation = false;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initOperation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isTime = true;
                changeUi();
            }
        }, 3000);

        if (isLogin) {
            login(userData.getUSERID(), userData.getPASSWORD());
        }
    }

    @Override
    public void cancleDbDeal() {
        if (dbDeal != null) {
            dbDeal.cancleDbDeal();
            dismissProgressDialog();
        }
    }

    private void login(String username, String password) {
        if (StringUtils.isEmpty(username)) {
            alert(R.string.please_input_username);
            return;
        }
        if (StringUtils.isEmpty(password)) {
            alert(R.string.please_input_password);
            return;
        }
        dbDeal = DBManager.dbDeal(DBManager.SELECT);
                dbDeal.sql(SqlUrl.LoginSql)
                .params(new String[]{username, password})
                .clazz(UserInfoEntity.class)
                .execut(mContext, new DbCallBack() {
                    @Override
                    public void onDbStart() {
                    }

                    @Override
                    public void onError(String err) {
                    }

                    @Override
                    public void onResponse(List result) {
                        if (result != null && result.size() != 0) {
                            UserInfoEntity entity = (UserInfoEntity) result.get(0);
                            if ("1".equals(entity.getENABLE())) {
                                checkUserPermission(entity);
                            } else {
                            }
                        }
                    }
                });
    }

    private void checkUserPermission(final UserInfoEntity userInfoEntity1) {
        dbDeal = DBManager.dbDeal(DBManager.SELECT);
                dbDeal.sql(SqlUrl.LoginRule)
                .params(new String[]{userInfoEntity1.getUSERID()})
                .clazz(UserRoleEntity.class)
                .execut(mContext, new DbCallBack() {
                    @Override
                    public void onDbStart() {
                    }

                    @Override
                    public void onError(String err) {
                    }

                    @Override
                    public void onResponse(List result) {
                        if (result != null && result.size() != 0) {
                            boolean isOne = false;
                            boolean isTwo = false;
                            for (int i=0; i<result.size(); i++) {
                                String role = ((UserRoleEntity) result.get(i)).getROLEID();
                                if ("003".equals(role)) {
                                    isOne = true;
                                }
                                if ("004".equals(role)) {
                                    isTwo = true;
                                }
                                if (isOne && isTwo) {
                                    break;
                                }
                            }
                            if (isOne && isTwo) {
                                userInfoEntity = userInfoEntity1;
                                myLogin = true;
                                changeUi();
                            } else {
                            }
                        } else {
                        }
                    }
                });
    }

    private void changeUi() {
        if (isTime && myLogin) {
            isTime = false;
            saveLoginData(userInfoEntity);
            Intent intent = new Intent(mActivity, KeeperMainActivity.class);
            startActivity(intent);
            finish();
        } else if (isTime && !myLogin) {
            isTime = false;
            Intent intent = new Intent(mActivity, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void saveLoginData(UserInfoEntity loginData) {
        SharedPreferences sharedPreferences = getSharedPreferences(ShareConstants.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userData = JSONHelper.toJSONString(loginData);
        editor.putString(ShareConstants.USERINFO, userData);
        editor.commit();
    }
}
