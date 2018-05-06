package com.jiekai.wzglxc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.TabHost;

import com.bth.api.cls.Comm_Bluetooth;
import com.function.SPconfig;
import com.jiekai.wzglxc.config.Config;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.DeviceInspectionEntity;
import com.jiekai.wzglxc.entity.DeviceapplyEntity;
import com.jiekai.wzglxc.entity.DevicelogEntity;
import com.jiekai.wzglxc.entity.DevicemoveEntity;
import com.jiekai.wzglxc.ui.RecordHistoryActivity;
import com.jiekai.wzglxc.utils.PictureSelectUtils;
import com.jiekai.wzglxc.utils.StringUtils;
import com.jiekai.wzglxc.utils.dbutils.DBManager;
import com.jiekai.wzglxc.utils.dbutils.DbCallBack;
import com.jiekai.wzglxc.utils.dbutils.DbDeal;
import com.jiekai.wzglxc.utils.ftputils.FtpManager;
import com.jiekai.wzglxc.utils.localDbUtils.DBHelper;
import com.silionmodule.Reader;
import com.silionmodule.TAGINFO;
import com.silionmodule.TagFilter;
import com.silionmodule.TagOp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by LaoWu on 2017/11/27.
 */

public class AppContext extends Application {
    public static DBHelper dbHelper;

