package com.example.administrator.accountbook.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by {zpj}
 *  on 2018/5/18 0018.
 */
@Entity
data class Financial @JvmOverloads constructor(
        @ColumnInfo(name = "input_date") var input_date: String? = null,
        @ColumnInfo(name = "input_date_l") var input_datel: Long? = null,
        @ColumnInfo(name = "income_date") var income_date: String? = null,
        @ColumnInfo(name = "income_datel") var income_datel: Long? = null,
        @ColumnInfo(name = "rate") var rate: Double = 0.0,
        @ColumnInfo(name = "input_amount") var input_amount: Double = 0.0,
        @ColumnInfo(name = "income_amount") var income_amount: Double = 0.0,
        @ColumnInfo(name = "uid") var uid: String = "",
        @ColumnInfo(name = "user_name") var user_name: String = "",
        @ColumnInfo(name = "financial_type") var financial_type: String = "",
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString() //账单id
)