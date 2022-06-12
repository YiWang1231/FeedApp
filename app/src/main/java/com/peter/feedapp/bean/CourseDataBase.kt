package com.peter.feedapp.bean

data class CourseDataBase(
    val datas: MutableList<Course>?,
    val curPage: Int?,
    val pageCount: Int?,
    val total: Int?
)