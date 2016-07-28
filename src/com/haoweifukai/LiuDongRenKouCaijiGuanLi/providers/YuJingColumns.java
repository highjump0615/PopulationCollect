package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>预警信息表</b>
 * <br>
 * 保存预警比对匹配记录，以备预警信息统计。
 */
public class YuJingColumns implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.YUJINGXINXI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.yjxx";

    /**
     * 预警信息ID：		YJXXID、VC/60、非空、主键；
     */
    public static final String YJXXID = "YJXXID";
    /**
     * 黑名单人员ID：	HMDRYID、VC/60、非空、黑名单人员唯一标识；
     */
    public static final String HMDRYID = "HMDRYID";
    /**
     * 来京人员人员ID：	LJRYRYID、VC/60、非空、来京人员唯一标识；
     */
    public static final String LJRYRYID = "LJRYRYID";
    /**
     * 比对身份证号：	BDSFZH、VC/60、非空；
     */
    public static final String BDSFZH = "BDSFZH";
    /**
     * 比对时间：		BDSJ、DATE、非空；
     */
    public static final String BDSJ = "BDSJ";
    /**
     * 是否查看：		SFCK、VC/60、0 – 未查看（默认）、1 – 已查看；
     */
    public static final String SFCK = "SFCK";
    /**
     * 查看人ID：		CKRID、VC/60；
     */
    public static final String CKRID = "CKRID";
    /**
     * 查看人警号：		CKRJH、VC/60；
     */
    public static final String CKRJH = "CKRJH";
    /**
     * 查看人姓名：		CKRXM、VC/60；
     */
    public static final String CKRXM = "CKRXM";
    /**
     * 查看人单位ID：	CKRDWID、VC/60、非空、录入来京人员民警单位ID；
     */
    public static final String CKRDWID = "CKRDWID";
    /**
     * 查看人单位名称：	CKRDWMC、VC/60；
     */
    public static final String CKRDWMC = "CKRDWMC";
    /**
     * 查看时间：		CKSJ、DATE；
     */
    public static final String CKSJ = "CKSJ";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = YJXXID + " ASC";

}
