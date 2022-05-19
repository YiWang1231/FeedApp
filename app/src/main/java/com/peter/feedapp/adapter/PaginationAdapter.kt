package com.peter.feedapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.peter.feedapp.CourseActivity
import com.peter.feedapp.R
import com.peter.feedapp.bean.Course
import com.peter.feedapp.view.RoundCornerImageView
import com.squareup.picasso.Picasso

class PaginationAdapter(var context: Context): RecyclerView.Adapter<CourseViewHolder>() {
    var courseList: MutableList<Course> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder{
        return CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.course_item, null))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]

//        holder.itemView.setOnClickListener(View.OnClickListener {
//            val intent = Intent(context, CourseActivity::class.java)
//            intent.putExtra("url", course.link)
//        })

        // 绑定数据
        holder.courseTitle.text = course.title

        holder.courseDesc.text = course.desc
        if (course.envelopePic.isNotEmpty()) {
            holder.courseImg.visibility = View.VISIBLE
            Picasso.get().load(course.envelopePic).placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).into(holder.courseImg)
            holder.courseTitle.layoutParams = holder.setMargin(true, holder.courseTitle)
            holder.courseDesc.layoutParams = holder.setMargin(true, holder.courseDesc)
        } else {
            holder.courseImg.visibility = View.GONE
            holder.courseTitle.layoutParams = holder.setMargin(false, holder.courseTitle)
            holder.courseDesc.layoutParams = holder.setMargin(false, holder.courseDesc)
        }


        holder.author.text = course.author

        holder.niceDate.text = course.niceDate

        holder.courseChapter.text = course.superChapterName + "." + course.chapterName

        if (course.tag.isEmpty()) {
            holder.tagCourse.visibility = View.GONE
        } else {
            holder.tagCourse.visibility = View.GONE
            holder.tagCourse.text = course.tag
        }

        if (!course.fresh) {
            holder.tagNew.visibility = View.GONE
            holder.author.layoutParams = holder.setMargin(false, holder.author)
        } else {
            holder.tagNew.visibility = View.VISIBLE
            holder.author.layoutParams = holder.setMargin(true, holder.author)
        }

        if (course.type != 1) {
            holder.topTag.visibility = View.GONE
            holder.courseChapter.layoutParams = holder.setMargin(false, holder.courseChapter)
        } else {
            holder.topTag.visibility = View.VISIBLE
            holder.courseChapter.layoutParams = holder.setMargin(true, holder.courseChapter)
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    private fun getItem(pos: Int): Course {
        return courseList[pos]
    }

    fun add(course: Course) {
        courseList.add(course)
        notifyItemInserted(courseList.size - 1)
    }

    fun addAll(_courList: MutableList<Course>) {
        courseList.addAll(_courList)
    }

    private fun remove(course: Course) {
        val pos = courseList.indexOf(course)
        if (pos > -1) {
            courseList.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    fun clear() {
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }


}

class CourseViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {
    public var tagNew:TextView = itemView.findViewById(R.id.new_course_tag)
    public var author: TextView = itemView.findViewById(R.id.course_author)
    public var tagCourse: TextView = itemView.findViewById(R.id.course_tag)
    public var niceDate: TextView = itemView.findViewById(R.id.nice_date)
    public var courseImg: RoundCornerImageView = itemView.findViewById(R.id.course_img)
    public var courseTitle: TextView = itemView.findViewById(R.id.course_title)
    public var courseDesc: TextView = itemView.findViewById(R.id.course_desc)
    public var topTag: TextView = itemView.findViewById(R.id.top_tag)
    public var courseChapter: TextView = itemView.findViewById(R.id.course_chapter)

    fun setMargin(visible: Boolean, view: View): ViewGroup.MarginLayoutParams{
        val params: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        if (visible) {
            params.marginStart = 5
        } else {
            params.marginStart = 0
        }
        return params
    }
}

fun main() {
//    Picasso.get().load("https://www.wanandroid.com/resources/image/pc/default_project_img.jpg")
}