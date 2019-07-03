package com.jacky.wanandroidkotlin.model.entity

/**
 * @author:Hzj
 * @date  :2019/7/3/003
 * desc  ：用户信息数据实体
 * record：
 */
data class UserEntity(
    val collectIds: List<Int>,
    val email: String,
    val icon: String,
    val id: Int,
    val password: String,
    val type: Int,
    val username: String
)