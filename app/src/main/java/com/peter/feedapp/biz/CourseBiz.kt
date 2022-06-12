package com.peter.feedapp.biz


import com.peter.feedapp.bean.Course
import com.peter.feedapp.bean.JsonArrayBase
import com.peter.feedapp.utils.GsonUtils
import org.jsoup.Jsoup

class CourseBiz {

    companion object {
        fun parseCourseContent(courses: MutableList<Course>?): MutableList<Course> {
            courses?.forEach { course ->
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
            return courses?: mutableListOf()
        }


        fun parseCourseContent(jsonArrayBase: JsonArrayBase<List<Course>>): MutableList<Course> {
            val courseList: MutableList<Course> = ArrayList()
            val dataArray = jsonArrayBase.data
            courseList.addAll(dataArray)
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