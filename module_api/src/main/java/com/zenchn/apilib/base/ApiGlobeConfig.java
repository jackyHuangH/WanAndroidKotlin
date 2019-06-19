package com.zenchn.apilib.base;

/**
 * 描    述：
 * 修订记录：
 *
 * @author HZJ
 */

public interface ApiGlobeConfig {

    String CLIENT_ID = "00000000000000000000000000000001";
    String CLIENT_SECRET = "inspect_house_app";
    String GRANT_TYPE = "password";
    String REFRESH_TYPE = "refresh_token";
    String DEVICE_TYPE_ANDROID = "Android";
    String ACCESS_TOKEN = "access-token";
    String APP_DEVICE_ID = "app-device-id";
    String APP_DEVICE_NAME = "app-device-name";
    String APP_DEVICE_TYPE = "app-device-type";

    int MAX_RETRY_COUNT = 3;

    //用户可登录权限
    String USER_AUTHORITY_KEY_LOGIN = "*";

    interface ResponseCode {

        int CODE_200 = 200;//请求成功

        int CODE_400 = 400;//错误请求
        int CODE_401 = 401;//未授权
        int CODE_403 = 403;//请求禁止
        int CODE_404 = 404;//未找到页面
        int CODE_405 = 405;//方法禁用
        int CODE_408 = 408;//请求超时

        int CODE_500 = 500;//服务器内部错误
        int CODE_502 = 502;//错误网关
        int CODE_503 = 503;//服务不可用
    }
}
