package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>操作日志信息表</b>
 * <br>
 * 保存系统操作人员操作使用系统功能记录。
 */
public class CaoZuoRiZhiColumns implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.CAOZUORIZHI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.czrz";

    /**
     * 操作信息ID：		CZXXID、VC/60、非空、主键；
     */
    public static final String CZXXID = "CZXXID";
    /**
     * 操作人ID：		CZRID、VC/60、非空、操作人的民警ID或房主ID；
     */
    public static final String CZRID = "CZRID";
    /**
     * 操作人警号：		CZRJH、VC/60、操作人是警员时才有；
     */
    public static final String CZRJH = "CZRJH";
    /**
     * 操作人姓名：		CZRXM、VC/60、非空、操作人姓名；
     */
    public static final String CZRXM = "CZRXM";
    /**
     * 所属单位ID：		SSDWID、VC/60、操作人是警员时才有，警员的单位ID；
     */
    public static final String SSDWID = "SSDWID";
    /**
     * 所属单位名称：	SSDWMC、VC/60、操作人是民警时才有，警员的单位名称；
     */
    public static final String SSDWMC = "SSDWMC";
    /**
     * 操作模块：		CZMK、VC/60、非空、在程序中设定；
     */
    public static final String CZMK = "CZMK";
    /**
     * 操作数据ID：		CZSJID、VC/60、数据记录ID、没有时为空；
     */
    public static final String CZSJID = "CZSJID";
    /**
     * 操作数据类型：	CZSJLX、VC/60、在程序中设定、没有时为空；
     */
    public static final String CZSJLX = "CZSJLX";
    /**
     * 操作描述：		CZMS、VC/60、非空、在程序中设定；
     */
    public static final String CZMS = "CZMS";
    /**
     * 操作时间：		CZSJ、DATE、非空、执行操作的时间；
     */
    public static final String CZSJ = "CZSJ";
    /**
     * 操作人类型：		CZRLX、VC/60、非空、在程序中设定；
     */
    public static final String CZRLX = "CZRLX";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = CZSJ + " DESC";

}
