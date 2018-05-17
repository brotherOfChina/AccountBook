package com.example.administrator.accountbook.account

import android.support.v7.widget.RecyclerView
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.entities.Account

/**
 * Created by {zpj}
 *  on 2018/5/17 0017.
 */
class AccountsAdapter(layoutId: Int, recyclerView: RecyclerView) : BGARecyclerViewAdapter<Account>(recyclerView, layoutId) {
    override fun fillData(helper: BGAViewHolderHelper, position: Int, model: Account) {
        helper.setText(R.id.tv_nick_name, model.nick_name)
        helper.setText(R.id.tv_item_date, model.create_date)
        helper.setText(R.id.tv_account_amount, model.amount.toString())
        helper.setText(R.id.tv_account_type, when (model.type) {
            "0" -> {
                "支出"
            }
            else -> {
                "收入"
            }
        })
        helper.setText(R.id.tv_account_status, when (model.type) {
            "0" -> {
                when (model.status) {
                    "0" -> {
                        "通讯"
                    }
                    "1" -> {
                        "餐饮"
                    }
                    "2" -> {
                        "旅游"
                    }
                    "3" -> {
                        "购物"
                    }
                    "4" -> {
                        "教育"
                    }
                    else -> {
                        "其他"
                    }
                }

            }
            else -> {
                when (model.status) {
                    "0" -> {
                        "工资"
                    }
                    "1" -> {
                        "理财"
                    }
                    else -> {
                        "其它"
                    }

                }
            }
        })


    }
}