package com.zenchn.apilib.service;


import com.zenchn.apilib.base.ApiGlobeConfig;
import com.zenchn.apilib.entity.PortraitEntity;
import com.zenchn.apilib.entity.UserInfoEntity;
import com.zenchn.apilib.model.HttpResultModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

/**
 * 作    者：hzj on 2018/9/18 17:21
 * 描    述：
 * 修订记录：
 */
public interface UserService {

    /**
     * 描    述：获取用户信息
     */
    @GET("my/staffInfo")
    Observable<HttpResultModel<UserInfoEntity>> getUserInfo(@Header(ApiGlobeConfig.ACCESS_TOKEN) String accessToken);


    /**
     * 更新头像
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PUT("account/upload/portrait")
    Observable<HttpResultModel<PortraitEntity>> updatePortrait(@Header(ApiGlobeConfig.ACCESS_TOKEN) String accessToken,
                                                               @Body RequestBody body);

    /**
     * 修改个人信息
     *
     * @param accessToken
     * @param body
     * @return
     */
    @PUT("account/revInfo")
    Observable<HttpResultModel<Object>> updateUserInfo(@Header(ApiGlobeConfig.ACCESS_TOKEN) String accessToken,
                                                       @Body RequestBody body);
}
