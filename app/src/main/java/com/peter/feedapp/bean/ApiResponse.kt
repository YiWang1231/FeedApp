package com.peter.feedapp.bean

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName(value = "data", alternate = ["datas"])
    val data: T?,
    val errorCode: Int?,
    val errorMsg: String?
)