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
    suspend fun insert(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("select * from user")
    suspend fun queryAll(): List<UserEntity>
}