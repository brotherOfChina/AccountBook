package com.example.administrator.accountbook.bill


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.accountDb
import com.example.administrator.accountbook.db.entities.Account
import com.example.administrator.accountbook.pickerview.DateUtil
import com.example.administrator.accountbook.pickerview.TimeSelector
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

    var billBeans = mutableListOf<BillAccountBean>()
    private var startTime: Long = 0
    private var endTime: Long = 0
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("China"))
    /**
     * 为了计算每一个月的收入支出明细
     */
    private var expenditureAamount = 0.0;
    private var incomeAamount = 0.0;
    var monthAccounts = mutableListOf<Account>()

    /**
     * 用来计算每一天的收入支出
     */
    private var dateString: String = ""
    var income = 0.0
    var expenditure = 0.0
    /**
     * adapter
     */


    private fun initView() {
        iv_back.setOnClickListener {
            finish()
        }

        head_title.text = "账单"
        tab_bill.addTab(tab_bill.newTab().setText("明细"))
        tab_bill.addTab(tab_bill.newTab().setText("类别报表"))
        tab_bill.addTab(tab_bill.newTab().setText("账户"))
        loadDetails(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)

        date_year.setOnClickListener {
            val timeSelector = TimeSelector(this, TimeSelector.ResultHandler {
                date_year.text = it
                isRefresh = true
                loadDetails(it.substring(0, 4).toInt(), it.substring(it.length - 2, it.length).toInt())
            }, "2017-01", SimpleDateFormat("yyyy-MM", Locale("China")).format(Date()));
            timeSelector.show()
        }

    }
    var myAccounts:MutableList<Account> ?=null
    private var isRefresh = false;
    val cal = Calendar.getInstance()
    private fun loadDetails(year: Int, month: Int) {
        ViseLog.d("$year-$month")
        startTime = DateUtil.getSupportBeginDayofMonth(year, month).time
        endTime = DateUtil.getSupportEndDayofMonth(year, month).time
        ViseLog.d("开始时间：" + sdf.format(startTime) + ":" + startTime)
        ViseLog.d("结束时间：" + sdf.format(endTime) + ":" + endTime)
        async(UI) {
            val accounts = bg {

                accountDb().accountDao().getDateAccounts(startTime, endTime)
            }
            ViseLog.d(accounts.await())
            monthAccounts.clear()
            billBeans.clear()
            monthAccounts.addAll(accounts.await())
            if (accounts.await().isNotEmpty()){
                for ((i,account) in accounts.await().withIndex()) {

                    if (account.type == "1") {
                        incomeAamount += account.amount
                    } else if (account.type == "0") {
                        expenditureAamount += (-account.amount)
                    }
//                if (account.create_date.substring(0, account.create_date.length - 6)!=dateString){
//                    myAccounts.clear()
//                    billBeans.add(BillAccountBean(account.create_date.substring(0, account.create_date.length - 6), income, expenditure, myAccounts))
//                    dateString=account.create_date.substring(0, account.create_date.length - 6)
//                }else{
//                    if (account.type == "1") {
//                        income += account.amount
//                    } else if (account.type == "0") {
//                        expenditure += account.amount
//                    }
//                    myAccounts.add(account)
//                }
                    if (i==0){
                        dateString=account.create_date.substring(0, account.create_date.length - 6)
                        myAccounts= mutableListOf()
                    }
                    if (account.create_date.substring(0, account.create_date.length - 6) == dateString) {
                        if (account.type == "1") {
                            income += account.amount
                        } else if (account.type == "0") {
                            expenditure += account.amount
                        }
                        myAccounts?.add(account)
                    } else {
                        billBeans.add(BillAccountBean(dateString, income, expenditure, myAccounts!!))


                        income=0.0
                        expenditure=0.0
                        myAccounts= mutableListOf()
                        if (account.type == "1") {
                            income += account.amount
                        } else if (account.type == "0") {
                            expenditure += account.amount
                        }
                        myAccounts?.add(account)
                        dateString = account.create_date.substring(0, account.create_date.length - 6)

                    }

                }
            billBeans.add(BillAccountBean(dateString, income, expenditure, myAccounts!!))
            }


            ViseLog.d(billBeans)
            tv_income_amount.text = "收入：${incomeAamount}元"
            tv_expenditure_amount.text = "支出：${expenditureAamount}元"
            replaceFragment(0)
            tab_bill.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    replaceFragment(tab!!.position)
                }
            })
        }
    }

    var accountDetailsFragment: AccountDetailsFragment? = null
    var accountFragment: AccountFragment? = null
    var accountsReportsFragment: AccountsReportsFragment? = null
    private fun replaceFragment(i: Int) {
        val transaction = supportFragmentManager.beginTransaction()


        if (accountDetailsFragment == null) {
            accountDetailsFragment = AccountDetailsFragment.newInstance("", "")
            transaction.add(R.id.fl_bills, accountDetailsFragment)
        }else{
            accountDetailsFragment?.refresh()
        }
        if (accountFragment == null) {
            accountFragment = AccountFragment.newInstance("", "")
            transaction.add(R.id.fl_bills, accountFragment)

        }else{
            accountFragment?.refesh()
        }
        if (accountsReportsFragment == null) {
            accountsReportsFragment = AccountsReportsFragment.newInstance("", "")
            transaction.add(R.id.fl_bills, accountsReportsFragment)
        }else{
            accountsReportsFragment?.refresh()
        }


        when (i) {
            0 -> {
                transaction.hide(accountFragment)
                transaction.hide(accountsReportsFragment)
                transaction.show(accountDetailsFragment)
            }
            1 -> {
                transaction.hide(accountFragment)
                transaction.hide(accountDetailsFragment)
                transaction.show(accountsReportsFragment)
            }
            2 -> {
                transaction.hide(accountsReportsFragment)
                transaction.hide(accountDetailsFragment)
                transaction.show(accountFragment)

            }
        }
        transaction.commit()

    }


}

