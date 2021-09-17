package com.jacky.wanandroidkotlin.model.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zenchn.support.utils.LoggerKit;

import java.lang.ref.WeakReference;

/**
 * 描    述：百度地图定位
 * 修订记录：
 *
 * @author hzj
 */

public class LocationModel {
    private static final String TAG = "高德定位===";
    long mStartTime;

    private LocationModel() {
    }

    private static class SingletonInstance {
        private static final LocationModel INSTANCE = new LocationModel();
    }

    public static LocationModel getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public interface BaiduLocationCallback {

        void onLocationSuccess(@NonNull BDLocation bdLocation);

        void onLocationFailure();
    }

    /**
     * 获取百度地图定位坐标
     * 百度定位SDK默认输出GCJ02坐标系
     *
     * @param callback
     */
    public LocationClient getBaiduLocation(final boolean autoStop, @Nullable BaiduLocationCallback callback) {
        final WeakReference<BaiduLocationCallback> callbackWeakReference = new WeakReference<>(callback);

        Context applicationContext = ContextModel.INSTANCE.getApplicationContext();
        //定位初始化
        LocationClient bdLocationClient = new LocationClient(applicationContext);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setScanSpan(1000);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //设置坐标类型，默认GCJ02
        option.setCoorType("gcj02");
        //可选，设置是否需要地址描述
        option.setIsNeedLocationDescribe(true);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，
        // 该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        option.setOpenAutoNotifyMode();
        //设置locationClientOption
        bdLocationClient.setLocOption(option);
        //注册LocationListener监听器
        bdLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation == null) {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    if (callbackWeakReference.get() != null) {
                        callbackWeakReference.get().onLocationFailure();
                    }
                } else {
                    //定位成功回调信息,回调在主线程，设置相关消息
                    if (callbackWeakReference.get() != null) {
                        LoggerKit.INSTANCE.d("baidu 定位：" + bdLocation.toString());
                        LoggerKit.INSTANCE.d("baidu 定位locType：" + bdLocation.getLocType() + "--" + bdLocation.getLocTypeDescription());
                        callbackWeakReference.get().onLocationSuccess(bdLocation);
                    }

                    if (autoStop) {
                        //成功后停止定位
                        bdLocationClient.stop();
                    }
                }
            }
        });
        //开启地图定位图层
        bdLocationClient.start();
        return bdLocationClient;
    }
}
