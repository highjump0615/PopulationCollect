/**
 * @author LuYongXing
 * @date 2014.09.15
 * @filename XLSUtil.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.ProgressUpdateListener;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CheLiangColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.T_HMD_MD5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class XLSUtil {

    private static final String TAG = XLSUtil.class.getSimpleName();

    private static final String ROOM_PICTURE_DIRECTORY = "fwxxPic";
    private static final String ROOM_VIDEO_DIRECTORY = "FwxxVideo";
    private static final String PERSON_PICTURE_DIRECTORY = "ryxxPic";
    private static final String VEHICLE_PICTURE_DIRECTORY = "clxxPic";

    private static final int ROOM_COLUMN_COUNT = 100;
    private static final int PERSON_COLUMN_COUNT = 92;

    public static final int FANGWU_TABLE_INDEX = 0;
    public static final int RENYUAN_TABLE_INDEX = 1;
    public static final int CHELIANG_TABLE_INDEX = 2;
    public static final int HEIMINGDAN_MD5_TABLE_INDEX = 3;

    private static final String[] EXPORT_FILE_NAMES = {
            "fwxx.xls",
            "ryxx.xls",
            "clxx.xls",
    };

    private static final String[] TABLE_LABELS = {
            "大兴分局流动人口管理系统房屋信息",
            "大兴分局流动人口管理系统流动(来京)人员信息",
            "大兴分局流动人口管理系统车辆信息",
            "数据",
    };

    public static class XLSFileInfo {
        public boolean isXLS;
        public int tableIndex;
        public int recordCount;
    }

    /**
     * 导出数据库的列表到XLS文件
     */
    public static String exportToXLS(Context context, String path, int tableIndex, Cursor cursor, ProgressUpdateListener progressListener) {
        if (cursor == null || cursor.getCount() == 0) return null;

        String startDate = "", endDate = "";
        if (cursor.moveToFirst()) {
            startDate = cursor.getString(cursor.getColumnIndex("LRSJ"));
            startDate = CommonUtils.convertDateFormat("yyyy-MM-dd HH:mm:ss",
                    "yyyyMMddHHmmss", startDate);
        }
        if (cursor.moveToLast()) {
            endDate = cursor.getString(cursor.getColumnIndex("LRSJ"));
            endDate = CommonUtils.convertDateFormat("yyyy-MM-dd HH:mm:ss",
                    "yyyyMMddHHmmss", endDate);
        }

        // select title map
        int columnCount = cursor.getColumnCount();
        HashMap<String, String> labelMap;

        if (tableIndex == FANGWU_TABLE_INDEX) {
            labelMap = MyContentProvider.fwxxLabelMap;
            columnCount = ROOM_COLUMN_COUNT;
        } else if (tableIndex == RENYUAN_TABLE_INDEX) {
            labelMap = MyContentProvider.ljryxxLabelMap;
            columnCount = PERSON_COLUMN_COUNT;
        } else if (tableIndex == CHELIANG_TABLE_INDEX) {
            labelMap = MyContentProvider.clxxLabelMap;
        } else {
            labelMap = null;
        }

        // compose file name
        //String fileName = TABLE_LABELS[tableIndex] + "(" + startDate + "~" + endDate + ").xls";
        //File file = CommonUtils.getExportFile(fileName);
        String fileName = EXPORT_FILE_NAMES[tableIndex];
        File file = new File(path, fileName);

        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file);

            workbook.createSheet("sheet1", 0);
            WritableSheet sheet = workbook.getSheet(0);

            WritableCellFormat titleFormat = new WritableCellFormat();
            titleFormat.setAlignment(Alignment.CENTRE);
            titleFormat.setBackground(Colour.PALE_BLUE);
            WritableFont cellFont = new WritableFont(WritableFont.COURIER);
            cellFont.setBoldStyle(WritableFont.BOLD);
            titleFormat.setFont(cellFont);

            WritableCellFormat columnFormat = new WritableCellFormat();
            columnFormat.setAlignment(Alignment.CENTRE);
            columnFormat.setBackground(Colour.PALE_BLUE);
            columnFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            //columnFormat.setWrap(true);
            CellView cellView = new CellView();
            cellView.setFormat(columnFormat);
            cellView.setAutosize(true);

            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setAlignment(Alignment.CENTRE);
            cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            //cellFormat.setWrap(true);

            int row = 0;
            int col = 0;
            cursor.moveToFirst();

            // Set Title
            Label label = new Label(col, row, TABLE_LABELS[tableIndex] + "(" + startDate + "~" + endDate + ")", titleFormat);
            sheet.addCell(label);
            row++;

            // Set Fixed Column
            for (col = 0; col < columnCount; col++) {
                label = new jxl.write.Label(col, row, labelMap.get(cursor.getColumnName(col)), columnFormat);
                sheet.addCell(label);
                //sheet.setColumnView(col, cellView);
            }
            row++;

            //merge cell for title
            sheet.mergeCells(0, 0, columnCount - 1, 0);

            // Set Data
            String str;
            int progress = 0;

            do {
                for (col = 0; col < columnCount; col++) {
                    String title = cursor.getColumnName(col);

                    switch (cursor.getType(col)) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            str = String.format("%d", cursor.getInt(col));

                            if (/*title.equals("SFSC") || title.equals("SFSCZFWQ")
                                    || title.equals("SFSCZHL") || title.equals("SFSCZLGB")
                                    || title.equals("SFJZ") || */title.equals(LaiJingRenYuanColumns.SFZWSFZH)) {
                                str = "0".equals(str) ? "否" : "是";
                            }/* else if (title.equals("LRSB")) {
                                str = "0".equals(str) ? "公寓" : "手特机";
                            }*/
                            break;

                        case Cursor.FIELD_TYPE_STRING:
                            str = cursor.getString(col);

                            /*if (cursor.getColumnName(col).equals("ZPURL")
                                    || cursor.getColumnName(col).equals("SXURL")) {
                                try {
                                    str = String.format("%s", URLDecoder.decode(str, "UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                    str = String.format("%s", str);
                                }
                            }*/
                            break;

                        default:
                            Log.d(TAG, "missed field = " + cursor.getColumnName(col));
                            str = null;
                            break;
                    }

                    label = new jxl.write.Label(col, row, str, cellFormat);
                    sheet.addCell(label);
                }

                extractPhoto(context, path, tableIndex, cursor);

                if (progressListener != null) progressListener.progressChanged(++progress);
                row++;
            } while (cursor.moveToNext());

            //close jxl objects
            workbook.write();
            workbook.close();

            return file.toString();
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
        } catch (WriteException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return null;
    }

    /**
     * 检查约定的文件格式
     */
    public static XLSFileInfo checkXLS(File xlsFile) {
        XLSFileInfo resultInfo = new XLSFileInfo();
        resultInfo.isXLS = false;

        try {
            Workbook workbook = Workbook.getWorkbook(xlsFile);

            // Get the first sheet
            Sheet sheet = workbook.getSheet(0);

            // Check title
            Cell titleCell = sheet.getCell(0, 0);
            String title = titleCell.getContents();

            if (!TextUtils.isEmpty(title)) {
                if (title.startsWith(TABLE_LABELS[FANGWU_TABLE_INDEX])) {
                    resultInfo.tableIndex = FANGWU_TABLE_INDEX;

                    // Check column title
                    boolean isSame = true;
                    Cell[] columns = sheet.getRow(1);

                    for (int i = 0; i < columns.length; i++) {
                        if (!columns[i].getContents().equals(MyContentProvider.FANGWU_TABLE_COMMENTS[i])) {
                            isSame = false;
                            break;
                        }
                    }

                    resultInfo.isXLS = isSame;
                    resultInfo.recordCount = sheet.getRows() - 2;

                } else if (title.startsWith(TABLE_LABELS[RENYUAN_TABLE_INDEX])) {
                    resultInfo.tableIndex = RENYUAN_TABLE_INDEX;

                    // Check column title
                    boolean isSame = true;
                    Cell[] columns = sheet.getRow(1);

                    for (int i = 0; i < columns.length; i++) {
                        if (!columns[i].getContents().equals(MyContentProvider.LAIJINGRENYUAN_TABLE_COMMENTS[i])) {
                            isSame = false;
                            break;
                        }
                    }

                    resultInfo.isXLS = isSame;
                    resultInfo.recordCount = sheet.getRows() - 2;
                } else if (title.startsWith(TABLE_LABELS[CHELIANG_TABLE_INDEX])) {
                    resultInfo.tableIndex = CHELIANG_TABLE_INDEX;

                    // Check column title
                    boolean isSame = true;
                    Cell[] columns = sheet.getRow(1);

                    for (int i = 0; i < columns.length; i++) {
                        if (!columns[i].getContents().equals(MyContentProvider.CHELIANG_TABLE_COMMENTS[i])) {
                            isSame = false;
                            break;
                        }
                    }

                    resultInfo.isXLS = isSame;
                    resultInfo.recordCount = sheet.getRows() - 2;
                } else if (title.startsWith(TABLE_LABELS[HEIMINGDAN_MD5_TABLE_INDEX])) {
                    resultInfo.tableIndex = HEIMINGDAN_MD5_TABLE_INDEX;

                    // Check column title
                    boolean isSame = true;
                    Cell[] columns = sheet.getRow(1);

                    for (int i = 0; i < columns.length; i++) {
                        if (!columns[i].getContents().equals(MyContentProvider.HEIMINGDAN_MD5_TABLE_COMMENTS[i])) {
                            isSame = false;
                            break;
                        }
                    }

                    resultInfo.isXLS = isSame;
                    resultInfo.recordCount = sheet.getRows() - 2;
                }
            }

            workbook.close();
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
        } catch (BiffException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return resultInfo;
    }

    /**
     * 导入从XLS文件到数据库
     */
    public static boolean importXLS(Context context, File xlsFile, int tableIndex,
                                    ProgressUpdateListener progressListener) {
        try {
            MyContentProvider contentProvider = new MyContentProvider();
            Workbook workbook = Workbook.getWorkbook(xlsFile);

            // Get the first sheet
            Sheet sheet = workbook.getSheet(0);

            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

            int rows = sheet.getRows();
            int columns = sheet.getColumns();

            // Import data
            for (int row = 2; row < rows; row++) {

                // 读取一行并添加到数据库
                ContentValues contentValues = new ContentValues();

                for (int col = 0; col < columns; col++) {
                    String name = sheet.getCell(col, 1).getContents();
                    String value = sheet.getCell(col, row).getContents();

                    if (tableIndex == FANGWU_TABLE_INDEX) {
                        name = (String) CommonUtils.getKeyFromValue(MyContentProvider.fwxxLabelMap, name);
                    } else if (tableIndex == RENYUAN_TABLE_INDEX) {
                        name = (String) CommonUtils.getKeyFromValue(MyContentProvider.ljryxxLabelMap, name);
                    } else if (tableIndex == HEIMINGDAN_MD5_TABLE_INDEX) {
                        name = (String) CommonUtils.getKeyFromValue(MyContentProvider.hmdLabelMap, name);
                    }

                    if (!TextUtils.isEmpty(name)) {
                        if (name.equals("DRSJ"))
                            contentValues.put("DRSJ", CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));

                        if (name.equals("SFSC") || name.equals("SFSCZFWQ")
                                || name.equals("SFSCZHL") || name.equals("SFSCZLGB")
                                || name.equals("SFJZ") || name.equals("SFZWSFZH")) {
                            value = "否".equals(value) ? "0" : "1";
                        } else if (name.equals("LRSB")) {
                            value = "公寓".equals(value) ? "0" : "1";
                        }

                        /*if (name.equals("ZPURL") || name.equals("SXURL")) {
                            try {
                                value = String.format("%s", URLEncoder.encode(value, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                value = String.format("%s", value);
                            }
                        }*/

                        if (!TextUtils.isEmpty(value))
                            contentValues.put(name, value);
                        else
                            contentValues.put(name, (String)null);
                    }
                }

                Uri contentUri = null;
                String where = "";
                boolean isExit = false;

                if (tableIndex == FANGWU_TABLE_INDEX) {
                    contentUri = FangWuColumns.CONTENT_URI;
                    where = FangWuColumns.FWID + " IS '" + contentValues.getAsString(FangWuColumns.FWID) + "'";
                    isExit = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(FangWuColumns.CONTENT_URI), where) > 0;
                } else if (tableIndex == RENYUAN_TABLE_INDEX) {
                    contentUri = LaiJingRenYuanColumns.CONTENT_URI;
                    where = LaiJingRenYuanColumns.RYID + " IS '" + contentValues.getAsString(LaiJingRenYuanColumns.RYID) + "'";
                    isExit = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI), where) > 0;
                } else if (tableIndex == CHELIANG_TABLE_INDEX) {
                    contentUri = CheLiangColumns.CONTENT_URI;
                    where = CheLiangColumns.CLID + " IS '" + contentValues.getAsString(CheLiangColumns.CLID) + "'";
                    isExit = contentProvider.fetchCount(CommonUtils.getLastPathFromUri(CheLiangColumns.CONTENT_URI), where) > 0;
                } else if (tableIndex == HEIMINGDAN_MD5_TABLE_INDEX) {
                    contentUri = T_HMD_MD5.CONTENT_URI;
                    isExit = false;
                    contentValues.put("DRSJ", CommonUtils.getFormattedDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                }

                if (contentValues.size() > 0) {
                    if (isExit) {
                        operations.add(ContentProviderOperation.newUpdate(contentUri)
                                .withSelection(where, null)
                                .withValues(contentValues)
                                .build());
                    } else {
                        operations.add(ContentProviderOperation.newInsert(contentUri)
                                .withValues(contentValues)
                                .build());
                    }
                }

                if (progressListener != null)
                    progressListener.progressChanged(row - 2);
            }

            // Delete original data
            if (tableIndex == HEIMINGDAN_MD5_TABLE_INDEX) {
                operations.add(0, ContentProviderOperation.newDelete(T_HMD_MD5.CONTENT_URI)
                        .withSelection(null, null)
                        .build());
            }

            try {
                context.getContentResolver().applyBatch(MyContentProvider.AUTHORITY, operations);
            } catch (RemoteException e) {
                if (Config.DEBUG) e.printStackTrace();
                return false;
            } catch (OperationApplicationException e) {
                if (Config.DEBUG) e.printStackTrace();
                return false;
            } catch (SQLException e) {
                if (Config.DEBUG) e.printStackTrace();
                return false;
            }

            if (progressListener != null)
                progressListener.progressChanged(rows);

            return true;
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
        } catch (BiffException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return false;
    }

    private static boolean extractPhoto(Context context, String path, int tableIndex, Cursor cursor) {
        String photoExportPath = "";
        String videoExportPath = "";

        ArrayList<String> photoFileNameList = new ArrayList<String>();
        String videoFileName = "";

        if (tableIndex == FANGWU_TABLE_INDEX) {
            photoExportPath = path + File.separator + ROOM_PICTURE_DIRECTORY;
            videoExportPath = path + File.separator + ROOM_VIDEO_DIRECTORY;

            CommonUtils.createDirectory(videoExportPath);
            videoFileName = cursor.getString(cursor.getColumnIndex(FangWuColumns.SXURL));
        } else if (tableIndex == RENYUAN_TABLE_INDEX) {
            photoExportPath = path + File.separator + PERSON_PICTURE_DIRECTORY;
        } else if (tableIndex == CHELIANG_TABLE_INDEX) {
            photoExportPath = path + File.separator + VEHICLE_PICTURE_DIRECTORY;
        }

        CommonUtils.createDirectory(photoExportPath);

        String fileName = cursor.getString(cursor.getColumnIndex("ZPURL"));
        if (!TextUtils.isEmpty(fileName)) {
            photoFileNameList = PhotoUtil.getNamesFromString(fileName);
        }

        for (String name : photoFileNameList) {
            copyFile(context, name, photoExportPath);
        }

        if (!TextUtils.isEmpty(videoFileName)) {
            copyFile(context, videoFileName, videoExportPath);
        }

        return false;
    }

    private static void copyFile(Context context, String filename, String destPath) {
        String fromFileName = CommonUtils.getMyApplicationDirectory(context, Config.IMAGE_DIRECTORY_NAME) + File.separator + filename;

        // Create directory as present by date string
        String dateString = filename.substring(0, 8);

        if (TextUtils.isDigitsOnly(dateString)) {
            String toFileName = destPath + File.separator + dateString;

            CommonUtils.createDirectory(toFileName);
            toFileName += (File.separator + filename);

            CommonUtils.copyFile(fromFileName, toFileName);
        }
    }

}
