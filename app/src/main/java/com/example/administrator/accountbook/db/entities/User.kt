package com.example.administrator.accountbook.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by {zpj}
 *  on 2018/5/14 0014.
 */
@Entity
data class User @JvmOverloads constructor(
        @ColumnInfo(name = "phone") var phone: String = "",
        @ColumnInfo(name = "password") var password: String = "",
        @ColumnInfo(name = "nick_name") var nick_name: String = "",
        @PrimaryKey @ColumnInfo(name = "uid") var uid: String = UUID.randomUUID().toString()


)