package com.peter.feedapp.utils

import android.util.Log
import okhttp3.*
import java.io.IOException

object HttpUtils {
    fun doGet(urlStr: String): String {
        val mClient = OkHttpClient()
        val builder = Request.Builder()
        builder.url(urlStr)
        val request = builder.build()
        val call = mClient.newCall(request)
        val response = call.execute()
        return if (response.isSuccessful) {
            response.body()!!.string()
        } else {
            return ""
        }
    }
}

fun main() {
    val a = "https://www.wanandroid.com/article/top/json"
    val b = HttpUtils.doGet(a)

}