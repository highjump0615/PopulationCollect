package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>黑名单MD5表</b>
 * <br>
 * 保存黑名单MD5表。
 */
public class T_HMD_MD5 implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.HEIMINGDAN_MD5_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.hmd_md5";

    /**
     * MD5： 		MD5、VC/60、非空、主键；
     */
    public static final String MD5 = "MD5";
    /**
     * 数据来源：		TYPE、VC/60、非空；录入设备：1手持机，0公寓系统，2流管办系统
     */
    public static final String TYPE = "TYPE";
    /**
     * 导入时间：		DRSJ、DATE；
     */
    public static final String DRSJ = "DRSJ";

}
