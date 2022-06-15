package com.peter.feedapp.bean

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(@SerializedName(value = "data", alternate = ["datas"])
                          var data: T?,
                          var errorCode: Int?,
                          var errorMsg: String?)