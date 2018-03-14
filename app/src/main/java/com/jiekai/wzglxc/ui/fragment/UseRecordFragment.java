package com.jiekai.wzglxc.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiekai.wzglxc.R;
import com.jiekai.wzglxc.config.Config;
import com.jiekai.wzglxc.config.Constants;
import com.jiekai.wzglxc.config.IntentFlag;
import com.jiekai.wzglxc.config.SqlUrl;
import com.jiekai.wzglxc.entity.LastInsertIdEntity;
import com.jiekai.wzglxc.test.NFCBaseActivity;
import com.jiekai.wzglxc.ui.fragment.base.MyNFCBaseFragment;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by laowu on 2018/1/2.
 */

public class UseRecordFragment extends MyNFCBaseFragment implements View.OnClickListener {
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
    @BindView(R.id.commit)
    TextView commit;
    @BindView(R.id.beizhu)
    EditText beizhu;
    private String title;
    private String bh;
    private boolean enableNfc = false;
    private AlertDialog alertDialog;

    private List<LocalMedia> choosePictures = new ArrayList<>();
    private String imagePath;       //图片的远程地址 /out/123.jpg
    private String imageType;       //图片的类型     .jpg
    private String romoteImageName;     //图片远程服务器的名称 123.jpg
    private String localPath;   //图片本地的地址

    public static UseRecordFragment getInstance(String title, String bh) {
        UseRecordFragment useRecordFragment = new UseRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentFlag.TITLE, title);
        bundle.putString(IntentFlag.BH, bh);
        useRecordFragment.setArguments(bundle);
        return useRecordFragment;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_use_record, container, false);
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(IntentFlag.TITLE);
            bh = bundle.getString(IntentFlag.BH);
            deviceId.setText(bh);
        }
    }

    @Override
    public void initOperation() {
        readCard.setOnClickListener(this);
        choosePicture.setOnClickListener(this);
        commit.setOnClickListener(this);

        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("")
                .setMessage(getResources().getString(R.string.please_nfc))
                .create();
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

        }
        enableNfc = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_card:
                ((NFCBaseActivity) getActivity()).nfcEnable = true;
                enableNfc = true;
                alertDialog.show();
                break;
            case R.id.choose_picture:
                PictureSelectUtils.choosePicture(PictureSelector.create(this), Constants.REQUEST_PICTURE);
                break;
            case R.id.record_image:
                if (choosePictures != null && choosePictures.size() != 0)
                    PictureSelectUtils.previewPicture(mActivity, choosePictures);
                break;
            case R.id.commit:
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
        if (StringUtils.isEmpty(bh)) {
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
        if (!StringUtils.isEmpty(beizhu.getText().toString())) {
            if (beizhu.getText().toString().length() > 255) {
                alert(R.string.beizhu_to_long);
                return;
            }
        }
        updataImage();
    }

    private void updataImage() {
        localPath = choosePictures.get(0).getCompressPath();
        imageType = localPath.substring(localPath.lastIndexOf("."));
        romoteImageName = mActivity.userData.getUSERID() + bh + System.currentTimeMillis() + imageType;
        FtpManager.getInstance().uploadFile(localPath,
                Config.RECORD_PATH, romoteImageName, new FtpCallBack() {
                    @Override
                    public void ftpStart() {
                        showProgressDialog(getResources().getString(R.string.uploading_image));
                    }

                    @Override
                    public void ftpProgress(long allSize, long currentSize, int process) {

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
        String path = Config.RECORD_PATH + romoteImageName;
        if (StringUtils.isEmpty(path)) {
            return;
        }
        FtpManager.getInstance().deletFile(path, new FtpCallBack() {
            @Override
            public void ftpStart() {

            }

            @Override
            public void ftpProgress(long allSize, long currentSize, int process) {

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
        DBManager.dbDeal(DBManager.EVENT_INSERT)
                .sql(SqlUrl.ADD_RECORD)
                .params(new Object[]{title, bh, duihao.getText().toString(), jinghao.getText().toString(),
                        new Date(new java.util.Date().getTime()), mActivity.userData.getUSERID(),
                        CommonUtils.getDataIfNull(beizhu.getText().toString())})
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
                        getInsertId();
                    }
                });
    }

    private void getInsertId() {
        DBManager.dbDeal(DBManager.EVENT_SELECT)
                .sql(SqlUrl.SELECT_INSERT_ID)
                .clazz(LastInsertIdEntity.class)
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
                        if (result != null && result.size() != 0) {
                            insertImagePath(String.valueOf(((LastInsertIdEntity) result.get(0)).getLast_insert_id()));
                        } else {
                            alert(R.string.insert_erro);
                            dismissProgressDialog();
                            rollback();
                            deletImage();
                        }
                    }
                });
    }

    /**
     * 图片路径插入到数据库中
     * SBBH就是上次插入的id
     */
    private void insertImagePath(String SBBH) {
        String fileSize = FileSizeUtils.getAutoFileOrFilesSize(localPath);
        if (StringUtils.isEmpty(fileSize)) {
            rollback();
            deletImage();
            dismissProgressDialog();
            return;
        }
        DBManager.dbDeal(DBManager.EVENT_INSERT)
                .sql(SqlUrl.INSERT_IAMGE)
                .params(new String[]{SBBH, romoteImageName, fileSize, imagePath, imageType, Config.doc_sbjlzl})
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
                        alert(R.string.commit_record_faild);
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
                        alert(R.string.commit_record_faild);
                        dismissProgressDialog();
                    }

                    @Override
                    public void onResponse(List result) {
                        alert(R.string.commit_record_success);
                        dismissProgressDialog();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_PICTURE && resultCode == RESULT_OK) {
            choosePictures = PictureSelector.obtainMultipleResult(data);
            if (choosePictures != null && choosePictures.size() != 0) {
                String currentDevicePicturePath = choosePictures.get(0).getCompressPath();
                GlidUtils.displayImage(mActivity, currentDevicePicturePath, recordImage);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PictureSelectUtils.clearPictureSelectorCache(mActivity);
    }
}