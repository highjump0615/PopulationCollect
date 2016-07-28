/**
 * @author LuYongXing
 * @date 2014.09.03
 * @filename SystemLogViewActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.AlertStatisticsAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.DistrictStatisticsAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.DomicileStatisticsAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.QualityStatisticsAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.AlertStatisticsData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.DistrictStatisticsData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.DomicileStatisticsData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.QualityStatisticsData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CheLiangColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.YuJingColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatisticsActivity extends PermanentActivity implements View.OnClickListener {

    private static final String TAG = StatisticsActivity.class.getSimpleName();

    private String[] UNNEEDED_FIELDS = {
            LaiJingRenYuanColumns.LXDH, LaiJingRenYuanColumns.CSD, LaiJingRenYuanColumns.LKYJRQ,
            LaiJingRenYuanColumns.BZ, LaiJingRenYuanColumns.DWDJBXH, LaiJingRenYuanColumns.JYDWXXDZ,
            LaiJingRenYuanColumns.XXSZD, LaiJingRenYuanColumns.ZPURL
    };

    private EditText mEditStartDate;
    private EditText mEditEndDate;

    private View mLayoutXiaQu;
    private View mLayoutYuJing;
    private View mLayoutZhiLiang;
    private View mLayoutHuJiDi;
    private View mLayoutCheLiang;

    private DistrictStatisticsAdapter mXiaQuAdapter;
    private AlertStatisticsAdapter mYuJingAdapter;
    private QualityStatisticsAdapter mZhiLiangAdapter;
    private DomicileStatisticsAdapter mHuJiDiAdapter;
    private DistrictStatisticsAdapter mCheLiangAdapter;

    private ArrayList<DistrictStatisticsData> mXiaQuDataList = new ArrayList<DistrictStatisticsData>();
    private ArrayList<AlertStatisticsData> mYuJingDataList = new ArrayList<AlertStatisticsData>();
    private ArrayList<QualityStatisticsData> mZhiLiangDataList = new ArrayList<QualityStatisticsData>();
    private ArrayList<DomicileStatisticsData> mHuJiDiDataList = new ArrayList<DomicileStatisticsData>();
    private ArrayList<DistrictStatisticsData> mCheLiangDataList = new ArrayList<DistrictStatisticsData>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_xiaqu:
                onCalculateXiaQu();
                new MyContentProvider().addSystemLog(mOwnerData, "信息统计", "来京人员信息", "", "按社区进行信息统计", "民警");
                break;

            case R.id.button_yujing:
                onCalculateYuJing();
                new MyContentProvider().addSystemLog(mOwnerData, "信息统计", "来京人员信息", "", "预警信息统计", "民警");
                break;

            case R.id.button_zhiliang:
                onCalculateZhiLiang();
                new MyContentProvider().addSystemLog(mOwnerData, "信息统计", "来京人员信息", "", "按质量进行信息统计", "民警");
                break;

            case R.id.button_hujidi:
                onCalculateHuJiDi();
                new MyContentProvider().addSystemLog(mOwnerData, "信息统计", "来京人员信息", "", "按户籍地进行信息统计", "民警");
                break;

            case R.id.button_cheliang:
                onCalculateCheLiang();
                new MyContentProvider().addSystemLog(mOwnerData, "信息统计", "车辆信息", "", "按所在街道进行信息统计", "民警");
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_TianJia).setVisible(false);
        menu.findItem(R.id.action_CaiJiTongJi).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void initViews() {
        super.initViews();

        View view = mLayoutInflater.inflate(R.layout.activity_statistics, mLayoutContainer);

        // Initialize ListView and Adapter
        mXiaQuAdapter = new DistrictStatisticsAdapter(this, mXiaQuDataList);
        ListView listView = (ListView) view.findViewById(R.id.list_xiaqu);
        listView.setAdapter(mXiaQuAdapter);

        mYuJingAdapter = new AlertStatisticsAdapter(this, mYuJingDataList);
        listView = (ListView) view.findViewById(R.id.list_yujing);
        listView.setAdapter(mYuJingAdapter);

        mZhiLiangAdapter = new QualityStatisticsAdapter(this, mZhiLiangDataList);
        listView = (ListView) view.findViewById(R.id.list_zhiliang);
        listView.setAdapter(mZhiLiangAdapter);

        mHuJiDiAdapter = new DomicileStatisticsAdapter(this, mHuJiDiDataList);
        listView = (ListView) view.findViewById(R.id.list_hujidi);
        listView.setAdapter(mHuJiDiAdapter);

        mCheLiangAdapter = new DistrictStatisticsAdapter(this, mCheLiangDataList);
        listView = (ListView) view.findViewById(R.id.list_cheliang);
        listView.setAdapter(mCheLiangAdapter);

        // EditText
        mEditStartDate = (EditText) view.findViewById(R.id.edit_start_date);
        mEditStartDate.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(this, mEditStartDate));
        mEditEndDate = (EditText) view.findViewById(R.id.edit_end_date);
        mEditEndDate.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(this, mEditEndDate));

        // Buttons
        findViewById(R.id.button_xiaqu).setOnClickListener(this);
        findViewById(R.id.button_yujing).setOnClickListener(this);
        findViewById(R.id.button_zhiliang).setOnClickListener(this);
        findViewById(R.id.button_hujidi).setOnClickListener(this);
        findViewById(R.id.button_cheliang).setOnClickListener(this);

        // Containers
        mLayoutXiaQu = findViewById(R.id.layout_xiaqu_tongji);
        mLayoutYuJing = findViewById(R.id.layout_yujing_tongji);
        mLayoutZhiLiang = findViewById(R.id.layout_zhiliang_tongji);
        mLayoutHuJiDi = findViewById(R.id.layout_hujidi_tongji);
        mLayoutCheLiang = findViewById(R.id.layout_cheliang_tongji);

        mLayoutXiaQu.setVisibility(View.GONE);
        mLayoutYuJing.setVisibility(View.GONE);
        mLayoutZhiLiang.setVisibility(View.GONE);
        mLayoutHuJiDi.setVisibility(View.GONE);
        mLayoutCheLiang.setVisibility(View.GONE);
    }

    /**
     * 按辖区采集统计
     */
    private void onCalculateXiaQu() {
        mLayoutXiaQu.setVisibility(View.VISIBLE);
        mLayoutYuJing.setVisibility(View.GONE);
        mLayoutZhiLiang.setVisibility(View.GONE);
        mLayoutHuJiDi.setVisibility(View.GONE);
        mLayoutCheLiang.setVisibility(View.GONE);

        mXiaQuDataList.clear();

        MyContentProvider contentProvider = new MyContentProvider();
        String whereString = getWhereStringWithDate();

        if (!TextUtils.isEmpty(whereString)) {
            whereString += (" AND " + LaiJingRenYuanColumns.SFSC + "=0");
        } else {
            whereString = LaiJingRenYuanColumns.SFSC + "=0";
        }

        Cursor cursor = contentProvider.runRawQuery("SELECT " + LaiJingRenYuanColumns.SZDXQ + ", COUNT(*) AS CNT"
                + " FROM " + CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI)
                + " WHERE (" + whereString + ")"
                + " GROUP BY " + LaiJingRenYuanColumns.SZDXQ + ";", null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    DistrictStatisticsData data = new DistrictStatisticsData();
                    data.name = cursor.getString(0);
                    data.count = cursor.getInt(1);
                    data.policeName = mOwnerData.MJXM;

                    mXiaQuDataList.add(data);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        mXiaQuAdapter.notifyDataSetChanged();
    }

    /**
     * 按预警统计统计
     */
    private void onCalculateYuJing() {
        mYuJingDataList.clear();
        mLayoutXiaQu.setVisibility(View.GONE);
        mLayoutYuJing.setVisibility(View.VISIBLE);
        mLayoutZhiLiang.setVisibility(View.GONE);
        mLayoutHuJiDi.setVisibility(View.GONE);
        mLayoutCheLiang.setVisibility(View.GONE);

        MyContentProvider contentProvider = new MyContentProvider();

        String startDate = mEditStartDate.getText().toString();
        String endDate = mEditEndDate.getText().toString();

        try {
            Date sDate = null;
            Date eDate;

            if (!TextUtils.isEmpty(startDate)) {
                sDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.CHINA).parse(CommonUtils.convertChinaFormatTime(startDate));
            } else {
                Cursor cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI,
                        new String[]{LaiJingRenYuanColumns.LRSJ}, LaiJingRenYuanColumns.SFSC + "=0", null,
                        LaiJingRenYuanColumns.LRSJ + " ASC");
                if (cursor != null) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        Log.d(TAG, "LRSJ = " + cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LRSJ)));

                        startDate = CommonUtils.convertDateFormat("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",
                                cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LRSJ)));
                        sDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.CHINA).parse(CommonUtils.convertChinaFormatTime(startDate));
                    }

                    cursor.close();
                }

                if (sDate == null) {
                    //CommonUtils.createErrorAlertDialog(this, "请选择采集时间！").show();
                    return;
                }
            }

            if (!TextUtils.isEmpty(endDate))
                eDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.CHINA).parse(CommonUtils.convertChinaFormatTime(endDate));
            else
                eDate = new Date();

            Calendar calendar = Calendar.getInstance();

            while (sDate.compareTo(eDate) <= 0) {
                String dateString = CommonUtils.getFormattedDateString(sDate, "yyyy-MM-dd");

                long count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI),
                        "strftime('%Y-%m-%d', " + LaiJingRenYuanColumns.LRSJ + ")='" + dateString + "' AND "
                                + LaiJingRenYuanColumns.SFSC + "=0");

                if (count != 0) {
                    long alertCount = contentProvider.fetchCount(
                            CommonUtils.getLastPathFromUri(YuJingColumns.CONTENT_URI),
                            "strftime('%Y-%m-%d', " + YuJingColumns.BDSJ + ")='" + dateString + "'");

                    AlertStatisticsData data = new AlertStatisticsData();
                    data.date = dateString;
                    data.collectionCount = (int) count;
                    data.alertCount = (int) alertCount;

                    mYuJingDataList.add(data);
                }

                calendar.setTime(sDate);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                sDate = calendar.getTime();
            }

            mYuJingAdapter.notifyDataSetChanged();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按质量统计统计
     */
    private void onCalculateZhiLiang() {
        mZhiLiangDataList.clear();
        mLayoutXiaQu.setVisibility(View.GONE);
        mLayoutYuJing.setVisibility(View.GONE);
        mLayoutZhiLiang.setVisibility(View.VISIBLE);
        mLayoutHuJiDi.setVisibility(View.GONE);
        mLayoutCheLiang.setVisibility(View.GONE);

        MyContentProvider contentProvider = new MyContentProvider();

        /*String whereString = getWhereStringWithDate();

        if (!TextUtils.isEmpty(whereString)) {
            whereString += (" AND " + LaiJingRenYuanColumns.SFSC + "=0");
        } else {
            whereString = LaiJingRenYuanColumns.SFSC + "=0";
        }

        mZhiLiangDataList.clear();

        Cursor cursor = contentProvider.runRawQuery("SELECT strftime('%Y-%m-%d', " + LaiJingRenYuanColumns.LRSJ + "), COUNT(*) AS CNT FROM "
                + CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI)
                + " GROUP BY " + LaiJingRenYuanColumns.LRSJ
                + " HAVING (" + whereString + ");", null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {
                    String dateString = cursor.getString(0);
                    int count = cursor.getInt(1);

                    if (count > 0) {
                        long filledCount = contentProvider.fetchInputtedValueCountInUnNeeded(
                                CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI),
                                UNNEEDED_FIELDS, "strftime('%Y-%m-%d', " + LaiJingRenYuanColumns.LRSJ + ")='" + dateString + "' AND "
                                        + LaiJingRenYuanColumns.SFSC + "=0");

                        QualityStatisticsData data = new QualityStatisticsData();
                        data.time = dateString;
                        data.count = count;
                        data.filledFieldsCount = (int) filledCount;
                        data.totalFieldsCount = UNNEEDED_FIELDS.length * count;

                        mZhiLiangDataList.add(data);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        mZhiLiangAdapter.notifyDataSetChanged();*/


        String startDate = mEditStartDate.getText().toString();
        String endDate = mEditEndDate.getText().toString();

        try {
            Date sDate = null;
            Date eDate;

            if (!TextUtils.isEmpty(startDate)) {
                sDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.CHINA).parse(CommonUtils.convertChinaFormatTime(startDate));
            } else {
                Cursor cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI,
                        new String[]{LaiJingRenYuanColumns.LRSJ}, LaiJingRenYuanColumns.SFSC + "=0", null,
                        LaiJingRenYuanColumns.LRSJ + " ASC");
                if (cursor != null) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        startDate = CommonUtils.convertDateFormat("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",
                                cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.LRSJ)));
                        sDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.CHINA).parse(CommonUtils.convertChinaFormatTime(startDate));
                    }

                    cursor.close();
                }

                if (sDate == null) {
                    //CommonUtils.createErrorAlertDialog(this, "请选择采集时间！").show();
                    return;
                }
            }

            if (!TextUtils.isEmpty(endDate))
                eDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.CHINA).parse(CommonUtils.convertChinaFormatTime(endDate));
            else
                eDate = new Date();

            Calendar calendar = Calendar.getInstance();

            while (sDate.compareTo(eDate) <= 0) {
                String dateString = CommonUtils.getFormattedDateString(sDate, "yyyy-MM-dd");

                long count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI),
                        "strftime('%Y-%m-%d', " + LaiJingRenYuanColumns.LRSJ + ")='" + dateString + "' AND "
                                + LaiJingRenYuanColumns.SFSC + "=0");

                if (count != 0) {
                    long filledCount = contentProvider.fetchInputtedValueCountInUnNeeded(
                            CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI),
                            UNNEEDED_FIELDS, "strftime('%Y-%m-%d', " + LaiJingRenYuanColumns.LRSJ + ")='" + dateString + "' AND "
                                    + LaiJingRenYuanColumns.SFSC + "=0");

                    QualityStatisticsData data = new QualityStatisticsData();
                    data.time = dateString;
                    data.count = (int) count;
                    data.filledFieldsCount = (int) filledCount;
                    data.totalFieldsCount = (int) (UNNEEDED_FIELDS.length * count);

                    mZhiLiangDataList.add(data);
                }

                calendar.setTime(sDate);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                sDate = calendar.getTime();
            }

            mZhiLiangAdapter.notifyDataSetChanged();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按户籍地统计统计
     */
    private void onCalculateHuJiDi() {
        mLayoutXiaQu.setVisibility(View.GONE);
        mLayoutYuJing.setVisibility(View.GONE);
        mLayoutZhiLiang.setVisibility(View.GONE);
        mLayoutHuJiDi.setVisibility(View.VISIBLE);
        mLayoutCheLiang.setVisibility(View.GONE);

        MyContentProvider contentProvider = new MyContentProvider();

        String whereString = getWhereStringWithDate();

        if (!TextUtils.isEmpty(whereString)) {
            whereString += (" AND " + LaiJingRenYuanColumns.SFSC + "=0");
        } else {
            whereString = LaiJingRenYuanColumns.SFSC + "=0";
        }

        mHuJiDiDataList.clear();

        Cursor cursor = contentProvider.runRawQuery(
                "SELECT " + LaiJingRenYuanColumns.HJDZS + "," + LaiJingRenYuanColumns.HJDZSHI + "," + LaiJingRenYuanColumns.HJDZQX + ", COUNT(*) AS CNT"
                        + " FROM " + CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI)
                        + " WHERE (" + whereString + ")"
                        + " GROUP BY " + LaiJingRenYuanColumns.HJDZS + ", " + LaiJingRenYuanColumns.HJDZSHI + ", " + LaiJingRenYuanColumns.HJDZQX + ";",
                null);


        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    DomicileStatisticsData data = new DomicileStatisticsData();
                    String province = cursor.getString(0);
                    String city = cursor.getString(1);
                    String district = cursor.getString(2);

                    data.address = province;
                    if (!province.equals(city))
                        data.address += city;
                    data.address += district;

                    data.count = cursor.getInt(3);

                    mHuJiDiDataList.add(data);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        mHuJiDiAdapter.notifyDataSetChanged();
    }

    /**
     * 按街道车辆采集统计
     */
    private void onCalculateCheLiang() {
        mLayoutXiaQu.setVisibility(View.GONE);
        mLayoutYuJing.setVisibility(View.GONE);
        mLayoutZhiLiang.setVisibility(View.GONE);
        mLayoutHuJiDi.setVisibility(View.GONE);
        mLayoutCheLiang.setVisibility(View.VISIBLE);

        mCheLiangDataList.clear();

        MyContentProvider contentProvider = new MyContentProvider();
        String whereString = getWhereStringWithDate();

        if (!TextUtils.isEmpty(whereString)) {
            whereString += (" AND " + CheLiangColumns.SFSC + "=0");
        } else {
            whereString = CheLiangColumns.SFSC + "=0";
        }

        Cursor cursor = contentProvider.runRawQuery("SELECT " + CheLiangColumns.SZJD + ", COUNT(*) AS CNT"
                + " FROM " + CommonUtils.getLastPathFromUri(CheLiangColumns.CONTENT_URI)
                + " WHERE (" + whereString + ")"
                + " GROUP BY " + CheLiangColumns.SZJD + ";", null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    DistrictStatisticsData data = new DistrictStatisticsData();
                    data.name = cursor.getString(0);
                    if (TextUtils.isEmpty(data.name)) data.name = "";
                    data.count = cursor.getInt(1);
                    data.policeName = mOwnerData.MJXM;

                    mCheLiangDataList.add(data);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        mCheLiangAdapter.notifyDataSetChanged();
    }

    /**
     * 获得选择的日子
     */
    private String getWhereStringWithDate() {
        String startDate = mEditStartDate.getText().toString();
        String endDate = mEditEndDate.getText().toString();

        String where = "";

        if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            where = " strftime('%Y-%m-%d', LRSJ) >= '" + startDate + "' AND strftime('%Y-%m-%d', LRSJ) <= '" + endDate + "'";
        } else if (!TextUtils.isEmpty(startDate)) {
            where = " strftime('%Y-%m-%d', LRSJ) >= '" + startDate + "'";
        } else if (!TextUtils.isEmpty(endDate)) {
            where = " strftime('%Y-%m-%d', LRSJ) <= '" + endDate + "'";
        }

        return where;
    }

}