data class BillAccountBean(val date: String, val income: Double, val expenditure: Double, val accounts: MutableList<Account>)

class BillsAdapter(recyclerView: RecyclerView, layoutId: Int) : BGARecyclerViewAdapter<BillAccountBean>(recyclerView, layoutId) {
    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: BillAccountBean?) {
        helper?.setText(R.id.tv_date_day, model?.date)
        helper?.setText(R.id.item_amount, "收入：${model?.income} 支出：${model?.expenditure}")
        val rv = helper?.getView<RecyclerView>(R.id.rv_bill_adapter)
        rv?.layoutManager = LinearLayoutManager(helper?.convertView?.context)
        val accountAdapter = AccountsAdapter(R.layout.adapter_accounts, rv!!)
        rv.adapter = accountAdapter
        accountAdapter.data = model?.accounts

    }
}

class AccountsAdapter(layoutId: Int, recyclerView: RecyclerView) : BGARecyclerViewAdapter<Account>(recyclerView, layoutId) {
    override fun setItemChildListener(helper: BGAViewHolderHelper?, viewType: Int) {
        helper?.setItemChildClickListener(R.id.iv_delete)
        helper?.setItemChildClickListener(R.id.card_account)
    }

    override fun fillData(helper: BGAViewHolderHelper, position: Int, model: Account) {
        helper.setVisibility(R.id.iv_delete, View.INVISIBLE)
        helper.setText(R.id.tv_nick_name, model.nick_name)
        helper.setText(R.id.tv_item_date, model.create_date)
        helper.setText(R.id.tv_account_amount, model.amount.toString())
        helper.setText(R.id.tv_account_description, model.description)
        helper.setText(R.id.tv_account_type, when (model.type) {
            "0" -> {
                "支出"
            }
            "1" -> {
                "收入"
            }
            else -> {
                "理财"
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
            "1" -> {
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
            else -> {
                when (model.status) {
                    "0" -> {
                        "贷款"
                    }
                    "1" -> {
                        "水电气"
                    }
                    "2" -> {
                        "房租"
                    }
                    "3" -> {
                        "医疗"
                    }
                    "4" -> {
                        "教育"
                    }
                    "5" -> {
                        "餐饮"
                    }
                    "6" -> {
                        "人情"
                    }
                    "7" -> {
                        "意外"
                    }

                    else -> {
                        "非必要"
                    }

                }
            }
        })


    }
}