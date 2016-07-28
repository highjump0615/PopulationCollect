package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>公安单位表</b>
 * <br>
 * 保存公安单位信息。
 */
public class C_GADW implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.GONGANDANWEI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.gadw";

    /**
     * 单位ID：			DWID、VC/60、非空、主键；
     */
    public static final String DWID = "DWID";
    /**
     * 单位名称：		DWMC、VC/60、非空；
     */
    public static final String DWMC = "DWMC";
    /**
     * 上级单位ID：		SJDWID、VC/60、当前单位的上级单位ID；
     */
    public static final String SJDWID = "SJDWID";
    /**
     * 导入时间：		DRSJ、DATE；
     */
    public static final String DRSJ = "DRSJ";
    /**
     * 单位级别：		DWJB、VC/60、0 – 派出所、1 – 分县局、2 – 市局；
     */
    public static final String DWJB = "DWJB";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = DWID + " ASC";

}
