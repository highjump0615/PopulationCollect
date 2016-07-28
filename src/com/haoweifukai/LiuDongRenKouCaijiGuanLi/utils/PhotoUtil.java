/**
 * @author LuYongXing
 * @date 2014.09.28
 * @filename PhotoUtil.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

public class PhotoUtil {

    private static final String TAG = PhotoUtil.class.getSimpleName();

    public static ArrayList<Uri> getUrisFromString(Context context, String str) {
        ArrayList<Uri> arrayList = new ArrayList<Uri>();

        if (!TextUtils.isEmpty(str)) {
            str = Uri.decode(str);
            String[] contents = str.split(",");

            for (String name : contents) {
                arrayList.add(Uri.parse(CommonUtils.getMyApplicationDirectory(context,
                        Config.IMAGE_DIRECTORY_NAME) + File.separator + name));
            }
        }

        return arrayList;
    }

    public static ArrayList<String> getNamesFromString(String str) {
        ArrayList<String> arrayList = new ArrayList<String>();

        if (!TextUtils.isEmpty(str)) {
            str = Uri.decode(str);
            String[] contents = str.split(",");

            Collections.addAll(arrayList, contents);
        }

        return arrayList;
    }

    public static String convertUrisToString(ArrayList<Uri> uriArrayList) {
        String result = "";

        if (uriArrayList != null) {
            for (Uri uri : uriArrayList) {
                result += (CommonUtils.getLastPathFromUri(uri) + ",");
            }

            if (result.endsWith(","))
                result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public static String getPhotoUploadField(Context context, Cursor cursor, boolean hasVideo) {
        if (cursor == null) return "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);

        try {
            String fileName = cursor.getString(cursor.getColumnIndex("ZPURL"));
            if (!TextUtils.isEmpty(fileName)) {
                ArrayList<String> fileNameList = getNamesFromString(fileName);

                for (String name : fileNameList) {
                    printWriter.append(name).append(",");

                    name = CommonUtils.getMyApplicationDirectory(context, Config.IMAGE_DIRECTORY_NAME)
                            + File.separator + name;
                    printWriter.append(URLEncoder.encode(getBase64FromImage(name), "UTF-8")).append("\n");
                }
            }

            if (hasVideo) {
                fileName = cursor.getString(cursor.getColumnIndex("SXURL"));
                printWriter.append(fileName).append(",");

                fileName = CommonUtils.getMyApplicationDirectory(context, Config.IMAGE_DIRECTORY_NAME)
                        + File.separator + fileName;
                printWriter.append(URLEncoder.encode(getBase64FromImage(fileName), "UTF-8")).append("\n");
            }

            String result = byteArrayOutputStream.toString();

            printWriter.close();
            byteArrayOutputStream.close();

            return result;
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return "";
    }

    /*public static OutputStream getPhotoUploadField(Context context, Cursor cursor, boolean hasVideo) {
        if (cursor == null) return null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);

        try {
            printWriter.append("&images=");

            String fileName = cursor.getString(cursor.getColumnIndex("ZPURL"));
            if (!TextUtils.isEmpty(fileName)) {
                ArrayList<String> fileNameList = getNamesFromString(fileName);

                for (String name : fileNameList) {
                    printWriter.append(name).append(",");

                    name = CommonUtils.getMyApplicationDirectory(context, Config.IMAGE_DIRECTORY_NAME)
                            + File.separator + name;
                    printWriter.append(URLEncoder.encode(getBase64FromImage(name), "UTF-8")).append("\n");
                }
            }

            if (hasVideo) {
                fileName = cursor.getString(cursor.getColumnIndex("SXURL"));
                printWriter.append(fileName).append(",");

                fileName = CommonUtils.getMyApplicationDirectory(context, Config.IMAGE_DIRECTORY_NAME)
                        + File.separator + fileName;
                printWriter.append(URLEncoder.encode(getBase64FromImage(fileName), "UTF-8")).append("\n");
            }

            printWriter.close();
            return byteArrayOutputStream;
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return null;
    }*/

    public static String getBase64FromImage(String filename) {
        try {
            FileInputStream inputStream = new FileInputStream(filename);
            int size = inputStream.available();
            byte[] bytes = new byte[size];

            size = inputStream.read(bytes);
            if (Config.DEBUG) Log.i(TAG, "filename = " + filename);
            if (Config.DEBUG) Log.i(TAG, "file size = " + size);

            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
        /*Bitmap bitmap = BitmapFactory.decodeFile(filename);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();


        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);*/
    }

}
