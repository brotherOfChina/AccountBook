package com.example.administrator.accountbook.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.administrator.accountbook.db.entities.User

/**
 * Created by {zpj}
 *  on 2018/5/14 0014.
 */
@Dao
interface UserDao {
    /**
     * 添加用户user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: User)

    /**
     * 获取所有用户
     */
    @Query("SELECT * FROM User")
    fun getUsers(): List<User>

    /**
     * 根据手机号获取用户
     */
    @Query("SELECT * FROM User WHERE phone = :phone")
    fun getUser(phone: String):User
    /**
     * 根据昵称获取用户
     */
    @Query("SELECT * FROM User WHERE nick_name = :nick_name")
    fun getUserByNick(nick_name: String):User
    /**
     * 删除用户
     */
    @Query("DELETE FROM User WHERE uid = :uid")
    fun deleteUid(uid: String)

}