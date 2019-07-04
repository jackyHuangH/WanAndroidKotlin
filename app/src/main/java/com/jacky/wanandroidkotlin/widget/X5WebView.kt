package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.util.AttributeSet
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：X5内核WebView包装
 * record：
 */
class X5WebView(context: Context, attr: AttributeSet) : WebView(context, attr) {

    init {
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                //避免调用系统浏览器
                url?.let { view?.loadUrl(it) }
                return true
            }
        }
        initWebViewSettings()
        view.isClickable = true
    }

    private fun initWebViewSettings() {
        val webSetting = settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        webSetting.loadWithOverviewMode = true
        webSetting.setAppCacheEnabled(true)
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
    }
}