/**
 * @author LuYongXing
 * @date 2014.08.22
 * @filename FangWuColumns.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;

/**
 * <b>房屋信息表</b>
 * <p/>
 * <p>保存出租房屋信息，由民警采集录入维护，包括房主信息。</p>
 */
public class FangWuColumns extends ShangChuanColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.FANGWUXINXI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.fwxx";


    /**
     * *房屋ID ：		FWID、VC/60、非空、主键、创建记录时系统生成
     */
    public static final String FWID = "FWID";
    /**
     * *房主ID ：		FZID、VC/60、非空、主键
     */
    //public static final String FZID = "FZID";
    /**
     * *所在地（辖区）：	SZDXQ、VC/60、非空、动态字典项录入；
     */
    public static final String SZDXQ = "SZDXQ";
    /**
     * *管理员姓名：		GLYXM、VC/60、非空、动态字典项录入（与辖区关联）
     */
    public static final String GLYXM = "GLYXM";
    /**
     * *管理员编号：		GLYBH、VC/60、非空、动态字典项录入（与辖区关联）
     */
    public static final String GLYBH = "GLYBH";
    /**
     * *登记表序号：      DJBXH、VC/60、非空、创建记录时系统生成
     */
    public static final String DJBXH = "DJBXH";
    /**
     * *填表日期：		TBRQ、C/10、非空、YYYY-MM-DD、录入时的当天日期
     */
    public static final String TBRQ = "TBRQ";
    /**
     * *房屋类型：		FWLX、VC/60、非空、静态字典项录入
     */
    public static final String FWLX = "FWLX";
    /**
     * *建设性质：		JSXZ、VC/60、非空、静态字典项录入
     */
    public static final String JSXZ = "JSXZ";
    /**
     * 所在地址分局：	    SZDZFJ、VC/60、非空、静态数据（从录入数据警员相关信息中获取）
     */
    public static final String SZDZFJ = "SZDZFJ";
    /**
     * 所在地址派出所：	SZDZPCS、VC/60、非空、静态数据（从录入数据警员相关信息中获取）
     */
    public static final String SZDZPCS = "SZDZPCS";
    /**
     * *所在地详址社区：	SZDXZSQ、VC/60、非空、动态字典项录入
     */
    public static final String SZDXZSQ = "SZDXZSQ";
    /**
     * *所在地详址街道：	SZDXZJD、VC/60、非空、动态字典项录入
     */
    public static final String SZDXZJD = "SZDXZJD";
    /**
     * *所在地详址自填：	SZDXZZT、VC/500、非空
     */
    public static final String SZDXZZT = "SZDXZZT";
    /**
     * *建筑类型：		JZLX、VC/60、非空、静态字典项录入
     */
    public static final String JZLX = "JZLX";
    /**
     * 房产证号：		   FCZH、VC/60
     */
    public static final String FCZH = "FCZH";
    /**
     * *所属派出所名称：	SSPCSMC、VC/60、非空、静态数据（从录入数据警员相关信息中获取）
     */
    public static final String SSPCSMC = "SSPCSMC";
    /**
     * *民警姓名：		MJXM、VC/60、非空、静态数据（从录入数据警员相关信息中获取）
     */
    public static final String MJXM = "MJXM";
    /**
     * *所有权类型：		SYQLX、VC/60、非空、静态字典项录入
     */
    public static final String SYQLX = "SYQLX";
    /**
     * *出租人类型：		CZRLX、VC/60、非空、静态字典项录入
     */
    public static final String CZRLX = "CZRLX";
    /**
     * *姓名：			XM、VC/60、非空、房主姓名
     */
    public static final String XM = "XM";
    /**
     * 证件类别：		ZJLB、VC/60、静态字典项录入
     */
    public static final String ZJLB = "ZJLB";
    /**
     * 证件号码：		ZJHM、VC/60
     */
    public static final String ZJHM = "ZJHM";
    /**
     * *性别：			XB、VC/60、非空、静态字典项录入
     */
    public static final String XB = "XB";
    /**
     * 出生日期：		CSRQ、C/10、YYYY-MM-DD
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
     * *出租间数：		CZJS、VC/60、非空（数值）
     */
    public static final String CZJS = "CZJS";
    /**
     * *出租面积：		CZMJ、VC/60、非空（数值）
     */
    public static final String CZMJ = "CZMJ";
    /**
     * *房屋层数：		FWCS、VC/60、非空、静态字典项录入
     */
    public static final String FWCS = "FWCS";
    /**
     * *安全隐患         AQYH、VC/60、非空、静态字典项目录入（单选）
     */
    public static final String AQYH = "AQYH";
    /**
     * *出租用途：		CZYT、VC/60、非空、静态字典项录入
     */
    public static final String CZYT = "CZYT";
    /**
     * *其他出租用途：		QTCZYT、VC/60、非空、静态字典项录入
     */
    public static final String QTCZYT = "QTCZYT";
    /**
     * *租住人员数量本市：ZZRYSLBS、VC/60、非空（整数）
     */
    public static final String ZZRYSLBS = "ZZRYSLBS";
    /**
     * *租住人员数量外省市：ZZRYSLWSS、VC/60、非空（整数）
     */
    public static final String ZZRYSLWSS = "ZZRYSLWSS";
    /**
     * *租住人员数量港澳台：ZZRYSLGAT、VC/60、非空（整数）
     */
    public static final String ZZRYSLGAT = "ZZRYSLGAT";
    /**
     * *租住人员数量外籍：ZZRYSLWJ、VC/60、非空（整数）
     */
    public static final String ZZRYSLWJ = "ZZRYSLWJ";
    /**
     * *租金方式：		ZJFS、VC/60、非空、静态字典项录入（单选）
     */
    public static final String ZJFS = "ZJFS";
    /**
     * *租金：			ZJ、VC/60、非空（数值）
     */
    public static final String ZJ = "ZJ";
    /**
     * *租赁合同：		ZLHT、VC/60、非空、静态字典项录入（单选）
     */
    public static final String ZLHT = "ZLHT";
    /**
     * *纳税：			NS、VC/60、非空、静态字典项录入（单选）
     */
    public static final String NS = "NS";
    /**
     * 登记备案：		DJBA、VC/60、静态字典项录入
     */
    public static final String DJBA = "DJBA";
    /**
     * *签订责任书：		QDZRS、VC/60、非空、静态字典项录入（多选）
     */
    public static final String QDZRS = "QDZRS";
    /**
     * 签订责任书日期：	QDZRSRQ、C/10、YYYY-MM-DD
     */
    public static final String QDZRSRQ = "QDZRSRQ";
    /**
     * *出租起始日期：	CZQSRQ、C/10、非空、YYYY-MM-DD
     */
    public static final String CZQSRQ = "CZQSRQ";
    /**
     * 出租截止日期：	CZJZRQ、C/10、YYYY-MM-DD
     */
    public static final String CZJZRQ = "CZJZRQ";
    /**
     * 备注：			BZ、VC/1000
     */
    public static final String BZ = "BZ";
    /**
     * *群组房类型：		QZFLX、VC/60、静态字典项录入
     */
    public static final String QZFLX = "QZFLX";
    /**
     * *其它房屋类型：	QTFWLX、VC/60、静态字典项录入
     */
    public static final String QTFWLX = "QTFWLX";
    /**
     * *其它建设性质：	QTJSXZ、VC/60、静态字典项录入
     */
    public static final String QTJSXZ = "QTJSXZ";
    /**
     * *其它建筑类型：	QTJZLX、VC/60、静态字典项录入
     */
    public static final String QTJZLX = "QTJZLX";
    /**
     * *其它证件类别：	QTZJ、VC/60、静态字典项录入
     */
    public static final String QTZJ = "QTZJ";
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
    /**
     * 国籍(地区)：	GJDQ、VC/60
     */
    public static final String GJDQ = "GJDQ";
    /**
     * *中介公司名称：	ZJGSMC、VC/60
     */
    public static final String ZJGSMC = "ZJGSMC";
    /**
     * *中介公司联系电话：ZJGSLXDH、VC/60
     */
    public static final String ZJGSLXDH = "ZJGSLXDH";
    /**
     * *房屋负责人姓名：	FWFZRXM、VC/60
     */
    public static final String FWFZRXM = "FWFZRXM";
    /**
     * 房屋负责人身份证：FWFZRSFZ、VC/60
     */
    public static final String FWFZRSFZ = "FWFZRSFZ";
    /**
     * *转租人姓名：		ZZRXM、VC/60
     */
    public static final String ZZRXM = "ZZRXM";
    /**
     * 转租人证件类别：	ZZRZJLB、VC/60
     */
    public static final String ZZRZJLB = "ZZRZJLB";
    /**
     * 转租人其他证件类别：	ZZRQTZJLB、VC/60
     */
    public static final String ZZRQTZJLB = "ZZRQTZJLB";
    /**
     * 转租人证件号码：	ZZRZJHM、VC/60
     */
    public static final String ZZRZJHM = "ZZRZJHM";
    /**
     * *转租人联系电话：	ZZRLXDH、VC/60
     */
    public static final String ZZRLXDH = "ZZRLXDH";
    /**
     * 转租人现住地址：	ZZRXZDZ、VC/60
     */
    public static final String ZZRXZDZ = "ZZRXZDZ";
    /**
     * 照片URL：		ZPURL、VC/500、多张照片URL用逗号分隔
     */
    public static final String ZPURL = "ZPURL";
    /**
     * 摄像URL：		SXURL、VC/500、多个摄像URL用逗号分隔
     */
    public static final String SXURL = "SXURL";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = FWID + " ASC";

}
