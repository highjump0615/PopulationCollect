package com.synjones.otg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

/**
 * 这个Service不是必须的，可根据需要使用。 通过BroadcastReceiver不能监听到usb的插入消息，所以使用一个单独的线程来监控
 * 是否有希望的usb设备接入。而usb设备的拔出则通过广播来监听。
 *
 * @author zhaodianbo@Synjones
 * @ClassName UsbFinderService
 * @date 2014-7-24 下午3:40:26
 */
public class UsbMonitorService extends Service {

    public static final String TAG = "UsbConnectMonitor";

    private UsbFinderBinder mBinder = new UsbFinderBinder();
    private UsbFinder usbFinderHandler = null;
    private static Handler mHandler = null;
    private static int what;
    private static String usbDevName = null;
    private static final String ACTION_USB_PERMISSION = "com.prolific.pl2303hxdsimpletest.USB_PERMISSION";
    private static boolean isConnect = false;

    // private static Context mContext;
    public static boolean isConnect() {
        return isConnect;
    }

    public static void setDisconnectHandler(Handler disconnectHandler, int whatMsg) {
        mHandler = disconnectHandler;
        what = whatMsg;
    }

    public static void start(Context mContext) {
        // UsbMontiorService.mContext=mContext;
        Intent intent = new Intent(mContext, UsbMonitorService.class);
        mContext.startService(intent);
    }

    public static void stop(Context mContext) {
        Intent intent = new Intent(mContext, UsbMonitorService.class);
        mContext.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(
                "android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(ACTION_USB_PERMISSION);
        getApplicationContext()
                .registerReceiver(mUsbDisconnectReceiver, filter);
        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        beginFindUsb();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(mUsbDisconnectReceiver);
        stopFindUsb();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class UsbFinderBinder extends Binder {
        public UsbMonitorService getService() {

            return UsbMonitorService.this;
        }
    }

    public void beginFindUsb() {
        Log.d("TAG", "beginFindUsb() executed");
        if (usbFinderHandler != null && usbFinderHandler.finderRun)
            return;
        usbFinderHandler = new UsbFinder();
        usbFinderHandler.start();
    }

    public void stopFindUsb() {
        if (usbFinderHandler != null)
            usbFinderHandler.cancel();
        usbFinderHandler = null;
    }

    private static final BroadcastReceiver mUsbDisconnectReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");

            if ("android.hardware.usb.action.USB_DEVICE_DETACHED"
                    .equals(action)) {
                String deviceName = device.getDeviceName();
                if (usbDevName != null && usbDevName.equals(deviceName)) {
                    if (mHandler != null && Build.MODEL.contains("A5000"))// A3000 or A3500 doesn't work
                        mHandler.sendEmptyMessage(what);
                    isConnect = false;
                    UsbMonitorService.start(context);
                }
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                if (intent.getBooleanExtra("permission", false)) {
                    // permission get!
                    isConnect = true;
                }
            }
        }
    };

    class UsbFinder extends Thread {
        private ArrayList<String> Supported_VID_PID = new ArrayList<String>();
        public boolean finderRun = false;

        public void run() {
            Looper.prepare();
            finderRun = true;
            this.Supported_VID_PID.add("067B:2303");
            this.Supported_VID_PID.add("067B:2551");
            this.Supported_VID_PID.add("067B:AAA5");
            this.Supported_VID_PID.add("0557:2008");
            this.Supported_VID_PID.add("05AD:0FBA");

            while (finderRun && !interrupted()) {
                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values()
                        .iterator();
                while (deviceIterator.hasNext()) {
                    UsbDevice device = deviceIterator.next();
                    int usbVid = device.getVendorId();
                    int usbPid = device.getProductId();
                    boolean found = false;
                    for (String vidpid : Supported_VID_PID) {
                        if (String.format(
                                "%04X:%04X",
                                new Object[]{Integer.valueOf(usbVid),
                                        Integer.valueOf(usbPid)}).equals(
                                vidpid)) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        // Toast.makeText(getApplicationContext(),
                        // "Found Usb device: FT232R UART",
                        // Toast.LENGTH_LONG).show();
                        // Toast.makeText(getApplicationContext(),
                        // "Now send message USB_ACTION_ATTACH to activity ",
                        // Toast.LENGTH_LONG).show();

                        usbDevName = device.getDeviceName();
                        // mHandler.sendEmptyMessage(READER_CONNECTED);
                        try {
                            SdtDemoActivity.Reader.initSerialPort();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (manager.hasPermission(device)) {// 如果apk已经获取了usb权限
                            // 则不会再次发送权限广播
                            // 所以在这里更新
                            isConnect = true;
                        }

                        finderRun = false;
                        return;
                    } else {
                        // Toast.makeText(getApplicationContext(),
                        // "Found USB VID="+usbVid+" PID=" + usbPid,
                        // Toast.LENGTH_LONG).show();
                        // util.mPrintLog(getClass(),"Found USB VID="+usbVid+" PID="
                        // + usbPid);
                    }

                }// while(deviceIterator.hasNext()){

                try {
                    // util.mPrintLog(getClass(),"USB devices not found");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }// while(run)
            finderRun = false;
        }// run

        synchronized public void cancel() {
            if (!finderRun) return;
            finderRun = false;
            // util.mPrintLog(getClass(),"cancel UsbFinder");
            try {
                interrupt();
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }// UsbFinder class

}