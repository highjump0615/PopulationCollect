/**
 * @author LongHu
 * @date 2014.03.27
 * @filename DictionaryContentProvider.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.OwnerData;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MyContentProvider extends ContentProvider {

    private static final String TAG = MyContentProvider.class.getSimpleName();

    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * 数据库名字
     */
    public static final String DATABASE_NAME = "database.sqlite";

    /**
     * 数据库权威
     */
    public static final String AUTHORITY = MyContentProvider.class.getCanonicalName();//"com.lexibook.gb_dictionary_tab.providers.DictionaryContentProvider";

    /**
     * 在房屋信息列表专栏标题
     */
    public static final String FANGWU_TABLE_COMMENTS[] = {
            "*房屋ID",		//0
            "*所在地（辖 区）",	//1
            "*管理员姓名",	//2
            "*管理员编号",	//3
            "*登记表序号",	//4
            "*填表日期",		//5
            "*房屋类型",		//6
            "*建设性质",		//7
            "*其它建设性质",	//96
            "*群租房类型",	//76
            "*其他房屋类型",	//77
            "所在地址分局",	//8
            "所在地址派出所",	//9
            "*所在地详址社区",	//10
            "*所在地详址街道",//11
            "*所在地详址自填",//12
            "*建筑类型",		//13
            "*其它建筑类型",	//97
            "房产证号",		//14
            "*所属派出所名称",	//15
            "*民警姓名",		//16
            "*所有权类型",	//17
            "*出租人类型",	//18
            "*姓名",			//19
            "证件类别",		//20
            "*其他证件",		//98
            "证件号码",		//21
            "*性别",			//22
            "出生日期",		//23
            "*户籍地",		//24
            "政治面貌",		//25
            "*联系电话",		//26
            "现住地址省",		//27
            "现住地址市",		//28
            "现居住地详细地址",//29
            "户籍地址省",		//30
            "户籍地址市",		//31
            "户籍地详细地址",	//32
            "*单位名称",		//78
            "负责人姓名",		//79
            "*单位联系电话",	//80
            "所在地址省",		//81
            "所在地址市",		//82
            "所在地详细地址",	//83
            "*出租间数",		//34
            "*出租面积",		//35
            "*房屋层数",		//36
            "*安全隐患",		//37
            "*出租用途",		//38
            "*其他出租用途",	//99
            "*租住人员数量本市",//39
            "*租住人员数量外省市",//40
            "*租住人员数量港澳台",//41
            "*租住人员数量外籍",	//42
            "*租金方式（年、月）",//43
            "*租金",			//44
            "*租赁合同",		//45
            "*纳税",			//46
            "登记备案",		//47
            "*签订责任书",	//48
            "签订责任书日期",	//49
            "*出租起始日期",	//50
            "出租截止日期",	//51
            "*中介公司名称",	//84
            "*中介公司联系电话",//85
            "*房屋负责人姓名",	//86
            "房屋负责人身份证",//87
            "*转租人姓名",	//88
            "转租人证件类别",	//89
            "*转租人其他证件类别",	//100
            "转租人证件号码",	//90
            "*转租人联系电话",	//91
            "转租人现住地址",	//92
            "备注",			//52
            "录入民警警号",	//53
            "录入民警ID",		//54
            "录入民警名称",	//55
            "录入单位ID",		//56
            "录入单位名称",	//57
            "录入时间",		//58
            "修改民警警号",	//59
            "修改民警ID",		//60
            "修改民警名称",	//61
            "修改单位ID",		//62
            "修改单位名称",	//63
            "修改时间",		//64
            "删除民警警号",	//65
            "删除民警ID",		//66
            "删除民警名称",	//67
            "删除单位ID",		//68
            "删除单位名称",	//69
            "删除时间",		//70
            "是否删除（1是，0否）",//71
            "录入设备（0出租公寓，1采集终端，2流管平台）",	//72
            "是否上传至服务器（1是，0否）",	//73
            "是否上传至核录（1是，0否）",	//74
            "是否上传至流管办（1是，0否）",	//75
            "是否校准（1是，0否）",//93
            "照片URL",		//94
            "摄像URL",		//95
            "国籍（地区）",		//33
    };

    /**
     * 在来京人员信息列表专栏标题
     */
    public static final String LAIJINGRENYUAN_TABLE_COMMENTS[] = {
            "*人员ID", //0
            "*所在地（辖区）", //2
            "*管理员姓名", //3
            "*管理员编号", //4
            "*登记表序号", //5
            "*填表日期", //6
            "*姓名", //7
            "*身份证号码", //8
            "是否暂无身份证号", //9
            "*出生日期", //10
            "*性别", //11
            "*民族", //12
            "*民族详细信息", //90
            "*政治面貌", //13
            "*受教育程度", //14
            "*户籍类别", //15
            "*婚姻状况", //16
            "联系电话", //17
            "*户籍地址省", //18
            "*户籍地址市", //19
            "*户籍地址区（县）", //20
            "*户籍地址街道", //21
            "*户籍详细地址", //22
            "出生地", //23
            "*居住证件", //24
            "*婚育证明", //25
            "*免疫接种证（18岁以下）", //26
            "*家庭户流入", //27
            "*是否户主", //28
            "*与户主关系", //71
            "*与户主详细关系", //91
            "*户主登记表序号", //72
            "*本户外来人口数总数", //29
            "*本户外来人口数16岁以下总数", //30
            "*本户外来人口其中男总数", //31
            "*本户外来人口其中女总数", //32
            "离开原籍日期", //33
            "*来京日期", //34
            "*来现住地日期", //35
            "*来京原因", //36
            "*来京其他详细原因", //92
            "*居住类型", //37
            "*房屋登记表序号", //73
            "*居住其他类型", //74
            "现住地分局", //38
            "现住地派出所", //39
            "*现住地详址社区", //40
            "*现住地详址街道", //41
            "*现住地详址自填", //42
            "*所属派出所名称", //43
            "*民警姓名", //44
            "*目前状况", //45
            "*就业单位名称", //75
            "单位登记表序号", //76
            "就业单位详细地址", //77
            "*就业单位所属行业", //78
            "*主要从事工作", //79
            "*职业", //80
            "*签订劳动合同", //81
            "*就业单位所在地（区/县）", //82
            "*在京参加社会保险（无、养老、失业、医疗、工伤、生育）多选", //83
            "*参加社会保险（无、养老、失业、医疗、工伤、生育）多选", //84
            "*学校名称", //85
            "学校所在地", //86
            "*目前状况详细信息", //87
            "备注", //46
            "录入民警警号", //47
            "录入民警ID", //48
            "录入民警名称", //49
            "录入单位ID", //50
            "录入单位名称", //51
            "录入时间", //52
            "修改民警警号", //53
            "修改民警ID", //54
            "修改民警名称", //55
            "修改单位ID", //56
            "修改单位名称", //57
            "修改时间", //58
            "删除民警警号", //59
            "删除民警ID", //60
            "删除民警名称", //61
            "删除单位ID", //62
            "删除单位名称", //63
            "删除时间", //64
            "是否删除（1是，0否）", //65
            "所属房屋信息ID", //66
            "录入设备（0出租公寓，1采集终端，2流管平台）", //67
//			"录入设备（1手持机，0公寓系统）", //67
            "是否上传至服务器（1是，0否）", //68
            "是否上传至核录（1是，0否）", //69
            "是否上传至流管办（1是，0否）", //70
            "是否校准（1是，0否）", //88
            "照片URL", //89
            "*房屋ID", //1
            "*就业单位所属其他行业",//93
            "*主要从事其他工作（描述）",//94
            "MD5",//95
    };

    /**
     * 在车辆列表专栏标题
     */
    public static final String CHELIANG_TABLE_COMMENTS[] = {
            "车辆ID",	//0
            "车牌照",		//1
            "车身颜色",	//2
            "品牌",		//3
            "型号",		//4
            "车辆类型", //5
            "车辆描述",	//6
            "驾驶人姓名",	//7
            "驾驶人身份证号",	//8
            "驾驶人驾照号码",	//9
            "车辆行驶证号码",	//10
            "所在区县",	//11
            "所在街道",	//12
            "所属派出所",	//13
            "详细地址",	//14
            "录入时间",	//15
            "号牌种类",	//16
            "录入民警警号",	//17
            "录入民警ID",	//18
            "录入民警名称",	//19
            "录入单位ID",		//20
            "录入单位名称",	//21
            "修改民警警号",	//22
            "修改民警ID",		//23
            "修改民警名称",	//24
            "修改单位ID",		//25
            "修改单位名称",	//26
            "修改时间",		//27
            "删除民警警号",	//28
            "删除民警ID",		//29
            "删除民警名称",	//30
            "删除单位ID",		//31
            "删除单位名称",	//32
            "删除时间",		//33
            "是否删除（1是，0否）",	//34
            "照片URL",		//35
            "是否上传至服务器（1是，0否）",	//36
            "经度",		//37
            "纬度",		//38
            "录入设备（0出租公寓，1采集终端，2流管平台）",	//39
            "是否上传至核录",	//40
            "是否上传至流管办"	//41
    };

    /**
     * 在来京人员信息列表专栏标题
     */
    public static final String HEIMINGDAN_MD5_TABLE_COMMENTS[] = {
            "A",// TYPE
            "B",// MD5
            "DRSJ"
    };

    /*
     * 数据库表名字
     */
    protected static final String FANGWUXINXI_TABLE_NAME = "T_FWXX";
    protected static final String FANGZHUXINXI_TABLE_NAME = "T_FZXX";
    protected static final String LAIJINGRENYUANXINXI_TABLE_NAME = "T_LJRYXX";
    protected static final String JINGYUANXINXI_TABLE_NAME = "T_JYXX";
    protected static final String HEIMINGDAN_TABLE_NAME = "T_HMD";
    protected static final String YUJINGXINXI_TABLE_NAME = "T_YJXX";
    protected static final String XITONGYONGHU_TABLE_NAME = "T_XTYH";
    protected static final String CAOZUORIZHI_TABLE_NAME = "T_CZRZ";
    protected static final String JIZHUXINXI_TABLE_NAME = "T_JZXX";
    protected static final String SHEQU_TABLE_NAME = "C_SQ";
    protected static final String SHEQUGUANLIYUAN_TABLE_NAME = "C_SQGLY";
    protected static final String GONGANDANWEI_TABLE_NAME = "C_GADW";
    protected static final String JIELUXIANG_TABLE_NAME = "C_JLX";
    protected static final String XINGZHENGQUHUA_TABLE_NAME = "C_XZQH";
    protected static final String JIAOHUANSHUJUDINGYI_TABLE_NAME = "F_SBSJDY";
    protected static final String HEIMINGDAN_MD5_TABLE_NAME = "T_HMD_MD5";
    protected static final String CHELIANGXINXI_TABLE_NAME = "T_CLXX";
    protected static final String CHELIANGLEIXING_TABLE_NAME = "C_CLLX";
    protected static final String HAOPAIZHONGLEI_TABLE_NAME = "C_HPZL";
    protected static final String CHESHENYANSE_TABLE_NAME = "C_CSYS";
    protected static final String BIANXIEFAGUIKU_MULU1_TABLE_NAME = "C_BXFGK_ML1";
    protected static final String BIANXIEFAGUIKU_MULU2_TABLE_NAME = "C_BXFGK_ML2";
    protected static final String BIANXIEFAGUIKU_NEIRONG_TABLE_NAME = "C_BXFGK_NR";

    /*
     * 数据库表识别符号
     */
    private static final int FANGWUXINXI = 1;
    private static final int FANGZHUXINXI = 2;
    private static final int LAIJINGRENYUANXINXI = 3;
    private static final int JINGYUANXINXI = 4;
    private static final int HEIMINGDAN = 5;
    private static final int YUJINGXINXI = 6;
    private static final int XITONGYONGHU = 7;
    private static final int CAOZUORIZHI = 8;
    private static final int JIZHUXINXI = 9;
    private static final int SHEQU = 10;
    private static final int SHEQUGUANLIYUAN = 11;
    private static final int GONGANDANWEI = 12;
    private static final int JIELUXIANG = 13;
    private static final int XINGZHENGQUHUA = 14;
    private static final int JIAOHUANSHUJUDINGYI = 15;
    private static final int HEIMINGDAN_MD5 = 16;
    private static final int CHELIANGXINXI = 17;
    private static final int CHELIANGLEIXING = 18;
    private static final int HAOPAIZHONGLEI = 19;
    private static final int CHESHENYANSE = 20;
    private static final int BIANXIEFAGUIKU_MULU1 = 21;
    private static final int BIANXIEFAGUIKU_MULU2 = 22;
    private static final int BIANXIEFAGUIKU_NEIRONG = 23;

    /*
     * 为数据库表投影映射
     */
    private static final LinkedHashMap<String, String> fwxxProjectionMap;
    private static final LinkedHashMap<String, String> fzxxProjectionMap;
    private static final LinkedHashMap<String, String> ljryxxProjectionMap;
    private static final LinkedHashMap<String, String> hmdProjectionMap;
    private static final LinkedHashMap<String, String> yjxxProjectionMap;
    private static final LinkedHashMap<String, String> czrzProjectionMap;
    private static final LinkedHashMap<String, String> jzxxProjectionMap;
    private static final LinkedHashMap<String, String> sqProjectionMap;
    private static final LinkedHashMap<String, String> sqglyProjectionMap;
    private static final LinkedHashMap<String, String> gadwProjectionMap;
    private static final LinkedHashMap<String, String> jlxProjectionMap;
    private static final LinkedHashMap<String, String> xzqhProjectionMap;
    private static final LinkedHashMap<String, String> sbsjdyProjectionMap;
    private static final LinkedHashMap<String, String> hmdmd5ProjectionMap;
    private static final LinkedHashMap<String, String> clxxProjectionMap;
    private static final LinkedHashMap<String, String> jdcProjectionMap;
    private static final LinkedHashMap<String, String> hpzlProjectionMap;
    private static final LinkedHashMap<String, String> csysProjectionMap;
    private static final LinkedHashMap<String, String> bxfgkml1ProjectionMap;
    private static final LinkedHashMap<String, String> bxfgkml2ProjectionMap;
    private static final LinkedHashMap<String, String> bxfgknrProjectionMap;

    /**
     * 列表名和uri之间的映射关系
     */
    private static final HashMap<String, Uri> mUriMap;

    /**
     * 来帮助匹配Uri
     */
    private static final UriMatcher sUriMatcher;

    /**
     * 数据库辅助
     */
    private static DatabaseHelper dbHelper;

    /**
     * 房屋信息列表标题映射
     */
    public static final LinkedHashMap<String, String> fwxxLabelMap;

    /**
     * 来京人员信息列表标题映射
     */
    public static final LinkedHashMap<String, String> ljryxxLabelMap;

    /**
     * 车辆信息列表标题映射
     */
    public static final LinkedHashMap<String, String> clxxLabelMap;

    /**
     * 黑名单列表标题映射
     */
    public static final LinkedHashMap<String, String> hmdLabelMap;

    static {
        mUriMap = new LinkedHashMap<String, Uri>();
        mUriMap.put(FANGWUXINXI_TABLE_NAME, FangWuColumns.CONTENT_URI);
        mUriMap.put(FANGZHUXINXI_TABLE_NAME, FangZhuColumns.CONTENT_URI);
        mUriMap.put(LAIJINGRENYUANXINXI_TABLE_NAME, LaiJingRenYuanColumns.CONTENT_URI);
        mUriMap.put(HEIMINGDAN_TABLE_NAME, HeiMingDanColumns.CONTENT_URI);
        mUriMap.put(YUJINGXINXI_TABLE_NAME, YuJingColumns.CONTENT_URI);
        mUriMap.put(CAOZUORIZHI_TABLE_NAME, CaoZuoRiZhiColumns.CONTENT_URI);
        mUriMap.put(JIZHUXINXI_TABLE_NAME, JiZhuColumns.CONTENT_URI);
        mUriMap.put(SHEQU_TABLE_NAME, C_SQ.CONTENT_URI);
        mUriMap.put(SHEQUGUANLIYUAN_TABLE_NAME, C_SQGLY.CONTENT_URI);
        mUriMap.put(GONGANDANWEI_TABLE_NAME, C_GADW.CONTENT_URI);
        mUriMap.put(JIELUXIANG_TABLE_NAME, C_JLX.CONTENT_URI);
        mUriMap.put(XINGZHENGQUHUA_TABLE_NAME, C_XZQH.CONTENT_URI);
        mUriMap.put(JIAOHUANSHUJUDINGYI_TABLE_NAME, F_SBSJDY.CONTENT_URI);
        mUriMap.put(HEIMINGDAN_MD5_TABLE_NAME, T_HMD_MD5.CONTENT_URI);
        mUriMap.put(CHELIANGXINXI_TABLE_NAME, CheLiangColumns.CONTENT_URI);
        mUriMap.put(CHELIANGLEIXING_TABLE_NAME, C_CLLX.CONTENT_URI);
        mUriMap.put(HAOPAIZHONGLEI_TABLE_NAME, C_HPZL.CONTENT_URI);
        mUriMap.put(CHESHENYANSE_TABLE_NAME, C_CSYS.CONTENT_URI);
        mUriMap.put(BIANXIEFAGUIKU_MULU1_TABLE_NAME, C_BXFGK_ML1.CONTENT_URI);
        mUriMap.put(BIANXIEFAGUIKU_MULU2_TABLE_NAME, C_BXFGK_ML2.CONTENT_URI);
        mUriMap.put(BIANXIEFAGUIKU_NEIRONG_TABLE_NAME, C_BXFGK_NR.CONTENT_URI);

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, FANGWUXINXI_TABLE_NAME, FANGWUXINXI);
        sUriMatcher.addURI(AUTHORITY, FANGZHUXINXI_TABLE_NAME, FANGZHUXINXI);
        sUriMatcher.addURI(AUTHORITY, LAIJINGRENYUANXINXI_TABLE_NAME, LAIJINGRENYUANXINXI);
        sUriMatcher.addURI(AUTHORITY, JINGYUANXINXI_TABLE_NAME, JINGYUANXINXI);
        sUriMatcher.addURI(AUTHORITY, HEIMINGDAN_TABLE_NAME, HEIMINGDAN);
        sUriMatcher.addURI(AUTHORITY, YUJINGXINXI_TABLE_NAME, YUJINGXINXI);
        sUriMatcher.addURI(AUTHORITY, XITONGYONGHU_TABLE_NAME, XITONGYONGHU);
        sUriMatcher.addURI(AUTHORITY, CAOZUORIZHI_TABLE_NAME, CAOZUORIZHI);
        sUriMatcher.addURI(AUTHORITY, JIZHUXINXI_TABLE_NAME, JIZHUXINXI);
        sUriMatcher.addURI(AUTHORITY, SHEQU_TABLE_NAME, SHEQU);
        sUriMatcher.addURI(AUTHORITY, SHEQUGUANLIYUAN_TABLE_NAME, SHEQUGUANLIYUAN);
        sUriMatcher.addURI(AUTHORITY, GONGANDANWEI_TABLE_NAME, GONGANDANWEI);
        sUriMatcher.addURI(AUTHORITY, JIELUXIANG_TABLE_NAME, JIELUXIANG);
        sUriMatcher.addURI(AUTHORITY, XINGZHENGQUHUA_TABLE_NAME, XINGZHENGQUHUA);
        sUriMatcher.addURI(AUTHORITY, JIAOHUANSHUJUDINGYI_TABLE_NAME, JIAOHUANSHUJUDINGYI);
        sUriMatcher.addURI(AUTHORITY, HEIMINGDAN_MD5_TABLE_NAME, HEIMINGDAN_MD5);
        sUriMatcher.addURI(AUTHORITY, CHELIANGXINXI_TABLE_NAME, CHELIANGXINXI);
        sUriMatcher.addURI(AUTHORITY, CHELIANGLEIXING_TABLE_NAME, CHELIANGLEIXING);
        sUriMatcher.addURI(AUTHORITY, HAOPAIZHONGLEI_TABLE_NAME, HAOPAIZHONGLEI);
        sUriMatcher.addURI(AUTHORITY, CHESHENYANSE_TABLE_NAME, CHESHENYANSE);
        sUriMatcher.addURI(AUTHORITY, BIANXIEFAGUIKU_MULU1_TABLE_NAME, BIANXIEFAGUIKU_MULU1);
        sUriMatcher.addURI(AUTHORITY, BIANXIEFAGUIKU_MULU2_TABLE_NAME, BIANXIEFAGUIKU_MULU2);
        sUriMatcher.addURI(AUTHORITY, BIANXIEFAGUIKU_NEIRONG_TABLE_NAME, BIANXIEFAGUIKU_NEIRONG);

        // 房屋信息表
        fwxxProjectionMap = new LinkedHashMap<String, String>();
        fwxxProjectionMap.put(FangWuColumns.FWID, FangWuColumns.FWID);      //0
        fwxxProjectionMap.put(FangWuColumns.SZDXQ, FangWuColumns.SZDXQ);    //1
        fwxxProjectionMap.put(FangWuColumns.GLYXM, FangWuColumns.GLYXM);    //2
        fwxxProjectionMap.put(FangWuColumns.GLYBH, FangWuColumns.GLYBH);    //3
        fwxxProjectionMap.put(FangWuColumns.DJBXH, FangWuColumns.DJBXH);    //4
        fwxxProjectionMap.put(FangWuColumns.TBRQ, FangWuColumns.TBRQ);      //5
        fwxxProjectionMap.put(FangWuColumns.FWLX, FangWuColumns.FWLX);      //6
        fwxxProjectionMap.put(FangWuColumns.JSXZ, FangWuColumns.JSXZ);      //7
        fwxxProjectionMap.put(FangWuColumns.QTJSXZ, FangWuColumns.QTJSXZ);  //96
        fwxxProjectionMap.put(FangWuColumns.QZFLX, FangWuColumns.QZFLX);    //76
        fwxxProjectionMap.put(FangWuColumns.QTFWLX, FangWuColumns.QTFWLX);  //77
        fwxxProjectionMap.put(FangWuColumns.SZDZFJ, FangWuColumns.SZDZFJ);  //8
        fwxxProjectionMap.put(FangWuColumns.SZDZPCS, FangWuColumns.SZDZPCS);//9
        fwxxProjectionMap.put(FangWuColumns.SZDXZSQ, FangWuColumns.SZDXZSQ);//10
        fwxxProjectionMap.put(FangWuColumns.SZDXZJD, FangWuColumns.SZDXZJD);//11
        fwxxProjectionMap.put(FangWuColumns.SZDXZZT, FangWuColumns.SZDXZZT);//12
        fwxxProjectionMap.put(FangWuColumns.JZLX, FangWuColumns.JZLX);      //13
        fwxxProjectionMap.put(FangWuColumns.QTJZLX, FangWuColumns.QTJZLX);  //97
        fwxxProjectionMap.put(FangWuColumns.FCZH, FangWuColumns.FCZH);      //14
        fwxxProjectionMap.put(FangWuColumns.SSPCSMC, FangWuColumns.SSPCSMC);//15
        fwxxProjectionMap.put(FangWuColumns.MJXM, FangWuColumns.MJXM);      //16
        fwxxProjectionMap.put(FangWuColumns.SYQLX, FangWuColumns.SYQLX);    //17
        fwxxProjectionMap.put(FangWuColumns.CZRLX, FangWuColumns.CZRLX);    //18
        fwxxProjectionMap.put(FangWuColumns.XM, FangWuColumns.XM);          //19
        fwxxProjectionMap.put(FangWuColumns.ZJLB, FangWuColumns.ZJLB);      //20
        fwxxProjectionMap.put(FangWuColumns.QTZJ, FangWuColumns.QTZJ);      //98
        fwxxProjectionMap.put(FangWuColumns.ZJHM, FangWuColumns.ZJHM);      //21
        fwxxProjectionMap.put(FangWuColumns.XB, FangWuColumns.XB);          //22
        fwxxProjectionMap.put(FangWuColumns.CSRQ, FangWuColumns.CSRQ);      //23
        fwxxProjectionMap.put(FangWuColumns.HJD, FangWuColumns.HJD);        //24
        fwxxProjectionMap.put(FangWuColumns.ZZMM, FangWuColumns.ZZMM);      //25
        fwxxProjectionMap.put(FangWuColumns.LXDH, FangWuColumns.LXDH);      //26
        fwxxProjectionMap.put(FangWuColumns.XZDZS, FangWuColumns.XZDZS);    //27
        fwxxProjectionMap.put(FangWuColumns.XZDZSHI, FangWuColumns.XZDZSHI);//28
        fwxxProjectionMap.put(FangWuColumns.XJZDXXDZ, FangWuColumns.XJZDXXDZ);//29
        fwxxProjectionMap.put(FangWuColumns.HJDZS, FangWuColumns.HJDZS);    //30
        fwxxProjectionMap.put(FangWuColumns.HJDZSHI, FangWuColumns.HJDZSHI);//31
        fwxxProjectionMap.put(FangWuColumns.HJDXXDZ, FangWuColumns.HJDXXDZ);//32
        fwxxProjectionMap.put(FangWuColumns.DWMC, FangWuColumns.DWMC);      //78
        fwxxProjectionMap.put(FangWuColumns.FZRXM, FangWuColumns.FZRXM);    //79
        fwxxProjectionMap.put(FangWuColumns.DWLXDH, FangWuColumns.DWLXDH);  //80
        fwxxProjectionMap.put(FangWuColumns.SZDZS, FangWuColumns.SZDZS);    //81
        fwxxProjectionMap.put(FangWuColumns.SZDZSHI, FangWuColumns.SZDZSHI);//82
        fwxxProjectionMap.put(FangWuColumns.SZDXXDZ, FangWuColumns.SZDXXDZ);//83
        fwxxProjectionMap.put(FangWuColumns.CZJS, FangWuColumns.CZJS);      //34
        fwxxProjectionMap.put(FangWuColumns.CZMJ, FangWuColumns.CZMJ);      //35
        fwxxProjectionMap.put(FangWuColumns.FWCS, FangWuColumns.FWCS);      //36
        fwxxProjectionMap.put(FangWuColumns.AQYH, FangWuColumns.AQYH);      //37
        fwxxProjectionMap.put(FangWuColumns.CZYT, FangWuColumns.CZYT);      //38
        fwxxProjectionMap.put(FangWuColumns.QTCZYT, FangWuColumns.QTCZYT);  //99
        fwxxProjectionMap.put(FangWuColumns.ZZRYSLBS, FangWuColumns.ZZRYSLBS);//39
        fwxxProjectionMap.put(FangWuColumns.ZZRYSLWSS, FangWuColumns.ZZRYSLWSS);//40
        fwxxProjectionMap.put(FangWuColumns.ZZRYSLGAT, FangWuColumns.ZZRYSLGAT);//41
        fwxxProjectionMap.put(FangWuColumns.ZZRYSLWJ, FangWuColumns.ZZRYSLWJ);//42
        fwxxProjectionMap.put(FangWuColumns.ZJFS, FangWuColumns.ZJFS);      //43
        fwxxProjectionMap.put(FangWuColumns.ZJ, FangWuColumns.ZJ);          //44
        fwxxProjectionMap.put(FangWuColumns.ZLHT, FangWuColumns.ZLHT);      //45
        fwxxProjectionMap.put(FangWuColumns.NS, FangWuColumns.NS);          //46
        fwxxProjectionMap.put(FangWuColumns.DJBA, FangWuColumns.DJBA);      //47
        fwxxProjectionMap.put(FangWuColumns.QDZRS, FangWuColumns.QDZRS);    //48
        fwxxProjectionMap.put(FangWuColumns.QDZRSRQ, FangWuColumns.QDZRSRQ);//49
        fwxxProjectionMap.put(FangWuColumns.CZQSRQ, FangWuColumns.CZQSRQ);  //50
        fwxxProjectionMap.put(FangWuColumns.CZJZRQ, FangWuColumns.CZJZRQ);  //51
        fwxxProjectionMap.put(FangWuColumns.ZJGSMC, FangWuColumns.ZJGSMC);  //84
        fwxxProjectionMap.put(FangWuColumns.ZJGSLXDH, FangWuColumns.ZJGSLXDH);//85
        fwxxProjectionMap.put(FangWuColumns.FWFZRXM, FangWuColumns.FWFZRXM);//86
        fwxxProjectionMap.put(FangWuColumns.FWFZRSFZ, FangWuColumns.FWFZRSFZ);//87
        fwxxProjectionMap.put(FangWuColumns.ZZRXM, FangWuColumns.ZZRXM);    //88
        fwxxProjectionMap.put(FangWuColumns.ZZRZJLB, FangWuColumns.ZZRZJLB);//89
        fwxxProjectionMap.put(FangWuColumns.ZZRQTZJLB, FangWuColumns.ZZRQTZJLB);//100
        fwxxProjectionMap.put(FangWuColumns.ZZRZJHM, FangWuColumns.ZZRZJHM);//90
        fwxxProjectionMap.put(FangWuColumns.ZZRLXDH, FangWuColumns.ZZRLXDH);//91
        fwxxProjectionMap.put(FangWuColumns.ZZRXZDZ, FangWuColumns.ZZRXZDZ);//92
        fwxxProjectionMap.put(FangWuColumns.BZ, FangWuColumns.BZ);          //52
        fwxxProjectionMap.put(FangWuColumns.LRMJJH, FangWuColumns.LRMJJH);  //53
        fwxxProjectionMap.put(FangWuColumns.LRMJID, FangWuColumns.LRMJID);  //54
        fwxxProjectionMap.put(FangWuColumns.LRMJXM, FangWuColumns.LRMJXM);  //55
        fwxxProjectionMap.put(FangWuColumns.LRDWID, FangWuColumns.LRDWID);  //56
        fwxxProjectionMap.put(FangWuColumns.LRDWMC, FangWuColumns.LRDWMC);  //57
        fwxxProjectionMap.put(FangWuColumns.LRSJ, FangWuColumns.LRSJ);      //58
        fwxxProjectionMap.put(FangWuColumns.XGMJJH, FangWuColumns.XGMJJH);  //59
        fwxxProjectionMap.put(FangWuColumns.XGMJID, FangWuColumns.XGMJID);  //60
        fwxxProjectionMap.put(FangWuColumns.XGMJXM, FangWuColumns.XGMJXM);  //61
        fwxxProjectionMap.put(FangWuColumns.XGDWID, FangWuColumns.XGDWID);  //62
        fwxxProjectionMap.put(FangWuColumns.XGDWMC, FangWuColumns.XGDWMC);  //63
        fwxxProjectionMap.put(FangWuColumns.XGSJ, FangWuColumns.XGSJ);      //64
        fwxxProjectionMap.put(FangWuColumns.SCMJJH, FangWuColumns.SCMJJH);  //65
        fwxxProjectionMap.put(FangWuColumns.SCMJID, FangWuColumns.SCMJID);  //66
        fwxxProjectionMap.put(FangWuColumns.SCMJXM, FangWuColumns.SCMJXM);  //67
        fwxxProjectionMap.put(FangWuColumns.SCDWID, FangWuColumns.SCDWID);  //68
        fwxxProjectionMap.put(FangWuColumns.SCDWMC, FangWuColumns.SCDWMC);  //69
        fwxxProjectionMap.put(FangWuColumns.SCSJ, FangWuColumns.SCSJ);      //70
        fwxxProjectionMap.put(FangWuColumns.SFSC, FangWuColumns.SFSC);      //71
        fwxxProjectionMap.put(FangWuColumns.LRSB, FangWuColumns.LRSB);      //72
        fwxxProjectionMap.put(FangWuColumns.SFSCZFWQ, FangWuColumns.SFSCZFWQ);//73
        fwxxProjectionMap.put(FangWuColumns.SFSCZHL, FangWuColumns.SFSCZHL);//74
        fwxxProjectionMap.put(FangWuColumns.SFSCZLGB, FangWuColumns.SFSCZLGB);//75
        fwxxProjectionMap.put(FangWuColumns.SFJZ, FangWuColumns.SFJZ);      //93
        fwxxProjectionMap.put(FangWuColumns.ZPURL, FangWuColumns.ZPURL);    //94
        fwxxProjectionMap.put(FangWuColumns.SXURL, FangWuColumns.SXURL);    //95
        fwxxProjectionMap.put(FangWuColumns.GJDQ, FangWuColumns.GJDQ);      //33

        // 房主信息表
        fzxxProjectionMap = new LinkedHashMap<String, String>();
        fzxxProjectionMap.put(FangZhuColumns.FZID, FangZhuColumns.FZID);
        fzxxProjectionMap.put(FangZhuColumns.XM, FangZhuColumns.XM);
        fzxxProjectionMap.put(FangZhuColumns.ZJLB, FangZhuColumns.ZJLB);
        fzxxProjectionMap.put(FangZhuColumns.ZJHM, FangZhuColumns.ZJHM);
        fzxxProjectionMap.put(FangZhuColumns.XB, FangZhuColumns.XB);
        fzxxProjectionMap.put(FangZhuColumns.CSRQ, FangZhuColumns.CSRQ);
        fzxxProjectionMap.put(FangZhuColumns.HJD, FangZhuColumns.HJD);
        fzxxProjectionMap.put(FangZhuColumns.ZZMM, FangZhuColumns.ZZMM);
        fzxxProjectionMap.put(FangZhuColumns.LXDH, FangZhuColumns.LXDH);
        fzxxProjectionMap.put(FangZhuColumns.XZDZS, FangZhuColumns.XZDZS);
        fzxxProjectionMap.put(FangZhuColumns.XZDZSHI, FangZhuColumns.XZDZSHI);
        fzxxProjectionMap.put(FangZhuColumns.XJZDXXDZ, FangZhuColumns.XJZDXXDZ);
        fzxxProjectionMap.put(FangZhuColumns.HJDZS, FangZhuColumns.HJDZS);
        fzxxProjectionMap.put(FangZhuColumns.HJDZSHI, FangZhuColumns.HJDZSHI);
        fzxxProjectionMap.put(FangZhuColumns.HJDXXDZ, FangZhuColumns.HJDXXDZ);
        fzxxProjectionMap.put(FangZhuColumns.GJDQ, FangZhuColumns.GJDQ);
        fzxxProjectionMap.put(FangZhuColumns.LRMJJH, FangZhuColumns.LRMJJH);
        fzxxProjectionMap.put(FangZhuColumns.LRMJID, FangZhuColumns.LRMJID);
        fzxxProjectionMap.put(FangZhuColumns.LRMJXM, FangZhuColumns.LRMJXM);
        fzxxProjectionMap.put(FangZhuColumns.LRDWID, FangZhuColumns.LRDWID);
        fzxxProjectionMap.put(FangZhuColumns.LRDWMC, FangZhuColumns.LRDWMC);
        fzxxProjectionMap.put(FangZhuColumns.LRSJ, FangZhuColumns.LRSJ);
        fzxxProjectionMap.put(FangZhuColumns.XGMJJH, FangZhuColumns.XGMJJH);
        fzxxProjectionMap.put(FangZhuColumns.XGMJID, FangZhuColumns.XGMJID);
        fzxxProjectionMap.put(FangZhuColumns.XGMJXM, FangZhuColumns.XGMJXM);
        fzxxProjectionMap.put(FangZhuColumns.XGDWID, FangZhuColumns.XGDWID);
        fzxxProjectionMap.put(FangZhuColumns.XGDWMC, FangZhuColumns.XGDWMC);
        fzxxProjectionMap.put(FangZhuColumns.XGSJ, FangZhuColumns.XGSJ);
        fzxxProjectionMap.put(FangZhuColumns.SCMJJH, FangZhuColumns.SCMJJH);
        fzxxProjectionMap.put(FangZhuColumns.SCMJID, FangZhuColumns.SCMJID);
        fzxxProjectionMap.put(FangZhuColumns.SCMJXM, FangZhuColumns.SCMJXM);
        fzxxProjectionMap.put(FangZhuColumns.SCDWID, FangZhuColumns.SCDWID);
        fzxxProjectionMap.put(FangZhuColumns.SCDWMC, FangZhuColumns.SCDWMC);
        fzxxProjectionMap.put(FangZhuColumns.SCSJ, FangZhuColumns.SCSJ);
        fzxxProjectionMap.put(FangZhuColumns.SFSC, FangZhuColumns.SFSC);
        fzxxProjectionMap.put(FangZhuColumns.LRSB, FangZhuColumns.LRSB);
        fzxxProjectionMap.put(FangZhuColumns.SFSCZFWQ, FangZhuColumns.SFSCZFWQ);
        fzxxProjectionMap.put(FangZhuColumns.SFSCZHL, FangZhuColumns.SFSCZHL);
        fzxxProjectionMap.put(FangZhuColumns.SFSCZLGB, FangZhuColumns.SFSCZLGB);
        fzxxProjectionMap.put(FangZhuColumns.SZDZS, FangZhuColumns.SZDZS);
        fzxxProjectionMap.put(FangZhuColumns.QTZJ, FangZhuColumns.QTZJ);
        fwxxProjectionMap.put(FangZhuColumns.DWMC, FangZhuColumns.DWMC);
        fwxxProjectionMap.put(FangZhuColumns.FZRXM, FangZhuColumns.FZRXM);
        fwxxProjectionMap.put(FangZhuColumns.DWLXDH, FangZhuColumns.DWLXDH);
        fwxxProjectionMap.put(FangZhuColumns.SZDZS, FangZhuColumns.SZDZS);
        fwxxProjectionMap.put(FangZhuColumns.SZDZSHI, FangZhuColumns.SZDZSHI);
        fwxxProjectionMap.put(FangZhuColumns.SZDXXDZ, FangZhuColumns.SZDXXDZ);
        fwxxProjectionMap.put(FangZhuColumns.SYQLX, FangZhuColumns.SYQLX);

        // 来京人员信息表
        ljryxxProjectionMap = new LinkedHashMap<String, String>();
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.RYID, LaiJingRenYuanColumns.RYID);        //0
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SZDXQ, LaiJingRenYuanColumns.SZDXQ);      //2
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.GLYXM, LaiJingRenYuanColumns.GLYXM);      //3
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.GLYBH, LaiJingRenYuanColumns.GLYBH);      //4
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.DJBXH, LaiJingRenYuanColumns.DJBXH);      //5
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.TBRQ, LaiJingRenYuanColumns.TBRQ);        //6
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XM, LaiJingRenYuanColumns.XM);            //7
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SFZHM, LaiJingRenYuanColumns.SFZHM);      //8
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SFZWSFZH, LaiJingRenYuanColumns.SFZWSFZH);//9
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.CSRQ, LaiJingRenYuanColumns.CSRQ);        //10
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XB, LaiJingRenYuanColumns.XB);            //11
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.MZ, LaiJingRenYuanColumns.MZ);            //12
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.MZXXXX, LaiJingRenYuanColumns.MZXXXX);    //90
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.ZZMM, LaiJingRenYuanColumns.ZZMM);        //13
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SJYCD, LaiJingRenYuanColumns.SJYCD);      //14
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HJLB, LaiJingRenYuanColumns.HJLB);        //15
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HYZK, LaiJingRenYuanColumns.HYZK);        //16
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LXDH, LaiJingRenYuanColumns.LXDH);        //17
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HJDZS, LaiJingRenYuanColumns.HJDZS);      //18
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HJDZSHI, LaiJingRenYuanColumns.HJDZSHI);  //19
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HJDZQX, LaiJingRenYuanColumns.HJDZQX);    //20
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HJDZJD, LaiJingRenYuanColumns.HJDZJD);    //21
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HJXXDZ, LaiJingRenYuanColumns.HJXXDZ);    //22
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.CSD, LaiJingRenYuanColumns.CSD);          //23
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.JZZJ, LaiJingRenYuanColumns.JZZJ);        //24
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HYZM, LaiJingRenYuanColumns.HYZM);        //25
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.MYJZZ, LaiJingRenYuanColumns.MYJZZ);      //26
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.JTHLR, LaiJingRenYuanColumns.JTHLR);      //27
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SFHZ, LaiJingRenYuanColumns.SFHZ);        //28
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.YHZGX, LaiJingRenYuanColumns.YHZGX);      //71
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.YHZXXGX, LaiJingRenYuanColumns.YHZXXGX);  //91
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.HZDJBXH, LaiJingRenYuanColumns.HZDJBXH);  //72
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.BHWLRKZS, LaiJingRenYuanColumns.BHWLRKZS);//29
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.BHWLRK16, LaiJingRenYuanColumns.BHWLRK16);//30
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.BHWLRKNAN, LaiJingRenYuanColumns.BHWLRKNAN);//31
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.BHWLRKNV, LaiJingRenYuanColumns.BHWLRKNV);//32
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LKYJRQ, LaiJingRenYuanColumns.LKYJRQ);    //33
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LJRQ, LaiJingRenYuanColumns.LJRQ);        //34
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LXZDRQ, LaiJingRenYuanColumns.LXZDRQ);    //35
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LJYY, LaiJingRenYuanColumns.LJYY);        //36
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LJQTXXYY, LaiJingRenYuanColumns.LJQTXXYY);//92
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.JZLX, LaiJingRenYuanColumns.JZLX);        //37
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.FWDJBXH, LaiJingRenYuanColumns.FWDJBXH);  //73
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.JZQTLX, LaiJingRenYuanColumns.JZQTLX);    //74
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XZDFJ, LaiJingRenYuanColumns.XZDFJ);      //38
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XZDPCS, LaiJingRenYuanColumns.XZDPCS);    //39
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XZDXZSQ, LaiJingRenYuanColumns.XZDXZSQ);  //40
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XZDXZJD, LaiJingRenYuanColumns.XZDXZJD);  //41
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XZDXZZT, LaiJingRenYuanColumns.XZDXZZT);  //42
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SSPCSMC, LaiJingRenYuanColumns.SSPCSMC);  //43
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.MJXM, LaiJingRenYuanColumns.MJXM);        //44
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.MQZK, LaiJingRenYuanColumns.MQZK);        //45
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.JYDWMC, LaiJingRenYuanColumns.JYDWMC);    //75
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.DWDJBXH, LaiJingRenYuanColumns.DWDJBXH);  //76
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.JYDWXXDZ, LaiJingRenYuanColumns.JYDWXXDZ);//77
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.JYDWSSHY, LaiJingRenYuanColumns.JYDWSSHY);//78
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.ZYCSGZ, LaiJingRenYuanColumns.ZYCSGZ);    //79
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.ZY, LaiJingRenYuanColumns.ZY);            //80
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.QDLDHT, LaiJingRenYuanColumns.QDLDHT);    //81
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.JYDWSZD, LaiJingRenYuanColumns.JYDWSZD);  //82
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.ZJCJSHBX, LaiJingRenYuanColumns.ZJCJSHBX);//83
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.CJSHBX, LaiJingRenYuanColumns.CJSHBX);    //84
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XXMC, LaiJingRenYuanColumns.XXMC);        //85
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XXSZD, LaiJingRenYuanColumns.XXSZD);      //86
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.MQZKXXXX, LaiJingRenYuanColumns.MQZKXXXX);//87
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.BZ, LaiJingRenYuanColumns.BZ);            //46
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LRMJJH, LaiJingRenYuanColumns.LRMJJH);    //47
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LRMJID, LaiJingRenYuanColumns.LRMJID);    //48
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LRMJXM, LaiJingRenYuanColumns.LRMJXM);    //49
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LRDWID, LaiJingRenYuanColumns.LRDWID);    //50
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LRDWMC, LaiJingRenYuanColumns.LRDWMC);    //51
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LRSJ, LaiJingRenYuanColumns.LRSJ);        //52
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XGMJJH, LaiJingRenYuanColumns.XGMJJH);    //53
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XGMJID, LaiJingRenYuanColumns.XGMJID);    //54
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XGMJXM, LaiJingRenYuanColumns.XGMJXM);    //55
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XGDWID, LaiJingRenYuanColumns.XGDWID);    //56
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XGDWMC, LaiJingRenYuanColumns.XGDWMC);    //57
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.XGSJ, LaiJingRenYuanColumns.XGSJ);        //58
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SCMJJH, LaiJingRenYuanColumns.SCMJJH);    //59
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SCMJID, LaiJingRenYuanColumns.SCMJID);    //60
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SCMJXM, LaiJingRenYuanColumns.SCMJXM);    //61
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SCDWID, LaiJingRenYuanColumns.SCDWID);    //62
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SCDWMC, LaiJingRenYuanColumns.SCDWMC);    //63
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SCSJ, LaiJingRenYuanColumns.SCSJ);        //64
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SFSC, LaiJingRenYuanColumns.SFSC);        //65
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SSFWXXID, LaiJingRenYuanColumns.SSFWXXID);//66
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.LRSB, LaiJingRenYuanColumns.LRSB);        //67
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SFSCZFWQ, LaiJingRenYuanColumns.SFSCZFWQ);//68
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SFSCZHL, LaiJingRenYuanColumns.SFSCZHL);  //69
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SFSCZLGB, LaiJingRenYuanColumns.SFSCZLGB);//70
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.SFJZ, LaiJingRenYuanColumns.SFJZ);        //88
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.ZPURL, LaiJingRenYuanColumns.ZPURL);      //89
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.FWID, LaiJingRenYuanColumns.FWID);        //1
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.QTHY, LaiJingRenYuanColumns.QTHY);
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.QTZYCSGZMS, LaiJingRenYuanColumns.QTZYCSGZMS);
        ljryxxProjectionMap.put(LaiJingRenYuanColumns.MD5, LaiJingRenYuanColumns.MD5);

        // 黑名单表
        hmdProjectionMap = new LinkedHashMap<String, String>();
        hmdProjectionMap.put(HeiMingDanColumns.RYID, HeiMingDanColumns.RYID);
        hmdProjectionMap.put(HeiMingDanColumns.RYXM, HeiMingDanColumns.RYXM);
        hmdProjectionMap.put(HeiMingDanColumns.SFZH, HeiMingDanColumns.SFZH);
        hmdProjectionMap.put(HeiMingDanColumns.RYLX, HeiMingDanColumns.RYLX);
        hmdProjectionMap.put(HeiMingDanColumns.SJHQSJ, HeiMingDanColumns.SJHQSJ);
        hmdProjectionMap.put(HeiMingDanColumns.DRSJ, HeiMingDanColumns.DRSJ);

        // 预警信息表
        yjxxProjectionMap = new LinkedHashMap<String, String>();
        yjxxProjectionMap.put(YuJingColumns.YJXXID, YuJingColumns.YJXXID);
        yjxxProjectionMap.put(YuJingColumns.HMDRYID, YuJingColumns.HMDRYID);
        yjxxProjectionMap.put(YuJingColumns.LJRYRYID, YuJingColumns.LJRYRYID);
        yjxxProjectionMap.put(YuJingColumns.BDSFZH, YuJingColumns.BDSFZH);
        yjxxProjectionMap.put(YuJingColumns.BDSJ, YuJingColumns.BDSJ);
        yjxxProjectionMap.put(YuJingColumns.SFCK, YuJingColumns.SFCK);
        yjxxProjectionMap.put(YuJingColumns.CKRID, YuJingColumns.CKRID);
        yjxxProjectionMap.put(YuJingColumns.CKRJH, YuJingColumns.CKRJH);
        yjxxProjectionMap.put(YuJingColumns.CKRXM, YuJingColumns.CKRXM);
        yjxxProjectionMap.put(YuJingColumns.CKRDWID, YuJingColumns.CKRDWID);
        yjxxProjectionMap.put(YuJingColumns.CKRDWMC, YuJingColumns.CKRDWMC);
        yjxxProjectionMap.put(YuJingColumns.CKSJ, YuJingColumns.CKSJ);

        // 操作日志信息表
        czrzProjectionMap = new LinkedHashMap<String, String>();
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZXXID, CaoZuoRiZhiColumns.CZXXID);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZRID, CaoZuoRiZhiColumns.CZRID);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZRJH, CaoZuoRiZhiColumns.CZRJH);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZRXM, CaoZuoRiZhiColumns.CZRXM);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.SSDWID, CaoZuoRiZhiColumns.SSDWID);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.SSDWMC, CaoZuoRiZhiColumns.SSDWMC);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZMK, CaoZuoRiZhiColumns.CZMK);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZSJID, CaoZuoRiZhiColumns.CZSJID);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZSJLX, CaoZuoRiZhiColumns.CZSJLX);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZMS, CaoZuoRiZhiColumns.CZMS);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZSJ, CaoZuoRiZhiColumns.CZSJ);
        czrzProjectionMap.put(CaoZuoRiZhiColumns.CZRLX, CaoZuoRiZhiColumns.CZRLX);

        // 手持机机主信息
        jzxxProjectionMap = new LinkedHashMap<String, String>();
        jzxxProjectionMap.put(JiZhuColumns.MJID, JiZhuColumns.MJID);
        jzxxProjectionMap.put(JiZhuColumns.MJJH, JiZhuColumns.MJJH);
        jzxxProjectionMap.put(JiZhuColumns.MJXM, JiZhuColumns.MJXM);
        jzxxProjectionMap.put(JiZhuColumns.SSDWID, JiZhuColumns.SSDWID);
        jzxxProjectionMap.put(JiZhuColumns.SSDWMC, JiZhuColumns.SSDWMC);

        // 社区表
        sqProjectionMap = new LinkedHashMap<String, String>();
        sqProjectionMap.put(C_SQ.SQID, C_SQ.SQID);
        sqProjectionMap.put(C_SQ.SQMC, C_SQ.SQMC);
        sqProjectionMap.put(C_SQ.SSDWMC, C_SQ.SSDWMC);
        sqProjectionMap.put(C_SQ.SSDWID, C_SQ.SSDWID);
        sqProjectionMap.put(C_SQ.DRSJ, C_SQ.DRSJ);

        // 社区管理员表
        sqglyProjectionMap = new LinkedHashMap<String, String>();
        sqglyProjectionMap.put(C_SQGLY.ID, C_SQGLY.ID);
        sqglyProjectionMap.put(C_SQGLY.GLYBH, C_SQGLY.GLYBH);
        sqglyProjectionMap.put(C_SQGLY.GLYXM, C_SQGLY.GLYXM);
        sqglyProjectionMap.put(C_SQGLY.SQID, C_SQGLY.SQID);
        sqglyProjectionMap.put(C_SQGLY.DRSJ, C_SQGLY.DRSJ);

        // 公安单位表
        gadwProjectionMap = new LinkedHashMap<String, String>();
        gadwProjectionMap.put(C_GADW.DWID, C_GADW.DWID);
        gadwProjectionMap.put(C_GADW.DWMC, C_GADW.DWMC);
        gadwProjectionMap.put(C_GADW.SJDWID, C_GADW.SJDWID);
        gadwProjectionMap.put(C_GADW.DRSJ, C_GADW.DRSJ);
        gadwProjectionMap.put(C_GADW.DWJB, C_GADW.DWJB);

        // 街路巷表
        jlxProjectionMap = new LinkedHashMap<String, String>();
        jlxProjectionMap.put(C_JLX.JLXID, C_JLX.JLXID);
        jlxProjectionMap.put(C_JLX.JLXMC, C_JLX.JLXMC);
        jlxProjectionMap.put(C_JLX.GSSQID, C_JLX.GSSQID);
        jlxProjectionMap.put(C_JLX.DRSJ, C_JLX.DRSJ);

        // 行政区划表
        xzqhProjectionMap = new LinkedHashMap<String, String>();
        xzqhProjectionMap.put(C_XZQH.XZQH, C_XZQH.XZQH);
        xzqhProjectionMap.put(C_XZQH.MC, C_XZQH.MC);
        xzqhProjectionMap.put(C_XZQH.DRSJ, C_XZQH.DRSJ);

        // 交换数据定义表
        sbsjdyProjectionMap = new LinkedHashMap<String, String>();
        sbsjdyProjectionMap.put(F_SBSJDY.SJJHGN, F_SBSJDY.SJJHGN);
        sbsjdyProjectionMap.put(F_SBSJDY.SJJLZD, F_SBSJDY.SJJLZD);
        sbsjdyProjectionMap.put(F_SBSJDY.JLSJBM, F_SBSJDY.JLSJBM);

        // 黑名单MD5表
        hmdmd5ProjectionMap = new LinkedHashMap<String, String>();
        hmdmd5ProjectionMap.put(T_HMD_MD5.TYPE, T_HMD_MD5.TYPE);
        hmdmd5ProjectionMap.put(T_HMD_MD5.MD5, T_HMD_MD5.MD5);
        hmdmd5ProjectionMap.put(T_HMD_MD5.DRSJ, T_HMD_MD5.DRSJ);

        // 车辆信息表
        clxxProjectionMap = new LinkedHashMap<String, String>();
        clxxProjectionMap.put(CheLiangColumns.CLID, CheLiangColumns.CLID);      //0
        clxxProjectionMap.put(CheLiangColumns.CPH, CheLiangColumns.CPH);        //1
        clxxProjectionMap.put(CheLiangColumns.CSYS, CheLiangColumns.CSYS);      //2
        clxxProjectionMap.put(CheLiangColumns.PP, CheLiangColumns.PP);          //3
        clxxProjectionMap.put(CheLiangColumns.XH, CheLiangColumns.XH);          //4
        clxxProjectionMap.put(CheLiangColumns.CLLX, CheLiangColumns.CLLX);      //5
        clxxProjectionMap.put(CheLiangColumns.CLMS, CheLiangColumns.CLMS);      //6
        clxxProjectionMap.put(CheLiangColumns.JSRXM, CheLiangColumns.JSRXM);    //7
        clxxProjectionMap.put(CheLiangColumns.JSRSFZH, CheLiangColumns.JSRSFZH);//8
        clxxProjectionMap.put(CheLiangColumns.JSRJZHM, CheLiangColumns.JSRJZHM);//9
        clxxProjectionMap.put(CheLiangColumns.CLXSZHM, CheLiangColumns.CLXSZHM);//10
        clxxProjectionMap.put(CheLiangColumns.SZQX, CheLiangColumns.SZQX);      //11
        clxxProjectionMap.put(CheLiangColumns.SZJD, CheLiangColumns.SZJD);      //12
        clxxProjectionMap.put(CheLiangColumns.SSPCS, CheLiangColumns.SSPCS);    //13
        clxxProjectionMap.put(CheLiangColumns.XXDZ, CheLiangColumns.XXDZ);      //14
        clxxProjectionMap.put(CheLiangColumns.LRSJ, CheLiangColumns.LRSJ);      //15
        clxxProjectionMap.put(CheLiangColumns.HPZL, CheLiangColumns.HPZL);      //16
        clxxProjectionMap.put(CheLiangColumns.LRMJJH, CheLiangColumns.LRMJJH);  //17
        clxxProjectionMap.put(CheLiangColumns.LRMJID, CheLiangColumns.LRMJID);  //18
        clxxProjectionMap.put(CheLiangColumns.LRMJXM, CheLiangColumns.LRMJXM);  //19
        clxxProjectionMap.put(CheLiangColumns.LRDWID, CheLiangColumns.LRDWID);  //20
        clxxProjectionMap.put(CheLiangColumns.LRDWMC, CheLiangColumns.LRDWMC);  //21
        clxxProjectionMap.put(CheLiangColumns.XGMJJH, CheLiangColumns.XGMJJH);  //22
        clxxProjectionMap.put(CheLiangColumns.XGMJID, CheLiangColumns.XGMJID);  //23
        clxxProjectionMap.put(CheLiangColumns.XGMJXM, CheLiangColumns.XGMJXM);  //24
        clxxProjectionMap.put(CheLiangColumns.XGDWID, CheLiangColumns.XGDWID);  //25
        clxxProjectionMap.put(CheLiangColumns.XGDWMC, CheLiangColumns.XGDWMC);  //26
        clxxProjectionMap.put(CheLiangColumns.XGSJ, CheLiangColumns.XGSJ);      //27
        clxxProjectionMap.put(CheLiangColumns.SCMJJH, CheLiangColumns.SCMJJH);  //28
        clxxProjectionMap.put(CheLiangColumns.SCMJID, CheLiangColumns.SCMJID);  //29
        clxxProjectionMap.put(CheLiangColumns.SCMJXM, CheLiangColumns.SCMJXM);  //30
        clxxProjectionMap.put(CheLiangColumns.SCDWID, CheLiangColumns.SCDWID);  //31
        clxxProjectionMap.put(CheLiangColumns.SCDWMC, CheLiangColumns.SCDWMC);  //32
        clxxProjectionMap.put(CheLiangColumns.SCSJ, CheLiangColumns.SCSJ);      //33
        clxxProjectionMap.put(CheLiangColumns.SFSC, CheLiangColumns.SFSC);      //34
        clxxProjectionMap.put(CheLiangColumns.ZPURL, CheLiangColumns.ZPURL);    //35
        clxxProjectionMap.put(CheLiangColumns.SFSCZFWQ, CheLiangColumns.SFSCZFWQ);//36
        clxxProjectionMap.put(CheLiangColumns.X, CheLiangColumns.X);            //37
        clxxProjectionMap.put(CheLiangColumns.Y, CheLiangColumns.Y);            //38
        clxxProjectionMap.put(CheLiangColumns.LRSB, CheLiangColumns.LRSB);      //39
        clxxProjectionMap.put(CheLiangColumns.SFSCZHL, CheLiangColumns.SFSCZHL);//40
        clxxProjectionMap.put(CheLiangColumns.SFSCZLGB, CheLiangColumns.SFSCZLGB);//41

        // 机动车表
        jdcProjectionMap = new LinkedHashMap<String, String>();
        jdcProjectionMap.put(C_CLLX.DM, C_CLLX.DM);
        jdcProjectionMap.put(C_CLLX.DMNR, C_CLLX.DMNR);

        // 号牌种类
        hpzlProjectionMap = new LinkedHashMap<String, String>();
        hpzlProjectionMap.put(C_HPZL.DM, C_HPZL.DM);
        hpzlProjectionMap.put(C_HPZL.DMNR, C_HPZL.DMNR);
        hpzlProjectionMap.put(C_HPZL.BZ, C_HPZL.BZ);

        // 车身颜色
        csysProjectionMap = new LinkedHashMap<String, String>();
        csysProjectionMap.put(C_CSYS.DM, C_CSYS.DM);
        csysProjectionMap.put(C_CSYS.YS, C_CSYS.YS);

        // 便携法规库目录1
        bxfgkml1ProjectionMap = new LinkedHashMap<String, String>();
        bxfgkml1ProjectionMap.put(C_BXFGK_ML1.MLID, C_BXFGK_ML1.MLID);
        bxfgkml1ProjectionMap.put(C_BXFGK_ML1.MLMC, C_BXFGK_ML1.MLMC);

        // 便携法规库目录2
        bxfgkml2ProjectionMap = new LinkedHashMap<String, String>();
        bxfgkml2ProjectionMap.put(C_BXFGK_ML2.MLID, C_BXFGK_ML2.MLID);
        bxfgkml2ProjectionMap.put(C_BXFGK_ML2.MLMC, C_BXFGK_ML2.MLMC);
        bxfgkml2ProjectionMap.put(C_BXFGK_ML2.YJMLID, C_BXFGK_ML2.YJMLID);

        // 便携法规库内容
        bxfgknrProjectionMap = new LinkedHashMap<String, String>();
        bxfgknrProjectionMap.put(C_BXFGK_NR.NRID, C_BXFGK_NR.NRID);
        bxfgknrProjectionMap.put(C_BXFGK_NR.NR, C_BXFGK_NR.NR);
        bxfgknrProjectionMap.put(C_BXFGK_NR.EJMLID, C_BXFGK_NR.EJMLID);

        // 初始化房屋信息表标题映射
        fwxxLabelMap = new LinkedHashMap<String, String>();
        int i = 0;
        for (String column : fwxxProjectionMap.keySet()) {
            fwxxLabelMap.put(column, FANGWU_TABLE_COMMENTS[i++]);
        }

        // 初始化来京人员信息表标题映射
        ljryxxLabelMap = new LinkedHashMap<String, String>();
        i = 0;
        for (String column : ljryxxProjectionMap.keySet()) {
            ljryxxLabelMap.put(column, LAIJINGRENYUAN_TABLE_COMMENTS[i++]);
        }

        // 初始化车辆信息表标题映射
        clxxLabelMap = new LinkedHashMap<String, String>();
        i = 0;
        for (String column : clxxProjectionMap.keySet()) {
            clxxLabelMap.put(column, CHELIANG_TABLE_COMMENTS[i++]);
        }

        // 初始化车辆信息表标题映射
        hmdLabelMap = new LinkedHashMap<String, String>();
        i = 0;
        for (String column : hmdmd5ProjectionMap.keySet()) {
            hmdLabelMap.put(column, HEIMINGDAN_MD5_TABLE_COMMENTS[i++]);
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * 获得列表类型
     */
    @Override
    public String getType(Uri uri) {
        int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case FANGWUXINXI:
                return FangWuColumns.CONTENT_TYPE;

            case FANGZHUXINXI:
                return FangZhuColumns.CONTENT_TYPE;

            case LAIJINGRENYUANXINXI:
                return LaiJingRenYuanColumns.CONTENT_TYPE;

            case HEIMINGDAN:
                return HeiMingDanColumns.CONTENT_TYPE;

            case YUJINGXINXI:
                return YuJingColumns.CONTENT_TYPE;

            case CAOZUORIZHI:
                return CaoZuoRiZhiColumns.CONTENT_TYPE;

            case JIZHUXINXI:
                return JiZhuColumns.CONTENT_TYPE;

            case SHEQU:
                return C_SQ.CONTENT_TYPE;

            case SHEQUGUANLIYUAN:
                return C_SQGLY.CONTENT_TYPE;

            case GONGANDANWEI:
                return C_GADW.CONTENT_TYPE;

            case JIELUXIANG:
                return C_JLX.CONTENT_TYPE;

            case XINGZHENGQUHUA:
                return C_XZQH.CONTENT_TYPE;

            case JIAOHUANSHUJUDINGYI:
                return F_SBSJDY.CONTENT_TYPE;

            case HEIMINGDAN_MD5:
                return T_HMD_MD5.CONTENT_TYPE;

            case CHELIANGXINXI:
                return CheLiangColumns.CONTENT_TYPE;

            case CHELIANGLEIXING:
                return C_CLLX.CONTENT_TYPE;

            case HAOPAIZHONGLEI:
                return C_HPZL.CONTENT_TYPE;

            case CHESHENYANSE:
                return C_CSYS.CONTENT_TYPE;

            case BIANXIEFAGUIKU_MULU1:
                return C_BXFGK_ML1.CONTENT_TYPE;

            case BIANXIEFAGUIKU_MULU2:
                return C_BXFGK_ML2.CONTENT_TYPE;

            case BIANXIEFAGUIKU_NEIRONG:
                return C_BXFGK_NR.CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /**
     * 删除列表数据
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) return 0;

        int count;
        switch (sUriMatcher.match(uri)) {
            case FANGWUXINXI:
                count = db.delete(FANGWUXINXI_TABLE_NAME, selection, selectionArgs);
                break;

            case FANGZHUXINXI:
                count = db.delete(FANGZHUXINXI_TABLE_NAME, selection, selectionArgs);
                break;

            case JINGYUANXINXI:
                count = db.delete(JINGYUANXINXI_TABLE_NAME, selection, selectionArgs);
                break;

            case HEIMINGDAN:
                count = db.delete(HEIMINGDAN_TABLE_NAME, selection, selectionArgs);
                break;

            case YUJINGXINXI:
                count = db.delete(YUJINGXINXI_TABLE_NAME, selection, selectionArgs);
                break;

            case XITONGYONGHU:
                count = db.delete(XITONGYONGHU_TABLE_NAME, selection, selectionArgs);
                break;

            case CAOZUORIZHI:
                count = db.delete(CAOZUORIZHI_TABLE_NAME, selection, selectionArgs);
                break;

            case JIZHUXINXI:
                count = db.delete(JIZHUXINXI_TABLE_NAME, selection, selectionArgs);
                break;

            case XINGZHENGQUHUA:
                count = db.delete(XINGZHENGQUHUA_TABLE_NAME, selection, selectionArgs);
                break;

            case GONGANDANWEI:
                count = db.delete(GONGANDANWEI_TABLE_NAME, selection, selectionArgs);
                break;

            case JIELUXIANG:
                count = db.delete(JIELUXIANG_TABLE_NAME, selection, selectionArgs);
                break;

            case SHEQU:
                count = db.delete(SHEQU_TABLE_NAME, selection, selectionArgs);
                break;

            case SHEQUGUANLIYUAN:
                count = db.delete(SHEQUGUANLIYUAN_TABLE_NAME, selection, selectionArgs);
                break;

            case HEIMINGDAN_MD5:
                count = db.delete(HEIMINGDAN_MD5_TABLE_NAME, selection, selectionArgs);
                break;

            case CHELIANGXINXI:
                count = db.delete(CHELIANGXINXI_TABLE_NAME, selection, selectionArgs);
                break;

            case CHELIANGLEIXING:
                count = db.delete(CHELIANGLEIXING_TABLE_NAME, selection, selectionArgs);
                break;

            case HAOPAIZHONGLEI:
                count = db.delete(HAOPAIZHONGLEI_TABLE_NAME, selection, selectionArgs);
                break;

            case CHESHENYANSE:
                count = db.delete(CHESHENYANSE_TABLE_NAME, selection, selectionArgs);
                break;

            case BIANXIEFAGUIKU_MULU1:
                count = db.delete(BIANXIEFAGUIKU_MULU1_TABLE_NAME, selection, selectionArgs);
                break;

            case BIANXIEFAGUIKU_MULU2:
                count = db.delete(BIANXIEFAGUIKU_MULU2_TABLE_NAME, selection, selectionArgs);
                break;

            case BIANXIEFAGUIKU_NEIRONG:
                count = db.delete(BIANXIEFAGUIKU_NEIRONG_TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        dbHelper.mContext.getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * 插入列表数据
     */
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = -1;

        switch (sUriMatcher.match(uri)) {
            case FANGWUXINXI:
                if (db != null) {
                    rowId = db.insert(FANGWUXINXI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(FangWuColumns.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case FANGZHUXINXI:
                if (db != null) {
                    rowId = db.insert(FANGZHUXINXI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(FangZhuColumns.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case LAIJINGRENYUANXINXI:
                if (db != null) {
                    rowId = db.insert(LAIJINGRENYUANXINXI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(LaiJingRenYuanColumns.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case HEIMINGDAN:
                if (db != null) {
                    rowId = db.insert(HEIMINGDAN_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(HeiMingDanColumns.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case YUJINGXINXI:
                if (db != null) {
                    rowId = db.insert(YUJINGXINXI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(YuJingColumns.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case CAOZUORIZHI:
                if (db != null) {
                    rowId = db.insert(CAOZUORIZHI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(CaoZuoRiZhiColumns.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case JIZHUXINXI:
                if (db != null) {
                    rowId = db.insert(JIZHUXINXI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(JiZhuColumns.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case SHEQU:
                if (db != null) {
                    rowId = db.insert(SHEQU_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_SQ.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case SHEQUGUANLIYUAN:
                if (db != null) {
                    rowId = db.insert(SHEQUGUANLIYUAN_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_SQGLY.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case GONGANDANWEI:
                if (db != null) {
                    rowId = db.insert(GONGANDANWEI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_GADW.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case JIELUXIANG:
                if (db != null) {
                    rowId = db.insert(JIELUXIANG_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_JLX.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case XINGZHENGQUHUA:
                if (db != null) {
                    rowId = db.insert(XINGZHENGQUHUA_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_XZQH.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case JIAOHUANSHUJUDINGYI:
                if (db != null) {
                    rowId = db.insert(JIAOHUANSHUJUDINGYI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(F_SBSJDY.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case HEIMINGDAN_MD5:
                if (db != null) {
                    rowId = db.insert(HEIMINGDAN_MD5_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(T_HMD_MD5.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case CHELIANGXINXI:
                if (db != null) {
                    rowId = db.insert(CHELIANGXINXI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(CheLiangColumns.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case CHELIANGLEIXING:
                if (db != null) {
                    rowId = db.insert(CHELIANGLEIXING_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_CLLX.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case HAOPAIZHONGLEI:
                if (db != null) {
                    rowId = db.insert(HAOPAIZHONGLEI_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_HPZL.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case CHESHENYANSE:
                if (db != null) {
                    rowId = db.insert(CHESHENYANSE_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_CSYS.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case BIANXIEFAGUIKU_MULU1:
                if (db != null) {
                    rowId = db.insert(BIANXIEFAGUIKU_MULU1_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_BXFGK_ML1.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case BIANXIEFAGUIKU_MULU2:
                if (db != null) {
                    rowId = db.insert(BIANXIEFAGUIKU_MULU2_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_BXFGK_ML2.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            case BIANXIEFAGUIKU_NEIRONG:
                if (db != null) {
                    rowId = db.insert(BIANXIEFAGUIKU_NEIRONG_TABLE_NAME, null, values);
                }
                if (rowId > -1) {
                    Uri dataUri = ContentUris.withAppendedId(C_BXFGK_NR.CONTENT_URI, rowId);
                    dbHelper.mContext.getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /**
     * 查询列表数据
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case FANGWUXINXI:
                qb.setTables(FANGWUXINXI_TABLE_NAME);
                qb.setProjectionMap(fwxxProjectionMap);
                break;

            case FANGZHUXINXI:
                qb.setTables(FANGZHUXINXI_TABLE_NAME);
                qb.setProjectionMap(fzxxProjectionMap);
                break;

            case LAIJINGRENYUANXINXI:
                qb.setTables(LAIJINGRENYUANXINXI_TABLE_NAME);
                qb.setProjectionMap(ljryxxProjectionMap);
                break;

            case HEIMINGDAN:
                qb.setTables(HEIMINGDAN_TABLE_NAME);
                qb.setProjectionMap(hmdProjectionMap);
                break;

            case YUJINGXINXI:
                qb.setTables(YUJINGXINXI_TABLE_NAME);
                qb.setProjectionMap(yjxxProjectionMap);
                break;

            case CAOZUORIZHI:
                qb.setTables(CAOZUORIZHI_TABLE_NAME);
                qb.setProjectionMap(czrzProjectionMap);
                break;

            case JIZHUXINXI:
                qb.setTables(JIZHUXINXI_TABLE_NAME);
                qb.setProjectionMap(jzxxProjectionMap);
                break;

            case SHEQU:
                qb.setTables(SHEQU_TABLE_NAME);
                qb.setProjectionMap(sqProjectionMap);
                break;

            case SHEQUGUANLIYUAN:
                qb.setTables(SHEQUGUANLIYUAN_TABLE_NAME);
                qb.setProjectionMap(sqglyProjectionMap);
                break;

            case GONGANDANWEI:
                qb.setTables(GONGANDANWEI_TABLE_NAME);
                qb.setProjectionMap(gadwProjectionMap);
                break;

            case JIELUXIANG:
                qb.setTables(JIELUXIANG_TABLE_NAME);
                qb.setProjectionMap(jlxProjectionMap);
                break;

            case XINGZHENGQUHUA:
                qb.setTables(XINGZHENGQUHUA_TABLE_NAME);
                qb.setProjectionMap(xzqhProjectionMap);
                break;

            case JIAOHUANSHUJUDINGYI:
                qb.setTables(JIAOHUANSHUJUDINGYI_TABLE_NAME);
                qb.setProjectionMap(sbsjdyProjectionMap);
                break;

            case HEIMINGDAN_MD5:
                qb.setTables(HEIMINGDAN_MD5_TABLE_NAME);
                qb.setProjectionMap(hmdmd5ProjectionMap);
                break;

            case CHELIANGXINXI:
                qb.setTables(CHELIANGXINXI_TABLE_NAME);
                qb.setProjectionMap(clxxProjectionMap);
                break;

            case CHELIANGLEIXING:
                qb.setTables(CHELIANGLEIXING_TABLE_NAME);
                qb.setProjectionMap(jdcProjectionMap);
                break;

            case HAOPAIZHONGLEI:
                qb.setTables(HAOPAIZHONGLEI_TABLE_NAME);
                qb.setProjectionMap(hpzlProjectionMap);
                break;

            case CHESHENYANSE:
                qb.setTables(CHESHENYANSE_TABLE_NAME);
                qb.setProjectionMap(csysProjectionMap);
                break;

            case BIANXIEFAGUIKU_MULU1:
                qb.setTables(BIANXIEFAGUIKU_MULU1_TABLE_NAME);
                qb.setProjectionMap(bxfgkml1ProjectionMap);
                break;

            case BIANXIEFAGUIKU_MULU2:
                qb.setTables(BIANXIEFAGUIKU_MULU2_TABLE_NAME);
                qb.setProjectionMap(bxfgkml2ProjectionMap);
                break;

            case BIANXIEFAGUIKU_NEIRONG:
                qb.setTables(BIANXIEFAGUIKU_NEIRONG_TABLE_NAME);
                qb.setProjectionMap(bxfgknrProjectionMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

            if (c != null)
                c.setNotificationUri(dbHelper.mContext.getContentResolver(), uri);
            return c;
        }

        return null;
    }

    /**
     * 修改列表数据
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int matcher = sUriMatcher.match(uri);
        int count;

        if (db == null) return 0;

        switch (matcher) {
            case FANGWUXINXI:
                count = db.update(FANGWUXINXI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case FANGZHUXINXI:
                count = db.update(FANGZHUXINXI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case LAIJINGRENYUANXINXI:
                count = db.update(LAIJINGRENYUANXINXI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case JINGYUANXINXI:
                count = db.update(JINGYUANXINXI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case HEIMINGDAN:
                count = db.update(HEIMINGDAN_TABLE_NAME, values, selection, selectionArgs);
                break;

            case YUJINGXINXI:
                count = db.update(YUJINGXINXI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case XITONGYONGHU:
                count = db.update(XITONGYONGHU_TABLE_NAME, values, selection, selectionArgs);
                break;

            case CAOZUORIZHI:
                count = db.update(CAOZUORIZHI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case JIZHUXINXI:
                count = db.update(JIZHUXINXI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case HEIMINGDAN_MD5:
                count = db.update(HEIMINGDAN_MD5_TABLE_NAME, values, selection, selectionArgs);
                break;

            case CHELIANGXINXI:
                count = db.update(CHELIANGXINXI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case CHELIANGLEIXING:
                count = db.update(CHELIANGLEIXING_TABLE_NAME, values, selection, selectionArgs);
                break;

            case HAOPAIZHONGLEI:
                count = db.update(HAOPAIZHONGLEI_TABLE_NAME, values, selection, selectionArgs);
                break;

            case CHESHENYANSE:
                count = db.update(CHESHENYANSE_TABLE_NAME, values, selection, selectionArgs);
                break;

            case BIANXIEFAGUIKU_MULU1:
                count = db.update(BIANXIEFAGUIKU_MULU1_TABLE_NAME, values, selection, selectionArgs);
                break;

            case BIANXIEFAGUIKU_MULU2:
                count = db.update(BIANXIEFAGUIKU_MULU2_TABLE_NAME, values, selection, selectionArgs);
                break;

            case BIANXIEFAGUIKU_NEIRONG:
                count = db.update(BIANXIEFAGUIKU_NEIRONG_TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        dbHelper.mContext.getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * 实行SQL查询
     */
    public Cursor runRawQuery(String sql, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(sql, selectionArgs);
    }

    /**
     * 获得数据数量
     */
    public long fetchCount(String tableName, String where) {
        if (TextUtils.isEmpty(tableName)) return 0;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql;
        if (TextUtils.isEmpty(where))
            sql = "SELECT COUNT(*) FROM " + tableName;
        else
            sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + where;

        if (Config.DEBUG) Log.i(TAG, "fetchCount(), sql = " + sql);

        SQLiteStatement statement = db.compileStatement(sql);
        return statement.simpleQueryForLong();
    }

    /**
     * 计数空字段数
     */
    public long fetchEmptyValueCount(String tableName, String[] fields, String where) {
        if (fields == null || fields.length == 0) return 0;

        String sql = "SELECT (" + fields.length + " * COUNT(*)) - (";

        for (int i = 0; i < fields.length; i++) {
            if (i != 0) sql += " + ";
            sql += ("COUNT(" + fields[i] + ")");
        }
        sql += (") FROM " + tableName);

        if (!TextUtils.isEmpty(where))
            sql += (" WHERE " + where);

        if (Config.DEBUG) Log.i(TAG, "fetchEmptyValueCount(), sql = " + sql);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SQLiteStatement statement = db.compileStatement(sql);
        return statement.simpleQueryForLong();
    }

    /**
     * 计数录入字段数
     */
    public long fetchInputtedValueCountInUnNeeded(String tableName, String[] fields, String where) {
        if (fields == null || fields.length == 0) return 0;

        String sql = "SELECT (";

        for (int i = 0; i < fields.length; i++) {
            if (i != 0) sql += " + ";
            sql += ("COUNT(" + fields[i] + ")");
        }
        sql += (") FROM " + tableName);

        if (!TextUtils.isEmpty(where))
            sql += (" WHERE " + where);

        if (Config.DEBUG) Log.i(TAG, "fetchInputtedValueCountInUnNeeded(), sql = " + sql);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SQLiteStatement statement = db.compileStatement(sql);
        return statement.simpleQueryForLong();
    }

    /**
     * 添加系统日志
     */
    public void addSystemLog(OwnerData ownerData, String moduleName, String tableName, String id,
                             String operation, String operator) {
        Date currentDate = new Date();

        ContentValues values = new ContentValues();
        values.put(CaoZuoRiZhiColumns.CZXXID, ownerData.MJID
                + CommonUtils.getFormattedDateString(currentDate, "yyyyMMddHHmmss"));
        values.put(CaoZuoRiZhiColumns.CZRID, ownerData.MJID);
        values.put(CaoZuoRiZhiColumns.CZRJH, ownerData.MJJH);
        values.put(CaoZuoRiZhiColumns.CZRXM, ownerData.MJXM);
        values.put(CaoZuoRiZhiColumns.SSDWID, ownerData.SSDWID);
        values.put(CaoZuoRiZhiColumns.SSDWMC, ownerData.SSDWMC);
        values.put(CaoZuoRiZhiColumns.CZMK, moduleName);
        values.put(CaoZuoRiZhiColumns.CZSJID, id);
        values.put(CaoZuoRiZhiColumns.CZSJLX, tableName);
        values.put(CaoZuoRiZhiColumns.CZMS, operation);
        values.put(CaoZuoRiZhiColumns.CZRLX, operator);
        values.put(CaoZuoRiZhiColumns.CZSJ, CommonUtils.getFormattedDateString(currentDate, "yyyy年MM月dd日 HH:mm:ss"));

        try {
            insert(CaoZuoRiZhiColumns.CONTENT_URI, values);
        } catch (android.database.SQLException ex) {
        }
    }

    /**
     * 检查有无代码字典表
     */
    public boolean checkConstantTablesExist() {
        // 行政区划表
        long count = fetchCount(CommonUtils.getLastPathFromUri(C_XZQH.CONTENT_URI), "");
        if (count == 0) return false;

        // 公安单位表
        count = fetchCount(CommonUtils.getLastPathFromUri(C_GADW.CONTENT_URI), "");
        if (count == 0) return false;

        // 街路巷表
        count = fetchCount(CommonUtils.getLastPathFromUri(C_JLX.CONTENT_URI), "");
        if (count == 0) return false;

        // 社区表
        count = fetchCount(CommonUtils.getLastPathFromUri(C_SQ.CONTENT_URI), "");
        if (count == 0) return false;

        // 社区管理员表
        count = fetchCount(CommonUtils.getLastPathFromUri(C_SQGLY.CONTENT_URI), "");
        if (count == 0) return false;

        // 机动车
        count = fetchCount(CommonUtils.getLastPathFromUri(C_CLLX.CONTENT_URI), "");
        if (count == 0) return false;

        // 号牌种类
        count = fetchCount(CommonUtils.getLastPathFromUri(C_HPZL.CONTENT_URI), "");
        if (count == 0) return false;

        // 车身颜色
        count = fetchCount(CommonUtils.getLastPathFromUri(C_CSYS.CONTENT_URI), "");
        if (count == 0) return false;

        // 便携法规 - 主目录
        count = fetchCount(CommonUtils.getLastPathFromUri(C_BXFGK_ML1.CONTENT_URI), "");
        if (count == 0) return false;

        // 便携法规 - 分目录
        count = fetchCount(CommonUtils.getLastPathFromUri(C_BXFGK_ML2.CONTENT_URI), "");
        if (count == 0) return false;

        // 便携法规 - 内容
        count = fetchCount(CommonUtils.getLastPathFromUri(C_BXFGK_NR.CONTENT_URI), "");
        if (count == 0) return false;

        return true;
    }

    /**
     *
     */
    public Uri getContentUri(String tableName) {
        return mUriMap.get(tableName);
    }

    /**
     * 获得民警ID
     */
    public String getPolicemanID() {
        String id = null;
        Cursor cursor = query(JiZhuColumns.CONTENT_URI, new String[]{JiZhuColumns.MJID}, null, null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst())
                id = cursor.getString(0);

            cursor.close();
        }

        return id;
    }

    public ArrayList<String> getDistrictNames() {
        ArrayList<String> arrayList = new ArrayList<String>();

        Cursor cursor = query(C_SQ.CONTENT_URI, new String[]{C_SQ.SQMC}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    arrayList.add(cursor.getString(cursor.getColumnIndex(C_SQ.SQMC)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return arrayList;
    }

    public LinkedHashMap<String, String> getManagerInfoMapFromDistrictName(String districtName) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
        String districtID = "";

        // Find SheQu id from SheQu name
        Cursor cursor = query(C_SQ.CONTENT_URI, new String[]{C_SQGLY.SQID},
                C_SQ.SQMC + "='" + districtName + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                districtID = cursor.getString(cursor.getColumnIndex(C_SQGLY.SQID));
            }

            cursor.close();
        }

        // Find GuanLiYuan name from SheQu id
        if (!TextUtils.isEmpty(districtID)) {
            cursor = query(C_SQGLY.CONTENT_URI, new String[]{C_SQGLY.GLYXM, C_SQGLY.GLYBH},
                    C_SQGLY.SQID + "='" + districtID + "'", null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(C_SQGLY.GLYXM));
                        String id = cursor.getString(cursor.getColumnIndex(C_SQGLY.GLYBH));

                        hashMap.put(name, id);
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }
        }

        return hashMap;
    }

    public ArrayList<String> getStreetNames(String districtName) {
        ArrayList<String> streetNames = new ArrayList<String>();
        String districtID = "";

        // Find SheQu id from SheQu name
        Cursor cursor = query(C_SQ.CONTENT_URI, new String[]{C_SQGLY.SQID},
                C_SQ.SQMC + "='" + districtName + "'", null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                districtID = cursor.getString(cursor.getColumnIndex(C_SQGLY.SQID));
            }

            cursor.close();
        }

        // Find Street name from SheQu id
        if (!TextUtils.isEmpty(districtID)) {
            cursor = query(C_JLX.CONTENT_URI, new String[]{C_JLX.JLXMC},
                    C_JLX.GSSQID + "='" + districtID + "'", null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        streetNames.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }
        }

        return streetNames;
    }

    public String getFenJuNameFromPaiChuSuoID(String paichusuoID) {
        String name = "";
        String id = "";

        // 使用单位ID找到上级单位ID
        Cursor cursor = query(C_GADW.CONTENT_URI, new String[]{C_GADW.SJDWID},
                C_GADW.DWID + "='" + paichusuoID + "'", null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndex(C_GADW.SJDWID));
            }
            cursor.close();
        }

        // 使用上级单位ID找到上级单位名称
        if (!TextUtils.isEmpty(id)) {
            cursor = query(C_GADW.CONTENT_URI, new String[]{C_GADW.DWMC},
                    C_GADW.DWID + "='" + id + "'", null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(C_GADW.DWMC));
                }
                cursor.close();
            }
        }

        return name;
    }

    public ArrayList<String> getSheng() {
        ArrayList<String> arrayList = new ArrayList<String>();

        // "select XZQH,MC from C_XZQH where XZQH like '%0000' order by XZQH"
        Cursor cursor = query(C_XZQH.CONTENT_URI, new String[]{C_XZQH.XZQH, C_XZQH.MC},
                C_XZQH.XZQH + " LIKE '%0000'", null, C_XZQH.SORT_ORDER_DEFAULT);

        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    arrayList.add(cursor.getString(cursor.getColumnIndex(C_XZQH.MC)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return arrayList;
    }

    public ArrayList<String> getShi(String strSheng) {
        ArrayList<String> arrayList = new ArrayList<String>();
        String shengCode = "";

        // Find code of Sheng name
        Cursor cursor = query(C_XZQH.CONTENT_URI, new String[]{C_XZQH.XZQH},
                C_XZQH.MC + " LIKE '" + strSheng + "'", null, C_XZQH.SORT_ORDER_DEFAULT);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                shengCode = cursor.getString(cursor.getColumnIndex(C_XZQH.XZQH));
            }

            cursor.close();
        }

        // "select XZQH,MC from C_XZQH where XZQH like '"+strSHENG.substring(0,2)+"%' order by XZQH"
        if (!TextUtils.isEmpty(shengCode)) {
            cursor = query(C_XZQH.CONTENT_URI, new String[]{C_XZQH.XZQH, C_XZQH.MC},
                    C_XZQH.XZQH + " LIKE '" + shengCode.substring(0, 2) + "%'", null, C_XZQH.SORT_ORDER_DEFAULT);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        String strID = cursor.getString(cursor.getColumnIndex(C_XZQH.XZQH));
                        String strMC = cursor.getString(cursor.getColumnIndex(C_XZQH.MC));

                        if (strID.substring(2).equals("0000")) {
                            // 滤掉省（直辖市、自治州）
                        } else {
                            if (strID.substring(0, 2).equals("11") ||        // 北京
                                    strID.substring(0, 2).equals("12") ||    // 天津
                                    strID.substring(0, 2).equals("31") ||    // 上海
                                    strID.substring(0, 2).equals("50")) {    // 重庆
                                if (strID.substring(4).equals("00")) {
                                    // 滤掉市辖区
                                } else {
                                    arrayList.add(strMC);
                                }
                            } else {
                                if (strID.substring(2, 4).equals("90")) {
                                    if (strID.substring(4).equals("00")) {
                                        // 滤掉省直辖县级行政区划
                                    } else {
                                        arrayList.add(strMC);
                                    }
                                } else {
                                    if (strID.substring(4).equals("00")) {
                                        arrayList.add(strMC);
                                    } // 滤掉市辖区以下
                                }
                            }
                        }
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }
        }

        return arrayList;
    }

    public ArrayList<String> getShi1(String strSheng) {
        ArrayList<String> arrayList = new ArrayList<String>();
        String shengCode = "";

        // Find code of Sheng name
        Cursor cursor = query(C_XZQH.CONTENT_URI, new String[]{C_XZQH.XZQH},
                C_XZQH.MC + " LIKE '" + strSheng + "'", null, C_XZQH.SORT_ORDER_DEFAULT);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                shengCode = cursor.getString(cursor.getColumnIndex(C_XZQH.XZQH));
            }

            cursor.close();
        }

        // "select XZQH,MC from C_XZQH where XZQH like '"+strSHENG.substring(0,2)+"%' order by XZQH"
        if (!TextUtils.isEmpty(shengCode)) {
            cursor = query(C_XZQH.CONTENT_URI, new String[]{C_XZQH.XZQH, C_XZQH.MC},
                    C_XZQH.XZQH + " LIKE '" + shengCode.substring(0, 2) + "%'", null, C_XZQH.SORT_ORDER_DEFAULT);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        String strID = cursor.getString(cursor.getColumnIndex(C_XZQH.XZQH));
                        String strMC = cursor.getString(cursor.getColumnIndex(C_XZQH.MC));

                        if (strID.substring(2).equals("0000")) {
                            if (strID.substring(0, 2).equals("11") ||        // 北京
                                    strID.substring(0, 2).equals("12") ||    // 天津
                                    strID.substring(0, 2).equals("31") ||    // 上海
                                    strID.substring(0, 2).equals("50")) {    // 重庆
                                arrayList.add(strMC);
                            } else {
                                // 滤掉非直辖市
                            }
                        } else {
                            if (strID.substring(0, 2).equals("11") ||        // 北京
                                    strID.substring(0, 2).equals("12") ||    // 天津
                                    strID.substring(0, 2).equals("31") ||    // 上海
                                    strID.substring(0, 2).equals("50")) {    // 重庆
                                // 滤掉直辖市区县
                            } else {
                                if (strID.substring(4).equals("00")) {
                                    if (strID.substring(2, 4).equals("90")) {
                                        // 滤掉省直辖县级行政区划
                                    } else {
                                        arrayList.add(strMC);
                                    }
                                } else {
                                    // 滤掉市辖区以下
                                    if (strID.substring(2, 4).equals("90")) {
                                        // 显示省直辖县级行政区划
                                        arrayList.add(strMC);
                                    }
                                }
                            }
                        }
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }
        }

        return arrayList;
    }

    public ArrayList<String> getQX(String strShi) {
        ArrayList<String> arrayList = new ArrayList<String>();
        String shiCode = "";

        // Find code of Sheng name
        Cursor cursor = query(C_XZQH.CONTENT_URI, new String[]{C_XZQH.XZQH},
                C_XZQH.MC + " LIKE '" + strShi + "'", null, C_XZQH.SORT_ORDER_DEFAULT);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                shiCode = cursor.getString(cursor.getColumnIndex(C_XZQH.XZQH));
            }

            cursor.close();
        }

        if (!TextUtils.isEmpty(shiCode)) {
            String where;

            if (shiCode.substring(0, 2).equals("11") ||        // 北京
                    shiCode.substring(0, 2).equals("12") ||    // 天津
                    shiCode.substring(0, 2).equals("31") ||    // 上海
                    shiCode.substring(0, 2).equals("50")) {    // 重庆
                where = C_XZQH.XZQH + " LIKE '" + shiCode.substring(0, 2) + "%'";
            } else {
                where = C_XZQH.XZQH + " LIKE '" + shiCode.substring(0, 4) + "%'";
            }

            cursor = query(C_XZQH.CONTENT_URI, new String[]{C_XZQH.XZQH, C_XZQH.MC},
                    where, null, C_XZQH.SORT_ORDER_DEFAULT);

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        String strID = cursor.getString(cursor.getColumnIndex(C_XZQH.XZQH));
                        String strMC = cursor.getString(cursor.getColumnIndex(C_XZQH.MC));

                        if (strID.substring(2).equals("0000")) {
                            // 滤掉省（直辖市、自治州）
                        } else {
                            if (strID.substring(0, 2).equals("11") ||        // 北京
                                    strID.substring(0, 2).equals("12") ||    // 天津
                                    strID.substring(0, 2).equals("31") ||    // 上海
                                    strID.substring(0, 2).equals("50")) {    // 重庆
                                if (strID.substring(4).equals("00")) {
                                    // 滤掉市辖区
                                } else {
                                    arrayList.add(strMC);
                                }
                            } else {
                                if (strID.substring(2, 4).equals("90")) {
                                    // 滤掉省直辖县级行政区划
                                } else {
                                    if (strID.substring(4).equals("00") || strMC.equals("市辖区")) {
                                        // 滤掉市辖区
                                    } else {
                                        arrayList.add(strMC);
                                    }
                                }
                            }
                        }
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }
        }

        return arrayList;
    }

    public ArrayList<String> getVehicleColor() {
        ArrayList<String> arrayList = new ArrayList<String>();

        Cursor cursor = query(C_CSYS.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    arrayList.add(cursor.getString(cursor.getColumnIndex(C_CSYS.YS)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return arrayList;
    }

    public ArrayList<String> getVehicleType() {
        ArrayList<String> arrayList = new ArrayList<String>();

        Cursor cursor = query(C_CLLX.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    arrayList.add(cursor.getString(cursor.getColumnIndex(C_CLLX.DMNR)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return arrayList;
    }

    public ArrayList<String> getVehicleNumberType() {
        ArrayList<String> arrayList = new ArrayList<String>();

        Cursor cursor = query(C_HPZL.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    arrayList.add(cursor.getString(cursor.getColumnIndex(C_HPZL.DMNR)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return arrayList;
    }

    /**
     * DatabaseHelper for English Learning Method
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        //private SQLiteDatabase mDatabase;
        private final Context mContext;

        /**
         * Constructor
         * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
         *
         * @param context Application context for managing database
         */
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
            try {
                createDatabase();
            } catch (IOException e) {
                if (Config.DEBUG) e.printStackTrace();
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        /**
         * Creates a empty database on the system and rewrites it with your own database.
         */
        public void createDatabase() throws IOException {
            boolean dbExist = checkDatabase();

            if (dbExist) {
                //do nothing - database already exist
                Log.d(TAG, "createDatabase(): Database already exists!");
            } else {
                //By calling this method and empty database will be created into the default system path
                //of your application so we are gonna be able to overwrite that database with our database.
                this.getReadableDatabase();

                try {
                    copyDatabase();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }
        }

        /**
         * Check if the database already exist to avoid re-copying the file each time you open the application.
         *
         * @return true if it exists, false if it doesn't
         */
        private boolean checkDatabase() {
            SQLiteDatabase checkDB = null;

            try {
                String myPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            } catch (SQLiteException e) {
                //database doesn't exist yet.
                if (Config.DEBUG) e.printStackTrace();
            }

            if (checkDB != null) {
                checkDB.close();
            }

            return checkDB != null;
        }

        /**
         * Copies your database from your local assets-folder to the just created empty database in the
         * system folder, from where it can be accessed and handled.
         * This is done by transferring byte stream.
         */
        private void copyDatabase() throws IOException {
            //Open your local db as the input stream
            InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

            // Path to the just created empty db
            String outFileName = mContext.getDatabasePath(DATABASE_NAME).getPath();

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        /*
        public void openDatabase() throws SQLException {
            //Open the database
            String myPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
            mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }

        @Override
        public synchronized void close() {
            if (mDatabase != null)
                mDatabase.close();
            super.close();
        }*/
    }

}
