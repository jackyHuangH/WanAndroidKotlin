package com.jacky.wanandroidkotlin.ui.demos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.jacky.support.utils.LoggerKit
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.WeatherIconBean

/**
 * @author:Hzj
 * @date  :2022/11/7
 * desc  ：天气资源管理
 * record：
 */
object WeatherResManager {
    //白天天气图标
    private val mIconDayList by lazy { mutableListOf<WeatherIconBean>() }

    //夜间天气图标
    private val mIconNightList by lazy { mutableListOf<WeatherIconBean>() }

    /**
     * 初始化天气图表映射
     */
    fun initIcons() {
        mIconDayList.clear()
        mIconNightList.clear()
        mIconDayList.apply {
            add(WeatherIconBean(100, "晴", R.drawable.icon_100d))
            add(WeatherIconBean(101, "多云", R.drawable.icon_101d))
            add(WeatherIconBean(102, "少云", R.drawable.icon_102d))
            add(WeatherIconBean(103, "晴间多云", R.drawable.icon_103d))
            add(WeatherIconBean(104, "阴", R.drawable.icon_104d))
            add(WeatherIconBean(150, "晴(夜)", R.drawable.icon_100n))
            add(WeatherIconBean(153, "晴间多云(夜)", R.drawable.icon_103n))
            add(WeatherIconBean(154, "阴(夜)", R.drawable.icon_104n))
            add(WeatherIconBean(300, "阵雨", R.drawable.icon_300d))
            add(WeatherIconBean(301, "强阵雨", R.drawable.icon_301d))
            add(WeatherIconBean(302, "雷阵雨", R.drawable.icon_302d))
            add(WeatherIconBean(303, "强雷阵雨", R.drawable.icon_303d))
            add(WeatherIconBean(305, "小雨", R.drawable.icon_305d))
            add(WeatherIconBean(306, "中雨", R.drawable.icon_306d))
            add(WeatherIconBean(307, "大雨", R.drawable.icon_307d))
            add(WeatherIconBean(308, "极端降雨", R.drawable.icon_308d))
            add(WeatherIconBean(309, "毛毛雨/细雨", R.drawable.icon_309d))
            add(WeatherIconBean(310, "暴雨", R.drawable.icon_310d))
            add(WeatherIconBean(311, "大暴雨", R.drawable.icon_311d))
            add(WeatherIconBean(312, "特大暴雨", R.drawable.icon_312d))
            add(WeatherIconBean(313, "冻雨", R.drawable.icon_313d))
            add(WeatherIconBean(314, "小到中雨", R.drawable.icon_314d))
            add(WeatherIconBean(315, "中到大雨", R.drawable.icon_315d))
            add(WeatherIconBean(316, "大到暴雨", R.drawable.icon_316d))
            add(WeatherIconBean(317, "暴雨到大暴雨", R.drawable.icon_317d))
            add(WeatherIconBean(318, "大暴雨到特大暴雨", R.drawable.icon_318d))
            add(WeatherIconBean(399, "雨", R.drawable.icon_399d))
            add(WeatherIconBean(350, "阵雨", R.drawable.icon_300d))
            add(WeatherIconBean(351, "强阵雨", R.drawable.icon_301d))
            add(WeatherIconBean(400, "小雪", R.drawable.icon_400d))
            add(WeatherIconBean(401, "中雪", R.drawable.icon_401d))
            add(WeatherIconBean(402, "大雪", R.drawable.icon_402d))
            add(WeatherIconBean(403, "暴雪", R.drawable.icon_403d))
            add(WeatherIconBean(404, "雨夹雪", R.drawable.icon_404d))
            add(WeatherIconBean(405, "雨雪天气", R.drawable.icon_405d))
            add(WeatherIconBean(406, "阵雨夹雪", R.drawable.icon_406d))
            add(WeatherIconBean(407, "阵雪", R.drawable.icon_407d))
            add(WeatherIconBean(408, "小到中雪", R.drawable.icon_408d))
            add(WeatherIconBean(409, "中到大雪", R.drawable.icon_409d))
            add(WeatherIconBean(410, "大到暴雪", R.drawable.icon_410d))
            add(WeatherIconBean(456, "阵雨夹雪", R.drawable.icon_406d))
            add(WeatherIconBean(457, "阵雪", R.drawable.icon_407d))
            add(WeatherIconBean(499, "雪", R.drawable.icon_499d))
            add(WeatherIconBean(500, "薄雾", R.drawable.icon_500d))
            add(WeatherIconBean(501, "雾", R.drawable.icon_501d))
            add(WeatherIconBean(502, "霾", R.drawable.icon_502d))
            add(WeatherIconBean(503, "扬沙", R.drawable.icon_503d))
            add(WeatherIconBean(504, "浮尘", R.drawable.icon_504d))
            add(WeatherIconBean(507, "沙尘暴", R.drawable.icon_507d))
            add(WeatherIconBean(508, "强沙尘暴", R.drawable.icon_508d))
            add(WeatherIconBean(509, "浓雾", R.drawable.icon_509d))
            add(WeatherIconBean(510, "强浓雾", R.drawable.icon_510d))
            add(WeatherIconBean(511, "中度霾", R.drawable.icon_511d))
            add(WeatherIconBean(512, "重度霾", R.drawable.icon_512d))
            add(WeatherIconBean(513, "严重霾", R.drawable.icon_513d))
            add(WeatherIconBean(514, "大雾", R.drawable.icon_514d))
            add(WeatherIconBean(515, "特强浓雾", R.drawable.icon_515d))
            add(WeatherIconBean(900, "热", R.drawable.icon_900d))
            add(WeatherIconBean(901, "冷", R.drawable.icon_901d))
            add(WeatherIconBean(999, "未知", R.drawable.icon_999d))
        }
        mIconNightList.apply {
            add(WeatherIconBean(100, "晴", R.drawable.icon_100n))
            add(WeatherIconBean(101, "多云", R.drawable.icon_101n))
            add(WeatherIconBean(102, "少云", R.drawable.icon_102n))
            add(WeatherIconBean(103, "晴间多云", R.drawable.icon_103n))
            add(WeatherIconBean(104, "阴", R.drawable.icon_104n))
            add(WeatherIconBean(150, "晴(夜)", R.drawable.icon_100n))
            add(WeatherIconBean(153, "晴间多云(夜)", R.drawable.icon_103n))
            add(WeatherIconBean(154, "阴(夜)", R.drawable.icon_104n))
            add(WeatherIconBean(300, "阵雨", R.drawable.icon_300n))
            add(WeatherIconBean(301, "强阵雨", R.drawable.icon_301n))
            add(WeatherIconBean(302, "雷阵雨", R.drawable.icon_302n))
            add(WeatherIconBean(303, "强雷阵雨", R.drawable.icon_303n))
            add(WeatherIconBean(305, "小雨", R.drawable.icon_305n))
            add(WeatherIconBean(306, "中雨", R.drawable.icon_306n))
            add(WeatherIconBean(307, "大雨", R.drawable.icon_307n))
            add(WeatherIconBean(308, "极端降雨", R.drawable.icon_308n))
            add(WeatherIconBean(309, "毛毛雨/细雨", R.drawable.icon_309n))
            add(WeatherIconBean(310, "暴雨", R.drawable.icon_310n))
            add(WeatherIconBean(311, "大暴雨", R.drawable.icon_311n))
            add(WeatherIconBean(312, "特大暴雨", R.drawable.icon_312n))
            add(WeatherIconBean(313, "冻雨", R.drawable.icon_313n))
            add(WeatherIconBean(314, "小到中雨", R.drawable.icon_314n))
            add(WeatherIconBean(315, "中到大雨", R.drawable.icon_315n))
            add(WeatherIconBean(316, "大到暴雨", R.drawable.icon_316n))
            add(WeatherIconBean(317, "暴雨到大暴雨", R.drawable.icon_317n))
            add(WeatherIconBean(318, "大暴雨到特大暴雨", R.drawable.icon_318n))
            add(WeatherIconBean(399, "雨", R.drawable.icon_399n))
            add(WeatherIconBean(350, "阵雨", R.drawable.icon_300n))
            add(WeatherIconBean(351, "强阵雨", R.drawable.icon_301n))
            add(WeatherIconBean(400, "小雪", R.drawable.icon_400n))
            add(WeatherIconBean(401, "中雪", R.drawable.icon_401n))
            add(WeatherIconBean(402, "大雪", R.drawable.icon_402n))
            add(WeatherIconBean(403, "暴雪", R.drawable.icon_403n))
            add(WeatherIconBean(404, "雨夹雪", R.drawable.icon_404n))
            add(WeatherIconBean(405, "雨雪天气", R.drawable.icon_405n))
            add(WeatherIconBean(406, "阵雨夹雪", R.drawable.icon_406n))
            add(WeatherIconBean(407, "阵雪", R.drawable.icon_407n))
            add(WeatherIconBean(408, "小到中雪", R.drawable.icon_408n))
            add(WeatherIconBean(409, "中到大雪", R.drawable.icon_409n))
            add(WeatherIconBean(410, "大到暴雪", R.drawable.icon_410n))
            add(WeatherIconBean(456, "阵雨夹雪", R.drawable.icon_406n))
            add(WeatherIconBean(457, "阵雪", R.drawable.icon_407n))
            add(WeatherIconBean(499, "雪", R.drawable.icon_499n))
            add(WeatherIconBean(500, "薄雾", R.drawable.icon_500n))
            add(WeatherIconBean(501, "雾", R.drawable.icon_501n))
            add(WeatherIconBean(502, "霾", R.drawable.icon_502n))
            add(WeatherIconBean(503, "扬沙", R.drawable.icon_503n))
            add(WeatherIconBean(504, "浮尘", R.drawable.icon_504n))
            add(WeatherIconBean(507, "沙尘暴", R.drawable.icon_507n))
            add(WeatherIconBean(508, "强沙尘暴", R.drawable.icon_508n))
            add(WeatherIconBean(509, "浓雾", R.drawable.icon_509n))
            add(WeatherIconBean(510, "强浓雾", R.drawable.icon_510n))
            add(WeatherIconBean(511, "中度霾", R.drawable.icon_511n))
            add(WeatherIconBean(512, "重度霾", R.drawable.icon_512n))
            add(WeatherIconBean(513, "严重霾", R.drawable.icon_513n))
            add(WeatherIconBean(514, "大雾", R.drawable.icon_514n))
            add(WeatherIconBean(515, "特强浓雾", R.drawable.icon_515n))
            add(WeatherIconBean(900, "热", R.drawable.icon_900n))
            add(WeatherIconBean(901, "冷", R.drawable.icon_901n))
            add(WeatherIconBean(999, "未知", R.drawable.icon_999n))
        }
    }

