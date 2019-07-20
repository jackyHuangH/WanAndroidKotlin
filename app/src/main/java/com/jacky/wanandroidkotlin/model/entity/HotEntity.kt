package com.jacky.wanandroidkotlin.model.entity

/**
 * 热搜词
 */
data class HotEntity(
    val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int,
    val icon: String
)