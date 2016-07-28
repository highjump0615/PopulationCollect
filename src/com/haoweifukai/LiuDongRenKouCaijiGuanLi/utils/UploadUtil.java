package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.PermanentActivity;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.SettingsActivity;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CheLiangColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class UploadUtil {

    private static final String TAG = UploadUtil.class.getSimpleName();

    public static final String UPLOAD_DATA_API = "mobile_import.jsp";
    public static final String UPLOAD_IMAGE_API = "mobile_import_image.jsp";

    public static final String UPLOAD_ROOM_DATA_ACTION = "录入房屋登记";
    public static final String UPLOAD_PERSON_DATA_ACTION = "录入来京人员登记";
    public static final String UPLOAD_VEHICLE_DATA_ACTION = "车辆信息登记";

    public static final String UPLOAD_ROOM_IMAGE_ACTION = "房屋照片";
    public static final String UPLOAD_ROOM_VIDEO_ACTION = "房屋摄像";
    public static final String UPLOAD_PERSON_IMAGE_ACTION = "人员照片";
    public static final String UPLOAD_VEHICLE_IMAGE_ACTION = "车辆照片";

    private static HashMap<String, LinkedHashMap<String, Boolean>> mUploadMap = new HashMap<String, LinkedHashMap<String, Boolean>>();

    public static boolean uploadNewData(Activity activity, String contentType, Cursor cursor, boolean showProgress) {
        LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>();
        linkedHashMap.put(cursor.getString(0), false);

        mUploadMap = new HashMap<String, LinkedHashMap<String, Boolean>>();
        mUploadMap.put(contentType, linkedHashMap);

        new UploadDataTask(activity, 1, showProgress).execute();

        return false;
    }

    /**
     * 是否上传至服务器标志设置为是
     */
    private static void setUploadFlag(Context context) {
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
            context.getContentResolver().applyBatch(MyContentProvider.AUTHORITY, operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备上传
     */
    private static class PrepareExportDataTask extends AsyncTask<Void, Integer, Integer> {

        private boolean mIsFileExport;// true: Export, false: Upload
        private String mExportPath;

        private String mStartDate;
        private String mEndDate;

        private Activity mActivity;
        private boolean mShowProgress;
        private ProgressDialog mProgressDialog;

        public PrepareExportDataTask(Activity activity, boolean isExport, String path,
                                     String startDate, String endDate, boolean showProgress) {
            mActivity = activity;
            mIsFileExport = isExport;
            mExportPath = path;

            mStartDate = startDate;
            mEndDate = endDate;
            mShowProgress = showProgress;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("正在加载数据...");
            mProgressDialog.show();

            mUploadMap.clear();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            MyContentProvider contentProvider = new MyContentProvider();
            int count = 0;

            String where = ShangChuanColumns.SFSCZFWQ + "=0 AND " + ShangChuanColumns.SFSC + "=0";
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
                CommonUtils.createErrorAlertDialog(mActivity, message).show();
                return;
            }

            new UploadDataTask(mActivity, count, mShowProgress).execute();
        }

    }

    /**
     * 上传数据到服务器
     */
    public static class UploadDataTask extends AsyncTask<Void, Integer, Boolean> {

        private Activity mActivity;
        private int mCount;
        private boolean mShowProgress;
        private ProgressDialog mProgressDialog;

        public UploadDataTask(Activity activity, int count, boolean showProgress) {
            mActivity = activity;
            mCount = count;
            mShowProgress = showProgress;
        }

        @Override
        protected void onPreExecute() {
            if (mShowProgress) {
                mProgressDialog = new ProgressDialog(mActivity);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMax(mCount);
                mProgressDialog.setMessage("正在上传中...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgress(0);
                mProgressDialog.show();
            } else {
                mProgressDialog = null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mShowProgress)
                mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) setUploadFlag(mActivity);

            if (mShowProgress) {
                mProgressDialog.dismiss();

                if (success) {
                    CommonUtils.createErrorAlertDialog(mActivity, "成功上传完成").show();
                } else {
                    CommonUtils.createErrorAlertDialog(mActivity, "上传失败").show();
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MyContentProvider contentProvider = new MyContentProvider();
            int uploadIndex = 0;
            boolean uploadSuccess = false;

            // 房屋登记
            LinkedHashMap<String, Boolean> linkedHashMap = mUploadMap.get(FangWuColumns.CONTENT_TYPE);
            if (linkedHashMap != null && linkedHashMap.size() > 0) {
                String fixedLine;
                String csvString;
                String containString = "";
                uploadSuccess = false;

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
                                paramMap.put("action", UPLOAD_ROOM_DATA_ACTION);
                                paramMap.put("actionid", ((PermanentActivity) mActivity).mOwnerData.MJID);
                                paramMap.put("data", csvString);
                                paramMap.put("content_uri", FangWuColumns.CONTENT_TYPE);

                                if (runApi(mActivity, UPLOAD_DATA_API, paramMap)) {
                                    uploadSuccess = true;
                                    uploadImageFile(mActivity, UPLOAD_ROOM_IMAGE_ACTION, cursor, true);
                                    linkedHashMap.put(cursor.getString(cursor.getColumnIndex(FangWuColumns.FWID)), true);
                                }
                            }

                            publishProgress(++uploadIndex);
                        } while (cursor.moveToNext());
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
                uploadSuccess = false;

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
                                paramMap.put("action", UPLOAD_PERSON_DATA_ACTION);
                                paramMap.put("actionid", ((PermanentActivity) mActivity).mOwnerData.MJID);
                                paramMap.put("data", csvString);
                                paramMap.put("content_uri", LaiJingRenYuanColumns.CONTENT_TYPE);

                                if (runApi(mActivity, UPLOAD_DATA_API, paramMap)) {
                                    uploadSuccess = true;
                                    uploadImageFile(mActivity, UPLOAD_PERSON_IMAGE_ACTION, cursor, false);
                                    linkedHashMap.put(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.RYID)), true);
                                }
                            }

                            publishProgress(++uploadIndex);
                        } while (cursor.moveToNext());
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
                uploadSuccess = false;

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
                                paramMap.put("action", UPLOAD_VEHICLE_DATA_ACTION);
                                paramMap.put("actionid", ((PermanentActivity) mActivity).mOwnerData.MJID);
                                paramMap.put("data", csvString);
                                paramMap.put("content_uri", CheLiangColumns.CONTENT_TYPE);

                                if (runApi(mActivity, UPLOAD_DATA_API, paramMap)) {
                                    uploadSuccess = true;
                                    uploadImageFile(mActivity, UPLOAD_VEHICLE_IMAGE_ACTION, cursor, false);
                                    linkedHashMap.put(cursor.getString(cursor.getColumnIndex(CheLiangColumns.CLID)), true);
                                }
                            }

                            publishProgress(++uploadIndex);
                        } while (cursor.moveToNext());
                    }

                    cursor.close();
                }
            }

            return uploadSuccess;
        }
    }

    public static boolean uploadImageFile(Activity activity,
                                          String action, Cursor cursor, boolean hasVideo) {
        if (cursor == null) return true;

        String fileName = cursor.getString(cursor.getColumnIndex("ZPURL"));
        if (!TextUtils.isEmpty(fileName)) {
            ArrayList<String> fileNameList = PhotoUtil.getNamesFromString(fileName);

            for (String name : fileNameList) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("actionid", name);

                runApi(activity, UPLOAD_IMAGE_API, params);
            }
        }

        if (hasVideo) {
            fileName = cursor.getString(cursor.getColumnIndex("SXURL"));

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("action", UPLOAD_ROOM_VIDEO_ACTION);
            params.put("actionid", fileName);

            runApi(activity, UPLOAD_IMAGE_API, params);
        }

        return true;
    }

    public static boolean runApi(Activity activity,
                                 String api, HashMap<String, String> params) {
        try {
            String serverAddress = SettingsActivity.loadServerAddress(activity);
            if (TextUtils.isEmpty(serverAddress)) return false;

            String strApi = String.format("%s/%s", serverAddress, api);

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(strApi);
            HttpEntity formEntity = null;

            String data;

            LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>();

            if (api.equals(UPLOAD_DATA_API)) {
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
            } else if (api.equals(UPLOAD_IMAGE_API)) {
                if (params != null) {
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                    multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    String action = params.get("action");
                    String actionId = params.get("actionid");
                    String fileName = CommonUtils.getMyApplicationDirectory(
                            activity, Config.IMAGE_DIRECTORY_NAME) + File.separator + actionId;

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

}
