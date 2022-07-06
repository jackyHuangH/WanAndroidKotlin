package com.zenchn.support.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author:Hzj
 * @date :2022/5/26
 * desc  ：SharedPreferences工具类
 * record：
 */
public class SPUtils {
    private static SharedPreferences mSharedPreferences = null;
    private static SharedPreferences.Editor mEditor = null;
    private SPUtils() {
    }

    public static void init(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void removeKey(String key) {
        mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

    public static void removeAll() {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.commit();
    }

    //保存String
    public static void commitString(String key, String value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }

    /**
     * 读取String
     * @param key
     * @param defValue 为空时的默认值
     * @return
     */
    public static String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public static void commitInt(String key, int value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public static int getInt(String key, int value) {
        return mSharedPreferences.getInt(key, value);
    }

    public static void commitLong(String key, long value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public static Long getLong(String key, long value) {
        return mSharedPreferences.getLong(key, value);
    }

    public static void commitBoolean(String key, boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public static boolean getBoolean(String key, boolean value) {
        return mSharedPreferences.getBoolean(key, value);
    }
}
