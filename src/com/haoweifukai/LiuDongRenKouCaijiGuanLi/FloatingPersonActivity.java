/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename FloatingPersonActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.T_HMD_MD5;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.YuJingColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.IDCardUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.PhotoUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.PlayerUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.UploadUtil;
import com.synjones.otg.UsbMonitorService;
import com.synjones.padreader.IDCard;
import com.synjones.padreader.ReaderDriver;
import com.synjones.padreader.SynjonesOTGReaderLib;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;

public class FloatingPersonActivity extends PermanentActivity implements View.OnClickListener {

    // 常数
    private static final String TAG = FloatingPersonActivity.class.getSimpleName();
    public static final String SELECTED_PERSON_ID = "selected_person_id";

    private static final int ReadOnceDone = 0xa1;
    private static final int LoopRead = 0xa2;
    private static final int CONNECT_ERR = 0xa3;
    private static final int DISCONNECT = 0xa4;

    public static final int MEDIA_TYPE_IMAGE = 0;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CROP_IMAGE_REQUEST_CODE = 150;
    private static final int CHOOSE_ROOM_ID_REQUEST_CODE = 200;
    private static final int CHOOSE_PERSON_ID_REQUEST_CODE = 300;
    private static final int PREVIEW_PHOTO_REQUEST_CODE = 400;

    // 变数
    // Card Reader
    private static Handler mHandler = null;

    private long ReadErrTime = 0;
    private long ContinuousErrTime = 0;

    // 读卡器相关
    public static SynjonesOTGReaderLib Reader;
    private ReaderDriver readerDriver;
    private IDCard idcard = null;

    private boolean reading = false;
    private ReadThread ReadThreadHandler;

    private MediaPlayer mediaPlayer;
    private Bitmap mBitmapPhoto;

    // variables to capture image
    private Uri mFileUri;
    private ArrayList<Uri> mZhaoPianUriArray = new ArrayList<Uri>();

    //
    private boolean mIsAdd = false;
    private boolean mIsUpdate = false;
    private boolean mHasRoomID = false;
    private String mSelectedID = "";
    private String mSelectedRoomID = "";

    private String mStrGuanLiYuanXingMing = "";
    private String mStrXianZhuDi_JieDao = "";
    private String mStrZhuYaoCongShiGongZuo = "";

    private LinkedHashMap<String, String> mGuanLiYuanMap = new LinkedHashMap<String, String>();

    private String mStrHuJiDiZhi_Shi = "";
    private String mStrHuJiDiZhi_Qu = "";

    // 定义控制
    // 登记信息
    private Spinner mSpinnerSuoZaiDi_XiaQu;
    private Spinner mSpinnerGuanLiYuanXingMing;
    private EditText mEditGuanLiYuanBianHao;
    private EditText mEditDengJiBiaoXuHao;
    private EditText mEditTianBiaoRiQi;

    // 来京人员基本信息
    private Button mButtonCaiJiZhaoPian;
    private Button mButtonJianSuoGuiJi;
    private Button mButtonCaiJiShenFenZheng;
    private ImageView mImageZhaoPian;
    private EditText mEditXingMing;
    private EditText mEditShenFenZhengHaoMa;
    private CheckBox mCheckZanWu;

    private EditText mEditChuShengRiQi;
    private Spinner mSpinnerXingBie;

    private Spinner mSpinnerMinZu;
    private View mLayoutMinZuXiangXiXinXi;
    private EditText mEditMinZuXiangXiXinXi;

    private Spinner mSpinnerZhengZhiMianMao;
    private Spinner mSpinnerShouJiaoYuChengDu;
    private Spinner mSpinnerHuJiLeiBie;
    private Spinner mSpinnerHunYinZhuangKuang;
    private EditText mEditLianXiDianHua;

    private Spinner mSpinnerHuJiDiZhi_Sheng;
    private Spinner mSpinnerHuJiDiZhi_Shi;
    private Spinner mSpinnerHuJiDiZhi_Qu;
    private EditText mEditHuJiXiangXiDiZhi;

    private Spinner mSpinnerChuShengDi;
    private Spinner mSpinnerJuZhuZhengJian;
    private Spinner mSpinnerHunYuZhengMing;
    private Spinner mSpinnerMianYiJieZhongZheng;

    // 家庭户信息
    private Spinner mSpinnerJiaTingHuLiuRu;
    private View mLayoutHuZhu;
    private Spinner mSpinnerHuZhu;

    private View mLayoutBenHuWaiLaiRenKouShu;
    private EditText mEditBenHuWaiLaiRenKouShu;
    private EditText mEditBenHuWaiLaiRenKouShu_16;
    private EditText mEditBenHuWaiLaiRenKouShu_Nan;
    private EditText mEditBenHuWaiLaiRenKouShu_Nv;

    private View mLayoutYuHuZhuGuanXi;
    private Spinner mSpinnerYuHuZhuGuanXi;
    private EditText mEditHuZhuDengJiBiaoXuHao;
    private Button mButtonXuanZeHuZhu;

    private View mLayoutYuHuZhuXiangXiGuanXi;
    private EditText mEditYuHuZhuXiangXiGuanXi;

    // 居住信息
    private EditText mEditLiKaiYuanJiRiQi;
    private EditText mEditLaiJingRiQi;
    private EditText mEditLaiXianZhuDiRiQi;

    private Spinner mSpinnerLaiJingYuanYin;
    private View mLayoutLaiJingQiTaXiangXiYuanYin;
    private EditText mEditLaiJingQiTaXiangXiYuanYin;

    private Spinner mSpinnerJuZhuLeiXing;
    private View mLayoutJuZhuQiTaLeiXing;
    private EditText mEditJuZhuQiTaLeiXing;
    private View mLayoutFangWuDengJiBiaoXuHao;
    private EditText mEditFangWuDengJiBiaoXuHao;
    private Button mButtonXuanZeFangWu;

    private View mLayoutXianZhuDi;
    private View mLayoutXianZhuDi2;
    private Spinner mSpinnerXianZhuDi_FenJu;
    private Spinner mSpinnerXianZhuDi_PaiChuSuo;
    private TextView mEditXianZhuDi_SheQu;
    private Spinner mSpinnerXianZhuDi_JieDao;

    private EditText mEditXianZhuDiXiangZhi;
    private TextView mTextXianZhuDiXiangZhi;
    private EditText mEditSuoShuPaiChuSuoMingCheng;
    private EditText mEditMinJingXingMing;

    // 就业社保信息
    private Spinner mSpinnerMuQianZhuangKuang;

    private View mLayoutMuQianZhuangKuangXiangXiXinXi;
    private EditText mEditMuQianZhuangKuangXiangXiXinXi;

    private View mLayoutXueXiaoXinXi;
    private EditText mEditXueXiaoMingCheng;
    private EditText mEditXueXiaoSuoZaiDi;

    private View mLayoutJiuYeXinXi;
    private EditText mEditJiuYeDanWeiMingCheng;
    private EditText mEditDanWeiDengJiBiaoXuHao;
    private EditText mEditJiuYeDanWeiXiangXiDiZhi;

    private Spinner mSpinnerJiuYeDanWeiSuoShuHangYe;
    private EditText mEditQiTaHangYe;
    private Spinner mSpinnerZhuYaoCongShiGongZuo;
    private EditText mEditQiTaZhuYaoCongShiGongZuo;

    private Spinner mSpinnerZhiYe;
    private Spinner mSpinnerQianDingLaoDongHeTong;
    private Spinner mSpinnerJiuYeDanWeiSuoZaiDi;

    private CheckBox mCheckZaiJingBaoXian_Wu;
    private CheckBox mCheckZaiJingBaoXian_YangLao;
    private CheckBox mCheckZaiJingBaoXian_ShiYe;
    private CheckBox mCheckZaiJingBaoXian_YiLiao;
    private CheckBox mCheckZaiJingBaoXian_GongShang;
    private CheckBox mCheckZaiJingBaoXian_ShengYu;

    private CheckBox mCheckBaoXian_Wu;
    private CheckBox mCheckBaoXian_YangLao;
    private CheckBox mCheckBaoXian_ShiYe;
    private CheckBox mCheckBaoXian_YiLiao;
    private CheckBox mCheckBaoXian_GongShang;
    private CheckBox mCheckBaoXian_ShengYu;

    // 备注
    private EditText mEditBeiZhu;

    // 按钮
    private View mLayoutXiuGaiAnNiu;
    private View mLayoutLuRuAnNiu;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    cropImage();
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

            case CHOOSE_ROOM_ID_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String id = data.getStringExtra(RentRoomListActivity.CHOOSE_RENT_ROOM_ID);
                        mEditFangWuDengJiBiaoXuHao.setText(id);

