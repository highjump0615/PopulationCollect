/**
 * @author LuYongXing
 * @date 2014.09.03
 * @filename SystemLogViewActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CaoZuoRiZhiColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;

public class SystemLogViewActivity extends PermanentActivity {

    public static final String SELECTED_LOG_ID = "selected_log_id";

    private String mLogID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(SELECTED_LOG_ID)) {
            mLogID = intent.getStringExtra(SELECTED_LOG_ID);
        }

        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initViews() {
        super.initViews();

        mLayoutInflater.inflate(R.layout.activity_log_view, mLayoutContainer);

        TextView textJingHao = (TextView) findViewById(R.id.text_jinghao);
        TextView textXingMing = (TextView) findViewById(R.id.text_xingming);
        TextView textDanWeiMingCheng = (TextView) findViewById(R.id.text_danweimingcheng);
        TextView textMoKuai = (TextView) findViewById(R.id.text_mokuai);
        TextView textShuJuLeiXing = (TextView) findViewById(R.id.text_shujuleixing);
        TextView textMiaoShu = (TextView) findViewById(R.id.text_miaoshu);
        TextView textShiJian = (TextView) findViewById(R.id.text_shijian);
        TextView textRenYuanLeiXing = (TextView) findViewById(R.id.text_renyuanleixing);

        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(CaoZuoRiZhiColumns.CONTENT_URI, null,
                CaoZuoRiZhiColumns.CZXXID + " IS '" + mLogID + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                textJingHao.setText(cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZRJH)));
                textXingMing.setText(cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZRXM)));
                textDanWeiMingCheng.setText(cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.SSDWMC)));
                textMoKuai.setText(cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZMK)));
                textShuJuLeiXing.setText(cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZSJLX)));
                textMiaoShu.setText(cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZMS)));
                textShiJian.setText(cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZSJ)));
                textRenYuanLeiXing.setText(cursor.getString(cursor.getColumnIndex(CaoZuoRiZhiColumns.CZRLX)));
            }

            cursor.close();
        }
    }

}
