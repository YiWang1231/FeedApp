package com.peter.feedapp.biz

import android.util.Log
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.bean.BannerDataBase
import com.peter.feedapp.bean.NetDataBase
import com.peter.feedapp.utils.GsonUtils

private const val BANNER_API = "https://www.wanandroid.com/banner/json"

class BannerBiz {
    private lateinit var getNewBannerTask: DataDownloadTask<BannerDataBase>

    fun getBannersTask(callback:Callback<BannerDataBase>) {
        getNewBannerTask = DataDownloadTask(callback)
        getNewBannerTask.execute(BANNER_API)
    }
}
