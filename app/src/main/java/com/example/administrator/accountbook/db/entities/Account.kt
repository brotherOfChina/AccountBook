package com.example.administrator.accountbook.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by {zpj}
 *  on 2018/5/16 0016.
 */
@Entity(tableName = "Account")
data class Account @JvmOverloads constructor(
        @ColumnInfo(name = "date") var date: Long = 0,//创建时间

        @ColumnInfo(name = "create_date") var create_date: String = "",//创建时间
        @ColumnInfo(name = "edit_date") var edit_date: String = "",//修改时间
        @ColumnInfo(name = "type") var type: String = "",//1 支出  2收入
        @ColumnInfo(name = "user_id") var user_id: String = "",//对应userid
        @ColumnInfo(name = "nick_name") var nick_name: String = "",//对应userid

        //type==1   1  通讯，2餐饮，3旅游，4购物，5教育，6其他   type==2  1 工资，2 理财，3 其它
        @ColumnInfo(name = "status") var status: String = "",
        @ColumnInfo(name = "amount") var amount: Double = 0.0,//金额
        @ColumnInfo(name = "description") var description: String = "",//消费介绍
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString() //账单id

)

