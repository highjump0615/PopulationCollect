package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class TencentGPSTracker extends Service implements TencentLocationListener {

    private static final String TAG = TencentGPSTracker.class.getSimpleName();

    private Handler mHandler;
    private HandlerThread mThread;

    private TencentLocationManager mLocationManager;

    private Context mContext;

    public double mLatitude;
    public double mLongitude;

    public TencentGPSTracker(Context context) {
        mContext = context;

        mThread = new HandlerThread("Thread_gps_" + (int) (Math.random() * 10));
        mThread.start();
        mHandler = new Handler(mThread.getLooper());

        mLocationManager = TencentLocationManager.getInstance(mContext);
        // 设置坐标系为 gcj-02, 缺省坐标为 gcj-02, 所以通常不必进行如下调用
        mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84/*COORDINATE_TYPE_GCJ02*/);
    }

    public void stopTracker() {
        /**
         * 注意, 本示例中 requestLocationUpdates 和 removeUpdates 都可能被多次重复调用.
         * <p>
         * 重复调用 requestLocationUpdates, 将忽略之前的 reqest 并自动取消之前的 listener, 并使用最新的
         * request 和 listener 继续定位
         * <p>
         * 重复调用 removeUpdates, 将定位停止
         */

        // 退出 activity 前一定要停止定位!
        stopLocation();
        // 清空
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        // 停止线程
        if (mThread != null)
            mThread.getLooper().quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ====== view listener

    // 响应点击"停止"
    public void stopLocation() {
        if (mLocationManager != null)
            mLocationManager.removeUpdates(this);

        updateLocationStatus("停止定位");
    }

    // 响应点击"开始"
    public void startLocation() {

        // 创建定位请求
        final TencentLocationRequest request = TencentLocationRequest.create();

        // 修改定位请求参数, 定位周期 3000 ms
        request.setInterval(3000);

        // 在 mThread 线程发起定位
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mLocationManager.requestLocationUpdates(request, TencentGPSTracker.this);
            }
        });

        updateLocationStatus("开始定位: " + request);
    }

    // ====== location callback

    @Override
    public void onLocationChanged(TencentLocation location, int error,
                                  String reason) {
        String msg = null;
        if (error == TencentLocation.ERROR_OK) {

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            // 当前线程名字
            String threadName = Thread.currentThread().getName();

            // 定位成功
            StringBuilder sb = new StringBuilder();
            sb.append("(纬度=").append(location.getLatitude()).append(",经度=")
                    .append(location.getLongitude()).append(",精度=")
                    .append(location.getAccuracy()).append("), 来源=")
                    .append(location.getProvider()).append(", 地址=")
                    .append(location.getAddress());
            sb.append(", 当前线程=").append(threadName);
            msg = sb.toString();
        } else {
            // 定位失败
            msg = "定位失败: " + reason;
        }

        Log.i(TAG, "result = " + msg);
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        // ignore
    }

    // ====== location callback

    private void updateLocationStatus(String message) {
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.app_name);

        //Setting Dialog Message
        alertDialog.setMessage("请设置全球定位系统！");

        //On Pressing Setting button
        alertDialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

}
