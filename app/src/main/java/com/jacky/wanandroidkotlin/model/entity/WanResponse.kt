package com.jacky.wanandroidkotlin.model.entity

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：WanAndroid Api 返回json数据格式封装
 * record：父类泛型对象可以赋值给子类泛型对象，用 in，只能输入，等同Java的super；
 * 子类泛型对象可以赋值给父类泛型对象，用 out,只能输出，等同Java的extends。
 */
data class WanResponse<out T>(
    val errorCode: Int,
    val errorMsg: String,
    val data: T
)