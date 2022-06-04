package com.peter.feedapp.biz


import com.peter.feedapp.bean.Course
import com.peter.feedapp.bean.JsonArrayBase
import com.peter.feedapp.utils.GsonUtils
import org.jsoup.Jsoup

class CourseBiz {

    companion object {
        fun parseCourseContent(jsonArrayBase: JsonArrayBase<Course>): MutableList<Course> {
            val courseList: MutableList<Course> = ArrayList()
            val dataArray = GsonUtils.newInstance().bean2Json(jsonArrayBase.data)
            courseList.addAll(GsonUtils.newInstance().gson2List(dataArray, Course::class.java))
            for (course in courseList) {
                // 设置描述
                if (course.desc?.isNotEmpty() == true) {
                    val doc = Jsoup.parse(course.desc)
                    course.desc =doc.body().text()
                }

                // 设置作者
                if (course.author?.isEmpty() == true) {
                    course.author = course.shareUser
                }

                // 设置tag
                if (course.tags?.isNotEmpty() == true) {
                    course.showTag = course.tags!![0].name
                } else {
                    course.showTag = ""
                }
            }
            return courseList
        }
    }
}