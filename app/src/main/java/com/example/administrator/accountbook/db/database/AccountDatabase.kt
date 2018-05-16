package com.example.administrator.accountbook.db.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.administrator.accountbook.base.MyApplication
import com.example.administrator.accountbook.db.dao.AccountDao
import com.example.administrator.accountbook.db.entities.Account
import com.example.administrator.accountbook.db.entities.User

/**
 * Created by 赵鹏军
 *  on 2018/5/16 0016.
 */
@Database(entities = [(Account::class),(User::class)], version = 1)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        private var INSTANCE: AccountDatabase? = null
        var lock = Any()
        fun getInstance(): AccountDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(MyApplication.instance.applicationContext, AccountDatabase::class.java, "Account.db")
                            .build()
                }
                return INSTANCE!!
            }
        }

    }
}

fun Context.accountDb(): AccountDatabase {
    return AccountDatabase.getInstance()
}