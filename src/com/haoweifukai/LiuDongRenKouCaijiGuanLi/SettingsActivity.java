/**
 * @author LuYongXing
 * @date 2014.09.02
 * @filename SettingActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_BXFGK_ML1;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_BXFGK_ML2;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_BXFGK_NR;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_CLLX;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_CSYS;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_GADW;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_HPZL;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_JLX;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_SQ;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_SQGLY;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_XZQH;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.F_SBSJDY;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.JiZhuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CSVUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;
import com.ry.fileexplorer.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SettingsActivity extends PermanentActivity implements View.OnClickListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static final String SERVER_ADDRESS_PREF = "server_address_pref";
    private static final int REQUEST_FILE_PATH = 1111;

    public static final int XingZhengQuHua = 0;
    public static final int GongAnDanWei = 1;
    public static final int JieLuXiang = 2;
    public static final int SheQu = 3;
    public static final int SheQuGuanLiYuan = 4;

    public static final int JiDongChe = 5;
    public static final int HaoPaiZhongLei = 6;
    public static final int CheShenYanSe = 7;

    public static final int BianXieFaGui_ZhuMuLu = 8;
    public static final int BianXieFaGui_FenMuLu = 9;
    public static final int BianXieFaGui_NeiRong = 10;

    private static final String[][] FIXED_COLUMNS = new String[][]{
            new String[]{"行政区划", "名称"},
            new String[]{"单位ID", "单位名称", "上级单位ID", "单位级别"},
            new String[]{"街路巷ID", "街路巷名称", "归属社区ID"},
            new String[]{"社区ID", "社区名称", "所属单位名称", "所属单位ID"},
            new String[]{"管理员编号", "管理员姓名", "社区ID"},
            new String[]{"代码", "代码内容"},
            new String[]{"代码", "代码内容", "备注"},
            new String[]{"代码", "颜色"},
            new String[]{"一级目录ID", "一级目录名称"},
            new String[]{"二级目录ID", "二级目录名称", "一级目录ID"},
            new String[]{"内容ID", "内容", "二级目录ID"},
    };

    private TextView mTextXingZhengQuHuaDaoRuLe;
    private TextView mTextGongAnDanWeiDaoRuLe;
    private TextView mTextJieLuXiangDaoRuLe;
    private TextView mTextSheQuDaoRuLe;
    private TextView mTextSheQuGuanLiYuanDaoRuLe;
    private TextView mTextJiDongCheDaoRuLe;
    private TextView mTextHaoPaiZhongLeiDaoRuLe;
    private TextView mTextCheShenYanSeDaoRuLe;
    private TextView mTextZhuMuLuDaoRuLe;
    private TextView mTextFenMuLuDaoRuLe;
    private TextView mTextNeiRongDaoRuLe;

    private EditText mEditFuWuQiDiZhi;

    private boolean mHasAccount = false;
    private int mImportType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        if (requestCode == REQUEST_FILE_PATH) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra(FileChooser.FILE_PATH_PREF);
                String fileName = data.getStringExtra(FileChooser.FILE_NAME_PREF);

                File csvFile = new File(path, fileName);

                switch (mImportType) {
                    case XingZhengQuHua:
                        // 导入行政区划从CSV文件
                        new InsertDataTask(csvFile, "导入行政区划").execute();
                        break;

                    case GongAnDanWei:
                        // 导入公安单位从CSV文件
                        new InsertDataTask(csvFile, "导入公安单位").execute();
                        break;

                    case JieLuXiang:
                        // 导入街路巷从CSV文件
                        new InsertDataTask(csvFile, "导入街路巷").execute();
                        break;

                    case SheQu:
                        // 导入社区从CSV文件
                        new InsertDataTask(csvFile, "导入社区").execute();
                        break;

                    case SheQuGuanLiYuan:
                        // 导入社区管理员从CSV文件
                        new InsertDataTask(csvFile, "导入社区管理员").execute();
                        break;

                    case JiDongChe:
                        // 导入机动车从CSV文件
                        new InsertDataTask(csvFile, "导入车辆类型").execute();
                        break;

                    case HaoPaiZhongLei:
                        // 导入号牌种类从CSV文件
                        new InsertDataTask(csvFile, "导入号牌种类").execute();
                        break;

                    case CheShenYanSe:
                        // 导入车身颜色从CSV文件
                        new InsertDataTask(csvFile, "导入车身颜色").execute();
                        break;

                    case BianXieFaGui_ZhuMuLu:
                        // 导入便携法规主目录从CSV文件
                        new InsertDataTask(csvFile, "导入主目录").execute();
                        break;

                    case BianXieFaGui_FenMuLu:
                        // 导入便携法规分目录从CSV文件
                        new InsertDataTask(csvFile, "导入分目录").execute();
                        break;

                    case BianXieFaGui_NeiRong:
                        // 导入内容从CSV文件
                        new InsertDataTask(csvFile, "导入内容").execute();
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_xingzhengquhua_daoru:
                mImportType = XingZhengQuHua;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_gongandanwei_daoru:
                mImportType = GongAnDanWei;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_jieluxiang_daoru:
                mImportType = JieLuXiang;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_shequ_daoru:
                mImportType = SheQu;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_shequguanliyuan_daoru:
                mImportType = SheQuGuanLiYuan;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_jidongche_daoru:
                mImportType = JiDongChe;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_haopaizhonglei_daoru:
                mImportType = HaoPaiZhongLei;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_cheshenyanse_daoru:
                mImportType = CheShenYanSe;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_zhumulu_daoru:
                mImportType = BianXieFaGui_ZhuMuLu;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_fenmulu_daoru:
                mImportType = BianXieFaGui_FenMuLu;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_neirong_daoru:
                mImportType = BianXieFaGui_NeiRong;
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_shezhi:
                saveServerAddress(mEditFuWuQiDiZhi.getText().toString());
                CommonUtils.hideKeyboard(this, mEditFuWuQiDiZhi);
                CommonUtils.createErrorAlertDialog(this, "设置成功！").show();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mHasAccount)
            menu.findItem(R.id.action_XiTongGuanLi).setVisible(false);
        else
            menu.clear();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkDictionaries();
    }

    /**
     * 初始化视图
     */
    @Override
    protected void initViews() {
        super.initViews();

        mLayoutInflater.inflate(R.layout.activity_settings, mLayoutContainer);

        // 行政区划
        mTextXingZhengQuHuaDaoRuLe = (TextView) findViewById(R.id.text_xingzhengquhua_daorule);
        findViewById(R.id.button_xingzhengquhua_daoru).setOnClickListener(this);

        // 公安单位
        mTextGongAnDanWeiDaoRuLe = (TextView) findViewById(R.id.text_gongandanwei_daorule);
        findViewById(R.id.button_gongandanwei_daoru).setOnClickListener(this);

        // 街路巷
        mTextJieLuXiangDaoRuLe = (TextView) findViewById(R.id.text_jieluxiang_daorule);
        findViewById(R.id.button_jieluxiang_daoru).setOnClickListener(this);

        // 社区
        mTextSheQuDaoRuLe = (TextView) findViewById(R.id.text_shequ_daorule);
        findViewById(R.id.button_shequ_daoru).setOnClickListener(this);

        // 社区管理员
        mTextSheQuGuanLiYuanDaoRuLe = (TextView) findViewById(R.id.text_shequguanliyuan_daorule);
        findViewById(R.id.button_shequguanliyuan_daoru).setOnClickListener(this);

        // 机动车
        mTextJiDongCheDaoRuLe = (TextView) findViewById(R.id.text_jidongche_daorule);
        findViewById(R.id.button_jidongche_daoru).setOnClickListener(this);

        // 号牌种类
        mTextHaoPaiZhongLeiDaoRuLe = (TextView) findViewById(R.id.text_haopaizhonglei_daorule);
        findViewById(R.id.button_haopaizhonglei_daoru).setOnClickListener(this);

        // 车声颜色
        mTextCheShenYanSeDaoRuLe = (TextView) findViewById(R.id.text_cheshenyanse_daorule);
        findViewById(R.id.button_cheshenyanse_daoru).setOnClickListener(this);

        // 便携法规库 - 主目录
        mTextZhuMuLuDaoRuLe = (TextView) findViewById(R.id.text_zhumulu_daorule);
        findViewById(R.id.button_zhumulu_daoru).setOnClickListener(this);

        // 便携法规库 - 分目录
        mTextFenMuLuDaoRuLe = (TextView) findViewById(R.id.text_fenmulu_daorule);
        findViewById(R.id.button_fenmulu_daoru).setOnClickListener(this);

        // 便携法规库 - 内容
        mTextNeiRongDaoRuLe = (TextView) findViewById(R.id.text_neirong_daorule);
        findViewById(R.id.button_neirong_daoru).setOnClickListener(this);

        // 服务器地址
        mEditFuWuQiDiZhi = (EditText) findViewById(R.id.edit_fuwuqidizhi);
        mEditFuWuQiDiZhi.setText(loadServerAddress(this));
        findViewById(R.id.button_shezhi).setOnClickListener(this);

        mHasAccount = new MyContentProvider().fetchCount(CommonUtils.getLastPathFromUri(JiZhuColumns.CONTENT_URI), "") > 0;
    }

    /**
     * 检查代码字典
     */
    private void checkDictionaries() {
        MyContentProvider contentProvider = new MyContentProvider();

        long count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_XZQH.CONTENT_URI), "");
        mTextXingZhengQuHuaDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_GADW.CONTENT_URI), "");
        mTextGongAnDanWeiDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_JLX.CONTENT_URI), "");
        mTextJieLuXiangDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_SQ.CONTENT_URI), "");
        mTextSheQuDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_SQGLY.CONTENT_URI), "");
        mTextSheQuGuanLiYuanDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_CLLX.CONTENT_URI), "");
        mTextJiDongCheDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_HPZL.CONTENT_URI), "");
        mTextHaoPaiZhongLeiDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_CSYS.CONTENT_URI), "");
        mTextCheShenYanSeDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_BXFGK_ML1.CONTENT_URI), "");
        mTextZhuMuLuDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_BXFGK_ML2.CONTENT_URI), "");
        mTextFenMuLuDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(C_BXFGK_NR.CONTENT_URI), "");
        mTextNeiRongDaoRuLe.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 开后对话框
     */
    private void openPromptAddAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage("要使用采集系统，您必须添加一个用户。请添加新用户信息。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(SettingsActivity.this, ManageAccountActivity.class));
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * 获得服务器地址
     */
    public static String loadServerAddress(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(SERVER_ADDRESS_PREF, "");
    }

    /**
     * 保存服务器地址
     */
    private void saveServerAddress(String address) {
        if (!TextUtils.isEmpty(address) && !address.startsWith("http://")) {
            address = "http://" + address;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SERVER_ADDRESS_PREF, address);
        editor.apply();
    }

    /**
     * 导入从CSV文件数据库
     */
    private class InsertDataTask extends AsyncTask<Void, Integer, String> {

        private File mCSVFile;
        private String mFunction;

        private ProgressDialog mProgressDialog;

        public InsertDataTask(File file, String function) {
            mCSVFile = file;
            mFunction = function;

            mProgressDialog = new ProgressDialog(SettingsActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("正在处理中...");
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String error) {
            mProgressDialog.dismiss();

            if (error != null) {
                CommonUtils.createErrorAlertDialog(SettingsActivity.this, error).show();
            }

            checkDictionaries();

            if (new MyContentProvider().checkConstantTablesExist() && !mHasAccount) {
                openPromptAddAccountDialog();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            MyContentProvider contentProvider = new MyContentProvider();

            // 获得特定表的项目从数据库
            String tableName = "";
            ArrayList<String> fieldNameList = new ArrayList<String>();

            Cursor cursor = contentProvider.query(F_SBSJDY.CONTENT_URI, null,
                    F_SBSJDY.SJJHGN + " IS '" + mFunction + "'", null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    tableName = cursor.getString(cursor.getColumnIndex(F_SBSJDY.JLSJBM));
                    String columns = cursor.getString(cursor.getColumnIndex(F_SBSJDY.SJJLZD));

                    String[] strContent = columns.split(",");
                    Collections.addAll(fieldNameList, strContent);
                }

                cursor.close();
            }

            ArrayList<String> fixedColumnList = new ArrayList<String>();
            ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();

            // 读取文件内容，然后添加到数据库
            if (CSVUtil.readFromCSV(mCSVFile, fixedColumnList, valueList)) {
                if (fixedColumnList.size() != fieldNameList.size())
                    return "错误的记录，数据项个数不符，请检查数据！";

                int i = 0;
                for (String fixedColumn : fixedColumnList) {
                    if (!fixedColumn.equals(FIXED_COLUMNS[mImportType][i++]))
                        return "错误的记录，数据项名不符，请检查数据！";
                }

                Uri contentUri = contentProvider.getContentUri(tableName);

                // 检查“导入时间”项目存在有无
                boolean hasImportTime = false;

                try {
                    cursor = contentProvider.query(contentUri, new String[]{"DRSJ"}, null, null, null);
                    if (cursor != null) cursor.close();

                    hasImportTime = true;
                } catch (Exception ex) {
                    if (Config.DEBUG) ex.printStackTrace();
                    Log.e(TAG, "'DRSJ' filed not exists in " + tableName);
                }

                ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

                // 删除以前的数据
                operations.add(ContentProviderOperation.newDelete(contentUri)
                        .withSelection(null, null)
                        .build());

                // 读取一行并添加到数据库
                for (ArrayList<String> oneRecord : valueList) {
                    ContentValues contentValues = new ContentValues();
                    i = 0;

                    for (String column : oneRecord)
                        contentValues.put(fieldNameList.get(i++), column);

                    if (hasImportTime)
                        contentValues.put("DRSJ", CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));

                    operations.add(ContentProviderOperation.newInsert(contentUri)
                            .withValues(contentValues)
                            .build());
                }

                try {
                    getContentResolver().applyBatch(MyContentProvider.AUTHORITY, operations);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            } else {
                return "错误的CSV文件！";
            }

            return null;
        }
    }

}
