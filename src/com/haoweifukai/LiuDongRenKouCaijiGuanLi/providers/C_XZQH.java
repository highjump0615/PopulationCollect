package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>行政区划表</b>
 * <br>
 * 保存行政区划信息。
 */
public class C_XZQH implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.XINGZHENGQUHUA_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.xzqh";

    /**
     * 行政区划：		XZQH、VC/60、非空、主键；
     */
    public static final String XZQH = "XZQH";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = XZQH + " ASC";
    /**
     * 名称：			MC、VC/60、非空；
     */
    public static final String MC = "MC";
    /**
     * 导入时间：		DRSJ、DATE；
     */
    public static final String DRSJ = "DRSJ";

}
