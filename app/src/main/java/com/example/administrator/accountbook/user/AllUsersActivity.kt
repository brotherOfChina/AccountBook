package com.example.administrator.accountbook.user

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.UserDatabase
import com.example.administrator.accountbook.db.entities.User
import kotlinx.android.synthetic.main.activity_all_users.*
import kotlinx.android.synthetic.main.back_head.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.toast

class AllUsersActivity : AppCompatActivity(), BGAOnItemChildClickListener {
    override fun onItemChildClick(parent: ViewGroup?, childView: View?, position: Int) {
        if (childView?.id == R.id.iv_delete_user) {
            deleteUser(usersAdapter.data[position].uid)
            usersAdapter.removeItem(position)
        }
    }

    private val usersAdapter: UsersAdapter by lazy {
        UsersAdapter(rv_users, R.layout.adapter_user_item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)
        initView()
    }

    private fun initView() {
        iv_back.setOnClickListener {
            finish()
        }
        head_title.text = "删除用户"
        rv_users.layoutManager = LinearLayoutManager(this)
        usersAdapter.setOnItemChildClickListener(this)
        rv_users.adapter = usersAdapter
        async(UI) {
            val users = bg {
                val db = UserDatabase.getInstance().userDao()
                db.getUsers()
            }
            usersAdapter.data = users.await()
        }
    }

    private fun deleteUser(uid: String) {
        async(UI) {
            val users = bg {
                val db = UserDatabase.getInstance().userDao()
                db.deleteUid(uid)
            }
            toast("删除账户成功")

        }
    }
}

class UsersAdapter(recyclerView: RecyclerView, lauoutId: Int) : BGARecyclerViewAdapter<User>(recyclerView, lauoutId) {
    override fun setItemChildListener(helper: BGAViewHolderHelper?, viewType: Int) {
        helper?.setItemChildClickListener(R.id.iv_delete_user)
    }

    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: User?) {
        if (position == 0) {
            helper?.setVisibility(R.id.iv_delete_user, View.INVISIBLE)
        }
        helper?.setText(R.id.tv_user_name, "用户姓名:" + model?.nick_name)
        helper?.setText(R.id.tv_user_phone, "用户电话:" + model?.phone)
    }

}
