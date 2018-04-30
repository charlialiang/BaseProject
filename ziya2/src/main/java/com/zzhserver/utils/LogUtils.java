package com.zzhserver.utils;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2017/1/12 0012.
 */

public class LogUtils {
    static String className;
    static String methodName;
    static int lineNumber;
    private static boolean DEBUG = true;
    private static StringBuilder stringBuffer = new StringBuilder();

    public static void x(int action) {
        x(String.valueOf(action));
    }

    public static void x(String message) {
        if (DEBUG) {
            getNames(new Throwable().getStackTrace());
            stringBuffer.setLength(0);
            stringBuffer.append(System.currentTimeMillis()).append("_").append(className).append("_").append(methodName)
                    .append("_").append(lineNumber).append("_").append(message);
            Log.w(className, stringBuffer.toString());
            try {
                if (true) {//开启日志写到文件 Android/<PackageName>/files/log/log.txt
                    File logDir = AppUtils.getAppContext().getExternalFilesDir("log");
                    File file = FileUtils.makeDirFile(FileUtils.makeDir(logDir.getAbsolutePath()) + "/log.txt");
                    FileWriter fos = new FileWriter(file, true);
                    fos.write(stringBuffer.append("\n").toString());
                    fos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            getNames(new Throwable().getStackTrace());
            stringBuffer.setLength(0);
            stringBuffer.append(methodName).append("_").append(lineNumber).append("_").append(message);
            Log.i(className, stringBuffer.toString());
        }
    }

    private static void getNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }
}
