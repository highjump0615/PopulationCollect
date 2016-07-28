/**
 * @author LuYongXing
 * @date 2014.08.29
 * @filename ReportDataActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CheLiangColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CSVUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.PhotoUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.UploadUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.XLSUtil;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.ZipUtil;
import com.ry.fileexplorer.DirectoryChooser;
import com.ry.fileexplorer.FileChooser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class ReportDataActivity extends PermanentActivity implements View.OnClickListener {

    private static final String TAG = ReportDataActivity.class.getSimpleName();

    private static final int REQUEST_IMPORT_FILE_PATH = 1111;
    private static final int REQUEST_EXPORT_DIRECTORY_PATH = 2222;

    private TextView mTextHaiWeiShangBaoJiLu;
    private TextView mTextChengGongShangBaoJiLu;
    private TextView mTextYiJingShangBaoJiLu;

    private TextView mTextXianZaiShiJian;

    private EditText mEditStartDate;
    private EditText mEditEndDate;

    private Button mButtonShangChuan;
    private Button mButtonXiaZai;
    private Button mButtonDaoChu;
    private Button mButtonDaoRu;

    private ProgressDialog mProgressDialog;

    private long mWillUploadDataCount = 0;

    private Handler mHandler;
    private boolean mTickerStopped = false;

    private String mServerAddress;

    private HashMap<String, LinkedHashMap<String, Boolean>> mUploadMap = new HashMap<String, LinkedHashMap<String, Boolean>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        if (requestCode == REQUEST_IMPORT_FILE_PATH) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra(FileChooser.FILE_PATH_PREF);
                String fileName = data.getStringExtra(FileChooser.FILE_NAME_PREF);

                File xlsFile = new File(path, fileName);
                new PrepareImportDataTask().execute(xlsFile);
            }
        } else if (requestCode == REQUEST_EXPORT_DIRECTORY_PATH) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra(DirectoryChooser.DIRECTORY_PATH_PREF);
                new PrepareExportDataTask(true, path).execute();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_shangchuan:
                mServerAddress = SettingsActivity.loadServerAddress(this);
                if (TextUtils.isEmpty(mServerAddress)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_name)
                            .setMessage("要使用无线功能，您必须设置服务器地址。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ReportDataActivity.this, SettingsActivity.class));
                                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    onBackPressed();
                                }
                            }).create().show();
                } else {
                    new PrepareExportDataTask(false, null).execute();
                }
                break;

            case R.id.button_xiazai:
                break;

            case R.id.button_daochu:
                startActivityForResult(new Intent(this, DirectoryChooser.class), REQUEST_EXPORT_DIRECTORY_PATH);
                break;

            case R.id.button_daoru:
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_IMPORT_FILE_PATH);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTickerStopped = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTickerStopped = false;
        mHandler = new Handler();

        /**
         * Requests a tick on the next hard-second boundary
         */
        new Runnable() {
            @Override
            public void run() {
                if (mTickerStopped) return;

                mTextXianZaiShiJian.setText(CommonUtils.getFormattedDateString(new Date(), "yyyy年MM月dd日 HH:mm:ss"));

                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(this, next);
            }
        }.run();

        getRecordCount();

        if (mWillUploadDataCount != 0) {
            mButtonShangChuan.setEnabled(true);
        } else {
            mButtonShangChuan.setEnabled(false);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_TianJia).setVisible(false);
        menu.findItem(R.id.action_ShangBaoShuJu).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void initViews() {
        super.initViews();

        // 添加
        mLayoutInflater.inflate(R.layout.activity_report_data, mLayoutContainer);

        // 还未上报数据记录
        mTextHaiWeiShangBaoJiLu = (TextView) findViewById(R.id.text_haiweishangbaojilu);

        // 成功上报数据记录
        mTextChengGongShangBaoJiLu = (TextView) findViewById(R.id.text_yijingshangbaojilu);

        // 已经上报数据记录
        mTextYiJingShangBaoJiLu = (TextView) findViewById(R.id.text_quanbushangbaojilu);

        // 年月日时分秒
        mTextXianZaiShiJian = (TextView) findViewById(R.id.text_xianzaishijian);

        // 开始日子
        mEditStartDate = (EditText) findViewById(R.id.edit_start_date);
        mEditStartDate.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(this, mEditStartDate));

        // 结束日子
        mEditEndDate = (EditText) findViewById(R.id.edit_end_date);
        mEditEndDate.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(this, mEditEndDate));

        // 上传数据到服务器
        mButtonShangChuan = (Button) findViewById(R.id.button_shangchuan);
        mButtonShangChuan.setOnClickListener(this);

        // 下载数据从服务器
        mButtonXiaZai = (Button) findViewById(R.id.button_xiazai);
        mButtonXiaZai.setOnClickListener(this);
        mButtonXiaZai.setVisibility(View.GONE);

        // 导出数据成CSV文件格式
        mButtonDaoChu = (Button) findViewById(R.id.button_daochu);
        mButtonDaoChu.setOnClickListener(this);

        // 导入数据从CSV文件格式
        mButtonDaoRu = (Button) findViewById(R.id.button_daoru);
        mButtonDaoRu.setOnClickListener(this);
        mButtonDaoRu.setVisibility(View.GONE);
    }

    private void getRecordCount() {
        MyContentProvider contentProvider = new MyContentProvider();

        mWillUploadDataCount = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(FangWuColumns.CONTENT_URI),
                ShangChuanColumns.SFSCZFWQ + "=0 AND " + ShangChuanColumns.SFSC + "=0");
        mWillUploadDataCount += contentProvider.fetchCount(CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI),
                ShangChuanColumns.SFSCZFWQ + "=0 AND " + ShangChuanColumns.SFSC + "=0");
        mWillUploadDataCount += contentProvider.fetchCount(CommonUtils.getLastPathFromUri(CheLiangColumns.CONTENT_URI),
                ShangChuanColumns.SFSCZFWQ + "=0 AND " + ShangChuanColumns.SFSC + "=0");

        long already_upload_count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(FangWuColumns.CONTENT_URI),
                ShangChuanColumns.SFSCZFWQ + "=1 AND " + ShangChuanColumns.SFSC + "=0");
        already_upload_count += contentProvider.fetchCount(CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI),
                ShangChuanColumns.SFSCZFWQ + "=1 AND " + ShangChuanColumns.SFSC + "=0");
        already_upload_count += contentProvider.fetchCount(CommonUtils.getLastPathFromUri(CheLiangColumns.CONTENT_URI),
                ShangChuanColumns.SFSCZFWQ + "=1 AND " + ShangChuanColumns.SFSC + "=0");

        mTextHaiWeiShangBaoJiLu.setText(getString(R.string.HaiWeiShangBaoShuJuJiLu, mWillUploadDataCount));

        mTextChengGongShangBaoJiLu.setText(getString(R.string.YiJingShangBaoShuJuJiLu, already_upload_count));

        mTextYiJingShangBaoJiLu.setText(getString(R.string.QuanBuShangBaoShuJuJiLu, mWillUploadDataCount + already_upload_count));
    }

    /**
     * 是否上传至服务器标志设置为是
     */
    private void setUploadFlag() {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        // 房屋登记
        LinkedHashMap<String, Boolean> linkedHashMap = mUploadMap.get(FangWuColumns.CONTENT_TYPE);
        Set<String> idArray;

        if (linkedHashMap != null) {
            idArray = linkedHashMap.keySet();

            for (String fwID : idArray) {
                if (linkedHashMap.get(fwID)) {
                    operations.add(ContentProviderOperation.newUpdate(FangWuColumns.CONTENT_URI)
                            .withSelection(FangWuColumns.FWID + " LIKE '" + fwID + "'", null)
                            .withValue(ShangChuanColumns.SFSCZFWQ, true)
                            .build());
                }
            }
        }

        // 来京人员登记
        linkedHashMap = mUploadMap.get(LaiJingRenYuanColumns.CONTENT_TYPE);
        if (linkedHashMap != null) {
            idArray = linkedHashMap.keySet();

            for (String ryID : idArray) {
                if (linkedHashMap.get(ryID)) {
                    operations.add(ContentProviderOperation.newUpdate(LaiJingRenYuanColumns.CONTENT_URI)
                            .withSelection(LaiJingRenYuanColumns.RYID + " LIKE '" + ryID + "'", null)
                            .withValue(ShangChuanColumns.SFSCZFWQ, true)
                            .build());
                }
            }
        }

        // 车辆信息登记
        linkedHashMap = mUploadMap.get(CheLiangColumns.CONTENT_TYPE);
        if (linkedHashMap != null) {
            idArray = linkedHashMap.keySet();

            for (String clID : idArray) {
                if (linkedHashMap.get(clID)) {
                    operations.add(ContentProviderOperation.newUpdate(CheLiangColumns.CONTENT_URI)
                            .withSelection(CheLiangColumns.CLID + " LIKE '" + clID + "'", null)
                            .withValue(ShangChuanColumns.SFSCZFWQ, true)
                            .build());
                }
            }
        }

        try {
            getContentResolver().applyBatch(MyContentProvider.AUTHORITY, operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备上传
     */
    private class PrepareExportDataTask extends AsyncTask<Void, Integer, Integer> {

        private boolean mIsFileExport;// true: Export, false: Upload
        private String mExportPath;

        private String mStartDate;
        private String mEndDate;

        public PrepareExportDataTask(boolean isExport, String path) {
            mIsFileExport = isExport;
            mExportPath = path;

            mStartDate = mEditStartDate.getText().toString();
            mEndDate = mEditEndDate.getText().toString();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ReportDataActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("正在加载数据...");
            mProgressDialog.show();

            mUploadMap.clear();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            MyContentProvider contentProvider = new MyContentProvider();
            int count = 0;

            String where = ShangChuanColumns.SFSC + "=0";

            if (!mIsFileExport) {
                where += " AND " + ShangChuanColumns.SFSCZFWQ + "=0";
            }

            if (!TextUtils.isEmpty(mStartDate) && !TextUtils.isEmpty(mEndDate)) {
                where += " AND strftime('%Y-%m-%d', LRSJ) > '" + mStartDate + "' AND strftime('%Y-%m-%d', LRSJ) < '" + mEndDate + "'";
            } else if (!TextUtils.isEmpty(mStartDate)) {
                where += " AND strftime('%Y-%m-%d', LRSJ) > '" + mStartDate + "'";
            } else if (!TextUtils.isEmpty(mEndDate)) {
                where += " AND strftime('%Y-%m-%d', LRSJ) < '" + mEndDate + "'";
            }

            // 房屋信息
            Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI, new String[]{FangWuColumns.FWID},
                    where, null, FangWuColumns.SORT_ORDER_DEFAULT);
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    count += cursor.getCount();

                    LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>();

                    do {
                        linkedHashMap.put(cursor.getString(0), false);
                    } while (cursor.moveToNext());

                    mUploadMap.put(FangWuColumns.CONTENT_TYPE, linkedHashMap);
                }

                cursor.close();
            }

            // 来京人员信息
            cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI, new String[]{LaiJingRenYuanColumns.RYID},
                    where, null, LaiJingRenYuanColumns.SORT_ORDER_DEFAULT);
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    count += cursor.getCount();

                    LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>();

                    do {
                        linkedHashMap.put(cursor.getString(0), false);
                    } while (cursor.moveToNext());

                    mUploadMap.put(LaiJingRenYuanColumns.CONTENT_TYPE, linkedHashMap);
                }

                cursor.close();
            }

            // 车辆信息登记
            cursor = contentProvider.query(CheLiangColumns.CONTENT_URI, new String[]{CheLiangColumns.CLID},
                    where, null, CheLiangColumns.SORT_ORDER_DEFAULT);
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    count += cursor.getCount();

                    LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>();

                    do {
                        linkedHashMap.put(cursor.getString(0), false);
                    } while (cursor.moveToNext());

                    mUploadMap.put(CheLiangColumns.CONTENT_TYPE, linkedHashMap);
                }

                cursor.close();
            }

            return count;
        }

        @Override
        protected void onPostExecute(Integer count) {
            mProgressDialog.dismiss();

            if (count == 0) {
                String message = mIsFileExport ? "没有导出数据！" : "没有上传数据！";
                CommonUtils.createErrorAlertDialog(ReportDataActivity.this, message).show();
                return;
            }

            if (mIsFileExport)
                new ExportDataTask(count, mExportPath).execute();
            else
                new UploadDataTask(count).execute();
        }

    }

    /**
     * 上传数据到服务器
     */
    public class UploadDataTask extends AsyncTask<Void, Integer, Boolean> {

        private int mCount;

        public UploadDataTask(int count) {
            mCount = count;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ReportDataActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(mCount);
            mProgressDialog.setMessage("正在上传中...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mProgressDialog.dismiss();

            if (success) {
                setUploadFlag();
                onResume();
                CommonUtils.createErrorAlertDialog(ReportDataActivity.this, "成功上传完成").show();
            } else {
                CommonUtils.createErrorAlertDialog(ReportDataActivity.this, "上传失败").show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MyContentProvider contentProvider = new MyContentProvider();
            int uploadIndex = 0;
            boolean uploadSuccess1 = false;
            boolean uploadSuccess2 = false;
            boolean uploadSuccess3 = false;

            // 房屋登记
            LinkedHashMap<String, Boolean> linkedHashMap = mUploadMap.get(FangWuColumns.CONTENT_TYPE);
            if (linkedHashMap != null && linkedHashMap.size() > 0) {
                String fixedLine;
                String csvString;
                String containString = "";

                for (String str : linkedHashMap.keySet()) {
                    containString += ("'" + str + "',");
                }
                containString = containString.substring(0, containString.length() - 1);
                containString = FangWuColumns.FWID + " IN (" + containString + ")";

                Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI, null,
                        containString, null, FangWuColumns.SORT_ORDER_DEFAULT);

                if (cursor != null) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        String tableName = CommonUtils.getLastPathFromUri(FangWuColumns.CONTENT_URI);
                        fixedLine = CSVUtil.getFixedColumns(tableName, cursor) + "\n";
                        Log.e(TAG, fixedLine);

                        do {
                            String oneLine = CSVUtil.getOneLineOfCSV(tableName, cursor, true);
                            Log.e(TAG, oneLine);

                            csvString = (fixedLine + oneLine + "\n");

                            if (!TextUtils.isEmpty(csvString)) {
                                HashMap<String, String> paramMap = new HashMap<String, String>();
                                paramMap.put("action", UploadUtil.UPLOAD_ROOM_DATA_ACTION);
                                paramMap.put("actionid", mOwnerData.MJID);
                                paramMap.put("data", csvString);
                                paramMap.put("content_uri", FangWuColumns.CONTENT_TYPE);

                                if (runApi(UploadUtil.UPLOAD_DATA_API, paramMap)) {
                                    uploadSuccess1 = true;
                                    uploadImageFile(UploadUtil.UPLOAD_ROOM_IMAGE_ACTION, cursor, true);

                                    linkedHashMap.put(cursor.getString(cursor.getColumnIndex(FangWuColumns.FWID)), true);
                                }
                            }

                            publishProgress(++uploadIndex);
                        } while (cursor.moveToNext());

                        if (uploadSuccess1)
                            new MyContentProvider().addSystemLog(mOwnerData, "信息导入导出", "房屋信息", "", "上传房屋登记", "民警");
                    }

                    cursor.close();
                }
            }

            // 来京人员登记
            linkedHashMap = mUploadMap.get(LaiJingRenYuanColumns.CONTENT_TYPE);
            if (linkedHashMap != null && linkedHashMap.size() > 0) {
                String fixedLine;
                String csvString;
                String containString = "";

                for (String str : linkedHashMap.keySet()) {
                    containString += ("'" + str + "',");
                }
                containString = containString.substring(0, containString.length() - 1);
                containString = LaiJingRenYuanColumns.RYID + " IN (" + containString + ")";

                Cursor cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI, null,
                        containString, null, LaiJingRenYuanColumns.SORT_ORDER_DEFAULT);

                if (cursor != null) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        String tableName = CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI);
                        fixedLine = CSVUtil.getFixedColumns(tableName, cursor) + "\n";
                        Log.e(TAG, fixedLine);

                        do {
                            String oneLine = CSVUtil.getOneLineOfCSV(tableName, cursor, true);
                            Log.e(TAG, oneLine);

                            csvString = (fixedLine + oneLine + "\n");

                            if (!TextUtils.isEmpty(csvString)) {
                                HashMap<String, String> paramMap = new HashMap<String, String>();
                                paramMap.put("action", UploadUtil.UPLOAD_PERSON_DATA_ACTION);
                                paramMap.put("actionid", mOwnerData.MJID);
                                paramMap.put("data", csvString);
                                paramMap.put("content_uri", LaiJingRenYuanColumns.CONTENT_TYPE);

                                if (runApi(UploadUtil.UPLOAD_DATA_API, paramMap)) {
                                    uploadSuccess2 = true;
                                    uploadImageFile(UploadUtil.UPLOAD_PERSON_IMAGE_ACTION, cursor, false);

                                    linkedHashMap.put(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.RYID)), true);
                                }
                            }

                            publishProgress(++uploadIndex);
                        } while (cursor.moveToNext());

                        if (uploadSuccess2)
                            new MyContentProvider().addSystemLog(mOwnerData, "信息导入导出", "来京人员信息", "", "上传来京人员登记", "民警");
                    }

                    cursor.close();
                }
            }

            // 车辆信息登记
            linkedHashMap = mUploadMap.get(CheLiangColumns.CONTENT_TYPE);
            if (linkedHashMap != null && linkedHashMap.size() > 0) {
                String fixedLine;
                String csvString;
                String containString = "";

                for (String str : linkedHashMap.keySet()) {
                    containString += ("'" + str + "',");
                }
                containString = containString.substring(0, containString.length() - 1);
                containString = CheLiangColumns.CLID + " IN (" + containString + ")";

                Cursor cursor = contentProvider.query(CheLiangColumns.CONTENT_URI, null,
                        containString, null, CheLiangColumns.SORT_ORDER_DEFAULT);

                if (cursor != null) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        String tableName = CommonUtils.getLastPathFromUri(CheLiangColumns.CONTENT_URI);
                        fixedLine = CSVUtil.getFixedColumns(tableName, cursor) + "\n";
                        Log.e(TAG, fixedLine);

                        do {
                            String oneLine = CSVUtil.getOneLineOfCSV(tableName, cursor, true);
                            Log.e(TAG, oneLine);

                            csvString = (fixedLine + oneLine + "\n");

                            if (!TextUtils.isEmpty(csvString)) {
                                HashMap<String, String> paramMap = new HashMap<String, String>();
                                paramMap.put("action", UploadUtil.UPLOAD_VEHICLE_DATA_ACTION);
                                paramMap.put("actionid", mOwnerData.MJID);
                                paramMap.put("data", csvString);
                                paramMap.put("content_uri", CheLiangColumns.CONTENT_TYPE);

                                if (runApi(UploadUtil.UPLOAD_DATA_API, paramMap)) {
                                    uploadSuccess3 = true;
                                    uploadImageFile(UploadUtil.UPLOAD_VEHICLE_IMAGE_ACTION, cursor, false);

                                    linkedHashMap.put(cursor.getString(cursor.getColumnIndex(CheLiangColumns.CLID)), true);
                                }
                            }

                            publishProgress(++uploadIndex);
                        } while (cursor.moveToNext());

                        if (uploadSuccess3)
                            new MyContentProvider().addSystemLog(mOwnerData, "信息导入导出", "车辆信息", "", "上传车辆信息登记", "民警");
                    }

                    cursor.close();
                }
            }

            return (uploadSuccess1 || uploadSuccess2 || uploadSuccess3);
        }
    }

    public boolean uploadImageFile(String action, Cursor cursor, boolean hasVideo) {
        if (cursor == null) return true;

        String fileName = cursor.getString(cursor.getColumnIndex("ZPURL"));
        if (!TextUtils.isEmpty(fileName)) {
            ArrayList<String> fileNameList = PhotoUtil.getNamesFromString(fileName);

            for (String name : fileNameList) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("actionid", name);

                runApi(UploadUtil.UPLOAD_IMAGE_API, params);
            }
        }

        if (hasVideo) {
            fileName = cursor.getString(cursor.getColumnIndex("SXURL"));

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("action", UploadUtil.UPLOAD_ROOM_VIDEO_ACTION);
            params.put("actionid", fileName);

            runApi(UploadUtil.UPLOAD_IMAGE_API, params);
        }

        return true;
    }

    public boolean runApi(String api, HashMap<String, String> params) {
        try {
            String strApi = String.format("%s/%s", mServerAddress, api);

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(strApi);
            HttpEntity formEntity = null;

            String data;

            LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>();

            if (api.equals(UploadUtil.UPLOAD_DATA_API)) {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                if (params != null) {
                    for (String strKey : params.keySet()) {
                        data = params.get(strKey);

                        nameValuePairs.add(new BasicNameValuePair(strKey, URLEncoder.encode(data, "UTF-8")));

                        if (strKey.equals("content_uri")) {
                            linkedHashMap = mUploadMap.get(data);
                        }
                    }

                    formEntity = new UrlEncodedFormEntity(nameValuePairs);
                    ((UrlEncodedFormEntity) formEntity).setContentEncoding("UTF-8");
                }
            } else if (api.equals(UploadUtil.UPLOAD_IMAGE_API)) {
                if (params != null) {
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                    multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    String action = params.get("action");
                    String actionId = params.get("actionid");
                    String fileName = CommonUtils.getMyApplicationDirectory(
                            ReportDataActivity.this, Config.IMAGE_DIRECTORY_NAME) + File.separator + actionId;

                    if (Config.DEBUG) Log.d(TAG, "upload filename = " + fileName);

                    multipartEntityBuilder.addTextBody("action", URLEncoder.encode(action, "UTF-8"));
                    multipartEntityBuilder.addTextBody("actionid", URLEncoder.encode(actionId, "UTF-8"));
                    multipartEntityBuilder.addBinaryBody("data", new File(fileName));

                    formEntity = multipartEntityBuilder.build();
                }
            }

            httppost.setEntity(formEntity);

            HttpResponse response = httpclient.execute(httppost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                StringBuilder builder = new StringBuilder();
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("error:")) {
                        String[] results = line.split(",");

                        if (results.length > 2 && results[0].equals("error:0")) {
                            String id = results[2];
                            linkedHashMap.put(id, true);

                            builder.append(id).append(",");
                        }
                    }
                }

                String result = builder.toString();
                if (Config.DEBUG) Log.d(TAG, "result = " + result);

                return !TextUtils.isEmpty(result);
            } else {
                return false;
            }
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return false;
    }

    /**
     * 导出数据成XLS文件格式
     */
    private class ExportDataTask extends AsyncTask<Void, Integer, String> {

        private int mCount;
        private String mPath;
        private ArrayList<String> mExportFileList = new ArrayList<String>();

        public ExportDataTask(int count, String path) {
            mCount = count;
            mPath = path;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ReportDataActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(mCount);
            mProgressDialog.setMessage("正在导出数据...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();

            /*String message = result;
            if (TextUtils.isEmpty(message)) {
                message = "成功导出完成！";
                for (String filename : mExportFileList) {
                    message += ("\n\n" + filename);
                }
            }*/
            CommonUtils.createErrorAlertDialog(ReportDataActivity.this, result).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(Void... params) {
            // Create root directory
            String rootDirectoryName = "APP" + CommonUtils.getFormattedDateString(new Date(), "yyyyMMddHHmmss")
                    + "(" + CommonUtils.convertDateFormat("yyyy-MM-dd", "yyyyMMdd", mEditStartDate.getText().toString())
                    + "~" + CommonUtils.convertDateFormat("yyyy-MM-dd", "yyyyMMdd", mEditEndDate.getText().toString()) + ")";
            rootDirectoryName = mPath + File.separator + rootDirectoryName;

            CommonUtils.createDirectory(rootDirectoryName);

            if (Config.DEBUG) Log.i(TAG, "export root directory = " + rootDirectoryName);

            // Export XLS to root directory
            exportToXLSAndPhoto(rootDirectoryName);

            // Zip file
            try {
                ZipUtil.zip(rootDirectoryName, rootDirectoryName + ".zip");
            } catch (Exception e) {
                return "到ZIP文件导出中失败！";
            }

            // Delete all temporary directories and files
            CommonUtils.deleteDirectory(new File(rootDirectoryName));

            return "成功导出完成！" + "\n\n" + rootDirectoryName + ".zip";
        }

        private void exportToXLSAndPhoto(String rootDirectoryPath) {
            MyContentProvider contentProvider = new MyContentProvider();
            int count = 0;

            // 房屋登记
            LinkedHashMap<String, Boolean> linkedHashMap = mUploadMap.get(FangWuColumns.CONTENT_TYPE);
            if (linkedHashMap != null && linkedHashMap.size() > 0) {
                String containString = "";

                for (String str : linkedHashMap.keySet()) {
                    containString += ("'" + str + "',");
                }
                containString = containString.substring(0, containString.length() - 1);
                containString = FangWuColumns.FWID + " IN (" + containString + ")";

                Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI, null,
                        containString, null, FangWuColumns.SORT_ORDER_DEFAULT);

                if (cursor != null) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        count = cursor.getCount();

                        String filename = XLSUtil.exportToXLS(ReportDataActivity.this, rootDirectoryPath, XLSUtil.FANGWU_TABLE_INDEX, cursor, new ProgressUpdateListener() {
                            @Override
                            public void progressChanged(Integer progress) {
                                publishProgress(progress);
                            }
                        });

                        if (!TextUtils.isEmpty(filename)) {
                            mExportFileList.add(filename);
                            new MyContentProvider().addSystemLog(mOwnerData, "信息导入导出", "房屋信息", "", "导出房屋信息", "民警");
                        }
                    }

                    cursor.close();
                }
            }

            // 来京人员登记
            linkedHashMap = mUploadMap.get(LaiJingRenYuanColumns.CONTENT_TYPE);
            if (linkedHashMap != null && linkedHashMap.size() > 0) {
                String containString = "";

                for (String str : linkedHashMap.keySet()) {
                    containString += ("'" + str + "',");
                }
                containString = containString.substring(0, containString.length() - 1);
                containString = LaiJingRenYuanColumns.RYID + " IN (" + containString + ")";

                Cursor cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI, null,
                        containString, null, LaiJingRenYuanColumns.SORT_ORDER_DEFAULT);

                if (cursor != null) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        final int finalCount = count;
                        String filename = XLSUtil.exportToXLS(ReportDataActivity.this, rootDirectoryPath, XLSUtil.RENYUAN_TABLE_INDEX, cursor, new ProgressUpdateListener() {
                            @Override
                            public void progressChanged(Integer progress) {
                                publishProgress(finalCount + progress);
                            }
                        });

                        if (!TextUtils.isEmpty(filename)) {
                            mExportFileList.add(filename);
                            new MyContentProvider().addSystemLog(mOwnerData, "信息导入导出", "来京人员信息", "", "导出来京人员信息", "民警");
                        }
                    }

                    cursor.close();
                }
            }

            // 车辆信息登记
            linkedHashMap = mUploadMap.get(CheLiangColumns.CONTENT_TYPE);
            if (linkedHashMap != null && linkedHashMap.size() > 0) {
                String containString = "";

                for (String str : linkedHashMap.keySet()) {
                    containString += ("'" + str + "',");
                }
                containString = containString.substring(0, containString.length() - 1);
                containString = CheLiangColumns.CLID + " IN (" + containString + ")";

                Cursor cursor = contentProvider.query(CheLiangColumns.CONTENT_URI, null,
                        containString, null, CheLiangColumns.SORT_ORDER_DEFAULT);

                if (cursor != null) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        final int finalCount = count;
                        String filename = XLSUtil.exportToXLS(ReportDataActivity.this, rootDirectoryPath, XLSUtil.CHELIANG_TABLE_INDEX, cursor, new ProgressUpdateListener() {
                            @Override
                            public void progressChanged(Integer progress) {
                                publishProgress(finalCount + progress);
                            }
                        });

                        if (!TextUtils.isEmpty(filename)) {
                            mExportFileList.add(filename);
                            new MyContentProvider().addSystemLog(mOwnerData, "信息导入导出", "车辆信息", "", "导出车辆信息", "民警");
                        }
                    }

                    cursor.close();
                }
            }
        }
    }

    /**
     * 准备上传
     */
    private class PrepareImportDataTask extends AsyncTask<File, Integer, XLSUtil.XLSFileInfo> {

        private File mXLSFile;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ReportDataActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("正在加载文件...");
            mProgressDialog.show();
        }

        @Override
        protected XLSUtil.XLSFileInfo doInBackground(File... params) {
            mXLSFile = params[0];
            return XLSUtil.checkXLS(mXLSFile);
        }

        @Override
        protected void onPostExecute(XLSUtil.XLSFileInfo result) {
            mProgressDialog.dismiss();

            if (result.isXLS) {
                if (result.tableIndex == XLSUtil.FANGWU_TABLE_INDEX
                        || result.tableIndex == XLSUtil.RENYUAN_TABLE_INDEX) {
                    if (result.recordCount > 0)
                        new ImportDataTask(result.tableIndex, result.recordCount).execute(mXLSFile);
                    else
                        CommonUtils.createErrorAlertDialog(ReportDataActivity.this, "无纪录！").show();
                } else {
                    CommonUtils.createErrorAlertDialog(ReportDataActivity.this, "不是房屋信息或人员信息XLS文件！").show();
                }
            } else {
                CommonUtils.createErrorAlertDialog(ReportDataActivity.this, "文件格式不正确！").show();
            }
        }
    }

    /**
     * 导入数据成XLS文件格式
     */
    private class ImportDataTask extends AsyncTask<File, Integer, Boolean> {

        private int mTableIndex = 0;
        private String mTableName = "";
        private int mRecordCount = 0;

        public ImportDataTask(int tableIndex, int recordCount) {
            mTableIndex = tableIndex;
            if (mTableIndex == 0)
                mTableName = "房屋信息表";
            else if (mTableIndex == 1)
                mTableName = "来京人员信息";

            mRecordCount = recordCount;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ReportDataActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(mRecordCount);
            mProgressDialog.setMessage(String.format("正在导入数据到%s...", mTableName));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mProgressDialog.dismiss();

            onResume();
            CommonUtils.createErrorAlertDialog(ReportDataActivity.this, "成功导入完成").show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected Boolean doInBackground(File... params) {
            boolean success = XLSUtil.importXLS(ReportDataActivity.this, params[0], mTableIndex, new ProgressUpdateListener() {
                @Override
                public void progressChanged(Integer progress) {
                    publishProgress(progress);
                }
            });

            if (success)
                new MyContentProvider().addSystemLog(mOwnerData, "信息导入导出", mTableName, "", "导入" + mTableName, "民警");

            return success;
        }
    }

}
