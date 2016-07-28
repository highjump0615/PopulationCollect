/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename RentRoomActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangZhuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.IDCardUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.PhotoUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.UploadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;

public class RentRoomActivity extends PermanentActivity implements View.OnClickListener {

    private static final String TAG = RentRoomActivity.class.getSimpleName();
    // String extra preference
    public static final String SELECTED_ROOM_ID = "selected_room_id";
    public static final String SELECTED_RENTER_ID = "selected_renter_id";

    //
    private static final int MEDIA_TYPE_IMAGE = 0;
    private static final int MEDIA_TYPE_VIDEO = 1;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CROP_IMAGE_REQUEST_CODE = 150;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PREVIEW_PHOTO_REQUEST_CODE = 400;
    private static final int PREVIEW_VIDEO_REQUEST_CODE = 500;

    // 变数
    // variables to capture image
    private Uri mFileUri;
    //private Bitmap mBitmap = null;

    private boolean mIsAdd = false;
    private boolean mIsUpdate = false;
    private String mSelectedRoomID = "";
    private String mSelectedRenterID = "";

    private LinkedHashMap<String, String> mGuanLiYuanMap = new LinkedHashMap<String, String>();

    private String mStrGuanLiYuanXingMing = "";
    private String mStrSuoZaiDi_JieDao = "";
    private String mStrXianZhuDiZhi_Shi = "";
    private String mStrHuJiDiZhi_Shi = "";
    private String mStrSuoZaiDi_Qu = "";

    private ArrayList<Uri> mZhaoPianUriArray = new ArrayList<Uri>();
    private Uri mZhaoPianUri = null;
    private Uri mSheXiangUri = null;

    private MediaController mMediaController = null;

    // 定义控制
    // 登记信息
    private Spinner mSpinnerSuoZaiDi_XiaQu;
    private Spinner mSpinnerGuanLiYuanXingMing;
    private EditText mEditGuanLiYuanBianHao;
    private EditText mEditDengJiBiaoXuHao;
    private EditText mEditTianBiaoRiQi;

    // 房屋基本信息
    private Spinner mSpinnerFangWuLeiXing;
    private Spinner mSpinnerJianSheXingZhi;

    private View mLayoutQunZuFangLeiXing;
    private Spinner mSpinnerQunZuFangLeiXing;

    private View mLayoutQiTaFangWuLeiXing;
    private EditText mEditQiTaFangWuLeiXing;
    private View mLayoutQiTaJianSheXingZhi;
    private EditText mEditQiTaJianSheXingZhi;

    private Spinner mSpinnerSuoZaiDiZhi_FenJu;
    private Spinner mSpinnerSuoZaiDiZhi_PaiChuSuo;
    private EditText mEditSuoZaiDiZhi_SheQu;
    private Spinner mSpinnerSuoZaiDiZhi_JieDao;

    private EditText mEditSuoZaiDiXiangZhi;

    private Spinner mSpinnerJianZhuLeiXing;
    private View mLayoutQiTaJianZhuLeiXing;
    private EditText mEditQiTaJianZhuLeiXing;

    private EditText mEditFangChanZhengHao;
    private EditText mEditSuoShuPaiChuSuoMingCheng;
    private EditText mEditMinJingXingMing;

    // 房主信息
    private Spinner mSpinnerSuoYouQuanLeiXing;
    private Spinner mSpinnerChuZuRenLeiXing;

    // 个人房屋信息
    private View mLayoutGeRenFangZhuXinXi;
    private EditText mEditXingMing;

    private Spinner mSpinnerZhengJianLeiBie;
    private View mLayoutQiTaZhengJian;
    private EditText mEditQiTaZhengJian;

    private TextView mTextZhengJianHaoMa;
    private EditText mEditZhengJianHaoMa;

    private Spinner mSpinnerXingBie;

    private EditText mEditChuShengRiQi;

    private Spinner mSpinnerHuJiDi;

    private Spinner mSpinnerZhengZhiMianMao;

    private EditText mEditLianXiDianHua;

    private View mLayoutGuoJi;
    private Spinner mSpinnerGuoJi;

    private Spinner mSpinnerXianZhuDiZhi_Sheng;
    private Spinner mSpinnerXianZhuDiZhi_Shi;
    private EditText mEditXianZhuXiangZhi;

    private View mLayoutHuJiDiZhi;
    private Spinner mSpinnerHuJiDiZhi_Sheng;
    private Spinner mSpinnerHuJiDiZhi_Shi;
    private View mLayoutHuJiDiXiangZhi;
    private EditText mEditHuJiXiangZhi;

    // 单位房屋信息
    private View mLayoutDanWeiFangZhuXinXi;
    private EditText mEditDanWeiMingCheng;

    private EditText mEditFuZeRenXingMing;
    private EditText mEditDanWeiLianXiDianHua;

    private Spinner mSpinnerSuoZaiDiZhi_Sheng;
    private Spinner mSpinnerSuoZaiDiZhi_Qu;
    private EditText mEditDanWeiSuoZaiXiangZhi;

    // 房屋出租信息
    private EditText mEditChuZuJianShu;
    private EditText mEditChuZuPingFangMi;

    private Spinner mSpinnerFangWuCengShu;

    private CheckBox mCheckAnQuanYinHuan_Wu;
    private CheckBox mCheckAnQuanYinHuan_BuWenDingFengXian;
    private CheckBox mCheckAnQuanYinHuan_ZhiAnYinHuan;
    private CheckBox mCheckAnQuanYinHuan_XiaoFangYinHuan;
    private CheckBox mCheckAnQuanYinHuan_JianZhuAnQuan;

    private Spinner mSpinnerChuZuYongTu;
    private View mLayoutQiTaChuZuYongTu;
    private EditText mEditQiTaChuZuYongTu;

    private EditText mEditZuZhuRenYuan_BenShi;
    private EditText mEditZuZhuRenYuan_WaiShengShi;
    private EditText mEditZuZhuRenYuan_GangAoTai;
    private EditText mEditZuZhuRenYuan_WaiJi;

    private RadioGroup mRadioGroupZuJin;
    private EditText mEditZuJin;

    private Spinner mSpinnerZuLinHeTong;
    private Spinner mSpinnerNaShui;
    private Spinner mSpinnerDengJiBeiAn;

    private CheckBox mCheckJianDingZeRenShu_Wu;
    private CheckBox mCheckJianDingZeRenShu_ZhiAn;
    private CheckBox mCheckJianDingZeRenShu_XiaoFang;
    private CheckBox mCheckJianDingZeRenShu_HunYu;

    private EditText mEditJianDingZeShuRiQi;
    private EditText mEditChuZuQiShiRiQi;
    private EditText mEditChuZuJieZhiRiQi;

    // 中介公司信息
    private View mLayoutZhongJieGongSiXinXi;
    private EditText mEditZhongJieGongSiMingCheng;
    private EditText mEditZhongJie_LianXiDianHua;
    private EditText mEditFangWuFuZeRenXingMing;
    private EditText mEditFangWuFuZeRenShenFenZheng;

    // 转租人信息
    private View mLayoutGeRenZhuanZuRenXinXi;
    private EditText mEditZhuanZuRenXingMing;

    private Spinner mSpinnerZhuanZuRen_ZhengJianLeiBie;
    private View mLayoutZhuanZuRen_QiTaZhengJianLeiBie;
    private EditText mEditZhuanZuRen_QiTaZhengJianLeiBie;

    private TextView mTextZhuanZuRen_ZhengJianHaoMa;
    private EditText mEditZhuanZuRen_ZhengJianHaoMa;
    private EditText mEditZhuanZuRen_LianXiDianHua;
    private EditText mEditZhuanZuRen_XianZhuDiZhi;

    // 备注
    private EditText mEditBeiZhu;

    // 房屋图像
    private ImageView mImageZhaoPian;
    private VideoView mVideoSheXiang;

