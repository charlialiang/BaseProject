package com.zzhserver.manager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.zzhserver.utils.FileUtils;
import com.zzhserver.utils.LogUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by shnagri on 2016/3/31.
 */
public class MediaManager implements MediaPlayer.OnCompletionListener {
    public final static int SUCCESS = 0;
    public final static int E_STATE_RECODING = -1;
    public final static int E_UNKOWN = -2;

    private boolean isRecord = false;//是否正在录音
    private MediaRecorder mediaRecorder;//录音
    private MediaPlayer mediaPlayer = new MediaPlayer();//播放
    private String fileBasePath = FileUtils.getAppFile("record").getAbsolutePath();//录音输出文件
    public String recCachePath = fileBasePath + "/record_cache.amr";//录音缓存文件
    private long recStartTime;//开始录音时间
    private static MediaManager mInstance;

    private MediaManager() {
    }


    public static MediaManager getInstance() {
        if (null == mInstance) {
            synchronized (MediaManager.class) {
                if (null == mInstance) {
                    mInstance = new MediaManager();
                }
            }
        }
        return mInstance;
    }

    public long startRecord() {
        recStartTime = System.currentTimeMillis();
        if (isRecord) {
            return E_STATE_RECODING;
        } else {
            isRecord = true;
            createMediaRecord();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                LogUtils.i("starRecord");
                return recStartTime;
            } catch (IOException ex) {
                ex.printStackTrace();
                return E_UNKOWN;
            } catch (IllegalStateException ie) {
                ie.printStackTrace();
                return E_UNKOWN;
            } catch (Exception e) {
                e.printStackTrace();
                return E_UNKOWN;
            }
        }
    }

    public long stopRecord() {
        if (mediaRecorder != null) {
            try {
                LogUtils.i("stopRecord");
                isRecord = false;
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                long recTime = System.currentTimeMillis() - recStartTime;
                return recTime;
            } catch (IllegalStateException ie) {
                ie.printStackTrace();
                return 0;
            } catch (Exception e) {
                //clreaAllRecFile();
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    private void createMediaRecord() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setAudioEncodingBitRate(8);
            mediaRecorder.setAudioChannels(1);
            mediaRecorder.setAudioSamplingRate(16000);//采用频率
            mediaRecorder.setOutputFile(recCachePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除录音缓存文件
     */
    public void clearRecCacheFile() {
        FileUtils.deleteFile(new File(recCachePath));
    }

    /**
     * 删除录音文件
     */
    public void clearAllRecFile() {
        FileUtils.deleteAllFiles(new File(fileBasePath));
    }

    public void startPlay(String path) {
        try {
            //设置要播放的文件
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            //播放
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean stopPlay() {
        try {
            mediaPlayer.reset();
            mediaPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtils.i("播放完成!");
        stopPlay();
    }
}