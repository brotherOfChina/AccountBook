package com.example.administrator.accountbook.financial

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.getFinancialDao
import com.example.administrator.accountbook.db.entities.Financial
import com.example.administrator.accountbook.extensions.isLogin
import com.example.administrator.accountbook.supercalendar.SyllabusActivity
import com.example.administrator.accountbook.user.LoginActivity
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_financial.*
import kotlinx.android.synthetic.main.back_head.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class FinancialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial)
        initView()
    }

    private val financialAdapter: FinancialsAdapter by lazy {
        FinancialsAdapter(rv_financial, R.layout.adapter_financial_item)
    }

    override fun onResume() {
        super.onResume()
        async(UI) {
            val financials = bg {
                getFinancialDao().loadAllFinancials()

            }
            ViseLog.d(financials.await())
            financialAdapter.data = financials.await()

        }
    }

    private fun initView() {
        iv_calder.setOnClickListener {
            startActivity<SyllabusActivity>()
        }
        iv_back.setOnClickListener {
            finish()
        }
        head_title.text = "理财"
        fab.setOnClickListener {
            if (isLogin()) {
                startActivity<AddFinancialManagement>("id" to "0")
            } else {
                Snackbar.make(fab, "记笔记请先登录", Snackbar.LENGTH_LONG).setAction("去登录") {
                    startActivity<LoginActivity>()
                }.show()
            }
        }
        rv_financial.layoutManager = LinearLayoutManager(this)
        rv_financial.adapter = financialAdapter

    }
}

class FinancialsAdapter(recyclerView: RecyclerView, layoutId: Int) : BGARecyclerViewAdapter<Financial>(recyclerView, layoutId) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("China"))
    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: Financial?) {
        helper?.setText(R.id.tv_income_date, "收益日期：" + (model?.income_date))
        helper?.setText(R.id.tv_input_date, "投入日期：" + (model?.input_date))
        helper?.setText(R.id.tv_income_rate, "收益利率：" + model?.rate)
        helper?.setText(R.id.tv_input_amount, "投入金额：" + model?.input_amount)
        helper?.setText(R.id.tv_maturity_income, "到期收益：" + model?.income_amount)
        helper?.setText(R.id.tv_financial_name, "投资人：" + model?.user_name)
        helper?.setText(R.id.financial_type, "投资类型：" + when (model?.financial_type) {
            "0" -> {
                "债券"
            }
            "1" -> {
                "保险"
            }
            "2" -> {
                "股票"
            }
            else -> {
                "其它"
            }
        })


    }

}
