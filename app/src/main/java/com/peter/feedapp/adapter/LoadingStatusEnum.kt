package com.peter.feedapp.adapter

enum class LoadingStatusEnum(val statusCode: Int) {
    STATUS_LOADING(10000),
    STATUS_FINISHED(20000),
    STATUS_FAILED(-100000)
}