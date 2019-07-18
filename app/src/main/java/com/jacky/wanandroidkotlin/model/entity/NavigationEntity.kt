package com.jacky.wanandroidkotlin.model.entity

/**
 * @author:Hzj
 * @date  :2019-07-18
 * desc  ：导航数据实体
 * record：
 */
data class NavigationEntity(val articles: List<ArticleEntity>,
                            val cid: Int,
                            val name: String)