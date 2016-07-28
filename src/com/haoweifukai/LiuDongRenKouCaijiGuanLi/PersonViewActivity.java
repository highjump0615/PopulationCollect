/**
 * @author LuYongXing
 * @date 2014.09.10
 * @filename PersonViewActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class PersonViewActivity extends PermanentActivity {

    private static final String TAG = PersonViewActivity.class.getSimpleName();
    public static final String JSON_STRING = "json_string";

    //
    private String mStrSuoZaiDi_XiaQu = "";
    private String mStrXianZhuDi_JieDao = "";
    private String mStrZhuYaoCongShiGongZuo = "";

    private LinkedHashMap<String, String> mGuanLiYuanMap = new LinkedHashMap<String, String>();

    private String mStrHuJiDiZhi_Shi = "";
    private String mStrHuJiDiZhi_Qu = "";

    private String mJsonString = null;
    private HashMap<String, String> mFieldMap = new HashMap<String, String>();

    // 定义控制
    // 登记信息
    private Spinner mSpinnerSuoZaiDi_XiaQu;
    private Spinner mSpinnerGuanLiYuanXingMing;
    private EditText mEditGuanLiYuanBianHao;
    private EditText mEditDengJiBiaoXuHao;
    private EditText mEditTianBiaoRiQi;

    // 来京人员基本信息
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
    private TextView mTextLaiXianZhuDiRiQi;
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
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(JSON_STRING)) {
            mJsonString = intent.getStringExtra(JSON_STRING);
        }

        super.onCreate(savedInstanceState);
        loadData();

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
                        PersonViewActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerGuanLiYuanXingMing.setAdapter(spinnerArrayAdapter);

                if (!TextUtils.isEmpty(mStrSuoZaiDi_XiaQu)) {
                    CommonUtils.selectSpinnerItem(mSpinnerGuanLiYuanXingMing, mStrSuoZaiDi_XiaQu);
                    mSpinnerGuanLiYuanXingMing.setEnabled(false);
                    mStrSuoZaiDi_XiaQu = "";
                }

                // 所在地社区
                spinnerArray = contentProvider.getStreetNames(districtName);
                spinnerArray.add(0, "请选择");
                spinnerArrayAdapter = new ArrayAdapter<String>(PersonViewActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
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
        mEditTianBiaoRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(PersonViewActivity.this, mEditTianBiaoRiQi));

        /* 来京人员基本信息 */
        mButtonJianSuoGuiJi = (Button) findViewById(R.id.button_jiansuoguiji);
        mButtonCaiJiShenFenZheng = (Button) findViewById(R.id.button_caijishenfenzheng);
        mImageZhaoPian = (ImageView) findViewById(R.id.image_zhaopian);
        mImageZhaoPian.setImageResource(R.drawable.avatar);

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

        mEditChuShengRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(PersonViewActivity.this, mEditChuShengRiQi));

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
                        PersonViewActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
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

        mSpinnerHuJiDiZhi_Shi.setEnabled(false);
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
                        PersonViewActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
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
        mButtonXuanZeHuZhu.setVisibility(View.GONE);

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
        mTextLaiXianZhuDiRiQi = (TextView) findViewById(R.id.text_laixianzhudiriqi);
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
        mButtonXuanZeFangWu.setVisibility(View.GONE);

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

        mEditLiKaiYuanJiRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(PersonViewActivity.this, mEditLiKaiYuanJiRiQi));
        mEditLaiJingRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(PersonViewActivity.this, mEditLaiJingRiQi));
        mTextLaiXianZhuDiRiQi.setText("数据来源");
        //mEditLaiXianZhuDiRiQi.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(PersonViewActivity.this, mEditLaiXianZhuDiRiQi));

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
        /*mSpinnerXianZhuDi_FenJu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEditSuoShuPaiChuSuoMingCheng.setText(mSpinnerXianZhuDi_PaiChuSuo.getSelectedItem().toString());

                mEditXianZhuDiXiangZhi.setText(mSpinnerXianZhuDi_FenJu.getSelectedItem().toString()
                        + mSpinnerXianZhuDi_PaiChuSuo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/

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

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(PersonViewActivity.this,
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

        spinnerArray = contentProvider.getDistrictNames();
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
                } else {
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                }
            }
        });

        mCheckBaoXian_YangLao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckZaiJingBaoXian_Wu.setChecked(false);
            }
        });

        mCheckBaoXian_ShiYe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckZaiJingBaoXian_Wu.setChecked(false);
            }
        });

        mCheckBaoXian_YiLiao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckZaiJingBaoXian_Wu.setChecked(false);
            }
        });

        mCheckBaoXian_GongShang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckZaiJingBaoXian_Wu.setChecked(false);
            }
        });

        mCheckBaoXian_ShengYu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mCheckZaiJingBaoXian_Wu.setChecked(false);
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
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_YangLao.setChecked(true);
                }
            }
        });

        mCheckZaiJingBaoXian_ShiYe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_ShiYe.setChecked(true);
                }
            }
        });

        mCheckZaiJingBaoXian_YiLiao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_YiLiao.setChecked(true);
                }
            }
        });

        mCheckZaiJingBaoXian_GongShang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckZaiJingBaoXian_Wu.setChecked(false);
                    mCheckBaoXian_GongShang.setChecked(true);
                }
            }
        });

        mCheckZaiJingBaoXian_ShengYu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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

        mButtonJianSuoGuiJi.setVisibility(View.GONE);
        mButtonCaiJiShenFenZheng.setVisibility(View.GONE);
        findViewById(R.id.button_caijizhoapian).setVisibility(View.GONE);
        mLayoutLuRuAnNiu.setVisibility(View.GONE);
        mLayoutXiuGaiAnNiu.setVisibility(View.GONE);

        CommonUtils.enableViewGroup(mLayoutContainer, false);
    }

    /**
     * Load data from database with selected FWID
     */
    private void loadData() {
        if (TextUtils.isEmpty(mJsonString)) return;
        Log.d(TAG, "json data = " + mJsonString);

        mFieldMap.clear();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(mJsonString);
        } catch (JSONException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        if (jsonObject == null) return;

        Set<String> columnSet = MyContentProvider.ljryxxLabelMap.keySet();
        for (String column : columnSet) {
            String value = null;

            try {
                value = jsonObject.getString(column);
            } catch (JSONException e) {
                try {
                    value = "" + jsonObject.getInt(column);
                } catch (JSONException e1) {
                    if (Config.DEBUG) e.printStackTrace();
                }
            }

            if (!TextUtils.isEmpty(value)) {
                mFieldMap.put(column, value);
            }
        }


        // 登记人员信息
        CommonUtils.selectSpinnerItem(mSpinnerSuoZaiDi_XiaQu, mFieldMap.get(LaiJingRenYuanColumns.SZDXQ));
        mStrSuoZaiDi_XiaQu = mFieldMap.get(LaiJingRenYuanColumns.GLYXM);
        mEditGuanLiYuanBianHao.setText(mFieldMap.get(LaiJingRenYuanColumns.GLYBH));
        mEditDengJiBiaoXuHao.setText(mFieldMap.get(LaiJingRenYuanColumns.RYID));
        mEditTianBiaoRiQi.setText(mFieldMap.get(LaiJingRenYuanColumns.TBRQ));

        // 来京人员基本信息
        String path = mFieldMap.get(LaiJingRenYuanColumns.ZPURL);
        if (!TextUtils.isEmpty(path)) {
            mImageZhaoPian.setImageURI(Uri.parse(path));
        } else {
            mImageZhaoPian.setImageResource(R.drawable.avatar);
        }

        mEditXingMing.setText(mFieldMap.get(LaiJingRenYuanColumns.XM));
        mCheckZanWu.setChecked("1".equals(mFieldMap.get(LaiJingRenYuanColumns.SFZWSFZH)));

        if (!mCheckZanWu.isChecked())
            mEditShenFenZhengHaoMa.setText(mFieldMap.get(LaiJingRenYuanColumns.SFZHM));

        mEditChuShengRiQi.setText(mFieldMap.get(LaiJingRenYuanColumns.CSRQ));
        CommonUtils.selectSpinnerItem(mSpinnerXingBie, mFieldMap.get(LaiJingRenYuanColumns.XB));

        CommonUtils.selectSpinnerItem(mSpinnerMinZu, mFieldMap.get(LaiJingRenYuanColumns.MZ));
        if (mSpinnerMinZu.getSelectedItem().toString().equals("其他")) {
            mEditMinZuXiangXiXinXi.setText(mFieldMap.get(LaiJingRenYuanColumns.MZXXXX));
        }

        CommonUtils.selectSpinnerItem(mSpinnerZhengZhiMianMao, mFieldMap.get(LaiJingRenYuanColumns.ZZMM));
        CommonUtils.selectSpinnerItem(mSpinnerShouJiaoYuChengDu, mFieldMap.get(LaiJingRenYuanColumns.SJYCD));
        CommonUtils.selectSpinnerItem(mSpinnerHuJiLeiBie, mFieldMap.get(LaiJingRenYuanColumns.HJLB));
        CommonUtils.selectSpinnerItem(mSpinnerHunYinZhuangKuang, mFieldMap.get(LaiJingRenYuanColumns.HYZK));
        mEditLianXiDianHua.setText(mFieldMap.get(LaiJingRenYuanColumns.LXDH));

        CommonUtils.selectSpinnerItem(mSpinnerHuJiDiZhi_Sheng, mFieldMap.get(LaiJingRenYuanColumns.HJDZS));
        mStrHuJiDiZhi_Shi = mFieldMap.get(LaiJingRenYuanColumns.HJDZSHI);
        mStrHuJiDiZhi_Qu = mFieldMap.get(LaiJingRenYuanColumns.HJDZQX);
        mEditHuJiXiangXiDiZhi.setText(mFieldMap.get(LaiJingRenYuanColumns.HJXXDZ));

        CommonUtils.selectSpinnerItem(mSpinnerChuShengDi, mFieldMap.get(LaiJingRenYuanColumns.CSD));
        CommonUtils.selectSpinnerItem(mSpinnerJuZhuZhengJian, mFieldMap.get(LaiJingRenYuanColumns.JZZJ));
        CommonUtils.selectSpinnerItem(mSpinnerHunYuZhengMing, mFieldMap.get(LaiJingRenYuanColumns.HYZM));
        CommonUtils.selectSpinnerItem(mSpinnerMianYiJieZhongZheng, mFieldMap.get(LaiJingRenYuanColumns.MYJZZ));

        // 家庭户流入
        CommonUtils.selectSpinnerItem(mSpinnerJiaTingHuLiuRu, mFieldMap.get(LaiJingRenYuanColumns.JTHLR));
        if (mSpinnerJiaTingHuLiuRu.getSelectedItem().toString().equals("是")) {
            CommonUtils.selectSpinnerItem(mSpinnerHuZhu, mFieldMap.get(LaiJingRenYuanColumns.SFHZ));

            if (mSpinnerHuZhu.getSelectedItem().toString().equals("是")) {
                mEditBenHuWaiLaiRenKouShu.setText(mFieldMap.get(LaiJingRenYuanColumns.BHWLRKZS));
                mEditBenHuWaiLaiRenKouShu_16.setText(mFieldMap.get(LaiJingRenYuanColumns.BHWLRK16));
                mEditBenHuWaiLaiRenKouShu_Nan.setText(mFieldMap.get(LaiJingRenYuanColumns.BHWLRKNAN));
                mEditBenHuWaiLaiRenKouShu_Nv.setText(mFieldMap.get(LaiJingRenYuanColumns.BHWLRKNV));
            } else {
                CommonUtils.selectSpinnerItem(mSpinnerYuHuZhuGuanXi, mFieldMap.get(LaiJingRenYuanColumns.YHZGX));
                if (mSpinnerYuHuZhuGuanXi.getSelectedItem().toString().equals("其他"))
                    mEditYuHuZhuXiangXiGuanXi.setText(mFieldMap.get(LaiJingRenYuanColumns.YHZXXGX));

                mEditHuZhuDengJiBiaoXuHao.setText(mFieldMap.get(LaiJingRenYuanColumns.HZDJBXH));
            }
        }

        // 居住信息
        mEditLiKaiYuanJiRiQi.setText(mFieldMap.get(LaiJingRenYuanColumns.LKYJRQ));
        mEditLaiJingRiQi.setText(mFieldMap.get(LaiJingRenYuanColumns.LJRQ));

        String from = mFieldMap.get(LaiJingRenYuanColumns.LRSB);
        String str = "";
        if ("1".equals(from)) {
            str = "手持机";
        } else if ("0".equals(from)) {
            str = "公寓系统";
        } else if ("2".equals(from)) {
            str = "流管办系统";
        }
        mEditLaiXianZhuDiRiQi.setText(str);

        CommonUtils.selectSpinnerItem(mSpinnerLaiJingYuanYin, mFieldMap.get(LaiJingRenYuanColumns.LJYY));
        if (mSpinnerLaiJingYuanYin.getSelectedItem().toString().equals("其他"))
            mEditLaiJingQiTaXiangXiYuanYin.setText(mFieldMap.get(LaiJingRenYuanColumns.LJQTXXYY));

        CommonUtils.selectSpinnerItem(mSpinnerJuZhuLeiXing, mFieldMap.get(LaiJingRenYuanColumns.JZLX));
        if (mSpinnerJuZhuLeiXing.getSelectedItem().toString().equals("租赁房屋")) {
            mEditFangWuDengJiBiaoXuHao.setText(mFieldMap.get(LaiJingRenYuanColumns.FWDJBXH));
        } else {
            if (mSpinnerJuZhuLeiXing.getSelectedItem().toString().equals("其他")) {
                mEditJuZhuQiTaLeiXing.setText(mFieldMap.get(LaiJingRenYuanColumns.JZQTLX));
            }

            CommonUtils.selectSpinnerItem(mSpinnerXianZhuDi_FenJu, mFieldMap.get(LaiJingRenYuanColumns.XZDFJ));
            CommonUtils.selectSpinnerItem(mSpinnerXianZhuDi_PaiChuSuo, mFieldMap.get(LaiJingRenYuanColumns.XZDPCS));
            mEditXianZhuDi_SheQu.setText(mFieldMap.get(LaiJingRenYuanColumns.XZDXZSQ));
            mStrXianZhuDi_JieDao = mFieldMap.get(LaiJingRenYuanColumns.XZDXZJD);
        }
        mEditXianZhuDiXiangZhi.setText(mFieldMap.get(LaiJingRenYuanColumns.XZDXZZT));
        mEditSuoShuPaiChuSuoMingCheng.setText(mFieldMap.get(LaiJingRenYuanColumns.SSPCSMC));
        mEditMinJingXingMing.setText(mFieldMap.get(LaiJingRenYuanColumns.MJXM));

        // 就业社保信息
        CommonUtils.selectSpinnerItem(mSpinnerMuQianZhuangKuang, mFieldMap.get(LaiJingRenYuanColumns.MQZK));
        if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("其他")) {
            mEditMuQianZhuangKuangXiangXiXinXi.setText(mFieldMap.get(LaiJingRenYuanColumns.MQZKXXXX));
        } else if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("学生")) {
            mEditXueXiaoMingCheng.setText(mFieldMap.get(LaiJingRenYuanColumns.XXMC));
            mEditXueXiaoSuoZaiDi.setText(mFieldMap.get(LaiJingRenYuanColumns.XXSZD));
        } else if (mSpinnerMuQianZhuangKuang.getSelectedItem().toString().equals("就业")) {
            mEditJiuYeDanWeiMingCheng.setText(mFieldMap.get(LaiJingRenYuanColumns.JYDWMC));
            mEditDanWeiDengJiBiaoXuHao.setText(mFieldMap.get(LaiJingRenYuanColumns.DWDJBXH));
            mEditJiuYeDanWeiXiangXiDiZhi.setText(mFieldMap.get(LaiJingRenYuanColumns.JYDWXXDZ));

            CommonUtils.selectSpinnerItem(mSpinnerJiuYeDanWeiSuoShuHangYe, mFieldMap.get(LaiJingRenYuanColumns.JYDWSSHY));
            //CommonUtils.selectSpinnerItem(mSpinnerZhuYaoCongShiGongZuo, mFieldMap.get(LaiJingRenYuanColumns.ZYCSGZ)));
            mStrZhuYaoCongShiGongZuo = mFieldMap.get(LaiJingRenYuanColumns.ZYCSGZ);
            CommonUtils.selectSpinnerItem(mSpinnerZhiYe, mFieldMap.get(LaiJingRenYuanColumns.ZY));
            CommonUtils.selectSpinnerItem(mSpinnerQianDingLaoDongHeTong, mFieldMap.get(LaiJingRenYuanColumns.QDLDHT));
            CommonUtils.selectSpinnerItem(mSpinnerJiuYeDanWeiSuoZaiDi, mFieldMap.get(LaiJingRenYuanColumns.JYDWSZD));

            if (mSpinnerJiuYeDanWeiSuoShuHangYe.getSelectedItem().toString().equals("其他行业"))
                mEditQiTaHangYe.setText(mFieldMap.get(LaiJingRenYuanColumns.QTHY));
            if (mStrZhuYaoCongShiGongZuo.contains("（描述）"))
                mEditQiTaZhuYaoCongShiGongZuo.setText(mFieldMap.get(LaiJingRenYuanColumns.QTZYCSGZMS));
        }

        String string = mFieldMap.get(LaiJingRenYuanColumns.ZJCJSHBX);
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

        string = mFieldMap.get(LaiJingRenYuanColumns.CJSHBX);
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
        mEditBeiZhu.setText(mFieldMap.get(LaiJingRenYuanColumns.BZ));
    }

}
