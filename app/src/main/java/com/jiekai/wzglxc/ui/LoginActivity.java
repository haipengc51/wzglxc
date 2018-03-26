package com.jiekai.wzglxc.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.config.ShareConstants;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.UserInfoEntity;
import com.jiekai.wzglxc.entity.UserRoleEntity;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;
import com.jiekai.wzglxc.utils.InputPasswordUtils;
import com.jiekai.wzglxc.utils.JSONHelper;
import com.jiekai.wzglxc.utils.StringUtils;
import com.jiekai.wzglxc.utils.dbutils.DbCallBack;
import com.jiekai.wzglxc.utils.dbutils.DBManager;
import com.jiekai.wzglxc.utils.dbutils.DbDeal;
import com.jiekai.wzglxc.weight.ClickDrawableEdit;

import java.util.List;

import butterknife.BindView;

/**
 * Created by LaoWu on 2017/12/6.
 * 登录页面
 */

public class LoginActivity extends MyBaseActivity implements View.OnClickListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.input_username)
    ClickDrawableEdit inputUsername;
    @BindView(R.id.cancle_username)
    ImageView cancleUsername;
    @BindView(R.id.input_password)
    ClickDrawableEdit inputPassword;
    @BindView(R.id.cancle_password)
    ImageView canclePassword;
    @BindView(R.id.loginBtn)
    TextView loginBtn;

    private InputPasswordUtils userInputUtils;
    private InputPasswordUtils passwordInputUtils;

    private DbDeal dbDeal = null;

    @Override
    public void initView() {
        setContentView(R.layout.activity_login);
        isAnimation = false;
    }

    @Override
    public void initData() {
        title.setText(getResources().getString(R.string.login));
        if (isLogin) {
            inputUsername.setText(userData.getUSERID());
            inputUsername.setSelection(inputUsername.length());
            inputPassword.setText(userData.getPASSWORD());
            inputPassword.setSelection(inputPassword.length());
        }
    }

    @Override
    public void initOperation() {
        back.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        userInputUtils = new InputPasswordUtils(inputUsername, cancleUsername);
        passwordInputUtils = new InputPasswordUtils(inputPassword, canclePassword);
    }

    @Override
    public void cancleDbDeal() {
        if (dbDeal != null) {
            dbDeal.cancleDbDeal();
            dismissProgressDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.loginBtn:
                login();
                break;
        }
    }

    private void login() {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
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
                        showProgressDialog(getResources().getString(R.string.loging));
                    }

                    @Override
                    public void onError(String err) {
                        alert(err);
                        dismissProgressDialog();
                        //判断上次的数据进行登录
                        useOldDataLogin();
                    }

                    @Override
                    public void onResponse(List result) {
                        if (result != null && result.size() != 0) {
                            UserInfoEntity entity = (UserInfoEntity) result.get(0);
                            if ("1".equals(entity.getENABLE())) {
                                checkUserPermission(entity);
                            } else {
                                alert(R.string.zhang_hao_jing_yong);
                                dismissProgressDialog();
                            }
                        } else {
                            alert("用户名或密码错误");
                            dismissProgressDialog();
                        }
                    }
                });
    }

    private void checkUserPermission(final UserInfoEntity userInfoEntity) {
        dbDeal = DBManager.dbDeal(DBManager.SELECT);
                dbDeal.sql(SqlUrl.LoginRule)
                .params(new String[]{userInfoEntity.getUSERID()})
                .clazz(UserRoleEntity.class)
                .execut(mContext, new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {
                        alert(err);
                        dismissProgressDialog();
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
                                saveLoginData(userInfoEntity);
                                Intent intent = new Intent(LoginActivity.this, KeeperMainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                alert(R.string.no_permission_xc);
                            }
                        } else {
                            alert(R.string.no_permission_xc);
                        }
                        dismissProgressDialog();
                    }
                });
    }

    private void saveLoginData(UserInfoEntity loginData) {
        SharedPreferences sharedPreferences = getSharedPreferences(ShareConstants.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userData = JSONHelper.toJSONString(loginData);
        editor.putString(ShareConstants.USERINFO, userData);
        editor.commit();
    }

    private void useOldDataLogin() {
        String userName = inputUsername.getText().toString();
        String userPassword = inputPassword.getText().toString();
        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(userPassword)
                && userData != null && !StringUtils.isEmpty(userData.getUSERID())
                && !StringUtils.isEmpty(userData.getPASSWORD())) {
            if (userName.equals(userData.getUSERID()) && userPassword.equals(userData.getPASSWORD())) {
                Intent intent = new Intent(LoginActivity.this, KeeperMainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
