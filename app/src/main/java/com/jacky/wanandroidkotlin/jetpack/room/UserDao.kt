package com.jacky.wanandroidkotlin.jetpack.room

import androidx.room.*

/**
 * @author:Hzj
 * @date  :2022/6/10
 * desc  ：DAO必须是接口或者抽象类，Room使用注解帮我们生成访问数据库的代码
 * record：
 */
@Dao
interface UserDao {

    @Insert
    fun insert(user: UserEntity)

    @Delete
    fun delete(user: UserEntity)

    @Update
    fun update(user: UserEntity)

    @Query("select * from user")
    fun queryAll(): List<UserEntity>
}