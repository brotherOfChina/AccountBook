package com.example.administrator.accountbook

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.administrator.accountbook.db.database.UserDatabase
import com.example.administrator.accountbook.db.entities.User
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        btn_sign_up.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val db = UserDatabase.getInstance(this).userDao()
        db.addUser(User("18404975605", "5178019", "赵鹏军"))
        val users = db.getUsers()
        ViseLog.d(users)

    }
}
