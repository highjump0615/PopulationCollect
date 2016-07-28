package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>黑名单</b>
 * <br>
 * 保存黑名单人员信息，从公安内部系统导入，且从出租公寓管理系统下载到手持机上。
 */
public class HeiMingDanColumns implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.HEIMINGDAN_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.hmd";

    /**
     * 人员ID：			RYID、VC/60、非空、主键；
     */
    public static final String RYID = "RYID";
    /**
     * 人员姓名：		RYXM、VC/60、非空；
     */
    public static final String RYXM = "RYXM";
    /**
     * 身份证号：		SFZH、VC/60、非空；
     */
    public static final String SFZH = "SFZH";
    /**
     * 人员类型：		RYLX、VC/60、非空、代码字段（261920115 – 在逃、179114115 – 前科、268151474 – 重点人）；
     */
    public static final String RYLX = "RYLX";
    /**
     * 数据获取时间：	SJHQSJ、DATE、非空；
     */
    public static final String SJHQSJ = "SJHQSJ";
    /**
     * 导入时间：		DRSJ、DATE、非空；
     */
    public static final String DRSJ = "DRSJ";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = RYID + " ASC";

}
