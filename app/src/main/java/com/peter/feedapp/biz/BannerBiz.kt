package com.peter.feedapp.biz

import com.peter.feedapp.bean.Banner
import com.peter.feedapp.bean.BannerDataBase
import com.peter.feedapp.bean.JsonArrayBase
import com.peter.feedapp.utils.HttpUtils
import okhttp3.Request
import com.peter.feedapp.utils.JsonDataBaseCallback
import com.peter.feedapp.utils.GsonUtils

private const val BANNER_API = "https://www.wanandroid.com/banner/json"

class BannerBiz {
    private lateinit var getNewBannerTask: DataDownloadTask<BannerDataBase>

    fun getBannersTask(callback:com.peter.feedapp.biz.Callback<BannerDataBase>) {
        getNewBannerTask = DataDownloadTask(callback)
        getNewBannerTask.execute(BANNER_API)
    }

    fun getBannerDataBase() {
        val bannerList: MutableList<Banner> = ArrayList()
        HttpUtils.get().url(BANNER_API).build().enqueue(HttpUtils.classOf(), object : JsonDataBaseCallback<JsonArrayBase<Banner>> {
            override fun onSuccess(database: JsonArrayBase<Banner>) {
                if (database.errorCode == 0) {
                    val bannerListJson = GsonUtils.newInstance().bean2Json(database.data)
                    bannerList.addAll(GsonUtils.newInstance().gson2List(bannerListJson, Banner::class.java))
                }

            }

            override fun onFailed(exception: Exception, request: Request) {
            }
        })
    }
}


