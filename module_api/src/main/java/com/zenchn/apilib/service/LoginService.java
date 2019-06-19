package com.zenchn.apilib.service;


import com.zenchn.apilib.entity.TokenEntity;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 作    者：hzj on 2018/9/17 15:15
 * 描    述：
 * 修订记录：
 */

public interface LoginService {

    /**
     * 作    者：hzj on 2018/9/17 15:15
     * 描    述：授权接口
     */
    @FormUrlEncoded
    @POST("oauth/token")
    Observable<TokenEntity> login(@Field("client_id") String clientId,
                                  @Field("client_secret") String clientSecret,
                                  @Field("grant_type") String grantType,
                                  @Field("username") String username,
                                  @Field("password") String password);

    /**
     * 作    者：hzj on 2018/9/17 15:15
     * 描    述：刷新令牌接口
     */
    @FormUrlEncoded
    @POST("oauth/token")
    Observable<TokenEntity> refreshToken(@Field("client_id") String clientId,
                                         @Field("client_secret") String clientSecret,
                                         @Field("grant_type") String grantType,
                                         @Field("refresh_token") String refreshToken);

}
