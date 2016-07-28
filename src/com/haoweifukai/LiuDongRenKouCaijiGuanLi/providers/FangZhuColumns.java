/**
 * @author LuYongXing
 * @date 2014.08.22
 * @filename FangZhuColumns.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;

/**
 * <b>房主信息表</b>
 * <p/>
 * <p>保存出租房屋房主信息，由民警录入维护。</p>
 */
public class FangZhuColumns extends ShangChuanColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.FANGZHUXINXI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.fzxx";


    /**
     * *房主ID ：		FZID、VC/60、非空、主键
     */
    public static final String FZID = "FZID";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = FZID + " ASC";
    /**
     * *姓名：			XM、VC/60、非空、房主姓名
     */
    public static final String XM = "XM";
    /**
     * 证件类别：		ZJLB、VC/60、静态字典项录入
     */
    public static final String ZJLB = "ZJLB";
    /**
     * *其它证件类别：	QTZJ、VC/60、静态字典项录入
     */
    public static final String QTZJ = "QTZJ";
    /**
     * 证件号码：		ZJHM、VC/60
     */
    public static final String ZJHM = "ZJHM";
    /**
     * *性别：			XB、VC/60、非空、静态字典项录入
     */
    public static final String XB = "XB";
    /**
     * 出生日期：		CSRQ、VC/600、YYYY-MM-DD
     */
    public static final String CSRQ = "CSRQ";
    /**
     * *户籍地：			HJD、VC/60、非空、静态字典项录入
     */
    public static final String HJD = "HJD";
    /**
     * 政治面貌：		ZZMM、VC/60、静态字典项录入
     */
    public static final String ZZMM = "ZZMM";
    /**
     * *联系电话：		LXDH、VC/60、非空
     */
    public static final String LXDH = "LXDH";
    /**
     * 现住地址省：		XZDZS、VC/60、动态字典项录入
     */
    public static final String XZDZS = "XZDZS";
    /**
     * 现住地址市：		XZDZSHI、VC/60、动态字典项录入
     */
    public static final String XZDZSHI = "XZDZSHI";
    /**
     * 现居住地详细地址：XJZDXXDZ、VC/200
     */
    public static final String XJZDXXDZ = "XJZDXXDZ";
    /**
     * 户籍地址省：		HJDZS、VC/60、动态字典项录入
     */
    public static final String HJDZS = "HJDZS";
    /**
     * 户籍地址市：		HJDZSHI、VC/60、动态字典项录入
     */
    public static final String HJDZSHI = "HJDZSHI";
    /**
     * 户籍地详细地址：	HJDXXDZ、VC/200
     */
    public static final String HJDXXDZ = "HJDXXDZ";
    /**
     * 国籍(地区)：		GJDQ、VC/60
     */
    public static final String GJDQ = "GJDQ";
    /**
     * *所有权类型：		SYQLX、VC/60、非空、静态字典项录入
     */
    public static final String SYQLX = "SYQLX";
    /**
     * *单位名称：		DWMC、VC/60
     */
    public static final String DWMC = "DWMC";
    /**
     * 负责人姓名：		FZRXM、VC/60
     */
    public static final String FZRXM = "FZRXM";
    /**
     * *单位联系电话：	DWLXDH、VC/60
     */
    public static final String DWLXDH = "DWLXDH";
    /**
     * 所在地址省：		SZDZS、VC/60
     */
    public static final String SZDZS = "SZDZS";
    /**
     * 所在地址市：		SZDZSHI、VC/60
     */
    public static final String SZDZSHI = "SZDZSHI";
    /**
     * 所在地详细地址：	SZDXXDZ、VC/500
     */
    public static final String SZDXXDZ = "SZDXXDZ";

}
