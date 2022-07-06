package com.jacky.wanandroidkotlin.util

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.zenchn.support.utils.StringUtils

/**
 * @author: hzj
 * @date 2022-07-05 16:39:19
 * mmkv工具类
 */
object MMKVUtils {
    private var mmkv: MMKV? = null

    fun init(context: Context) {
        MMKV.initialize(context)
        mmkv = MMKV.defaultMMKV()
    }
    /**
     * 存值，默认关联用户id
     *
     * @param key               key
     * @param value             value
     * @param isAssociatedUsers 是否关联用户id， true 关联，false 不关联
     */
    /**
     * 存值，默认关联用户id
     *
     * @param key   key
     * @param value value
     */
    @JvmOverloads
    fun put(key: String, value: Any?, isAssociatedUsers: Boolean = false) {
        checkNotNull(mmkv) { "u must init mmkv first!" }
        //如果需要跟用户id关联，处理key
        val userId = ""
        val newKey = if (isAssociatedUsers) key + userId else key
        when (value) {
            is Int -> {
                mmkv?.encode(newKey, (value as Int))
            }
            is Float -> {
                mmkv?.encode(newKey, (value as Float))
            }
            is Long -> {
                mmkv?.encode(newKey, (value as Long))
            }
            is Double -> {
                mmkv?.encode(newKey, (value as Double))
            }
            is Boolean -> {
                mmkv?.encode(newKey, (value as Boolean))
            }
            is String -> {
                mmkv?.encode(newKey, value as? String)
            }
            is Parcelable -> {
                mmkv?.encode(newKey, value as? Parcelable)
            }
            else -> {
                mmkv?.encode(newKey, Gson().toJson(value))
            }
        }
    }
    /**
     * 取值
     *
     * @param key               key
     * @param value             value
     * @param isAssociatedUsers 是否关联用户id， true 关联，false 不关联
     * @return
     */
    /**
     * 取值，默认关联用户id
     *
     * @param key   key
     * @param defValue defValue
     */
    @JvmOverloads
    operator fun get(key: String, value: Any, isAssociatedUsers: Boolean = false): Any? {
        checkNotNull(mmkv) { "u must init mmkv first!" }
        //如果需要跟用户id关联，处理key
        val userId = ""
        val newKey = if (isAssociatedUsers) key + userId else key
        if (value is Int) {
            return mmkv?.decodeInt(newKey, (value as Int))
        } else if (value is Float) {
            return mmkv?.decodeFloat(newKey, (value as Float))
        } else if (value is Long) {
            return mmkv?.decodeLong(newKey, (value as Long))
        } else if (value is Double) {
            return mmkv?.decodeDouble(newKey, (value as Double))
        } else if (value is Boolean) {
            return mmkv?.decodeBool(newKey, (value as Boolean))
        } else if (value is String) {
            return mmkv?.decodeString(newKey, value as String)
        } else if (value is Parcelable) {
            return mmkv?.decodeParcelable<Parcelable>(newKey, value.javaClass, value as? Parcelable)
        } else if (value != null) {
            val str = mmkv?.decodeString(newKey, "")
            if (str != null && "" != str) {
                return Gson().fromJson(str, value.javaClass)
            }
        }
        return value
    }
    /**
     * 移除
     *
     * @param key               key
     * @param isAssociatedUsers 是否关联用户id， true 关联，false 不关联
     */
    /**
     * 移除 默认关联用户id
     *
     * @param key key
     */
    @JvmOverloads
    fun remove(key: String, isAssociatedUsers: Boolean = false) {
        mmkv?.remove(key)
    }

    /**
     * 获取缓存字符串 默认关联用户id
     *
     * @param key key
     * @return
     */
    fun getString(key: String): String? {
        return getString(key, false)
    }

    /**
     * 获取缓存字符串
     *
     * @param key               key
     * @param isAssociatedUsers 是否关联用户id， true 关联，false 不关联
     * @return
     */
    fun getString(key: String, isAssociatedUsers: Boolean): String? {
        return MMKVUtils[key, "", isAssociatedUsers] as String?
    }

    /**
     * 获取缓存字符串,无缓存就取默认值
     *
     * @param key               key
     * @param isAssociatedUsers 是否关联用户id， true 关联，false 不关联
     * @param def               传入的默认值，无缓存就取默认值
     */
    fun getStringElseDefault(key: String, isAssociatedUsers: Boolean, def: String): String {
        val cacheStr = MMKVUtils[key, "", isAssociatedUsers] as? String
        return if (!StringUtils.isEmpty(cacheStr)) {
            cacheStr.orEmpty()
        } else def
    }
}