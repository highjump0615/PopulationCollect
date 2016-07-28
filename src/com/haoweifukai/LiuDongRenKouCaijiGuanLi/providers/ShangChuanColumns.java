/**
 * @author LuYongXing
 * @date 2014.08.22
 * @filename FangWuColumns.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.provider.BaseColumns;

/**
 * <b>上传信息表</b>
 * <p/>
 * <p>保存录入、修改、删除信息。</p>
 */
public abstract class ShangChuanColumns implements BaseColumns {

    /**
     * 录入民警警号：	LRMJJH、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String LRMJJH = "LRMJJH";

    /**
     * 录入民警ID：		LRMJID、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String LRMJID = "LRMJID";

    /**
     * 录入民警姓名：	LRMJXM、VC/60、自动从录入数据警员相关信息中获取；
     */
    public static final String LRMJXM = "LRMJXM";

    /**
     * 录入单位ID：		LRDWID、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String LRDWID = "LRDWID";

    /**
     * 录入单位名称：	LRDWMC、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String LRDWMC = "LRDWMC";

    /**
     * 录入时间：		LRSJ、DATE、非空
     */
    public static final String LRSJ = "LRSJ";

    /**
     * 修改民警警号：	XGMJJH、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String XGMJJH = "XGMJJH";

    /**
     * 修改民警ID：		XGMJID、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String XGMJID = "XGMJID";

    /**
     * 修改民警姓名：	XGMJXM、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String XGMJXM = "XGMJXM";

    /**
     * 修改单位ID：		XGDWID、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String XGDWID = "XGDWID";

    /**
     * 修改单位名称：	XGDWMC、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String XGDWMC = "XGDWMC";

    /**
     * 修改时间：		XGSJ、DATE
     */
    public static final String XGSJ = "XGSJ";

    /**
     * 删除民警警号：	SCMJJH、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String SCMJJH = "SCMJJH";

    /**
     * 删除民警ID：		SCMJID、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String SCMJID = "SCMJID";

    /**
     * 删除民警姓名：	SCMJXM、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String SCMJXM = "SCMJXM";

    /**
     * 删除单位ID：		SCDWID、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String SCDWID = "SCDWID";

    /**
     * 删除单位名称：	SCDWMC、VC/60、自动从录入数据警员相关信息中获取
     */
    public static final String SCDWMC = "SCDWMC";

    /**
     * 删除时间：		SCSJ、DATE
     */
    public static final String SCSJ = "SCSJ";

    /**
     * 是否校准：		SFJZ、C/1、0 – 否、1 – 是
     */
    public static final String SFJZ = "SFJZ";

    /**
     * 是否删除：		SFSC、C/1、非空、1 – 已删除、0 – 未删除（默认）
     */
    public static final String SFSC = "SFSC";

    /**
     * 录入设备：		LRSB、C/1、非空、0 - 公寓（默认）、1 - 手持机、2 - 流管办系统
     */
    public static final String LRSB = "LRSB";

    /**
     * 是否上传至服务器：SFSCZFWQ、C/1、非空、0 – 未上传（默认）、1 – 已上传、所有手持机上录入的数据初始状态为0（未上传），待执行上报数据并且接收到返回信息确认上报成功后，该数据记录状态改为1（已上传）
     */
    public static final String SFSCZFWQ = "SFSCZFWQ";

    /**
     * 是否上传至核录：	SFSCZHL、C/1、非空、0 – 未上传（默认）、1 – 已上传
     */
    public static final String SFSCZHL = "SFSCZHL";

    /**
     * 是否上传至流管办：SFSCZLGB、C/1、非空、0 – 未上传（默认）、1 – 已上传
     */
    public static final String SFSCZLGB = "SFSCZLGB";

}
