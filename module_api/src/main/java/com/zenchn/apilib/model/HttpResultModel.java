package com.zenchn.apilib.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 描    述：基本返回数据模型
 * 修订记录：
 *
 * @author HZJ
 */
public class HttpResultModel<T> {

    /***访问URL***/
    @JSONField(name = "path")
    public String path;

    /***statusCode为0时请求成功，其他情况另定***/
    @JSONField(name = "statusCode")
    public Integer statusCode;

    @JSONField(name = "message")
    public String message;

    @JSONField(name = "timestamp")
    public String timestamp;

    @JSONField(name = "success")
    public Boolean success;

    @JSONField(name = "data")
    public T data;


    @Override
    public String toString() {
        return "HttpResultModel{" +
                "path='" + path + '\'' +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data=" + data +
                '}';
    }
}
