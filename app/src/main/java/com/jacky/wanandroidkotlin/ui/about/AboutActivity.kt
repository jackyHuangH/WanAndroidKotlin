package com.jacky.wanandroidkotlin.ui.about

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.common.GITHUB_HOME
import com.jacky.wanandroidkotlin.common.GITHUB_URL
import com.jacky.wanandroidkotlin.common.ISSUE_URL
import com.jacky.wanandroidkotlin.util.openBrowser
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.router.Router
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.model.Notice
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：
 * record：
 */
class AboutActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_about

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.title = getString(R.string.about_title)
        val versionName = AndroidKit.Package.getVersionName(this)
        tv_version.text = "V$versionName"

        tv_license.setOnClickListener {
            val license = ApacheSoftwareLicense20()
            val notice = Notice(getString(R.string.app_name), GITHUB_URL, "", license)
            LicensesDialog.Builder(this)
                .setNotices(notice)
                .build()
                .show()
        }

        tv_source.setOnClickListener { openBrowser(GITHUB_URL) }
        tv_feedback.setOnClickListener { showFeedBackMenu() }
        tv_thirdLib.setOnClickListener {
            LicensesDialog.Builder(this)
                .setNotices(R.raw.licenses)
                .build()
                .show()
        }
        tv_developer.setOnClickListener { openBrowser(GITHUB_HOME) }
    }

    private fun showFeedBackMenu() {
        val feedbackMenu = PopupMenu(this, tv_feedback, Gravity.END)
        feedbackMenu.menuInflater.inflate(R.menu.menu_feedback, feedbackMenu.menu)
        feedbackMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_issue -> {
                    openBrowser(ISSUE_URL)
                    true
                }
                R.id.menu_send_email -> {
                    val uri = Uri.parse(getString(R.string.send_email_to))
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_topic))
                    startActivity(intent)
                    true
                }
                else -> {
                    true
                }
            }
        }
        feedbackMenu.show()
    }


    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(AboutActivity::class.java)
                .launch()
        }
    }
}