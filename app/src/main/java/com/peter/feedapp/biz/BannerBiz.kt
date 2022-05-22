package com.peter.feedapp.biz

import android.os.AsyncTask
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.bean.NetDataBase
import com.peter.feedapp.utils.GsonUtils
import com.peter.feedapp.utils.HttpUtils
import kotlin.Exception

private const val BANNER_API = "https://www.wanandroid.com/banner/json"

class BannerBiz {
    private lateinit var getBannerTask: GetBannerTask

    fun getBanners(callBack: CallBack) {
        getBannerTask = GetBannerTask(callBack)
        getBannerTask.execute()
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
            val content = HttpUtils.newInstance().doGet(BANNER_API)
            return parseContent(content)
        }

        private fun parseContent(content: String): MutableList<Banner> {
            // 数据解析
            val bannerList: MutableList<Banner> = ArrayList()
            val netDataBase: NetDataBase<Banner> =
                GsonUtils.gsonProvider.fromJson<NetDataBase<Banner>>(content, NetDataBase::class.java)
            if (netDataBase.errorCode == 0) {
                val dataArray = GsonUtils.newInstance().bean2Json(netDataBase.data)
                bannerList.addAll(GsonUtils.newInstance().gson2List(dataArray, Banner::class.java))
            }
            return bannerList
        }
    }

    interface CallBack {
        fun onSuccess(bannerList: MutableList<Banner>)
        fun onFailed(exception: Exception)
    }
}
