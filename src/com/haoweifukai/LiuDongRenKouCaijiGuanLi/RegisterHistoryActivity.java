/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename FloatingPersonListActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.RegisterHistoryAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.RegisterHistoryData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RegisterHistoryActivity extends PermanentActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = RegisterHistoryActivity.class.getSimpleName();

    // Constants
    public static final String ID_CARD_NUMBER = "id_card_number";

    //
    public static final String TRACK_HISTORY_API_URL = "mobile_service.jsp";
    // 来京人员轨迹检索
    public static final String SEARCH_HISTORY_REQUEST_ACTION = "J_LJRYGJJS";
    // 来京人员历史轨迹
    public static final String SEARCH_HISTORY_RESPONSE_ACTION = "J_LJRYLSGJ";
    // 来京人员轨迹查看
    public static final String VIEW_HISTORY_REQUEST_ACTION = "J_LJRYGJCK";
    // 来京人员轨迹信息
    public static final String VIEW_HISTORY_RESPONSE_ACTION = "J_LJRYGJXX";

    // Variables
    private TextView mTextIDCardNumber;

    private RegisterHistoryAdapter mAdapter;

    private ArrayList<RegisterHistoryData> mDataList = new ArrayList<RegisterHistoryData>();

    private String mIDCardNumber = "";

    private String mServerAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(ID_CARD_NUMBER)) {
            mIDCardNumber = intent.getStringExtra(ID_CARD_NUMBER);
        }

        super.onCreate(savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mServerAddress = SettingsActivity.loadServerAddress(this);
        if (TextUtils.isEmpty(mServerAddress)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name)
                    .setMessage("要使用无线功能，您必须设置服务器地址。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(RegisterHistoryActivity.this, SettingsActivity.class));
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
            new SearchRegisterHistoryTask().execute();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RegisterHistoryData data = mDataList.get(position);

        new GetRegisterInfoTask().execute(data.id);
        new MyContentProvider().addSystemLog(mOwnerData, "无线接口", "来京人员信息", data.id, "无线轨迹查看", "民警");
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

        View view = mLayoutInflater.inflate(R.layout.activity_register_history_list, mLayoutContainer);

        // TextView for ID Card Number
        mTextIDCardNumber = (TextView) view.findViewById(R.id.text_id_card_number);

        // Initialize ListView and Adapter
        mAdapter = new RegisterHistoryAdapter(this, mDataList);
        ListView listView = (ListView) view.findViewById(R.id.list_register_history);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    /**
     * 检索来京人员轨迹
     */
    private class SearchRegisterHistoryTask extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog mProgressDialog;

        private String mErrorMessage;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(RegisterHistoryActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("正在检索中...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Request
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", SEARCH_HISTORY_REQUEST_ACTION);

                jsonObject.put("actionid", mOwnerData.MJID);

                JSONObject dataObject = new JSONObject();
                dataObject.put(LaiJingRenYuanColumns.SFZHM, mIDCardNumber);

                jsonObject.put("data", dataObject);
                String response = runApi(TRACK_HISTORY_API_URL, jsonObject.toString());

                // Parse result
                return loadData(response);
            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mProgressDialog.dismiss();

            mAdapter.notifyDataSetChanged();
            mTextIDCardNumber.setText(mIDCardNumber);

            if (!success) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterHistoryActivity.this);
                builder.setTitle(R.string.app_name)
                        .setMessage(mErrorMessage)
                        .setCancelable(true)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
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
                        })
                        .create().show();
            }
        }

        /**
         * Load Register history
         */
        private boolean loadData(String response) {
            if (TextUtils.isEmpty(response)) return false;

            mDataList.clear();

            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean success = jsonObject.getBoolean("success");
                String action = jsonObject.getString("action");
                String error = jsonObject.getString("error");

                // Check if it is success and response action
                if (!success || !SEARCH_HISTORY_RESPONSE_ACTION.equals(action)) {
                    mErrorMessage = "查看轨迹失败";

                    if ("empty data".equals(error)) {
                        mErrorMessage = "无轨迹结果";
                    }

                    return false;
                }

                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject == null) {
                    mErrorMessage = "无轨迹结果";
                    return false;
                }

                // Check if id card is matching.
                String id = dataObject.getString(LaiJingRenYuanColumns.SFZHM);
                if (!mIDCardNumber.equals(id)) {
                    mErrorMessage = "无轨迹结果";
                    return false;
                }

                JSONArray resultArray = dataObject.getJSONArray("result");
                if (resultArray == null) {
                    mErrorMessage = "无轨迹结果";
                    return false;
                }

                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject registerObject = (JSONObject) resultArray.get(i);

                    RegisterHistoryData data = new RegisterHistoryData();
                    data.no = i + 1;
                    data.id = registerObject.getString(LaiJingRenYuanColumns.RYID);
                    data.name = registerObject.getString(LaiJingRenYuanColumns.XM);
                    data.address = registerObject.getString(LaiJingRenYuanColumns.XZDXZZT);
                    data.origin = registerObject.getString(LaiJingRenYuanColumns.LRSB);
                    data.time = registerObject.getString(LaiJingRenYuanColumns.LRSJ);

                    if ("1".equals(data.origin)) {
                        data.origin = "手持机";
                    } else if ("0".equals(data.origin)) {
                        data.origin = "公寓系统";
                    } else if ("2".equals(data.origin)) {
                        data.origin = "流管办系统";
                    }

                    // Adjust Date format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                    try {
                        data.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateFormat.parse(data.time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    mDataList.add(data);
                }

            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
                mErrorMessage = "查看轨迹失败";
                return false;
            }

            return true;
        }
    }

    /**
     * 获得来京人员轨迹信息
     */
    private class GetRegisterInfoTask extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog mProgressDialog;
        private String mPersonID;
        private String mErrorMessage;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(RegisterHistoryActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("正在获得中...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            mPersonID = params[0];

            try {
                // Make and send request
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", VIEW_HISTORY_REQUEST_ACTION);

                jsonObject.put("actionid", mOwnerData.MJID);

                JSONObject dataObject = new JSONObject();
                dataObject.put(LaiJingRenYuanColumns.RYID, mPersonID);

                jsonObject.put("data", dataObject);
                String response = runApi(TRACK_HISTORY_API_URL, jsonObject.toString());

                // Parse result
                return loadData(response);
            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mProgressDialog.dismiss();

            if (!success) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterHistoryActivity.this);
                builder.setTitle(R.string.app_name)
                        .setMessage(mErrorMessage)
                        .setCancelable(true)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
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
                        })
                        .create().show();
            }
        }

        /**
         * Load Register information
         */
        private boolean loadData(String response) {
            if (TextUtils.isEmpty(response)) return false;

            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean success = jsonObject.getBoolean("success");
                String action = jsonObject.getString("action");

                // Check if it is success and response action
                if (!success || !VIEW_HISTORY_RESPONSE_ACTION.equals(action)) {
                    mErrorMessage = "查看轨迹失败";
                    return false;
                }

                final JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject == null) {
                    mErrorMessage = "无轨迹结果";
                    return false;
                }

                // Check if ID is matching
                String id = dataObject.getString(LaiJingRenYuanColumns.RYID);
                if (!mPersonID.equals(id)) {
                    mErrorMessage = "轨迹ID不对";
                    return false;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(RegisterHistoryActivity.this, PersonViewActivity.class);
                        intent.putExtra(PersonViewActivity.JSON_STRING, dataObject.toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public String runApi(String type, String json) {
        String strApi = String.format("%s/%s", mServerAddress, type);

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(strApi);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("request_param", json));

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);
            formEntity.setContentEncoding("UTF-8");
            httppost.setEntity(formEntity);

            HttpResponse response = httpclient.execute(httppost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                StringBuilder builder = new StringBuilder();
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                String result = builder.toString();
                Log.d(TAG, "result = " + result);

                return result;
            } else {
                Log.e(TAG, "status code=" + response.getStatusLine().getStatusCode());
                return null;
            }
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return null;
    }

}
