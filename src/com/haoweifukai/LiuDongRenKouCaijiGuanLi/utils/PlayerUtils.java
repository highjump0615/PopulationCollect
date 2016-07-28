/**
 * @author LuYongXing
 * @date 2014.04.04
 * @filename PlayerUtils.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

public class PlayerUtils {

    private static final String TAG = PlayerUtils.class.getSimpleName();

    private static MediaPlayer mMediaPlayer;
    private static MediaPlayer mBackSoundPlayer;

    private static int mStartPosition;
    private static boolean mIsMute;

    // Music play from assets
    public static void playSound(Context context, String fileName) {
        Log.i(TAG, "Sound = " + fileName);

        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd(fileName);
            long start = descriptor.getStartOffset();
            long end = descriptor.getLength();

            if (mMediaPlayer == null)
                mMediaPlayer = new MediaPlayer();
            else
                mMediaPlayer.reset();

            mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), start, end);

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                    }
                    return false;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                    }
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                }
            });
            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {

                }
            });

            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

    public static void stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }

    public static void playBackSound(Context context, String fileName, MediaPlayer.OnCompletionListener completeListener) {
        Log.i(TAG, "Sound = " + fileName);

        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd(fileName);
            long start = descriptor.getStartOffset();
            long end = descriptor.getLength();

            if (mBackSoundPlayer == null)
                mBackSoundPlayer = new MediaPlayer();
            else
                mBackSoundPlayer.reset();

            mBackSoundPlayer.setDataSource(descriptor.getFileDescriptor(), start, end);

            mBackSoundPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    if (mediaPlayer != null)
                        mediaPlayer.release();
                    return false;
                }
            });
            mBackSoundPlayer.setOnCompletionListener(completeListener);
            mBackSoundPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                }
            });
            mBackSoundPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {

                }
            });

            if (mIsMute)
                mBackSoundPlayer.setVolume(0.0f, 0.0f);
            else
                mBackSoundPlayer.setVolume(1.0f, 1.0f);

            mStartPosition = 0;
            mBackSoundPlayer.setLooping(true);
            mBackSoundPlayer.prepare();
            mBackSoundPlayer.start();
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

    public static void pauseBackSound() {
        if (mBackSoundPlayer != null && mBackSoundPlayer.isPlaying()) {
            mStartPosition = mBackSoundPlayer.getCurrentPosition();
            mBackSoundPlayer.pause();
        }
    }

    public static void resumeBackSound() {
        if (mBackSoundPlayer != null && !mBackSoundPlayer.isPlaying()) {
            mBackSoundPlayer.seekTo(mStartPosition);
            mBackSoundPlayer.start();
        }
    }

    public static void stopBackSound() {
        if (mBackSoundPlayer != null) {
            mBackSoundPlayer.stop();
            mBackSoundPlayer.release();
            mBackSoundPlayer = null;
        }
    }

    public static void setMute(boolean mute) {
        mIsMute = mute;

        if (mBackSoundPlayer != null) {
            if (mute)
                mBackSoundPlayer.setVolume(0.0f, 0.0f);
            else
                mBackSoundPlayer.setVolume(1.0f, 1.0f);
        }
    }

}
