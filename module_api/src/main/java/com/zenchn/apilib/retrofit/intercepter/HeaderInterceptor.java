package com.zenchn.apilib.retrofit.intercepter;

import android.app.Application;

import com.zenchn.apilib.base.ApiGlobeConfig;
import com.zenchn.apilib.entity.LoginInfoEntity;
import com.zenchn.apilib.util.ClientWrapper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author:Hzj
 * @date :2018/9/12/012
 * desc  ：OKhttp header 拦截器，在请求头中放入设备信息
 * 每一个拦截器中，一个关键部分就是使用chain.proceed(request)发起请求。
 * 这个简单的方法就是所有Http工作发生的地方，生成和请求对应的响应
 * record：
 */
public class HeaderInterceptor implements Interceptor {

    private Application mApplication;

    public HeaderInterceptor(Application application) {
        mApplication = application;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //头部添加设备信息：deviceId,deviceName,deviceType
        LoginInfoEntity loginInfo = ClientWrapper.getClientInfo(mApplication);
        Request originRequest = chain.request();
        //get new request,add header
        Request newRequest = originRequest.newBuilder()
                .addHeader(ApiGlobeConfig.APP_DEVICE_ID, loginInfo.getDeviceId())
                .addHeader(ApiGlobeConfig.APP_DEVICE_NAME, loginInfo.getDeviceName())
                .addHeader(ApiGlobeConfig.APP_DEVICE_TYPE, ApiGlobeConfig.DEVICE_TYPE_ANDROID)
                .build();
        return chain.proceed(newRequest);
    }
}
