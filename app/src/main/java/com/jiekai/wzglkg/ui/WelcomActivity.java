package com.jiekai.wzglkg.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.jiekai.wzglkg.R;
import com.jiekai.wzglkg.config.ShareConstants;
import com.jiekai.wzglkg.config.SqlUrl;
import com.jiekai.wzglkg.entity.UserInfoEntity;
import com.jiekai.wzglkg.entity.UserRoleEntity;
import com.jiekai.wzglkg.ui.base.MyBaseActivity;
import com.jiekai.wzglkg.utils.JSONHelper;
import com.jiekai.wzglkg.utils.StringUtils;
import com.jiekai.wzglkg.utils.dbutils.DBManager;
import com.jiekai.wzglkg.utils.dbutils.DbCallBack;

import java.util.List;

/**
 * Created by laowu on 2017/12/5.
 * 欢迎页面
 */

public class WelcomActivity extends MyBaseActivity {
    private boolean isTime = false;     //欢迎页时间到标志
    private boolean myLogin = false;    //后台登录成功标志

    private UserInfoEntity userInfoEntity;
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
        }, 2000);

        if (isLogin) {
            login(userData.getUSERID(), userData.getPASSWORD());
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
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.LoginSql)
                .params(new String[]{username, password})
                .clazz(UserInfoEntity.class)
                .execut(new DbCallBack() {
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
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.LoginRule)
                .params(new String[]{userInfoEntity1.getUSERID()})
                .clazz(UserRoleEntity.class)
                .execut(new DbCallBack() {
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
                            boolean isThree = false;
                            for (int i=0; i<result.size(); i++) {
                                String role = ((UserRoleEntity) result.get(i)).getROLEID();
                                if ("003".equals(role)) {
                                    isOne = true;
                                }
                                if ("004".equals(role)) {
                                    isTwo = true;
                                }
                                if ("006".equals(role)) {
                                    isThree = true;
                                }
                                if (isOne && isTwo && isThree) {
                                    break;
                                }
                            }
                            if (isOne && isTwo && isThree) {
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
