/**
 * @author LuYongXing
 * @date 2014.08.29
 * @filename CSVUtil.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

import android.database.Cursor;
import android.util.Log;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.CheLiangColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.FangWuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.LaiJingRenYuanColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;


public class CSVUtil {

    public static final String TAG = CSVUtil.class.getSimpleName();

    /**
     * 获得固定项目
     */
    public static String getFixedColumns(String tableName, Cursor cursor) {
        if (cursor == null) return null;

        int columnCount = cursor.getColumnCount();
        HashMap<String, String> labelMap;

        if (CommonUtils.getLastPathFromUri(FangWuColumns.CONTENT_URI).equals(tableName)) {
            labelMap = MyContentProvider.fwxxLabelMap;
            columnCount = labelMap.size();
        } else if (CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI).equals(tableName)) {
            labelMap = (HashMap<String, String>) MyContentProvider.ljryxxLabelMap.clone();
            labelMap.remove(LaiJingRenYuanColumns.QTHY);
            labelMap.remove(LaiJingRenYuanColumns.QTZYCSGZMS);
            labelMap.remove(LaiJingRenYuanColumns.MD5);
            columnCount = labelMap.size();
        } else if (CommonUtils.getLastPathFromUri(CheLiangColumns.CONTENT_URI).equals(tableName)) {
            labelMap = MyContentProvider.clxxLabelMap;
        } else {
            labelMap = null;
        }

        String fixedColumns = "";
        String columnName;

        for (int i = 0; i < columnCount; i++) {
            columnName = cursor.getColumnName(i);

            if (labelMap != null) {
                fixedColumns += labelMap.get(columnName);
            } else {
                fixedColumns += columnName;
            }

            if (i != columnCount - 1)
                fixedColumns += ",";
        }

        return fixedColumns;
    }

    public static String getOneLineOfCSV(String tableName, Cursor cursor, boolean encode) {
        if (cursor == null) return null;

        int columnCount = cursor.getColumnCount();

        if (CommonUtils.getLastPathFromUri(FangWuColumns.CONTENT_URI).equals(tableName)) {
            columnCount = MyContentProvider.fwxxLabelMap.size();
        } else if (CommonUtils.getLastPathFromUri(LaiJingRenYuanColumns.CONTENT_URI).equals(tableName)) {
            columnCount = MyContentProvider.ljryxxLabelMap.size();
        }

        String csv = "";
        String item;

        for (int i = 0; i < columnCount; i++) {
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_INTEGER:
                    item = String.format("%d", cursor.getInt(i));
                    if (encode) {
                        try {
                            item = URLEncoder.encode(item, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                        }
                    }
                    csv += item;
                    break;

                case Cursor.FIELD_TYPE_STRING:
                    item = cursor.getString(i);

                    if (encode) {
                        /*if (!(cursor.getColumnName(i).equals("ZPURL")
                                || cursor.getColumnName(i).equals("SXURL"))) {*/
                            try {
                                item = String.format("%s", URLEncoder.encode(item, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                            }
                        /*}*/
                    }/* else {
                        if (cursor.getColumnName(i).equals("ZPURL")
                                || cursor.getColumnName(i).equals("SXURL")) {
                            try {
                                item = String.format("%s", URLDecoder.decode(item, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                            }
                        }
                    }*/
                    csv += item;
                    break;

                default:
                    if (Config.DEBUG) Log.d(TAG, "missed field = " + cursor.getColumnName(i));
                    item = "";
                    break;
            }

            if (Config.DEBUG)
                Log.d(TAG, "item[" + i + "]: " + cursor.getColumnName(i) + " = " + item);

            if (i != columnCount - 1)
                csv += ",";
        }

        return csv;
    }

    public static boolean writeToCSV(File file, String csv) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            //fileOutputStream.write(csv.getBytes());

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, Charset.forName("GBK"));
            outputStreamWriter.write(csv);

            outputStreamWriter.close();
            fileOutputStream.close();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean readFromCSV(File file, ArrayList<String> columnList, ArrayList<ArrayList<String>> valueList) {
        if (file == null || !file.exists()) return false;

        try {
            CSVParser parser = CSVParser.parse(file, Charset.forName("GBK"), CSVFormat.EXCEL);

            boolean isFixedColumn = true;
            int fixedColumnSize = 0;
            int columnSize;

            for (CSVRecord csvRecord : parser) {

                columnSize = csvRecord.size();

                // Fixed column
                if (isFixedColumn) {
                    if (columnSize > 0) {
                        if (Config.DEBUG) Log.d(TAG, "Fixed Column = " + csvRecord.toString());

                        for (int i = 0; i < columnSize; i++)
                            columnList.add(csvRecord.get(i).trim());

                        isFixedColumn = false;
                        fixedColumnSize = columnSize;
                    } else {
                        Log.e(TAG, "CSV file format error, can't find fixed columns at the begin of this file.");
                    }
                } else {
                    if (columnSize > 1) {
                        if (Config.DEBUG) Log.d(TAG, "value = " + csvRecord.toString());

                        ArrayList<String> oneRecord = new ArrayList<String>();

                        for (int i = 0; i < columnSize; i++) {
                            String value = csvRecord.get(i);


                            if (i < fixedColumnSize) {
                                oneRecord.add(value);
                            } else {
                                Log.e(TAG, "column count of value record exceeds one of fixed column");
                            }
                        }

                        valueList.add(oneRecord);
                    }
                }
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
