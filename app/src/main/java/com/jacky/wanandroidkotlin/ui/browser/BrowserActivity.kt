package com.jacky.wanandroidkotlin.ui.browser

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.util.Log
import android.view.View
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.zenchn.support.router.Router
import kotlinx.android.synthetic.main.activity_browser.*


/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：基于腾讯X5内核 webview 浏览器
 * record：
 */
class BrowserActivity : BaseActivity() {

    override fun getLayoutId(): Int = com.jacky.wanandroidkotlin.R.layout.activity_browser

    override fun initWidget() {
        window.setFormat(PixelFormat.TRANSLUCENT)//（这个对宿主没什么影响，建议声明）
        ibt_back.setOnClickListener { onBackPressed() }
        tv_title.isSelected = true
        initWebView()
    }

    private fun initWebView() {
        intent?.getStringExtra(EXTRA_URL)?.let {
            web_view.loadUrl(it)
        }

        web_view.run {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(p0: WebView?, p1: String?) {
                    super.onPageFinished(p0, p1)
                    pb_loading.visibility = View.GONE
                }

                override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                    super.onPageStarted(p0, p1, p2)
                    pb_loading.visibility = View.VISIBLE
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(p0: WebView?, p1: String?) {
                    super.onReceivedTitle(p0, p1)
                    p1?.let { tv_title.text = it }
                }

                override fun onProgressChanged(p0: WebView?, p1: Int) {
                    super.onProgressChanged(p0, p1)
                    pb_loading.progress = p1
                }
            }
        }

        Log.d("是否为x5", ":${web_view.x5WebViewExtension}")
    }

    override fun onBackPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (web_view != null) {
            //加载null内容
            web_view.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            //清除历史记录
            web_view.clearHistory()
            //销毁VebView
            web_view.destroy()
        }
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"

        fun launch(from: Activity, url: String) {
            Router
                .newInstance()
                .from(from)
                .putString(EXTRA_URL, url)
                .to(BrowserActivity::class.java)
                .launch()
        }
    }
}