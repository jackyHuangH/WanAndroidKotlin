package com.jacky.wanandroidkotlin.aidltest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.jacky.wanandroidkotlin.R;
import com.jacky.wanandroidkotlin.aidl.ICalcAIDL;
import com.jacky.wanandroidkotlin.base.BaseActivity;

/**
 * @author:Hzj
 * @date :2022/3/2
 * desc  ：
 * record：
 */
public class AidlTestActivity extends BaseActivity {
    private static final String TAG = "client";

    private ICalcAIDL mICalAidl;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mICalAidl = ICalcAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            mICalAidl = null;
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.acitvity_aidl_test;
    }

    @Override
    public void initWidget() {
        AppCompatButton bindService = findViewById(R.id.bind_service);
        AppCompatButton unBindService = findViewById(R.id.unbind_service);
        AppCompatButton add = findViewById(R.id.add);
        AppCompatButton min = findViewById(R.id.min);
        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bind service
                Intent intent = new Intent(AidlTestActivity.this,CalcService.class);
//                intent.setAction("com.jacky.wanandroidkotlin.aidl");
                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        });

        unBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(mServiceConnection);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mICalAidl != null) {
                        int addRes = mICalAidl.add(14, 12);
                        showMessage(addRes + "");
                    } else {
                        showMessage("服务器被异常杀死，请重新绑定服务端");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mICalAidl != null) {
                        int res = mICalAidl.min(90, 76);
                        showMessage(res + "");
                    } else {
                        showMessage("服务器被异常杀死，请重新绑定服务端");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
