package com.example.administrator.accountbook.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.administrator.accountbook.db.entities.Financial
import java.util.*

/**
 * Created by {zpj}
 *  on 2018/5/18 0018.
 */
@Dao
interface FinancialDao {
    /**
     * 添加
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFinancial(financial: Financial)

    /**
     * 删除
     */
    @Query("DELETE   FROM  Financial WHERE id = :id")
    fun deleteFinancialById(id: String)

    /**
     * 获取全部
     */
    @Query("SELECT * FROM Financial")
    fun loadAllFinancials(): List<Financial>

    /**
     * 获取理财
     */
    @Query("SELECT * FROM Financial WHERE id = :id")
    fun getFinancialById(id: String): Financial

    @Query("SELECT * FROM Financial WHERE input_date BETWEEN :from AND :end")
    fun loadFinancials(from: Date, end: Date): List<Financial>

    @Query("SELECT * FROM Financial WHERE income_date = :date")
    fun loadDayFinancials(date: String): List<Financial>
}