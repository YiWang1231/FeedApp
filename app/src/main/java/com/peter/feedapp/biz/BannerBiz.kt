package com.peter.feedapp.biz

import com.peter.feedapp.bean.BannerDataBase

private const val BANNER_API = "https://www.wanandroid.com/banner/json"

class BannerBiz {
    private lateinit var getNewBannerTask: DataDownloadTask<BannerDataBase>

    fun getNewBanners(callback:Callback<BannerDataBase>) {
        getNewBannerTask = DataDownloadTask(callback)
        getNewBannerTask.execute(BANNER_API)
    }

}
