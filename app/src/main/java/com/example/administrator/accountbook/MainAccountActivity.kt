package com.example.administrator.accountbook

import android.app.ProgressDialog.show
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener
import com.example.administrator.accountbook.R.id.rv_account
import com.example.administrator.accountbook.account.AccountsAdapter
import com.example.administrator.accountbook.account.AddAccountActivity
import com.example.administrator.accountbook.base.MyApplication
import com.example.administrator.accountbook.db.database.accountDb
import com.example.administrator.accountbook.db.database.userDb
import com.example.administrator.accountbook.db.entities.Account
import com.example.administrator.accountbook.extensions.DelegatesExt
import com.example.administrator.accountbook.extensions.isLogin
import com.example.administrator.accountbook.extensions.setLogin
import com.example.administrator.accountbook.user.LoginActivity
import com.example.administrator.accountbook.user.SignUpActivity
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_main_account.*
import kotlinx.android.synthetic.main.app_bar_main_account.*
import kotlinx.android.synthetic.main.content_main_account.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainAccountActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, BGAOnItemChildClickListener {
    override fun onItemChildClick(parent: ViewGroup?, childView: View?, position: Int) {
        if (childView?.id == R.id.iv_delete) {

            async(UI) {
                bg {
                    accountDb().accountDao().deleteById(accountsAdapter.data[position].id)
                }
                accountsAdapter.removeItem(position)
                toast("删除成功")
            }

        } else if (childView?.id == R.id.card_account) {
            startActivity<AddAccountActivity>("id" to accountsAdapter.data[position].id)
        }
    }

    var nickname: String by DelegatesExt.preference(MyApplication.instance, "nickname", "")
    var tvName: TextView? = null
    var tvPhone: TextView? = null
    private var accounts= mutableListOf<Account>()
    private var selectedPosition=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_account)
        setSupportActionBar(toolbar)
        rv_account.layoutManager = LinearLayoutManager(this)
        rv_account.adapter = accountsAdapter
        accountsAdapter.data = accounts
        accountsAdapter.setOnItemChildClickListener(this)
        fab.setOnClickListener { view ->
            if (isLogin()) {
                startActivity<AddAccountActivity>("id" to "0")
            } else {
                Snackbar.make(fab, "记笔记请先登录", Snackbar.LENGTH_LONG).setAction("去登录") {
                    startActivity<LoginActivity>()
                }.show()
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        val headView = nav_view.getHeaderView(0)
        tvName = headView.findViewById<TextView>(R.id.name)
        tvPhone = headView.findViewById<TextView>(R.id.tv_phone)

        tvPhone?.setOnClickListener {
            startActivity<LoginActivity>()
        }
        tab_main.addTab(tab_main.newTab().setText("全部"))
        tab_main.addTab(tab_main.newTab().setText("收入"))
        tab_main.addTab(tab_main.newTab().setText("支出"))
        tab_main.addTab(tab_main.newTab().setText("理财"))
        tab_main.getTabAt(0)?.select()
        loadAllAccounts(0)
        tab_main.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

                loadAllAccounts(tab!!.position)
            }
        })

    }

    private fun loadAllAccounts(position: Int) {
        ViseLog.d(position)
        selectedPosition=position
        async(UI) {
            val accounts = bg {
                when (position) {
                    1 -> {
                        accountDb().accountDao().getPayAccounts("1")
                    }
                    2 -> {
                        accountDb().accountDao().getPayAccounts("0")
                    }
                    3 -> {
                        accountDb().accountDao().getPayAccounts("2")
                    }
                    else -> {
                        accountDb().accountDao().getAllAccounts()
                    }
                }

            }
            ViseLog.d(accounts.await())
            showAccounts(accounts.await())
        }
    }

    val accountsAdapter: AccountsAdapter by lazy {
        AccountsAdapter(R.layout.adapter_accounts, rv_account)
    }

    private fun showAccounts(accounts: List<Account>) {

        accountsAdapter.data=accounts

    }

    override fun onResume() {
        super.onResume()
        tab_main.getTabAt(selectedPosition)?.select()

        if (isLogin()) {
            tvName?.text = nickname
            tvPhone?.visibility = View.INVISIBLE


        } else {
            tvName?.text = "未登录"
            tvPhone?.text = "去登录"
            tvPhone?.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_account, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

        /**
         * 添加账户
         */
            R.id.nav_add -> {
                toSignUp("3")

            }
        /**
         * 删除账户
         */
            R.id.nav_delete -> {
                toSignUp("5")
            }
        /**
         * 退出登录
         */
            R.id.nav_out -> {
                setLogin(false)
            }
        /**
         * 重置密码
         */
            R.id.nav_password -> {
                toSignUp("4")
            }
        /**
         * 注销账户
         */
            R.id.nav_cancel -> {
                toSignUp("1")
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * @param type 0注册；1注销；2退出；3添加；4重置  5   删除
     */
    private fun toSignUp(type: String) {
        startActivity<SignUpActivity>("type" to type)
    }
}

