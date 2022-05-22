package com.peter.feedapp.utils

import okhttp3.*

private val mClient = OkHttpClient()

class HttpUtils private constructor(){
    fun doGet(urlStr: String): String {
        val builder = Request.Builder()
        builder.url(urlStr)
        val request = builder.build()
        val call = mClient.newCall(request)
        try {
            val response = call.execute()
            return if (response.isSuccessful) {
                response.body()!!.string()
            } else {
                return ""
            }
        } catch (e: Exception) {
            println(e)
            return ""
        }
    }

    companion object {
        private var instance: HttpUtils? = null
            get() {
                if (field == null) {
                    field = HttpUtils()
                }
                return field
            }

        @Synchronized
        fun newInstance(): HttpUtils {
            return instance!!
        }
    }
}