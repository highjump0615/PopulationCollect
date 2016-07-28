package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>号牌种类</b>
 * <br>
 * 保存号牌种类。
 */
public class C_HPZL implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.HAOPAIZHONGLEI_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.hpzl";

    /**
     * 代码：			DM、VC/60、非空、主键；
     */
    public static final String DM = "DM";
    /**
     * 代码内容：		   DMNR、VC/60、非空；
     */
    public static final String DMNR = "DMNR";
    /**
     * 备注：			BZ、VC/1000
     */
    public static final String BZ = "BZ";

}
