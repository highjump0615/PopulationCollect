/**
 * @author LongHu
 * @date 2014.05.14
 * @filename AnagramColumns.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;

/**
 * <b>来京人员信息表</b>
 * <p/>
 * <p>保存来京人员信息，由民警或出租屋房主采集录入维护。</p>
 */
public class LaiJingRenYuanColumns extends ShangChuanColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.LAIJINGRENYUANXINXI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.ljryxx";


    /**
     * 人员ID ：		  RYID、VC/60、非空、主键
     */
    public static final String RYID = "RYID";
    /**
     * *房屋ID ：		FWID、VC/60、非空、主键、创建记录时系统生成
     */
    public static final String FWID = "FWID";
    /**
     * *所在地（辖区）：	SZDXQ、VC/60、非空、动态字典项录入
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
     * *填表日期：		TBRQ、VC/600、非空、YYYY-MM-DD、录入时的当天日期
     */
    public static final String TBRQ = "TBRQ";
    /**
     * *姓名：			XM、VC/60、非空、来京人员姓名
     */
    public static final String XM = "XM";
    /**
     * *身份证号码：		SFZHM、VC/60、非空、来京人员身份证号码
     */
    public static final String SFZHM = "SFZHM";
    /**
     * 是否暂无身份证号：SFZWSFZH、VC/60、0 – 否（默认）、1 – 是（此时身份证号用“空格”代替）
     */
    public static final String SFZWSFZH = "SFZWSFZH";
    /**
     * *出生日期：		CSRQ、VC/600、非空、YYYY-MM-DD
     */
    public static final String CSRQ = "CSRQ";
    /**
     * *性别：			XB、VC/60、非空、静态字典项录入
     */
    public static final String XB = "XB";
    /**
     * *民族：			MZ、VC/60、非空、静态字典项录入
     */
    public static final String MZ = "MZ";
    /**
     * *民族详细信息：			MZXXXX、VC/60、非空、静态字典项录入
     */
    public static final String MZXXXX = "MZXXXX";
    /**
     * *政治面貌：		ZZMM、VC/60、非空、静态字典项录入
     */
    public static final String ZZMM = "ZZMM";
    /**
     * *受教育程度：		SJYCD、VC/60、非空、静态字典项录入
     */
    public static final String SJYCD = "SJYCD";
    /**
     * *户籍类别：		HJLB、VC/60、非空、静态字典项录入
     */
    public static final String HJLB = "HJLB";
    /**
     * *婚姻状况：		HYZK、VC/60、非空、静态字典项录入
     */
    public static final String HYZK = "HYZK";
    /**
     * 联系电话：		LXDH、VC/60
     */
    public static final String LXDH = "LXDH";
    /**
     * *户籍地址省：		HJDZS、VC/60、非空、静态字典项录入
     */
    public static final String HJDZS = "HJDZS";
    /**
     * 户籍地址市：		HJDZSHI、VC/60、动态字典项录入（依据省动态加载）
     */
    public static final String HJDZSHI = "HJDZSHI";
    /**
     * 户籍地址区（县）：	HJDZQX、VC/60、动态字典项录入（依据市动态加载）
     */
    public static final String HJDZQX = "HJDZQX";
    /**
     * *户籍地址街道：	HJDZJD、VC/60、动态字典项录入（依据区县动态加载）
     */
    public static final String HJDZJD = "HJDZJD";
    /**
     * *户籍详细地址：	HJXXDZ、VC/60、非空
     */
    public static final String HJXXDZ = "HJXXDZ";
    /**
     * 出生地：			CSD、VC/60
     */
    public static final String CSD = "CSD";
    /**
     * *居住证件：		JZZJ、VC/60、非空、静态字典项录入、0 – 无、1 – 有
     */
    public static final String JZZJ = "JZZJ";
    /**
     * *婚育证明：		HYZM、VC/60、非空、静态字典项录入
     */
    public static final String HYZM = "HYZM";
    /**
     * *免疫接种证(18岁以下)  ：MYJZZ、VC/60、非空、静态字典项录入
     */
    public static final String MYJZZ = "MYJZZ";
    /**
     * *家庭户流入：		JTHLR、VC/60、非空、静态字典项录入
     */
    public static final String JTHLR = "JTHLR";
    /**
     * *是否户主：		SFHZ、VC/60、非空、静态字典项录入
     */
    public static final String SFHZ = "SFHZ";
    /**
     * *本户外来人口数总数：BHWLRKZS、VC/60、非空、整数
     */
    public static final String BHWLRKZS = "BHWLRKZS";
    /**
     * *本户外来人口数16岁以下总数：BHWLRK16、VC/60、非空、整数
     */
    public static final String BHWLRK16 = "BHWLRK16";
    /**
     * *本户外来人口其中男总数：BHWLRKNAN、VC/60、非空、整数
     */
    public static final String BHWLRKNAN = "BHWLRKNAN";
    /**
     * *本户外来人口其中女总数：BHWLRKNV、VC/60、非空、整数
     */
    public static final String BHWLRKNV = "BHWLRKNV";
    /**
     * *与户主关系：		YHZGX、VC/60；
     */
    public static final String YHZGX = "YHZGX";
    /**
     * *与户主详细关系：		YHZXXGX、VC/60；
     */
    public static final String YHZXXGX = "YHZXXGX";
    /**
     * *户主登记表序号：	HZDJBXH、VC/60、户主的来京人员信息表记录中的“登记表序号”
     */
    public static final String HZDJBXH = "HZDJBXH";
    /**
     * 离开原籍日期：	LKYJRQ、VC/600、YYYY-MM-DD
     */
    public static final String LKYJRQ = "LKYJRQ";
    /**
     * *来京日期：		LJRQ、VC/600、非空、YYYY-MM-DD
     */
    public static final String LJRQ = "LJRQ";
    /**
     * *来现住地日期：	LXZDRQ、VC/600、非空、YYYY-MM-DD
     */
    public static final String LXZDRQ = "LXZDRQ";
    /**
     * *来京原因：		LJYY、VC/60、非空、静态字典项录入
     */
    public static final String LJYY = "LJYY";
    /**
     * *来京其他详细原因：		LJQTXXYY、VC/60、非空、静态字典项录入
     */
    public static final String LJQTXXYY = "LJQTXXYY";
    /**
     * *居住类型：		JZLX、VC/60、非空、静态字典项录入
     */
    public static final String JZLX = "JZLX";
    /**
     * *居住其他类型：	JZQTLX、VC/60
     */
    public static final String JZQTLX = "JZQTLX";
    /**
     * *房屋登记表序号：	FWDJBXH、VC/60、租住房屋的“登记表序号”，与房屋关联
     */
    public static final String FWDJBXH = "FWDJBXH";
    /**
     * 现住地分局：		XZDFJ、VC/60、非空、静态数据（从出租屋相关信息获取）
     */
    public static final String XZDFJ = "XZDFJ";
    /**
     * 现住地派出所：	XZDPCS、VC/60、非空、静态数据（从出租屋相关信息获取）
     */
    public static final String XZDPCS = "XZDPCS";
    /**
     * 现住地详址社区：	XZDXZSQ、VC/60、非空、静态数据（从出租屋相关信息获取）
     */
    public static final String XZDXZSQ = "XZDXZSQ";
    /**
     * 现住地详地街道：	XZDXZJD、VC/60、非空、静态数据（从出租屋相关信息获取）
     */
    public static final String XZDXZJD = "XZDXZJD";
    /**
     * *现住地详址自填：	XZDXZZT、VC/500、非空、静态数据（从出租屋相关信息获取）
     */
    public static final String XZDXZZT = "XZDXZZT";
    /**
     * *所属派出所名称：	SSPCSMC、VC/60、非空、静态数据（从出租屋相关信息获取）
     */
    public static final String SSPCSMC = "SSPCSMC";
    /**
     * *民警姓名：		MJXM、VC/60、非空
     */
    public static final String MJXM = "MJXM";
    /**
     * *目前状况：		MQZK、VC/60、非空、静态字典项录入
     */
    public static final String MQZK = "MQZK";
    /**
     * *就业单位名称：	JYDWMC、VC/60
     */
    public static final String JYDWMC = "JYDWMC";
    /**
     * 单位登记表序号：	DWDJBXH、VC/60
     */
    public static final String DWDJBXH = "DWDJBXH";
    /**
     * 就业单位详细地址：JYDWXXDZ、VC/500
     */
    public static final String JYDWXXDZ = "JYDWXXDZ";
    /**
     * *就业单位所属行业：JYDWSSHY、VC/60、静态字典项录入
     */
    public static final String JYDWSSHY = "JYDWSSHY";
    /**
     * *就业单位所属其他行业：QTHY、VC/60、静态字典项录入
     */
    public static final String QTHY = "QTHY";
    /**
     * *主要从事工作：	ZYCSGZ、VC/60
     */
    public static final String ZYCSGZ = "ZYCSGZ";
    /**
     * *主要从事其他工作（描述）：	QTZYCSGZMS、VC/60
     */
    public static final String QTZYCSGZMS = "QTZYCSGZMS";
    /**
     * *职业：			ZY、VC/60
     */
    public static final String ZY = "ZY";
    /**
     * *签订劳动合同：	QDLDHT、VC/60
     */
    public static final String QDLDHT = "QDLDHT";
    /**
     * *就业单位所在地（区/县）：JYDWSZD、VC/60
     */
    public static final String JYDWSZD = "JYDWSZD";
    /**
     * *在京参加社会保险：ZJCJSHBX、VC/60、（无、养老、失业、医疗、工伤、生育）多选
     */
    public static final String ZJCJSHBX = "ZJCJSHBX";
    /**
     * *参加社会保险：	CJSHBX、VC/60、（无、养老、失业、医疗、工伤、生育）多选
     */
    public static final String CJSHBX = "CJSHBX";
    /**
     * *学校名称：		XXMC、VC/60
     */
    public static final String XXMC = "XXMC";
    /**
     * 学校所在地：		XXSZD、VC/500
     */
    public static final String XXSZD = "XXSZD";
    /**
     * *目前状况详细信息：MQZKXXXX、VVC/60000
     */
    public static final String MQZKXXXX = "MQZKXXXX";
    /**
     * 备注：			BZ、VVC/60000
     */
    public static final String BZ = "BZ";
    /**
     * 照片URL：		ZPURL、VC/500、多张照片URL用逗号分隔
     */
    public static final String ZPURL = "ZPURL";
    /**
     * 所属房屋信息ID：    SSFWXXID、非空、VC60、与FWID同一
     */
    public static final String SSFWXXID = "SSFWXXID";
    /**
     * MD5
     */
    public static final String MD5 = "MD5";
    /**
     * Sort order
     */
    public static final String SORT_ORDER_DEFAULT = RYID + " ASC";

}
