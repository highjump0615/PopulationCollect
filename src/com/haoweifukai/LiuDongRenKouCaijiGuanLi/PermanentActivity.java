/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename PermanentActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.OwnerData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.JiZhuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PermanentActivity extends FragmentActivity {

    private static final String TAG = PermanentActivity.class.getSimpleName();

    public static final String EXIT_APPLICATION_ACTION = "com.haoweifukai.LiuDongRenKouCaijiGuanLi.ACTION_EXIT_APPLICATION";

    private BroadcastReceiver mExitAppReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "exit from " + context.toString());
            finish();
        }
    };

    protected Menu mOptionMenu;

    protected LayoutInflater mLayoutInflater;
    protected FrameLayout mLayoutContainer;
    private TextView mTextDate;
    private TextView mTextUnitName;
    private TextView mTextPoliceName;
    protected ImageView mImageAlertLED;

    public OwnerData mOwnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permanent);

        mLayoutInflater = LayoutInflater.from(this);

        loadOwnerData();
        initViews();

        registerExitApplicationReceiver();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadOwnerData();

        mTextDate.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.CHINA).format(new Date()));
        mTextUnitName.setText(mOwnerData.SSDWMC);
        mTextPoliceName.setText(mOwnerData.MJXM);
    }

    @Override
    protected void onDestroy() {
        onExitActivity();
        super.onDestroy();
    }

    private void registerExitApplicationReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EXIT_APPLICATION_ACTION);
        registerReceiver(mExitAppReceiver, intentFilter);
    }

    private void unRegisterExitApplicationReceiver() {
        try {
            unregisterReceiver(mExitAppReceiver);
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        mOptionMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
                return true;

            case R.id.action_ChuZuFangWu:
                onRentRoom();
                return true;

            case R.id.action_LaiJingRenYuan:
                onFloatingPeople();
                return true;

            case R.id.action_CheLiangXinXi:
                onVehicle();
                return true;

            case R.id.action_ShangBaoShuJu:
                onReportData();
                return true;

            case R.id.action_CaiJiTongJi:
                onStatistics();
                return true;

            case R.id.action_YongHuGuanLi:
                onAccountSetting();
                return true;

            case R.id.action_BiDuiYuJing:
                onBlackList();
                return true;

            case R.id.action_XiTongRiZhi:
                onSystemLog();
                return true;

            case R.id.action_XiTongGuanLi:
                onSettings();
                return true;

            case R.id.action_BianXieFaGui:
                onHelp();
                return true;

            case R.id.action_TuiChu:
                onExitApplication();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialize sub views in Permanent frame
     */
    protected void initViews() {
        mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);

        mTextDate = (TextView) findViewById(R.id.text_date);
        mTextUnitName = (TextView) findViewById(R.id.text_unit_name);
        mTextPoliceName = (TextView) findViewById(R.id.text_police_name);

        mImageAlertLED = (ImageView) findViewById(R.id.image_alert_led);
        mImageAlertLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAlertDetail();
            }
        });
    }

    /**
     * Load configuration
     */
    private void loadOwnerData() {
        MyContentProvider contentProvider = new MyContentProvider();

        mOwnerData = new OwnerData();

        Cursor cursor = contentProvider.query(JiZhuColumns.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                mOwnerData.MJID = cursor.getString(cursor.getColumnIndex(JiZhuColumns.MJID));
                mOwnerData.MJJH = cursor.getString(cursor.getColumnIndex(JiZhuColumns.MJJH));
                mOwnerData.MJXM = cursor.getString(cursor.getColumnIndex(JiZhuColumns.MJXM));
                mOwnerData.SSDWID = cursor.getString(cursor.getColumnIndex(JiZhuColumns.SSDWID));
                mOwnerData.SSDWMC = cursor.getString(cursor.getColumnIndex(JiZhuColumns.SSDWMC));
            }

            cursor.close();
        }
    }

    /**
     * Process after activity was exited
     */
    protected void onExitActivity() {
        unRegisterExitApplicationReceiver();
    }

    /**
     * 进入“出租房屋列表”页面
     */
    private void onRentRoom() {
        startActivity(new Intent(this, RentRoomListActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 进入“来京人员列表”页面
     */
    private void onFloatingPeople() {
        startActivity(new Intent(this, FloatingPersonListActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 进入“系统管理”页面
     */
    private void onVehicle() {
        startActivity(new Intent(this, VehicleListActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 上报本地所有未上报的数据记录
     */
    private void onReportData() {
        startActivity(new Intent(this, ReportDataActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 进入“采集统计”页面
     */
    private void onStatistics() {
        startActivity(new Intent(this, StatisticsActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 进入“用户管理”页面
     */
    private void onAccountSetting() {
        startActivity(new Intent(this, ManageAccountActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 进入“黑名单”页面
     */
    private void onBlackList() {
        startActivity(new Intent(this, BlackListActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 进入“系统日志”页面
     */
    private void onSystemLog() {
        startActivity(new Intent(this, SystemLogActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 进入“系统管理”页面
     */
    private void onSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 进入“便携法规”页面
     */
    private void onHelp() {
        startActivity(new Intent(this, HelpActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 退出当前应用
     */
    private void onExitApplication() {
        // 关闭其他开放活动
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(EXIT_APPLICATION_ACTION);
        sendBroadcast(broadcastIntent);

        finish();
    }

    /**
     * 进入“黑名单详细”页面
     */
    protected void onAlertDetail() {
    }

}
