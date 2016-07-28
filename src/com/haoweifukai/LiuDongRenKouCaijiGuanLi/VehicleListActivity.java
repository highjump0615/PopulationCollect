/**
 * @author LuYongXing
 * @date 2014.09.28
 * @filename VehicleListActivity.java
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

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.VehicleAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.VehicleData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CheLiangColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

import java.util.ArrayList;

public class VehicleListActivity extends PermanentActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    // Constants
    private static final int ONCE_LOAD_RECORD_COUNT = 10;

    private EditText mEditStartDate;
    private EditText mEditEndDate;
    private EditText mEditVehicleNumber;
    private EditText mEditVehicleBrand;
    private EditText mEditVehicleModel;
    private EditText mEditDriverCertificateID;
    private EditText mEditDriverName;
    private EditText mEditStreet;

    private Spinner mSpinnerVehicleColor;
    private Spinner mSpinnerVehicleType;
    private Spinner mSpinnerDistrict;

    private TextView mTextRecordCount;

    private ImageButton mButtonFirst;
    private ImageButton mButtonLast;
    private ImageButton mButtonPrev;
    private ImageButton mButtonNext;

    private VehicleAdapter mAdapter;

    private ArrayList<VehicleData> mDataList = new ArrayList<VehicleData>();

    private String mWhereString = "";
    private int mRecordCount = 0;
    private int mPageIndex = 0;
    private int mPageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_search:
                mPageIndex = 0;
                onSearch();
                new MyContentProvider().addSystemLog(mOwnerData, "信息检索", "车辆信息", "", "检索车辆信息", "民警");
                break;

            case R.id.button_add:
                startActivity(new Intent(this, VehicleActivity.class));
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
        VehicleData data = mDataList.get(position);

        Intent intent = new Intent(this, VehicleActivity.class);
        intent.putExtra(VehicleActivity.SELECTED_VEHICLE_ID, data.id);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        new MyContentProvider().addSystemLog(mOwnerData, "信息检索", "车辆信息", String.valueOf(data.id), "查看车辆", "民警");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_TianJia).setVisible(true);
        menu.findItem(R.id.action_CheLiangXinXi).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
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

        View view = mLayoutInflater.inflate(R.layout.activity_vehicle_list, mLayoutContainer);

        // Initialize ListView and Adapter
        mAdapter = new VehicleAdapter(this, mDataList);
        ListView listView = (ListView) view.findViewById(R.id.list_rent_room);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        // EditText
        mEditStartDate = (EditText) view.findViewById(R.id.edit_start_date);
        mEditStartDate.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(this, mEditStartDate));
        mEditEndDate = (EditText) view.findViewById(R.id.edit_end_date);
        mEditEndDate.setOnTouchListener(CommonUtils.getOnTouchListenerForDatePicker(this, mEditEndDate));
        mEditVehicleNumber = (EditText) view.findViewById(R.id.edit_vehicle_number);
        mEditVehicleBrand = (EditText) view.findViewById(R.id.edit_vehicle_brand);
        mEditVehicleModel = (EditText) view.findViewById(R.id.edit_vehicle_model);
        mEditDriverCertificateID = (EditText) view.findViewById(R.id.edit_certificate_id);
        mEditDriverName = (EditText) view.findViewById(R.id.edit_driver_name);
        mEditStreet = (EditText) view.findViewById(R.id.edit_street);

        mSpinnerVehicleColor = (Spinner) view.findViewById(R.id.spinner_vehicle_color);
        mSpinnerVehicleType = (Spinner) view.findViewById(R.id.spinner_vehicle_type);
        mSpinnerDistrict = (Spinner) view.findViewById(R.id.spinner_district);

        // 车身颜色
        final MyContentProvider contentProvider = new MyContentProvider();
        ArrayList<String> spinnerArray = contentProvider.getVehicleColor();
        spinnerArray.add(0, "请选择");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerVehicleColor.setAdapter(spinnerArrayAdapter);

        // 所在区县
        spinnerArray = contentProvider.getVehicleType();
        spinnerArray.add(0, "请选择");

        spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerVehicleType.setAdapter(spinnerArrayAdapter);

        // 所在区县
        spinnerArray = contentProvider.getQX("北京市");
        spinnerArray.add(0, "请选择");

        spinnerArrayAdapter = new ArrayAdapter<String>(this,
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

        Cursor cursor = contentProvider.query(CheLiangColumns.CONTENT_URI,
                new String[]{CheLiangColumns.CLID, CheLiangColumns.CPH, CheLiangColumns.CSYS,
                        CheLiangColumns.CLLX, CheLiangColumns.XXDZ},
                whereString, null, null);
        if (cursor != null) {
            mRecordCount = cursor.getCount();

            if (mRecordCount > 0 && cursor.moveToPosition(mPageIndex * ONCE_LOAD_RECORD_COUNT)) {
                int i = 0;

                do {
                    VehicleData data = new VehicleData();
                    data.no = mPageIndex * ONCE_LOAD_RECORD_COUNT + i + 1;
                    data.id = cursor.getString(cursor.getColumnIndex(CheLiangColumns.CLID));
                    data.number = cursor.getString(cursor.getColumnIndex(CheLiangColumns.CPH));
                    data.color = cursor.getString(cursor.getColumnIndex(CheLiangColumns.CSYS));
                    data.type = cursor.getString(cursor.getColumnIndex(CheLiangColumns.CLLX));
                    data.address = cursor.getString(cursor.getColumnIndex(CheLiangColumns.XXDZ));

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
        String vehicleNumber = mEditVehicleNumber.getText().toString();
        String color = mSpinnerVehicleColor.getSelectedItem().toString();
        String brand = mEditVehicleBrand.getText().toString();
        String model = mEditVehicleModel.getText().toString();
        String type = mSpinnerVehicleType.getSelectedItem().toString();
        String driverCertificateID = mEditDriverCertificateID.getText().toString();
        String driverName = mEditDriverName.getText().toString();
        String district = mSpinnerDistrict.getSelectedItem().toString();
        String street = mEditStreet.getText().toString();

        String whereString = ShangChuanColumns.SFSC + "=0";

        if (!TextUtils.isEmpty(startDate)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += "strftime('%Y-%m-%d', LRSJ) >= '" + startDate + "'";
        }

        if (!TextUtils.isEmpty(endDate)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += "strftime('%Y-%m-%d', LRSJ) <= '" + endDate + "'";
        }

        if (!TextUtils.isEmpty(vehicleNumber)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.CPH + " LIKE '%" + vehicleNumber + "%'";
        }

        if (!"请选择".equals(color)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.CSYS + " LIKE '%" + color + "%' ";
        }

        if (!TextUtils.isEmpty(brand)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.PP + " LIKE '%" + brand + "%' ";
        }

        if (!TextUtils.isEmpty(model)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.XH + " LIKE '%" + model + "%' ";
        }

        if (!"请选择".equals(type)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.CLLX + " LIKE '%" + type + "%' ";
        }

        if (!TextUtils.isEmpty(driverCertificateID)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.JSRSFZH + " LIKE '%" + driverCertificateID + "%' ";
        }

        if (!TextUtils.isEmpty(driverName)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.JSRXM + " LIKE '%" + driverName + "%'";
        }

        if (!"请选择".equals(district)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.SZQX + " LIKE '%" + district + "%'";
        }

        if (!TextUtils.isEmpty(street)) {
            if (!TextUtils.isEmpty(whereString)) whereString += " AND ";
            whereString += CheLiangColumns.SZJD + " LIKE '%" + street + "%'";
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
