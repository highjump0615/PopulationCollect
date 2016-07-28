package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>街路巷表</b>
 * <br>
 * 保存街路巷信息。
 */
public class C_JLX implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.JIELUXIANG_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.jlx";

    /**
     * 街路巷ID：		JLXID、VC/60、非空、主键；
     */
    public static final String JLXID = "JLXID";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = JLXID + " ASC";
    /**
     * 街路巷名称：		JLXMC、VC/60、非空；
     */
    public static final String JLXMC = "JLXMC";
    /**
     * 归属社区ID：		GSSQID、VC/60、所属辖区的社区ID；
     */
    public static final String GSSQID = "GSSQID";
    /**
     * 导入时间：		DRSJ、DATE；
     */
    public static final String DRSJ = "DRSJ";

}
