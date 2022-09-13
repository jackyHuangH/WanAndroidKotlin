package com.jacky.wanandroidkotlin.common

import com.jacky.wanandroidkotlin.R

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
    "https://picsum.photos/id/100/2500/1656",
    "https://picsum.photos/id/1009/5000/7502",
    "https://picsum.photos/id/1011/5472/3648",
    "https://picsum.photos/id/1014/6016/4000",
    "https://picsum.photos/id/1028/5184/3456",
    "https://picsum.photos/id/1036/4608/3072"
)

//本地资源图片
val PIC_RES_LIST = listOf<Int>(
    R.drawable.bing0,
    R.drawable.bing1,
    R.drawable.bing2,
    R.drawable.bing3,
    R.drawable.bing4,
    R.drawable.bing5
)

/**
 * mmkv key常量
 */
object MMkvKey {
    const val ICON_SETTING = "icon_setting"
}