                        MyContentProvider contentProvider = new MyContentProvider();
                        Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI, new String[]{FangWuColumns.SZDXZZT},
                                FangWuColumns.FWID + " LIKE '" + id + "'", null, null);
                        if (cursor != null) {
                            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                                String address = cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXZZT));
                                mEditXianZhuDiXiangZhi.setText(address);
                            }

                            cursor.close();
                        }
                    }
                }
                break;

            case CHOOSE_PERSON_ID_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String id = data.getStringExtra(FloatingPersonListActivity.CHOOSE_PERSON_ID);
                        mEditHuZhuDengJiBiaoXuHao.setText(id);
                    }
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
        if (intent.hasExtra(SELECTED_PERSON_ID)) {
            mSelectedID = intent.getStringExtra(SELECTED_PERSON_ID);
            mIsAdd = false;
        } else {
            mIsAdd = true;
        }

        if (intent.hasExtra(RentRoomActivity.SELECTED_ROOM_ID)) {
            mSelectedRoomID = intent.getStringExtra(RentRoomActivity.SELECTED_ROOM_ID);
            mHasRoomID = true;
        } else {
            mHasRoomID = false;
        }

        super.onCreate(savedInstanceState);
        if (!mIsAdd) loadData();
        if (mHasRoomID) setRenterInformation();

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        initCardReader();
    }

    @Override
    protected void onResume() {
        super.onResume();

        reading = false;
        //mButtonCaiJiShenFenZheng.setText("采集身份证");
        UsbMonitorService.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayerUtils.stopSound();

        cancelReadThread();
        UsbMonitorService.stop(this);
    }

    @Override
    protected void onExitActivity() {
        super.onExitActivity();

        cancelReadThread();
        try {
            if (readerDriver != null)
                readerDriver.EnterSavePowerModeNoReply();
            readerDriver = null;
            if (Reader != null)
                Reader.CloseSerialPort();
            Reader = null;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_caijizhoapian:
                if (mZhaoPianUriArray.size() >= Config.MAX_PERSON_PHOTO_COUNT) {
                    Dialog dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.app_name)
                            .setMessage("照片数已经" + Config.MAX_PERSON_PHOTO_COUNT + "个超过了。您删除照片吗？")
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

            case R.id.button_jiansuoguiji:
                onSearchRegisterHistory();
                break;

            case R.id.button_caijishenfenzheng:
                onReadIDCard();
                break;

            case R.id.button_baocun:
                onSave();
                break;

            case R.id.button_xiugai:
                mIsUpdate = true;
                mLayoutLuRuAnNiu.setVisibility(View.VISIBLE);
                mLayoutXiuGaiAnNiu.setVisibility(View.GONE);
                findViewById(R.id.button_caijifangwushexiang).setVisibility(View.GONE);
                mButtonCaiJiZhaoPian.setVisibility(View.VISIBLE);
                setEnableModify(true);
                break;

            case R.id.button_shanchu:
                onDelete();
                break;

            case R.id.button_xuanzehuzhu:
                onSelectHouseHolder();
                break;

            case R.id.button_xuanzefangwu:
                onSelectRoom();
                break;

            case R.id.image_zhaopian:
                if (mZhaoPianUriArray.size() > 0)
                    onPreviewPhoto();
                break;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();

        View rootView = mLayoutInflater.inflate(R.layout.activity_floating_person, mLayoutContainer);

        CommonUtils.changeForeColorInTextView(this, "*", rootView);

        // 来京人员基本信息
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
                    mEditXianZhuDi_SheQu.setText(districtName);
                } else {
                    mEditXianZhuDi_SheQu.setText("");
                }

                ArrayList<String> spinnerArray = new ArrayList<String>();
                spinnerArray.add("请选择");

                if (!"请选择".equals(districtName)) {
                    mGuanLiYuanMap = contentProvider.getManagerInfoMapFromDistrictName(districtName);
                    Set<String> nameSet = mGuanLiYuanMap.keySet();

                    for (String name : nameSet)
                        spinnerArray.add(name);
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        FloatingPersonActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
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
                spinnerArrayAdapter = new ArrayAdapter<String>(FloatingPersonActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerXianZhuDi_JieDao.setAdapter(spinnerArrayAdapter);

                if (!TextUtils.isEmpty(mStrXianZhuDi_JieDao)) {
                    CommonUtils.selectSpinnerItem(mSpinnerXianZhuDi_JieDao, mStrXianZhuDi_JieDao);
                    mSpinnerXianZhuDi_JieDao.setEnabled(false);
                    mStrXianZhuDi_JieDao = "";
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
        mEditTianBiaoRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(FloatingPersonActivity.this, mEditTianBiaoRiQi));

        /* 来京人员基本信息 */
        mButtonCaiJiZhaoPian = (Button) findViewById(R.id.button_caijizhoapian);
        mButtonCaiJiZhaoPian.setOnClickListener(this);
        mButtonJianSuoGuiJi = (Button) findViewById(R.id.button_jiansuoguiji);
        mButtonJianSuoGuiJi.setOnClickListener(this);
        mButtonJianSuoGuiJi.setVisibility(View.GONE);
        mButtonCaiJiShenFenZheng = (Button) findViewById(R.id.button_caijishenfenzheng);
        mButtonCaiJiShenFenZheng.setOnClickListener(this);
        mImageZhaoPian = (ImageView) findViewById(R.id.image_zhaopian);
        mImageZhaoPian.setImageResource(R.drawable.avatar);
        mImageZhaoPian.setOnClickListener(this);

        mEditXingMing = (EditText) findViewById(R.id.edit_xingming);
        mEditShenFenZhengHaoMa = (EditText) findViewById(R.id.edit_shenfenzhenghaoma);
        mCheckZanWu = (CheckBox) findViewById(R.id.check_zanwu);

        mEditChuShengRiQi = (EditText) findViewById(R.id.edit_chushengriqi);
        mSpinnerXingBie = (Spinner) findViewById(R.id.spinner_xingbie);

        mSpinnerMinZu = (Spinner) findViewById(R.id.spinner_minzu);
        mLayoutMinZuXiangXiXinXi = findViewById(R.id.layout_minzuxiangxixinxi);
        mEditMinZuXiangXiXinXi = (EditText) findViewById(R.id.edit_minzuxiangxixinxi);

        mSpinnerZhengZhiMianMao = (Spinner) findViewById(R.id.spinner_zhengzhimianmao);
        mSpinnerShouJiaoYuChengDu = (Spinner) findViewById(R.id.spinner_shoujiaoyuchengdu);
        mSpinnerHuJiLeiBie = (Spinner) findViewById(R.id.spinner_hujileibie);
        mSpinnerHunYinZhuangKuang = (Spinner) findViewById(R.id.spinner_hunyinzhuangkuang);
        mEditLianXiDianHua = (EditText) findViewById(R.id.edit_lianxidianhua);

        mSpinnerHuJiDiZhi_Sheng = (Spinner) findViewById(R.id.spinner_hujidizhi_sheng);
        mSpinnerHuJiDiZhi_Shi = (Spinner) findViewById(R.id.spinner_hujidizhi_shi);
        mSpinnerHuJiDiZhi_Qu = (Spinner) findViewById(R.id.spinner_hujidizhi_qu);
        mEditHuJiXiangXiDiZhi = (EditText) findViewById(R.id.edit_hujidixiangxidizhi);

        mSpinnerChuShengDi = (Spinner) findViewById(R.id.spinner_chushengdi);
        mSpinnerJuZhuZhengJian = (Spinner) findViewById(R.id.spinner_juzhuzhengjian);
        mSpinnerHunYuZhengMing = (Spinner) findViewById(R.id.spinner_hunyuzhengming);
        mSpinnerMianYiJieZhongZheng = (Spinner) findViewById(R.id.spinner_mianyijiezhongzheng);

        //
        mCheckZanWu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEditShenFenZhengHaoMa.setEnabled(false);
                    mEditShenFenZhengHaoMa.setText("");
                } else {
                    mEditShenFenZhengHaoMa.setEnabled(true);
                }
            }
        });

        mEditShenFenZhengHaoMa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String idNumber = mEditShenFenZhengHaoMa.getText().toString();

                    if (TextUtils.isEmpty(idNumber)) {
                        CommonUtils.createErrorAlertDialog(FloatingPersonActivity.this,
                                "“" + "身份证号码" + "”不能为空，请将信息补充完整！").show();
                    } else {
                        idNumber = IDCardUtil.verifyCardID(FloatingPersonActivity.this, idNumber, "人员");
                        if (idNumber != null) {
                            setPersonInformationFromID(idNumber);
                        }
                    }
                }
            }
        });

        mEditChuShengRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(FloatingPersonActivity.this, mEditChuShengRiQi));

        mSpinnerMinZu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerMinZu.getSelectedItem().toString().equals("其他")) {
                    mLayoutMinZuXiangXiXinXi.setVisibility(View.VISIBLE);
                } else {
                    mLayoutMinZuXiangXiXinXi.setVisibility(View.INVISIBLE);
                    mEditMinZuXiangXiXinXi.setText("");
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
        mSpinnerHuJiDiZhi_Sheng.setAdapter(spinnerArrayAdapter);
        mSpinnerHuJiDiZhi_Sheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sheng = mSpinnerHuJiDiZhi_Sheng.getSelectedItem().toString();

                ArrayList<String> spinnerArray = new ArrayList<String>();

                if (!"请选择".equals(sheng)) {
                    spinnerArray = contentProvider.getShi1(sheng);
                }

                spinnerArray.add(0, "请选择");
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        FloatingPersonActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
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

        if (mIsAdd) {
            spinnerArray = new ArrayList<String>();
            spinnerArray.add("请选择");
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerHuJiDiZhi_Shi.setAdapter(spinnerArrayAdapter);
        } else {
            mSpinnerHuJiDiZhi_Shi.setEnabled(false);
        }
        mSpinnerHuJiDiZhi_Shi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String shi = mSpinnerHuJiDiZhi_Shi.getSelectedItem().toString();

                ArrayList<String> spinnerArray = new ArrayList<String>();

                if (!"请选择".equals(shi)) {
                    spinnerArray = contentProvider.getQX(shi);
                }

                spinnerArray.add(0, "请选择");
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        FloatingPersonActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerHuJiDiZhi_Qu.setAdapter(spinnerArrayAdapter);

                if (!TextUtils.isEmpty(mStrHuJiDiZhi_Qu)) {
                    CommonUtils.selectSpinnerItem(mSpinnerHuJiDiZhi_Qu, mStrHuJiDiZhi_Qu);
                    mStrHuJiDiZhi_Qu = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerArray = new ArrayList<String>();
        spinnerArray.add("请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerHuJiDiZhi_Qu.setAdapter(spinnerArrayAdapter);

        /* 家庭户流入 */
        mSpinnerJiaTingHuLiuRu = (Spinner) findViewById(R.id.spinner_jiatinghuliuru);
        mLayoutHuZhu = findViewById(R.id.layout_huzhu);
        mSpinnerHuZhu = (Spinner) findViewById(R.id.spinner_huzhu);

        mLayoutBenHuWaiLaiRenKouShu = findViewById(R.id.layout_benhuwailairenkoushu);
        mEditBenHuWaiLaiRenKouShu = (EditText) findViewById(R.id.edit_benhuwailairenkoushu);
        mEditBenHuWaiLaiRenKouShu_16 = (EditText) findViewById(R.id.edit_benhuwailairenkoushu_16);
        mEditBenHuWaiLaiRenKouShu_Nan = (EditText) findViewById(R.id.edit_benhuwailairenkoushu_nan);
        mEditBenHuWaiLaiRenKouShu_Nv = (EditText) findViewById(R.id.edit_benhuwailairenkoushu_nv);

        mLayoutYuHuZhuGuanXi = findViewById(R.id.layout_yuhuzhuguanxi);
        mSpinnerYuHuZhuGuanXi = (Spinner) findViewById(R.id.spinner_yuhuzhuguanxi);
        mEditHuZhuDengJiBiaoXuHao = (EditText) findViewById(R.id.edit_huzhudengjibiaoxuhao);
        mButtonXuanZeHuZhu = (Button) findViewById(R.id.button_xuanzehuzhu);
        mButtonXuanZeHuZhu.setOnClickListener(this);

        mLayoutYuHuZhuXiangXiGuanXi = findViewById(R.id.layout_yuhuzhuxiangxiguanxi);
        mEditYuHuZhuXiangXiGuanXi = (EditText) findViewById(R.id.edit_yuhuzhuxiangxiguanxi);

        //
        mSpinnerJiaTingHuLiuRu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerJiaTingHuLiuRu.getSelectedItem().toString().equals("是")) {
                    mLayoutHuZhu.setVisibility(View.VISIBLE);
                    mLayoutYuHuZhuGuanXi.setVisibility(View.GONE);
                    mLayoutBenHuWaiLaiRenKouShu.setVisibility(View.GONE);
                    mLayoutYuHuZhuXiangXiGuanXi.setVisibility(View.GONE);
                } else {
                    mLayoutHuZhu.setVisibility(View.GONE);
                    mLayoutYuHuZhuGuanXi.setVisibility(View.GONE);
                    mLayoutBenHuWaiLaiRenKouShu.setVisibility(View.GONE);
                    mLayoutYuHuZhuXiangXiGuanXi.setVisibility(View.GONE);

                    mSpinnerHuZhu.setSelection(0);
                    mSpinnerYuHuZhuGuanXi.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mEditBenHuWaiLaiRenKouShu_16.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    mEditBenHuWaiLaiRenKouShu_Nan.setEnabled(true);
                    mEditBenHuWaiLaiRenKouShu_Nv.setEnabled(true);
                } else {
                    mEditBenHuWaiLaiRenKouShu_Nan.setEnabled(false);
                    mEditBenHuWaiLaiRenKouShu_Nv.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSpinnerHuZhu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String string = mSpinnerHuZhu.getSelectedItem().toString();

                if (string.equals("是")) {
                    mLayoutBenHuWaiLaiRenKouShu.setVisibility(View.VISIBLE);
                    mLayoutYuHuZhuGuanXi.setVisibility(View.GONE);
                    mLayoutYuHuZhuXiangXiGuanXi.setVisibility(View.GONE);

                    mSpinnerYuHuZhuGuanXi.setSelection(0);
                } else if (string.equals("否")) {
                    mLayoutBenHuWaiLaiRenKouShu.setVisibility(View.GONE);
                    mLayoutYuHuZhuGuanXi.setVisibility(View.VISIBLE);
                    mLayoutYuHuZhuXiangXiGuanXi.setVisibility(View.GONE);
                } else {
                    mLayoutBenHuWaiLaiRenKouShu.setVisibility(View.GONE);
                    mLayoutYuHuZhuGuanXi.setVisibility(View.GONE);
                    mLayoutYuHuZhuXiangXiGuanXi.setVisibility(View.GONE);

                    mSpinnerYuHuZhuGuanXi.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerYuHuZhuGuanXi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerYuHuZhuGuanXi.getSelectedItem().toString().equals("其他"))
                    mLayoutYuHuZhuXiangXiGuanXi.setVisibility(View.VISIBLE);
                else
                    mLayoutYuHuZhuXiangXiGuanXi.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* 居住信息 */
        mEditLiKaiYuanJiRiQi = (EditText) findViewById(R.id.edit_likaiyuanjiriqi);
        mEditLaiJingRiQi = (EditText) findViewById(R.id.edit_laijingriqi);
        mEditLaiXianZhuDiRiQi = (EditText) findViewById(R.id.edit_laixianzhudiriqi);

        mSpinnerLaiJingYuanYin = (Spinner) findViewById(R.id.spinner_laijingyuanyin);
        mLayoutLaiJingQiTaXiangXiYuanYin = findViewById(R.id.layout_laijingqitaxiangxi);
        mEditLaiJingQiTaXiangXiYuanYin = (EditText) findViewById(R.id.edit_laijingqitaxiangxi);

        mSpinnerJuZhuLeiXing = (Spinner) findViewById(R.id.spinner_juzhuleixing);
        mLayoutJuZhuQiTaLeiXing = findViewById(R.id.layout_juzhuqitaleixing);
        mEditJuZhuQiTaLeiXing = (EditText) findViewById(R.id.edit_juzhuqitaleixing);

        mLayoutFangWuDengJiBiaoXuHao = findViewById(R.id.layout_fangwudengjibiaoxuhao);
        mEditFangWuDengJiBiaoXuHao = (EditText) findViewById(R.id.edit_fangwudengjibiaoxuhao);
        mButtonXuanZeFangWu = (Button) findViewById(R.id.button_xuanzefangwu);
        mButtonXuanZeFangWu.setOnClickListener(this);

        mLayoutXianZhuDi = findViewById(R.id.layout_xianzhudi1);
        mSpinnerXianZhuDi_FenJu = (Spinner) findViewById(R.id.spinner_xianzhudi_fenju);
        mSpinnerXianZhuDi_PaiChuSuo = (Spinner) findViewById(R.id.spinner_xianzhudi_paichusuo);

        mLayoutXianZhuDi2 = findViewById(R.id.layout_xianzhudi2);
        mEditXianZhuDi_SheQu = (EditText) findViewById(R.id.edit_xianzhudi_shequ);
        mEditXianZhuDi_SheQu.setEnabled(false);
        mSpinnerXianZhuDi_JieDao = (Spinner) findViewById(R.id.spinner_xianzhudi_jiedao);

        mEditXianZhuDiXiangZhi = (EditText) findViewById(R.id.edit_xianzhudixiangzhi);
        mTextXianZhuDiXiangZhi = (TextView) findViewById(R.id.text_xianzhudixiangzhi);
        mEditSuoShuPaiChuSuoMingCheng = (EditText) findViewById(R.id.edit_suoshupaichusuomingcheng);
        mEditMinJingXingMing = (EditText) findViewById(R.id.edit_minjingxingming);

        //
        mEditSuoShuPaiChuSuoMingCheng.setText(mOwnerData.SSDWMC);
        mEditMinJingXingMing.setText(mOwnerData.MJXM);

        mEditLiKaiYuanJiRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(FloatingPersonActivity.this, mEditLiKaiYuanJiRiQi));
        mEditLaiJingRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(FloatingPersonActivity.this, mEditLaiJingRiQi));
        mEditLaiXianZhuDiRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(FloatingPersonActivity.this, mEditLaiXianZhuDiRiQi));

        mSpinnerLaiJingYuanYin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerLaiJingYuanYin.getSelectedItem().toString().equals("其他"))
                    mLayoutLaiJingQiTaXiangXiYuanYin.setVisibility(View.VISIBLE);
                else
                    mLayoutLaiJingQiTaXiangXiYuanYin.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerJuZhuLeiXing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String string = mSpinnerJuZhuLeiXing.getSelectedItem().toString();

                if (string.equals("租赁房屋")) {
                    mLayoutJuZhuQiTaLeiXing.setVisibility(View.GONE);
                    mLayoutXianZhuDi.setVisibility(View.GONE);
                    mLayoutXianZhuDi2.setVisibility(View.GONE);
                    mTextXianZhuDiXiangZhi.setVisibility(View.GONE);
                    mLayoutFangWuDengJiBiaoXuHao.setVisibility(View.VISIBLE);
                } else {
                    if (string.equals("其他"))
                        mLayoutJuZhuQiTaLeiXing.setVisibility(View.VISIBLE);
                    else
                        mLayoutJuZhuQiTaLeiXing.setVisibility(View.GONE);

                    mLayoutXianZhuDi.setVisibility(View.VISIBLE);
                    mLayoutXianZhuDi2.setVisibility(View.VISIBLE);
                    mTextXianZhuDiXiangZhi.setVisibility(View.GONE/*VISIBLE*/);
                    mLayoutFangWuDengJiBiaoXuHao.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerXianZhuDi_FenJu.setEnabled(false);
        spinnerArray = new ArrayList<String>();
        spinnerArray.add(contentProvider.getFenJuNameFromPaiChuSuoID(mOwnerData.SSDWID));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerXianZhuDi_FenJu.setAdapter(spinnerArrayAdapter);

        spinnerArray = new ArrayList<String>();
        spinnerArray.add(mOwnerData.SSDWMC);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerXianZhuDi_PaiChuSuo.setAdapter(spinnerArrayAdapter);
        mSpinnerXianZhuDi_PaiChuSuo.setEnabled(false);

        /* 就业社保信息 */
        mSpinnerMuQianZhuangKuang = (Spinner) findViewById(R.id.spinner_muqianzhuangkuang);

        mLayoutMuQianZhuangKuangXiangXiXinXi = findViewById(R.id.layout_muqianzhuangkuangxiangxixinxi);
        mEditMuQianZhuangKuangXiangXiXinXi = (EditText) findViewById(R.id.edit_muqianzhuangkuangxiangxixinxi);

        mLayoutXueXiaoXinXi = findViewById(R.id.layout_xuexiaoxinxi);
        mEditXueXiaoMingCheng = (EditText) findViewById(R.id.edit_xuexiaomingcheng);
        mEditXueXiaoSuoZaiDi = (EditText) findViewById(R.id.edit_xuexiaosuozaidi);

        mLayoutJiuYeXinXi = findViewById(R.id.layout_jiuyexinxi);
        mEditJiuYeDanWeiMingCheng = (EditText) findViewById(R.id.edit_jiuyedanweimingcheng);
        mEditDanWeiDengJiBiaoXuHao = (EditText) findViewById(R.id.edit_jiuyedanweidengjibiaoxuhao);
        mEditJiuYeDanWeiXiangXiDiZhi = (EditText) findViewById(R.id.edit_jiuyedanweixiangxidizhi);

        mSpinnerJiuYeDanWeiSuoShuHangYe = (Spinner) findViewById(R.id.spinner_jiuyedanweisuoshuhangye);
        mEditQiTaHangYe = (EditText) findViewById(R.id.edit_qitahangye);
        mSpinnerZhuYaoCongShiGongZuo = (Spinner) findViewById(R.id.spinner_zhuyaocongshigongzuo);
        mEditQiTaZhuYaoCongShiGongZuo = (EditText) findViewById(R.id.edit_qitazhuyaocongshigongzuo);

        mSpinnerZhiYe = (Spinner) findViewById(R.id.spinner_zhiye);
        mSpinnerQianDingLaoDongHeTong = (Spinner) findViewById(R.id.spinner_qiandinglaodonghetong);
        mSpinnerJiuYeDanWeiSuoZaiDi = (Spinner) findViewById(R.id.spinner_jiuyedanweisuozaidi);

        mCheckZaiJingBaoXian_Wu = (CheckBox) findViewById(R.id.check_zaijing_baoxian_wu);
        mCheckZaiJingBaoXian_YangLao = (CheckBox) findViewById(R.id.check_zaijing_baoxian_yanglao);
        mCheckZaiJingBaoXian_ShiYe = (CheckBox) findViewById(R.id.check_zaijing_baoxian_shiye);
        mCheckZaiJingBaoXian_YiLiao = (CheckBox) findViewById(R.id.check_zaijing_baoxian_yiliao);
        mCheckZaiJingBaoXian_GongShang = (CheckBox) findViewById(R.id.check_zaijing_baoxian_gongshang);
        mCheckZaiJingBaoXian_ShengYu = (CheckBox) findViewById(R.id.check_zaijing_baoxian_shengyu);

        mCheckBaoXian_Wu = (CheckBox) findViewById(R.id.check_baoxian_wu);
        mCheckBaoXian_YangLao = (CheckBox) findViewById(R.id.check_baoxian_yanglao);
        mCheckBaoXian_ShiYe = (CheckBox) findViewById(R.id.check_baoxian_shiye);
        mCheckBaoXian_YiLiao = (CheckBox) findViewById(R.id.check_baoxian_yiliao);
        mCheckBaoXian_GongShang = (CheckBox) findViewById(R.id.check_baoxian_gongshang);
        mCheckBaoXian_ShengYu = (CheckBox) findViewById(R.id.check_baoxian_shengyu);

        //
        mSpinnerMuQianZhuangKuang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String string = mSpinnerMuQianZhuangKuang.getSelectedItem().toString();

                if (string.equals("其他")) {
                    mLayoutMuQianZhuangKuangXiangXiXinXi.setVisibility(View.VISIBLE);
                    mLayoutJiuYeXinXi.setVisibility(View.GONE);
                    mLayoutXueXiaoXinXi.setVisibility(View.GONE);
                } else if (string.equals("就业")) {
                    mLayoutMuQianZhuangKuangXiangXiXinXi.setVisibility(View.GONE);
                    mLayoutJiuYeXinXi.setVisibility(View.VISIBLE);
                    mLayoutXueXiaoXinXi.setVisibility(View.GONE);
                } else if (string.equals("学生")) {
                    mLayoutMuQianZhuangKuangXiangXiXinXi.setVisibility(View.GONE);
                    mLayoutJiuYeXinXi.setVisibility(View.GONE);
                    mLayoutXueXiaoXinXi.setVisibility(View.VISIBLE);
                } else {
                    mLayoutMuQianZhuangKuangXiangXiXinXi.setVisibility(View.GONE);
                    mLayoutJiuYeXinXi.setVisibility(View.GONE);
                    mLayoutXueXiaoXinXi.setVisibility(View.GONE);
                }

                if (!string.equals("就业")) {
                    mSpinnerJiuYeDanWeiSuoShuHangYe.setSelection(0);
                    mSpinnerZhuYaoCongShiGongZuo.setSelection(0);
                    mSpinnerZhiYe.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ArrayList<ArrayList<String>> gongzuoArray = new ArrayList<ArrayList<String>>();
        ArrayList<String> allList = new ArrayList<String>();

        for (int i = 0; i < 21; i++) {
            int identifier = getResources().getIdentifier(String.format("array_main_job_type%d", i + 1),
                    "array", this.getPackageName());

            if (identifier != 0) {
                String[] items = getResources().getStringArray(identifier);
                ArrayList<String> arrayList = new ArrayList<String>();
                Collections.addAll(arrayList, items);

                gongzuoArray.add(arrayList);
                for (String item : arrayList)
                    allList.add(item);
            }
        }
        gongzuoArray.add(0, allList);

        mSpinnerJiuYeDanWeiSuoShuHangYe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> spinnerArray = new ArrayList<String>();
                spinnerArray.add("请选择");
                for (String item : gongzuoArray.get(position))
                    spinnerArray.add(item);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(FloatingPersonActivity.this,
                        android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerZhuYaoCongShiGongZuo.setAdapter(spinnerArrayAdapter);

                if (mSpinnerJiuYeDanWeiSuoShuHangYe.getSelectedItem().toString().equals("其他行业")) {
                    mEditQiTaHangYe.setVisibility(View.VISIBLE);
                    mEditQiTaZhuYaoCongShiGongZuo.setVisibility(View.VISIBLE);
                    CommonUtils.selectSpinnerItem(mSpinnerZhuYaoCongShiGongZuo, "其他工作（描述）");
                } else {
                    mEditQiTaHangYe.setVisibility(View.INVISIBLE);
                    mEditQiTaZhuYaoCongShiGongZuo.setVisibility(View.INVISIBLE);
                    mEditQiTaHangYe.setText("");
                    mEditQiTaZhuYaoCongShiGongZuo.setText("");

                    if (!TextUtils.isEmpty(mStrZhuYaoCongShiGongZuo)) {
                        CommonUtils.selectSpinnerItem(mSpinnerZhuYaoCongShiGongZuo, mStrZhuYaoCongShiGongZuo);
                        mSpinnerZhuYaoCongShiGongZuo.setEnabled(false);
                        mStrZhuYaoCongShiGongZuo = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerZhuYaoCongShiGongZuo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerZhuYaoCongShiGongZuo.getSelectedItem().toString().contains("（描述）")) {
                    mEditQiTaZhuYaoCongShiGongZuo.setVisibility(View.VISIBLE);
                } else {
                    mEditQiTaZhuYaoCongShiGongZuo.setVisibility(View.INVISIBLE);
                    mEditQiTaZhuYaoCongShiGongZuo.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerArray = contentProvider.getShi("北京市");
        spinnerArray.add(0, "请选择");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerJiuYeDanWeiSuoZaiDi.setAdapter(spinnerArrayAdapter);

        mCheckBaoXian_Wu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckZaiJingBaoXian_Wu.setChecked(true);

                    mCheckBaoXian_YangLao.setChecked(false);
                    mCheckBaoXian_ShiYe.setChecked(false);
                    mCheckBaoXian_YiLiao.setChecked(false);
                    mCheckBaoXian_GongShang.setChecked(false);
                    mCheckBaoXian_ShengYu.setChecked(false);
                }/* else {
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                }*/
            }
        });

        mCheckBaoXian_YangLao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mCheckBaoXian_Wu.setChecked(false);
                else
                    mCheckZaiJingBaoXian_YangLao.setChecked(false);
            }
        });

        mCheckBaoXian_ShiYe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mCheckBaoXian_Wu.setChecked(false);
                else
                    mCheckZaiJingBaoXian_ShiYe.setChecked(false);
            }
        });

        mCheckBaoXian_YiLiao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mCheckBaoXian_Wu.setChecked(false);
                else
                    mCheckZaiJingBaoXian_YiLiao.setChecked(false);
            }
        });

        mCheckBaoXian_GongShang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mCheckBaoXian_Wu.setChecked(false);
                else
                    mCheckZaiJingBaoXian_GongShang.setChecked(false);
            }
        });

        mCheckBaoXian_ShengYu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mCheckBaoXian_Wu.setChecked(false);
                else
                    mCheckBaoXian_ShengYu.setChecked(false);
            }
        });

        mCheckZaiJingBaoXian_Wu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckZaiJingBaoXian_Wu.setChecked(true);

                    mCheckZaiJingBaoXian_YangLao.setChecked(false);
                    mCheckZaiJingBaoXian_ShiYe.setChecked(false);
                    mCheckZaiJingBaoXian_YiLiao.setChecked(false);
                    mCheckZaiJingBaoXian_GongShang.setChecked(false);
                    mCheckZaiJingBaoXian_ShengYu.setChecked(false);
                }
            }
        });

        mCheckZaiJingBaoXian_YangLao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckBaoXian_Wu.setChecked(false);
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_YangLao.setChecked(true);
                }
            }
        });

        mCheckZaiJingBaoXian_ShiYe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckBaoXian_Wu.setChecked(false);
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_ShiYe.setChecked(true);
                }
            }
        });

        mCheckZaiJingBaoXian_YiLiao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckBaoXian_Wu.setChecked(false);
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_YiLiao.setChecked(true);
                }
            }
        });

        mCheckZaiJingBaoXian_GongShang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckBaoXian_Wu.setChecked(false);
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_GongShang.setChecked(true);
                }
            }
        });

        mCheckZaiJingBaoXian_ShengYu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckBaoXian_Wu.setChecked(false);
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_ShengYu.setChecked(true);
                }
            }
        });

        /* 备注 */
        mEditBeiZhu = (EditText) findViewById(R.id.edit_beizhu);

        /* 按钮 */
        mLayoutLuRuAnNiu = findViewById(R.id.layout_luru_anniu);
        mLayoutXiuGaiAnNiu = findViewById(R.id.layout_xiugai_anniu);

        if (mIsAdd) {
            mButtonCaiJiShenFenZheng.setVisibility(View.VISIBLE);
            mLayoutLuRuAnNiu.setVisibility(View.VISIBLE);
            mLayoutXiuGaiAnNiu.setVisibility(View.GONE);
            setEnableModify(true);
        } else {
            mButtonCaiJiShenFenZheng.setVisibility(View.GONE);
            mLayoutLuRuAnNiu.setVisibility(View.GONE);
            mLayoutXiuGaiAnNiu.setVisibility(View.VISIBLE);
            setEnableModify(false);
        }

        findViewById(R.id.button_baocun).setOnClickListener(this);
        findViewById(R.id.button_caijifangwutuxiang).setVisibility(View.GONE);
        findViewById(R.id.button_caijifangwushexiang).setVisibility(View.GONE);

        findViewById(R.id.button_xiugai).setOnClickListener(this);
        findViewById(R.id.button_shanchu).setOnClickListener(this);
        findViewById(R.id.button_dangqianzuzhuren).setVisibility(View.GONE);
        findViewById(R.id.button_tianjiazuzhuren).setVisibility(View.GONE);

        /* 从RentRoomActivity */
        if (mHasRoomID) {
            CommonUtils.selectSpinnerItem(mSpinnerJuZhuLeiXing, "租赁房屋");
            mSpinnerJuZhuLeiXing.setEnabled(false);

            mEditFangWuDengJiBiaoXuHao.setText(mSelectedRoomID);
            mEditFangWuDengJiBiaoXuHao.setEnabled(false);
            mButtonXuanZeFangWu.setVisibility(View.GONE);
        }
    }

    /**
     * Set enable to all views
     */
    private void setEnableModify(boolean enabled) {
        if (enabled) {
            CommonUtils.enableViewGroup(mLayoutContainer, true);

            mEditGuanLiYuanBianHao.setEnabled(false);
            mEditDengJiBiaoXuHao.setEnabled(false);
            mEditSuoShuPaiChuSuoMingCheng.setEnabled(false);
            mEditMinJingXingMing.setEnabled(false);

            mSpinnerXianZhuDi_FenJu.setEnabled(false);
            mSpinnerXianZhuDi_PaiChuSuo.setEnabled(false);

            mEditFangWuDengJiBiaoXuHao.setEnabled(false);

            if (TextUtils.isEmpty(mEditBenHuWaiLaiRenKouShu_16.getText().toString())) {
                mEditBenHuWaiLaiRenKouShu_Nan.setEnabled(false);
                mEditBenHuWaiLaiRenKouShu_Nv.setEnabled(false);
            }
        } else {
            CommonUtils.enableViewGroup(mLayoutContainer, false);
            CommonUtils.enableViewGroup((ViewGroup) mLayoutXiuGaiAnNiu, true);
            mButtonJianSuoGuiJi.setEnabled(true);
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

        // 来京人员基本信息
        if (TextUtils.isEmpty(mEditXingMing.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "姓名" + "”" + warning).show();
            mEditXingMing.requestFocusFromTouch();
            return false;
        }
        if (!mCheckZanWu.isChecked()) {
            String idNumber = mEditShenFenZhengHaoMa.getText().toString();

            if (TextUtils.isEmpty(idNumber)) {
                CommonUtils.createErrorAlertDialog(this, "“" + "身份证号码" + "”" + warning).show();
                mEditShenFenZhengHaoMa.requestFocusFromTouch();
                return false;
            } else {
                idNumber = IDCardUtil.verifyCardID(FloatingPersonActivity.this, idNumber, "人员");
                if (idNumber == null) {
                    mEditShenFenZhengHaoMa.requestFocusFromTouch();
                    return false;
                }
            }
        }
        if (TextUtils.isEmpty(mEditChuShengRiQi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "出生日期" + "”" + warning).show();
            mEditChuShengRiQi.requestFocusFromTouch();
            return false;
        }

        if (mSpinnerXingBie.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "性别" + "”" + warning).show();
            mSpinnerXingBie.setFocusableInTouchMode(true);
            mSpinnerXingBie.requestFocusFromTouch();
            mSpinnerXingBie.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerMinZu.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "民族" + "”" + warning).show();
            mSpinnerMinZu.setFocusableInTouchMode(true);
            mSpinnerMinZu.requestFocusFromTouch();
            mSpinnerMinZu.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerMinZu.getSelectedItem().toString().equals("其他")
                && TextUtils.isEmpty(mEditMinZuXiangXiXinXi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "民族详细信息" + "”" + warning).show();
            mEditMinZuXiangXiXinXi.requestFocusFromTouch();
            return false;
        }

        if (mSpinnerZhengZhiMianMao.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "政治面貌" + "”" + warning).show();
            mSpinnerZhengZhiMianMao.setFocusableInTouchMode(true);
            mSpinnerZhengZhiMianMao.requestFocusFromTouch();
            mSpinnerZhengZhiMianMao.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerShouJiaoYuChengDu.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "受教育程度" + "”" + warning).show();
            mSpinnerShouJiaoYuChengDu.setFocusableInTouchMode(true);
            mSpinnerShouJiaoYuChengDu.requestFocusFromTouch();
            mSpinnerShouJiaoYuChengDu.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerHuJiLeiBie.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "户籍类别" + "”" + warning).show();
            mSpinnerHuJiLeiBie.setFocusableInTouchMode(true);
            mSpinnerHuJiLeiBie.requestFocusFromTouch();
            mSpinnerHuJiLeiBie.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerHunYinZhuangKuang.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "婚姻状况" + "”" + warning).show();
            mSpinnerHunYinZhuangKuang.setFocusableInTouchMode(true);
            mSpinnerHunYinZhuangKuang.requestFocusFromTouch();
            mSpinnerHunYinZhuangKuang.setFocusableInTouchMode(false);
            return false;
        }

        if (mSpinnerHuJiDiZhi_Sheng.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "户籍地址（省）" + "”" + warning).show();
            mSpinnerHuJiDiZhi_Sheng.setFocusableInTouchMode(true);
            mSpinnerHuJiDiZhi_Sheng.requestFocusFromTouch();
            mSpinnerHuJiDiZhi_Sheng.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerHuJiDiZhi_Shi.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "户籍地址（市）" + "”" + warning).show();
            mSpinnerHuJiDiZhi_Shi.setFocusableInTouchMode(true);
            mSpinnerHuJiDiZhi_Shi.requestFocusFromTouch();
            mSpinnerHuJiDiZhi_Shi.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerHuJiDiZhi_Qu.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "户籍地址（区）" + "”" + warning).show();
            mSpinnerHuJiDiZhi_Qu.setFocusableInTouchMode(true);
            mSpinnerHuJiDiZhi_Qu.requestFocusFromTouch();
            mSpinnerHuJiDiZhi_Qu.setFocusableInTouchMode(false);
            return false;
        }
        if (TextUtils.isEmpty(mEditHuJiXiangXiDiZhi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "户籍详细地址" + "”" + warning).show();
            mEditHuJiXiangXiDiZhi.requestFocusFromTouch();
            return false;
        }

        /*if (mSpinnerChuShengDi.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "出生地" + "”" + warning).show();
            return false;
        }*/
        if (mSpinnerJuZhuZhengJian.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "居住证件" + "”" + warning).show();
            mSpinnerJuZhuZhengJian.setFocusableInTouchMode(true);
            mSpinnerJuZhuZhengJian.requestFocusFromTouch();
            mSpinnerJuZhuZhengJian.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerHunYuZhengMing.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "婚育证明" + "”" + warning).show();
            mSpinnerHunYuZhengMing.setFocusableInTouchMode(true);
            mSpinnerHunYuZhengMing.requestFocusFromTouch();
            mSpinnerHunYuZhengMing.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerMianYiJieZhongZheng.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "免疫接种证" + "”" + warning).show();
            mSpinnerMianYiJieZhongZheng.setFocusableInTouchMode(true);
            mSpinnerMianYiJieZhongZheng.requestFocusFromTouch();
            mSpinnerMianYiJieZhongZheng.setFocusableInTouchMode(false);
            return false;
        }

        // 家庭户信息
        if (mSpinnerJiaTingHuLiuRu.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "家庭户流入" + "”" + warning).show();
            mSpinnerJiaTingHuLiuRu.setFocusableInTouchMode(true);
            mSpinnerJiaTingHuLiuRu.requestFocusFromTouch();
            mSpinnerJiaTingHuLiuRu.setFocusableInTouchMode(false);
            return false;
        }
        if (mSpinnerJiaTingHuLiuRu.getSelectedItem().toString().equals("是")) {
            if (mSpinnerHuZhu.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "户主" + "”" + warning).show();
                mSpinnerHuZhu.setFocusableInTouchMode(true);
                mSpinnerHuZhu.requestFocusFromTouch();
                mSpinnerHuZhu.setFocusableInTouchMode(false);
                return false;
            } else if (mSpinnerHuZhu.getSelectedItem().toString().equals("是")) {
                if (TextUtils.isEmpty(mEditBenHuWaiLaiRenKouShu.getText().toString())) {
                    CommonUtils.createErrorAlertDialog(this, "“" + "本户外来人口数" + "”" + warning).show();
                    mEditBenHuWaiLaiRenKouShu.requestFocusFromTouch();
                    return false;
                }
                int totalCount = Integer.parseInt(mEditBenHuWaiLaiRenKouShu.getText().toString());
                if (totalCount < 2) {
                    CommonUtils.createErrorAlertDialog(this, "“" + "本户外来人口数" + "”不能小于2人，请更正！").show();
                    mEditBenHuWaiLaiRenKouShu.requestFocusFromTouch();
                    return false;
                }

                if (!TextUtils.isEmpty(mEditBenHuWaiLaiRenKouShu_16.getText().toString())) {
                    if (TextUtils.isEmpty(mEditBenHuWaiLaiRenKouShu_Nan.getText().toString())) {
                        CommonUtils.createErrorAlertDialog(this, "“" + "本户外来人口数（其中 男）" + "”" + warning).show();
                        mEditBenHuWaiLaiRenKouShu_Nan.requestFocusFromTouch();
                        return false;
                    }
                    if (TextUtils.isEmpty(mEditBenHuWaiLaiRenKouShu_Nv.getText().toString())) {
                        CommonUtils.createErrorAlertDialog(this, "“" + "本户外来人口数（其中 女）" + "”" + warning).show();
                        mEditBenHuWaiLaiRenKouShu_Nv.requestFocusFromTouch();
                        return false;
                    }

                    int totalCount_16 = Integer.parseInt(mEditBenHuWaiLaiRenKouShu_16.getText().toString());
                    if (totalCount_16 > totalCount) {
                        CommonUtils.createErrorAlertDialog(this, "16岁以下人口数应小于本户外来人口总数，请更正！").show();
                        mEditBenHuWaiLaiRenKouShu_16.requestFocusFromTouch();
                        return false;
                    }
                    int count_nan_16 = Integer.parseInt(mEditBenHuWaiLaiRenKouShu_Nan.getText().toString());
                    int count_nv_16 = Integer.parseInt(mEditBenHuWaiLaiRenKouShu_Nv.getText().toString());
                    if ((count_nan_16 + count_nv_16) != totalCount_16) {
                        CommonUtils.createErrorAlertDialog(this, "本户外来人口数16岁以下男女总数不应大于16岁以下人口总数，请更正！").show();
                        mEditBenHuWaiLaiRenKouShu_Nan.requestFocusFromTouch();
                        return false;
                    }
                }
            } else if (mSpinnerHuZhu.getSelectedItem().toString().equals("否")) {
                if (mSpinnerYuHuZhuGuanXi.getSelectedItem().toString().equals("请选择")) {
                    CommonUtils.createErrorAlertDialog(this, "“" + "与户主关系" + "”" + warning).show();
                    mSpinnerYuHuZhuGuanXi.setFocusableInTouchMode(true);
                    mSpinnerYuHuZhuGuanXi.requestFocusFromTouch();
                    mSpinnerYuHuZhuGuanXi.setFocusableInTouchMode(false);
                    return false;
                } else if (mSpinnerYuHuZhuGuanXi.getSelectedItem().toString().equals("其他")
                        && TextUtils.isEmpty(mEditYuHuZhuXiangXiGuanXi.getText().toString())) {
                    CommonUtils.createErrorAlertDialog(this, "“" + "与户主详细关系" + "”" + warning).show();
                    mEditYuHuZhuXiangXiGuanXi.requestFocusFromTouch();
                    return false;
                } else if (TextUtils.isEmpty(mEditHuZhuDengJiBiaoXuHao.getText().toString())) {
                    CommonUtils.createErrorAlertDialog(this, "“" + "户主登记表序号" + "”" + warning).show();
                    mEditHuZhuDengJiBiaoXuHao.requestFocusFromTouch();
                    return false;
                }
            }
        }

        // 居住信息
        String lkyjrq = mEditLiKaiYuanJiRiQi.getText().toString();
        if (!TextUtils.isEmpty(lkyjrq)
                && CommonUtils.getDateFromString(lkyjrq).equals(new Date())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "离开原籍日期" + "”不能大于当前时间！").show();
            mEditLiKaiYuanJiRiQi.requestFocusFromTouch();
            return false;
        }

        String ljrq = mEditLaiJingRiQi.getText().toString();
        if (TextUtils.isEmpty(ljrq)) {
            CommonUtils.createErrorAlertDialog(this, "“" + "来京日期" + "”" + warning).show();
            mEditLaiJingRiQi.requestFocusFromTouch();
            return false;
        } else {
            if (CommonUtils.getDateFromString(ljrq).equals(new Date())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "来京日期" + "”不能大于当前时间！").show();
                mEditLaiJingRiQi.requestFocusFromTouch();
                return false;
            }
            if (!TextUtils.isEmpty(lkyjrq)
                    && CommonUtils.getDateFromString(ljrq).before(CommonUtils.getDateFromString(lkyjrq))) {
                CommonUtils.createErrorAlertDialog(this, "离开原籍日期不能大于来京日期！").show();
                mEditLaiJingRiQi.requestFocusFromTouch();
                return false;
            }
        }

        String lxzdrq = mEditLaiXianZhuDiRiQi.getText().toString();
        if (TextUtils.isEmpty(lxzdrq)) {
            CommonUtils.createErrorAlertDialog(this, "“" + "来现住地日期" + "”" + warning).show();
            mEditLaiXianZhuDiRiQi.requestFocusFromTouch();
            return false;
        } else {
            if (CommonUtils.getDateFromString(lxzdrq).equals(new Date())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "来现住地日期" + "”不能大于当前时间！").show();
                mEditLaiXianZhuDiRiQi.requestFocusFromTouch();
                return false;
            }
            if (!TextUtils.isEmpty(lkyjrq)
                    && CommonUtils.getDateFromString(lkyjrq).after(CommonUtils.getDateFromString(lxzdrq))) {
                CommonUtils.createErrorAlertDialog(this, "离开原籍日期不能大于来现住地日期！").show();
                mEditLaiXianZhuDiRiQi.requestFocusFromTouch();
                return false;
            }
            if (CommonUtils.getDateFromString(ljrq).after(CommonUtils.getDateFromString(lxzdrq))) {
                CommonUtils.createErrorAlertDialog(this, "来京日期不能大于来现住地日期！").show();
                mEditLaiXianZhuDiRiQi.requestFocusFromTouch();
                return false;
            }
        }

        if (mSpinnerLaiJingYuanYin.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "来京原因" + "”" + warning).show();
            mSpinnerLaiJingYuanYin.setFocusableInTouchMode(true);
            mSpinnerLaiJingYuanYin.requestFocusFromTouch();
            mSpinnerLaiJingYuanYin.setFocusableInTouchMode(false);
            return false;
        } else if (mSpinnerLaiJingYuanYin.getSelectedItem().toString().equals("其他")
                && TextUtils.isEmpty(mEditLaiJingQiTaXiangXiYuanYin.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "来京其他详细原因" + "”" + warning).show();
            mEditLaiJingQiTaXiangXiYuanYin.requestFocusFromTouch();
            return false;
        }

        if (mSpinnerJuZhuLeiXing.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "居住类型" + "”" + warning).show();
            mSpinnerJuZhuLeiXing.setFocusableInTouchMode(true);
            mSpinnerJuZhuLeiXing.requestFocusFromTouch();
            mSpinnerJuZhuLeiXing.setFocusableInTouchMode(false);
            return false;
        } else if (mSpinnerJuZhuLeiXing.getSelectedItem().toString().equals("租赁房屋")) {
            if (TextUtils.isEmpty(mEditFangWuDengJiBiaoXuHao.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "房屋登记表序号" + "”" + warning).show();
                mEditFangWuDengJiBiaoXuHao.requestFocusFromTouch();
                return false;
            }
        } else {
            if (mSpinnerJuZhuLeiXing.getSelectedItem().toString().equals("其他")
                    && TextUtils.isEmpty(mEditJuZhuQiTaLeiXing.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "居住其他类型" + "”" + warning).show();
                mEditJuZhuQiTaLeiXing.requestFocusFromTouch();
                return false;
            }
            if (mSpinnerXianZhuDi_JieDao.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "现住地街道" + "”" + warning).show();
                mSpinnerXianZhuDi_JieDao.setFocusableInTouchMode(true);
                mSpinnerXianZhuDi_JieDao.requestFocusFromTouch();
                mSpinnerXianZhuDi_JieDao.setFocusableInTouchMode(false);
                return false;
            }
        }

        if (TextUtils.isEmpty(mEditXianZhuDiXiangZhi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "现住地详址" + "”" + warning).show();
            mEditXianZhuDiXiangZhi.requestFocusFromTouch();
            return false;
        }

        // 就业社保信息
        if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "“" + "目前状况" + "”" + warning).show();
            mSpinnerMuQianZhuangKuang.setFocusableInTouchMode(true);
            mSpinnerMuQianZhuangKuang.requestFocusFromTouch();
            mSpinnerMuQianZhuangKuang.setFocusableInTouchMode(false);
            return false;
        } else if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("其他")
                && TextUtils.isEmpty(mEditMuQianZhuangKuangXiangXiXinXi.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "目前状况详细信息" + "”" + warning).show();
            mEditMuQianZhuangKuangXiangXiXinXi.requestFocusFromTouch();
            return false;
        } else if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("学生")
                && TextUtils.isEmpty(mEditXueXiaoMingCheng.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "“" + "学校名称" + "”" + warning).show();
            mEditXueXiaoMingCheng.requestFocusFromTouch();
            return false;
        } else if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("就业")) {
            if (TextUtils.isEmpty(mEditJiuYeDanWeiMingCheng.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "就业单位名称" + "”" + warning).show();
                mEditJiuYeDanWeiMingCheng.requestFocusFromTouch();
                return false;
            }

            if (mSpinnerJiuYeDanWeiSuoShuHangYe.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "就业单位所属行业" + "”" + warning).show();
                mSpinnerJiuYeDanWeiSuoShuHangYe.setFocusableInTouchMode(true);
                mSpinnerJiuYeDanWeiSuoShuHangYe.requestFocusFromTouch();
                mSpinnerJiuYeDanWeiSuoShuHangYe.setFocusableInTouchMode(false);
                return false;
            } else if (mSpinnerJiuYeDanWeiSuoShuHangYe.getSelectedItem().toString().equals("其他行业")
                    && TextUtils.isEmpty(mEditQiTaHangYe.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "其他就业单位所属行业" + "”" + warning).show();
                mEditQiTaHangYe.requestFocusFromTouch();
                return false;
            }

            if (mSpinnerZhuYaoCongShiGongZuo.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "主要从事工作" + "”" + warning).show();
                mSpinnerZhuYaoCongShiGongZuo.setFocusableInTouchMode(true);
                mSpinnerZhuYaoCongShiGongZuo.requestFocusFromTouch();
                mSpinnerZhuYaoCongShiGongZuo.setFocusableInTouchMode(false);
                return false;
            } else if (mSpinnerZhuYaoCongShiGongZuo.getSelectedItem().toString().contains("（描述）")
                    && TextUtils.isEmpty(mEditQiTaZhuYaoCongShiGongZuo.getText().toString())) {
                CommonUtils.createErrorAlertDialog(this, "“" + "其他主要从事工作" + "”" + warning).show();
                mEditQiTaZhuYaoCongShiGongZuo.requestFocusFromTouch();
                return false;
            }

            if (mSpinnerZhiYe.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "职业" + "”" + warning).show();
                mSpinnerZhiYe.setFocusableInTouchMode(true);
                mSpinnerZhiYe.requestFocusFromTouch();
                mSpinnerZhiYe.setFocusableInTouchMode(false);
                return false;
            }
            if (mSpinnerQianDingLaoDongHeTong.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "签订劳动合同" + "”" + warning).show();
                mSpinnerQianDingLaoDongHeTong.setFocusableInTouchMode(true);
                mSpinnerQianDingLaoDongHeTong.requestFocusFromTouch();
                mSpinnerQianDingLaoDongHeTong.setFocusableInTouchMode(false);
                return false;
            }
            if (mSpinnerJiuYeDanWeiSuoZaiDi.getSelectedItem().toString().equals("请选择")) {
                CommonUtils.createErrorAlertDialog(this, "“" + "就业单位所在地" + "”" + warning).show();
                mSpinnerJiuYeDanWeiSuoZaiDi.setFocusableInTouchMode(true);
                mSpinnerJiuYeDanWeiSuoZaiDi.requestFocusFromTouch();
                mSpinnerJiuYeDanWeiSuoZaiDi.setFocusableInTouchMode(false);
                return false;
            }

            if (!mCheckZaiJingBaoXian_Wu.isChecked() && !mCheckZaiJingBaoXian_YangLao.isChecked()
                    && !mCheckZaiJingBaoXian_ShiYe.isChecked() && !mCheckZaiJingBaoXian_YiLiao.isChecked()
                    && !mCheckZaiJingBaoXian_GongShang.isChecked() && !mCheckZaiJingBaoXian_ShengYu.isChecked()) {
                CommonUtils.createErrorAlertDialog(this, "“" + "在京参加社会保险" + "”" + warning).show();
                mCheckZaiJingBaoXian_Wu.requestFocusFromTouch();
                return false;
            }
            if (!mCheckBaoXian_Wu.isChecked() && !mCheckBaoXian_YangLao.isChecked()
                    && !mCheckBaoXian_ShiYe.isChecked() && !mCheckBaoXian_YiLiao.isChecked()
                    && !mCheckBaoXian_GongShang.isChecked() && !mCheckBaoXian_ShengYu.isChecked()) {
                CommonUtils.createErrorAlertDialog(this, "“" + "参加社会保险" + "”" + warning).show();
                mCheckBaoXian_Wu.requestFocusFromTouch();
                return false;
            }
        }

        return true;
    }

    private void initCardReader() {
        Reader = new SynjonesOTGReaderLib(this);
        Reader.initSerialPort();
        readerDriver = Reader.getReaderDriver();
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case ReadOnceDone:
                            idcard = (IDCard) msg.obj;
                            mBitmapPhoto = Reader.getPhotoBmp();
                            if (idcard != null) Beep();
                            displayIDCard(idcard);
                            mButtonCaiJiShenFenZheng.setEnabled(true);
                            break;

                        case LoopRead:
                            if (idcard == null && ReadErrTime < 3
                                    && ContinuousErrTime < 5) {
                                ReadErrTime++;
                                ContinuousErrTime++;
                            } else {
                                mBitmapPhoto = Reader.getPhotoBmp();
                                if (idcard != null)
                                    Beep();
                                displayIDCard(idcard);
                                ReadErrTime = 0;
                            }
                            IDCard.SW1 = 0;
                            IDCard.SW2 = 0;
                            IDCard.SW3 = 0;
                            break;

                        case CONNECT_ERR:
                            if (ReadThreadHandler != null)
                                ReadThreadHandler.StopReadThread();
                            mButtonCaiJiShenFenZheng.setText("采集身份证");
                            break;
                        case DISCONNECT:
                            Toast.makeText(FloatingPersonActivity.this, "读卡器断开连接", Toast.LENGTH_LONG).show();
                            break;

                    }
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
                super.handleMessage(msg);
            }
        };

        UsbMonitorService.setDisconnectHandler(mHandler, DISCONNECT);
        UsbMonitorService.start(this);
    }

    /**
     * 用读卡器获取个人信息
     */
    private void onReadIDCard() {
        Reader.initSerialPort();
        Reader.getIdcard(mHandler, ReadOnceDone);
        mButtonCaiJiShenFenZheng.setEnabled(false);

        mBitmapPhoto = null;
    }

    /**
     * 显示身份证的数据
     */
    private void displayIDCard(IDCard idcard) {
        Log.d(TAG, "display begin");

        if (idcard != null) {
            mEditXingMing.setText(idcard.getName());

            mEditShenFenZhengHaoMa.setText(idcard.getIDCardNo());

            CommonUtils.selectSpinnerItem(mSpinnerXingBie, idcard.getSex());

            mEditChuShengRiQi.setText(idcard.getBirthday().substring(0, 4) + "-"
                    + idcard.getBirthday().substring(4, 6) + "-"
                    + idcard.getBirthday().substring(6, 8));

            CommonUtils.selectSpinnerItem(mSpinnerMinZu, idcard.getNation());

            mEditHuJiXiangXiDiZhi.setText(idcard.getAddress());

            try {
                if (mBitmapPhoto != null) {
                    String fileName = getOutputMediaFileUri(MEDIA_TYPE_IMAGE).getPath();
                    mFileUri = Uri.parse(fileName);

                    CommonUtils.saveFile(fileName, CommonUtils.getByteFromBitmap(mBitmapPhoto));

                    if (mBitmapPhoto != null) {
                        if (!mBitmapPhoto.isRecycled())
                            mBitmapPhoto.recycle();
                        mBitmapPhoto = null;
                    }

                    File file = new File(fileName);
                    if (file.exists()) {
                        int size = mZhaoPianUriArray.size();

                        //if (!CommonUtils.compareFiles(mZhaoPianUriArray.get(size - 1).getPath(), mFileUri.getPath())) {
                            if (size < Config.MAX_PERSON_PHOTO_COUNT) {
                                mZhaoPianUriArray.add(Uri.parse(fileName));
                            } else {
                                if (mZhaoPianUriArray.size() >= Config.MAX_PERSON_PHOTO_COUNT) {
                                    Dialog dialog = new AlertDialog.Builder(this)
                                            .setTitle(R.string.app_name)
                                            .setMessage("照片数已经" + Config.MAX_PERSON_PHOTO_COUNT + "个超过了。您删除照片吗？")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    onPreviewPhoto();
                                                }
                                            })
                                            .setNegativeButton("取消", null)
                                            .create();
                                    dialog.show();
                                }
                            }
                        //}

                        refreshImageViews();
                    } else {
                        mImageZhaoPian.setImageResource(R.drawable.avatar);
                    }
                } else {
                    mImageZhaoPian.setImageResource(R.drawable.avatar);
                }

                // log.debug("decode wlt finish");
                System.gc();
            } catch (Exception ioe) {
                ioe.printStackTrace();
                // log.debug("photo display error:" + ioe.getMessage());
                // tvMessage.setText("状态：照片显示错" + ioe.getMessage());
                mImageZhaoPian.setImageResource(R.drawable.avatar);
            }
        }
    }

    /**
     * 从数据库选择户主
     */
    private void onSelectHouseHolder() {
        Intent intent = new Intent(this, FloatingPersonListActivity.class);
        intent.putExtra(FloatingPersonListActivity.REQUEST_CHOOSE_MODE, true);
        startActivityForResult(intent, CHOOSE_PERSON_ID_REQUEST_CODE);
    }

    /**
     * 从数据库选择房屋
     */
    private void onSelectRoom() {
        Intent intent = new Intent(this, RentRoomListActivity.class);
        intent.putExtra(RentRoomListActivity.REQUEST_CHOOSE_MODE, true);
        startActivityForResult(intent, CHOOSE_ROOM_ID_REQUEST_CODE);
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
                        contentValues.put(LaiJingRenYuanColumns.SFSC, true);
                        contentValues.put(LaiJingRenYuanColumns.SCSJ, CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                        int count = contentProvider.update(LaiJingRenYuanColumns.CONTENT_URI, contentValues, LaiJingRenYuanColumns.RYID + " LIKE '" + mSelectedID  + "'", null);

                        if (count == 1) {
                            CommonUtils.createErrorAlertDialog(FloatingPersonActivity.this, "删除成功！").show();

                            Dialog dialog = new AlertDialog.Builder(FloatingPersonActivity.this)
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

                            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "来京人员信息", mSelectedID, "删除来京人员信息", "民警");
                        } else {
                            CommonUtils.createErrorAlertDialog(FloatingPersonActivity.this, "删除失败！").show();
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

        // 登记人员信息
        contentValues.put(LaiJingRenYuanColumns.SZDXQ, mSpinnerSuoZaiDi_XiaQu.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.GLYXM, mSpinnerGuanLiYuanXingMing.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.GLYBH, mEditGuanLiYuanBianHao.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.TBRQ, mEditTianBiaoRiQi.getText().toString());

        // 来京人员基本信息
        contentValues.put(LaiJingRenYuanColumns.ZPURL, PhotoUtil.convertUrisToString(mZhaoPianUriArray));

        contentValues.put(LaiJingRenYuanColumns.XM, mEditXingMing.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.SFZWSFZH, mCheckZanWu.isChecked());
        if (mCheckZanWu.isChecked()) {
            contentValues.put(LaiJingRenYuanColumns.SFZHM, "");
        } else {
            contentValues.put(LaiJingRenYuanColumns.SFZHM, mEditShenFenZhengHaoMa.getText().toString());
            contentValues.put(LaiJingRenYuanColumns.MD5, CommonUtils.getMD5EncryptedString(mEditShenFenZhengHaoMa.getText().toString()));
        }

        contentValues.put(LaiJingRenYuanColumns.CSRQ, mEditChuShengRiQi.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.XB, mSpinnerXingBie.getSelectedItem().toString());

        contentValues.put(LaiJingRenYuanColumns.MZ, mSpinnerMinZu.getSelectedItem().toString());
        if (mSpinnerMinZu.getSelectedItem().toString().equals("其他"))
            contentValues.put(LaiJingRenYuanColumns.MZXXXX, mEditMinZuXiangXiXinXi.getText().toString());
        else
            contentValues.put(LaiJingRenYuanColumns.MZXXXX, "");

        contentValues.put(LaiJingRenYuanColumns.ZZMM, mSpinnerZhengZhiMianMao.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.SJYCD, mSpinnerShouJiaoYuChengDu.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.HJLB, mSpinnerHuJiLeiBie.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.HYZK, mSpinnerHunYinZhuangKuang.getSelectedItem().toString());
        if (!TextUtils.isEmpty(mEditLianXiDianHua.getText().toString())) {
            contentValues.put(LaiJingRenYuanColumns.LXDH, mEditLianXiDianHua.getText().toString());
        } else {
            contentValues.put(LaiJingRenYuanColumns.LXDH, (String) null);
        }

        contentValues.put(LaiJingRenYuanColumns.HJDZS, mSpinnerHuJiDiZhi_Sheng.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.HJDZSHI, mSpinnerHuJiDiZhi_Shi.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.HJDZQX, mSpinnerHuJiDiZhi_Qu.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.HJXXDZ, mEditHuJiXiangXiDiZhi.getText().toString());

        if (!mSpinnerChuShengDi.getSelectedItem().toString().equals("请选择")) {
            contentValues.put(LaiJingRenYuanColumns.CSD, mSpinnerChuShengDi.getSelectedItem().toString());
        } else {
            contentValues.put(LaiJingRenYuanColumns.CSD, (String) null);
        }
        contentValues.put(LaiJingRenYuanColumns.JZZJ, mSpinnerJuZhuZhengJian.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.HYZM, mSpinnerHunYuZhengMing.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.MYJZZ, mSpinnerMianYiJieZhongZheng.getSelectedItem().toString());

        // 家庭户流入
        contentValues.put(LaiJingRenYuanColumns.JTHLR, mSpinnerJiaTingHuLiuRu.getSelectedItem().toString());

        if (mSpinnerHuZhu.getSelectedItem().toString().equals("请选择"))
            contentValues.put(LaiJingRenYuanColumns.SFHZ, (String) null);
        else
            contentValues.put(LaiJingRenYuanColumns.SFHZ, mSpinnerHuZhu.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.BHWLRKZS, mEditBenHuWaiLaiRenKouShu.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.BHWLRK16, mEditBenHuWaiLaiRenKouShu_16.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.BHWLRKNAN, mEditBenHuWaiLaiRenKouShu_Nan.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.BHWLRKNV, mEditBenHuWaiLaiRenKouShu_Nv.getText().toString());

        if (mSpinnerYuHuZhuGuanXi.getSelectedItem().toString().equals("请选择"))
            contentValues.put(LaiJingRenYuanColumns.YHZGX, (String) null);
        else
            contentValues.put(LaiJingRenYuanColumns.YHZGX, mSpinnerYuHuZhuGuanXi.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.YHZXXGX, mEditYuHuZhuXiangXiGuanXi.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.HZDJBXH, mEditHuZhuDengJiBiaoXuHao.getText().toString());

        // 居住信息
        if (!TextUtils.isEmpty(mEditLiKaiYuanJiRiQi.getText().toString()))
            contentValues.put(LaiJingRenYuanColumns.LKYJRQ, mEditLiKaiYuanJiRiQi.getText().toString());
        else
            contentValues.put(LaiJingRenYuanColumns.LKYJRQ, (String) null);
        contentValues.put(LaiJingRenYuanColumns.LJRQ, mEditLaiJingRiQi.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.LXZDRQ, mEditLaiXianZhuDiRiQi.getText().toString());

        contentValues.put(LaiJingRenYuanColumns.LJYY, mSpinnerLaiJingYuanYin.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.LJQTXXYY, mEditLaiJingQiTaXiangXiYuanYin.getText().toString());

        contentValues.put(LaiJingRenYuanColumns.JZLX, mSpinnerJuZhuLeiXing.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.JZQTLX, mEditJuZhuQiTaLeiXing.getText().toString());

        if (mSpinnerJuZhuLeiXing.getSelectedItem().toString().equals("租赁房屋")) {
            contentValues.put(LaiJingRenYuanColumns.FWDJBXH, mEditFangWuDengJiBiaoXuHao.getText().toString());
            contentValues.put(LaiJingRenYuanColumns.FWID, mEditFangWuDengJiBiaoXuHao.getText().toString());
            contentValues.put(LaiJingRenYuanColumns.SSFWXXID, mEditFangWuDengJiBiaoXuHao.getText().toString());
        } else {
            contentValues.put(LaiJingRenYuanColumns.XZDFJ, mSpinnerXianZhuDi_FenJu.getSelectedItem().toString());
            contentValues.put(LaiJingRenYuanColumns.XZDPCS, mSpinnerXianZhuDi_PaiChuSuo.getSelectedItem().toString());
            contentValues.put(LaiJingRenYuanColumns.XZDXZSQ, mEditXianZhuDi_SheQu.getText().toString());
            contentValues.put(LaiJingRenYuanColumns.XZDXZJD, mSpinnerXianZhuDi_JieDao.getSelectedItem().toString());
        }
        contentValues.put(LaiJingRenYuanColumns.XZDXZZT, mEditXianZhuDiXiangZhi.getText().toString());

        contentValues.put(LaiJingRenYuanColumns.SSPCSMC, mEditSuoShuPaiChuSuoMingCheng.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.MJXM, mEditMinJingXingMing.getText().toString());

        // 就业社保信息
        contentValues.put(LaiJingRenYuanColumns.MQZK, mSpinnerMuQianZhuangKuang.getSelectedItem().toString());
        contentValues.put(LaiJingRenYuanColumns.MQZKXXXX, mEditMuQianZhuangKuangXiangXiXinXi.getText().toString());

        contentValues.put(LaiJingRenYuanColumns.XXMC, mEditXueXiaoMingCheng.getText().toString());
        if (!TextUtils.isEmpty(mEditXueXiaoSuoZaiDi.getText().toString()))
            contentValues.put(LaiJingRenYuanColumns.XXSZD, mEditXueXiaoSuoZaiDi.getText().toString());
        else
            contentValues.put(LaiJingRenYuanColumns.XXSZD, (String) null);

        contentValues.put(LaiJingRenYuanColumns.JYDWMC, mEditJiuYeDanWeiMingCheng.getText().toString());
        if (!TextUtils.isEmpty(mEditDanWeiDengJiBiaoXuHao.getText().toString()))
            contentValues.put(LaiJingRenYuanColumns.DWDJBXH, mEditDanWeiDengJiBiaoXuHao.getText().toString());
        else
            contentValues.put(LaiJingRenYuanColumns.DWDJBXH, (String) null);

        if (!TextUtils.isEmpty(mEditJiuYeDanWeiXiangXiDiZhi.getText().toString()))
            contentValues.put(LaiJingRenYuanColumns.JYDWXXDZ, mEditJiuYeDanWeiXiangXiDiZhi.getText().toString());
        else
            contentValues.put(LaiJingRenYuanColumns.JYDWXXDZ, (String) null);

        if (mSpinnerJiuYeDanWeiSuoShuHangYe.getSelectedItem().toString().equals("请选择"))
            contentValues.put(LaiJingRenYuanColumns.JYDWSSHY, (String) null);
        else
            contentValues.put(LaiJingRenYuanColumns.JYDWSSHY, mSpinnerJiuYeDanWeiSuoShuHangYe.getSelectedItem().toString());

        if (mSpinnerZhuYaoCongShiGongZuo.getSelectedItem().toString().equals("请选择"))
            contentValues.put(LaiJingRenYuanColumns.ZYCSGZ, (String) null);
        else
            contentValues.put(LaiJingRenYuanColumns.ZYCSGZ, mSpinnerZhuYaoCongShiGongZuo.getSelectedItem().toString());

        if (mSpinnerZhiYe.getSelectedItem().toString().equals("请选择"))
            contentValues.put(LaiJingRenYuanColumns.ZY, (String) null);
        else
            contentValues.put(LaiJingRenYuanColumns.ZY, mSpinnerZhiYe.getSelectedItem().toString());

        if (mSpinnerQianDingLaoDongHeTong.getSelectedItem().toString().equals("请选择"))
            contentValues.put(LaiJingRenYuanColumns.QDLDHT, (String) null);
        else
            contentValues.put(LaiJingRenYuanColumns.QDLDHT, mSpinnerQianDingLaoDongHeTong.getSelectedItem().toString());

        if (mSpinnerJiuYeDanWeiSuoZaiDi.getSelectedItem().toString().equals("请选择"))
            contentValues.put(LaiJingRenYuanColumns.JYDWSZD, (String) null);
        else
            contentValues.put(LaiJingRenYuanColumns.JYDWSZD, mSpinnerJiuYeDanWeiSuoZaiDi.getSelectedItem().toString());

        contentValues.put(LaiJingRenYuanColumns.QTHY, mEditQiTaHangYe.getText().toString());
        contentValues.put(LaiJingRenYuanColumns.QTZYCSGZMS, mEditQiTaZhuYaoCongShiGongZuo.getText().toString());

        String str = "";
        if (mCheckZaiJingBaoXian_Wu.isChecked()) {
            str = mCheckZaiJingBaoXian_Wu.getText().toString();
        } else {
            if (mCheckZaiJingBaoXian_YangLao.isChecked())
                str = mCheckZaiJingBaoXian_YangLao.getText().toString() + " ";

            if (mCheckZaiJingBaoXian_ShiYe.isChecked())
                str += mCheckZaiJingBaoXian_ShiYe.getText().toString() + " ";

            if (mCheckZaiJingBaoXian_YiLiao.isChecked())
                str += mCheckZaiJingBaoXian_YiLiao.getText().toString() + " ";

            if (mCheckZaiJingBaoXian_GongShang.isChecked())
                str += mCheckZaiJingBaoXian_GongShang.getText().toString() + " ";

            if (mCheckZaiJingBaoXian_ShengYu.isChecked())
                str += mCheckZaiJingBaoXian_ShengYu.getText().toString();
        }
        contentValues.put(LaiJingRenYuanColumns.ZJCJSHBX, str);

        str = "";
        if (mCheckBaoXian_Wu.isChecked()) {
            str = mCheckBaoXian_Wu.getText().toString();
        } else {
            if (mCheckBaoXian_YangLao.isChecked())
                str = mCheckBaoXian_YangLao.getText().toString() + " ";

            if (mCheckBaoXian_ShiYe.isChecked())
                str += mCheckBaoXian_ShiYe.getText().toString() + " ";

            if (mCheckBaoXian_YiLiao.isChecked())
                str += mCheckBaoXian_YiLiao.getText().toString() + " ";

            if (mCheckBaoXian_GongShang.isChecked())
                str += mCheckBaoXian_GongShang.getText().toString() + " ";

            if (mCheckBaoXian_ShengYu.isChecked())
                str += mCheckBaoXian_ShengYu.getText().toString();
        }
        contentValues.put(LaiJingRenYuanColumns.CJSHBX, str);

        // 备注
        if (!TextUtils.isEmpty(mEditBeiZhu.getText().toString()))
            contentValues.put(LaiJingRenYuanColumns.BZ, mEditBeiZhu.getText().toString());
        else
            contentValues.put(LaiJingRenYuanColumns.BZ, (String) null);

        contentValues.put(LaiJingRenYuanColumns.SFJZ, false);
        contentValues.put(LaiJingRenYuanColumns.SFSC, false);
        contentValues.put(LaiJingRenYuanColumns.LRSB, true);
        contentValues.put(LaiJingRenYuanColumns.SFSCZFWQ, false);
        contentValues.put(LaiJingRenYuanColumns.SFSCZHL, false);
        contentValues.put(LaiJingRenYuanColumns.SFSCZLGB, false);

        MyContentProvider contentProvider = new MyContentProvider();

        String message = "";
        String id;

        if (mIsAdd) {
            Date currentDate = new Date();

            contentValues.put(LaiJingRenYuanColumns.LRMJJH, mOwnerData.MJJH);
            contentValues.put(LaiJingRenYuanColumns.LRMJID, mOwnerData.MJID);
            contentValues.put(LaiJingRenYuanColumns.LRMJXM, mOwnerData.MJXM);
            contentValues.put(LaiJingRenYuanColumns.LRDWID, mOwnerData.SSDWID);
            contentValues.put(LaiJingRenYuanColumns.LRDWMC, mOwnerData.SSDWMC);
            contentValues.put(LaiJingRenYuanColumns.LRSJ, CommonUtils.getFormattedDateString(currentDate, "yyyy-MM-dd HH:mm:ss"));

            mSelectedID = id = mOwnerData.MJID + CommonUtils.getFormattedDateString(currentDate, "yyyyMMddHHmmss");
            contentValues.put(LaiJingRenYuanColumns.RYID, id);
            contentValues.put(LaiJingRenYuanColumns.DJBXH, id);

            contentProvider.insert(LaiJingRenYuanColumns.CONTENT_URI, contentValues);

            message = "保存成功！";
            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "来京人员信息", String.valueOf(id), "添加来京人员信息", "民警");
        }

        if (mIsUpdate) {
            contentValues.put(LaiJingRenYuanColumns.XGMJJH, mOwnerData.MJJH);
            contentValues.put(LaiJingRenYuanColumns.XGMJID, mOwnerData.MJID);
            contentValues.put(LaiJingRenYuanColumns.XGMJXM, mOwnerData.MJXM);
            contentValues.put(LaiJingRenYuanColumns.XGDWID, mOwnerData.SSDWID);
            contentValues.put(LaiJingRenYuanColumns.XGDWMC, mOwnerData.SSDWMC);
            contentValues.put(LaiJingRenYuanColumns.XGSJ, CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));

            contentProvider.update(LaiJingRenYuanColumns.CONTENT_URI, contentValues, LaiJingRenYuanColumns.RYID + " LIKE '" + mSelectedID + "'", null);

            message = "修改成功！";
            new MyContentProvider().addSystemLog(mOwnerData, "信息采集", "来京人员信息", mSelectedID, "修改来京人员信息", "民警");
        }

        String cardID = mEditShenFenZhengHaoMa.getText().toString();
        boolean isExist = compareWithBlackList(cardID);

        if (!isExist) {
            Dialog dialog = new AlertDialog.Builder(FloatingPersonActivity.this)
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
        } else {
            mLayoutLuRuAnNiu.setVisibility(View.GONE);
            mLayoutXiuGaiAnNiu.setVisibility(View.VISIBLE);
            setEnableModify(false);
            mIsUpdate = false;
        }

        if (CommonUtils.checkNetworkEnable(this)) {
            Cursor cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI, null,
                    LaiJingRenYuanColumns.RYID + " LIKE '" + mSelectedID + "'", null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    UploadUtil.uploadNewData(this, LaiJingRenYuanColumns.CONTENT_TYPE, cursor, false);
                }

                cursor.close();
            }
        }
    }

    /**
     * Load data from database with selected FWID
     */
    private void loadData() {
        MyContentProvider contentProvider = new MyContentProvider();
        Cursor cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI, null,
                LaiJingRenYuanColumns.RYID + " LIKE '" + mSelectedID + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                // 登记人员信息
                CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDi_XiaQu, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.SZDXQ)));
                mStrGuanLiYuanXingMing = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.GLYXM));
                mEditGuanLiYuanBianHao.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.GLYBH)));
                mEditDengJiBiaoXuHao.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.RYID)));
                mEditTianBiaoRiQi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.TBRQ)));

                // 来京人员基本信息
                String path = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.ZPURL));
                if (!TextUtils.isEmpty(path)) {
                    mZhaoPianUriArray = PhotoUtil.getUrisFromString(this, path);
                    refreshImageViews();
                }

                mButtonCaiJiZhaoPian.setVisibility(View.GONE);
                mButtonJianSuoGuiJi.setVisibility(View.VISIBLE);

                mEditXingMing.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XM)));
                mCheckZanWu.setChecked(cursor.getInt(cursor.getColumnIndex(LaiJingRenYuanColumns.SFZWSFZH)) == 1);

                if (!mCheckZanWu.isChecked())
                    mEditShenFenZhengHaoMa.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.SFZHM)));

                mEditChuShengRiQi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.CSRQ)));
                CommonUtils.selectSpinnerItem(mSpinnerXingBie, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XB)));

                CommonUtils.selectSpinnerItem(mSpinnerMinZu, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.MZ)));
                if (mSpinnerMinZu.getSelectedItem().toString().equals("其他")) {
                    mEditMinZuXiangXiXinXi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.MZXXXX)));
                }

                CommonUtils.selectSpinnerItem(mSpinnerZhengZhiMianMao, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.ZZMM)));
                CommonUtils.selectSpinnerItem(mSpinnerShouJiaoYuChengDu, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.SJYCD)));
                CommonUtils.selectSpinnerItem(mSpinnerHuJiLeiBie, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.HJLB)));
                CommonUtils.selectSpinnerItem(mSpinnerHunYinZhuangKuang, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.HYZK)));
                mEditLianXiDianHua.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LXDH)));

                CommonUtils.selectSpinnerItem(mSpinnerHuJiDiZhi_Sheng, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.HJDZS)));
                mStrHuJiDiZhi_Shi = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.HJDZSHI));
                mStrHuJiDiZhi_Qu = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.HJDZQX));
                mEditHuJiXiangXiDiZhi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.HJXXDZ)));

                CommonUtils.selectSpinnerItem(mSpinnerChuShengDi, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.CSD)));
                CommonUtils.selectSpinnerItem(mSpinnerJuZhuZhengJian, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.JZZJ)));
                CommonUtils.selectSpinnerItem(mSpinnerHunYuZhengMing, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.HYZM)));
                CommonUtils.selectSpinnerItem(mSpinnerMianYiJieZhongZheng, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.MYJZZ)));

                // 家庭户流入
                CommonUtils.selectSpinnerItem(mSpinnerJiaTingHuLiuRu, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.JTHLR)));
                if (mSpinnerJiaTingHuLiuRu.getSelectedItem().toString().equals("是")) {
                    CommonUtils.selectSpinnerItem(mSpinnerHuZhu, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.SFHZ)));

                    if (mSpinnerHuZhu.getSelectedItem().toString().equals("是")) {
                        mEditBenHuWaiLaiRenKouShu.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.BHWLRKZS)));
                        mEditBenHuWaiLaiRenKouShu_16.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.BHWLRK16)));
                        mEditBenHuWaiLaiRenKouShu_Nan.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.BHWLRKNAN)));
                        mEditBenHuWaiLaiRenKouShu_Nv.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.BHWLRKNV)));
                    } else {
                        CommonUtils.selectSpinnerItem(mSpinnerYuHuZhuGuanXi, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.YHZGX)));
                        if (mSpinnerYuHuZhuGuanXi.getSelectedItem().toString().equals("其他"))
                            mEditYuHuZhuXiangXiGuanXi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.YHZXXGX)));

                        mEditHuZhuDengJiBiaoXuHao.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.HZDJBXH)));
                    }
                }

                // 居住信息
                mEditLiKaiYuanJiRiQi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LKYJRQ)));
                mEditLaiJingRiQi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LJRQ)));
                mEditLaiXianZhuDiRiQi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LXZDRQ)));

                CommonUtils.selectSpinnerItem(mSpinnerLaiJingYuanYin, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LJYY)));
                if (mSpinnerLaiJingYuanYin.getSelectedItem().toString().equals("其他"))
                    mEditLaiJingQiTaXiangXiYuanYin.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LJQTXXYY)));

                CommonUtils.selectSpinnerItem(mSpinnerJuZhuLeiXing, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.JZLX)));
                if (mSpinnerJuZhuLeiXing.getSelectedItem().toString().equals("租赁房屋")) {
                    mEditFangWuDengJiBiaoXuHao.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.FWDJBXH)));
                } else {
                    if (mSpinnerJuZhuLeiXing.getSelectedItem().toString().equals("其他")) {
                        mEditJuZhuQiTaLeiXing.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.JZQTLX)));
                    }

                    CommonUtils.selectSpinnerItem(mSpinnerXianZhuDi_FenJu, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XZDFJ)));
                    CommonUtils.selectSpinnerItem(mSpinnerXianZhuDi_PaiChuSuo, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XZDPCS)));
                    mEditXianZhuDi_SheQu.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XZDXZSQ)));
                    mStrXianZhuDi_JieDao = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XZDXZJD));
                }
                mEditXianZhuDiXiangZhi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XZDXZZT)));
                mEditSuoShuPaiChuSuoMingCheng.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.SSPCSMC)));
                mEditMinJingXingMing.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.MJXM)));

                // 就业社保信息
                CommonUtils.selectSpinnerItem(mSpinnerMuQianZhuangKuang, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.MQZK)));
                if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("其他")) {
                    mEditMuQianZhuangKuangXiangXiXinXi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.MQZKXXXX)));
                } else if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("学生")) {
                    mEditXueXiaoMingCheng.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XXMC)));
                    mEditXueXiaoSuoZaiDi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XXSZD)));
                } else if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("就业")) {
                    mEditJiuYeDanWeiMingCheng.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.JYDWMC)));
                    mEditDanWeiDengJiBiaoXuHao.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.DWDJBXH)));
                    mEditJiuYeDanWeiXiangXiDiZhi.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.JYDWXXDZ)));

                    CommonUtils.selectSpinnerItem(mSpinnerJiuYeDanWeiSuoShuHangYe, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.JYDWSSHY)));
                    //CommonUtils.selectSpinnerItem(mSpinnerZhuYaoCongShiGongZuo, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.ZYCSGZ)));
                    mStrZhuYaoCongShiGongZuo = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.ZYCSGZ));
                    CommonUtils.selectSpinnerItem(mSpinnerZhiYe, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.ZY)));
                    CommonUtils.selectSpinnerItem(mSpinnerQianDingLaoDongHeTong, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.QDLDHT)));
                    CommonUtils.selectSpinnerItem(mSpinnerJiuYeDanWeiSuoZaiDi, cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.JYDWSZD)));

                    if (mSpinnerJiuYeDanWeiSuoShuHangYe.getSelectedItem().toString().equals("其他行业"))
                        mEditQiTaHangYe.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.QTHY)));
                    if (mStrZhuYaoCongShiGongZuo.contains("（描述）"))
                        mEditQiTaZhuYaoCongShiGongZuo.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.QTZYCSGZMS)));
                }

                String string = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.ZJCJSHBX));
                if (!TextUtils.isEmpty(string)) {
                    if ("无".equals(string.trim())) {
                        mCheckZaiJingBaoXian_Wu.setChecked(true);
                    } else {
                        String[] checks = string.split(" ");

                        if (checks.length > 0) {
                            for (String check : checks) {
                                if ("养老".trim().equals(check)) {
                                    mCheckZaiJingBaoXian_YangLao.setChecked(true);
                                } else if ("失业".trim().equals(check)) {
                                    mCheckZaiJingBaoXian_ShiYe.setChecked(true);
                                } else if ("医疗".trim().equals(check)) {
                                    mCheckZaiJingBaoXian_YiLiao.setChecked(true);
                                } else if ("工伤".trim().equals(check)) {
                                    mCheckZaiJingBaoXian_GongShang.setChecked(true);
                                } else if ("生育".trim().equals(check)) {
                                    mCheckZaiJingBaoXian_ShengYu.setChecked(true);
                                }
                            }
                        }
                    }
                }

                string = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.CJSHBX));
                if (!TextUtils.isEmpty(string)) {
                    if ("无".equals(string.trim())) {
                        mCheckBaoXian_Wu.setChecked(true);
                    } else {
                        String[] checks = string.split(" ");

                        if (checks.length > 0) {
                            for (String check : checks) {
                                if ("养老".trim().equals(check)) {
                                    mCheckBaoXian_YangLao.setChecked(true);
                                } else if ("失业".trim().equals(check)) {
                                    mCheckBaoXian_ShiYe.setChecked(true);
                                } else if ("医疗".trim().equals(check)) {
                                    mCheckBaoXian_YiLiao.setChecked(true);
                                } else if ("工伤".trim().equals(check)) {
                                    mCheckBaoXian_GongShang.setChecked(true);
                                } else if ("生育".trim().equals(check)) {
                                    mCheckBaoXian_ShengYu.setChecked(true);
                                }
                            }
                        }
                    }
                }

                // 备注
                mEditBeiZhu.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.BZ)));
            }

            cursor.close();
        }

        compareWithBlackList(mEditShenFenZhengHaoMa.getText().toString());
    }

    /**
     * 设定房屋信息
     */
    private void setRenterInformation() {
        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI, null,
                FangWuColumns.FWID + "='" + mSelectedRoomID + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDi_XiaQu, cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXQ)));
                mSpinnerSuoZaiDi_XiaQu.setEnabled(false);

                mStrGuanLiYuanXingMing = cursor.getString(cursor.getColumnIndex(FangWuColumns.GLYXM));
                mSpinnerGuanLiYuanXingMing.setEnabled(false);

                mEditGuanLiYuanBianHao.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.GLYBH)));
                mEditGuanLiYuanBianHao.setEnabled(false);
                mEditXianZhuDiXiangZhi.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXZZT)));
                mEditXianZhuDiXiangZhi.setEnabled(false);
            }

            cursor.close();
        }
    }

    /**
     * 查询历史轨迹
     */
    private void onSearchRegisterHistory() {
        if (!CommonUtils.isNetworkAvailable(this)) {
            CommonUtils.createErrorAlertDialog(this, "网路异常").show();
            return;
        }

        if (TextUtils.isEmpty(mEditShenFenZhengHaoMa.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "请输入身份证号码！").show();
            return;
        }

        Intent intent = new Intent(this, RegisterHistoryActivity.class);
        intent.putExtra(RegisterHistoryActivity.ID_CARD_NUMBER, mEditShenFenZhengHaoMa.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

        new MyContentProvider().addSystemLog(mOwnerData, "无线接口", "来京人员信息",
                mIsAdd ? "" : mSelectedID, "无线轨迹查询", "民警");
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
        if (count == 0) {
            mImageZhaoPian.setImageResource(R.drawable.avatar);
        } else {
            mImageZhaoPian.setImageBitmap(CommonUtils.getBitmapFromUri(mZhaoPianUriArray.get(count - 1), 1));
        }
    }

    private void onPreviewPhoto() {
        Intent intent = new Intent(this, PreviewPhotoActivity.class);
        intent.putParcelableArrayListExtra(PreviewPhotoActivity.PHOTO_ID_ARRAY, mZhaoPianUriArray);
        intent.putExtra(PreviewPhotoActivity.ENABLE_DELETE, mIsAdd || mIsUpdate);
        startActivityForResult(intent, PREVIEW_PHOTO_REQUEST_CODE);
    }

    /**
     * 自动输入出生日期和性别字段从身份证号
     */
    private void setPersonInformationFromID(String id) {
        String dqsj = CommonUtils.convertDateFormat("yyyy-MM-dd", "yyyyMMdd", mEditTianBiaoRiQi.getText().toString());

        //设置相应的出生日期
        mEditChuShengRiQi.setText(id.substring(6, 10) + "-" + id.substring(10, 12) + "-" + id.substring(12, 14));

        //设置性别
        int idx = Integer.parseInt(id.substring(16, 17)) % 2;
        mSpinnerXingBie.setSelection(idx % 2 == 1 ? 1 : 2);// 男女
        //判断是否进行免疫接种
        int age = Integer.parseInt(dqsj) - Integer.parseInt(id.substring(6, 14));
        if (age >= 180000) {
            CommonUtils.selectSpinnerItem(mSpinnerMianYiJieZhongZheng, "无");
        }
        //判断是否有婚育证明
        if (age <= 180000) {
            CommonUtils.selectSpinnerItem(mSpinnerHunYinZhuangKuang, "未婚");
            CommonUtils.selectSpinnerItem(mSpinnerHunYuZhengMing, "无");
        }
    }

    /**
     * 比对身份证ID与黑名单
     */
    private boolean compareWithBlackList(String id) {
        String md5 = CommonUtils.getMD5EncryptedString(id);

        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(T_HMD_MD5.CONTENT_URI, null,
                T_HMD_MD5.MD5 + " LIKE '" + md5 + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                int type = cursor.getInt(cursor.getColumnIndex(T_HMD_MD5.TYPE));

                if (type != 0) {
                    if (mIsAdd || mIsUpdate) {
                        onAlertBlackList();
                        onSaveAlertInfo();
                    } else {
                        mImageAlertLED.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            }

            cursor.close();
        }

        mImageAlertLED.setVisibility(View.GONE);
        return false;
    }

    /**
     * 预警黑名单的人员
     */
    private void onAlertBlackList() {
        mImageAlertLED.setVisibility(View.VISIBLE);

        // Play sound
        /*AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_PLAY_SOUND);

        PlayerUtils.playSound(this, "police_siren.mp3");*/

        // Get vibrate
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        // This code snippet will cause the phone to vibrate "SOS" in Morse Code
        // In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
        // There are pauses to separate dots/dashes, letters, and words
        // The following numbers represent millisecond lengths
        int dot = 200;          // Length of a Morse Code "dot" in milliseconds
        int dash = 500;         // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200;    // Length of Gap Between dots/dashes
        int medium_gap = 500;   // Length of Gap Between Letters
        int long_gap = 1000;    // Length of Gap Between Words

        long[] pattern = {
                0,  // Start immediately
                dot, short_gap, dot, short_gap, dot,    // s
                medium_gap,
                dash, short_gap, dash, short_gap, dash, // o
                medium_gap,
                dot, short_gap, dot, short_gap, dot,    // s
                long_gap
        };

        // Only perform this pattern one time (-1 means "do not repeat")
        vibrator.vibrate(pattern, -1);
    }

    /**
     * 保存预警信息
     */
    private void onSaveAlertInfo() {
        ContentValues contentValues = new ContentValues();

        String id = mEditShenFenZhengHaoMa.getText().toString();
        Date currentDate = new Date();

        String yujingID = mOwnerData.MJID + CommonUtils.getFormattedDateString(currentDate, "yyyyMMddHHmmss");

        contentValues.put(YuJingColumns.YJXXID, yujingID);
        if (mIsUpdate)
            contentValues.put(YuJingColumns.LJRYRYID, mSelectedID);
        contentValues.put(YuJingColumns.BDSFZH, id);
        contentValues.put(YuJingColumns.BDSJ, CommonUtils.getFormattedDateString(currentDate, "yyyy-MM-dd HH:mm:ss"));
        contentValues.put(YuJingColumns.SFCK, false);

        MyContentProvider contentProvider = new MyContentProvider();

        long count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(YuJingColumns.CONTENT_URI),
                YuJingColumns.BDSFZH + "='" + id + "'");

        if (count > 0) {
            contentProvider.update(YuJingColumns.CONTENT_URI, contentValues,
                    YuJingColumns.BDSFZH + "='" + id + "'", null);
        } else {
            contentProvider.insert(YuJingColumns.CONTENT_URI, contentValues);
        }

        new MyContentProvider().addSystemLog(mOwnerData, "比对预警", "预警信息", yujingID, "比对预警信息", "民警");
    }

    /**
     * 进入“预警详细”页面
     */
    @Override
    protected void onAlertDetail() {
        String id = mEditShenFenZhengHaoMa.getText().toString();

        MyContentProvider contentProvider = new MyContentProvider();
        Cursor cursor = contentProvider.query(YuJingColumns.CONTENT_URI, null,
                YuJingColumns.BDSFZH + "='" + id + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                String yujingID = cursor.getString(cursor.getColumnIndex(YuJingColumns.YJXXID));

                Intent intent = new Intent(this, AlertDetailActivity.class);
                intent.putExtra(AlertDetailActivity.SELECTED_PERSON_ID, yujingID);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }

            cursor.close();
        }
    }

    /**
     *
     */

    public void cancelReadThread() {
        if (ReadThreadHandler != null)
            ReadThreadHandler.StopReadThread();
        ReadThreadHandler = null;
    }

    public void Beep() {
        try {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ReadThread extends Thread {
        private int ErrCount = 0;
        private boolean pauseThread = false;

        public void run() {
            Looper.prepare();
            // nop
            try {
                readerDriver.EnterWorkModeNoReply();
                Thread.sleep(800);
            } catch (Exception e) {
                e.printStackTrace();
                SEND_MSG(CONNECT_ERR, null);
            }
            while (reading) {
                try {
                    if (pauseThread) {
                        Thread.sleep(100);
                        continue;
                    }
                    idcard = Reader.getIDcardBlocking();
                    SEND_MSG(LoopRead, null);
                    if (idcard != null) {
                        Thread.sleep(800);
                        ErrCount = 0;
                    } else {
                        Thread.sleep(200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    HandleErr();
                }
            }
        }

        public void HandleErr() {
            if (ErrCount++ > 10) {
                ErrCount = 0;
                pauseThread = true;
                SEND_MSG(CONNECT_ERR, null);
            }
        }

        public void StopReadThread() {
            reading = false;
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void SEND_MSG(int toMsg, Object obj) {
            Message m = new Message();
            m.what = toMsg;
            m.obj = obj;
            mHandler.sendMessage(m);
        }
    }

}
