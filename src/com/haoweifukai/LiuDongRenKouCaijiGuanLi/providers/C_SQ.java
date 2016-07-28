package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>社区表</b>
 * <br>
 * 保存出租房屋所在地辖区（社区）。
 */
public class C_SQ implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.SHEQU_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.sq";

    /**
     * 社区ID：			SQID、VC/60、非空、主键；
     */
    public static final String SQID = "SQID";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = SQID + " ASC";
    /**
     * 社区名称：		SQMC、VC/60、非空；
     */
    public static final String SQMC = "SQMC";
    /**
     * 所属单位名称：	SSDWMC、VC/60、非空、社区所属派出所名称；
     */
    public static final String SSDWMC = "SSDWMC";
    /**
     * 所属单位ID：		SSDWID、VC/60、非空、社区所属派出所ID；
     */
    public static final String SSDWID = "SSDWID";
    /**
     * 导入时间：		DRSJ、DATE；
     */
    public static final String DRSJ = "DRSJ";

}
