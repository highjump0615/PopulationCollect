/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename FloatingPersonListActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.FloatingPersonAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.FloatingPersonData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

import java.util.ArrayList;

public class FloatingPersonListActivity extends PermanentActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    // Constants
    public static final String EXTERNAL_QUERY = "external_query";
    public static final String REQUEST_CHOOSE_MODE = "request_choose_mode";
    public static final String CHOOSE_PERSON_ID = "choose_person_id";
    private static final int ONCE_LOAD_RECORD_COUNT = 10;

    private EditText mEditStartDate;
    private EditText mEditEndDate;
    private EditText mEditRenterName;
    private EditText mEditAddress;
    private EditText mEditRenterCertificateID;

    private Spinner mSpinnerDistrict;
    private Spinner mSpinnerGender;
    private Spinner mSpinnerPoliticalStatus;
    private Spinner mSpinnerComingCause;
    private Spinner mSpinnerResidentType;
    private Spinner mSpinnerPoliceStation;

    private TextView mTextRecordCount;

    private ImageButton mButtonFirst;
    private ImageButton mButtonLast;
    private ImageButton mButtonPrev;
    private ImageButton mButtonNext;

    private FloatingPersonAdapter mAdapter;

    private ArrayList<FloatingPersonData> mDataList = new ArrayList<FloatingPersonData>();

    private String mExternalQuery = "";
    private String mWhereString = "";
    private int mRecordCount = 0;
    private int mPageIndex = 0;
    private int mPageCount = 0;

    private boolean mIsChooseMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTERNAL_QUERY)) {
            mWhereString = mExternalQuery = intent.getStringExtra(EXTERNAL_QUERY);
        } else if (intent.hasExtra(REQUEST_CHOOSE_MODE)) {
            mIsChooseMode = intent.getBooleanExtra(REQUEST_CHOOSE_MODE, false);
        }

        super.onCreate(savedInstanceState);

        if (!TextUtils.isEmpty(mExternalQuery)) {
            onFillSearchFields();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(!mIsChooseMode);
            getActionBar().setDisplayShowHomeEnabled(!mIsChooseMode);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_search:
                mPageIndex = 0;
                onSearch();
                new MyContentProvider().addSystemLog(mOwnerData, "信息检索", "来京人员信息", "", "检索来京人员", "民警");
                break;

            case R.id.button_add:
                startActivity(new Intent(this, FloatingPersonActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                break;

            case R.id.button_guijichaxun:
                onSearchRegisterHistory();
                break;

            case R.id.button_first:
                mPageIndex = 0;
                onSearch();
                break;

            case R.id.button_previous:
                mPageIndex--;
                onSearch();
                break;

            case R.id.button_next:
                mPageIndex++;
                onSearch();
                break;

            case R.id.button_last:
                mPageIndex = mPageCount - 1;
                onSearch();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FloatingPersonData data = mDataList.get(position);

        if (!mIsChooseMode) {
            Intent intent = new Intent(this, FloatingPersonActivity.class);
            intent.putExtra(FloatingPersonActivity.SELECTED_PERSON_ID, data.id);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            new MyContentProvider().addSystemLog(mOwnerData, "信息检索", "来京人员信息", String.valueOf(data.id), "查看来京人员", "民警");
        } else {
            Intent intent = new Intent();
            intent.putExtra(CHOOSE_PERSON_ID, data.id);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!mIsChooseMode) {
            //menu.findItem(R.id.action_TianJia).setVisible(true);
            menu.findItem(R.id.action_LaiJingRenYuan).setVisible(false);

            return super.onPrepareOptionsMenu(menu);
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_TianJia:
                startActivity(new Intent(this, FloatingPersonActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void initViews() {
        super.initViews();

        View view = mLayoutInflater.inflate(R.layout.activity_floating_person_list, mLayoutContainer);

        // Initialize ListView and Adapter
        mAdapter = new FloatingPersonAdapter(this, mDataList);
        ListView listView = (ListView) view.findViewById(R.id.list_rent_room);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        // EditText
        mEditStartDate = (EditText) view.findViewById(R.id.edit_start_date);
        mEditStartDate.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(this, mEditStartDate));
        mEditEndDate = (EditText) view.findViewById(R.id.edit_end_date);
        mEditEndDate.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(this, mEditEndDate));
        mEditRenterName = (EditText) view.findViewById(R.id.edit_renter_name);
        mEditAddress = (EditText) view.findViewById(R.id.edit_detail_address);
        mEditRenterCertificateID = (EditText) view.findViewById(R.id.edit_certificate_id);

        mSpinnerDistrict = (Spinner) view.findViewById(R.id.spinner_district);
        mSpinnerGender = (Spinner) view.findViewById(R.id.spinner_gender);
        mSpinnerPoliticalStatus = (Spinner) view.findViewById(R.id.spinner_political_status);
        mSpinnerComingCause = (Spinner) view.findViewById(R.id.spinner_coming_cause);
        mSpinnerResidentType = (Spinner) view.findViewById(R.id.spinner_resident_type);
        mSpinnerPoliceStation = (Spinner) view.findViewById(R.id.spinner_police_station);

        MyContentProvider contentProvider = new MyContentProvider();
        ArrayList<String> spinnerArray = contentProvider.getDistrictNames();
        spinnerArray.add(0, "");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDistrict.setAdapter(spinnerArrayAdapter);

        spinnerArray = new ArrayList<String>();
        spinnerArray.add("");
        spinnerArray.add(mOwnerData.SSDWMC);

        spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPoliceStation.setAdapter(spinnerArrayAdapter);

        // Search button
        view.findViewById(R.id.button_search).setOnClickListener(this);
        // Add button
        view.findViewById(R.id.button_add).setOnClickListener(this);
        // History
        view.findViewById(R.id.button_guijichaxun).setOnClickListener(this);

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

        String whereString = "";
        if (!TextUtils.isEmpty(mWhereString)) {
            whereString = mWhereString + " AND ";
        }
        whereString += LaiJingRenYuanColumns.SFSC + "=0";

        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI,
                new String[]{LaiJingRenYuanColumns.RYID, LaiJingRenYuanColumns.FWDJBXH, LaiJingRenYuanColumns.XM, LaiJingRenYuanColumns.XZDXZZT},
                whereString, null, null);
        if (cursor != null) {
            mRecordCount = cursor.getCount();

            if (mRecordCount > 0 && cursor.moveToPosition(mPageIndex * ONCE_LOAD_RECORD_COUNT)) {
                int i = 0;

                do {
                    FloatingPersonData data = new FloatingPersonData();
                    data.no = mPageIndex * ONCE_LOAD_RECORD_COUNT + i + 1;
                    data.id = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.RYID));
                    data.renterName = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XM));
                    data.address = cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XZDXZZT));

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
     * Set where string and search data in database
     */
    private void onSearch() {
        String startDate = mEditStartDate.getText().toString();
        String endDate = mEditEndDate.getText().toString();
        String renterName = mEditRenterName.getText().toString();
        String address = mEditAddress.getText().toString();
        String district = mSpinnerDistrict.getSelectedItem().toString();
        String renterCertificateID = mEditRenterCertificateID.getText().toString();
        String gender = mSpinnerGender.getSelectedItem().toString();
        String politicalStatus = mSpinnerPoliticalStatus.getSelectedItem().toString();
        String comingCause = mSpinnerComingCause.getSelectedItem().toString();
        String residentType = mSpinnerResidentType.getSelectedItem().toString();
        String policeStand = mSpinnerPoliceStation.getSelectedItem().toString();

        String whereString = ShangChuanColumns.SFSC + "=0";

        if (!TextUtils.isEmpty(startDate)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += "strftime('%Y-%m-%d', LRSJ) >= '" + startDate + "'";
        }

        if (!TextUtils.isEmpty(endDate)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += "strftime('%Y-%m-%d', LRSJ) <= '" + endDate + "'";
        }

        if (!TextUtils.isEmpty(renterName)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.XM + " LIKE '%" + renterName + "%'";
        }

        if (!TextUtils.isEmpty(renterCertificateID)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.SFZHM + " LIKE '%" + renterCertificateID + "%' ";
        }

        if (!TextUtils.isEmpty(address)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.XZDXZZT + " LIKE '%" + address + "%'";
        }

        if (!TextUtils.isEmpty(district)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.SZDXQ + " IS '" + district + "'";
        }

        if (!"请选择".equals(gender)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.XB + " LIKE '%" + gender + "%' ";
        }

        if (!"请选择".equals(politicalStatus)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.ZZMM + " LIKE '%" + politicalStatus + "%' ";
        }

        if (!"请选择".equals(comingCause)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.LJYY + " LIKE '%" + comingCause + "%' ";
        }

        if (!"请选择".equals(residentType)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.JZLX + " LIKE '%" + residentType + "%' ";
        }

        if (!TextUtils.isEmpty(policeStand)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += LaiJingRenYuanColumns.SSPCSMC + " LIKE '%" + policeStand + "%' ";
        }

        if (!TextUtils.isEmpty(mExternalQuery)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += mExternalQuery;
        }

        mWhereString = whereString;
        loadData();
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

    private void onFillSearchFields() {
        MyContentProvider contentProvider = new MyContentProvider();
        Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI, null,
                mExternalQuery, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                mEditAddress.setText(cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXXDZ)));
                mEditAddress.setEnabled(false);
                CommonUtils.selectSpinnerItem(mSpinnerDistrict, cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXQ)));
                mSpinnerDistrict.setEnabled(false);
                CommonUtils.selectSpinnerItem(mSpinnerPoliceStation, cursor.getString(cursor.getColumnIndex(FangWuColumns.SSPCSMC)));
                mSpinnerPoliceStation.setEnabled(false);
                CommonUtils.selectSpinnerItem(mSpinnerResidentType, "租赁房屋");
                mSpinnerResidentType.setEnabled(false);
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

        if (TextUtils.isEmpty(mEditRenterCertificateID.getText().toString())) {
            CommonUtils.createErrorAlertDialog(this, "请输入身份证号码！").show();
            return;
        }

        Intent intent = new Intent(this, RegisterHistoryActivity.class);
        intent.putExtra(RegisterHistoryActivity.ID_CARD_NUMBER, mEditRenterCertificateID.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

        new MyContentProvider().addSystemLog(mOwnerData, "无线接口", "来京人员信息", "", "无线轨迹查询", "民警");
    }

}
