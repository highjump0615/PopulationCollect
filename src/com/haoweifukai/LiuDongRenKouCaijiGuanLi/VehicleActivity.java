/**
 * @author LuYongXing
 * @date 2014.09.28
 * @filename VehicleActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CheLiangColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.PhotoUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.TencentGPSTracker;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.UploadUtil;

import java.util.ArrayList;
import java.util.Date;

public class VehicleActivity extends PermanentActivity implements View.OnClickListener {

    private static final String TAG = VehicleActivity.class.getSimpleName();
    public static final String SELECTED_VEHICLE_ID = "selected_vehicle_id";

    private static final int MEDIA_TYPE_IMAGE = 0;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CROP_IMAGE_REQUEST_CODE = 150;
    private static final int PREVIEW_PHOTO_REQUEST_CODE = 400;

    // 常数
    // variables to capture image
    private Uri mFileUri;
    private ArrayList<Uri> mZhaoPianUriArray = new ArrayList<Uri>();

    private TencentGPSTracker mTencentGPSTracker = null;

    //
    private boolean mIsAdd = false;
    private boolean mIsUpdate = false;
    private String mSelectedID = "";

    // 定义控制
    // 车辆信息
    private EditText mEditChePaiZhao;
    private Spinner mSpinnerCheShenYanSe;
    private EditText mEditPinPai;
    private EditText mEditXingHao;
    private Spinner mSpinnerCheLiangLeiXing;
    private Spinner mSpinnerHaoPaiZhongLei;
    private EditText mEditCheLiangMiaoShu;
    private EditText mEditJiaShiRenXingMing;
    private EditText mEditJiaShiRenShenFenZhengHao;
    private EditText mEditJiaShiRenJiaZhaoHaoMa;
    private EditText mEditCheLiangXingShiZhengHaoMa;
    private Spinner mSpinnerSuoZaiQuXian;
    private EditText mEditSuoZaiJieDao;
    private EditText mEditSuoShuPaiChuSuo;
    private EditText mEditXiangXiDiZhi;

    // 车辆照片
    private ImageView[] mImageZhaoPians = new ImageView[3];

    // 按钮
    private View mLayoutXiuGaiAnNiu;
    private View mLayoutLuRuAnNiu;
    private View mLayoutZhaoPian;
    private Button mButtonCaiJiCheLiangZhaoPian;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //cropImage();
                    if (mFileUri != null) {
                        mZhaoPianUriArray.add(mFileUri);
                        refreshImageViews();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the photoImage capture
                    mFileUri = null;
                } else {
                    // Image capture failed, advise user
                    mFileUri = null;
                }
                break;

            case CROP_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (mFileUri != null) refreshImageViews();
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the photoImage capture
                    mFileUri = null;
                } else {
                    // Image capture failed, advise user
                    mFileUri = null;
                }
                break;

            case PREVIEW_PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mZhaoPianUriArray = (ArrayList<Uri>) data.getSerializableExtra(PreviewPhotoActivity.PHOTO_ID_ARRAY);
                    refreshImageViews();
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the photoImage capture
                } else {
                    // Image capture failed, advise user
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(SELECTED_VEHICLE_ID)) {
            mSelectedID = intent.getStringExtra(SELECTED_VEHICLE_ID);
            mIsAdd = false;
        } else {
            mIsAdd = true;
        }

        super.onCreate(savedInstanceState);
        if (!mIsAdd) loadData();

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        mTencentGPSTracker = new TencentGPSTracker(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTencentGPSTracker != null)
            mTencentGPSTracker.startLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTencentGPSTracker != null) {
            mTencentGPSTracker.stopLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTencentGPSTracker != null) {
            mTencentGPSTracker.stopTracker();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_caijifangwutuxiang:
                if (mZhaoPianUriArray.size() >= Config.MAX_VEHICLE_PHOTO_COUNT) {
                    Dialog dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.app_name)
                            .setMessage("照片数已经" + Config.MAX_VEHICLE_PHOTO_COUNT + "个超过了。您删除照片吗？")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onPreviewPhoto();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    dialog.show();
                } else {
                    onTakePhoto();
                }
                break;

            case R.id.layout_cheliangzhaopian:
                onPreviewPhoto();
                break;

            case R.id.button_baocun:
                onSave();
                break;

            case R.id.button_xiugai:
                mIsUpdate = true;
                mLayoutLuRuAnNiu.setVisibility(View.VISIBLE);
                mLayoutXiuGaiAnNiu.setVisibility(View.GONE);
                setEnableModify(true);
                break;

            case R.id.button_shanchu:
                onDelete();
                break;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();

        View rootView = mLayoutInflater.inflate(R.layout.activity_vehicle, mLayoutContainer);

        // 来京人员基本信息
        mEditChePaiZhao = (EditText) rootView.findViewById(R.id.edit_chepaizhao);
        mSpinnerCheShenYanSe = (Spinner) rootView.findViewById(R.id.spinner_cheshenyanse);

        MyContentProvider contentProvider = new MyContentProvider();

        ArrayList<String> spinnerArray = contentProvider.getVehicleColor();
        spinnerArray.add(0, "请选择");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCheShenYanSe.setAdapter(spinnerArrayAdapter);

        mEditPinPai = (EditText) rootView.findViewById(R.id.edit_pinpai);
        mEditXingHao = (EditText) rootView.findViewById(R.id.edit_xinghao);

        mSpinnerCheLiangLeiXing = (Spinner) rootView.findViewById(R.id.spinner_cheliangleixing);
        spinnerArray = contentProvider.getVehicleType();
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCheLiangLeiXing.setAdapter(spinnerArrayAdapter);

        mSpinnerHaoPaiZhongLei = (Spinner) rootView.findViewById(R.id.spinner_haopaizhonglei);
        spinnerArray = contentProvider.getVehicleNumberType();
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerHaoPaiZhongLei.setAdapter(spinnerArrayAdapter);

        mEditCheLiangMiaoShu = (EditText) rootView.findViewById(R.id.edit_cheliangmiaoshu);
        mEditJiaShiRenXingMing = (EditText) rootView.findViewById(R.id.edit_jiashirenxingming);
        mEditJiaShiRenShenFenZhengHao = (EditText) rootView.findViewById(R.id.edit_jiashirenshenfenzhenghao);
        mEditJiaShiRenJiaZhaoHaoMa = (EditText) rootView.findViewById(R.id.edit_jiashirenjiazhaohaoma);
        mEditCheLiangXingShiZhengHaoMa = (EditText) rootView.findViewById(R.id.edit_cheliangxingshizhenghaoma);

        mSpinnerSuoZaiQuXian = (Spinner) rootView.findViewById(R.id.spinner_suozaiquxian);
        spinnerArray = contentProvider.getQX("北京市");
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSuoZaiQuXian.setAdapter(spinnerArrayAdapter);

        mEditSuoZaiJieDao = (EditText) rootView.findViewById(R.id.edit_suozaijiedao);
        mEditSuoShuPaiChuSuo = (EditText) rootView.findViewById(R.id.edit_suoshupaichusuomingcheng);
        mEditSuoShuPaiChuSuo.setText(mOwnerData.SSDWMC);

        mEditXiangXiDiZhi = (EditText) rootView.findViewById(R.id.edit_xiangxidizhi);

        mLayoutZhaoPian = rootView.findViewById(R.id.layout_cheliangzhaopian);
        mLayoutZhaoPian.setOnClickListener(this);
        mImageZhaoPians[0] = (ImageView) rootView.findViewById(R.id.image_zhaopian1);
        mImageZhaoPians[1] = (ImageView) rootView.findViewById(R.id.image_zhaopian2);
        mImageZhaoPians[2] = (ImageView) rootView.findViewById(R.id.image_zhaopian3);

        /* 按钮 */
        mLayoutLuRuAnNiu = findViewById(R.id.layout_luru_anniu);
        mLayoutXiuGaiAnNiu = findViewById(R.id.layout_xiugai_anniu);

        findViewById(R.id.button_baocun).setOnClickListener(this);
        findViewById(R.id.button_caijifangwushexiang).setVisibility(View.GONE);
        mButtonCaiJiCheLiangZhaoPian = (Button) findViewById(R.id.button_caijifangwutuxiang);
        mButtonCaiJiCheLiangZhaoPian.setText("采集车辆照片");
        mButtonCaiJiCheLiangZhaoPian.setOnClickListener(this);

        findViewById(R.id.button_xiugai).setOnClickListener(this);
        findViewById(R.id.button_shanchu).setOnClickListener(this);
        findViewById(R.id.button_dangqianzuzhuren).setVisibility(View.GONE);
        findViewById(R.id.button_tianjiazuzhuren).setVisibility(View.GONE);

        if (mIsAdd) {
            mLayoutLuRuAnNiu.setVisibility(View.VISIBLE);
            mLayoutXiuGaiAnNiu.setVisibility(View.GONE);
            setEnableModify(true);
        } else {
            mLayoutLuRuAnNiu.setVisibility(View.GONE);
            mLayoutXiuGaiAnNiu.setVisibility(View.VISIBLE);
            setEnableModify(false);
            mLayoutZhaoPian.setEnabled(true);
        }
    }

    /**
     * Set enable to all views
     */
    private void setEnableModify(boolean enabled) {
        if (enabled) {
            CommonUtils.enableViewGroup(mLayoutContainer, true);
            mEditSuoShuPaiChuSuo.setEnabled(false);
            refreshImageViews();
        } else {
            CommonUtils.enableViewGroup(mLayoutContainer, false);
            CommonUtils.enableViewGroup((ViewGroup) mLayoutXiuGaiAnNiu, true);
        }
    }

    /**
     * Check if all fields were filled.
     */
    private boolean checkFields() {
        if (mZhaoPianUriArray.size() == 0) {
            CommonUtils.createErrorAlertDialog(this, "车辆照片必须要！").show();
            return false;
        }

        String idNumber = mEditJiaShiRenShenFenZhengHao.getText().toString();

        if (!TextUtils.isEmpty(idNumber) && !(idNumber.length() == 15 || idNumber.length() == 18)) {
            CommonUtils.createErrorAlertDialog(this, "“" + "身份证号码" + "”必须为15或18位！").show();
            mEditJiaShiRenShenFenZhengHao.requestFocusFromTouch();
            return false;
        }

        return true;
    }

    /**
     * 删除当前来京人登记
     */
    private void onDelete() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage("您真的删除此记录吗？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyContentProvider contentProvider = new MyContentProvider();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ShangChuanColumns.SCMJJH, mOwnerData.MJJH);
                        contentValues.put(ShangChuanColumns.SCMJID, mOwnerData.MJID);
                        contentValues.put(ShangChuanColumns.SCMJXM, mOwnerData.MJXM);
                        contentValues.put(ShangChuanColumns.SCDWID, mOwnerData.SSDWID);
                        contentValues.put(ShangChuanColumns.SCDWMC, mOwnerData.SSDWMC);
                        contentValues.put(ShangChuanColumns.SFSC, true);
                        contentValues.put(ShangChuanColumns.SCSJ, CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));

                        int count = contentProvider.update(CheLiangColumns.CONTENT_URI, contentValues, CheLiangColumns.CLID + " LIKE '" + mSelectedID + "'", null);

                        if (count == 1) {
                            CommonUtils.createErrorAlertDialog(VehicleActivity.this, "删除成功！").show();

                            Dialog dialog = new AlertDialog.Builder(VehicleActivity.this)
                                    .setTitle(R.string.app_name)
                                    .setMessage("删除成功")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    })
                                    .create();
                            dialog.show();

                            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "车辆信息", mSelectedID, "删除车辆信息", "民警");
                        } else {
                            CommonUtils.createErrorAlertDialog(VehicleActivity.this, "删除失败！").show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
    }

    /**
     * 保存当前登记
     */
    private void onSave() {
        if (!checkFields()) return;

        ContentValues contentValues = new ContentValues();

        contentValues.put(CheLiangColumns.CPH, mEditChePaiZhao.getText().toString());

        String str = mSpinnerCheShenYanSe.getSelectedItem().toString();
        if ("请选择".equals(str)) str = null;
        contentValues.put(CheLiangColumns.CSYS, str);

        contentValues.put(CheLiangColumns.PP, mEditPinPai.getText().toString());
        contentValues.put(CheLiangColumns.XH, mEditXingHao.getText().toString());

        str = mSpinnerCheLiangLeiXing.getSelectedItem().toString();
        if ("请选择".equals(str)) str = null;
        contentValues.put(CheLiangColumns.CLLX, str);

        str = mSpinnerHaoPaiZhongLei.getSelectedItem().toString();
        if ("请选择".equals(str)) str = null;
        contentValues.put(CheLiangColumns.HPZL, str);

        contentValues.put(CheLiangColumns.CLMS, mEditCheLiangMiaoShu.getText().toString());
        contentValues.put(CheLiangColumns.JSRXM, mEditJiaShiRenXingMing.getText().toString());
        contentValues.put(CheLiangColumns.JSRSFZH, mEditJiaShiRenShenFenZhengHao.getText().toString());
        contentValues.put(CheLiangColumns.JSRJZHM, mEditJiaShiRenJiaZhaoHaoMa.getText().toString());
        contentValues.put(CheLiangColumns.CLXSZHM, mEditCheLiangXingShiZhengHaoMa.getText().toString());

        str = mSpinnerSuoZaiQuXian.getSelectedItem().toString();
        if ("请选择".equals(str)) str = null;
        contentValues.put(CheLiangColumns.SZQX, str);

        if (TextUtils.isEmpty(mEditSuoZaiJieDao.getText().toString()))
            str = null;
        contentValues.put(CheLiangColumns.SZJD, str);
        contentValues.put(CheLiangColumns.SSPCS, mEditSuoShuPaiChuSuo.getText().toString());
        contentValues.put(CheLiangColumns.XXDZ, mEditXiangXiDiZhi.getText().toString());

        //
        contentValues.put(CheLiangColumns.ZPURL, PhotoUtil.convertUrisToString(mZhaoPianUriArray));

        // latitude and longitude
        if (mTencentGPSTracker != null) {
            contentValues.put(CheLiangColumns.X, String.valueOf(mTencentGPSTracker.mLongitude));
            contentValues.put(CheLiangColumns.Y, String.valueOf(mTencentGPSTracker.mLatitude));
        }

        contentValues.put(ShangChuanColumns.SFJZ, false);
        contentValues.put(ShangChuanColumns.SFSC, false);
        contentValues.put(ShangChuanColumns.LRSB, true);
        contentValues.put(ShangChuanColumns.SFSCZFWQ, false);
        contentValues.put(ShangChuanColumns.SFSCZHL, false);
        contentValues.put(ShangChuanColumns.SFSCZLGB, false);

        MyContentProvider contentProvider = new MyContentProvider();

        String message = "";
        String id;

        if (mIsAdd) {
            Date currentDate = new Date();

            contentValues.put(ShangChuanColumns.LRMJJH, mOwnerData.MJJH);
            contentValues.put(ShangChuanColumns.LRMJID, mOwnerData.MJID);
            contentValues.put(ShangChuanColumns.LRMJXM, mOwnerData.MJXM);
            contentValues.put(ShangChuanColumns.LRDWID, mOwnerData.SSDWID);
            contentValues.put(ShangChuanColumns.LRDWMC, mOwnerData.SSDWMC);
            contentValues.put(ShangChuanColumns.LRSJ, CommonUtils.getFormattedDateString(currentDate, "yyyy-MM-dd HH:mm:ss"));

            mSelectedID = id = mOwnerData.MJID + CommonUtils.getFormattedDateString(currentDate, "yyyyMMddHHmmss");
            contentValues.put(CheLiangColumns.CLID, id);

            contentProvider.insert(CheLiangColumns.CONTENT_URI, contentValues);

            message = "保存成功！";
            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "车辆信息", String.valueOf(id), "添加车辆信息", "民警");
        }

        if (mIsUpdate) {
            contentValues.put(ShangChuanColumns.XGMJJH, mOwnerData.MJJH);
            contentValues.put(ShangChuanColumns.XGMJID, mOwnerData.MJID);
            contentValues.put(ShangChuanColumns.XGMJXM, mOwnerData.MJXM);
            contentValues.put(ShangChuanColumns.XGDWID, mOwnerData.SSDWID);
            contentValues.put(ShangChuanColumns.XGDWMC, mOwnerData.SSDWMC);
            contentValues.put(ShangChuanColumns.XGSJ, CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));

            contentProvider.update(CheLiangColumns.CONTENT_URI, contentValues, CheLiangColumns.CLID + " LIKE '" + mSelectedID + "'", null);

            message = "修改成功！";
            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "车辆信息", mSelectedID, "修改车辆信息", "民警");
        }

        Dialog dialog = new AlertDialog.Builder(VehicleActivity.this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                })
                .create();
        dialog.show();

        if (CommonUtils.checkNetworkEnable(this)) {
            Cursor cursor = contentProvider.query(CheLiangColumns.CONTENT_URI, null,
                    CheLiangColumns.CLID + " LIKE '" + mSelectedID + "'", null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    UploadUtil.uploadNewData(this, CheLiangColumns.CONTENT_TYPE, cursor, false);
                }

                cursor.close();
            }
        }
    }

    /**
     * Load data from database with selected CLID
     */
    private void loadData() {
        MyContentProvider contentProvider = new MyContentProvider();
        Cursor cursor = contentProvider.query(CheLiangColumns.CONTENT_URI, null,
                CheLiangColumns.CLID + " LIKE '" + mSelectedID + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                mEditChePaiZhao.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.CPH)));
                CommonUtils.selectSpinnerItem(mSpinnerCheShenYanSe, cursor.getString(cursor.getColumnIndex(CheLiangColumns.CSYS)));

                mEditPinPai.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.PP)));
                mEditXingHao.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.XH)));

                CommonUtils.selectSpinnerItem(mSpinnerCheLiangLeiXing, cursor.getString(cursor.getColumnIndex(CheLiangColumns.CLLX)));
                CommonUtils.selectSpinnerItem(mSpinnerHaoPaiZhongLei, cursor.getString(cursor.getColumnIndex(CheLiangColumns.HPZL)));

                mEditCheLiangMiaoShu.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.CLMS)));
                mEditJiaShiRenXingMing.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.JSRXM)));
                mEditJiaShiRenShenFenZhengHao.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.JSRSFZH)));
                mEditJiaShiRenJiaZhaoHaoMa.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.JSRJZHM)));
                mEditCheLiangXingShiZhengHaoMa.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.CLXSZHM)));

                CommonUtils.selectSpinnerItem(mSpinnerSuoZaiQuXian, cursor.getString(cursor.getColumnIndex(CheLiangColumns.SZQX)));

                mEditSuoZaiJieDao.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.SZJD)));
                mEditSuoShuPaiChuSuo.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.SSPCS)));
                mEditXiangXiDiZhi.setText(cursor.getString(cursor.getColumnIndex(CheLiangColumns.XXDZ)));

                String path = cursor.getString(cursor.getColumnIndex(CheLiangColumns.ZPURL));
                if (!TextUtils.isEmpty(path)) {
                    mZhaoPianUriArray = PhotoUtil.getUrisFromString(this, path);
                    refreshImageViews();
                }
            }

            cursor.close();
        }
    }

    /**
     * 采集房屋图像
     */
    private void onTakePhoto() {
        try {
            mFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            Log.e(TAG, "mFileUri = " + mFileUri.getPath());

            //create new Intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } catch (ActivityNotFoundException anfe) {
            CommonUtils.createErrorAlertDialog(this, "Alert", "Your device doesn't support capturing images!").show();
        }
    }

    /**
     * Crop image
     */
    private void cropImage() {
        try {
            //create new Intent
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra("crop", "true");
            intent.setDataAndType(mFileUri, "image/*");
            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 4);
            //intent.putExtra("scale", true);
            //intent.putExtra("scaleUpIfNeeded", true);
            //intent.putExtra("outputX", 640); // max value
            //intent.putExtra("outputY", 640);
            intent.putExtra("setWallpaper", false);
            intent.putExtra("return-data", false);
            startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE);
        } catch (ActivityNotFoundException anfe) {
            CommonUtils.createErrorAlertDialog(this, "Alert", "Your device doesn't support capturing images!").show();
        }
    }

    /**
     * Creating file uri to store photoImage/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(CommonUtils.getOutputMediaFile(this, type == MEDIA_TYPE_IMAGE));
    }

    /**
     * Display room photo in ImageViews
     */
    private void refreshImageViews() {
        int count = mZhaoPianUriArray.size();
        for (int i = 0; i < count; i++) {
            //mImageZhaoPians[i].setImageURI(mZhaoPianUriArray.get(i));
            mImageZhaoPians[i].setImageBitmap(CommonUtils.getBitmapFromUri(mZhaoPianUriArray.get(i), 4));
        }
        for (int i = count; i < Config.MAX_VEHICLE_PHOTO_COUNT; i++) {
            mImageZhaoPians[i].setImageDrawable(null);
        }
    }

    private void onPreviewPhoto() {
        int count = mZhaoPianUriArray.size();

        if (count > 0) {
            Intent intent = new Intent(this, PreviewPhotoActivity.class);
            intent.putParcelableArrayListExtra(PreviewPhotoActivity.PHOTO_ID_ARRAY, mZhaoPianUriArray);
            intent.putExtra(PreviewPhotoActivity.ENABLE_DELETE, mIsAdd || mIsUpdate);
            startActivityForResult(intent, PREVIEW_PHOTO_REQUEST_CODE);
        }
    }

}
