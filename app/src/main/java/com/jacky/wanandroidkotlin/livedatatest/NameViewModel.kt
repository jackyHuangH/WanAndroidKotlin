package com.jacky.wanandroidkotlin.livedatatest

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * @author:Hzj
 * @date :2019/6/21/021
 * desc  ：学习liveData
 * record：
 * <p>通常使用LiveData有三个步骤：
 * 1，创建LiveData实例来保存数据，常常是配合ViewModel一起工作；
 * 2，定义一个Observer的观察者对象，如果有数据更新会通过观察者的onChanged()方法来同步到UI上面；
 * 3，将观察者Observer通过observe()方法进行绑定。
 * LiveData有两种使用方法：一种是直接使用，如接下来的例子；还有一种是继承LiveData的实现资源共享的方式。
 * 直接使用的时候，LiveData一般和抽象类ViewModel一起使用。
 * <p>
 */
class NameViewModel : ViewModel() {
    // Create a LiveData with a String
    private var mName: MutableLiveData<String>? = null

    // Create a LiveData with a String List
    private var mNameList: MutableLiveData<List<String>>? = null

    fun currentName(): MutableLiveData<String> {
        if (mName == null) {
            mName = MutableLiveData()
        }
        return mName as MutableLiveData<String>
    }

    fun getNameList(): MutableLiveData<List<String>> {
        if (mNameList == null) {
            mNameList = MutableLiveData()
        }
        return mNameList as MutableLiveData<List<String>>
    }
}