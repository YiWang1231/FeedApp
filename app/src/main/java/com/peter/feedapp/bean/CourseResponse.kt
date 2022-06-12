package com.peter.feedapp.bean

data class CourseResponse(val datas: MutableList<Course>?,
                          var pageCount: Int?
                          )