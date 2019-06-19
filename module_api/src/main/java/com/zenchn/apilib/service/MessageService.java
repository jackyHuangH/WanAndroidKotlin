package com.zenchn.apilib.service;

import com.zenchn.apilib.base.ApiGlobeConfig;
import com.zenchn.apilib.entity.MsgEntity;
import com.zenchn.apilib.model.HttpResultModel;
import com.zenchn.apilib.model.ListDataModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * @author:Hzj
 * @date :2018/11/30/030
 * desc  ：消息 相关接口
 * record：
 */
public interface MessageService {

    /**
     * 分页获取消息列表
     *
     * @param accessToken
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET("messageNotice/list")
    Observable<HttpResultModel<ListDataModel<MsgEntity>>> getMessageList(@Header(ApiGlobeConfig.ACCESS_TOKEN) String accessToken,
                                                                         @Query("page") Integer pageNum,
                                                                         @Query("pageSize") Integer pageSize);

    /**
     * 消息通知已读上报接口
     *
     * @param accessToken
     * @param messageId
     * @return
     */
    @PUT("messageNotice/read")
    Observable<HttpResultModel<Object>> markMessageRead(@Header(ApiGlobeConfig.ACCESS_TOKEN) String accessToken,
                                                        @Query("messageId") String messageId);
}
