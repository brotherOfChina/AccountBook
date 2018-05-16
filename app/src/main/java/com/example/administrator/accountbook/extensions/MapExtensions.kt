package com.example.administrator.accountbook.extensions

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by {zpj}
 *  on 2018/3/16 0016.
 */
fun Map<String, String?>.getSign(): String {
    val maps = this.toSortedMap()
    var s = String()
    for ((k, v) in maps) {
        s += v
    }
    return getMD5Str(s + "JxqzDist@20180228")
}

private fun getMD5Str(str: String): String {
    var messageDigest: MessageDigest? = null
    try {
        messageDigest = MessageDigest.getInstance("MD5")
        messageDigest!!.reset()
        messageDigest.update(str.toByteArray(charset("UTF-8")))
    } catch (e: NoSuchAlgorithmException) {
        println("NoSuchAlgorithmException caught!")
        System.exit(-1)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }

    val byteArray = messageDigest!!.digest()
    val md5StrBuff = StringBuffer()
    for (i in byteArray.indices) {
        if (Integer.toHexString(0xFF and byteArray[i].toInt()).length == 1)
            md5StrBuff.append("0").append(Integer.toHexString(0xFF and byteArray[i].toInt()))
        else
            md5StrBuff.append(Integer.toHexString(0xFF and byteArray[i].toInt()))
    }
    return md5StrBuff.toString()
}
