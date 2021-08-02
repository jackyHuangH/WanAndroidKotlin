package com.jacky.wanandroidkotlin.ui.browser

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.wrapper.getView
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.zenchn.support.router.Router


/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：基于腾讯X5内核 webview 浏览器
 * record：
 */
class BrowserActivity : BaseActivity() {
    private lateinit var webView: WebView

    override fun getLayoutId(): Int = R.layout.activity_browser

    override fun initWidget() {
        window.setFormat(PixelFormat.TRANSLUCENT)//（这个对宿主没什么影响，建议声明）
        webView = getView<WebView>(R.id.web_view)
        getView<ImageButton>(R.id.ibt_back).setOnClickListener { onBackPressed() }
        getView<TextView>(R.id.tv_title).isSelected = true
        initWebView()
    }


    private fun initWebView() {
        val pbLoading = getView<ProgressBar>(R.id.pb_loading)
        val tvTitle = getView<TextView>(R.id.tv_title)
        intent?.getStringExtra(EXTRA_URL)?.let {
            webView.loadUrl(it)
        }

        webView.run {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(p0: WebView?, p1: String?) {
                    super.onPageFinished(p0, p1)
                    pbLoading.visibility = View.GONE
                }

                override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                    super.onPageStarted(p0, p1, p2)
                    pbLoading.visibility = View.VISIBLE
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(p0: WebView?, p1: String?) {
                    super.onReceivedTitle(p0, p1)
                    p1?.let { tvTitle.text = it }
                }

                override fun onProgressChanged(p0: WebView?, p1: Int) {
                    super.onProgressChanged(p0, p1)
                    pbLoading.progress = p1
                }
            }
        }

        Log.d("是否为x5", ":${webView.x5WebViewExtension}")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (webView != null) {
            //加载null内容
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            //清除历史记录
            webView.clearHistory()
            //销毁VebView
            webView.destroy()
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