package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>车辆类型</b>
 * <br>
 * 保存车辆类型。
 */
public class C_CLLX implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.CHELIANGLEIXING_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.cllx";

    /**
     * 代码：			DM、VC/60、非空、主键；
     */
    public static final String DM = "DM";
    /**
     * 代码内容：		   DMNR、VC/60、非空；
     */
    public static final String DMNR = "DMNR";

}
