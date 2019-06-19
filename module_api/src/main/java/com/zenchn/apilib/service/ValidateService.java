package com.zenchn.apilib.service;


import com.zenchn.apilib.base.ApiGlobeConfig;
import com.zenchn.apilib.model.HttpResultModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

/**
 * 描    述：
 * 修订记录：
 *
 * @author HZJ
 */

public interface ValidateService {

    /**
     * 描    述：修改密码,使用(sha256)加密
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PUT("account/revPwd")
    Observable<HttpResultModel<Object>> revPwd(@Header(ApiGlobeConfig.ACCESS_TOKEN) String accessToken,
                                               @Body RequestBody body);
}
