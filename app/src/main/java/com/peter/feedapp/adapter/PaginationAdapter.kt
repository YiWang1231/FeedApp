package com.peter.feedapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.peter.feedapp.CourseActivity
import com.peter.feedapp.R
import com.peter.feedapp.bean.Course
import com.peter.feedapp.databinding.CourseItemBinding
import com.peter.feedapp.view.RoundCornerImageView
import com.squareup.picasso.Picasso

class PaginationAdapter(var context: Context): RecyclerView.Adapter<CourseViewHolder>() {
    private var _courseList: MutableList<Course> = mutableListOf()
    val courseList:List<Course> get() = _courseList
    private lateinit var binding: CourseItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder{
        binding = CourseItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CourseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CourseActivity::class.java)
            intent.putExtra("url", course.link)
            context.startActivity(intent)
        }

        // 绑定数据
        holder.courseTitle.text = course.title

        holder.courseDesc.text = course.desc
        if (course.envelopePic?.isNotEmpty() == true) {
            holder.courseImg.visibility = View.VISIBLE
            Picasso.get().load(course.envelopePic).placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).into(holder.courseImg)
            holder.courseTitle.layoutParams = setMargin(true, holder.courseTitle)
            holder.courseDesc.layoutParams = setMargin(true, holder.courseDesc)
        } else {
            holder.courseImg.visibility = View.GONE
            holder.courseTitle.layoutParams = setMargin(false, holder.courseTitle)
            holder.courseDesc.layoutParams = setMargin(false, holder.courseDesc)
        }


        holder.author.text = course.author

        holder.niceDate.text = course.niceDate

        holder.courseChapter.text = "${course.superChapterName}.${course.chapterName}"
        if (course.showTag?.isEmpty() == true) {
            holder.tagCourse.visibility = View.GONE
        } else {
            holder.tagCourse.visibility = View.VISIBLE
            holder.tagCourse.text = course.showTag
        }

        if (course.fresh == false) {
            holder.tagNew.visibility = View.GONE
            holder.author.layoutParams = setMargin(false, holder.author)
        } else {
            holder.tagNew.visibility = View.VISIBLE
            holder.author.layoutParams = setMargin(true, holder.author)
        }

        if (course.type != 1) {
            holder.topTag.visibility = View.GONE
            holder.courseChapter.layoutParams = setMargin(false, holder.courseChapter)
        } else {
            holder.topTag.visibility = View.VISIBLE
            holder.courseChapter.layoutParams = setMargin(true, holder.courseChapter)
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }


    private fun setMargin(visible: Boolean, view: View): ViewGroup.MarginLayoutParams{
        val params: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        if (visible) {
            params.marginStart = getPixelFromDp(8F)
        } else {
            params.marginStart = 0
        }
        return params
    }

    fun getPixelFromDp(dpValue: Float): Int {
        val scale: Float = Resources.getSystem().displayMetrics.density
        return (scale * dpValue + 0.5F).toInt()
    }

    fun addCourses(courses: MutableList<Course>) {
        _courseList.addAll(courses)
    }


}

class CourseViewHolder(private val binding: CourseItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var tagNew:TextView = binding.newCourseTag
    var author: TextView = binding.courseAuthor
    var tagCourse: TextView = binding.courseTag
    var niceDate: TextView = binding.niceDate
    var courseImg: RoundCornerImageView = binding.courseImg
    var courseTitle: TextView = binding.courseTitle
    var courseDesc: TextView = binding.courseDesc
    var topTag: TextView = binding.topTag
    var courseChapter: TextView = binding.courseChapter

}
