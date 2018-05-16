package com.example.administrator.accountbook.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

/**
 * Created by Administrator on 2017/9/5 0005.
 * string 的扩展
 */


fun String.hidePhone(): String =
        this.substring(0, 3) + "****" + this.substring(7, this.length)

fun String.getName(): String {
    return try {
        val start = this.indexOf("（")
        val end = this.indexOf("）")
        this.substring(start + 1, end)
    } catch (e: Exception) {
        this
    }

}

@SuppressLint("SimpleDateFormat")
fun String.getUseTime(): String {
    try {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val format = SimpleDateFormat(pattern)
        val placeTime = format.parse(this).time
        val userTime = System.currentTimeMillis() - placeTime
        val hour = userTime / 1000 / 3600  //一共有几个小时
        val minute = userTime / 1000 % 3600 / 60
        val seconds = userTime / 1000 % 3600 % 60 //一共有多少秒
        var showHour = ""
        var showMinute = ""
        var showSeconds = ""
        if (hour < 10) {
            showHour = "0$hour"
        } else {
            showHour = "$hour"
        }
        if (minute < 10) {
            showMinute = "0$minute"
        } else {
            showMinute = "$minute"
        }
        if (seconds < 10) {
            showSeconds = "0$seconds"
        } else {
            showSeconds = "$seconds"
        }
        return "$showHour:$showMinute:$showSeconds"
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "00:00:00"

}