    // 按钮
    private View mLayoutXiuGaiAnNiu;
    private View mLayoutLuRuAnNiu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(SELECTED_ROOM_ID)) {
            mSelectedRoomID = intent.getStringExtra(SELECTED_ROOM_ID);
            mSelectedRenterID = intent.getStringExtra(SELECTED_RENTER_ID);
            mIsAdd = false;
        } else {
            mIsAdd = true;
        }

        super.onCreate(savedInstanceState);
        if (!mIsAdd) loadData();

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Image captured and saved to fileUri specified in the Intent
                    //Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                    //convertImageUriToFile(mFileUri, this);
                    //mBitmap = CommonUtils.adjustBitmap(mFileUri);
                    mZhaoPianUri = mFileUri;
                    mZhaoPianUriArray.add(mZhaoPianUri);
                    refreshImageViews();
                    //cropImage();
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the photoImage capture
                    mZhaoPianUri = null;
                } else {
                    // Image capture failed, advise user
                    mZhaoPianUri = null;
                }
                break;

            case CROP_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Image captured and saved to fileUri specified in the Intent
                    //Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                    //convertImageUriToFile(mFileUri, this);
                    //mBitmap = CommonUtils.adjustBitmap(mFileUri);
                    refreshImageViews();
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the photoImage capture
                } else {
                    // Image capture failed, advise user
                }
                break;

            case CAMERA_CAPTURE_VIDEO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Video captured and saved to fileUri specified in the Intent
                    //Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                    //mBitmapToPost = ThumbnailUtils.createVideoThumbnail(mFileUri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    mSheXiangUri = mFileUri;
                    refreshVideoView();
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the video capture
                    mSheXiangUri = null;
                } else {
                    // Video capture failed, advise user
                    mSheXiangUri = null;
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

            case PREVIEW_VIDEO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(PreviewVideoActivity.VIDEO_PATH);
                    if (TextUtils.isEmpty(path)) {
                        mSheXiangUri = null;
                    } else {
                        mSheXiangUri = Uri.parse(path);
                    }
                    refreshVideoView();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_xiugai:
                mIsUpdate = true;
                mLayoutLuRuAnNiu.setVisibility(View.VISIBLE);
                mLayoutXiuGaiAnNiu.setVisibility(View.GONE);
                setEnableModify(true);
                break;

            case R.id.button_shanchu:
                onDelete();
                break;

            case R.id.button_dangqianzuzhuren:
                onShowCurrentRenters();
                break;

            case R.id.button_tianjiazuzhuren:
                onAddRenters();
                break;

            case R.id.button_baocun:
                onSave();
                break;

            case R.id.button_caijifangwutuxiang:
                if (mZhaoPianUriArray.size() >= Config.MAX_ROOM_PHOTO_COUNT) {
                    Dialog dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.app_name)
                            .setMessage("照片数已经" + Config.MAX_ROOM_PHOTO_COUNT + "个超过了。您删除照片吗？")
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

            case R.id.button_caijifangwushexiang:
                onRecordVideo();
                break;

            case R.id.image_zhaopian:
                onPreviewPhoto();
                break;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();

        View rootView = mLayoutInflater.inflate(R.layout.activity_rent_room, mLayoutContainer);

        CommonUtils.changeForeColorInTextView(this, "*", rootView);

        // 房屋出租信息
        /* 登记信息 */
        mSpinnerSuoZaiDi_XiaQu = (Spinner) findViewById(R.id.spinner_suozaidi_xiaqu);
        mSpinnerGuanLiYuanXingMing = (Spinner) findViewById(R.id.spinner_guanliyuanxingming);
        mEditGuanLiYuanBianHao = (EditText) findViewById(R.id.edit_guanliyuanbianhao);
        mEditDengJiBiaoXuHao = (EditText) findViewById(R.id.edit_dengjibiaoxuhao);
        mEditTianBiaoRiQi = (EditText) findViewById(R.id.edit_tianbiaoriqi);

        final MyContentProvider contentProvider = new MyContentProvider();
        ArrayList<String> spinnerArray = contentProvider.getDistrictNames();
        spinnerArray.add(0, "请选择");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSuoZaiDi_XiaQu.setAdapter(spinnerArrayAdapter);
        mSpinnerSuoZaiDi_XiaQu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String districtName = mSpinnerSuoZaiDi_XiaQu.getSelectedItem().toString();

                if (!"请选择".equals(districtName)) {
                    mEditSuoZaiDiZhi_SheQu.setText(districtName);
                } else {
                    mEditSuoZaiDiZhi_SheQu.setText("");
                }

                ArrayList<String> spinnerArray = new ArrayList<String>();
                spinnerArray.add(0, "请选择");

                if (!"请选择".equals(districtName)) {
                    mGuanLiYuanMap = contentProvider.getManagerInfoMapFromDistrictName(districtName);
                    Set<String> nameSet = mGuanLiYuanMap.keySet();

                    for (String name : nameSet)
                        spinnerArray.add(name);
                }

                // 管理员姓名
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        RentRoomActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerGuanLiYuanXingMing.setAdapter(spinnerArrayAdapter);

                if (!TextUtils.isEmpty(mStrGuanLiYuanXingMing)) {
                    CommonUtils.selectSpinnerItem(mSpinnerGuanLiYuanXingMing, mStrGuanLiYuanXingMing);
                    mSpinnerGuanLiYuanXingMing.setEnabled(false);
                    mStrGuanLiYuanXingMing = "";
                }

                // 所在地社区
                spinnerArray = contentProvider.getStreetNames(districtName);
                spinnerArray.add(0, "请选择");
                spinnerArrayAdapter = new ArrayAdapter<String>(RentRoomActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerSuoZaiDiZhi_JieDao.setAdapter(spinnerArrayAdapter);

                if (!TextUtils.isEmpty(mStrSuoZaiDi_JieDao)) {
                    CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDiZhi_JieDao, mStrSuoZaiDi_JieDao);
                    mSpinnerSuoZaiDiZhi_JieDao.setEnabled(false);
                    mStrSuoZaiDi_JieDao = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerGuanLiYuanXingMing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = mSpinnerGuanLiYuanXingMing.getSelectedItem().toString();

                if ("请选择".equals(name)) {
                    mEditGuanLiYuanBianHao.setText("");
                } else {
                    mEditGuanLiYuanBianHao.setText(mGuanLiYuanMap.get(name));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mEditTianBiaoRiQi.setText(CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd"));
        mEditTianBiaoRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(RentRoomActivity.this, mEditTianBiaoRiQi));

        /* 房屋基本信息 */
        mSpinnerFangWuLeiXing = (Spinner) findViewById(R.id.spinner_fangwuleixing);
        mSpinnerJianSheXingZhi = (Spinner) findViewById(R.id.spinner_jiansherxingzhi);

        mLayoutQunZuFangLeiXing = findViewById(R.id.layout_qunzufangleixing);
        mSpinnerQunZuFangLeiXing = (Spinner) findViewById(R.id.spinner_qunzufangleixing);

        mLayoutQiTaFangWuLeiXing = findViewById(R.id.layout_qitafangwuleixing);
        mEditQiTaFangWuLeiXing = (EditText) findViewById(R.id.edit_qitafangwuleixing);
        mLayoutQiTaJianSheXingZhi = findViewById(R.id.layout_qitajianshexingzhi);
        mEditQiTaJianSheXingZhi = (EditText) findViewById(R.id.edit_qitajianshexingzhi);

        mSpinnerSuoZaiDiZhi_FenJu = (Spinner) findViewById(R.id.spinner_suozaidizhi_fenju);
        mSpinnerSuoZaiDiZhi_FenJu.setEnabled(false);
        spinnerArray = new ArrayList<String>();
        spinnerArray.add(contentProvider.getFenJuNameFromPaiChuSuoID(mOwnerData.SSDWID));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSuoZaiDiZhi_FenJu.setAdapter(spinnerArrayAdapter);

        mSpinnerSuoZaiDiZhi_PaiChuSuo = (Spinner) findViewById(R.id.spinner_suozaidizhi_paichusuo);
        mSpinnerSuoZaiDiZhi_PaiChuSuo.setEnabled(false);
        spinnerArray = new ArrayList<String>();
        spinnerArray.add(mOwnerData.SSDWMC);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSuoZaiDiZhi_PaiChuSuo.setAdapter(spinnerArrayAdapter);
        /*mSpinnerSuoZaiDiZhi_PaiChuSuo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEditSuoShuPaiChuSuoMingCheng.setText(mSpinnerSuoZaiDiZhi_PaiChuSuo.getSelectedItem().toString());

                mEditSuoZaiDiXiangZhi.setText(mSpinnerSuoZaiDiZhi_FenJu.getSelectedItem().toString()
                        + mSpinnerSuoZaiDiZhi_PaiChuSuo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        mEditSuoZaiDiZhi_SheQu = (EditText) findViewById(R.id.edit_suozaidizhi_shequ);
        mEditSuoZaiDiZhi_SheQu.setEnabled(false);
        mSpinnerSuoZaiDiZhi_JieDao = (Spinner) findViewById(R.id.spinner_suozaidizhi_jiedao);

        mEditSuoZaiDiXiangZhi = (EditText) findViewById(R.id.edit_suozaidixiangzhi);

        mSpinnerJianZhuLeiXing = (Spinner) findViewById(R.id.spinner_jianzhuleixing);
        mLayoutQiTaJianZhuLeiXing = findViewById(R.id.layout_qitajianzhuleixing);
        mEditQiTaJianZhuLeiXing = (EditText) findViewById(R.id.edit_qitajianzhuleixing);

        mEditFangChanZhengHao = (EditText) findViewById(R.id.edit_fangchanzhenghao);
        mEditSuoShuPaiChuSuoMingCheng = (EditText) findViewById(R.id.edit_suoshupaichusuomingcheng);
        mEditSuoShuPaiChuSuoMingCheng.setText(mOwnerData.SSDWMC);
        mEditMinJingXingMing = (EditText) findViewById(R.id.edit_minjingxingming);
        mEditMinJingXingMing.setText(mOwnerData.MJXM);

        //
        mSpinnerFangWuLeiXing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_room_type);
                if (types[position].equals("群租房类")) {
                    mLayoutQunZuFangLeiXing.setVisibility(View.VISIBLE);
                } else {
                    mLayoutQunZuFangLeiXing.setVisibility(View.GONE);
                }

                if (types[position].equals("其它类")) {
                    mLayoutQiTaFangWuLeiXing.setVisibility(View.VISIBLE);
                } else {
                    mLayoutQiTaFangWuLeiXing.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerJianSheXingZhi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_construction_type);
                if (types[position].equals("其他")) {
                    mLayoutQiTaJianSheXingZhi.setVisibility(View.VISIBLE);
                } else {
                    mLayoutQiTaJianSheXingZhi.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerJianZhuLeiXing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_building_type);
                if (types[position].equals("其他")) {
                    mLayoutQiTaJianZhuLeiXing.setVisibility(View.VISIBLE);
                } else {
                    mLayoutQiTaJianZhuLeiXing.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* 房主信息 */
        mSpinnerSuoYouQuanLeiXing = (Spinner) findViewById(R.id.spinner_suoyouquanleixing);
        mSpinnerChuZuRenLeiXing = (Spinner) findViewById(R.id.spinner_chuzurenleixing);

        // 个人房屋信息
        mLayoutGeRenFangZhuXinXi = findViewById(R.id.layout_gerenfangzhuxinxi);
        mEditXingMing = (EditText) findViewById(R.id.edit_xingming);

        mSpinnerZhengJianLeiBie = (Spinner) findViewById(R.id.spinner_zhengjianleibie);
        mLayoutQiTaZhengJian = findViewById(R.id.layout_qitazhengjian);
        mEditQiTaZhengJian = (EditText) findViewById(R.id.edit_qitazhengjian);

        mTextZhengJianHaoMa = (TextView) findViewById(R.id.text_zhengjianhaoma);
        mEditZhengJianHaoMa = (EditText) findViewById(R.id.edit_zhengjianhaoma);
        mEditZhengJianHaoMa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mSpinnerZhengJianLeiBie.getSelectedItem().toString().equals("居民身份证")) {
                    String idNumber = mEditZhengJianHaoMa.getText().toString();

                    if (TextUtils.isEmpty(idNumber)) {
                        CommonUtils.createErrorAlertDialog(RentRoomActivity.this,
                                "“" + "身份证号码" + "”不能为空，请将信息补充完整！").show();
                    } else {
                        idNumber = IDCardUtil.verifyCardID(RentRoomActivity.this, idNumber, "房主");
                        if (idNumber != null) {
                            setPersonInformationFromID(idNumber);
                        }
                    }
                }
            }
        });

        mSpinnerXingBie = (Spinner) findViewById(R.id.spinner_xingbie);

        mEditChuShengRiQi = (EditText) findViewById(R.id.edit_chushengriqi);

        mSpinnerHuJiDi = (Spinner) findViewById(R.id.spinner_hujidi);
        mSpinnerZhengZhiMianMao = (Spinner) findViewById(R.id.spinner_zhengzhimianmao);

        mEditLianXiDianHua = (EditText) findViewById(R.id.edit_lianxidianhua);

        mLayoutGuoJi = findViewById(R.id.layout_guoji);
        mSpinnerGuoJi = (Spinner) findViewById(R.id.spinner_guoji);

        mSpinnerXianZhuDiZhi_Sheng = (Spinner) findViewById(R.id.spinner_xianzhudizhi_sheng);
        mSpinnerXianZhuDiZhi_Shi = (Spinner) findViewById(R.id.spinner_xianzhudizhi_shi);
        mEditXianZhuXiangZhi = (EditText) findViewById(R.id.edit_xianzhuxiangzhi);

        mLayoutHuJiDiZhi = findViewById(R.id.layout_hujidizhi);
        mSpinnerHuJiDiZhi_Sheng = (Spinner) findViewById(R.id.spinner_hujidizhi_sheng);
        mSpinnerHuJiDiZhi_Shi = (Spinner) findViewById(R.id.spinner_hujidizhi_shi);
        mLayoutHuJiDiXiangZhi = findViewById(R.id.layout_hujidixiangzhi);
        mEditHuJiXiangZhi = (EditText) findViewById(R.id.edit_hujixiangzhi);

        // 单位房屋信息
        mLayoutDanWeiFangZhuXinXi = findViewById(R.id.layout_danweifangzhuxinxi);
        mEditDanWeiMingCheng = (EditText) findViewById(R.id.edit_danweimingcheng);

        mEditFuZeRenXingMing = (EditText) findViewById(R.id.edit_fuzerenxingming);
        mEditDanWeiLianXiDianHua = (EditText) findViewById(R.id.edit_danwei_lianxidianhua);

        mSpinnerSuoZaiDiZhi_Sheng = (Spinner) findViewById(R.id.spinner_suozaidizhi_sheng);
        mSpinnerSuoZaiDiZhi_Qu = (Spinner) findViewById(R.id.spinner_suozaidizhi_qu);
        mEditDanWeiSuoZaiXiangZhi = (EditText) findViewById(R.id.edit_danwei_suozaidixiangzhi);

        //
        mSpinnerSuoYouQuanLeiXing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_ownership_type);
                if (types[position].equals("单位")) {
                    mLayoutGeRenFangZhuXinXi.setVisibility(View.GONE);
                    mLayoutDanWeiFangZhuXinXi.setVisibility(View.VISIBLE);
                } else {
                    mLayoutGeRenFangZhuXinXi.setVisibility(View.VISIBLE);
                    mLayoutDanWeiFangZhuXinXi.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerChuZuRenLeiXing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_renter_type);
                if (types[position].equals("中介公司")) {
                    mLayoutZhongJieGongSiXinXi.setVisibility(View.VISIBLE);
                } else {
                    mLayoutZhongJieGongSiXinXi.setVisibility(View.GONE);
                }

                if (types[position].equals("个人转租")) {
                    mLayoutGeRenZhuanZuRenXinXi.setVisibility(View.VISIBLE);
                } else {
                    mLayoutGeRenZhuanZuRenXinXi.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerZhengJianLeiBie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_certificate_type);
                if (types[position].equals("其他")) {
                    mLayoutQiTaZhengJian.setVisibility(View.VISIBLE);
                } else {
                    mLayoutQiTaZhengJian.setVisibility(View.GONE);
                }
                if (types[position].equals("居民身份证")) {
                    mTextZhengJianHaoMa.setText("*证件号码");
                } else {
                    mTextZhengJianHaoMa.setText("证件号码");
                }
                CommonUtils.changeForeColorInTextView(RentRoomActivity.this, "*", mLayoutContainer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerHuJiDi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_domicile_type);
                if (types[position].equals("外籍")) {
                    mLayoutGuoJi.setVisibility(View.VISIBLE);
                    mLayoutHuJiDiZhi.setVisibility(View.GONE);
                    mLayoutHuJiDiXiangZhi.setVisibility(View.GONE);
                } else {
                    mLayoutGuoJi.setVisibility(View.GONE);
                    mLayoutHuJiDiZhi.setVisibility(View.VISIBLE);
                    mLayoutHuJiDiXiangZhi.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerArray = contentProvider.getSheng();
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerXianZhuDiZhi_Sheng.setAdapter(spinnerArrayAdapter);
        mSpinnerXianZhuDiZhi_Sheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sheng = mSpinnerXianZhuDiZhi_Sheng.getSelectedItem().toString();

                ArrayList<String> spinnerArray = new ArrayList<String>();

                if (!"请选择".equals(sheng)) {
                    spinnerArray = contentProvider.getShi(sheng);
                }

                spinnerArray.add(0, "请选择");
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        RentRoomActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerXianZhuDiZhi_Shi.setAdapter(spinnerArrayAdapter);

                if (!TextUtils.isEmpty(mStrXianZhuDiZhi_Shi)) {
                    CommonUtils.selectSpinnerItem(mSpinnerXianZhuDiZhi_Shi, mStrXianZhuDiZhi_Shi);
                    mStrXianZhuDiZhi_Shi = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerArray = new ArrayList<String>();
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerXianZhuDiZhi_Shi.setAdapter(spinnerArrayAdapter);

        spinnerArray = contentProvider.getSheng();
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerHuJiDiZhi_Sheng.setAdapter(spinnerArrayAdapter);
        mSpinnerHuJiDiZhi_Sheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sheng = mSpinnerHuJiDiZhi_Sheng.getSelectedItem().toString();

                ArrayList<String> spinnerArray = new ArrayList<String>();

                if (!"请选择".equals(sheng)) {
                    spinnerArray = contentProvider.getShi(sheng);
                }

                spinnerArray.add(0, "请选择");
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        RentRoomActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerHuJiDiZhi_Shi.setAdapter(spinnerArrayAdapter);

                if (!TextUtils.isEmpty(mStrHuJiDiZhi_Shi)) {
                    CommonUtils.selectSpinnerItem(mSpinnerHuJiDiZhi_Shi, mStrHuJiDiZhi_Shi);
                    mStrHuJiDiZhi_Shi = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerArray = new ArrayList<String>();
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerHuJiDiZhi_Shi.setAdapter(spinnerArrayAdapter);

        spinnerArray = contentProvider.getSheng();
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSuoZaiDiZhi_Sheng.setAdapter(spinnerArrayAdapter);
        mSpinnerSuoZaiDiZhi_Sheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sheng = mSpinnerSuoZaiDiZhi_Sheng.getSelectedItem().toString();

                ArrayList<String> spinnerArray = new ArrayList<String>();

                if (!"请选择".equals(sheng)) {
                    spinnerArray = contentProvider.getShi(sheng);
                }

                spinnerArray.add(0, "请选择");
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        RentRoomActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerSuoZaiDiZhi_Qu.setAdapter(spinnerArrayAdapter);

                if (!TextUtils.isEmpty(mStrSuoZaiDi_Qu)) {
                    CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDiZhi_Qu, mStrSuoZaiDi_Qu);
                    mStrSuoZaiDi_Qu = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerArray = new ArrayList<String>();
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSuoZaiDiZhi_Qu.setAdapter(spinnerArrayAdapter);

        mEditChuShengRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(RentRoomActivity.this, mEditChuShengRiQi));

        /* 房屋出租信息 */
        mEditChuZuJianShu = (EditText) findViewById(R.id.edit_chuzujianshu);
        mEditChuZuPingFangMi = (EditText) findViewById(R.id.edit_chuzupingfangmi);

        mSpinnerFangWuCengShu = (Spinner) findViewById(R.id.spinner_fangwucengshu);

        mSpinnerChuZuYongTu = (Spinner) findViewById(R.id.spinner_chuzuyongtu);
        mLayoutQiTaChuZuYongTu = findViewById(R.id.layout_qitachuzuyongtu);
        mEditQiTaChuZuYongTu = (EditText) findViewById(R.id.edit_qitachuzuyongtu);

        mEditZuZhuRenYuan_BenShi = (EditText) findViewById(R.id.edit_zuzhurenyuan_benshi);
        mEditZuZhuRenYuan_WaiShengShi = (EditText) findViewById(R.id.edit_zuzhurenyuan_waishengshi);
        mEditZuZhuRenYuan_GangAoTai = (EditText) findViewById(R.id.edit_zuzhurenyuan_gangaotai);
        mEditZuZhuRenYuan_WaiJi = (EditText) findViewById(R.id.edit_zuzhurenyuan_waiji);

        mRadioGroupZuJin = (RadioGroup) findViewById(R.id.radio_group_zujin);
        mEditZuJin = (EditText) findViewById(R.id.edit_zujin);

        mSpinnerZuLinHeTong = (Spinner) findViewById(R.id.spinner_zulinhetong);
        mSpinnerNaShui = (Spinner) findViewById(R.id.spinner_nashui);
        mSpinnerDengJiBeiAn = (Spinner) findViewById(R.id.spinner_dengjibeian);

        mCheckJianDingZeRenShu_Wu = (CheckBox) findViewById(R.id.check_jiandingzerenshu_wu);
        mCheckJianDingZeRenShu_ZhiAn = (CheckBox) findViewById(R.id.check_jiandingzerenshu_zhian);
        mCheckJianDingZeRenShu_XiaoFang = (CheckBox) findViewById(R.id.check_jiandingzerenshu_xiaofang);
        mCheckJianDingZeRenShu_HunYu = (CheckBox) findViewById(R.id.check_jiandingzerenshu_hunyu);

        mEditJianDingZeShuRiQi = (EditText) findViewById(R.id.edit_jiandingzerenshuriqi);
        mEditChuZuQiShiRiQi = (EditText) findViewById(R.id.edit_chuzuqishiriqi);
        mEditChuZuJieZhiRiQi = (EditText) findViewById(R.id.edit_chuzujiezhiriqi);

        // 中介公司信息
        mLayoutZhongJieGongSiXinXi = findViewById(R.id.layout_zhongjiegongsixinxi);
        mEditZhongJieGongSiMingCheng = (EditText) findViewById(R.id.edit_zhongjiegongsimingcheng);
        mEditZhongJie_LianXiDianHua = (EditText) findViewById(R.id.edit_zhongjie_lianxidianhua);
        mEditFangWuFuZeRenXingMing = (EditText) findViewById(R.id.edit_fangwufuzerenxingming);
        mEditFangWuFuZeRenShenFenZheng = (EditText) findViewById(R.id.edit_fangwufuzerenshenfenzheng);

        // 转租人信息
        mLayoutGeRenZhuanZuRenXinXi = findViewById(R.id.layout_gerenzhuanzhurenxinxi);
        mEditZhuanZuRenXingMing = (EditText) findViewById(R.id.edit_zhuanzurenxingming);

        mSpinnerZhuanZuRen_ZhengJianLeiBie = (Spinner) findViewById(R.id.spinner_zhuanzuren_zhengjianleibie);
        mLayoutZhuanZuRen_QiTaZhengJianLeiBie = findViewById(R.id.layout_zhuanzuren_qitazhengjianleibie);
        mEditZhuanZuRen_QiTaZhengJianLeiBie = (EditText) findViewById(R.id.edit_zhuanzuren_qitazhengjianleibie);

        mTextZhuanZuRen_ZhengJianHaoMa = (TextView) findViewById(R.id.text_zhuanzuren_zhengjianhaoma);
        mEditZhuanZuRen_ZhengJianHaoMa = (EditText) findViewById(R.id.edit_zhuanzuren_zhengjianhaoma);
        mEditZhuanZuRen_LianXiDianHua = (EditText) findViewById(R.id.edit_zhuanzuren_lianxidianhua);
        mEditZhuanZuRen_XianZhuDiZhi = (EditText) findViewById(R.id.edit_zhuanzuren_xianzhudizhi);

        //
        spinnerArray = new ArrayList<String>();
        spinnerArray.add("请选择");
        for (int i = 1; i <= 50; i++)
            spinnerArray.add(String.valueOf(i));
        ArrayAdapter<String> cengArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        cengArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerFangWuCengShu.setAdapter(cengArrayAdapter);

        mSpinnerChuZuYongTu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_rental_purpose_type);
                if (types[position].equals("其他")) {
                    mLayoutQiTaChuZuYongTu.setVisibility(View.VISIBLE);
                } else {
                    mLayoutQiTaChuZuYongTu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCheckAnQuanYinHuan_Wu = (CheckBox) findViewById(R.id.check_anquanyinhuan_wu);
        mCheckAnQuanYinHuan_BuWenDingFengXian = (CheckBox) findViewById(R.id.check_buwendingfengxian);
        mCheckAnQuanYinHuan_ZhiAnYinHuan = (CheckBox) findViewById(R.id.check_zhianyinhuan);
        mCheckAnQuanYinHuan_XiaoFangYinHuan = (CheckBox) findViewById(R.id.check_xiaofangyinhuan);
        mCheckAnQuanYinHuan_JianZhuAnQuan = (CheckBox) findViewById(R.id.check_jianzhuanquan);

        mCheckAnQuanYinHuan_Wu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckAnQuanYinHuan_BuWenDingFengXian.setChecked(false);
                    mCheckAnQuanYinHuan_ZhiAnYinHuan.setChecked(false);
                    mCheckAnQuanYinHuan_XiaoFangYinHuan.setChecked(false);
                    mCheckAnQuanYinHuan_JianZhuAnQuan.setChecked(false);
                }
            }
        });
        mCheckAnQuanYinHuan_BuWenDingFengXian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckAnQuanYinHuan_Wu.setChecked(false);
            }
        });
        mCheckAnQuanYinHuan_ZhiAnYinHuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckAnQuanYinHuan_Wu.setChecked(false);
            }
        });
        mCheckAnQuanYinHuan_XiaoFangYinHuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckAnQuanYinHuan_Wu.setChecked(false);
            }
        });
        mCheckAnQuanYinHuan_JianZhuAnQuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckAnQuanYinHuan_Wu.setChecked(false);
            }
        });

        mRadioGroupZuJin.check(R.id.radio_zujin_yue);

        mCheckJianDingZeRenShu_Wu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckJianDingZeRenShu_ZhiAn.setChecked(false);
                    mCheckJianDingZeRenShu_XiaoFang.setChecked(false);
                    mCheckJianDingZeRenShu_HunYu.setChecked(false);
                }
            }
        });
        mCheckJianDingZeRenShu_ZhiAn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckJianDingZeRenShu_Wu.setChecked(false);
            }
        });
        mCheckJianDingZeRenShu_XiaoFang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckJianDingZeRenShu_Wu.setChecked(false);
            }
        });
        mCheckJianDingZeRenShu_HunYu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckJianDingZeRenShu_Wu.setChecked(false);
            }
        });

        mSpinnerZhuanZuRen_ZhengJianLeiBie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = getResources().getStringArray(R.array.array_certificate_type);
                if (types[position].equals("其他")) {
                    mLayoutZhuanZuRen_QiTaZhengJianLeiBie.setVisibility(View.VISIBLE);
                } else {
                    mLayoutZhuanZuRen_QiTaZhengJianLeiBie.setVisibility(View.GONE);
                }

                if (types[position].equals("居民身份证")) {
                    mTextZhuanZuRen_ZhengJianHaoMa.setText("*证件号码");
                } else {
                    mTextZhuanZuRen_ZhengJianHaoMa.setText("证件号码");
                }
                CommonUtils.changeForeColorInTextView(RentRoomActivity.this, "*", mLayoutContainer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mEditJianDingZeShuRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(RentRoomActivity.this, mEditJianDingZeShuRiQi));
        mEditChuZuQiShiRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(RentRoomActivity.this, mEditChuZuQiShiRiQi));
        mEditChuZuJieZhiRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(RentRoomActivity.this, mEditChuZuJieZhiRiQi));

        /* 备注 */
        mEditBeiZhu = (EditText) findViewById(R.id.edit_beizhu);

        /* 房屋图像 */
        mImageZhaoPian = (ImageView) findViewById(R.id.image_zhaopian);
        mImageZhaoPian.setOnClickListener(this);
        mVideoSheXiang = (VideoView) findViewById(R.id.video_shexiang);

        /* 按钮 */
        mLayoutLuRuAnNiu = findViewById(R.id.layout_luru_anniu);
        mLayoutXiuGaiAnNiu = findViewById(R.id.layout_xiugai_anniu);

        findViewById(R.id.button_baocun).setOnClickListener(this);
        findViewById(R.id.button_caijifangwutuxiang).setOnClickListener(this);
        findViewById(R.id.button_caijifangwushexiang).setOnClickListener(this);

        findViewById(R.id.button_xiugai).setOnClickListener(this);
        findViewById(R.id.button_shanchu).setOnClickListener(this);
        findViewById(R.id.button_dangqianzuzhuren).setOnClickListener(this);
        findViewById(R.id.button_tianjiazuzhuren).setOnClickListener(this);

        if (mIsAdd) {
            mLayoutLuRuAnNiu.setVisibility(View.VISIBLE);
            mLayoutXiuGaiAnNiu.setVisibility(View.GONE);
            setEnableModify(true);
        } else {
            mLayoutLuRuAnNiu.setVisibility(View.GONE);
            mLayoutXiuGaiAnNiu.setVisibility(View.VISIBLE);
            setEnableModify(false);
        }
    }

    /**
     * Load data from database with selected FWID
     */
    private void loadData() {
        MyContentProvider contentProvider = new MyContentProvider();
        Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI, null,
                FangWuColumns.FWID + " LIKE '" + mSelectedRoomID + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                // 登记人员信息
                CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDi_XiaQu, cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXQ)));
                mStrGuanLiYuanXingMing = cursor.getString(cursor.getColumnIndex(FangWuColumns.GLYXM));
                mEditGuanLiYuanBianHao.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.GLYBH)));
                mEditDengJiBiaoXuHao.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(FangWuColumns.FWID))));
                mEditTianBiaoRiQi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.TBRQ)));

                ContentValues fangwuValues = new ContentValues();

                // 房屋基本信息
                CommonUtils.selectSpinnerItem(mSpinnerFangWuLeiXing, cursor.getString(cursor.getColumnIndex(FangWuColumns.FWLX)));
                CommonUtils.selectSpinnerItem(mSpinnerJianSheXingZhi, cursor.getString(cursor.getColumnIndex(FangWuColumns.JSXZ)));

                CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDiZhi_FenJu, cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDZFJ)));
                CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDiZhi_PaiChuSuo, cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDZPCS)));

                mEditSuoZaiDiZhi_SheQu.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXZSQ)));
                mStrSuoZaiDi_JieDao = cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXZJD));
                mEditSuoZaiDiXiangZhi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXZZT)));

                CommonUtils.selectSpinnerItem(mSpinnerJianZhuLeiXing, cursor.getString(cursor.getColumnIndex(FangWuColumns.JZLX)));
                mEditFangChanZhengHao.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.FCZH)));

                mEditSuoShuPaiChuSuoMingCheng.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.SSPCSMC)));
                mEditMinJingXingMing.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.MJXM)));

                String string = cursor.getString(cursor.getColumnIndex(FangWuColumns.FWLX));
                if ("群租房类".equals(cursor.getString(cursor.getColumnIndex(FangWuColumns.FWLX)))) {
                    mLayoutQunZuFangLeiXing.setVisibility(View.VISIBLE);
                    CommonUtils.selectSpinnerItem(mSpinnerQunZuFangLeiXing, cursor.getString(cursor.getColumnIndex(FangWuColumns.QZFLX)));
                }

                if ("其它类".equals(string)) {
                    mLayoutQiTaFangWuLeiXing.setVisibility(View.VISIBLE);
                    mEditQiTaFangWuLeiXing.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.QTFWLX)));
                }

                if ("其他".equals(cursor.getString(cursor.getColumnIndex(FangWuColumns.JSXZ)))) {
                    mLayoutQiTaJianSheXingZhi.setVisibility(View.VISIBLE);
                    mEditQiTaJianSheXingZhi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.QTJSXZ)));
                }

                if ("其他".equals(cursor.getString(cursor.getColumnIndex(FangWuColumns.JZLX)))) {
                    mLayoutQiTaJianZhuLeiXing.setVisibility(View.VISIBLE);
                    mEditQiTaJianZhuLeiXing.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.QTJZLX)));
                }

                // 检查房主信息
                CommonUtils.selectSpinnerItem(mSpinnerSuoYouQuanLeiXing, cursor.getString(cursor.getColumnIndex(FangWuColumns.SYQLX)));
                CommonUtils.selectSpinnerItem(mSpinnerChuZuRenLeiXing, cursor.getString(cursor.getColumnIndex(FangWuColumns.CZRLX)));

                if ("单位".equals(cursor.getString(cursor.getColumnIndex(FangWuColumns.SYQLX)))) {
                    mLayoutDanWeiFangZhuXinXi.setVisibility(View.VISIBLE);
                    mLayoutGeRenFangZhuXinXi.setVisibility(View.GONE);

                    mEditDanWeiMingCheng.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.DWMC)));
                    mEditFuZeRenXingMing.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.FZRXM)));
                    mEditDanWeiLianXiDianHua.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.DWLXDH)));
                    mEditDanWeiSuoZaiXiangZhi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXXDZ)));
                    CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDiZhi_Sheng, cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDZS)));
                    mStrSuoZaiDi_Qu = cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDZSHI));
                } else {
                    mLayoutDanWeiFangZhuXinXi.setVisibility(View.GONE);
                    mLayoutGeRenFangZhuXinXi.setVisibility(View.VISIBLE);

                    mEditXingMing.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.XM)));
                    CommonUtils.selectSpinnerItem(mSpinnerZhengJianLeiBie, cursor.getString(cursor.getColumnIndex(FangWuColumns.ZJLB)));
                    mEditZhengJianHaoMa.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZJHM)));
                    CommonUtils.selectSpinnerItem(mSpinnerXingBie, cursor.getString(cursor.getColumnIndex(FangWuColumns.XB)));
                    mEditChuShengRiQi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.CSRQ)));
                    CommonUtils.selectSpinnerItem(mSpinnerHuJiDi, cursor.getString(cursor.getColumnIndex(FangWuColumns.HJD)));
                    CommonUtils.selectSpinnerItem(mSpinnerZhengZhiMianMao, cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZMM)));
                    mEditLianXiDianHua.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.LXDH)));

                    if (mSpinnerZhengJianLeiBie.getSelectedItem().toString().equals("其他")) {
                        mLayoutQiTaZhengJian.setVisibility(View.VISIBLE);
                        mEditQiTaZhengJian.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.QTZJ)));
                    }

                    if (mSpinnerHuJiDi.getSelectedItem().toString().equals("外籍")) {
                        mLayoutHuJiDiZhi.setVisibility(View.VISIBLE);
                        CommonUtils.selectSpinnerItem(mSpinnerGuoJi, cursor.getString(cursor.getColumnIndex(FangWuColumns.GJDQ)));
                        fangwuValues.put(FangWuColumns.GJDQ, mSpinnerGuoJi.getSelectedItem().toString());
                    } else {
                        CommonUtils.selectSpinnerItem(mSpinnerHuJiDiZhi_Sheng, cursor.getString(cursor.getColumnIndex(FangWuColumns.HJDZS)));
                        mStrHuJiDiZhi_Shi = cursor.getString(cursor.getColumnIndex(FangWuColumns.HJDZSHI));
                        mEditHuJiXiangZhi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.HJDXXDZ)));
                    }

                    CommonUtils.selectSpinnerItem(mSpinnerXianZhuDiZhi_Sheng, cursor.getString(cursor.getColumnIndex(FangWuColumns.XZDZS)));
                    mStrXianZhuDiZhi_Shi = cursor.getString(cursor.getColumnIndex(FangWuColumns.XZDZSHI));
                    mEditXianZhuXiangZhi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.XJZDXXDZ)));
                }

                //
                if (mSpinnerChuZuRenLeiXing.getSelectedItem().toString().equals("中介公司")) {
                    mLayoutZhongJieGongSiXinXi.setVisibility(View.VISIBLE);

                    mEditZhongJieGongSiMingCheng.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZJGSMC)));
                    mEditZhongJie_LianXiDianHua.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZJGSLXDH)));
                    mEditFangWuFuZeRenXingMing.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.FWFZRXM)));
                    mEditFangWuFuZeRenShenFenZheng.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.FWFZRSFZ)));
                } else if (mSpinnerChuZuRenLeiXing.getSelectedItem().toString().equals("个人转租")) {
                    mLayoutGeRenZhuanZuRenXinXi.setVisibility(View.VISIBLE);

                    mEditZhuanZuRenXingMing.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRXM)));
                    CommonUtils.selectSpinnerItem(mSpinnerZhuanZuRen_ZhengJianLeiBie, cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRZJLB)));

                    if (mSpinnerZhuanZuRen_ZhengJianLeiBie.getSelectedItem().toString().equals("其他")) {
                        mLayoutZhuanZuRen_QiTaZhengJianLeiBie.setVisibility(View.VISIBLE);
                        mEditZhuanZuRen_QiTaZhengJianLeiBie.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRQTZJLB)));
                    }

                    mEditZhuanZuRen_ZhengJianHaoMa.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRZJHM)));
                    mEditZhuanZuRen_LianXiDianHua.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRLXDH)));
                    mEditZhuanZuRen_XianZhuDiZhi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRXZDZ)));
                }

                // 检查房屋出租信息
                mEditChuZuJianShu.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.CZJS)));
                mEditChuZuPingFangMi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.CZMJ)));
                CommonUtils.selectSpinnerItem(mSpinnerFangWuCengShu, cursor.getString(cursor.getColumnIndex(FangWuColumns.FWCS)));

                string = cursor.getString(cursor.getColumnIndex(FangWuColumns.AQYH));
                if (!TextUtils.isEmpty(string)) {
                    if ("无".equals(string.trim())) {
                        mCheckAnQuanYinHuan_Wu.setChecked(true);
                    } else {
                        String[] checks = string.split(" ");

                        if (checks.length > 0) {
                            for (String check : checks) {
                                if ("不稳定风险".trim().equals(check)) {
                                    mCheckAnQuanYinHuan_BuWenDingFengXian.setChecked(true);
                                } else if ("治安隐患".trim().equals(check)) {
                                    mCheckAnQuanYinHuan_ZhiAnYinHuan.setChecked(true);
                                } else if ("消防隐患".trim().equals(check)) {
                                    mCheckAnQuanYinHuan_XiaoFangYinHuan.setChecked(true);
                                } else if ("建筑安全".trim().equals(check)) {
                                    mCheckAnQuanYinHuan_JianZhuAnQuan.setChecked(true);
                                }
                            }
                        }
                    }
                }

                CommonUtils.selectSpinnerItem(mSpinnerChuZuYongTu, cursor.getString(cursor.getColumnIndex(FangWuColumns.CZYT)));
                if (mSpinnerChuZuYongTu.getSelectedItem().toString().equals("其他")) {
                    mLayoutQiTaChuZuYongTu.setVisibility(View.VISIBLE);
                    mEditQiTaChuZuYongTu.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.QTCZYT)));
                }

                mEditZuZhuRenYuan_BenShi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRYSLBS)));
                mEditZuZhuRenYuan_WaiShengShi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRYSLWSS)));
                mEditZuZhuRenYuan_GangAoTai.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRYSLGAT)));
                mEditZuZhuRenYuan_WaiJi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZZRYSLWJ)));

                if ("年".equals(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZJFS)))) {
                    mRadioGroupZuJin.check(R.id.radio_zujin_nian);
                } else {
                    mRadioGroupZuJin.check(R.id.radio_zujin_yue);
                }
                mEditZuJin.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.ZJ)));

                CommonUtils.selectSpinnerItem(mSpinnerZuLinHeTong, cursor.getString(cursor.getColumnIndex(FangWuColumns.ZLHT)));
                CommonUtils.selectSpinnerItem(mSpinnerNaShui, cursor.getString(cursor.getColumnIndex(FangWuColumns.NS)));
                CommonUtils.selectSpinnerItem(mSpinnerDengJiBeiAn, cursor.getString(cursor.getColumnIndex(FangWuColumns.DJBA)));

                string = cursor.getString(cursor.getColumnIndex(FangWuColumns.QDZRS));
                if (!TextUtils.isEmpty(string)) {
                    if ("无".equals(string.trim())) {
                        mCheckJianDingZeRenShu_Wu.setChecked(true);
                    } else {
                        String[] checks = string.split(" ");

                        if (checks.length > 0) {
                            for (String check : checks) {
                                if ("治安".trim().equals(check)) {
                                    mCheckJianDingZeRenShu_ZhiAn.setChecked(true);
                                } else if ("消防".trim().equals(check)) {
                                    mCheckJianDingZeRenShu_XiaoFang.setChecked(true);
                                } else if ("婚育".trim().equals(check)) {
                                    mCheckJianDingZeRenShu_HunYu.setChecked(true);
                                }
                            }
                        }
                    }
                }

                mEditJianDingZeShuRiQi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.QDZRSRQ)));
                mEditChuZuQiShiRiQi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.CZQSRQ)));
                mEditChuZuJieZhiRiQi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.CZJZRQ)));

                // 备注
                mEditBeiZhu.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.BZ)));

                // 房屋图像
                String path = cursor.getString(cursor.getColumnIndex(FangWuColumns.ZPURL));
                if (!TextUtils.isEmpty(path)) {
                    mZhaoPianUriArray = PhotoUtil.getUrisFromString(this, path);
                    refreshImageViews();
                }

                path = cursor.getString(cursor.getColumnIndex(FangWuColumns.SXURL));
                if (!TextUtils.isEmpty(path)) {
                    mSheXiangUri = Uri.parse(CommonUtils.getMyApplicationDirectory(this, Config.IMAGE_DIRECTORY_NAME) + File.separator + path);
                    refreshVideoView();
                }

                /*byte[] bytes = cursor.getBlob(cursor.getColumnIndex(FangWuColumns.ZPURL));
                if (bytes != null) {
                    mImageZhaoPian.setImageBitmap(CommonUtils.getBitmapFromBytes(bytes));
                }

                bytes = cursor.getBlob(cursor.getColumnIndex(FangWuColumns.SXURL));
                if (bytes != null) {
                    mVideoSheXiang.setImageBitmap(CommonUtils.getBitmapFromBytes(bytes));
                }*/
            }

            cursor.close();
        }
    }

    /**
     * Set enable/disable view group
     */
    private void setEnableModify(boolean enabled) {
        if (enabled) {
            CommonUtils.enableViewGroup(mLayoutContainer, true);

            mEditGuanLiYuanBianHao.setEnabled(false);
            mEditDengJiBiaoXuHao.setEnabled(false);
            mSpinnerSuoZaiDiZhi_FenJu.setEnabled(false);
            mSpinnerSuoZaiDiZhi_PaiChuSuo.setEnabled(false);
            mEditSuoZaiDiZhi_SheQu.setEnabled(false);
            mEditSuoShuPaiChuSuoMingCheng.setEnabled(false);
            mEditMinJingXingMing.setEnabled(false);

            refreshImageViews();

            if (mSheXiangUri != null)
                findViewById(R.id.button_caijifangwushexiang).setEnabled(true);
        } else {
            CommonUtils.enableViewGroup(mLayoutContainer, false);
            CommonUtils.enableViewGroup((ViewGroup) mLayoutXiuGaiAnNiu, true);
        }
    }

    /**
     * 显示当前租住人
     */
    private void onShowCurrentRenters() {
        Intent intent = new Intent(this, FloatingPersonListActivity.class);
        intent.putExtra(FloatingPersonListActivity.EXTERNAL_QUERY, LaiJingRenYuanColumns.FWID + " LIKE '" + mSelectedRoomID + "'");
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 添加组主人
     */
    private void onAddRenters() {
        Intent intent = new Intent(this, FloatingPersonActivity.class);
        intent.putExtra(SELECTED_ROOM_ID, mSelectedRoomID);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 删除当前房屋登记
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
                        contentValues.put(FangWuColumns.SFSC, true);
                        contentValues.put(FangWuColumns.SCSJ, CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                        int count = contentProvider.update(FangWuColumns.CONTENT_URI, contentValues, FangWuColumns.FWID + " LIKE '" + mSelectedRoomID + "'", null);

                        if (count == 1) {
                            CommonUtils.createErrorAlertDialog(RentRoomActivity.this, "删除成功！").show();

                            Dialog dialog = new AlertDialog.Builder(RentRoomActivity.this)
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

                            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "房屋信息", mSelectedRoomID, "删除房屋信息", "民警");
                        } else {
                            CommonUtils.createErrorAlertDialog(RentRoomActivity.this, "删除失败！").show();
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

        ContentValues fangwuValues = new ContentValues();
        ContentValues fangzhuValues = new ContentValues();

        // 登记人员信息
        fangwuValues.put(FangWuColumns.SZDXQ, mSpinnerSuoZaiDi_XiaQu.getSelectedItem().toString());
        fangwuValues.put(FangWuColumns.GLYXM, mSpinnerGuanLiYuanXingMing.getSelectedItem().toString());
        fangwuValues.put(FangWuColumns.GLYBH, mEditGuanLiYuanBianHao.getText().toString());
        fangwuValues.put(FangWuColumns.TBRQ, mEditTianBiaoRiQi.getText().toString());

        // 房屋基本信息
        fangwuValues.put(FangWuColumns.FWLX, mSpinnerFangWuLeiXing.getSelectedItem().toString());
        fangwuValues.put(FangWuColumns.JSXZ, mSpinnerJianSheXingZhi.getSelectedItem().toString());

        if (!TextUtils.isEmpty(mSpinnerSuoZaiDiZhi_FenJu.getSelectedItem().toString()))
            fangwuValues.put(FangWuColumns.SZDZFJ, mSpinnerSuoZaiDiZhi_FenJu.getSelectedItem().toString());
        if (!TextUtils.isEmpty(mSpinnerSuoZaiDiZhi_PaiChuSuo.getSelectedItem().toString()))
            fangwuValues.put(FangWuColumns.SZDZPCS, mSpinnerSuoZaiDiZhi_PaiChuSuo.getSelectedItem().toString());

        fangwuValues.put(FangWuColumns.SZDXZSQ, mEditSuoZaiDiZhi_SheQu.getText().toString());
        fangwuValues.put(FangWuColumns.SZDXZJD, mSpinnerSuoZaiDiZhi_JieDao.getSelectedItem().toString());
        fangwuValues.put(FangWuColumns.SZDXZZT, mEditSuoZaiDiXiangZhi.getText().toString());

        fangwuValues.put(FangWuColumns.JZLX, mSpinnerJianZhuLeiXing.getSelectedItem().toString());
        if (!TextUtils.isEmpty(mEditFangChanZhengHao.getText().toString()))
            fangwuValues.put(FangWuColumns.FCZH, mEditFangChanZhengHao.getText().toString());

        fangwuValues.put(FangWuColumns.SSPCSMC, mEditSuoShuPaiChuSuoMingCheng.getText().toString());
        fangwuValues.put(FangWuColumns.MJXM, mEditMinJingXingMing.getText().toString());

        if (mSpinnerFangWuLeiXing.getSelectedItem().toString().equals("群租房类"))
            fangwuValues.put(FangWuColumns.QZFLX, mSpinnerQunZuFangLeiXing.getSelectedItem().toString());
        else
            fangwuValues.put(FangWuColumns.QZFLX, "");

        if (mSpinnerFangWuLeiXing.getSelectedItem().toString().equals("其它类"))
            fangwuValues.put(FangWuColumns.QTFWLX, mEditQiTaFangWuLeiXing.getText().toString());
        else
            fangwuValues.put(FangWuColumns.QTFWLX, "");

        if (mSpinnerJianSheXingZhi.getSelectedItem().toString().equals("其他"))
            fangwuValues.put(FangWuColumns.QTJSXZ, mEditQiTaJianSheXingZhi.getText().toString());
        else
            fangwuValues.put(FangWuColumns.QTJSXZ, "");

        if (mSpinnerJianZhuLeiXing.getSelectedItem().toString().equals("其他"))
            fangwuValues.put(FangWuColumns.QTJZLX, mEditQiTaJianZhuLeiXing.getText().toString());
        else
            fangwuValues.put(FangWuColumns.QTJZLX, "");

        // 检查房主信息
        fangwuValues.put(FangWuColumns.SYQLX, mSpinnerSuoYouQuanLeiXing.getSelectedItem().toString());
        fangwuValues.put(FangWuColumns.CZRLX, mSpinnerChuZuRenLeiXing.getSelectedItem().toString());

        if (mSpinnerSuoYouQuanLeiXing.getSelectedItem().toString().equals("单位")) {
            fangwuValues.put(FangWuColumns.DWMC, mEditDanWeiMingCheng.getText().toString());
            fangwuValues.put(FangWuColumns.FZRXM, mEditFuZeRenXingMing.getText().toString());
            fangwuValues.put(FangWuColumns.DWLXDH, mEditDanWeiLianXiDianHua.getText().toString());
            fangwuValues.put(FangWuColumns.SZDZS, mSpinnerSuoZaiDiZhi_Sheng.getSelectedItem().toString());
            fangwuValues.put(FangWuColumns.SZDZSHI, mSpinnerSuoZaiDiZhi_Qu.getSelectedItem().toString());
            fangwuValues.put(FangWuColumns.SZDXXDZ, mEditDanWeiSuoZaiXiangZhi.getText().toString());

            fangzhuValues.put(FangZhuColumns.DWMC, mEditDanWeiMingCheng.getText().toString());
            fangzhuValues.put(FangZhuColumns.FZRXM, mEditFuZeRenXingMing.getText().toString());
            fangzhuValues.put(FangZhuColumns.DWLXDH, mEditDanWeiLianXiDianHua.getText().toString());
            fangzhuValues.put(FangZhuColumns.SZDZS, mSpinnerSuoZaiDiZhi_Sheng.getSelectedItem().toString());
            fangzhuValues.put(FangZhuColumns.SZDZSHI, mSpinnerSuoZaiDiZhi_Qu.getSelectedItem().toString());
            fangzhuValues.put(FangZhuColumns.SZDXXDZ, mEditDanWeiSuoZaiXiangZhi.getText().toString());
        } else {
            fangwuValues.put(FangWuColumns.XM, mEditXingMing.getText().toString());

            if (mSpinnerZhengJianLeiBie.getSelectedItem().toString().equals("请选择"))
                fangwuValues.put(FangWuColumns.ZJLB, (String) null);
            else
                fangwuValues.put(FangWuColumns.ZJLB, mSpinnerZhengJianLeiBie.getSelectedItem().toString());
            fangwuValues.put(FangWuColumns.ZJHM, mEditZhengJianHaoMa.getText().toString());
            fangwuValues.put(FangWuColumns.XB, mSpinnerXingBie.getSelectedItem().toString());
            fangwuValues.put(FangWuColumns.CSRQ, mEditChuShengRiQi.getText().toString());
            fangwuValues.put(FangWuColumns.HJD, mSpinnerHuJiDi.getSelectedItem().toString());
            if (mSpinnerZhengZhiMianMao.getSelectedItem().toString().equals("请选择"))
                fangwuValues.put(FangWuColumns.ZZMM, (String) null);
            else
                fangwuValues.put(FangWuColumns.ZZMM, mSpinnerZhengZhiMianMao.getSelectedItem().toString());
            fangwuValues.put(FangWuColumns.LXDH, mEditLianXiDianHua.getText().toString());

            if (mSpinnerZhengJianLeiBie.getSelectedItem().toString().equals("其他"))
                fangwuValues.put(FangWuColumns.QTZJ, mEditQiTaZhengJian.getText().toString());
            else
                fangwuValues.put(FangWuColumns.QTZJ, "");

            if (mSpinnerHuJiDi.getSelectedItem().toString().equals("外籍")) {
                if (mSpinnerGuoJi.getSelectedItem().toString().equals("请选择"))
                    fangwuValues.put(FangWuColumns.GJDQ, (String) null);
                else
                    fangwuValues.put(FangWuColumns.GJDQ, mSpinnerGuoJi.getSelectedItem().toString());
                fangwuValues.put(FangWuColumns.HJDZS, (String) null);
                fangwuValues.put(FangWuColumns.HJDZSHI, (String) null);
                fangwuValues.put(FangWuColumns.HJDXXDZ, (String) null);
            } else {
                fangwuValues.put(FangWuColumns.GJDQ, (String) null);
                if (mSpinnerHuJiDiZhi_Sheng.getSelectedItem().toString().equals("请选择"))
                    fangwuValues.put(FangWuColumns.HJDZS, (String) null);
                else
                    fangwuValues.put(FangWuColumns.HJDZS, mSpinnerHuJiDiZhi_Sheng.getSelectedItem().toString());

                if (mSpinnerHuJiDiZhi_Shi.getSelectedItem().toString().equals("请选择"))
                    fangwuValues.put(FangWuColumns.HJDZSHI, (String) null);
                else
                    fangwuValues.put(FangWuColumns.HJDZSHI, mSpinnerHuJiDiZhi_Shi.getSelectedItem().toString());
                if (TextUtils.isEmpty(mEditHuJiXiangZhi.getText().toString()))
                    fangwuValues.put(FangWuColumns.HJDXXDZ, (String) null);
                else
                    fangwuValues.put(FangWuColumns.HJDXXDZ, mEditHuJiXiangZhi.getText().toString());
            }

            if (mSpinnerXianZhuDiZhi_Sheng.getSelectedItem().toString().equals("请选择"))
                fangwuValues.put(FangWuColumns.XZDZS, (String) null);
            else
                fangwuValues.put(FangWuColumns.XZDZS, mSpinnerXianZhuDiZhi_Sheng.getSelectedItem().toString());

            if (mSpinnerXianZhuDiZhi_Shi.getSelectedItem().toString().equals("请选择"))
                fangwuValues.put(FangWuColumns.XZDZSHI, (String) null);
            else
                fangwuValues.put(FangWuColumns.XZDZSHI, mSpinnerXianZhuDiZhi_Shi.getSelectedItem().toString());

            if (TextUtils.isEmpty(mEditXianZhuXiangZhi.getText().toString()))
                fangwuValues.put(FangWuColumns.XJZDXXDZ, (String) null);
            else
                fangwuValues.put(FangWuColumns.XJZDXXDZ, mEditXianZhuXiangZhi.getText().toString());

            // 房主信息
            fangzhuValues.put(FangZhuColumns.XM, mEditXingMing.getText().toString());
            fangzhuValues.put(FangZhuColumns.ZJLB, mSpinnerZhengJianLeiBie.getSelectedItem().toString());
            fangzhuValues.put(FangZhuColumns.ZJHM, mEditZhengJianHaoMa.getText().toString());
            fangzhuValues.put(FangZhuColumns.XB, mSpinnerXingBie.getSelectedItem().toString());
            fangzhuValues.put(FangZhuColumns.CSRQ, mEditChuShengRiQi.getText().toString());
            fangzhuValues.put(FangZhuColumns.HJD, mSpinnerHuJiDi.getSelectedItem().toString());
            fangzhuValues.put(FangZhuColumns.ZZMM, mSpinnerZhengZhiMianMao.getSelectedItem().toString());
            fangzhuValues.put(FangZhuColumns.LXDH, mEditLianXiDianHua.getText().toString());

            if (mSpinnerZhengJianLeiBie.getSelectedItem().toString().equals("其他"))
                fangzhuValues.put(FangZhuColumns.QTZJ, mEditQiTaZhengJian.getText().toString());
            else
                fangzhuValues.put(FangZhuColumns.QTZJ, "");

            if (mSpinnerHuJiDi.getSelectedItem().toString().equals("外籍")) {
                fangzhuValues.put(FangZhuColumns.GJDQ, mSpinnerGuoJi.getSelectedItem().toString());
                fangzhuValues.put(FangZhuColumns.HJDZS, "");
                fangzhuValues.put(FangZhuColumns.HJDZSHI, "");
                fangzhuValues.put(FangZhuColumns.HJDXXDZ, "");
            } else {
                fangzhuValues.put(FangZhuColumns.GJDQ, "");
                fangzhuValues.put(FangZhuColumns.HJDZS, mSpinnerHuJiDiZhi_Sheng.getSelectedItem().toString());
                fangzhuValues.put(FangZhuColumns.HJDZSHI, mSpinnerHuJiDiZhi_Shi.getSelectedItem().toString());
                fangzhuValues.put(FangZhuColumns.HJDXXDZ, mEditHuJiXiangZhi.getText().toString());
            }

            fangzhuValues.put(FangZhuColumns.XZDZS, mSpinnerXianZhuDiZhi_Sheng.getSelectedItem().toString());
            fangzhuValues.put(FangZhuColumns.XZDZSHI, mSpinnerXianZhuDiZhi_Shi.getSelectedItem().toString());
            fangzhuValues.put(FangZhuColumns.XJZDXXDZ, mEditXianZhuXiangZhi.getText().toString());
        }

        //
        if (mSpinnerChuZuRenLeiXing.getSelectedItem().toString().equals("中介公司")) {
            fangwuValues.put(FangWuColumns.ZJGSMC, mEditZhongJieGongSiMingCheng.getText().toString());
            fangwuValues.put(FangWuColumns.ZJGSLXDH, mEditZhongJie_LianXiDianHua.getText().toString());
            fangwuValues.put(FangWuColumns.FWFZRXM, mEditFangWuFuZeRenXingMing.getText().toString());
            fangwuValues.put(FangWuColumns.FWFZRSFZ, mEditFangWuFuZeRenShenFenZheng.getText().toString());
        } else if (mSpinnerChuZuRenLeiXing.getSelectedItem().toString().equals("个人转租")) {
            fangwuValues.put(FangWuColumns.ZZRXM, mEditZhuanZuRenXingMing.getText().toString());
            fangwuValues.put(FangWuColumns.ZZRZJLB, mSpinnerZhuanZuRen_ZhengJianLeiBie.getSelectedItem().toString());

            if (mSpinnerZhuanZuRen_ZhengJianLeiBie.getSelectedItem().toString().equals("其他"))
                fangwuValues.put(FangWuColumns.ZZRQTZJLB, mEditZhuanZuRen_QiTaZhengJianLeiBie.getText().toString());
            else
                fangwuValues.put(FangWuColumns.ZZRQTZJLB, "");

            fangwuValues.put(FangWuColumns.ZZRZJHM, mEditZhuanZuRen_ZhengJianHaoMa.getText().toString());
            fangwuValues.put(FangWuColumns.ZZRLXDH, mEditZhuanZuRen_LianXiDianHua.getText().toString());
            fangwuValues.put(FangWuColumns.ZZRXZDZ, mEditZhuanZuRen_XianZhuDiZhi.getText().toString());
        }

        // 检查房屋出租信息
        fangwuValues.put(FangWuColumns.CZJS, mEditChuZuJianShu.getText().toString());
        fangwuValues.put(FangWuColumns.CZMJ, mEditChuZuPingFangMi.getText().toString());
        fangwuValues.put(FangWuColumns.FWCS, mSpinnerFangWuCengShu.getSelectedItem().toString());

        String str = "";
        if (mCheckAnQuanYinHuan_Wu.isChecked()) {
            str = mCheckJianDingZeRenShu_Wu.getText().toString().trim();
        } else {
            if (mCheckAnQuanYinHuan_BuWenDingFengXian.isChecked())
                str = mCheckAnQuanYinHuan_BuWenDingFengXian.getText().toString().trim() + " ";

            if (mCheckAnQuanYinHuan_ZhiAnYinHuan.isChecked())
                str += mCheckAnQuanYinHuan_ZhiAnYinHuan.getText().toString().trim() + " ";

            if (mCheckAnQuanYinHuan_XiaoFangYinHuan.isChecked())
                str += mCheckAnQuanYinHuan_XiaoFangYinHuan.getText().toString().trim() + " ";

            if (mCheckAnQuanYinHuan_JianZhuAnQuan.isChecked())
                str += mCheckAnQuanYinHuan_JianZhuAnQuan.getText().toString().trim();
        }
        fangwuValues.put(FangWuColumns.AQYH, str);

        fangwuValues.put(FangWuColumns.CZYT, mSpinnerChuZuYongTu.getSelectedItem().toString());
        if (mSpinnerChuZuYongTu.getSelectedItem().toString().equals("其他"))
            fangwuValues.put(FangWuColumns.QTCZYT, mEditQiTaChuZuYongTu.getText().toString());
        else
            fangwuValues.put(FangWuColumns.QTCZYT, "");

        fangwuValues.put(FangWuColumns.ZZRYSLBS, mEditZuZhuRenYuan_BenShi.getText().toString());
        fangwuValues.put(FangWuColumns.ZZRYSLWSS, mEditZuZhuRenYuan_WaiShengShi.getText().toString());
        fangwuValues.put(FangWuColumns.ZZRYSLGAT, mEditZuZhuRenYuan_GangAoTai.getText().toString());
        fangwuValues.put(FangWuColumns.ZZRYSLWJ, mEditZuZhuRenYuan_WaiJi.getText().toString());

        fangwuValues.put(FangWuColumns.ZJFS, ((RadioButton) findViewById(mRadioGroupZuJin.getCheckedRadioButtonId())).getText().toString().trim());
        fangwuValues.put(FangWuColumns.ZJ, mEditZuJin.getText().toString());

        fangwuValues.put(FangWuColumns.ZLHT, mSpinnerZuLinHeTong.getSelectedItem().toString());
        fangwuValues.put(FangWuColumns.NS, mSpinnerNaShui.getSelectedItem().toString());
        fangwuValues.put(FangWuColumns.DJBA, mSpinnerDengJiBeiAn.getSelectedItem().toString());

        str = "";
        if (mCheckJianDingZeRenShu_Wu.isChecked()) {
            str = mCheckJianDingZeRenShu_Wu.getText().toString();
        } else {
            if (mCheckJianDingZeRenShu_ZhiAn.isChecked())
                str = mCheckJianDingZeRenShu_ZhiAn.getText().toString() + " ";

            if (mCheckJianDingZeRenShu_XiaoFang.isChecked())
                str += mCheckJianDingZeRenShu_XiaoFang.getText().toString() + " ";

            if (mCheckJianDingZeRenShu_HunYu.isChecked())
                str += mCheckJianDingZeRenShu_HunYu.getText().toString();
        }
        fangwuValues.put(FangWuColumns.QDZRS, str);

        fangwuValues.put(FangWuColumns.QDZRSRQ, mEditJianDingZeShuRiQi.getText().toString());
        fangwuValues.put(FangWuColumns.CZQSRQ, mEditChuZuQiShiRiQi.getText().toString());
        fangwuValues.put(FangWuColumns.CZJZRQ, mEditChuZuJieZhiRiQi.getText().toString());

        // 备注
        if (!TextUtils.isEmpty(mEditBeiZhu.getText().toString()))
            fangwuValues.put(FangWuColumns.BZ, mEditBeiZhu.getText().toString());

        // 图像
        fangwuValues.put(FangWuColumns.ZPURL, PhotoUtil.convertUrisToString(mZhaoPianUriArray));
        if (mSheXiangUri != null)
            fangwuValues.put(FangWuColumns.SXURL, CommonUtils.getLastPathFromUri(mSheXiangUri));
        else
            fangwuValues.put(FangWuColumns.SXURL, (String) null);

        fangwuValues.put(ShangChuanColumns.SFJZ, false);
        fangwuValues.put(ShangChuanColumns.SFSC, false);
        fangwuValues.put(ShangChuanColumns.LRSB, true);
        fangwuValues.put(ShangChuanColumns.SFSCZFWQ, false);
        fangwuValues.put(ShangChuanColumns.SFSCZHL, false);
        fangwuValues.put(ShangChuanColumns.SFSCZLGB, false);

        fangzhuValues.put(ShangChuanColumns.SFSC, false);
        fangzhuValues.put(ShangChuanColumns.LRSB, true);

        MyContentProvider contentProvider = new MyContentProvider();

        Date currentDate = new Date();
        String message = "";
        String id;

        if (mIsAdd) {
            mSelectedRoomID = id = mOwnerData.MJID + CommonUtils.getFormattedDateString(currentDate, "yyyyMMddHHmmss");

            fangzhuValues.put(FangZhuColumns.FZID, id);
            fangzhuValues.put(ShangChuanColumns.LRMJJH, mOwnerData.MJJH);
            fangzhuValues.put(ShangChuanColumns.LRMJID, mOwnerData.MJID);
            fangzhuValues.put(ShangChuanColumns.LRMJXM, mOwnerData.MJXM);
            fangzhuValues.put(ShangChuanColumns.LRDWID, mOwnerData.SSDWID);
            fangzhuValues.put(ShangChuanColumns.LRDWMC, mOwnerData.SSDWMC);
            fangzhuValues.put(ShangChuanColumns.LRSJ, CommonUtils.getFormattedDateString(currentDate, "yyyy-MM-dd HH:mm:ss"));

            /*Uri fangzhuUri = */
            contentProvider.insert(FangZhuColumns.CONTENT_URI, fangzhuValues);
            //long fangzhuID = ContentUris.parseId(fangzhuUri);

            //fangwuValues.put(FangWuColumns.FZID, id);
            fangwuValues.put(FangWuColumns.FWID, id);
            fangwuValues.put(FangWuColumns.DJBXH, id);
            fangwuValues.put(ShangChuanColumns.LRMJJH, mOwnerData.MJJH);
            fangwuValues.put(ShangChuanColumns.LRMJID, mOwnerData.MJID);
            fangwuValues.put(ShangChuanColumns.LRMJXM, mOwnerData.MJXM);
            fangwuValues.put(ShangChuanColumns.LRDWID, mOwnerData.SSDWID);
            fangwuValues.put(ShangChuanColumns.LRDWMC, mOwnerData.SSDWMC);
            fangwuValues.put(ShangChuanColumns.LRSJ, CommonUtils.getFormattedDateString(currentDate, "yyyy-MM-dd HH:mm:ss"));

            Uri uri = contentProvider.insert(FangWuColumns.CONTENT_URI, fangwuValues);
            //mSelectedRoomID = String.valueOf(ContentUris.parseId(uri));

            message = "保存成功！";
            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "房屋信息，房主信息", String.valueOf(id), "添加房屋房主信息", "民警");
        }

        if (mIsUpdate) {
            fangzhuValues.put(ShangChuanColumns.XGMJJH, mOwnerData.MJJH);
            fangzhuValues.put(ShangChuanColumns.XGMJID, mOwnerData.MJID);
            fangzhuValues.put(ShangChuanColumns.XGMJXM, mOwnerData.MJXM);
            fangzhuValues.put(ShangChuanColumns.XGDWID, mOwnerData.SSDWID);
            fangzhuValues.put(ShangChuanColumns.XGDWMC, mOwnerData.SSDWMC);
            fangzhuValues.put(ShangChuanColumns.XGSJ, CommonUtils.getFormattedDateString(currentDate, "yyyy-MM-dd HH:mm:ss"));

            contentProvider.update(FangZhuColumns.CONTENT_URI, fangzhuValues, FangZhuColumns.FZID + " LIKE '" + mSelectedRenterID + "'", null);

            fangwuValues.put(ShangChuanColumns.XGMJJH, mOwnerData.MJJH);
            fangwuValues.put(ShangChuanColumns.XGMJID, mOwnerData.MJID);
            fangwuValues.put(ShangChuanColumns.XGMJXM, mOwnerData.MJXM);
            fangwuValues.put(ShangChuanColumns.XGDWID, mOwnerData.SSDWID);
            fangwuValues.put(ShangChuanColumns.XGDWMC, mOwnerData.SSDWMC);
            fangwuValues.put(ShangChuanColumns.XGSJ, CommonUtils.getFormattedDateString(currentDate, "yyyy-MM-dd HH:mm:ss"));
            contentProvider.update(FangWuColumns.CONTENT_URI, fangwuValues, FangWuColumns.FWID + " LIKE '" + mSelectedRoomID + "'", null);

            message = "修改成功！";
            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "房屋信息，房主信息", mSelectedRoomID, "修改房屋房主信息", "民警");
        }

        Dialog dialog = new AlertDialog.Builder(RentRoomActivity.this)
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
            Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI, null,
                    FangWuColumns.FWID + " LIKE '" + mSelectedRoomID + "'", null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    UploadUtil.uploadNewData(this, FangWuColumns.CONTENT_TYPE, cursor, false);
                }

                cursor.close();
            }
        }
    }

    /**
     * Check if all fields were filled.
     */
    private boolean checkFields() {
        String warning = "不能为空，请将信息补充完整！";

        // 检查登记人员信息
        if (mSpinnerSuoZaiDi_XiaQu.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "所在地（辖区）" + "”" + warning).show();
            mSpinnerSuoZaiDi_XiaQu.setFocusableInTouchMode(true);
            mSpinnerSuoZaiDi_XiaQu.requestFocusFromTouch();
            mSpinnerSuoZaiDi_XiaQu.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerGuanLiYuanXingMing.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "管理员姓名" + "”" + warning).show();
            mSpinnerGuanLiYuanXingMing.setFocusableInTouchMode(true);
            mSpinnerGuanLiYuanXingMing.requestFocusFromTouch();
            mSpinnerGuanLiYuanXingMing.setFocusableInTouchMode(false);
            return false;
        }

        // 检查房屋基本信息
        if (mSpinnerFangWuLeiXing.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "房屋类型" + "”" + warning).show();
            mSpinnerFangWuLeiXing.setFocusableInTouchMode(true);
            mSpinnerFangWuLeiXing.requestFocusFromTouch();
            mSpinnerFangWuLeiXing.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerJianSheXingZhi.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "建设性质" + "”" + warning).show();
            mSpinnerJianSheXingZhi.setFocusableInTouchMode(true);
            mSpinnerJianSheXingZhi.requestFocusFromTouch();
            mSpinnerJianSheXingZhi.setFocusableInTouchMode(false);
            return false;
        }

        if (mSpinnerFangWuLeiXing.getSelectedItem().toString().equals("群租房类")
                && mSpinnerQunZuFangLeiXing.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "群租房类型" + "”" + warning).show();
            mSpinnerQunZuFangLeiXing.setFocusableInTouchMode(true);
            mSpinnerQunZuFangLeiXing.requestFocusFromTouch();
            mSpinnerQunZuFangLeiXing.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerFangWuLeiXing.getSelectedItem().toString().equals("其它类")
                && TextUtils.isEmpty(mEditQiTaFangWuLeiXing.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "其它房屋类型" + "”" + warning).show();
            mEditQiTaFangWuLeiXing.requestFocusFromTouch();
            return false;
        }
        if (mSpinnerJianSheXingZhi.getSelectedItem().toString().equals("其他")
                && TextUtils.isEmpty(mEditQiTaJianSheXingZhi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "其他建设性质" + "”" + warning).show();
            mEditQiTaJianSheXingZhi.requestFocusFromTouch();
            return false;
        }

        if (mSpinnerSuoZaiDiZhi_JieDao.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "所在地址街道" + "”" + warning).show();
            mSpinnerSuoZaiDiZhi_JieDao.setFocusableInTouchMode(true);
            mSpinnerSuoZaiDiZhi_JieDao.requestFocusFromTouch();
            mSpinnerSuoZaiDiZhi_JieDao.setFocusableInTouchMode(false);
            return false;
        }
        if (TextUtils.isEmpty(mEditSuoZaiDiXiangZhi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "所在地详址" + "”" + warning).show();
            mEditSuoZaiDiXiangZhi.requestFocusFromTouch();
            return false;
        }

        if (mSpinnerJianZhuLeiXing.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "建筑类型" + "”" + warning).show();
            mSpinnerJianZhuLeiXing.setFocusableInTouchMode(true);
            mSpinnerJianZhuLeiXing.requestFocusFromTouch();
            mSpinnerJianZhuLeiXing.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerJianZhuLeiXing.getSelectedItem().toString().equals("其他")
                && TextUtils.isEmpty(mEditQiTaJianZhuLeiXing.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "其他建筑类型" + "”" + warning).show();
            mEditQiTaJianZhuLeiXing.requestFocusFromTouch();
            return false;
        }

        // 检查房主信息
        if (mSpinnerSuoYouQuanLeiXing.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "所有权类型" + "”" + warning).show();
            mSpinnerSuoYouQuanLeiXing.setFocusableInTouchMode(true);
            mSpinnerSuoYouQuanLeiXing.requestFocusFromTouch();
            mSpinnerSuoYouQuanLeiXing.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerChuZuRenLeiXing.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "出租人类型" + "”" + warning).show();
            mSpinnerChuZuRenLeiXing.setFocusableInTouchMode(true);
            mSpinnerChuZuRenLeiXing.requestFocusFromTouch();
            mSpinnerChuZuRenLeiXing.setFocusableInTouchMode(false);
            return false;
        }

        if (mSpinnerSuoYouQuanLeiXing.getSelectedItem().toString().equals("单位")) {
            if (TextUtils.isEmpty(mEditDanWeiMingCheng.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "单位名称" + "”" + warning).show();
                mEditDanWeiMingCheng.requestFocusFromTouch();
                return false;
            }
            if (TextUtils.isEmpty(mEditDanWeiLianXiDianHua.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "联系电话" + "”" + warning).show();
                mEditDanWeiLianXiDianHua.requestFocusFromTouch();
                return false;
            }
        } else {
            if (TextUtils.isEmpty(mEditXingMing.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "姓名" + "”" + warning).show();
                mEditXingMing.requestFocusFromTouch();
                return false;
            }
            if (mSpinnerZhengJianLeiBie.getSelectedItem().toString().equals("其他")
                    && TextUtils.isEmpty(mEditQiTaZhengJian.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "其他证件" + "”" + warning).show();
                mEditQiTaZhengJian.requestFocusFromTouch();
                return false;
            } else if (mSpinnerZhengJianLeiBie.getSelectedItem().toString().equals("居民身份证")) {
                String idNumber = mEditZhengJianHaoMa.getText().toString();

                if (TextUtils.isEmpty(idNumber)) {
                    CommonUtils.createErrorAlertDialog(RentRoomActivity.this,
                            "“" + "证件号码" + "”不能为空，请将信息补充完整！").show();
                    mEditZhengJianHaoMa.requestFocusFromTouch();
                    return false;
                } else {
                    idNumber = IDCardUtil.verifyCardID(RentRoomActivity.this, idNumber, "房主");
                    if (idNumber != null) {
                        setPersonInformationFromID(idNumber);
                    } else {
                        mEditZhengJianHaoMa.requestFocusFromTouch();
                        return false;
                    }
                }
            }
            if (mSpinnerXingBie.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "性别" + "”" + warning).show();
                mSpinnerXingBie.setFocusableInTouchMode(true);
                mSpinnerXingBie.requestFocusFromTouch();
                mSpinnerXingBie.setFocusableInTouchMode(false);
                return false;
            }
            if (mSpinnerHuJiDi.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "户籍地" + "”" + warning).show();
                mSpinnerHuJiDi.setFocusableInTouchMode(true);
                mSpinnerHuJiDi.requestFocusFromTouch();
                mSpinnerHuJiDi.setFocusableInTouchMode(false);
                return false;
            }
            if (TextUtils.isEmpty(mEditLianXiDianHua.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "联系电话" + "”" + warning).show();
                mEditLianXiDianHua.requestFocusFromTouch();
                return false;
            }
        }

        // 检查房屋出租信息
        if (TextUtils.isEmpty(mEditChuZuJianShu.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "出租间数" + "”" + warning).show();
            mEditChuZuJianShu.requestFocusFromTouch();
            return false;
        }
        if (TextUtils.isEmpty(mEditChuZuPingFangMi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "出租面积" + "”" + warning).show();
            mEditChuZuPingFangMi.requestFocusFromTouch();
            return false;
        }
        if (mSpinnerFangWuCengShu.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "房屋层数" + "”" + warning).show();
            mSpinnerFangWuCengShu.setFocusableInTouchMode(true);
            mSpinnerFangWuCengShu.requestFocusFromTouch();
            mSpinnerFangWuCengShu.setFocusableInTouchMode(false);
            return false;
        }

        if (!mCheckAnQuanYinHuan_Wu.isChecked()
                && !mCheckAnQuanYinHuan_BuWenDingFengXian.isChecked()
                && !mCheckAnQuanYinHuan_ZhiAnYinHuan.isChecked()
                && !mCheckAnQuanYinHuan_XiaoFangYinHuan.isChecked()
                && !mCheckAnQuanYinHuan_JianZhuAnQuan.isChecked()) {
            CommonUtils.createErrorAlertDialog(this, "“" + "安全隐患" + "”" + warning).show();
            mCheckAnQuanYinHuan_Wu.requestFocusFromTouch();
            return false;
        }

        if (mSpinnerChuZuYongTu.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "出租用途" + "”" + warning).show();
            mSpinnerChuZuYongTu.setFocusableInTouchMode(true);
            mSpinnerChuZuYongTu.requestFocusFromTouch();
            mSpinnerChuZuYongTu.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerChuZuYongTu.getSelectedItem().toString().equals("其他")
                && TextUtils.isEmpty(mEditQiTaChuZuYongTu.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "其他出租用途" + "”" + warning).show();
            mEditQiTaChuZuYongTu.requestFocusFromTouch();
            return false;
        }

        if (TextUtils.isEmpty(mEditZuZhuRenYuan_BenShi.getText().toString())
                && TextUtils.isEmpty(mEditZuZhuRenYuan_WaiShengShi.getText().toString())
                && TextUtils.isEmpty(mEditZuZhuRenYuan_GangAoTai.getText().toString())
                && TextUtils.isEmpty(mEditZuZhuRenYuan_WaiJi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "租住人员" + "”" + warning).show();
            mEditZuZhuRenYuan_BenShi.requestFocusFromTouch();
            return false;
        }

        if (TextUtils.isEmpty(mEditZuJin.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "租金" + "”" + warning).show();
            mEditZuJin.requestFocusFromTouch();
            return false;
        }
        if (mSpinnerZuLinHeTong.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "租赁合同" + "”" + warning).show();
            mSpinnerZuLinHeTong.setFocusableInTouchMode(true);
            mSpinnerZuLinHeTong.requestFocusFromTouch();
            mSpinnerZuLinHeTong.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerNaShui.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "纳税" + "”" + warning).show();
            mSpinnerNaShui.setFocusableInTouchMode(true);
            mSpinnerNaShui.requestFocusFromTouch();
            mSpinnerNaShui.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerDengJiBeiAn.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "登记备案" + "”" + warning).show();
            mSpinnerDengJiBeiAn.setFocusableInTouchMode(true);
            mSpinnerDengJiBeiAn.requestFocusFromTouch();
            mSpinnerDengJiBeiAn.setFocusableInTouchMode(false);
            return false;
        }

        if (TextUtils.isEmpty(mEditChuZuQiShiRiQi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "出租起始日期" + "”" + warning).show();
            mEditChuZuQiShiRiQi.requestFocusFromTouch();
            return false;
        }

        if (!mCheckJianDingZeRenShu_Wu.isChecked()
                && !mCheckJianDingZeRenShu_ZhiAn.isChecked()
                && !mCheckJianDingZeRenShu_XiaoFang.isChecked()
                && !mCheckJianDingZeRenShu_HunYu.isChecked()) {
            CommonUtils.createErrorAlertDialog(this, "“" + "签订责任书" + "”" + warning).show();
            mCheckJianDingZeRenShu_Wu.requestFocusFromTouch();
            return false;
        }

        if (mSpinnerChuZuRenLeiXing.getSelectedItem().toString().equals("中介公司")) {
            if (TextUtils.isEmpty(mEditZhongJieGongSiMingCheng.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "中介公司名称" + "”" + warning).show();
                mEditZhongJieGongSiMingCheng.requestFocusFromTouch();
                return false;
            }
            if (TextUtils.isEmpty(mEditZhongJie_LianXiDianHua.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "中介公司的联系电话" + "”" + warning).show();
                mEditZhongJie_LianXiDianHua.requestFocusFromTouch();
                return false;
            }
            if (TextUtils.isEmpty(mEditFangWuFuZeRenXingMing.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "房屋负责人姓名" + "”" + warning).show();
                mEditFangWuFuZeRenXingMing.requestFocusFromTouch();
                return false;
            }

            String idNumber = mEditFangWuFuZeRenShenFenZheng.getText().toString();
            if (!TextUtils.isEmpty(idNumber)) {
                IDCardUtil.verifyCardID(RentRoomActivity.this, idNumber, "房屋负责人");
            }
        } else if (mSpinnerChuZuRenLeiXing.getSelectedItem().toString().equals("个人转租")) {
            if (mSpinnerZhuanZuRen_ZhengJianLeiBie.getSelectedItem().toString().equals("其他")
                    && TextUtils.isEmpty(mEditZhuanZuRen_QiTaZhengJianLeiBie.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "转租人的证件类别" + "”" + warning).show();
                mEditZhuanZuRen_QiTaZhengJianLeiBie.requestFocusFromTouch();
                return false;
            } else if (mSpinnerZhuanZuRen_ZhengJianLeiBie.getSelectedItem().toString().equals("居民身份证")) {
                String idNumber = mEditZhuanZuRen_ZhengJianHaoMa.getText().toString();

                if (TextUtils.isEmpty(idNumber)) {
                    CommonUtils.createErrorAlertDialog(RentRoomActivity.this,
                            "“" + "身份证号码" + "”不能为空，请将信息补充完整！").show();
                    mEditZhuanZuRen_ZhengJianHaoMa.requestFocusFromTouch();
                    return false;
                } else {
                    idNumber = IDCardUtil.verifyCardID(RentRoomActivity.this, idNumber, "转租人");
                    if (idNumber == null) {
                        mEditZhuanZuRen_ZhengJianHaoMa.requestFocusFromTouch();
                        return false;
                    }
                }
            }
            if (TextUtils.isEmpty(mEditZhuanZuRenXingMing.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "转租人姓名" + "”" + warning).show();
                mEditZhuanZuRenXingMing.requestFocusFromTouch();
                return false;
            }
            if (TextUtils.isEmpty(mEditZhuanZuRen_LianXiDianHua.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "转租人联系电话" + "”" + warning).show();
                mEditZhuanZuRen_LianXiDianHua.requestFocusFromTouch();
                return false;
            }
        }

        return true;
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
            //intent.putExtra("aspectX", 1);
            //intent.putExtra("aspectY", 1);
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

    /*
     * Recording video
     */
    private void onRecordVideo() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            mFileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

            // set video quality
            // 1- for high quality video, 0 - for mms quality video
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

            // start the video capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        } catch (ActivityNotFoundException anfe) {
            CommonUtils.createErrorAlertDialog(this, "Alert", "Your device doesn't support capturing video!").show();
        }
    }

    /**
     * Creating file uri to store photoImage/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(CommonUtils.getOutputMediaFile(this, type == MEDIA_TYPE_IMAGE));
    }

    /**
     * Display room photo in ImageView
     */
    private void refreshImageViews() {
        int count = mZhaoPianUriArray.size();

        if (count == 0) {
            mImageZhaoPian.setVisibility(View.GONE);
        } else {
            mImageZhaoPian.setVisibility(View.VISIBLE);
            //mImageZhaoPian.setImageURI(mZhaoPianUriArray.get(count - 1));
            mImageZhaoPian.setImageBitmap(CommonUtils.getBitmapFromUri(mZhaoPianUriArray.get(count - 1), 4));
        }
    }

    /**
     * Display room video in VideoView
     */
    private void refreshVideoView() {
        try {
            if (mSheXiangUri != null) {
                mVideoSheXiang.setVisibility(View.VISIBLE);

                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(mSheXiangUri.getPath());

                String widthString = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String heightString = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

                metaRetriever.release();
                Log.e(TAG, "video size = " + widthString + "x" + heightString);

                // Set width and height
                int width, height;

                try {
                    width = Integer.parseInt(widthString);
                } catch (NumberFormatException ex) {
                    width = 292;
                }
                try {
                    height = Integer.parseInt(heightString);
                } catch (NumberFormatException ex) {
                    height = 292;
                }


                height = 292 * width / height;
                width = 292;

                RelativeLayout layout = (RelativeLayout) mVideoSheXiang.getParent();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, height);
                params.gravity = Gravity.CENTER;
                params.weight = 1;
                layout.setLayoutParams(params);
                //mVideoSheXiang.setDimensions(width, height);
                mVideoSheXiang.getHolder().setFixedSize(width, height);
                mVideoSheXiang.setVideoURI(mSheXiangUri);
                //mVideoSheXiang.start();
                mVideoSheXiang.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.seekTo(1);
                    }
                });
                mVideoSheXiang.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.seekTo(1);
                    }
                });

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (mMediaController == null) {
                            v.setOnClickListener(null);

                            mMediaController = new MediaController(RentRoomActivity.this);
                            mMediaController.setAnchorView(mVideoSheXiang);
                            mVideoSheXiang.setMediaController(mMediaController);
                            mMediaController.show();
                        }*/
                        if (mSheXiangUri != null) {
                            onPreviewVideo();
                        }
                    }
                });
            } else {
                mVideoSheXiang.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

    private void onPreviewPhoto() {
        Intent intent = new Intent(this, PreviewPhotoActivity.class);
        intent.putParcelableArrayListExtra(PreviewPhotoActivity.PHOTO_ID_ARRAY, mZhaoPianUriArray);
        intent.putExtra(PreviewPhotoActivity.ENABLE_DELETE, mIsAdd || mIsUpdate);
        startActivityForResult(intent, PREVIEW_PHOTO_REQUEST_CODE);
    }

    private void onPreviewVideo() {
        Intent intent = new Intent(this, PreviewVideoActivity.class);
        intent.putExtra(PreviewVideoActivity.VIDEO_PATH, mSheXiangUri.getPath());
        intent.putExtra(PreviewPhotoActivity.ENABLE_DELETE, mIsAdd || mIsUpdate);
        startActivityForResult(intent, PREVIEW_VIDEO_REQUEST_CODE);
    }

    /**
     * 自动输入出生日期和性别字段从身份证号
     */
    private void setPersonInformationFromID(String id) {
        //设置相应的出生日期
        mEditChuShengRiQi.setText(id.substring(6, 10) + "-" + id.substring(10, 12) + "-" + id.substring(12, 14));

        //设置性别
        int idx = Integer.parseInt(id.substring(16, 17)) % 2;
        mSpinnerXingBie.setSelection(idx % 2 == 1 ? 1 : 2);// 男女
    }

}
