package com.jacky.wanandroidkotlin.util

import android.content.Context
import com.jacky.wanandroidkotlin.model.local.ContextModel
import java.io.*
import kotlin.reflect.KProperty

/**
 * @author:Hzj
 * @date  :2018/12/21/021
 * desc  ：kotlin 属性委托+SharedPreference 工具类
 * record：
 */
class PreferenceUtil<T>(val keyName: String, private val defaultValue: T) {

    companion object {
        private const val FILE_NAME = "SP_Set"

        //记录是否登录
        const val KEY_IS_LOGIN = "is_login"
        //记录已登录用户信息
        const val KEY_USER_INFO = "user_info"
        //记录系统设置的主题模式
        const val KEY_SETTING_THEME = "setting_theme"
        //记录系统设置的多语言
        const val KEY_SETTING_LANGUAGE = "setting_language"
        //记录GoogleMaven最近一次搜索记录
        const val KEY_MAVEN_LATEST_SEARCH_WORD = "maven_latest_search_word"

        private val mPreference by lazy {
            ContextModel.getApplicationContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        }

        /**
         * 根据key移除SP数据
         */
        fun removePreferenceByKey(key: String) {
            mPreference.edit().remove(key).apply()
        }

        /**
         * 清空SP数据
         */
        fun clearAllPreferen() {
            mPreference.edit().clear().apply()
        }
    }

    //属性委托定义set,get
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getPreference(keyName, defaultValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(keyName, value)
    }

    /**
     * 从SP取出
     */
    @Suppress("UNCHECKED_CAST")
    private fun getPreference(key: String, default: T): T = with(mPreference) {
        val res: Any = when (default) {
            is Long -> getLong(key, default)
            is String -> getString(key, default) ?: ""
            is Int -> getInt(key, default)
            is Boolean -> getBoolean(key, default)
            is Float -> getFloat(key, default)
            else -> deSerialization(getString(key, serialize(default)) ?: "")
        }
        return res as T
    }

    /**
     * 存入SP
     */
    private fun putPreference(key: String, value: T) = with(mPreference.edit()) {
        when (value) {
            is Long -> putLong(key, value)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            else -> putString(key, serialize(value))
        }.apply()
    }

    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream
        )
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1"))
        )
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream
        )
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }

    /**
     * 查询是否存在key
     */
    fun contains(key: String): Boolean {
        return mPreference.contains(key)
    }

    /**
     * 取出SP中所有键值对
     */
    fun getAllMap(): Map<String, *> {
        return mPreference.all
    }
}