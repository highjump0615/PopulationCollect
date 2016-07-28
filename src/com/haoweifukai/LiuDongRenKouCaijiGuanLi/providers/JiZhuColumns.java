package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>手持机机主信息</b>
 * <br>
 * 保存手持机领用使用警员的基本信息，派发手持机时进行设置。
 */
public class JiZhuColumns implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.JIZHUXINXI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.jzxx";

    /**
     * 民警ID：			MJID、VC/60、非空；
     */
    public static final String MJID = "MJID";
    /**
     * 民警警号：		MJJH、VC/60、非空；
     */
    public static final String MJJH = "MJJH";
    /**
     * 民警姓名：		MJXM、VC/60、非空；
     */
    public static final String MJXM = "MJXM";
    /**
     * 所属单位ID：		SSDWID、VC/60、非空；
     */
    public static final String SSDWID = "SSDWID";
    /**
     * 所属单位名称：	SSDWMC、VC/60、非空；
     */
    public static final String SSDWMC = "SSDWMC";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = MJID + " ASC";

}
