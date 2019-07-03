package com.jacky.wanandroidkotlin.model.api

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.jacky.wanandroidkotlin.model.local.ContextModel
import com.zenchn.support.utils.NetworkUtils
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import java.io.File

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：WanAndroid api 管理cookie
 * record：
 */
object WanRetrofitClient : BaseRetrofitClient() {

    private const val HEADER_PRAGMA = "Pragma"
    private const val HEADER_CACHE_CONTROL = "Cache-Control"

    val mService by lazy { getService(WanApiService::class.java, WanApiService.BASE_URL) }

    val mCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(ContextModel.getApplicationContext()))
    }

    override fun handleBuilder(builder: OkHttpClient.Builder) {
        //添加cookie
        val applicationContext = ContextModel.getApplicationContext()
        val httpCacheDir = File(applicationContext.cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024L //10Mb
        val cache = Cache(httpCacheDir, cacheSize)
        builder.cache(cache)
            .cookieJar(mCookieJar)
            .addInterceptor { chain ->
                var request = chain.request()
                if (!NetworkUtils.isNetworkAvailable(applicationContext)) {
                    //网络不可用时
                    request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
                }
                val response = chain.proceed(request)
                //根据网络是否可用重新设置cookie时长
                if (NetworkUtils.isNetworkAvailable(applicationContext)) {
                    val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                    response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .addHeader(HEADER_CACHE_CONTROL, "public, only-if-cached, max-stale=$maxStale")
                } else {
                    val maxAge = 60 * 60
                    response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .header(HEADER_CACHE_CONTROL, "public, max-age=$maxAge")
                        .build()
                }
                response
            }
    }
}