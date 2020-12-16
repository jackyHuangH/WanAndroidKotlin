package com.jacky.wanandroidkotlin.jetpack.binding

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField

/**
 * @author:Hzj
 * @date  :2020/12/12
 * desc  ：DataBinding单向绑定 BaseObservable
 * record：1.notifyPropertyChanged(); 只会刷新属于它的UI，就如代码，他只会更新name。
 *         2.notifyChange(); 会刷新所有UI
 */
//class PeopleBean constructor(name: String, age: Int) : BaseObservable() {
//    private var age: Int = 0
//
//    //用 @Bindable 进行注解，否则BR里无法生成对应字段
//    @Bindable
//    var name: String? = null

//    init {
//        this.name = name
//        this.age = age
//    }

//    fun updateName(n: String) {
//        this.name = n
//        //只刷新属于它的UI
//        notifyPropertyChanged(BR.name)
//    }
//
//    fun getAge() = this.age
//
//    fun updateAge(a: Int) {
//        this.age = a
//        //刷新所有UI
//        notifyChange()
//    }
//}

/**
 * 使用ObservableField进行单向绑定，ObservableField是对BaseObservable的简单封装
 */
class PeopleBean constructor(val name: ObservableField<String>, val age: ObservableField<Int>) :
    BaseObservable() {}