/**
 * @author LuYongXing
 * @date 2014.08.22
 * @filename FangZhuColumns.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;

/**
 * <b>车辆信息表</b>
 * <p/>
 * <p>保存车辆信息，由民警录入维护。</p>
 */
public class CheLiangColumns extends ShangChuanColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.CHELIANGXINXI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.clxx";

    /**
     * *车辆ID ：		CLID、VC/60、非空、主键
     */
    public static final String CLID = "CLID";
    /**
     * 车牌照：			CPH、VC/60、非空、车辆牌号
     */
    public static final String CPH = "CPH";
    /**
     * 车身颜色：	       	CSYS、VC/60、静态字典项录入
     */
    public static final String CSYS = "CSYS";
    /**
     * 品牌：          	PP、VC/60、静态字典项录入
     */
    public static final String PP = "PP";
    /**
     * 型号：	      	XH、VC/60
     */
    public static final String XH = "XH";
    /**
     * 车辆类型（轿车、SUV、MPV、商务车、皮卡、跑车）：	XB、VC/60、非空、静态字典项录入
     */
    public static final String CLLX = "CLLX";
    /**
     * 车辆描述：	       	CLMS、VC/500、YYYY-MM-DD
     */
    public static final String CLMS = "CLMS";
    /**
     * 驾驶人姓名：		JSRXM、VC/60、非空、静态字典项录入
     */
    public static final String JSRXM = "JSRXM";
    /**
     * 驾驶人身份证号：	JSRSFZH、VC/60、静态字典项录入
     */
    public static final String JSRSFZH = "JSRSFZH";
    /**
     * 驾驶人驾照号码：	JSRJZHM、VC/60、非空
     */
    public static final String JSRJZHM = "JSRJZHM";
    /**
     * 车辆行驶证号码：	CLXSZHM、VC/60、动态字典项录入
     */
    public static final String CLXSZHM = "CLXSZHM";
    /**
     * 所在区县：	       	SZQX、VC/60、动态字典项录入
     */
    public static final String SZQX = "SZQX";
    /**
     * 所在街道：        SZJD、VC/60
     */
    public static final String SZJD = "SZJD";
    /**
     * 所属派出所：		SSPCS、VC/60、动态字典项录入
     */
    public static final String SSPCS = "SSPCS";
    /**
     * 详细地址：	       	XXDZ、VC/500
     */
    public static final String XXDZ = "XXDZ";
    /**
     * 号牌种类：        	HPZL、VC/60
     */
    public static final String HPZL = "HPZL";
    /**
     * 照片URL：   		ZPURL、VC/500、多张照片URL用逗号分隔
     */
    public static final String ZPURL = "ZPURL";
    /**
     * 经度：	      	X、VC/60、车辆停放地址的经度
     */
    public static final String X = "X";
    /**
     * 纬度：	        Y、VC/500、车辆停放地址的维度
     */
    public static final String Y = "Y";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = CLID + " ASC";

}
