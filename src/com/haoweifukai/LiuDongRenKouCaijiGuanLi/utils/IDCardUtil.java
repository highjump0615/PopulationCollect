/**
 * @author LuYongXing
 * @date 2014.10.08
 * @filename IDCardUtil.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IDCardUtil {

    private static final String TAG = IDCardUtil.class.getSimpleName();

    // 校验码
    private static final int[] CHECKSUM_WEIGHT = new int[] {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};
    private static final String[] CHECKSUM =  new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};


    public static String verifyCardID(Context context, String id, String name) {
        if (TextUtils.isEmpty(id)) return null;

        //身份证校验

        int l_l_total = 0;
        String strObject = id;

        //位数校验
        boolean isNotOldCard = strObject.length() != 15;
        boolean isNotNewCard = strObject.length() != 18;
        if (isNotOldCard && isNotNewCard) {
            CommonUtils.createErrorAlertDialog(context, name + "身份证必须为15位或18位！").show();
            return null;
        }

        //15校验
        if (strObject.length() == 15) {
            if (!TextUtils.isDigitsOnly(strObject)) {
                CommonUtils.createErrorAlertDialog(context, name + "身份证号输入错误，应全为数字！").show();
                return null;
            }
            //15位转18位
            strObject = convertOldCardID2New(strObject);
        }

        String l_s_temp = strObject.substring(0, 17);
        if (!TextUtils.isDigitsOnly(l_s_temp)) {
            CommonUtils.createErrorAlertDialog(context, name + "身份证号前17位输入错误，应全为数字！").show();
            return null;
        }

        String LastNum = strObject.substring(17, 18);
        if (!TextUtils.isDigitsOnly(LastNum) && !LastNum.equals("x") && !LastNum.equals("X")) {
            CommonUtils.createErrorAlertDialog(context, name + "18位的身份证号最后一位录入错误!").show();
            return null;
        }

        String L_s_temp = strObject.substring(6, 14);
        String year = L_s_temp.substring(0, 4);
        String month = L_s_temp.substring(4, 6);
        String day = L_s_temp.substring(6, 8);
        String L_s_csny = year + "-" + month + "-" + day;

        //是否是合法日期
        if (!isDateFormat(L_s_csny, "yyyy-MM-dd")) {
            CommonUtils.createErrorAlertDialog(context, name + "身份证号的出生年月日不正确！").show();
            return null;
        }
        if (!isValidDate(L_s_csny, "yyyy-MM-dd")) {
            CommonUtils.createErrorAlertDialog(context, name + "身份证号的出生年月日不正确！").show();
            return null;
        }

        for (int i = 0; i < strObject.length() - 1; i++) {
            int val = Integer.parseInt(strObject.substring(i, i+1), 10) * CHECKSUM_WEIGHT[i];
            l_l_total += val;
        }
        if (TextUtils.isDigitsOnly(strObject.substring(17, 18))) {
            l_l_total += Integer.parseInt(strObject.substring(17, 18));
        }
        if (strObject.substring(17, 18).equals("X") || strObject.substring(17, 18).equals("x")) {
            l_l_total += 10;
        }
        l_l_total--;
        if (!(l_l_total % 11 == 0)) {
            CommonUtils.createErrorAlertDialog(context, "输入的18位身份证号不正确!").show();
            return null;
        }

        return strObject;
    }

    public static String getCheckSum(String id) {
        if (TextUtils.isEmpty(id) || id.length() != 17 || !TextUtils.isDigitsOnly(id)) return "";

        int total = 0;

        for (int i = 0; i < id.length(); i++) {
            int val = Integer.parseInt(id.substring(i, i + 1), 10) * CHECKSUM_WEIGHT[i];
            total += val;
        }

        return CHECKSUM[total % 11];
    }

    /**
     * Convert 15 digits id string to 18 digits id string
     */
    public static String convertOldCardID2New(String id) {
        String birthPlaceStr = id.substring(0, 6);
        String birthdayStr = id.substring(6, 12);
        String genderStr = id.substring(12);
        birthdayStr = CommonUtils.convertDateFormat("yyMMdd", "19yyMMdd", birthdayStr);

        String newID = birthPlaceStr + birthdayStr + genderStr;
        return newID + getCheckSum(newID);
    }

    /**
     * Check whether Date string has valid DateFormat or not
     */
    public static boolean isDateFormat(String dateString, String dateFormat) {

        if (TextUtils.isEmpty(dateString)) return false;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false);

        try {
            Date date = simpleDateFormat.parse(dateString);
            Log.d(TAG, "date = " + date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Check whether Date string has valid or not
     */
    public static boolean isValidDate(String dateString, String dateFormat) {
        if (TextUtils.isEmpty(dateString)) return false;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false);

        try {
            Date date = simpleDateFormat.parse(dateString);
            Log.d(TAG, "date = " + date);

            if (date.after(new Date())) return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
