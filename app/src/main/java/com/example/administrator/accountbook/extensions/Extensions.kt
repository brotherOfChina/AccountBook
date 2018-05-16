package com.example.administrator.accountbook.extensions

import android.content.Context
import com.example.administrator.accountbook.base.MyApplication

/**
 * Created by Administrator
 * on 2018/2/23 0023.
 */

fun <T> Any.checkNotNum(t: T): T? {
    if (t == null) {
        throw NullPointerException("$t:不能为空")
    } else {
        return t
    }
}

fun Context.isLogin(): Boolean {
    val isLogin: Boolean by DelegatesExt.preference(MyApplication.instance, "login", false)
    return isLogin
}

fun Context.setLogin(login: Boolean): Boolean {
    var isLogin: Boolean by DelegatesExt.preference(MyApplication.instance, "login", false)
    isLogin = login
    return isLogin
}
