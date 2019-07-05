package com.jacky.wanandroidkotlin.model.entity

import java.io.Serializable

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：树 父节点数据实体
 * record：
 */
data class TreeParentEntity(
    val children: List<TreeChildEntity>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int,
    val userControlSetTop: Boolean
) : Serializable