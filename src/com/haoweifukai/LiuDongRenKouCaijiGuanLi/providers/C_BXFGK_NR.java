package com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <b>便携法规内容</b>
 * <br>
 * 保存便携法规内容。
 */
public class C_BXFGK_NR implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/" + MyContentProvider.BIANXIEFAGUIKU_NEIRONG_TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.dictionary.bxfgk_nr";

    /**
     * 内容ID：     NRID、VC/60、非空、主键；
     */
    public static final String NRID = "NRID";
    /**
     * 内容：       NR、VC/60、非空；
     */
    public static final String NR = "NR";
    /**
     * 二级目录ID：  EJMLID、VC/60、非空、主键；
     */
    public static final String EJMLID = "EJMLID";

}
