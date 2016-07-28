package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>社区管理员表</b>
 * <br>
 * 保存出租房屋所在地辖区（社区）管理员信息。
 */
public class C_SQGLY implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.SHEQUGUANLIYUAN_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.sqgly";

    /**
     * AutoIncrement, Primary Key
     */
    public static final String ID = "ID";

    /**
     * 管理员编号：		GLYBH、VC/60、非空、主键；
     */
    public static final String GLYBH = "GLYBH";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = GLYBH + " ASC";
    /**
     * 管理员姓名：		GLYXM、VC/60、非空；
     */
    public static final String GLYXM = "GLYXM";
    /**
     * 社区ID：			SQID、VC/60、非空、管理员管理辖区；
     */
    public static final String SQID = "SQID";
    /**
     * 导入时间：		DRSJ、DATE；
     */
    public static final String DRSJ = "DRSJ";

}
