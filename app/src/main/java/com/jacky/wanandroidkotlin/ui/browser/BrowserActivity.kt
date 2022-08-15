package com.jacky.wanandroidkotlin.ui.browser

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyf.immersionbar.ImmersionBar
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.databinding.ActivityBrowserBinding
import com.jacky.wanandroidkotlin.util.ClipboardUtils
import com.jacky.wanandroidkotlin.util.openBrowser
import com.jacky.wanandroidkotlin.util.shareUrl
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
class BrowserActivity : BaseActivity<ActivityBrowserBinding>() {
    private lateinit var webView: WebView
    private val mWebUrl by lazy { intent.getStringExtra(EXTRA_URL) }
    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun getLayoutId(): Int = R.layout.activity_browser

    override fun initStatusBar() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.colorPrimary)
            .statusBarDarkFont(false)
            .navigationBarColor(R.color.backgroundColor).init()
    }

    override fun initWidget() {
        window.setFormat(PixelFormat.TRANSLUCENT)//（这个对宿主没什么影响，建议声明）
        webView = mViewBinding.webView
        mViewBinding.ibtBack.setOnClickListener { onBackPressed() }
        mViewBinding.tvTitle.isSelected = true
        mViewBinding.ibtMore.setOnClickListener {
            showBottomDialog()
        }
        initWebView()
    }

    private fun showBottomDialog() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = BottomSheetDialog(this).apply {
                val behavior: BottomSheetBehavior<*> = this.behavior
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                val dialogView: View =
                    View.inflate(this@BrowserActivity, R.layout.dialog_share_bottom_sheet, null)
                dialogView.findViewById<TextView>(R.id.tv_copy_clipboard).setOnClickListener {
                    //复制链接
                    ClipboardUtils.copyToClipboard(this@BrowserActivity, mWebUrl)
                    showMessage("链接已复制到剪贴板")
                    dismiss()
                }
                dialogView.findViewById<TextView>(R.id.tv_open_in_browser).setOnClickListener {
                    //使用自带浏览器打开
                    openBrowser(mWebUrl)
                    dismiss()
                }
                dialogView.findViewById<TextView>(R.id.tv_share).setOnClickListener {
                    //三方分享
                    shareUrl(mWebUrl, mViewBinding.tvTitle.text.toString())
                    dismiss()
                }
                setContentView(dialogView)
                //给布局设置透明背景色
                ((dialogView.parent) as View).setBackgroundColor(Color.TRANSPARENT)
            }
        }
        bottomSheetDialog?.show()
    }

    private fun initWebView() {
        mWebUrl?.let {
            webView.loadUrl(it)
        }

        webView.run {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(p0: WebView?, p1: String?) {
                    super.onPageFinished(p0, p1)
                    mViewBinding.pbLoading.visibility = View.GONE
                }

                override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                    super.onPageStarted(p0, p1, p2)
                    mViewBinding.pbLoading.visibility = View.VISIBLE
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(p0: WebView?, p1: String?) {
                    super.onReceivedTitle(p0, p1)
                    p1?.let { mViewBinding.tvTitle.text = it }
                }

                override fun onProgressChanged(p0: WebView?, p1: Int) {
                    super.onProgressChanged(p0, p1)
                    mViewBinding.pbLoading.progress = p1
                }
            }
            //获取当前是否是暗黑模式
            val isDarkTheme: Boolean =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            // enable:true(日间模式)，enable：false（夜间模式）
            settingsExtension.setDayOrNight(!isDarkTheme)
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