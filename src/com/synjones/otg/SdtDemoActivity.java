package com.synjones.otg;

/**
 * 联想A3000 使用USB读卡器读身份证
 */

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.synjones.padreader.IDCard;
import com.synjones.padreader.ReaderDriver;
import com.synjones.padreader.SynjonesOTGReaderLib;
import com.synjones.padreader.util;

public class SdtDemoActivity extends Activity {
    private Button buttonReadCard;
    private Button BtnReadOnce;
    private Button buttonExit;
    private Button btnSavePower;
    private Button btnWorkMode;

    private Bitmap bmp;
    private MediaPlayer mediaPlayer;

    private static final int SavePowerMode = 0;
    private static final int WorkMode = 1;
    private static int CurrentPowerMode = WorkMode;

    private long StartTime;
    private long ReadCount;
    private long SuccessCount;
    private long FailCount;
    private long ReadErrTime = 0;
    private long ContinuousErrTime = 0;
    private long eclipseTime;
    private TextView tvTime;
    private TextView tvCount;
    private TextView tvSuccessCount;
    private TextView tvFailCount;
    private TextView tvSoftVersion;

    private static final int ReadOnceDone = 0xa1;
    private static final int LoopRead = 0xa2;
    private static final int CONNECT_ERR = 0xa3;
    private static final int DISCONNECT = 0xa4;
    private static Handler mHandler = null;

    // 读卡器相关
    public static SynjonesOTGReaderLib Reader;
    private ReaderDriver readerDriver;
    private IDCard idcard = null;

    private boolean reading = false;
    private ReadThread ReadThreadHandler;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tvCount = (TextView) findViewById(R.id.tvCount);
        tvSuccessCount = (TextView) findViewById(R.id.tvSuccessCount);
        tvFailCount = (TextView) findViewById(R.id.tvFailCount);
        InitCounter();
        Reader = new SynjonesOTGReaderLib(this);
        Reader.initSerialPort();
        readerDriver = Reader.getReaderDriver();
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case ReadOnceDone:
                            idcard = (IDCard) msg.obj;
                            bmp = Reader.getPhotoBmp();
                            if (idcard != null) Beep();
                            showIDcardInfo();
                            BtnReadOnce.setEnabled(true);
                            break;

                        case LoopRead:
                            if (idcard == null && ReadErrTime < 3
                                    && ContinuousErrTime < 5) {
                                ReadErrTime++;
                                ContinuousErrTime++;
                            } else {
                                bmp = Reader.getPhotoBmp();
                                if (idcard != null)
                                    Beep();
                                showIDcardInfo();
                                ReadErrTime = 0;
                            }
                            IDCard.SW1 = 0;
                            IDCard.SW2 = 0;
                            IDCard.SW3 = 0;
                            break;

                        case CONNECT_ERR:
                            if (ReadThreadHandler != null)
                                ReadThreadHandler.StopReadThread();
                            buttonReadCard.setText("读卡");
                            break;