    public Comm_Bluetooth comm_bluetooth;
    public Activity acty;

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
     * @param context
     * @param userId
     */
    public static void getUnCheckedData(final Context context, final String userId) {
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.GET_RECORD_CHECK_LIST)
                .params(new String[]{userId})
                .clazz(DevicelogEntity.class)
                .execut(context, new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {

                    }

                    @Override
                    public void onResponse(List result) {
                        if (result != null && result.size() != 0) {
                            showUnCheckDialog(context);
                            return;
                        }
                        getData(context, userId);
                    }
                });
    }

    private static void getData(final Context context, final String userId) {
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.GET_MOVE_CHECK_LIST)
                .params(new String[]{userId})
                .clazz(DevicemoveEntity.class)
                .execut(context, new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {

                    }

                    @Override
                    public void onResponse(List result) {
                        if (result != null && result.size() != 0) {
                            showUnCheckDialog(context);
                            return;
                        }
                        getApplyUncheckData(context, userId);
                    }
                });
    }

    private  static void getApplyUncheckData(final Context context, final String userId) {
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.GET_APPLAY_CHECK_LIST)
                .params(new String[]{userId})
                .clazz(DeviceapplyEntity.class)
                .execut(context, new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {

                    }

                    @Override
                    public void onResponse(List result) {
                        if (result != null && result.size() != 0) {
                            showUnCheckDialog(context);
                            return;
                        }
                        getDataThree(context, userId);
                    }
                });
    }

    private static void getDataThree(final Context context, String userId) {
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.GET_INSPECTION_CHECK_LIST)
                .params(new String[]{userId})
                .clazz(DeviceInspectionEntity.class)
                .execut(context, new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {

                    }

                    @Override
                    public void onResponse(List result) {
                        if (result != null && result.size() != 0) {
                            showUnCheckDialog(context);
                        }
                    }
                });
    }
    private static void showUnCheckDialog(final Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("提示").create();
        alertDialog.setMessage("您有上传的信息没有审核通过，点击确定查看详情。");
        alertDialog.setButton(BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                context.startActivity(new Intent(context, RecordHistoryActivity.class));
            }
        });
        alertDialog.setButton(BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    //常量
    //*
    public static String Constr_READ = "读";
    public static String Constr_CONNECT = "连接";
    public static String Constr_INVENTORY = "盘点";
    public static String Constr_RWLOP = "读写锁";
    public static String Constr_PASSVICE = "被动设置";
    public static String Constr_ACTIVE = "主动设置";
    public static String Constr_SetFaill = "设置失败：";
    public static String Constr_GetFaill = "获取失败：";
    public static String Constr_SetOk="设置成功";
    public static String Constr_unsupport="不支持";
    public static String Constr_Putandexit = "再按一次退出程序";
    public static String[] Coname = new String[] { "序号", "EPC ID", "次数", "天线",
            "协议", "RSSI", "频率", "附加数据 " };
    public static String Constr_stopscan = "请先停止扫描";
    public static String Constr_scanasetconnecto = "请扫描并选中一个蓝牙读写器,并且完成连接";
    public static String Constr_scanselectabluereader = "请扫描并选中一个蓝牙读写器";
    public static String Constr_scanselectabluereaderandconnect = "请扫描并选中一个蓝牙读写器,并且完成连接";
    public static String Constr_hadconnected = "已经连接";
    public static String Constr_plsetuuid = "请设置好UUID:";
    public static String Constr_pwderror = "密码错误";
    public static String Constr_search = "搜索";
    public static String Constr_stop = "停止";
    public static String Constr_plselectsearchblueset = "请选择要搜索的蓝牙设备";
    public static String Constr_startsearchblueok = "开始搜索蓝牙 成功";
    public static String Constr_startsearchbluefail1 = "开始搜索蓝牙:1 失败";
    public static String Constr_startsearchbluefail2 = "开始搜索蓝牙:2 失败";
    public static String Constr_startsearchbluefail12 = "开始搜索蓝牙:1,2 失败，将重启蓝牙，请等待重启完成";
    public static String Constr_canclebluematch = "取消蓝牙设备匹配:";
    public static String Constr_connectbluesetfail = "连接蓝牙设备失败:";
    public static String Constr_matchbluefail = "匹配蓝牙设备失败";
    public static String Constr_pwdmatchfail = "密码匹配失败";
    public static String Constr_connectblueokthentoreader = "连接蓝牙设备成功,将连接读写器";
    public static String Constr_connectblueserfail = "连接蓝牙设备服务失败";
    public static String Constr_connectbluesetok = "连接蓝牙设备成功";
    public static String Constr_createreaderok = "读写器创建失败";
    public String[] pdaatpot = { "一天线", "双天线", "三天线", "四天线" };
    public String[] strconectway = { "被动式", "主动式" };

    String[] spibank={"保留区","EPC区","TID区","用户区"};
    String[] spifbank={"EPC区","TID区","用户区"};
    String[] spilockbank={"访问密码","销毁密码","EPCbank","TIDbank","USERbank"};
    String[] spilocktype={"解锁定","暂时锁定","永久锁定"};
    public static String Constr_sub3readmem = "读标签";
    public static String Constr_sub3writemem = "写标签";
    public static String Constr_sub3lockkill = "锁与销毁";
    public static String Constr_sub3readfail = "读失败:";
    public static String Constr_sub3nodata = "无数据";
    public static String Constr_sub3wrtieok = "写成功";
    public static String Constr_sub3writefail = "写失败:";
    public static String Constr_sub3lockok = "锁成功";
    public static String Constr_sub3lockfail = "锁失败:";
    public static String Constr_sub3killok = "销毁成功";
    public static String Constr_sub3killfial = "销毁失败:";

    String[] spireg={"中国","北美","日本","韩国","欧洲","印度","加拿大","全频段"
            ,"中国2"};
    String[] spinvmo={"普通模式","高速模式"};
    String[] spitari={"25微秒","12.5微秒","6.25微秒"};
    String[] spiwmod={"字写","块写"};
    String Auto="自动";

    public static String Constr_sub4invenpra="盘点参数";
    public static String Constr_sub4antpow="天线功率";
    public static String Constr_sub4regionfre="区域频率";
    public static String Constr_sub4gen2opt="Gen2项";
    public static String Constr_sub4invenfil="盘点过滤";
    public static String Constr_sub4addidata="附加数据";
    public static String Constr_sub4others="其他参数";
    public static String Constr_sub4setmodefail="配置模式失败";
    public static String Constr_sub4hadactivemo="已经为主动模式";
    public static String Constr_sub4setokresettoab="设置成功，重启读写器生效";
    public static String Constr_sub4hadpasstivemo="已经为被动模式";
    public static String Constr_sub4ndsapow="该设备需要功率一致";
    public static String Constr_sub4unspreg="不支持的区域";

    String[] spiregbs = { "北美", "中国", "欧频", "中国2" };
    public static String Constr_subblmode = "模式";
    public static String Constr_subblinven = "盘点";
    public static String Constr_subblfil = "过滤";
    public static String Constr_subblfre = "频率";
    public static String Constr_subbl = "蓝牙";
    public static String Constr_subblnofre = "没有选择频点";

    String[] cusreadwrite={"读操作","写操作"};
    String[] cuslockunlock={"锁","解锁"};

    public static String Constr_subcsalterpwd="改密码";
    public static String Constr_subcslockwpwd="带密码锁";
    public static String Constr_subcslockwoutpwd="不带密码锁";
    public static String Constr_subcsplsetimeou="请设置超时时间";
    public static String Constr_subcsputcnpwd="填入当前密码与新密码";
    public static String Constr_subcsplselreg="请选择区域";
    public static String Constr_subcsopfail="操作失败:";
    public static String Constr_subcsputcurpwd="填入当前密码";

    public static String Constr_subdbhaddisconnerecon = "已经断开,正在重新连接";
    public static String Constr_subdbdisconnreconn = "已经断开,正在重新连接";
    public static String Constr_subdbhadconnected = "已经连接";
    public static String Constr_subdbconnecting = "正在连接......";
    public static String Constr_subdbrev = "接收";
    public static String Constr_subdbstop = "停止";
    public static String Constr_subdbdalennot = "数据长度不对";
    public static String Constr_subdbplpuhexchar = "请输入16进制字符";

    /*
     * 公共变量
     */
    public Map<String,TAGINFO> TagsMap=new LinkedHashMap<String,TAGINFO>();//有序

    public Comm_Bluetooth CommBth;
    public Activity Mact;
    public int Mode;
    public Map<String, String> m;

    public Reader Mreader;
    public String Address;
    public SPconfig spf;
    public ReaderParams Rparams;
    public long exittime;
    public TabHost tabHost;
    public boolean isread;
    public String bluepassword;
    public int BackResult;

    public boolean connectok;

    public class ReaderParams
    {
        public int antportc;
        public int sleep;
        public int[] uants;
        public int readtime;
        public String Curepc;
        public int Bank;

        public TagOp To;
        public TagFilter Tf;

        public ReaderParams()
        {
            sleep=0;
            readtime=200;
            uants=new int[1];
            uants[0]=1;

        }
    }
}
