/**
 * @author LuYongXing
 * @date 2014.09.26
 * @filename PreviewActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.Config;

public class PreviewVideoActivity extends PermanentActivity {

    private static final String TAG = PreviewVideoActivity.class.getSimpleName();

    public static final String VIDEO_PATH = "video_path";
    public static final String ENABLE_DELETE = "enable_delete";

    private String mVideoPath = null;
    private VideoView mVideoView;
    private MediaController mMediaController = null;

    private boolean mEnableDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(VIDEO_PATH)) {
            mVideoPath = intent.getStringExtra(VIDEO_PATH);
            mEnableDelete = intent.getBooleanExtra(ENABLE_DELETE, false);
        }

        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (mEnableDelete) {
            Intent intent = new Intent();
            intent.putExtra(VIDEO_PATH, mVideoPath);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected void initViews() {
        super.initViews();

        mLayoutInflater.inflate(R.layout.activity_preview_video, mLayoutContainer);

        mVideoView = (VideoView) findViewById(R.id.videoView);

        Button buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(PreviewVideoActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage("您真的删除此摄像吗？")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mVideoPath = null;
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
            }
        });

        if (!mEnableDelete) buttonDelete.setVisibility(View.INVISIBLE);

        refreshVideoView();
    }

    /**
     * Display room video in VideoView
     */
    private void refreshVideoView() {
        try {
            if (mVideoPath != null) {
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(mVideoPath);

                /*String widthString = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String heightString = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

                metaRetriever.release();
                Log.e(TAG, "video size = " + widthString + "x" + heightString);

                // Set width and height
                int width, height;

                try {
                    width = Integer.parseInt(widthString);
                } catch (NumberFormatException ex) {
                    width = 292;
                }
                try {
                    height = Integer.parseInt(heightString);
                } catch (NumberFormatException ex) {
                    height = 292;
                }


                height = 292 * width / height;
                width = 292;

                RelativeLayout layout = (RelativeLayout) mVideoView.getParent();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, height);
                params.gravity = Gravity.CENTER;
                params.weight = 1;
                layout.setLayoutParams(params);
                mVideoView.getHolder().setFixedSize(width, height);*/
                mVideoView.setVideoPath(mVideoPath);

                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.seekTo(1);
                    }
                });
                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.seekTo(1);
                    }
                });

                if (mMediaController == null) {
                    mMediaController = new MediaController(PreviewVideoActivity.this);
                    mMediaController.setAnchorView(mVideoView);
                    mVideoView.setMediaController(mMediaController);
                    mMediaController.show();
                }
                /*layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMediaController == null) {
                            v.setOnClickListener(null);

                            mMediaController = new MediaController(PreviewVideoActivity.this);
                            mMediaController.setAnchorView(mVideoView);
                            mVideoView.setMediaController(mMediaController);
                            mMediaController.show();
                        }
                    }
                });*/
            }
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

}
