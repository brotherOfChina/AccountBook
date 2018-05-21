package com.example.administrator.accountbook.financial

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.getFinancialDao
import com.example.administrator.accountbook.db.database.userDb
import com.example.administrator.accountbook.db.entities.Financial
import com.example.administrator.accountbook.db.entities.User
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_financial_management.*
import kotlinx.android.synthetic.main.back_head.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.toast
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class AddFinancialManagement : AppCompatActivity() {
    private var userAdapter: ArrayAdapter<CharSequence>? = null
    private var uid = "1"   //用户id
    private var nickname = "1"   //用户id
    private var users = mutableListOf<User>()
    private var id = ""
    val calendar = Calendar.getInstance()
    val sdf=SimpleDateFormat("yyyy-MM-dd", Locale("China"))
    private var input_date: String = sdf.format(Date())//投入日期
    private var revenue_date: String = sdf.format(Date())//收入日期
    private var input_datel:Long=System.currentTimeMillis()
    private var revenue_datel:Long=System.currentTimeMillis()
    private var investment_amount: Double = 0.0
    private var investment_rate: Double = 0.0
    private var income_amount: Double = 0.0
    private var dateDiff = 0
    private var financial_type = "0";
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
        date_of_input.text = "投入日期：${sdf.format(Date())}"
        date_of_revenue.text = "收入日期：${sdf.format(Date())}"
        dateDiff = 0
        val adapter = ArrayAdapter.createFromResource(this, R.array.financial_type, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_layout_item)
        spinner_financial.adapter = adapter
        spinner_financial.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                financial_type = position.toString()
            }


        }
        et_investment_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.toString() != "") {
                    investment_amount = s.toString().toDouble()
                    income_amount = (investment_rate) * (investment_amount) * dateDiff
                    income_amount=df.format(income_amount).toDouble()
                    tv_forecast_income.text = income_amount.toString()
                }
            }
        })
        et_yield_interest_rate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.toString() != "") {
                    investment_rate = s.toString().toDouble()
                    ViseLog.d(investment_rate)
                    income_amount = (investment_rate) * (investment_amount) * dateDiff
                    income_amount=df.format(income_amount).toDouble()
                    tv_forecast_income.text = income_amount.toString()
                }
            }
        })
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
            DatePickerDialog(this@AddFinancialManagement, R.style.Theme_AppCompat_Light_Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // 绑定监听器(How the parent is notified that the date is set.)
                // 此处得到选择的时间，可以进行你想要的操作
                date_of_input.text = "投入日期：$year-${monthOfYear + 1}-$dayOfMonth"

                input_date = "$year-${monthOfYear + 1}-$dayOfMonth"
                input_datel=sdf.parse(input_date).time
                dateDiff = ((revenue_datel - input_datel) / 1000 / 60 / 60 / 24).toInt()
                income_amount=df.format(income_amount).toDouble()

                tv_forecast_income.text = income_amount.toString()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))// 设置初始日期
                    .show()
        }
        date_of_revenue.setOnClickListener {
            DatePickerDialog(this@AddFinancialManagement, R.style.Theme_AppCompat_Light_Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // 绑定监听器(How the parent is notified that the date is set.)
                // 此处得到选择的时间，可以进行你想要的操作
                date_of_revenue.text = "收益日期：$year-${monthOfYear + 1}-$dayOfMonth"
                revenue_date ="$year-${monthOfYear + 1}-$dayOfMonth"
                revenue_datel=sdf.parse(revenue_date).time
                dateDiff = ((revenue_datel - input_datel) / 1000 / 60 / 60 / 24).toInt()
                income_amount = (investment_rate) * (investment_amount) * dateDiff
                income_amount=df.format(income_amount).toDouble()

                tv_forecast_income.text = income_amount.toString()
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


            val financial = Financial(input_date,input_datel, revenue_date, revenue_datel,investment_rate, investment_amount, income_amount, uid, user_name = nickname, financial_type = financial_type)
            ViseLog.d(            Date(financial.income_datel!!)
            )
            async(UI) {
                val s = bg {
                    getFinancialDao().addFinancial(financial)
                    getFinancialDao().loadAllFinancials()
                }
                ViseLog.d(s.await())
                if (s.await().isNotEmpty()) {
                    toast("添加成功")
                    finish()
                } else {
                    toast("添加失败")
                }
            }
        }


    }
    val df=DecimalFormat("#.00")
    private fun showUserSpinner(users: MutableList<User>) {
        val userNames = mutableListOf<String>()
        for (user in users) {
            userNames.add(user.nick_name)
        }
        ViseLog.d(userNames)
        userAdapter = ArrayAdapter(this@AddFinancialManagement, android.R.layout.simple_spinner_item, userNames as List<CharSequence>?)
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
