package com.jacky.wanandroidkotlin.aidltest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jacky.wanandroidkotlin.aidl.ICalcAIDL;

/**
 * @author:Hzj
 * @date :2022/3/2
 * desc  ：aidl测试service
 * record：
 */
public class CalcService extends Service {
    private static final String TAG=CalcService.class.getCanonicalName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private final ICalcAIDL.Stub mBinder =new ICalcAIDL.Stub() {
        @Override
        public int add(int x, int y) throws RemoteException {
            return x+y;
        }

        @Override
        public int min(int x, int y) throws RemoteException {
            return x-y;
        }
    };
}
