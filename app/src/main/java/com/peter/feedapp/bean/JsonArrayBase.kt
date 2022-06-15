package com.peter.feedapp.bean

import com.google.gson.annotations.SerializedName

data class JsonArrayBase<T>(@SerializedName(value = "data", alternate = ["datas"])
                            var data: T,
                            var pageCount: Int,
                            var errorCode: Int)