package com.example.administrator.accountbook.account

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.accountDb
import com.example.administrator.accountbook.db.database.userDb
import com.example.administrator.accountbook.db.entities.Account
import com.example.administrator.accountbook.db.entities.User
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_add_account.*
import kotlinx.android.synthetic.main.adapter_accounts.*
import kotlinx.android.synthetic.main.back_head.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class AddAccountActivity : AppCompatActivity() {
    private var type = "1"   //0 支出 ，1  收入 2 预算
    private var status = "1"   //0通讯，餐饮，旅游，购物，教育，其他    1 工资，理财，其它
    private var uid = "1"   //用户id
    private var nickname = "1"   //用户id
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("china"))
    private var statusAdapter: ArrayAdapter<CharSequence>? = null
    private var userAdapter: ArrayAdapter<CharSequence>? = null
    private var users = mutableListOf<User>()
    private var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)
        iv_back.setOnClickListener {
            finish()
        }
        head_title.text = "记账"
        /**
         * 获取用户列表
         */
        async(UI) {
            val allUsers = bg {
                userDb().userDao().getUsers()
            }
            users.addAll(allUsers.await())
            try {
                showUserSpinner(users)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        btn_sure_create.setOnClickListener {
            createAccount()
        }
        id = intent.getStringExtra("id")

        val adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_layout_item)
        statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        statusAdapter?.setDropDownViewResource(R.layout.spinner_layout_item)
        status_spinner.adapter = statusAdapter
        type_spinner.adapter = adapter
        type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> {
                        type = p2.toString()
                        statusAdapter?.clear()

                        statusAdapter?.addAll("通讯",
                                "餐饮",
                                "旅游",
                                "购物",
                                "教育",
                                "其它")
                        statusAdapter?.notifyDataSetChanged()

                    }
                    1 -> {
                        statusAdapter?.clear()

                        type = p2.toString()
                        statusAdapter?.addAll("工资",
                                "理财",
                                "其它"
                                )
                        statusAdapter?.notifyDataSetChanged()
                    }
                    2 -> {
                        statusAdapter?.clear()

                        type = p2.toString()
                        statusAdapter?.addAll("贷款" ,
                                "水电气" ,
                                "房租" ,
                                "医疗" ,
                                "教育" ,
                                "餐饮"
                        )
                        statusAdapter?.notifyDataSetChanged()
                    }

                }

            }

        }
        status_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                status = p2.toString()
            }

        }

    }

    private fun createAccount() {
        var amount = 0.0;
        var description = ""
        val date = sdf.format(Date())
        if (et_amount.text?.toString() != null && et_amount.text?.toString() != "") {
            when (type) {
                "0" -> {
                    amount = -(et_amount.text.toString().toDouble())
                }
                "1", "2" -> {
                    amount = +(et_amount.text.toString().toDouble())
                }
            }
        } else {
            toast("请输入金额")
        }
        if (et_amount.text?.toString() != null) {
            description = et_description.text.toString()
        }
        async(UI) {
            val accounts = bg {
                val accountDao = accountDb().accountDao()
                val account = Account(date, "", type, uid, nickname, status, amount, description)
                ViseLog.d(account)

                accountDao.addAccount(account)
                ViseLog.d(accountDao.getAllAccounts())

            }
            toast("记账成功")
            finish()
        }
    }

    private fun showUserSpinner(users: MutableList<User>) {
        val userNames = mutableListOf<String>()
        for (user in users) {
            userNames.add(user.nick_name)
        }
        ViseLog.d(userNames)
        userAdapter = ArrayAdapter(this@AddAccountActivity, android.R.layout.simple_spinner_item, userNames as List<CharSequence>?)
        userAdapter?.setDropDownViewResource(R.layout.spinner_layout_item);
        user_spinner.adapter = userAdapter
        user_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                uid = users[p2].uid
                nickname = users[p2].nick_name
            }

        }
        async(UI) {
            val accountA = bg {
                accountDb().accountDao().getAccountById(id)
            }

            selectTab(accountA.await())
        }


    }

    private fun selectTab(account: Account) {
        var userPosition = 0

        ViseLog.d(account)
        type_spinner.setSelection(account.type.toInt())

        for ((i, user) in users.withIndex()) {
            if (account.user_id == user.uid) {
                userPosition = i
            }
        }
        when (type) {
            "0" -> {
                statusAdapter?.clear()
                statusAdapter?.addAll("通讯",
                        "餐饮",
                        "旅游",
                        "购物",
                        "教育",
                        "其它")
                statusAdapter?.notifyDataSetChanged()

            }
            "1" -> {
                statusAdapter?.clear()

                statusAdapter?.addAll("工资",
                        "理财",
                        "其它"
                )
                statusAdapter?.notifyDataSetChanged()
            }
            "2"-> {
                statusAdapter?.clear()

                statusAdapter?.addAll("贷款" ,
                        "水电气" ,
                        "房租" ,
                        "医疗" ,
                        "教育" ,
                        "餐饮"

                )
                statusAdapter?.notifyDataSetChanged()
            }

        }
        status_spinner.setSelection(account.status.toInt())

        user_spinner.setSelection(userPosition)
        if (account.amount < 0) {
            et_amount.setText((-account.amount).toString())
        } else {
            et_amount.setText(account.amount.toString())
        }
        et_description.setText(account.description)

    }
}
