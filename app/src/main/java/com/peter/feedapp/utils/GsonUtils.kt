package com.peter.feedapp.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.peter.feedapp.bean.JsonArrayBase
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GsonUtils private constructor(){

    /**
     * Bean转gson
     */

    fun <T> bean2Json(bean: List<T>): String {
        return gsonProvider.toJson(bean)
    }

    /**
     * gson转成泛型具象Bean
     * @param gsonString json字符串
     * @param bean 要转化的类
     */
    fun <T> gson2Bean(gsonString: String, bean: Class<T>): T{
        return gsonProvider.fromJson(gsonString, bean)
    }


    /**
     * gson转成泛型List<Bean>
     */
    fun <T> gson2List(gsonString: String, bean: Class<T>): MutableList<T>{
        val list: MutableList<T> = ArrayList()
        val array: JsonArray = JsonParser().parse(gsonString).asJsonArray
        for(item in array) {
            list.add(gsonProvider.fromJson(item, bean))
        }
        return list
    }

    /**
     * data为array
     */
    fun <T> fromJsonArray(gsonString: String, classOfT: Class<T>): JsonArrayBase<List<T>> {
        // 生成List<T>
        val listType: Type = ParameterizedTypeImpl<List<T>>(GsonUtils.classOf(), arrayOf(classOfT))
        val type: Type = ParameterizedTypeImpl<JsonArrayBase<List<T>>>(GsonUtils.classOf(), arrayOf(listType))
        return gsonProvider.fromJson(gsonString, type)
    }

    companion object {
        var gsonProvider = Gson()
        private set

        inline fun <reified T> classOf() = T::class.java

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

    class ParameterizedTypeImpl<T>(private val classOfT: Class<T>, private var args: Array<Type>?): ParameterizedType {
        init {
            args = if (args!=null) args else arrayOf()
        }

        override fun getActualTypeArguments(): Array<Type> {
            return args!!
        }

        override fun getRawType(): Type {
             return classOfT
        }

        override fun getOwnerType(): Type? {
            return null
        }


    }
}
