/**
 * @author LuYongXing
 * @date 2014.08.20
 * @filename Config.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

public class Config {

    public static final boolean DEBUG = false;

    /**
     * directory name to store csv/xls files would be imported
     */
    public static final String IMPORT_DIRECTORY_NAME = "导入";

    /**
     * directory name to store exported csv/xls files
     */
    public static final String EXPORT_DIRECTORY_NAME = "导出";

    /**
     * directory name to store captured images and videos
     */
    public static final String IMAGE_DIRECTORY_NAME = "图像";

    /**
     * Load count in once time
     */
    public static final int ONCE_LOAD_RECORD_COUNT = 10;

    /**
     * Room photo count
     */
    public static final int MAX_ROOM_PHOTO_COUNT = 10;

    /**
     * Person photo count
     */
    public static final int MAX_PERSON_PHOTO_COUNT = 3;

    /**
     * Car photo count
     */
    public static final int MAX_VEHICLE_PHOTO_COUNT = 3;

}
