package com.jacky.wanandroidkotlin.model.entity

/**
 * @author:Hzj
 * @date  :2020-01-20
 * desc  ：
 * record：
 */
data class GirlEntity(
    val _id: String,
    val createdAt: String,
    val desc: String,
    val publishedAt: String,
    val source: String,
    val type: String,
    val url: String,
    val used: Boolean,
    val who: String
)