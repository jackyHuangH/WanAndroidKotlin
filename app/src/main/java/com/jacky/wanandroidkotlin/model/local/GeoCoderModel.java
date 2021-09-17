package com.jacky.wanandroidkotlin.model.local;

import android.util.Log;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zenchn.support.utils.LoggerKit;

/**
 * 描    述：
 * 修订记录：
 *
 * @author hzj
 */

public class GeoCoderModel {
    private static final String TAG = GeoCoderModel.class.getCanonicalName();

    private GeoCoderModel() {
    }

    private static final GeoCoderModel INSTANCE = new GeoCoderModel();

    public static GeoCoderModel getInstance() {
        return INSTANCE;
    }

    public interface BdMapGeoCoderCallback {

        void onGeoCoderSuccess(String address);
    }

    /**
     * 百度地图逆地理编码
     *
     * @param latLng
     * @return
     */
    public void getBdMapGeocoderAddress(com.baidu.mapapi.model.LatLng latLng, BdMapGeoCoderCallback callback) {
        GeoCoder mGeoCoder = GeoCoder.newInstance();
        // 反地理编码请求参数对象
        ReverseGeoCodeOption mReverseGeoCodeOption = new ReverseGeoCodeOption();
        // 设置请求参数
        mReverseGeoCodeOption.location(latLng)
                // 设置是否返回新数据 默认值0不返回，1返回
                .newVersion(1).radius(50);
        // 设置查询结果监听者
        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                LoggerKit.INSTANCE.d("onGetGeoCodeResult" + geoCodeResult.toString());
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    Log.d(TAG, "百度地图逆地理编码无结果");
                } else {
                    LoggerKit.INSTANCE.d("getBdMapGeocoderAddress" + reverseGeoCodeResult.toString());
                    callback.onGeoCoderSuccess(reverseGeoCodeResult.getAddress());
                }
            }
        });
        // 发起反地理编码请求(经纬度->地址信息)
        mGeoCoder.reverseGeoCode(mReverseGeoCodeOption);
    }
}
