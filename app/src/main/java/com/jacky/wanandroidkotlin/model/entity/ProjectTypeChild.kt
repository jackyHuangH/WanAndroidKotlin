package com.jacky.wanandroidkotlin.model.entity

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：项目分类子节点数据实体
 * record：
 */
data class ProjectTypeChild(
    val child: List<ProjectTypeChild>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int
)