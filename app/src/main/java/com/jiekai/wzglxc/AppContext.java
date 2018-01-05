package com.jiekai.wzglxc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.jiekai.wzglxc.config.Config;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.DevicelogEntity;
import com.jiekai.wzglxc.ui.RecordHistoryActivity;
import com.jiekai.wzglxc.utils.PictureSelectUtils;
import com.jiekai.wzglxc.utils.dbutils.DBManager;
import com.jiekai.wzglxc.utils.dbutils.DbCallBack;
import com.jiekai.wzglxc.utils.dbutils.DbDeal;
import com.jiekai.wzglxc.utils.ftputils.FtpManager;
import com.jiekai.wzglxc.utils.localDbUtils.DBHelper;

import java.util.List;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by LaoWu on 2017/11/27.
 */

public class AppContext extends Application {
    public static DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        initDbFrame();
        initFTP();
        dbHelper = DBHelper.getInstance(getApplicationContext());
        DbDeal.getInstance();   //初始化dbDeal
        PictureSelectUtils.getCompressFile();
    }

    /**
     * 初始化数据库框架
     */
    private void initDbFrame() {

    }

    /**
     * 初始化FTP上传
     */
    private void initFTP() {
        FtpManager.getInstance().initFTP(Config.IP, Config.FTP_PORT, Config.FTP_USER_NAME, Config.FTP_PASSWORD);
    }

    /**
     * 检查是否有没有审核通过的信息
     * @param activity
     * @param userId
     */
    public static void getUnCheckedData(final Activity activity, String userId) {
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.GET_RECORD_CHECK_LIST)
                .params(new String[]{userId})
                .clazz(DevicelogEntity.class)
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
                            final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                                    .setTitle("提示").create();
                            alertDialog.setMessage("您有上传的信息没有审核通过，点击确定查看详情。");
                            alertDialog.setButton(BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                    activity.startActivity(new Intent(activity, RecordHistoryActivity.class));
                                }
                            });
                            alertDialog.setButton(BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.show();
                        }
                    }
                });
    }
}
