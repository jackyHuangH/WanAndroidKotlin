package com.jacky.wanandroidkotlin.model.entity

/**
 * @author:Hzj
 * @date  :2022/10/14
 * desc  ：聚合api 返回数据实体
 * record：
 */
data class JuHeApiEntity<out T>(
    val error_code: Int = 0,
    val reason: String = "",
    val result: T? = null
)

/**
 * 历史上的今天数据实体
 */
data class TodayInHistoryEntity(
    val date: String,
    val day: String,
    val e_id: String,
    val title: String
)