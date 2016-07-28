/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename MainActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CheLiangColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.ShangChuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

public class MainActivity extends PermanentActivity {

    private TextView mTextChuZuFangWuShu;
    private TextView mTextLaiJingRenYuanShu;
    private TextView mTextCheLiangDengJiShu;

    @Override
    protected void onResume() {
        super.onResume();

        getRecordCount();
    }

    @Override
    protected void initViews() {
        super.initViews();

        // 添加
        mLayoutInflater.inflate(R.layout.activity_main, mLayoutContainer);

        // 出租房屋
        mTextChuZuFangWuShu = (TextView) findViewById(R.id.text_chuzufangwushu);

        // 来京人员
        mTextLaiJingRenYuanShu = (TextView) findViewById(R.id.text_laijingrenyuanshu);

        // 车辆登记
        mTextCheLiangDengJiShu = (TextView) findViewById(R.id.text_cheliangdengjishu);
    }

    private void getRecordCount() {
        MyContentProvider contentProvider = new MyContentProvider();

        long count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(FangWuColumns.CONTENT_URI),
                ShangChuanColumns.SFSC + "=0");
        mTextChuZuFangWuShu.setText(getString(R.string.ChuZhuFangWuJiShu, count));

        // "SFSC='0' and FWID in (select FWID from T_FWXX where SFSC='0' and LRMJID='"
        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI),
                ShangChuanColumns.SFSC + "=0");
        mTextLaiJingRenYuanShu.setText(getString(R.string.LaiJingRenYuanJiShu, count));

        count = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(CheLiangColumns.CONTENT_URI),
                ShangChuanColumns.SFSC + "=0");
        mTextCheLiangDengJiShu.setText(getString(R.string.CheLiangDengJi, count));
    }

}
