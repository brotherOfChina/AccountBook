package com.example.administrator.accountbook

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.example.administrator.accountbook.account.AddAccountActivity
import com.example.administrator.accountbook.base.MyApplication
import com.example.administrator.accountbook.extensions.DelegatesExt
import com.example.administrator.accountbook.extensions.isLogin
import com.example.administrator.accountbook.extensions.setLogin
import com.example.administrator.accountbook.user.LoginActivity
import com.example.administrator.accountbook.user.SignUpActivity
import kotlinx.android.synthetic.main.activity_main_account.*
import kotlinx.android.synthetic.main.app_bar_main_account.*
import kotlinx.android.synthetic.main.content_main_account.*
import org.jetbrains.anko.startActivity

class MainAccountActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var nickname: String by DelegatesExt.preference(MyApplication.instance, "nickname", "")
    var tvName: TextView? = null
    var tvPhone: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_account)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->

                startActivity<AddAccountActivity>()
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
        tab_main.addTab(tab_main.newTab().setText("结余"))
    }

    override fun onResume() {
        super.onResume()
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
