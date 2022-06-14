package com.peter.feedapp.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

class GsonUtils private constructor(){
    /**
     * Bean转gson
     */

    fun <T> bean2Json(bean: List<T>): String {
        return gsonProvider.toJson(bean)
    }

    /**
     * gson转成泛型Bean
     * @param gsonString json字符串
     * @param bean 要转化的类
     */
    fun <T> gson2Bean(gsonString: String, bean: Class<T>): T{
        return gsonProvider.fromJson(gsonString, bean)
    }


    /**
     * gson转成泛型Bean
     */
    fun <T> gson2List(gsonString: String, bean: Class<T>): MutableList<T>{
        val list: MutableList<T> = ArrayList()
        val array: JsonArray = JsonParser().parse(gsonString).asJsonArray
        for(item in array) {
            list.add(gsonProvider.fromJson(item, bean))
        }
        return list
    }

    companion object {
        var gsonProvider = Gson()
        private set

        private var instance: GsonUtils? = null
            get() {
                if (field == null) {
                    field = GsonUtils()
                }
                return field
            }

        @Synchronized
        fun newInstance(): GsonUtils {
            return instance!!
        }
    }
}

fun main() {
    fun<T> getType(str: String): T {
        val typeToken = object : TypeToken<T>() {}.type
        return GsonUtils.gsonProvider.fromJson(str, typeToken)
    }
}