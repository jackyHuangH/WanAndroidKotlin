package com.jacky.wanandroidkotlin.model.entity

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：项目分类父节点数据实体
 * record：
 */
data class ProjectTypeParent(
    val children: List<ProjectTypeChild>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int,
    val userControlSetTop: Boolean
)