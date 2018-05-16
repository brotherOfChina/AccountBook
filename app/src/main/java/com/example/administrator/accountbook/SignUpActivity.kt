package com.example.administrator.accountbook

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.administrator.accountbook.base.MyApplication
import com.example.administrator.accountbook.db.database.UserDatabase
import com.example.administrator.accountbook.db.entities.User
import com.example.administrator.accountbook.extensions.setLogin
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.toast

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val type = intent.getStringExtra("type")
        /**
         * @param type 0注册；1注销；2退出；3添加；4重置  5   删除
         */
        when (type) {
            "0" -> {
                head.text = "注册"
                btn_sign_up.setOnClickListener {
                    if (et_phone.text!=null&&et_nick_name.text!=null&&et_password.text!=null){
                        signUp()

                    }
                }
            }
            "1" -> {
                head.text = "注销"
                btn_sign_up.text = "确认注销"
                et_nick_name.visibility = View.INVISIBLE
                et_password.visibility = View.INVISIBLE
                et_phone.hint = "输入要注销的账户手机号"
                btn_sign_up.setOnClickListener {
                    if (et_phone.text!=null){
                        cancleUser()
                    }
                }
            }
            "2" -> {
//                head.text = "注销"
            }
            "3" -> {
                head.text = "添加"
                btn_sign_up.text = "添加用户"
                et_password.visibility = View.INVISIBLE
                btn_sign_up.setOnClickListener {
                    if(et_nick_name.text!=null&&et_phone!=null){
                        addUser()
                    }
                }

            }
            "4" -> {
                head.text = "密码重置"
                et_password.hint = "输入新密码"
                et_nick_name.visibility = View.INVISIBLE
                btn_sign_up.text = "确认重置密码"
                btn_sign_up.setOnClickListener {
                    if (et_phone.text!=null&&et_password!=null){
                        resetPassword()
                    }
                }
            }
            "5" -> {
                head.text = "删除账户"
                btn_sign_up.text = "确认删除"
                et_phone.visibility = View.INVISIBLE
                et_password.visibility = View.INVISIBLE
                et_nick_name.hint = "输入要删除的账户昵称"
                btn_sign_up.setOnClickListener {
                    if (et_nick_name.text!=null){
                        deleteUser()

                    }
                }
            }
        }

    }

    private fun addUser() {
        async(UI) {
            val users = bg {
                val db = UserDatabase.getInstance(MyApplication.instance).userDao()
                db.addUser(User(et_phone.text.toString(), "", et_nick_name.text.toString()))
                db.getUsers()
            }
            ViseLog.d(users.await())
            toast("添加账户成功")
            finish()
        }
    }

    private fun deleteUser() {
        async(UI) {
            val users = bg {
                val db = UserDatabase.getInstance(MyApplication.instance).userDao()
                val user = db.getUserByNick(et_nick_name.text.toString())
                db.deleteUid(user.uid)
                db.getUsers()
            }
            ViseLog.d(users.await())
            toast("删除账户成功")
            finish()
        }
    }

    private fun cancleUser() {
        async(UI) {
            val users = bg {
                val db = UserDatabase.getInstance(MyApplication.instance).userDao()
                val user = db.getUser(et_phone.text.toString())
                db.deleteUid(user.uid)
                db.getUsers()
            }
            ViseLog.d(users.await())
            toast("注销账户成功")
            setLogin(false)
            finish()
        }
    }

    private fun resetPassword() {
        async(UI) {
            val users = bg {
                val db = UserDatabase.getInstance(MyApplication.instance)
                val user = db.userDao().getUser(et_phone.text.toString())
                db.userDao().addUser(User(user.phone, et_password.text.toString(), user.nick_name, user.uid))
            }
            ViseLog.d(users.await())
            toast("密码重置成功")
            finish()
        }
    }

    private fun signUp() {
        async(UI) {
            val users = bg {
                val db = UserDatabase.getInstance(MyApplication.instance).userDao()
                db.addUser(User(et_phone.text.toString(), et_password.text.toString(), et_nick_name.text.toString()))
                db.getUsers()
            }
            ViseLog.d(users.await())
            toast("注册成功")
            finish()
        }


    }
}
