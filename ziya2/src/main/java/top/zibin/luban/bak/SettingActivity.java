/*
package com.sora.exmain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sora.R;
import com.sora.global.BaseActivity;
import com.sora.global.Const;
import com.sora.pojo.bean.FileBean;
import com.sora.utils.DownUploadUtil;
import com.sora.utils.EncryptUtils;
import com.sora.utils.ImageUtils;
import com.sora.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import top.zibin.luban.OnCompressListener;

*/
/**
 * Created by Administrator on 2017/12/28 0028.
 *//*


public class SettingActivity extends BaseActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pic://发送图片
                //选择图片
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(this, PhotoPicker.REQUEST_CODE);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        LogUtils.i("resultCode = " + resultCode + ",requestCode = " + requestCode);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            LogUtils.i("选择图片");
            //选取图片
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String path = photos.get(0);
                LogUtils.i("path = " + path + ",file.length=" + new File(path).length());
                ImageUtils.showResult(new File(path));
                startActivityForResult(ImageUtils.cropImage(path), 1011);
            }
        } else if (resultCode == RESULT_OK && requestCode == 1011) {
            //
            LogUtils.i("OK CACHE_CROP_PATH=" + ImageUtils.CACHE_CROP_PATH);
            upFile();
        }
    }

    private void upFile() {//压缩图片
        ImageUtils.compress(ImageUtils.CACHE_CROP_PATH, new OnCompressListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(File file) {
                String pathName = EncryptUtils.MD5("" + System.currentTimeMillis());
                DownUploadUtil.getInstance().upload(Const.UP_FILE_URL, file.getAbsolutePath(), new DownUploadUtil.OnUploadListener() {
                    @Override
                    public void onUploadSuccess(String result) {
                        try {
                            LogUtils.i("onUploadSuccess:" + result);
                            FileBean fileBean = new Gson().fromJson(result, FileBean.class);
                            LogUtils.i("onUploadSuccess:fileBean=" + fileBean.toString());
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onUploading(int progress) {
                        LogUtils.i("progress:" + progress);
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onUploadFailed() {
                        LogUtils.i("onUploadFailed!!!!");
                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }
}
*/
