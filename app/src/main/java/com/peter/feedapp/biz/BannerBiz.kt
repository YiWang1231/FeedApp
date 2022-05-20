package com.peter.feedapp.biz

import android.os.AsyncTask
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.utils.HttpUtils
import org.json.JSONObject
import kotlin.Exception

private const val BANNER_API = "https://www.wanandroid.com/banner/json"

class BannerBiz {

    companion object {
        private lateinit var getBannerTask: GetBannerTask
        fun getBanners(callBack: CallBack) {
            getBannerTask = GetBannerTask(callBack)
            getBannerTask.execute()
        }
    }

    class GetBannerTask(private var callBack: CallBack):AsyncTask<Void, Void, MutableList<Banner>>() {
        private var exception: Exception? = null
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: Void?): MutableList<Banner> {
            val bannerList: MutableList<Banner> = ArrayList()
            try {
                bannerList.addAll(getBannerList())
            } catch (e: Exception) {
                exception = e
            }
            return bannerList
        }

        @Deprecated("Deprecated in Java",
            ReplaceWith("super.onPostExecute(result)", "android.os.AsyncTask")
        )
        override fun onPostExecute(result: MutableList<Banner>?) {
            super.onPostExecute(result)
            if (exception != null) {
                callBack.onFailed(exception!!)
                return
            }
            callBack.onSuccess(result!!)
        }

        private fun getBannerList():MutableList<Banner> {
            val content = HttpUtils.doGet(BANNER_API)
            return parseContent(content)
        }

        private fun parseContent(content: String): MutableList<Banner>{
            // 解析数据
            val bannerList: MutableList<Banner> = ArrayList()
            val root = JSONObject(content)
            val errorCode = root.opt("errorCode")
            if (errorCode == 0) {
                val dataJsonArray = root.optJSONArray("data")
                for (i in 0 until dataJsonArray!!.length()){
                    val bannerJsonObject = dataJsonArray.getJSONObject(i)
                    val banner = Banner()
                    banner.title = bannerJsonObject.getString("title")
                    banner.imagePath = bannerJsonObject.getString("imagePath")
                    banner.url = bannerJsonObject.getString("url")
                    bannerList.add(banner)
                }
            }
            return bannerList
        }
    }

    interface CallBack {
        fun onSuccess(bannerList: MutableList<Banner>)
        fun onFailed(exception: Exception)
    }
}
