package com.example.baseproject.luban;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
    private ExifInterface srcExif;
    private String srcImg;
    private File tagImg;
    private int srcWidth;
    private int srcHeight;
    private int gear = 0;
    private int quality = 60;

    Engine(String srcImg, File tagImg) throws IOException {
        if (Checker.isJPG(srcImg)) {
            this.srcExif = new ExifInterface(srcImg);
        }
        this.tagImg = tagImg;
        this.srcImg = srcImg;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(srcImg, options);
        this.srcWidth = options.outWidth;
        this.srcHeight = options.outHeight;
    }

    public int getGear() {
        return gear;
    }

    public Engine setGear(int gear) {
        this.gear = gear;
        return this;
    }

    public int getQuality() {
        return quality;
    }

    public Engine setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    private int computeSize() {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);
        float scale = ((float) shortSide / longSide);
        Log.i("Luban","scale = "+scale+",longSide = "+longSide);
        if (scale <= 1 && scale >= 0.5625) {
            if (longSide < 1024) {
                return 1 + gear;
            } else if (longSide >= 1024 && longSide < 1920) {
                return 2 + gear;
            } else if(longSide >= 1920 && longSide < 4990){
                return 3 + gear;
            }else if (longSide > 4990 && longSide < 10240) {
                return 4 + gear;
            } else {
                return (longSide / 1280 == 0 ? 1 : longSide / 1280) + gear;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return (longSide / 1280 == 0 ? 1 : longSide / 1280)+1 + gear;
        } else {
            return ((int) Math.ceil(longSide / (1280.0 / scale)))+1 + gear;
        }
    }

    private Bitmap rotatingImage(Bitmap bitmap) {
        if (srcExif == null) return bitmap;

        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    File compress() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();

        Bitmap tagBitmap = BitmapFactory.decodeFile(srcImg, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        tagBitmap = rotatingImage(tagBitmap);
        tagBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        tagBitmap.recycle();

        FileOutputStream fos = new FileOutputStream(tagImg);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();

        return tagImg;
    }
}