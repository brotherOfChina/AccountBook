package com.example.administrator.accountbook.financial

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.getFinancialDao
import com.example.administrator.accountbook.db.database.userDb
import com.example.administrator.accountbook.db.entities.User
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_financial_management.*
import kotlinx.android.synthetic.main.back_head.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import com.example.administrator.accountbook.db.entities.Financial
import org.jetbrains.anko.toast
import java.util.*


class FinancialManagement : AppCompatActivity() {
    private var userAdapter: ArrayAdapter<CharSequence>? = null
    private var uid = "1"   //用户id
    private var nickname = "1"   //用户id
    private var users = mutableListOf<User>()
    private var id = ""
    private var input_date: java.sql.Date? = null//投入日期
    private var revenue_date: java.sql.Date? = null//收入日期
    private var investment_amount: Double = 0.0
    private var investment_rate: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_management)
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        head_title.text = "理财"
        iv_back.setOnClickListener {
            finish()
        }
        val adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_layout_item)

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
        btn_sure.setOnClickListener {
            async(UI) {

            }
        }
        date_of_input.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this@FinancialManagement, R.style.Theme_AppCompat_Light_Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // 绑定监听器(How the parent is notified that the date is set.)
                // 此处得到选择的时间，可以进行你想要的操作
                date_of_input.text = "投入日期：$year-$monthOfYear-$dayOfMonth"
                input_date = java.sql.Date(year, monthOfYear, dayOfMonth)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))// 设置初始日期
                    .show()
        }
        date_of_revenue.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this@FinancialManagement, R.style.Theme_AppCompat_Light_Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // 绑定监听器(How the parent is notified that the date is set.)
                // 此处得到选择的时间，可以进行你想要的操作
                date_of_revenue.text = "收益日期：$year-$monthOfYear-$dayOfMonth"
                revenue_date = java.sql.Date(year, monthOfYear, dayOfMonth)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))// 设置初始日期
                    .show()
        }
        btn_sure.setOnClickListener {
            if (et_investment_amount.text == null || et_investment_amount.text.toString() == "") {
                toast("输入投资金额")
                return@setOnClickListener
            } else {
                investment_amount = et_investment_amount.text.toString().toDouble()
            }
            if (et_yield_interest_rate.text == null || et_yield_interest_rate.text.toString() == "") {
                toast("输入收益率")
                return@setOnClickListener
            } else {
                investment_rate = et_yield_interest_rate.text.toString().toDouble()
            }
            val financial = Financial(input_date, revenue_date, investment_rate, investment_amount, 0.0, uid, user_name = nickname)
            ViseLog.d(financial)
            async(UI) {
               bg{
                    getFinancialDao().addFinancial(financial)
                }
                ViseLog.d(getFinancialDao().loadAllFinancials())
            }
        }

    }

    private fun showUserSpinner(users: MutableList<User>) {
        val userNames = mutableListOf<String>()
        for (user in users) {
            userNames.add(user.nick_name)
        }
        ViseLog.d(userNames)
        userAdapter = ArrayAdapter(this@FinancialManagement, android.R.layout.simple_spinner_item, userNames as List<CharSequence>?)
        userAdapter?.setDropDownViewResource(R.layout.spinner_layout_item);
        spinner_user.adapter = userAdapter
        spinner_user.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                uid = users[p2].uid
                nickname = users[p2].nick_name
            }

        }

    }
}