                        case DISCONNECT:
                            Toast.makeText(SdtDemoActivity.this, "读卡器断开连接", Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
                super.handleMessage(msg);
            }
        };

        buttonReadCard = (Button) findViewById(R.id.buttonReadCard);
        buttonReadCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (reading == false) {
                    Reader.initSerialPort();
                    InitCounter();
                    StartTime = System.currentTimeMillis();
                    reading = true;
                    buttonReadCard.setText("停止");
                    ReadThreadHandler = new ReadThread();
                    ReadThreadHandler.start();
                    // Reader.getIdcard(mHandler, ReadOnceDone);
                } else {
                    // timer.cancel();
                    if (ReadThreadHandler != null)
                        ReadThreadHandler.StopReadThread();
                    buttonReadCard.setText("读卡");
                }
            }
        });

        BtnReadOnce = (Button) findViewById(R.id.btnReadOnce);
        BtnReadOnce.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Reader.initSerialPort();
                StartTime = System.currentTimeMillis();
                Reader.getIdcard(mHandler, ReadOnceDone);
                BtnReadOnce.setEnabled(false);
            }
        });
        btnSavePower = (Button) findViewById(R.id.btnSavePower);
        btnSavePower.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (readerDriver.EnterSavePowerMode()) {
                    CurrentPowerMode = SavePowerMode;
                    Toast.makeText(getApplicationContext(), "进入节电模式成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "进入节电模式失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnWorkMode = (Button) findViewById(R.id.btnWorkMode);
        btnWorkMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // PowerModeChange();
                if (readerDriver.EnterWorkMode()) {
                    CurrentPowerMode = WorkMode;
                    Toast.makeText(getApplicationContext(), "进入工作模式成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "进入工作模式失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvTime = (TextView) findViewById(R.id.tvTime);
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ExitProgram();
                SdtDemoActivity.this.finish();
            }
        });

        tvSoftVersion = (TextView) findViewById(R.id.SoftWareVersion);
        tvSoftVersion.setText(util.getVersionName(this));

        UsbMonitorService.setDisconnectHandler(mHandler, DISCONNECT);
        UsbMonitorService.start(this);

    }// onCreat

    public void ExitProgram() {
        cancelReadThread();
        try {
            if (readerDriver != null)
                readerDriver.EnterSavePowerModeNoReply();
            readerDriver = null;
            if (Reader != null)
                Reader.CloseSerialPort();
            Reader = null;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    private String TAG = "PL2303HXD_APLog ";

    public void onResume() {
        Log.e(TAG, "Enter onResume");
        super.onResume();
        reading = false;
        InitCounter();
        buttonReadCard.setText("读卡");
        UsbMonitorService.start(this);
    }

    @Override
    protected void onDestroy() {
        ExitProgram();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "on pause");
        cancelReadThread();
        UsbMonitorService.stop(this);
    }

    public void cancelReadThread() {
        if (ReadThreadHandler != null)
            ReadThreadHandler.StopReadThread();
        ReadThreadHandler = null;
    }

    public void InitCounter() {
        ReadCount = 0;
        SuccessCount = 0;
        FailCount = 0;
        StartTime = 0;
    }

    public void showIDcardInfo() {
        TextView tv;
        ReadCount++;
        if (idcard != null) {
            SuccessCount++;
            ContinuousErrTime = 0;
            tv = (TextView) findViewById(R.id.textViewName);
            tv.setText("姓名:" + idcard.getName());
            tv = (TextView) findViewById(R.id.textViewSex);
            tv.setText("性别:" + idcard.getSex());
            tv = (TextView) findViewById(R.id.textViewNation);
            tv.setText("民族:" + idcard.getNation());
            tv = (TextView) findViewById(R.id.textViewBirthday);
            tv.setText("出生日期:"
                    + idcard.getBirthday().substring(0, 4) + "-"
                    + idcard.getBirthday().substring(4, 6) + "-"
                    + idcard.getBirthday().substring(6, 8));
            tv = (TextView) findViewById(R.id.textViewAddress);
            tv.setText("住址:" + idcard.getAddress());
            tv = (TextView) findViewById(R.id.textViewPIDNo);
            tv.setText("公民身份号码:" + idcard.getIDCardNo());
            tv = (TextView) findViewById(R.id.textViewGrantDept);
            tv.setText("发证机关:" + idcard.getGrantDept());
            tv = (TextView) findViewById(R.id.textViewUserLife);
            tv.setText("有效期限:"
                    + idcard.getUserLifeBegin() + "-" + idcard.getUserLifeEnd());
            tv = (TextView) findViewById(R.id.textViewStatus);
            tv.setText("状态:");
            ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);

            try {
                if (bmp != null) {
                    imageViewPhoto.setImageBitmap(bmp);
                } else {
                    Resources res = getResources();
                    bmp = BitmapFactory.decodeResource(res, R.drawable.avatar);
                    imageViewPhoto.setImageBitmap(bmp);
                    int result = idcard.decodeResult;
                    Log.e("otgReader", "decode Result=" + result);
                    Toast.makeText(SdtDemoActivity.this, "照片解码错误",
                            Toast.LENGTH_LONG).show();
                }
                // log.debug("decode wlt finish");
                System.gc();

            } catch (Exception ioe) {
                ioe.printStackTrace();
                // log.debug("photo display error:" + ioe.getMessage());
                // tvMessage.setText("状态：照片显示错" + ioe.getMessage());
            }
        } else {
            FailCount++;
            tv = (TextView) findViewById(R.id.textViewName);
            tv.setText(getString(R.string.sdtname));
            tv = (TextView) findViewById(R.id.textViewSex);
            tv.setText(getString(R.string.sdtsex));
            tv = (TextView) findViewById(R.id.textViewNation);
            tv.setText(getString(R.string.sdtnation));
            tv = (TextView) findViewById(R.id.textViewBirthday);
            tv.setText(getString(R.string.sdtbirthday));
            tv = (TextView) findViewById(R.id.textViewAddress);
            tv.setText(getString(R.string.sdtaddress));
            tv = (TextView) findViewById(R.id.textViewPIDNo);
            tv.setText(getString(R.string.sdtpidno));
            tv = (TextView) findViewById(R.id.textViewGrantDept);
            tv.setText(getString(R.string.sdtgrantdept));
            tv = (TextView) findViewById(R.id.textViewUserLife);
            tv.setText(getString(R.string.sdtuserlife));
            tv = (TextView) findViewById(R.id.textViewStatus);
            tv.setText(getString(R.string.sdtstatus) + " "
                    + Integer.toHexString(IDCard.SW1) + " "
                    + Integer.toHexString(IDCard.SW2) + " "
                    + Integer.toHexString(IDCard.SW3));
            ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
            imageViewPhoto.setImageResource(R.drawable.avatar);
        }

        IDCard.SW1 = 0;
        IDCard.SW2 = 0;
        IDCard.SW3 = 0;
        tvCount.setText("次数：" + ReadCount);
        tvFailCount.setText("失败：" + FailCount);
        tvSuccessCount.setText("成功：" + SuccessCount);
        eclipseTime = (System.currentTimeMillis() - StartTime) / 1000;
        tvTime.setText("时间：" + eclipseTime);
    }

    public void Beep() {
        try {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SEND_MSG(int toMsg, Object obj) {
        Message m = new Message();
        m.what = toMsg;
        m.obj = obj;
        mHandler.sendMessage(m);
    }

    class ReadThread extends Thread {
        private int ErrCount = 0;
        private boolean pauseThread = false;

        public void run() {
            Looper.prepare();
            // nop
            try {
                readerDriver.EnterWorkModeNoReply();
                Thread.sleep(800);
            } catch (Exception e) {
                e.printStackTrace();
                SEND_MSG(CONNECT_ERR, null);
            }
            while (reading) {
                try {
                    if (pauseThread) {
                        Thread.sleep(100);
                        continue;
                    }
                    idcard = Reader.getIDcardBlocking();
                    SEND_MSG(LoopRead, null);
                    if (idcard != null) {
                        Thread.sleep(800);
                        ErrCount = 0;
                    } else {
                        Thread.sleep(200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    HandleErr();
                }
            }
        }

        public void HandleErr() {
            if (ErrCount++ > 10) {
                ErrCount = 0;
                pauseThread = true;
                SEND_MSG(CONNECT_ERR, null);
            }
        }

        public void StopReadThread() {
            reading = false;
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}