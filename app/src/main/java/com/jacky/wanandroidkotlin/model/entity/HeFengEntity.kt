package com.jacky.wanandroidkotlin.model.entity


/**
 * @author:Hzj
 * @date  :2022/10/28
 * desc  ：和风天气数据实体
 * record：
 */
data class WeatherEntity(
    val code: Int,
    val fxLink: String?,
    val now: Now?,
    val daily: List<DailyEntity>?,
    val refer: Refer,
    val updateTime: String
)

/**
 * 当天天气
 */
data class Now(
    val cloud: String,
    val dew: String,
    val feelsLike: String,
    val humidity: String,
    val icon: String,
    val obsTime: String,
    val precip: String,
    val pressure: String,
    val temp: String,
    val text: String,
    val vis: String,
    val wind360: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String
)

data class Refer(
    val license: List<String>,
    val sources: List<String>
)

/**
 * 每日天气数据
 */
data class DailyEntity(
    val cloud: String,
    val fxDate: String,
    val humidity: String,
    val iconDay: Int,
    val iconNight: Int,
    val moonPhase: String,
    val moonPhaseIcon: String,
    val moonrise: String,
    val moonset: String,
    val precip: String,
    val pressure: String,
    val sunrise: String,
    val sunset: String,
    val tempMax: Int,
    val tempMin: Int,
    val textDay: String,
    val textNight: String,
    val uvIndex: String,
    val vis: String,
    val wind360Day: String,
    val wind360Night: String,
    val windDirDay: String,
    val windDirNight: String,
    val windScaleDay: String,
    val windScaleNight: String,
    val windSpeedDay: String,
    val windSpeedNight: String
)

/**
 * 天气code-图片对应数据
 */
data class WeatherIconBean(
    val code: Int,
    val name: String,
    val resId: Int
)