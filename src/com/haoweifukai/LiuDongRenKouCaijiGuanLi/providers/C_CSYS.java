package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>车身颜色</b>
 * <br>
 * 保存车身颜色信息。
 */
public class C_CSYS implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.CHESHENYANSE_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.csys";

    /**
     * 代码：			DM、VC/60、非空、主键；
     */
    public static final String DM = "DM";
    /**
     * 颜色：		   YS、VC/60、非空；
     */
    public static final String YS = "YS";

}
