/**
 * @author LuYongXing
 * @date 2014.09.02
 * @filename ManageAccountActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.OwnerData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_GADW;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.JiZhuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

import java.util.ArrayList;

public class ManageAccountActivity extends PermanentActivity {

    private static final String TAG = ManageAccountActivity.class.getSimpleName();

    private View mLayoutXinXi;
    private EditText mEditMinJingID;
    private EditText mEditMinJingJingHao;
    private EditText mEditMinJingXingMing;
    private Spinner mSpinnerSuoShuDanWei;
    private Button mButtonBaoCun;

    private boolean mHasAccount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mHasAccount) {
            showPasswordDialog();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!mHasAccount) {
            menu.clear();
        } else {
            menu.findItem(R.id.action_YongHuGuanLi).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void initViews() {
        super.initViews();

        mLayoutInflater.inflate(R.layout.activity_manage_account, mLayoutContainer);

        mLayoutXinXi = findViewById(R.id.layout_xinxi);

        mEditMinJingID = (EditText) findViewById(R.id.edit_minjing_id);
        mEditMinJingJingHao = (EditText) findViewById(R.id.edit_minjingjinghao);
        mEditMinJingXingMing = (EditText) findViewById(R.id.edit_minjingxingming);

        mSpinnerSuoShuDanWei = (Spinner) findViewById(R.id.spinner_suoshudanwei);
        ArrayList<String> spinnerArray = getPoliceStationNames();
        spinnerArray.add(0, "请选择");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSuoShuDanWei.setAdapter(spinnerArrayAdapter);

        mButtonBaoCun = (Button) findViewById(R.id.button_baocun);
        mButtonBaoCun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonBaoCun.getText().toString().equals("保存")) {
                    if (checkFields())
                        onSave();
                } else {
                    showPasswordDialog();
                }
            }
        });

        loadPolicemanInformation();
    }

    /**
     * 载入民警信息
     */
    private void loadPolicemanInformation() {
        int count = 0;
        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(JiZhuColumns.CONTENT_URI,
                null, null, null, JiZhuColumns.SORT_ORDER_DEFAULT);
        if (cursor != null) {
            count = cursor.getCount();

            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                mOwnerData = new OwnerData();

                mOwnerData.MJID = cursor.getString(cursor.getColumnIndex(JiZhuColumns.MJID));
                mOwnerData.MJJH = cursor.getString(cursor.getColumnIndex(JiZhuColumns.MJJH));
                mOwnerData.MJXM = cursor.getString(cursor.getColumnIndex(JiZhuColumns.MJXM));
                mOwnerData.SSDWID = cursor.getString(cursor.getColumnIndex(JiZhuColumns.SSDWID));
                mOwnerData.SSDWMC = cursor.getString(cursor.getColumnIndex(JiZhuColumns.SSDWMC));

                mEditMinJingID.setText(mOwnerData.MJID);
                mEditMinJingJingHao.setText(mOwnerData.MJJH);
                mEditMinJingXingMing.setText(mOwnerData.MJXM);
                CommonUtils.selectSpinnerItem(mSpinnerSuoShuDanWei, mOwnerData.SSDWMC);
            }

            cursor.close();
        }

        if (count == 0) {
            CommonUtils.enableViewGroup((android.view.ViewGroup) mLayoutXinXi, true);
            mButtonBaoCun.setText("保存");
            mHasAccount = false;
        } else {
            CommonUtils.enableViewGroup((android.view.ViewGroup) mLayoutXinXi, false);
            mButtonBaoCun.setText("修改");
            mHasAccount = true;
        }
    }

    /**
     * Show password confirmation dialog
     */
    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = mLayoutInflater.inflate(R.layout.layout_password_input, null);
        final EditText editPassword = (EditText) view.findViewById(R.id.edit_mima);

        builder.setTitle(R.string.app_name)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = editPassword.getText().toString();

                        if (TextUtils.isEmpty(password)) {
                            new AlertDialog.Builder(ManageAccountActivity.this)
                                    .setTitle(R.string.app_name)
                                    .setMessage("请输入密码！")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            showPasswordDialog();
                                        }
                                    })
                                    .create()
                                    .show();
                        } else if (!checkPassword(password)) {
                            new AlertDialog.Builder(ManageAccountActivity.this)
                                    .setTitle(R.string.app_name)
                                    .setMessage("错误的密码！")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            showPasswordDialog();
                                        }
                                    })
                                    .create()
                                    .show();
                        } else {
                            CommonUtils.enableViewGroup((android.view.ViewGroup) mLayoutXinXi, true);
                            mButtonBaoCun.setText("保存");
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    /**
     * 检查密码
     */
    private boolean checkPassword(String password) {
        return password.equals("0000");
    }

    private boolean checkFields() {
        if (TextUtils.isEmpty(mEditMinJingID.getText().toString())
                || TextUtils.isEmpty(mEditMinJingJingHao.getText().toString())
                || TextUtils.isEmpty(mEditMinJingXingMing.getText().toString())
                || mSpinnerSuoShuDanWei.getSelectedItem().toString().equals("请选择")) {
            CommonUtils.createErrorAlertDialog(this, "请输入所有字段！").show();
            return false;
        }

        return true;
    }

    /**
     * 保存系统用户信息和机主信息
     */
    private void onSave() {
        MyContentProvider contentProvider = new MyContentProvider();

        // 机主信息
        ContentValues contentValues = new ContentValues();
        contentValues.put(JiZhuColumns.MJID, mEditMinJingID.getText().toString());
        contentValues.put(JiZhuColumns.MJJH, mEditMinJingJingHao.getText().toString());
        contentValues.put(JiZhuColumns.MJXM, mEditMinJingXingMing.getText().toString());
        contentValues.put(JiZhuColumns.SSDWMC, mSpinnerSuoShuDanWei.getSelectedItem().toString());
        contentValues.put(JiZhuColumns.SSDWID, getSuoShuDanWeiID(mSpinnerSuoShuDanWei.getSelectedItem().toString()));

        int count = 0;
        Cursor cursor = contentProvider.query(JiZhuColumns.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }

        if (count == 0) {
            contentProvider.insert(JiZhuColumns.CONTENT_URI, contentValues);

            mOwnerData.MJID = mEditMinJingID.getText().toString();
            mOwnerData.MJJH = mEditMinJingJingHao.getText().toString();
            mOwnerData.MJXM = mEditMinJingXingMing.getText().toString();
            mOwnerData.SSDWID = mSpinnerSuoShuDanWei.getSelectedItem().toString();
            mOwnerData.SSDWMC = getSuoShuDanWeiID(mSpinnerSuoShuDanWei.getSelectedItem().toString());

            new MyContentProvider().addSystemLog(mOwnerData, "系统管理", "用户信息", "1", "添加用户信息", "民警");
        } else {
            contentProvider.update(JiZhuColumns.CONTENT_URI, contentValues, "rowid=1", null);

            new MyContentProvider().addSystemLog(mOwnerData, "系统管理", "用户信息", "1", "修改用户信息", "民警");
        }

        new AlertDialog.Builder(ManageAccountActivity.this)
                .setTitle(R.string.app_name)
                .setMessage("成功保存！")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .create()
                .show();
    }

    /**
     * 获得派出所名称
     */
    private ArrayList<String> getPoliceStationNames() {
        ArrayList<String> nameList = new ArrayList<String>();
        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(C_GADW.CONTENT_URI, new String[]{C_GADW.DWMC},
                C_GADW.DWJB + "=0", null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    nameList.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return nameList;
    }

    /**
     * 获得所属单位ID
     */
    private String getSuoShuDanWeiID(String name) {
        String id = "";
        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(C_GADW.CONTENT_URI, null, C_GADW.DWMC + " IS '" + name + "'", null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndex(C_GADW.DWID));
            }

            cursor.close();
        }

        return id;
    }

}
