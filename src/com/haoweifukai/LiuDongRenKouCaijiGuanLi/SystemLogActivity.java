/**
 * @author LuYongXing
 * @date 2014.09.02
 * @filename SystemLogActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.LogAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.LogData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CaoZuoRiZhiColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;

import java.util.ArrayList;

public class SystemLogActivity extends PermanentActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    // Constants
    private static final int ONCE_LOAD_RECORD_COUNT = 10;

    private TextView mTextRecordCount;

    private ImageButton mButtonFirst;
    private ImageButton mButtonLast;
    private ImageButton mButtonPrev;
    private ImageButton mButtonNext;

    private LogAdapter mAdapter;

    private ArrayList<LogData> mDataList = new ArrayList<LogData>();

    private int mRecordCount = 0;
    private int mPageIndex = 0;
    private int mPageCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        new MyContentProvider().addSystemLog(mOwnerData, "日志管理", "日志信息", "", "检查日志信息", "民警");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
        LogData data = mDataList.get(position);

        Intent intent = new Intent(this, SystemLogViewActivity.class);
        intent.putExtra(SystemLogViewActivity.SELECTED_LOG_ID, data.id);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

        new MyContentProvider().addSystemLog(mOwnerData, "日志管理", "日志信息", String.valueOf(data.id), "检查日志信息", "民警");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_TianJia).setVisible(false);
        menu.findItem(R.id.action_XiTongRiZhi).setVisible(false);

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

        View view = mLayoutInflater.inflate(R.layout.activity_system_log_list, mLayoutContainer);

        // Initialize ListView and Adapter
        mAdapter = new LogAdapter(this, mDataList);
        ListView listView = (ListView) view.findViewById(R.id.list_rent_room);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

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
     * Load FangWuData
     */
    private void loadData() {
        mDataList.clear();

        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(CaoZuoRiZhiColumns.CONTENT_URI,
                new String[]{CaoZuoRiZhiColumns.CZXXID, CaoZuoRiZhiColumns.CZSJ, CaoZuoRiZhiColumns.CZMS},
                null, null, CaoZuoRiZhiColumns.SORT_ORDER_DEFAULT);
        if (cursor != null) {
            mRecordCount = cursor.getCount();

            if (mRecordCount > 0 && cursor.moveToPosition(mPageIndex * ONCE_LOAD_RECORD_COUNT)) {
                int i = 0;

                do {
                    LogData data = new LogData();
                    data.no = mRecordCount - mPageIndex * ONCE_LOAD_RECORD_COUNT - i;
                    data.id = cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZXXID));
                    data.time = cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZSJ));
                    data.operation = cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZMS));

                    mDataList.add(data);
                    i++;
                } while (cursor.moveToNext() && i < ONCE_LOAD_RECORD_COUNT);
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
        mPageCount = (mRecordCount + ONCE_LOAD_RECORD_COUNT - 1) / ONCE_LOAD_RECORD_COUNT;

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

}
