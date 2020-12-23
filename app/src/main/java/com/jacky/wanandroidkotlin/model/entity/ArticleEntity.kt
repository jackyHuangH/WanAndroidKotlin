package com.jacky.wanandroidkotlin.model.entity

import java.io.Serializable

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：首页文章数据实体
 * record：
 */
data class ArticleEntity(
    val id: Int,
    val originId: Int,
    val title: String,
    val chapterId: Int,
    val chapterName: String,
    val envelopePic: String,
    val link: String,
    val author: String,
    val origin: String,
    val publishTime: Long,
    val zan: Int,
    val desc: String,
    val visible: Int,
    val niceDate: String,
    val courseId: Int,
    var collect: Boolean,
    val apkLink: String,
    val projectLink: String,
    val superChapterId: Int,
    val superChapterName: String?,
    val type: Int,//1 置顶 0普通
    val fresh: Boolean//是否 新
) : Serializable