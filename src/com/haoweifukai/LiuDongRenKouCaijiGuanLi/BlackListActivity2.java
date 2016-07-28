/**
 * @author LuYongXing
 * @date 2014.09.17
 * @filename BlackListActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.BlackListAdapter2;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.BlackListData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.T_HMD_MD5;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.XLSUtil;
import com.ry.fileexplorer.FileChooser;

import java.io.File;
import java.util.ArrayList;

public class BlackListActivity2 extends PermanentActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static String TAG = BlackListActivity2.class.getSimpleName();

    private static final int REQUEST_FILE_PATH = 1111;

    private TextView mTextRecordCount;

    private ImageButton mButtonFirst;
    private ImageButton mButtonLast;
    private ImageButton mButtonPrev;
    private ImageButton mButtonNext;

    private BlackListAdapter2 mAdapter;

    private ArrayList<BlackListData> mDataList = new ArrayList<BlackListData>();

    private int mRecordCount = 0;
    private int mPageIndex = 0;
    private int mPageCount = 0;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        if (requestCode == REQUEST_FILE_PATH) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra(FileChooser.FILE_PATH_PREF);
                String fileName = data.getStringExtra(FileChooser.FILE_NAME_PREF);

                File xlsFile = new File(path, fileName);
                new PrepareImportDataTask().execute(xlsFile);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_import:
                startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
                break;

            case R.id.button_search:
                mPageIndex = 0;
                loadData();
                new MyContentProvider().addSystemLog(mOwnerData, "信息检索", "黑名单信息", "", "检索黑名单", "民警");
                break;

            case R.id.button_first:
                mPageIndex = 0;
                loadData();
                break;

            case R.id.button_previous:
                mPageIndex--;
                loadData();
                break;

            case R.id.button_next:
                mPageIndex++;
                loadData();
                break;

            case R.id.button_last:
                mPageIndex = mPageCount - 1;
                loadData();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*BlackListData data = mDataList.get(position);

        if (!TextUtils.isEmpty(data.id)) {
            Intent intent = new Intent(this, FloatingPersonActivity.class);
            intent.putExtra(FloatingPersonActivity.SELECTED_PERSON_ID, data.id);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            new MyContentProvider().addSystemLog(mOwnerData, "信息检索", "黑名单信息", String.valueOf(data.id), "检索黑名单", "民警");
        }*/
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_TianJia).setVisible(false);
        menu.findItem(R.id.action_BiDuiYuJing).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void initViews() {
        super.initViews();

        View view = mLayoutInflater.inflate(R.layout.activity_black_list, mLayoutContainer);

        // Initialize ListView and Adapter
        mAdapter = new BlackListAdapter2(this, mDataList);
        ListView listView = (ListView) view.findViewById(R.id.list_blacklist);
        listView.setAdapter(mAdapter);
        //listView.setOnItemClickListener(this);

        // Button
        view.findViewById(R.id.button_import).setOnClickListener(this);

        // Navigation buttons
        mButtonFirst = (ImageButton) view.findViewById(R.id.button_first);
        mButtonFirst.setOnClickListener(this);
        mButtonPrev = (ImageButton) view.findViewById(R.id.button_previous);
        mButtonPrev.setOnClickListener(this);
        mButtonNext = (ImageButton) view.findViewById(R.id.button_next);
        mButtonNext.setOnClickListener(this);
        mButtonLast = (ImageButton) view.findViewById(R.id.button_last);
        mButtonLast.setOnClickListener(this);

        // TextView for Record count
        mTextRecordCount = (TextView) view.findViewById(R.id.text_total_records);
    }

    /**
     * Load FloatingPersonData
     */
    private void loadData() {
        mDataList.clear();

        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(T_HMD_MD5.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            mRecordCount = cursor.getCount();

            if (mRecordCount > 0 && cursor.moveToPosition(mPageIndex * Config.ONCE_LOAD_RECORD_COUNT)) {
                int i = 0;

                do {
                    BlackListData data = new BlackListData();
                    data.no = mPageIndex * Config.ONCE_LOAD_RECORD_COUNT + i + 1;
                    data.id = cursor.getString(cursor.getColumnIndex(T_HMD_MD5.MD5));
                    data.type = cursor.getString(cursor.getColumnIndex(T_HMD_MD5.TYPE));

                    mDataList.add(data);
                    i++;
                } while (cursor.moveToNext() && i < Config.ONCE_LOAD_RECORD_COUNT);
            }

            cursor.close();
        } else {
            mRecordCount = 0;
        }

        mAdapter.notifyDataSetChanged();
        refreshButtons();

        mTextRecordCount.setText(getString(R.string.total_record, mRecordCount));
    }

    /**
     * Refresh status of navigation buttons
     */
    private void refreshButtons() {
        mPageCount = (mRecordCount + Config.ONCE_LOAD_RECORD_COUNT - 1) / Config.ONCE_LOAD_RECORD_COUNT;

        if (mPageIndex == 0) {
            mButtonFirst.setEnabled(false);
            mButtonPrev.setEnabled(false);
        }

        if (mPageIndex > 0) {
            mButtonFirst.setEnabled(true);
            mButtonPrev.setEnabled(true);
        }

        if (mPageIndex >= mPageCount - 1) {
            mButtonNext.setEnabled(false);
            mButtonLast.setEnabled(false);
        }

        if (mPageIndex < mPageCount - 1) {
            mButtonNext.setEnabled(true);
            mButtonLast.setEnabled(true);
        }
    }

    /**
     * 准备上传
     */
    private class PrepareImportDataTask extends AsyncTask<File, Integer, XLSUtil.XLSFileInfo> {

        private File mXLSFile;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(BlackListActivity2.this);
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
                if (result.tableIndex == XLSUtil.HEIMINGDAN_MD5_TABLE_INDEX) {
                    if (result.recordCount > 0)
                        new ImportDataTask(result.tableIndex, result.recordCount).execute(mXLSFile);
                    else
                        CommonUtils.createErrorAlertDialog(BlackListActivity2.this, "无纪录！").show();
                } else {
                    CommonUtils.createErrorAlertDialog(BlackListActivity2.this, "不是黑名单XLS文件！").show();
                }
            } else {
                CommonUtils.createErrorAlertDialog(BlackListActivity2.this, "文件格式不正确！").show();
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
            mProgressDialog = new ProgressDialog(BlackListActivity2.this);
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
            CommonUtils.createErrorAlertDialog(BlackListActivity2.this, "成功导入完成").show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected Boolean doInBackground(File... params) {
            boolean success = XLSUtil.importXLS(BlackListActivity2.this, params[0], mTableIndex, new ProgressUpdateListener() {
                @Override
                public void progressChanged(Integer progress) {
                    publishProgress(progress);
                }
            });

            if (success)
                new MyContentProvider().addSystemLog(mOwnerData, "信息导入导出", "黑名单信息", "", "导入黑名单", "民警");

            return success;
        }
    }

}
