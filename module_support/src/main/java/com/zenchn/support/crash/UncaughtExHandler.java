package com.zenchn.support.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 描    述：异常信息捕获者,持久化异常信息到本地
 * 修订记录：
 *
 * @author hzj
 */
public final class UncaughtExHandler implements UncaughtExceptionHandler {

    private static final String DEFAULT_TAG = "Logger";
    /**
     * crash 是否收集报错日志
     */
    private final boolean mIsReportCrash = true;
    private static final String FILE_NAME_PREFIX = "crash";
    private static final String FILE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String FILE_NAME_SUFFIX = ".txt";

    private ICrashCallback mCrashCallback;
    private Context mContext;
    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;

    private UncaughtExHandler() {
    }

    private static class SingletonInstance {
        private static final UncaughtExHandler INSTANCE = new UncaughtExHandler();
    }

    public static UncaughtExHandler getInstance() {
        return SingletonInstance.INSTANCE;
    }

    /**
     * 初始化方法必须放在application中才有效
     *
     * @param context
     * @param crashCallback
     */
    public void init(@NonNull Context context, @NonNull ICrashCallback crashCallback) {
        this.mCrashCallback = crashCallback;
        this.mContext = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public UncaughtExHandler setCrashCallback(@NonNull ICrashCallback mCrashCallback) {
        this.mCrashCallback = mCrashCallback;
        return this;
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(thread, ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.d(DEFAULT_TAG, "error:", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告
     *
     * @param ex true:处理了该异常信息返回true,否则返回false;
     * @return
     */
    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {
            return false;
        }
        //打印出当前调用栈信息
        ex.printStackTrace();
        if (mCrashCallback != null) {
            mCrashCallback.onCrash(thread, ex);
        }
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                //如果重写了异常处理，否则就由我们自己结束自己
                if (mIsReportCrash) {
                    try {
                        //导出异常信息到SD卡中
                        File logFile = dumpExceptionToSDCard(ex);
                        //notice:这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
                        Log.d(DEFAULT_TAG, "uncaughtException: " + logFile.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return true;
    }

    /**
     * 持久化异常信息
     *
     * @param ex
     * @return
     * @throws IOException
     */
    private File dumpExceptionToSDCard(Throwable ex) throws IOException {

        File logFile = null;

        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String logFilePath = mContext.getExternalFilesDir("/CrashLog/").getAbsolutePath();
            File dir = new File(logFilePath);
            if (!dir.exists() || dir.isFile()) {
                dir.mkdirs();
            }

            //获取奔溃时间（格式化）
            String crashTime = getCrashTime();

            //以当前时间创建log文件
            logFile = new File(dir, FILE_NAME_PREFIX + crashTime + FILE_NAME_SUFFIX);

            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));

                //导出发生异常的时间
                pw.println(crashTime);

                //导出手机信息
                dumpPhoneInfo(pw);

                pw.println();

                //导出异常的调用栈信息
                ex.printStackTrace(pw);

                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return logFile;
    }

    /**
     * 获取奔溃时间
     *
     * @return
     */
    private String getCrashTime() {
        String crashTime = "";
        long current = System.currentTimeMillis();
        try {
            crashTime = new SimpleDateFormat(FILE_DATE_FORMAT, Locale.CHINA).format(new Date(current));
        } catch (Exception e) {
            e.printStackTrace();
            crashTime = new SimpleDateFormat(FILE_DATE_FORMAT, Locale.CHINA).format(new Date(current));
        }
        return crashTime;
    }

    /**
     * 持久化异常信息
     *
     * @param printWriter
     * @throws NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter printWriter) throws NameNotFoundException {

        printWriter.append("================Build================\n");
        printWriter.append(String.format("BOARD\t%s\n", Build.BOARD));
        printWriter.append(String.format("BOOTLOADER\t%s\n", Build.BOOTLOADER));
        printWriter.append(String.format("BRAND\t%s\n", Build.BRAND));
        printWriter.append(String.format("CPU_ABI\t%s\n", Build.CPU_ABI));
        printWriter.append(String.format("CPU_ABI2\t%s\n", Build.CPU_ABI2));
        printWriter.append(String.format("DEVICE\t%s\n", Build.DEVICE));
        printWriter.append(String.format("DISPLAY\t%s\n", Build.DISPLAY));
        printWriter.append(String.format("FINGERPRINT\t%s\n", Build.FINGERPRINT));
        printWriter.append(String.format("HARDWARE\t%s\n", Build.HARDWARE));
        printWriter.append(String.format("HOST\t%s\n", Build.HOST));
        printWriter.append(String.format("ID\t%s\n", Build.ID));
        printWriter.append(String.format("MANUFACTURER\t%s\n", Build.MANUFACTURER));
        printWriter.append(String.format("MODEL\t%s\n", Build.MODEL));
        printWriter.append(String.format("SERIAL\t%s\n", Build.SERIAL));
        printWriter.append(String.format("PRODUCT\t%s\n", Build.PRODUCT));
        printWriter.append("================APP================\n");

        //应用的版本名称和版本号
        if (mContext != null) {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            printWriter.append(String.format("versionCode\t%s\n", versionCode));
            printWriter.append(String.format("versionName\t%s\n", versionName));
        }
        printWriter.append("================Exception================\n");
    }


    public interface ICrashCallback {
        /**
         * crash回调
         */
        void onCrash(Thread thread, Throwable throwable);
    }
}
