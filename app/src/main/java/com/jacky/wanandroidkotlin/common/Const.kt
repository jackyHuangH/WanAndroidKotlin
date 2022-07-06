package com.jacky.wanandroidkotlin.common

import android.Manifest
import com.yanzhenjie.permission.runtime.Permission

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：
 * record：
 */
const val LOGGERKIT_TAG = "HZJ_DEBUG"//loggerkit tag

const val TOOL_URL = "http://www.wanandroid.com/tools"
const val GITHUB_HOME = "https://github.com/jackyHuangH"
const val ISSUE_URL = "https://github.com/jackyHuangH/WanAndroidKotlin/issues"
const val GITHUB_URL = "https://github.com/jackyHuangH/WanAndroidKotlin"

//本地测试用的网络图片
val TEST_IMG_URLS = arrayOf(
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538114736288&di=adabcb5cdae781129ea12cf9d4c59707&imgtype=0&src=http%3A%2F%2Fcdn.feeyo.com%2Fpic%2F20150611%2F201506110343194676.jpg",
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538114736286&di=eff228f86402653761a63af1a1971861&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201502%2F04%2F185400pncct1hnj59v9oq9.jpg",
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538114930932&di=06a564006c1a0e142ebb9c88b2f96db4&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fd%2F58635f487bfb8.jpg",
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538114952417&di=c863c55262c5185b46f4db85088d7b7e&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F8%2F57d1105e7abdc.jpg",
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538114968846&di=445ce2717df360f8d102560126f580ed&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201608%2F06%2F20160806152235_fBkv2.jpeg",
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538114983263&di=6c21411a8f9a486348fd83ab9a8f99d8&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201503%2F20%2F000412d0szbrmowmhjbiwh.jpg"
)

/**
 * mmkv key常量
 */
object MMkvKey{
    const val ICON_SETTING="icon_setting"
}