package com.jiekai.wzglxc.ui;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.config.Config;
import com.jiekai.wzglxc.config.Constants;
import com.jiekai.wzglxc.config.IntentFlag;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.DeviceInspectionEntity;
import com.jiekai.wzglxc.entity.DevicedocEntity;
import com.jiekai.wzglxc.entity.DevicemoveEntity;
import com.jiekai.wzglxc.ui.base.MyBaseActivity;
import com.jiekai.wzglxc.utils.CommonUtils;
import com.jiekai.wzglxc.utils.FileSizeUtils;
import com.jiekai.wzglxc.utils.GlidUtils;
import com.jiekai.wzglxc.utils.PictureSelectUtils;
import com.jiekai.wzglxc.utils.StringUtils;
import com.jiekai.wzglxc.utils.dbutils.DBManager;
import com.jiekai.wzglxc.utils.dbutils.DbCallBack;
import com.jiekai.wzglxc.utils.ftputils.FtpCallBack;
import com.jiekai.wzglxc.utils.ftputils.FtpManager;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by LaoWu on 2018/1/5.
 * 未什么通过的申请的设备转场的详情页
 */

public class InspectionHistoryDetailActivity extends MyBaseActivity implements View.OnClickListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.record_type)
    TextView recordType;
    @BindView(R.id.device_id)
    TextView deviceId;
    @BindView(R.id.read_card)
    TextView readCard;
    @BindView(R.id.record_image)
    ImageView recordImage;
    @BindView(R.id.choose_picture)
    TextView choosePicture;
    @BindView(R.id.duihao)
    EditText duihao;
    @BindView(R.id.jinghao)
    EditText jinghao;
    @BindView(R.id.check_remark)
    TextView checkRemark;
    @BindView(R.id.recommit)
    TextView recommit;

    private DeviceInspectionEntity currentDatas;

    private List<LocalMedia> choosePictures = new ArrayList<>();
    private String imagePath;       //图片的远程地址 /out/123.jpg
    private String imageType;       //图片的类型     .jpg
    private String romoteImageName;     //图片远程服务器的名称 123.jpg
    private String localPath;   //图片本地的地址
    private boolean isChooseImage = false;  //是否重新上传了图片

    @Override
    public void initView() {
        setContentView(R.layout.activity_inspection_history_detail);
    }

    @Override
    public void initData() {
        title.setText(getResources().getString(R.string.record_failed_detail));
        back.setOnClickListener(this);
        choosePicture.setOnClickListener(this);
        recordImage.setOnClickListener(this);
        recommit.setOnClickListener(this);

        currentDatas = (DeviceInspectionEntity) getIntent().getSerializableExtra(IntentFlag.DATA);
    }

    @Override
    public void initOperation() {
        if (currentDatas != null) {
            recordType.setText(CommonUtils.getDataIfNull(getResources().getString(R.string.device_inspection)));
            deviceId.setText(CommonUtils.getDataIfNull(currentDatas.getSBBH()));
//            duihao.setText(CommonUtils.getDataIfNull(currentDatas.getDH()));
//            jinghao.setText(CommonUtils.getDataIfNull(currentDatas.getJH()));
            checkRemark.setText(CommonUtils.getDataIfNull(currentDatas.getSHBZ()));
            showCommitImage(currentDatas.getID());
        } else {
            alert(R.string.get_bh_faild);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.choose_picture:
                PictureSelectUtils.choosePicture(PictureSelector.create(this), Constants.REQUEST_PICTURE);
                break;
            case R.id.record_image:
                if (choosePictures != null && choosePictures.size() != 0)
                    PictureSelectUtils.previewPicture(mActivity, choosePictures);
                break;
            case R.id.recommit:
                upload();
                break;
        }
    }

    /**
     * 开始提交数据
     */
    private void upload() {
        if (choosePictures != null && choosePictures.size() == 0) {
            alert(R.string.please_choose_image);
            return;
        }
        if (StringUtils.isEmpty(currentDatas.getSBBH())) {
            alert(R.string.get_bh_faild);
            return;
        }
        if (StringUtils.isEmpty(duihao.getText().toString())) {
            alert(R.string.please_input_duihao);
            return;
        }
        if (StringUtils.isEmpty(jinghao.getText().toString())) {
            alert(R.string.please_input_jinghao);
            return;
        }
        if (isChooseImage) {
            updataImage();
        } else {
            startEvent();
        }
    }

    private void updataImage() {
        localPath = choosePictures.get(0).getCompressPath();
        imageType = localPath.substring(localPath.lastIndexOf("."));
        romoteImageName = userData.getUSERID() + currentDatas.getSBBH() + System.currentTimeMillis() + imageType;
        FtpManager.getInstance().uploadFile(localPath,
                Config.INSPECTION_PATH, romoteImageName, new FtpCallBack() {
                    @Override
                    public void ftpStart() {
                        showProgressDialog(getResources().getString(R.string.uploading_image));
                    }

                    @Override
                    public void ftpSuccess(String remotePath) {
                        dismissProgressDialog();
                        imagePath = Config.FTP_PATH_HANDLER + remotePath;
                        startEvent();
                    }

                    @Override
                    public void ftpFaild(String error) {
                        alert(error);
                        dismissProgressDialog();
                    }
                });
    }

    private void deletImage() {
        if (!isChooseImage) {
            return;
        }
        String path = Config.INSPECTION_PATH + romoteImageName;
        if (StringUtils.isEmpty(path)) {
            return;
        }
        FtpManager.getInstance().deletFile(path, new FtpCallBack() {
            @Override
            public void ftpStart() {

            }

            @Override
            public void ftpSuccess(String remotePath) {

            }

            @Override
            public void ftpFaild(String error) {

            }
        });
    }

    /**
     * 开启数据库事务
     */
    private void startEvent() {
        DBManager.dbDeal(DBManager.START_EVENT)
                .execut(new DbCallBack() {
                    @Override
                    public void onDbStart() {
                        showProgressDialog(getResources().getString(R.string.uploading_db));
                    }

                    @Override
                    public void onError(String err) {
                        alert(err);
                        deletImage();
                        dismissProgressDialog();
                    }

                    @Override
                    public void onResponse(List result) {
                        insertRecord();
                    }
                });
    }

    /**
     * 插入记录的数据库
     */
    private void insertRecord() {
        DBManager.dbDeal(DBManager.EVENT_UPDATA)
                .sql(SqlUrl.UPDATE_INSPECTION)
                .params(new Object[]{new Date(new java.util.Date().getTime()), userData.getUSERID(), currentDatas.getID()})
                .execut(new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {
                        alert(err);
                        dismissProgressDialog();
                        deletImage();
                        rollback();
                    }

                    @Override
                    public void onResponse(List result) {
                        if (isChooseImage) {
                            insertImagePath(String.valueOf(currentDatas.getID()));
                        } else {
                            commit();
                        }
                    }
                });
    }

    /**
     * 图片路径插入到数据库中
     * SBBH就是上次插入的id
     */
    private void insertImagePath(String SBBH) {
        if (!isChooseImage) {
            return;
        }
        String fileSize = FileSizeUtils.getAutoFileOrFilesSize(localPath);
        if (StringUtils.isEmpty(fileSize)) {
            rollback();
            deletImage();
            dismissProgressDialog();
            return;
        }
        DBManager.dbDeal(DBManager.EVENT_UPDATA)
                .sql(SqlUrl.UPDATE_IMAGE)
                .params(new String[]{romoteImageName, fileSize, imagePath, imageType, SBBH, Config.doc_sbxj})
                .execut(new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {
                        alert(err);
                        dismissProgressDialog();
                        rollback();
                        deletImage();
                    }

                    @Override
                    public void onResponse(List result) {
                        commit();
                    }
                });
    }

    /**
     * 删除上次图片
     */
    private void deletRemotImage() {
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.Get_Image_Path)
                .params(new String[]{String.valueOf(currentDatas.getID()), Config.doc_sbxj})
                .clazz(DevicedocEntity.class)
                .execut(new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {

                    }

                    @Override
                    public void onResponse(List result) {
                        String filePath = ((DevicedocEntity) result.get(0)).getWJDZ();
                        int start = filePath.indexOf(Config.FTP_PATH_HANDLER);
                        if (filePath != null && start != -1) {
                            filePath = filePath.substring(start + Config.FTP_PATH_HANDLER.length());
                        } else {
                            return;
                        }
                        FtpManager.getInstance().deletFile(filePath, new FtpCallBack() {
                            @Override
                            public void ftpStart() {

                            }

                            @Override
                            public void ftpSuccess(String remotePath) {

                            }

                            @Override
                            public void ftpFaild(String error) {

                            }
                        });
                    }
                });
    }

    private void rollback() {
        DBManager.dbDeal(DBManager.ROLLBACK)
                .execut(new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {
                        alert(err);
                    }

                    @Override
                    public void onResponse(List result) {
                        alert(R.string.device_inspection_faild);
                    }
                });
    }

    private void commit() {
        DBManager.dbDeal(DBManager.COMMIT)
                .execut(new DbCallBack() {
                    @Override
                    public void onDbStart() {

                    }

                    @Override
                    public void onError(String err) {
                        alert(R.string.device_inspection_faild);
                        dismissProgressDialog();
                    }

                    @Override
                    public void onResponse(List result) {
                        alert(R.string.device_inspection_success);
                        dismissProgressDialog();
                        finish();
                    }
                });
    }

    private void showCommitImage(int id) {
        if (id == -1) {
            alert(R.string.get_image_fail);
            return;
        }
        DBManager.dbDeal(DBManager.SELECT)
                .sql(SqlUrl.Get_Image_Path)
                .params(new Object[]{id, Config.doc_sbxj})
                .clazz(DevicedocEntity.class)
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
                            DevicedocEntity entity = (DevicedocEntity) result.get(0);
                            LocalMedia localMedia = new LocalMedia();
                            localMedia.setPath(Config.WEB_HOLDE + entity.getWJDZ());
                            choosePictures.clear();
                            choosePictures.add(localMedia);
                            GlidUtils.displayImage(mActivity, Config.WEB_HOLDE + entity.getWJDZ(), recordImage);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_PICTURE && resultCode == RESULT_OK) {
            choosePictures.clear();
            choosePictures = PictureSelector.obtainMultipleResult(data);
            if (choosePictures != null && choosePictures.size() != 0) {
                isChooseImage = true;
                String currentDevicePicturePath = choosePictures.get(0).getCompressPath();
                GlidUtils.displayImage(mActivity, currentDevicePicturePath, recordImage);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PictureSelectUtils.clearPictureSelectorCache(mActivity);
    }
}
