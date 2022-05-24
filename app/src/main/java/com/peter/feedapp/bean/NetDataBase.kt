package com.peter.feedapp.bean

data class NetDataBase<T>(var datas: List<T>, var data: List<T>, var pageCount: Int, var errorCode: Int)