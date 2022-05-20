package com.peter.feedapp.biz

import android.os.AsyncTask
import com.peter.feedapp.bean.Course
import com.peter.feedapp.utils.HttpUtils
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.Exception
import kotlin.properties.Delegates

private const val TOP_COURSE_LIST_API = "https://www.wanandroid.com/article/top/json"
class CourseBiz {

    companion object {
        private lateinit var getCourseTask: GetCourseTask
        var totalPage by Delegates.notNull<Int>()

        fun getCourses(page: Int, callback: Callback) {
            getCourseTask = GetCourseTask(callback)
            getCourseTask.execute(page)
        }
    }

    class GetCourseTask(private var callback: Callback): AsyncTask<Int, Void, MutableList<Course>>() {
        private var exception: Exception? = null

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg pages: Int?): MutableList<Course> {
            val page = pages[0]
            val courseList: MutableList<Course> = ArrayList()
            try {
                if (page == 0) {
                    courseList.addAll(getTopList())
                }
                courseList.addAll(getCurrentPageCourseList(page!!))
            } catch (e: Exception) {
                exception = e

            }

            return courseList
        }


        @Deprecated("Deprecated in Java",
            ReplaceWith("super.onPostExecute(result)", "android.os.AsyncTask")
        )

        override fun onPostExecute(result: MutableList<Course>?) {
            super.onPostExecute(result)
            if (exception != null) {
                callback.onFailed(exception!!)
                return
            }
            callback.onSuccess(result!!)
        }

        private fun getTopList(): MutableList<Course> {
            val content = HttpUtils.doGet(TOP_COURSE_LIST_API)
            return parseContent(content, true)
        }

        private fun getCurrentPageCourseList(page: Int): MutableList<Course> {
            val courseListApi = "https://www.wanandroid.com/article/list/$page/json"
            val content = HttpUtils.doGet(courseListApi)
            return parseContent(content, false)
        }

        private fun parseContent(content: String, isTopContent: Boolean) : MutableList<Course>{
            //TODO 解析数据
            val courseList: MutableList<Course> = ArrayList()
            val root = JSONObject(content)
            val errorCode = root.optInt("errorCode")
            val dataJsonArray: JSONArray
            if (errorCode == 0) {
                // topList和普通的列表json数据结构不一样
                dataJsonArray = if (isTopContent) {
                    root.optJSONArray("data")!!
                } else {
                    // 首次获取初始化总页数
                    if (root.optJSONObject("data")?.optInt("curPage") == 1) {
                        totalPage = root.optJSONObject("data")?.optInt("pageCount")!!
                    }
                    root.optJSONObject("data")?.optJSONArray("datas")!!
                }
                for (i in 0 until dataJsonArray.length()) {
                    val courseJsonObject = dataJsonArray.getJSONObject(i)
                    val course = Course()
                    // 设置title
                    course.title = courseJsonObject.getString("title")
                    // 设置描述
                    val desc = courseJsonObject.getString("desc")
                    if (desc.isNotEmpty()) {
                        val doc = Jsoup.parse(desc)
                        course.desc =doc.body().text()
                    } else {
                        course.desc = desc
                    }
                    // 设置图片
                    course.envelopePic = courseJsonObject.getString("envelopePic")
                    // 设置作者
                    if (courseJsonObject.getString("author").isNotEmpty()) {
                        course.author = courseJsonObject.getString("author")
                    } else {
                        course.author = courseJsonObject.getString("shareUser")
                    }

                    // 设置日期
                    course.niceDate = courseJsonObject.getString("niceDate")
                    // 设置链接
                    course.link = courseJsonObject.getString("link")
                    // 设置章节
                    course.superChapterName = courseJsonObject.getString("superChapterName")
                    course.chapterName = courseJsonObject.getString("chapterName")
                    // 设置tag
                    val tagJsonArray = courseJsonObject.optJSONArray("tags")
                    if (tagJsonArray!!.length() > 0) {
                        val tagJsonObject = tagJsonArray.getJSONObject(0)
                        course.tag = tagJsonObject.getString("name")
                    }
                    // 设置fresh
                    course.fresh =  courseJsonObject.getBoolean("fresh")
                    // 设置类型 0:Top 1:普通
                    course.type  = courseJsonObject.getInt("type")
                    courseList.add(course)
                }
            }
            return courseList
        }
    }

    interface Callback {
        fun onSuccess(courseList: MutableList<Course>)
        fun onFailed (exception: Exception)
    }
}