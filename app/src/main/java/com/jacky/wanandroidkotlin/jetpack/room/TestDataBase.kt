package com.jacky.wanandroidkotlin.jetpack.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jacky.wanandroidkotlin.model.local.ContextModel

/**
 * @author:Hzj
 * @date  :2022/6/10
 * desc  ：Room数据库必须是一个继承自RoomDatabase的抽象类。通常情况下应用内应该只有一个Room数据库实例。
 * 注释1处使用@Database注解WordRoomDatabase类，并指定数据库中的表，版本号和是否在指定的schemaLocation目录下输出数据库架构信息
 * record：
 */
@Database(entities = [UserEntity::class], version = 1, exportSchema = true)
abstract class TestDataBase : RoomDatabase() {

    //提供userDao
    abstract fun userDao(): UserDao

    //获取roomDatabase单例
    companion object {

        @Volatile
        private var INSTANCE: TestDataBase? = null


        fun getInstance(): TestDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(TestDataBase::class) {
                val instance = Room.databaseBuilder(ContextModel.getApplicationContext(), TestDataBase::class.java, "test_db").build()
                INSTANCE = instance
                return instance
            }
        }
    }

}