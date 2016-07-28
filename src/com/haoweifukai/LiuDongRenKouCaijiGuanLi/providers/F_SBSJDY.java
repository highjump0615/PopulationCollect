package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>交换数据定义表</b>
 * <br>
 * 保存手持机与出租公寓管理系统数据交换时数据记录中数据字段及顺序。
 */
public class F_SBSJDY implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.JIAOHUANSHUJUDINGYI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.sbsjdy";

    /**
     * 数据交换功能：	SJJHGN、VC/60、非空、主键；
     */
    public static final String SJJHGN = "SJJHGN";

    /**
     * 数据记录字段：	SJJLZD、VC/500、数据交换数据记录中的数据字段名及顺序（多个数据字段名用“,”逗号分隔）；
     */
    public static final String SJJLZD = "SJJLZD";

    /**
     * 记录数据表名：	JLSJBM、VC/60、要保存数据记录的数据库表；
     */
    public static final String JLSJBM = "JLSJBM";

}
