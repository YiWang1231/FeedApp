package com.peter.feedapp.utils

import android.os.Handler
import android.os.Looper
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.peter.feedapp.bean.*
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type

private val mClient = OkHttpClient()
private const val GetMethod = "GET"
private const val PostMethod = "POST"
private val mHandler = Handler(Looper.getMainLooper())

class HttpUtils private constructor(val method: String?, private val builder: Request.Builder, val url: String?, val params: JsonObject?){

    constructor(builder: BaseBuilder): this(
        builder.method,
        builder.builder,
        builder.url,
        builder.params
    )

    companion object {

        inline fun <reified T> classOf() = T::class.java

        inline fun <reified  T> typeToken() = object : TypeToken<T>(){}.type!!

        fun get(): BaseBuilder {
            val builder = GetBuilder()
            builder.setMethod(GetMethod)
            return builder
        }

        fun post(): BaseBuilder {
            val builder = PostBuilder()
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

    fun enqueue(callback: JsonDataBaseCallback) {
        val request = builder.build()
        val call = mClient.newCall(request)
        call.enqueue(object : Callback {

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

    fun<T> execute(returnType: Type, callBack: HttpCallback<T>) {
        val request = builder.build()
        val call = request.let { mClient.newCall(it) }
        val res  = call.execute()
        if (res.isSuccessful) {
            val json = res.body()!!.string()
            val rel: T = GsonUtils.gsonProvider.fromJson(json, returnType)
            callBack.onSuccess(rel)
        } else {
            val exception = java.lang.Exception(res.code().toString())
            mHandler.post{
                callBack.onFiled(exception)
            }
        }
    }

    fun<T> enqueue(returnType: Type, callBack: HttpCallback<T>) {
        val request = builder.build()
        val call = mClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val exception = java.lang.Exception("请求失败")
                mHandler.post{
                    callBack.onFiled(exception)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body()!!.string()
                    val rel: T = GsonUtils.gsonProvider.fromJson(json, returnType)
                    mHandler.post {
                        callBack.onSuccess(rel)
                    }
                } else {
                    mHandler.post {
                        val exception = java.lang.Exception(response.code().toString())
                        callBack.onFiled(exception)
                    }
                }
            }

        })
    }
}

interface JsonDataBaseCallback {
    fun onSuccess(result: String)
    fun onFailed(exception: Exception, request: Request)
}

interface HttpCallback<T> {
    fun onSuccess(result: T)
    fun onFiled(exception: Exception)
}

abstract class ApiCallback<T>: HttpCallback<ApiResponse<T>> {
    abstract fun onReqSuccess(ret: T)
    override fun onSuccess(result: ApiResponse<T>) {
        if (result.errorCode == 0 && result.data !=null) {
            onReqSuccess(result.data!!)
        } else {
            onFiled(java.lang.Exception(result.errorMsg))
        }
    }
}