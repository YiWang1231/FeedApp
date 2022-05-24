package com.peter.feedapp.biz

import android.os.AsyncTask
import com.peter.feedapp.utils.HttpUtils

class DataDownloadTask<T>(private val callback: Callback<T>): AsyncTask<String, Void, T>(){
    private var exception: Exception? = null
    override fun doInBackground(vararg urls: String?): T {
        val url = urls[0]
        var content = ""
        try {
            content = HttpUtils.doGet(url!!)
        } catch (e: Exception) {
            exception = e
        }

        return callback.parseContent(content)
    }

    override fun onPostExecute(result: T) {
        super.onPostExecute(result)
        if (exception != null) {
            callback.onFailed(exception!!)
        }
        callback.onSuccess(result!!)
    }

}


interface Callback<T> {
    fun onSuccess(database: T)
    fun onFailed(exception: Exception)
    fun parseContent(content: String): T
}