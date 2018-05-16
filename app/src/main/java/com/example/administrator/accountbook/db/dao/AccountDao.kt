package com.example.administrator.accountbook.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.administrator.accountbook.db.entities.Account

/**
 * Created by {zpj}
 *  on 2018/5/16 0016.
 */
@Dao
interface AccountDao {
    /**
     * 添加或修改
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAccount(account: Account)

    /**
     * 删除
     */
    @Query("DELETE FROM Account Where id = :id")
    fun deleteById(id: String)

    /**
     * 获取所有账单
     */
    @Query("SELECT FROM Account ")
    fun getAllAccounts(): List<Account>

    /**
     * 根据id获取账单
     */
    @Query("SELECT FROM Account WHERE id = :id")
    fun getAccountById(id: String)

}