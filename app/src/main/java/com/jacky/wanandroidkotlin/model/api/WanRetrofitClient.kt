package com.jacky.wanandroidkotlin.model.api

import android.annotation.SuppressLint
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.jacky.support.utils.LoggerKit
import com.jacky.support.utils.NetworkUtils
import com.jacky.wanandroidkotlin.model.local.ContextModel
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
    /**
     * 控制缓存开关的字段Pragma Cache-Control
     * Pragma有两个字段Pragma和Expires。Pragma的值为no-cache时，表示禁用缓存，Expires的值是一个GMT时间，表示该缓存的有效时间。
     *Pragma是旧产物，已经逐步抛弃，有些网站为了向下兼容还保留了这两个字段。如果一个报文中同时出现Pragma和Cache-Control时，以Pragma为准。
     *同时出现Cache-Control和Expires时，以Cache-Control为准。优先级从高到低是 Pragma -> Cache-Control -> Expires
     */
    private const val HEADER_PRAGMA = "Pragma"
    private const val HEADER_CACHE_CONTROL = "Cache-Control"

    val mService by lazy { getService(WanApiService::class.java, WanApiService.BASE_URL) }

    val mCookieJar by lazy {
        PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(ContextModel.getApplicationContext())
        )
    }

    @SuppressLint("MissingPermission")
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
                    //网络不可用时,只读取缓存
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
                        .addHeader(
                            HEADER_CACHE_CONTROL,
                            "public, only-if-cached, max-stale=$maxStale"
                        )
                } else {
                    //Cache-Control:max-age=N
                    //浏览器获取到资源内容后，将资源内容缓存在本地，缓存有效期是N秒。
                    //若过期前再次访问资源，直接使用本地缓存；过期后再访问，则向服务器发请求，若服务器检查资源没有更新，则返回304状态码；如果有更新，则返回200状态码以及新的资源内容。同时浏览器延长本地资源的缓存有效期。
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