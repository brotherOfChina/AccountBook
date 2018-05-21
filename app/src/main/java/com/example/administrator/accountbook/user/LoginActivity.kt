package com.example.administrator.accountbook.user

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.base.MyApplication
import com.example.administrator.accountbook.db.database.UserDatabase
import com.example.administrator.accountbook.db.entities.User
import com.example.administrator.accountbook.extensions.DelegatesExt
import com.example.administrator.accountbook.extensions.setLogin
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {
    var nickname: String by DelegatesExt.preference(MyApplication.instance, "nickname", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tv_to_sign_up.setOnClickListener {
            startActivity<SignUpActivity>("type" to "0")
        }
        btn_login.setOnClickListener {
            if (et_phone.text == null || et_phone.text.toString().length != 11) {
                toast("请输入11位手机号")
                return@setOnClickListener
            }
            if (et_password.text == null || et_password.text.toString().length < 6) {
                toast("请输入6位以上密码")
                return@setOnClickListener
            }


            async(UI) {
                val user = bg {
                    val db = UserDatabase.getInstance()
                    db.userDao().getUser(et_phone.text.toString())
                }
                login(user.await())

            }

        }
    }

    private fun login(user: User) {
        if (user.password == et_password.text.toString()) {
            toast("登录成功")
            setLogin(true)
            nickname = user.nick_name
            finish()
        }else{
            toast("登录失败")
        }

    }

}
