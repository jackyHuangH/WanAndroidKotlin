package com.jacky.wanandroidkotlin.ui.about

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.common.GITHUB_HOME
import com.jacky.wanandroidkotlin.common.GITHUB_URL
import com.jacky.wanandroidkotlin.common.ISSUE_URL
import com.jacky.wanandroidkotlin.databinding.ActivityAboutBinding
import com.jacky.wanandroidkotlin.util.openBrowser
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.jacky.support.router.Router
import com.jacky.support.utils.AndroidKit
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.model.Notice

/**
 * @author:Hzj
 * @date  :2019-07-20
 * desc  ：
 * record：
 */
class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_about

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        viewExt<Toolbar>(R.id.toolbar) {
            setNavigationOnClickListener { onBackPressed() }
            title = getString(R.string.about_title)
        }
        val versionName = AndroidKit.Package.getVersionName(this)
        getView<TextView>(R.id.tv_version).text = "V$versionName"

        getView<TextView>(R.id.tv_license).setOnClickListener {
            val license = ApacheSoftwareLicense20()
            val notice = Notice(getString(R.string.app_name), GITHUB_URL, "", license)
            LicensesDialog.Builder(this)
                .setNotices(notice)
                .build()
                .show()
        }

        getView<TextView>(R.id.tv_source).setOnClickListener { openBrowser(GITHUB_URL) }
        getView<TextView>(R.id.tv_feedback).setOnClickListener { showFeedBackMenu() }
        getView<TextView>(R.id.tv_thirdLib).setOnClickListener {
            LicensesDialog.Builder(this)
                .setNotices(R.raw.licenses)
                .build()
                .show()
        }
        getView<TextView>(R.id.tv_developer).setOnClickListener { openBrowser(GITHUB_HOME) }
    }

    private fun showFeedBackMenu() {
        val tvFeedback = getView<TextView>(R.id.tv_feedback)
        val feedbackMenu = PopupMenu(this, tvFeedback, Gravity.END)
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