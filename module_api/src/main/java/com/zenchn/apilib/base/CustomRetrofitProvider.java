package com.zenchn.apilib.base;

import android.app.Application;
import androidx.annotation.NonNull;
import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;
import com.zenchn.apilib.BuildConfig;
import com.zenchn.apilib.retrofit.IRetrofitProvider;
import com.zenchn.apilib.retrofit.intercepter.HeaderInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author:Hzj
 * @date :2018/3/22/022
 * desc  ：配置OKhttp参数
 * record：
 */

public final class CustomRetrofitProvider implements IRetrofitProvider {
    private static String BASE_URL;
    private static Application sApplication;

    private CustomRetrofitProvider(String baseUrl, Application application) {
        BASE_URL = baseUrl;
        sApplication = application;
    }

    static CustomRetrofitProvider getInstance(@NonNull String baseUrl, Application application) {
        return new CustomRetrofitProvider(baseUrl, application);
    }

    @Override
    @NonNull
    public Retrofit getDefaultRetrofit() {
        return CustomRetrofitProvider.SingletonInstance.INSTANCE;
    }

    private static class SingletonInstance {
        private static final Retrofit INSTANCE = create();

        private SingletonInstance() {
        }

        private static Retrofit create() {
            OkHttpClient.Builder builder = (new OkHttpClient.Builder())
                    .connectTimeout(120L, TimeUnit.SECONDS)
                    .readTimeout(120L, TimeUnit.SECONDS)
                    .writeTimeout(120L, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true);

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            if (BuildConfig.DEBUG) {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            }

            builder.addInterceptor(loggingInterceptor)
                    .addNetworkInterceptor(new HeaderInterceptor(sApplication));

            OkHttpClient okHttpClient = builder.build();
            return (new Retrofit.Builder())
                    .baseUrl(CustomRetrofitProvider.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(new Retrofit2ConverterFactory())
                    .client(okHttpClient)
                    .build();
        }
    }
}
