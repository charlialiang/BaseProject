package com.example.baseproject.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.example.baseproject.luban.Luban;
import com.example.baseproject.luban.OnCompressListener;
import com.example.baseproject.BuildConfig;

import java.io.File;


/**
 * Created by Administrator on 2017/12/27 0027.
 */

public class ImageUtils {

    public static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/Luban/";
    public static final String CACHE_PATH = IMAGE_PATH + "/cache_crop.jpg";

    /**
     * 压缩图片 Listener 方式
     */
    public static void compress(String photoPath, OnCompressListener listener) {
        Luban.with(AppUtils.getAppContext())
                .load(photoPath)
                .ignoreBy(80)
                .setTargetDir(FileUtils.makeDir(IMAGE_PATH))
                .setCompressListener(listener)
                .putGear(0)
                .launch();
    }

    public static void showResult(File file) {
        int[] thumbSize = computeSize(file.getAbsolutePath());
        LogUtils.i("参数= " + thumbSize[0] + "X" + thumbSize[1] + " size:" + file.length());
    }

    private static int[] computeSize(String srcImg) {
        int[] size = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(srcImg, options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;

        return size;
    }


    public static Uri getCacheUri(String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(AppUtils.getAppContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(path));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    //调用系统的图片裁剪,输出200X200
    public static Intent cropImage(String path) {
       return cropImage(path, 1, 1, 216, 216);
    }

    /**
     * 调用系统的图片裁剪
     *
     * @param path 图片的路径
     */
    public static Intent cropImage(String path, int aspectX, int aspectY, int outputX, int outputY) {
        Intent intent = null;
        try {
            intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(getCacheUri(path), "image/*");
            // 是否裁剪
            intent.putExtra("crop", "true");
            // 设置xy的裁剪比例1:1
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            // 设置输出的200X200像素
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
            //是否缩放
            intent.putExtra("scale", false);
            //输入预裁剪图片的Uri，指定以后，可以通过这个Uri获得图片
            File file = FileUtils.makeFile(CACHE_PATH);
            Uri imageCropedCacheUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCropedCacheUri);
            //是否返回图片数据可以不用，直接用Uri就可以
            intent.putExtra("return-data", false);
            // 设置输出图片格式
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            // 是否关闭面部识别
            intent.putExtra("noFaceDetection", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }
}