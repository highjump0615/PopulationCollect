/**
 * @author LuYongXing
 * @date 2014.08.20
 * @filename CommonUtils.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.tencent.map.geolocation.TencentLocationManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    public static Resources mResources;

    /**
     * Enable/Disable view itself and sub views
     *
     * @param viewGroup root view what will enabled/disabled
     * @param enabled
     */
    public static void enableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view.isFocusable())
                view.setEnabled(enabled);

            if (view instanceof ListView) {
                if (view.isFocusable())
                    view.setEnabled(enabled);
                ListView listView = (ListView) view;
                int listChildCount = listView.getChildCount();
                for (int j = 0; j < listChildCount; j++) {
                    if (view.isFocusable())
                        listView.getChildAt(j).setEnabled(false);
                }
            } else if (view instanceof ViewGroup) {
                enableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    /**
     * Change ForeColor of specified symbol of TextView in all views
     *
     * @param context Context
     * @param symbol  TextView what TextColor would be changed
     * @param root
     */
    public static void changeForeColorInTextView(Context context, String symbol, View root) {
        if (mResources == null)
            mResources = context.getResources();

        if (!(root instanceof Button) && !(root instanceof CheckedTextView)
                && !(root instanceof Chronometer) && !(root instanceof DigitalClock)
                && !(root instanceof EditText) && root instanceof TextView) {
            TextView textView = (TextView) root;
            String label = textView.getText().toString();

            if (!TextUtils.isEmpty(label)
                    && label.indexOf(symbol) == 0) {
                textView.setText(getRedStarString(label));
            }
        }

        // If has children view, process them
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                changeForeColorInTextView(context, symbol, viewGroup.getChildAt(i));
        }
    }

    /**
     * Get the string that are represents in red color
     */
    public static SpannableString getRedStarString(String string) {
        SpannableString ss;

        if (TextUtils.isEmpty(string)) {
            ss = new SpannableString("");
        } else {
            ss = new SpannableString(string);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4444")), 0,
                    1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ss;
    }

    /**
     * Get path of images to be saved
     */
    public static File getMyApplicationDirectory(Context context, String directoryName) {
        String appName = context.getString(R.string.app_name);

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), appName);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + appName + " directory");
                return null;
            }
        }

        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + appName, directoryName);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + directoryName + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    /*
     * returning photoImage / video
     */
    public static File getExportFile(Context context, String exportType) {
        File mediaStorageDir = getMyApplicationDirectory(context, Config.EXPORT_DIRECTORY_NAME);

        if (mediaStorageDir == null) return null;

        return new File(mediaStorageDir.getPath() + File.separator + exportType/* + "_" + timeStamp + ".csv"*/);
    }

    /**
     * Create directory with path string
     */
    public static boolean createDirectory(String path) {
        File directory = new File(path);
        return directory.exists() || directory.mkdir();
    }

    /**
     * Delete directory in recursive
     */
    public static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            String[] children = directory.list();
            for (String child : children) {
                boolean success = deleteDirectory(new File(directory, child));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return directory.delete();
    }

    /*
     * returning photoImage / video
     */
    public static File getOutputMediaFile(Context context, boolean isImageType) {
        File mediaStorageDir = getMyApplicationDirectory(context, Config.IMAGE_DIRECTORY_NAME);

        if (mediaStorageDir == null) return null;

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;

        if (isImageType) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator /*+ "IMG_"*/ + timeStamp + ".jpg");
        } else {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator /*+ "VID_"*/ + timeStamp + ".3gp");
        }

        return mediaFile;
    }

    public static boolean copyFile(String fromPath, String toPath) {
        try {
            InputStream in = new FileInputStream(fromPath);
            new File(toPath).createNewFile();
            OutputStream out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method used by copyAssets() on purpose to copy a file.
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean saveFile(String filePath, byte[] data) {
        try {
            if (data == null) return false;

            new File(filePath).createNewFile();
            OutputStream out = new FileOutputStream(filePath);

            out.write(data);

            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checking device has camera hardware or not
     */
    public static boolean isDeviceSupportCamera(Context context) {
        // this device has a camera
        return context.getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    /**
     * Get bitmap from internal image file.
     */
    public static Bitmap getBitmapFromUri(Uri fileUri, int sampleSize) {
        // bitmap factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // downsizing photoImage as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = sampleSize;
        options.inMutable = true;

        return BitmapFactory.decodeFile(fileUri.getPath(), options);
    }

    /**
     * Get degree from ExifInterface orientation.
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * Adjust orientation of JPEG image file and return bitmap.
     */
    public static Bitmap adjustBitmap(Uri jpegUri) {
        try {
            ExifInterface exif = new ExifInterface(jpegUri.getPath());

            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);

            Matrix matrix = new Matrix();
            if (rotation != 0f) {
                matrix.preRotate(rotationInDegrees);
            }

            Bitmap sourceBitmap = getBitmapFromUri(jpegUri, 1);
            if (sourceBitmap == null) return null;

            int width = 600;
            int height = 800;

            if (sourceBitmap.getWidth() > sourceBitmap.getHeight()) {
                int tmp = height;
                height = width;
                width = tmp;
            }

            /*Bitmap adjustedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                    sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);*/
            Bitmap adjustedBitmap = Bitmap.createScaledBitmap(sourceBitmap, width, height, true);

            sourceBitmap.recycle();

            return adjustedBitmap;
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return null;
    }

    /**
     * Get byte data from bitmap variable.
     */
    public static byte[] getByteFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return new byte[0];

        /*
        //calculate how many bytes our photoImage consists of.
        int bytes = bitmap.getByteCount();
        //or we can calculate bytes this way. Use a different value than 4 if you don't use 32bit images.
        //int bytes = b.getWidth()*b.getHeight()*4;

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
        bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

        return buffer.array();
        */
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();
        try {
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }

    public static Bitmap getBitmapFromBytes(byte[] data) {
        if (data == null || data.length == 0) return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * Create error AlertDialog.
     */
    public static Dialog createErrorAlertDialog(final Context context, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null).create();
    }

    /**
     * Create error AlertDialog.
     */
    public static Dialog createErrorAlertDialog(final Context context, String title, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null).create();
    }

    public static String convertChinaFormatTime(String date) {
        if (TextUtils.isEmpty(date)) return "";
        String[] strContent = date.split("-");

        if (strContent.length == 3) {
            return String.format("%s年%s月%s日", strContent[0], strContent[1], strContent[2]);
        }

        return "";
    }

    public static Date getDateFromString(String dateString) {
        Date date = new Date();

        if (!TextUtils.isEmpty(dateString)) {
            try {
                date = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.CHINA).parse(
                        convertChinaFormatTime(dateString));
            } catch (ParseException e) {
                if (Config.DEBUG) e.printStackTrace();
            }
        }

        return date;
    }

    /**
     * @param date
     * @return
     */
    public static String getFormattedDateString(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String convertDateFormat(String orgFormat, String destFormat, String dateString) {
        if (TextUtils.isEmpty(dateString)) return "";

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(orgFormat);

            Date date = simpleDateFormat.parse(dateString);

            simpleDateFormat = new SimpleDateFormat(destFormat);
            return simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Make new OnTouchListener to show DatePickerDialog
     */
    public static View.OnTouchListener getOnTouchListenerForDatePicker(final Context context, final EditText editText) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CommonUtils.hideKeyboard(context, editText);
                    final String originalText = editText.getText().toString();

                    Calendar currentTime = Calendar.getInstance();
                    currentTime.setTime(getDateFromString(originalText));

                    int year = currentTime.get(Calendar.YEAR);
                    int month = currentTime.get(Calendar.MONTH);
                    int day = currentTime.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    if("清空".equals(editText.getText().toString())) {
                                        editText.setText("");
                                    } else {
                                        editText.setText(CommonUtils.getFormattedDateString(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime(), "yyyy-MM-dd"));
                                    }
                                }
                            }, year, month, day);
                    datePickerDialog.setTitle("请选择日期");
                    datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            editText.setText(originalText);
                        }
                    });
                    datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "清空", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                                editText.setText("");
                            else
                                editText.setText("清空");
                        }
                    });
                    datePickerDialog.show();
                    return true;
                }

                return false;
            }
        };
    }

    /**
     * @param spinner
     * @param itemString
     */
    public static int selectSpinnerItem(Spinner spinner, String itemString) {
        if (TextUtils.isEmpty(itemString)) return -1;

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        // First, get item index
        for (int i = 0; i < adapter.getCount(); i++) {
            if (itemString.equals(adapter.getItem(i))) {
                spinner.setSelection(i);
                return i;
            }
        }

        return -1;
    }

    /**
     * @param uri
     * @return
     */
    public static String getLastPathFromUri(Uri uri) {
        String path[] = uri.getPath().split("/");

        if (path.length > 1)
            return path[path.length - 1];
        else
            return "";
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Object getKeyFromValue(Map map, Object value) {
        for (Object o : map.keySet()) {
            if (map.get(o).equals(value)) {
                return o;
            }
        }

        return null;
    }

    public static String getMD5EncryptedString(String encTarget) {
        return getMD5EncryptedString(encTarget.getBytes(), encTarget.length());
    }

    public static String getMD5EncryptedString(byte[] data, int length) {
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
            return null;
        }

        // Encryption algorithm
        mdEnc.update(data, 0, length);
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }

    public static boolean compareFiles(String fileName1, String fileName2) {
        File file1 = new File(fileName1);
        File file2 = new File(fileName2);

        try {
            FileInputStream inputStream = new FileInputStream(file1);
            int size = inputStream.available();

            byte[] buf1 = new byte[size];
            int size1 = inputStream.read(buf1, 0, size);
            inputStream.close();

            inputStream = new FileInputStream(file2);
            size = inputStream.available();

            byte[] buf2 = new byte[size];
            int size2 = inputStream.read(buf2, 0, size);
            inputStream.close();

            String md5_1 = getMD5EncryptedString(buf1, size1);
            String md5_2 = getMD5EncryptedString(buf2, size2);

            return md5_1.equals(md5_2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Get version for current application
     *
     * @param context is current Activity
     * @param cls     is class
     * @return string version
     */
    public static String getVersionName(Context context, Class<?> cls) {
        try {
            ComponentName componentName = new ComponentName(context, cls);
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(componentName.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Hide always Soft Keyboard
     *
     * @param context is current Activity
     */
    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (editText != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            editText.clearFocus();
            //editText.setInputType(0);
        }
    }

    /**
     * Show always Soft Keyboard
     *
     * @param context is current Activity
     */
    public static void showKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (editText != null) {
            imm.showSoftInput(editText, 0);
        }
    }

    /**
     * Gets Date with UTC time zone
     *
     * @param date is concrete date
     * @return new instance calendar
     */
    public static Calendar getCalendarUTC(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar;
    }

    /**
     * 返回坐标系名称
     */
    public static String toString(int coordinateType) {
        if (coordinateType == TencentLocationManager.COORDINATE_TYPE_GCJ02) {
            return "国测局坐标(火星坐标)";
        } else if (coordinateType == TencentLocationManager.COORDINATE_TYPE_WGS84) {
            return "WGS84坐标(GPS坐标, 地球坐标)";
        } else {
            return "非法坐标";
        }
    }

    /**
     * 返回 manifest 中的 key
     */
    public static String getKey(Context context) {
        String key = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle metaData = appInfo.metaData;
            if (metaData != null) {
                key = metaData.getString("TencentMapSDK");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TencentLocation",
                    "Location Manager: no key found in manifest file");
            key = "";
        }
        return key;
    }

    public static boolean checkNetworkEnable(Context context) {
        try {
            ConnectivityManager nInfo = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            nInfo.getActiveNetworkInfo().isConnectedOrConnecting();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

}
