/**
 * @author LuYongXing
 * @date 2014.09.18
 * @filename AlertDetailActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.T_HMD_MD5;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.YuJingColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

import java.util.Date;

public class AlertDetailActivity extends PermanentActivity {

    public static final String SELECTED_PERSON_ID = "selected_person_id";

    private String mPersonID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(SELECTED_PERSON_ID)) {
            mPersonID = intent.getStringExtra(SELECTED_PERSON_ID);
        }

        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        new MyContentProvider().addSystemLog(mOwnerData, "比对预警", "预警信息", mPersonID, "查看预警信息", "民警");
    }

    @Override
    protected void initViews() {
        super.initViews();

        mLayoutInflater.inflate(R.layout.activity_alert_view, mLayoutContainer);

        TextView textRenYuanXingMing = (TextView) findViewById(R.id.text_renyuanxingming);
        TextView textShenFenZhengHao = (TextView) findViewById(R.id.text_shenfenzhenghao);
        TextView textRenYuanLeiXing = (TextView) findViewById(R.id.text_renyuanleixing);
        TextView textHuoQuShiJian = (TextView) findViewById(R.id.text_huoqushijian);
        TextView textDaoRuShiJian = (TextView) findViewById(R.id.text_daorushijian);

        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(YuJingColumns.CONTENT_URI, null,
                YuJingColumns.YJXXID + " IS '" + mPersonID + "'", null, null);

        String renyuanID = "";

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                renyuanID = cursor.getString(cursor.getColumnIndex(YuJingColumns.LJRYRYID));

                textShenFenZhengHao.setText(cursor.getString(cursor.getColumnIndex(YuJingColumns.BDSFZH)));
                textHuoQuShiJian.setText(cursor.getString(cursor.getColumnIndex(YuJingColumns.BDSJ)));
                textRenYuanLeiXing.setText(String.valueOf(1));
            }

            cursor.close();
        }

        cursor = contentProvider.query(LaiJingRenYuanColumns.CONTENT_URI, null,
                LaiJingRenYuanColumns.RYID + " IS '" + renyuanID + "'", null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                textRenYuanXingMing.setText(cursor.getString(cursor.getColumnIndex(LaiJingRenYuanColumns.XM)));
            }

            cursor.close();
        }

        String id = textShenFenZhengHao.getText().toString();
        if (!TextUtils.isEmpty(id)) {
            cursor = contentProvider.query(T_HMD_MD5.CONTENT_URI, null, T_HMD_MD5.MD5 + " IS '"
                    + CommonUtils.getMD5EncryptedString(id) + "'", null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    textDaoRuShiJian.setText(cursor.getString(cursor.getColumnIndex(T_HMD_MD5.DRSJ)));
                }

                cursor.close();
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(YuJingColumns.SFCK, true);
        contentValues.put(YuJingColumns.CKRID, mOwnerData.MJID);
        contentValues.put(YuJingColumns.CKRJH, mOwnerData.MJJH);
        contentValues.put(YuJingColumns.CKRXM, mOwnerData.MJXM);
        contentValues.put(YuJingColumns.CKRDWID, mOwnerData.SSDWID);
        contentValues.put(YuJingColumns.CKRDWMC, mOwnerData.SSDWMC);
        contentValues.put(YuJingColumns.CKSJ, CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));

        contentProvider.update(YuJingColumns.CONTENT_URI, contentValues,
                YuJingColumns.YJXXID + " IS '" + mPersonID + "'", null);
    }

}
