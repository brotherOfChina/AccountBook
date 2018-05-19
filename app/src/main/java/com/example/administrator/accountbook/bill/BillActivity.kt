package com.example.administrator.accountbook.bill

import android.accounts.Account
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.accountDb
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_bill.*
import kotlinx.android.synthetic.main.back_head.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.text.SimpleDateFormat
import java.util.*


class BillActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        initView()
    }

    private var billBeans = mutableListOf<BillAccountBean>()
    private var startTime: Long = 0
    private var endTime: Long = 0
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("China"))
    private var expenditureAamount = 0.0;
    private var incomeAamount = 0.0;
    private var dateString: String = ""
    private fun initView() {
        iv_back.setOnClickListener {
            finish()
        }
        head_title.text = "账单"
        tab_bill.addTab(tab_bill.newTab().setText("明细"))
        tab_bill.addTab(tab_bill.newTab().setText("类别报表"))
        tab_bill.addTab(tab_bill.newTab().setText("账户"))
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        startTime = cal.time.time
        ViseLog.d("开始时间：" + sdf.format(cal.time) + ":" + startTime)
        cal.roll(Calendar.DAY_OF_MONTH, -1)
        endTime = cal.time.time

        ViseLog.d("结束时间：" + sdf.format(cal.time) + ":" + endTime)
        async(UI) {
            val accounts = bg {

                accountDb().accountDao().getDateAccounts(startTime, endTime)
            }
            ViseLog.d(accounts.await())
            for (account in accounts.await()) {
                if (account.type == "1") {
                    incomeAamount += account.amount
                } else {
                    expenditureAamount += account.amount
                }
                val accounts= mutableListOf<Account>()
                if (account.create_date.substring(0, account.create_date.length - 6)==dateString){
                }else{
                    dateString = account.create_date.substring(0, account.create_date.length - 6)

                }

            }
            tv_income_amount.text = "收入：${incomeAamount}元"
            tv_expenditure_amount.text = "支出：${expenditureAamount}元"

        }

    }

}

data class BillAccountBean(val date: String, val income: Double, val expenditure: Double, val accounts: List<Account>)
