package com.example.administrator.accountbook.db.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.administrator.accountbook.base.MyApplication
import com.example.administrator.accountbook.db.dao.FinancialDao
import com.example.administrator.accountbook.db.entities.Financial
import com.example.administrator.jxkd.extensions.Converters

/**
 * Created by {zpj}
 *  on 2018/5/18 0018.
 */
@Database(entities = [Financial::class], version = 1)
@TypeConverters(Converters::class)
abstract class FinancialDatabase : RoomDatabase() {
    abstract fun financialDao(): FinancialDao

    companion object {
        private var INSTANCE: FinancialDatabase? = null

        private val lock = Any()

        fun getInstance(): FinancialDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(MyApplication.instance.applicationContext, FinancialDatabase::class.java, "Financial.db")
                            .build()

                }
                return INSTANCE!!
            }
        }
    }

}

fun Context.getFinancialDao(): FinancialDao {
    return FinancialDatabase.getInstance().financialDao()
}