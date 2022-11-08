package com.jacky.wanandroidkotlin.ui.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.DailyEntity
import com.jacky.wanandroidkotlin.ui.demos.WeatherResManager
import com.jacky.wanandroidkotlin.widget.TempChart
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @author:Hzj
 * @date  :2022/11/7
 * desc  ：15天天气adapter
 * record：
 */
class Weather15DaysAdapter(resId: Int = R.layout.recycler_item_weather_15_daily) :
    BaseQuickAdapter<DailyEntity, BaseViewHolder>(resId) {
    override fun convert(holder: BaseViewHolder, item: DailyEntity) {
        holder.apply {
            //星期
            setText(R.id.tv_week, getWeekDay(absoluteAdapterPosition, item))
            setText(R.id.tv_date, item.fxDate.removeRange(0, 5))
            setText(R.id.tv_weather_day, item.textDay.orEmpty())
            setImageDrawable(
                R.id.iv_day,
                ContextCompat.getDrawable(
                    context,
                    WeatherResManager.getIconByCode(context, item.iconDay)
                )
            )
            setImageDrawable(
                R.id.iv_night,
                ContextCompat.getDrawable(
                    context,
                    WeatherResManager.getIconByCode(context, item.iconNight, true)
                )
            )
            setText(R.id.tv_weather_night, item.textNight.orEmpty())
            setText(R.id.tv_wind, item.windDirDay.orEmpty())
            setText(R.id.tv_wind_level, "${item.windScaleDay.orEmpty()}级")
            //设置温度折线数据
            getView<TempChart>(R.id.temp_chart).apply {
                val previous: DailyEntity? =
                    if (absoluteAdapterPosition > 0) getItem(absoluteAdapterPosition - 1) else null
                val next: DailyEntity? =
                    if (absoluteAdapterPosition < itemCount-1) getItem(absoluteAdapterPosition + 1) else null
                val minMaxTempPair = getMinMaxTemp()
                setDailyWeatherData(
                    item,
                    previous,
                    next,
                    minMaxTempPair.first,
                    minMaxTempPair.second
                )
            }
        }
    }

    private fun getMinMaxTemp(): Pair<Int, Int> {
        var minTemp = getItem(0).tempMin
        var maxTemp = getItem(0).tempMax
        data.forEach {
            minTemp = min(minTemp, it.tempMin)
            maxTemp = max(maxTemp, it.tempMax)
        }
        return Pair(minTemp, maxTemp)
    }

    private fun getWeekDay(position: Int, item: DailyEntity): String {
        val weeks = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
        return if (position == 0) {
            "今天"
        } else {
            val calendar = Calendar.getInstance()
            val dateArray = item.fxDate.split("-")
            calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt())
            var w = calendar.get(Calendar.DAY_OF_WEEK) - 1
            if (w < 0) {
                w = 0
            }
            weeks[w]
        }
    }
}