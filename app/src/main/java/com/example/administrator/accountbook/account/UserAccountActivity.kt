package com.example.administrator.accountbook.account

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.bill.AccountsAdapter
import com.example.administrator.accountbook.db.database.accountDb
import kotlinx.android.synthetic.main.activity_user_account.*
import kotlinx.android.synthetic.main.back_head.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class UserAccountActivity : AppCompatActivity() {
    private val accountsAdapter: AccountsAdapter by
    lazy {
        com.example.administrator.accountbook.bill.AccountsAdapter(R.layout.adapter_accounts, rv_user_accounts)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account)
        iv_back.setOnClickListener {
            finish()
        }
        head_title.text = "用户帐单"
        rv_user_accounts.layoutManager = LinearLayoutManager(this)
        rv_user_accounts.adapter = accountsAdapter
        val uid = intent.getStringExtra("uid")
        async(UI) {
            val accounts = bg {
                accountDb().accountDao().getUserAccounts(uid)
            }
            accountsAdapter.data = accounts.await()
        }
    }
}
