package com.zenchn.apilib.service;


import com.zenchn.apilib.base.ApiGlobeConfig;
import com.zenchn.apilib.model.HttpResultModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * 描    述：
 * 修订记录：
 *
 * @author HZJ
 */

public interface LogoutService {

    /**
     * 描    述：注销令牌
     */
    @GET("oauth/logout")
    Observable<HttpResultModel<Object>> logout(@Header(ApiGlobeConfig.ACCESS_TOKEN) String accessToken);

}
