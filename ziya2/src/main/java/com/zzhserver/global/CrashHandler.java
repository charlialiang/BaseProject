package com.zzhserver.global;

import com.zzhserver.utils.LogUtils;
import com.zzhserver.utils.ToastUtils;

/**
 * Created by Administrator on 2018/2/23.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    public void init() {
        LogUtils.i("5");
        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ToastUtils.show("出事了,请检查代码!!!");
        App.getInstance().threadShutdown();
        LogUtils.i("搞事情!!");
        ex.printStackTrace();
        // 如果用户没有处理则让系统默认的异常处理器来处理
        mDefaultHandler.uncaughtException(thread, ex);
        //View view = LayoutInflater.from(AppUtils.getAppContext()).inflate(R.layout.dialog_group_name, null);
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(AppUtils.getAppContext(), LoadingActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(AppUtils.getAppContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager) AppUtils.getAppContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        android.os.Process.killProcess(android.os.Process.myPid());*/
    }
}
