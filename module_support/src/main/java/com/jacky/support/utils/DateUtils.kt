package com.jacky.support.utils

import androidx.annotation.IntRange
import com.jacky.support.safelyRun
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.text.SimpleDateFormat
import java.util.*

/**
 * 作   者：hzj on 2019/11/11 13:24
 * 描   述：
 * 修订记录：
 */

object DateUtils {

    fun dateFormat(
        @NotNull date: Date,
        @NotNull pattern: String = DateFormatTemplate.SIMPLE_DATE_FORMAT_TEMPLATE,
        @Nullable def: String? = null
    ): String? {
        return sdf.safelyRun {
            it.applyPattern(pattern)
            it.format(date)
        } ?: def
    }


    /**
     * 是否是同一天
     *
     * @param aDate
     * @param bDate
     * @return
     */
    fun isSameDay(aDate: Date, bDate: Date): Boolean {
        return aDate.startTimeOfDay().time == bDate.startTimeOfDay().time;
    }

    /**
     * 读取两个日期之间的天数
     *
     * @return
     */
    fun getDaysBetween(aDate: Date, bDate: Date, ceil: Boolean = true): Int {
        return (getTimeMillisBetween(aDate, bDate) / (3600.0 * 24)).let {
            if (ceil) kotlin.math.ceil(it)
            else kotlin.math.floor(it)
        }.toInt()
    }

    /**
     * 读取两个日期之间的小时数
     *
     * @return
     */
    fun getHoursBetween(aDate: Date, bDate: Date, ceil: Boolean = true): Int {
        return (getTimeMillisBetween(aDate, bDate) / 3600.0).let {
            if (ceil) kotlin.math.ceil(it)
            else kotlin.math.floor(it)
        }.toInt()
    }

    /**
     * 读取两个日期之间秒数
     *
     * @return
     */
    fun getTimeMillisBetween(aDate: Date, bDate: Date, ceil: Boolean = true): Long {
        return (kotlin.math.abs(aDate.time - bDate.time) / 1000.0).let {
            if (ceil) kotlin.math.ceil(it)
            else kotlin.math.floor(it)
        }.toLong()
    }
}

object DateFormatTemplate {
    //# 默认时间格式
    const val SIMPLE_DATE_FORMAT_TEMPLATE = "yyyy-MM-dd HH:mm:ss"

    const val year = "yyyy"
    const val month = "MM"
    const val day = "dd"
    const val md = "MM-dd"
    const val ymd = "yyyy-MM-dd"
    const val ymdDot = "yyyy.MM.dd"
    const val ymdhm = "yyyy-MM-dd HH:mm"
    const val ymdhms = "yyyy-MM-dd HH:mm:ss"
    const val ymdhmss = "yyyy-MM-dd HH:mm:ss.SSS"
    const val hm = "HH:mm"
    const val mdhm = "MM月dd日 HH:mm"
    const val mdhmLink = "MM-dd HH:mm"
}

internal val sdf: SimpleDateFormat by lazy {
    SimpleDateFormat(DateFormatTemplate.SIMPLE_DATE_FORMAT_TEMPLATE, Locale.CHINA)
}

fun Date?.dateFormat(
    @NotNull pattern: String = DateFormatTemplate.SIMPLE_DATE_FORMAT_TEMPLATE,
    @Nullable def: String? = null
): String? =
    this?.let { DateUtils.dateFormat(it, pattern) } ?: def

fun Date.toCalendar(): Calendar = Calendar.getInstance().apply {
    time = this@toCalendar
}

fun Date.switch(@NotNull switch: (Calendar.() -> Unit)): Date {
    return toCalendar().apply(switch).time
}

/**
 * 未来N天
 */
fun Date.future(@IntRange(from = 0) days: Int): Date = switch {
    add(Calendar.HOUR_OF_DAY, 24 * days);
}

/**
 * 过去N天
 */
fun Date.past(@IntRange(from = 0) days: Int): Date = switch {
    add(Calendar.HOUR_OF_DAY, -24 * days);
}

fun Date.yesterday(): Date = past(1)

fun Date.tomorrow(): Date = future(1)

/**
 * 获取当天0点0分0秒
 *
 * @return
 */
fun Date.startTimeOfDay(): Date = switch {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

/**
 * 获取当天23点59分59秒
 *
 * @return
 */
fun Date.endTimeOfDay(): Date = switch {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}

/**
 * 获取本周第一天0点0分0秒
 *
 * @return
 */
fun Date.startTimeOfWeek(): Date = switch {
    firstDayOfWeek = Calendar.MONDAY
    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
}.startTimeOfDay()

/**
 * 获取本周最后一天23点59分59秒
 *
 * @return
 */
fun Date.endTimeOfWeek(): Date = switch {
    firstDayOfWeek = Calendar.MONDAY
    set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
}.endTimeOfDay()

/**
 * 获取当月第一天0点0分0秒
 *
 * @return
 */
fun Date.startTimeOfMonth(): Date = switch {
    set(Calendar.DAY_OF_MONTH, 1)
}.startTimeOfDay()

/**
 * 获取当月最后一天23点59分59秒
 *
 * @return
 */
fun Date.endTimeOfMonth(): Date = switch {
    set(Calendar.DAY_OF_MONTH, 1)
    roll(Calendar.DAY_OF_MONTH, -1)
}.endTimeOfDay()
