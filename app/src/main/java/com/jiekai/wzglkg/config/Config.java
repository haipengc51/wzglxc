package com.jiekai.wzglkg.config;

import android.os.Environment;

/**
 * Created by LaoWu on 2017/11/19.
 * 程序的配置文件都这道这个里面
 */

public class Config {
    public static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";     //数据库链接驱动
    public static final String IP = "114.115.171.225";      //服务器地址
    public static final String DB_URL = "jdbc:mysql://" + IP + ":3306/wzgl";   //mysql数据库url
    public static final String DB_USER_NAME = "root";  //用户名
    public static final String DB_USER_PASSWORD = "admin2017@";
    public static final int FTP_PORT = 21;                      //FTP端口号
    public static final String FTP_USER_NAME = "FtpTest";      //FTP用户名
    public static final String FTP_PASSWORD = "haipengc51~";
    public static final String WEB_HOLDE = "http://114.115.171.225";
    public static final String FTP_PATH_HANDLER = "/View/AppImage";     //FTP上传图片的基础目录地址
    public static final String BINDIMAGE_PATH = "/bind/";    //设备绑定所上传图片的地址
    public static final String OUTIMAGE_PATH = "/out/";
    public static final String INIMAGE_PATH = "/in/";
    public static final String REPAIR_PATH = "/repair/";
    public static final String SCRAP_PATH = "/scrap/";
    public static final String PICTURE_COMPRESS_PATH = Environment.getExternalStorageDirectory().toString() + "/wzgl/imag/compress";    //图片压缩的地址
    public static final String SBBD = "sbbd";   //设备绑定的类型
    public static final String doc_sbck= "sbck";    //设备出库的类型
    public static final String doc_sbrk= "sbrk";    //设备入库的类型
    public static final String doc_sbwx= "sbwx";    //设备维修的类型
    public static final String doc_sbdx= "sbdx";    //设备大修的类型
    public static final String doc_sbfc= "sbfc";    //设备返厂的类型
    public static final String doc_sbbf= "sbbf";    //设备报废的类型
    public static final String doc_jcbg= "jcbg";    //检测报告
    public static final String doc_hgz= "hgz";    //合格证
    public static final String doc_wzmp= "wzmp";    //物资铭牌
    public static final String doc_jgtz= "jgtz";    //结构图纸
    public static final String doc_sywhsms= "sywhsms";    //使用维护说明书
    public static final String doc_yspj= "yspj";    //易损配件
    public static final String doc_qtfj= "qtfj";    //其他附件

}
