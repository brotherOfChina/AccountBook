package com.example.administrator.accountbook.account

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.userDb
import com.example.administrator.accountbook.db.entities.User
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_add_account.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.spinner

class AddAccountActivity : AppCompatActivity() {
    private var type = "1"   //0 支出，1  收入 2 预算
    private var status = "1"   //
    private var uid = "1"   //用户id

    private var statusAdapter: ArrayAdapter<CharSequence>? = null
    private var userAdapter: ArrayAdapter<CharSequence>? = null
    private var users = mutableListOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)
        /**
         * 获取用户列表
         */
        async(UI) {
            val allUsers = bg {
                userDb().userDao().getUsers()
            }
            users.addAll(allUsers.await())
            showUserSpinner(users)
        }
        val adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item)
        statusAdapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item)
        statusAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        type_spinner.adapter = adapter
        type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> {
                        type = "0"
                        statusAdapter = ArrayAdapter.createFromResource(this@AddAccountActivity, R.array.expenditure_status, android.R.layout.simple_spinner_item)
                        status_spinner.adapter = statusAdapter
                    }
                    1 -> {
                        type = "1"
                        statusAdapter = ArrayAdapter.createFromResource(this@AddAccountActivity, R.array.income_status, android.R.layout.simple_spinner_item)
                        status_spinner.adapter = statusAdapter
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

    private fun showUserSpinner(users: MutableList<User>) {
        val userNames = mutableListOf<String>()
        for (user in users) {
            userNames.add(user.nick_name)
        }
        ViseLog.d(userNames)
        userAdapter = ArrayAdapter(this@AddAccountActivity, android.R.layout.simple_spinner_item, userNames as List<CharSequence>?)
        userAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_spinner.adapter = userAdapter
        user_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                uid = users[p2].uid
            }

        }

    }
}
