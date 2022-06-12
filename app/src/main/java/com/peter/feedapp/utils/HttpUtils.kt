package com.peter.feedapp.utils

import android.os.Handler
import android.os.Looper
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.peter.feedapp.bean.ApiResponse
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type

private val mClient = OkHttpClient()
private val mainHandler = Handler(Looper.getMainLooper())
private const val GetMethod = "GET"
private const val PostMethod = "POST"

class HttpUtils private constructor(val method: String?, private val builder: Request.Builder, val url: String?, val params: JsonObject?){

    private constructor(builder: Builder): this(
        builder.method,
        builder.builder,
        builder.url,
        builder.params
    )

    companion object {

        inline fun <reified T> typeToken() = object : TypeToken<T>(){}.type

        inline fun <reified T> getClass(value: T): Class<T> = T::class.java

        fun get(): Builder {
            val builder = Builder()
            builder.setMethod(GetMethod)
            return builder
        }

        fun post(): Builder {
            val builder = Builder()
            builder.setMethod(PostMethod)
            return builder
        }
    }

    fun execute(callback: JsonDataBaseCallback){
        val request = builder.build()
        val call = request.let { mClient.newCall(it) }
        val res  = call.execute()
        if (res.isSuccessful) {
            val json = res.body()!!.string()
            callback.onSuccess(json)
        } else {
            val exception = java.lang.Exception("请求失败")
            callback.onFailed(exception, request)
        }
    }

    fun <T> enqueue(returnType: Type, callback: HttpCallback<T>) {
        val request = builder.build()
        val call = mClient.newCall(request)
        call.enqueue(object : okhttp3.Callback {

            override fun onFailure(call: Call, e: IOException) {
                val exception = java.lang.Exception("请求失败")
                mainHandler.post{
                    callback.onFailed(exception)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body()!!.string()
                    val ret: T = GsonUtils.gsonProvider.fromJson(json, returnType)
                    mainHandler.post {
                        callback.onSuccess(ret)
                    }
                } else {
                    mainHandler.post{
                        val exception = java.lang.Exception(response.code().toString())
                        callback.onFailed(exception)
                    }
                }
            }
        })
    }

    fun enqueue(callback: JsonDataBaseCallback) {
        val request = builder.build()
        val call = mClient.newCall(request)
        call.enqueue(object : okhttp3.Callback {

            override fun onFailure(call: Call, e: IOException) {
                val exception = java.lang.Exception("请求失败")
                callback.onFailed(exception, request)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body()!!.string()
                    callback.onSuccess(json)
                } else {
                    val exception = java.lang.Exception(response.code().toString())
                    callback.onFailed(exception, request)
                }
            }
        })
    }


    class Builder {
        var method: String? = null
            private set
        var builder = Request.Builder()
            private set
        var url: String? = null
            private set
        var params: JsonObject? = JsonObject()
            private set

        fun setMethod(inputMethod: String) {
            this.method = inputMethod
        }

        fun url(inputUrl: String) = apply {
            this.url = inputUrl
        }

        fun headers(key: String, value: String) = apply {
            builder.addHeader(key, value)
        }

        fun addParams(key: String, value: String) = apply {
            this.params?.addProperty(key, value)
        }

        fun build(): HttpUtils {
            val p = this.params
            var tempUrl = this.url
            if (p?.entrySet()?.isNotEmpty() == true) {
                if (this.method == GetMethod) {
                    tempUrl  = "${tempUrl}?"
                    for ( param in p.entrySet()) {
                        tempUrl = if (param != p.entrySet().last()) {
                            "${tempUrl}${param.key}=${param.value.asString}&"
                        } else {
                            "${tempUrl}${param.key}=${param.value.asInt}"
                        }
                    }
                } else if (this.method == PostMethod) {
                    val formBodyBuilder = FormBody.Builder()
                    for ( param in p.entrySet()) {
                        formBodyBuilder.add(param.key, param.value.asString)
                    }
                    val formBody = formBodyBuilder.build()
                    this.builder.post(formBody)
                }
            }
            tempUrl?.let { this.builder.url(it) }
            return HttpUtils(this)
        }
    }
}

interface JsonDataBaseCallback {
    fun onSuccess(result: String)
    fun onFailed(exception: Exception, request: Request)
}
interface HttpCallback<T> {
    fun onSuccess(result: T)
    fun onFailed(exception: Exception)
}

abstract class ApiCallback<T>: HttpCallback<ApiResponse<T>> {

    abstract fun onReqSuccess(ret: T)

    override fun onSuccess(result: ApiResponse<T>) {
        if (result.errorCode == 0 && result.data != null) {
            onReqSuccess(result.data)
        } else {
            onFailed(java.lang.Exception(result.errorMsg))
        }
    }
}