    /**
     * 根据code查找对应icon，这种方式太臃肿，需要把所有code和icon对应关系列全，不推荐
     */
    @Deprecated("维护不方便，不推荐")
    fun getIconByCode(code: Int?, night: Boolean = false): Int {
        if (night) {
            mIconNightList.forEach {
                if (it.code == code) {
                    return it.resId
                }
            }
        } else {
            mIconDayList.forEach {
                if (it.code == code) {
                    return it.resId
                }
            }
        }
        //未查询到就返回未知图标
        return R.drawable.icon_999d
    }

    /**
     * 根据code查找对应icon
     */
    fun getIconByCode(context: Context, code: Int?, night: Boolean = false): Int {
        return try {
            //适配新增图标icon
            val newCode = when (code) {
                in 150 until 199,
                in 350 until 399,
                in 450 until 499 ->
                    code?.minus(50)
                else -> code
            }
            val name = "icon_$newCode${if (night) "n" else "d"}"
            //根据资源名称 查找drawable icon，
            var resId = context.resources.getIdentifier(name, "drawable", context.packageName)
            if (resId == 0) {
                resId = R.drawable.icon_999d
            }
            resId
        } catch (e: Exception) {
            LoggerKit.e("查找天气icon失败：${e.message}")
            R.drawable.icon_999d
        }
    }

    /**
     * 将图片压缩并返回Bitmap
     */
    fun getScaledBitmap(drawable: Drawable, w: Float, h: Float): Bitmap? {
        val bd = drawable as? BitmapDrawable
        return bd?.let {
            val src = bd.bitmap
            val matrix = Matrix()
            matrix.postScale(w / src.width, h / src.height)
            Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        }
    }
}

