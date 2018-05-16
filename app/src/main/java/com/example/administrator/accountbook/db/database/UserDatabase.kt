package com.example.administrator.accountbook.db.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.administrator.accountbook.base.MyApplication
import com.example.administrator.accountbook.db.dao.UserDao
import com.example.administrator.accountbook.db.entities.User

/**
 * Created by {zpj}
 *  on 2018/5/14 0014.
 */
@Database(entities = [(User::class)], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: UserDatabase? = null

        private val lock = Any()

        fun getInstance(): UserDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(MyApplication.instance.applicationContext, UserDatabase::class.java, "User.db")
                            .build()

                }
                return INSTANCE!!
            }
        }
    }
}
fun Context.userDb(): UserDatabase {
    return UserDatabase.getInstance()
}