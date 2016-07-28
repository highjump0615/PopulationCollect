/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename RentRoomListActivity.java
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

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.RentRoomAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.RentRoomData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

import java.util.ArrayList;

public class RentRoomListActivity extends PermanentActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    // Constants
    public static final String REQUEST_CHOOSE_MODE = "request_choose_mode";
    public static final String CHOOSE_RENT_ROOM_ID = "choose_rent_room_id";

    private static final int ONCE_LOAD_RECORD_COUNT = 10;

    // Variables
    private EditText mEditStartDate;
    private EditText mEditEndDate;
    private EditText mEditRenterName;
    private EditText mEditAddress;
    private EditText mEditRoomID;
    private EditText mEditRenterCertificateID;
    private Spinner mSpinnerDistrict;
    private Spinner mSpinnerRoomType;
    private Spinner mSpinnerConstructionType;
    private Spinner mSpinnerBuildingType;

    private TextView mTextRecordCount;

    private ImageButton mButtonFirst;
    private ImageButton mButtonLast;
    private ImageButton mButtonPrev;
    private ImageButton mButtonNext;

    private RentRoomAdapter mAdapter;

    private ArrayList<RentRoomData> mDataList = new ArrayList<RentRoomData>();

    private String mWhereString = "";
    private int mRecordCount = 0;
    private int mPageIndex = 0;
    private int mPageCount = 0;

    private boolean mIsChooseMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(REQUEST_CHOOSE_MODE)) {
            mIsChooseMode = intent.getBooleanExtra(REQUEST_CHOOSE_MODE, false);
        }

        super.onCreate(savedInstanceState);

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
                new MyContentProvider().addSystemLog(mOwnerData, "信息检索", "房屋信息", "", "检索房屋信息", "民警");
                break;

            case R.id.button_add:
                startActivity(new Intent(this, RentRoomActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
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
        RentRoomData rentRoomData = mDataList.get(position);

        if (!mIsChooseMode) {
            Intent intent = new Intent(this, RentRoomActivity.class);
            intent.putExtra(RentRoomActivity.SELECTED_ROOM_ID, rentRoomData.id);
            intent.putExtra(RentRoomActivity.SELECTED_RENTER_ID, rentRoomData.fz_id);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

            new MyContentProvider().addSystemLog(mOwnerData, "信息检索", "房屋信息", String.valueOf(rentRoomData.id), "查看房屋信息", "民警");
        } else {
            Intent intent = new Intent();
            intent.putExtra(CHOOSE_RENT_ROOM_ID, rentRoomData.id);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!mIsChooseMode) {
            //menu.findItem(R.id.action_TianJia).setVisible(true);
            menu.findItem(R.id.action_ChuZuFangWu).setVisible(false);

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
                startActivity(new Intent(this, RentRoomActivity.class));
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

        View view = mLayoutInflater.inflate(R.layout.activity_rent_room_list, mLayoutContainer);

        // Initialize ListView and Adapter
        mAdapter = new RentRoomAdapter(this, mDataList);
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
        mEditRoomID = (EditText) view.findViewById(R.id.edit_room_id);
        mEditRenterCertificateID = (EditText) view.findViewById(R.id.edit_certificate_id);

        mSpinnerDistrict = (Spinner) view.findViewById(R.id.spinner_district);
        mSpinnerRoomType = (Spinner) view.findViewById(R.id.spinner_room_type);
        mSpinnerConstructionType = (Spinner) view.findViewById(R.id.spinner_construction_type);
        mSpinnerBuildingType = (Spinner) view.findViewById(R.id.spinner_building_type);

        MyContentProvider contentProvider = new MyContentProvider();
        ArrayList<String> spinnerArray = contentProvider.getDistrictNames();
        spinnerArray.add(0, "");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDistrict.setAdapter(spinnerArrayAdapter);

        // Search button
        view.findViewById(R.id.button_search).setOnClickListener(this);
        // Add button
        view.findViewById(R.id.button_add).setOnClickListener(this);

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
     * Load RentRoomData
     */
    private void loadData() {
        mDataList.clear();

        String whereString = "";
        if (!TextUtils.isEmpty(mWhereString)) {
            whereString = mWhereString + " AND ";
        }
        whereString += FangWuColumns.SFSC + "=0";

        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(FangWuColumns.CONTENT_URI,
                new String[]{FangWuColumns.FWID, FangWuColumns.XM,
                        FangWuColumns.SZDXZZT, FangWuColumns.SYQLX, FangWuColumns.DWMC},
                whereString, null, null);
        if (cursor != null) {
            mRecordCount = cursor.getCount();

            if (mRecordCount > 0 && cursor.moveToPosition(mPageIndex * ONCE_LOAD_RECORD_COUNT)) {
                int i = 0;

                do {
                    RentRoomData data = new RentRoomData();
                    data.no = mPageIndex * ONCE_LOAD_RECORD_COUNT + i + 1;
                    data.id = cursor.getString(cursor.getColumnIndex(FangWuColumns.FWID));
                    data.fz_id = cursor.getString(cursor.getColumnIndex(FangWuColumns.FWID));
                    data.address = cursor.getString(cursor.getColumnIndex(FangWuColumns.SZDXZZT));

                    if (cursor.getString(cursor.getColumnIndex(FangWuColumns.SYQLX)).equals("单位"))
                        data.renterName = cursor.getString(cursor.getColumnIndex(FangWuColumns.DWMC));
                    else
                        data.renterName = cursor.getString(cursor.getColumnIndex(FangWuColumns.XM));

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
        String roomID = mEditRoomID.getText().toString();
        String renterCertificateID = mEditRenterCertificateID.getText().toString();
        String roomType = mSpinnerRoomType.getSelectedItem().toString();
        String constructionType = mSpinnerConstructionType.getSelectedItem().toString();
        String buildingType = mSpinnerBuildingType.getSelectedItem().toString();

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
            whereString += "(" + FangWuColumns.XM + " LIKE '%" + renterName + "%' OR ";
            whereString += FangWuColumns.DWMC + " LIKE '%" + renterName + "%')";
        }

        if (!TextUtils.isEmpty(address)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += FangWuColumns.SZDXZZT + " LIKE '%" + address + "%' ";
        }

        if (!TextUtils.isEmpty(district)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += FangWuColumns.SZDXQ + " LIKE '%" + district + "%' ";
        }

        if (!"请选择".equals(roomType)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += FangWuColumns.FWLX + " LIKE '%" + roomType + "%' ";
        }

        if (!"请选择".equals(constructionType)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += FangWuColumns.JSXZ + " LIKE '%" + constructionType + "%' ";
        }

        if (!"请选择".equals(buildingType)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += FangWuColumns.JZLX + " LIKE '%" + buildingType + "%' ";
        }

        if (!TextUtils.isEmpty(roomID)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += FangWuColumns.FCZH + " LIKE '%" + roomID + "%' ";
        }

        if (!TextUtils.isEmpty(renterCertificateID)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += FangWuColumns.ZJHM + " LIKE '%" + renterCertificateID + "%' ";
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

}
