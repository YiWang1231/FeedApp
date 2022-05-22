package com.peter.feedapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
    val courseList get() = _courseList
    private lateinit var binding: CourseItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder{
        binding = CourseItemBinding.inflate(LayoutInflater.from(context), parent, false)
        val view = binding.root
        return CourseViewHolder(view)
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
        if (course.envelopePic!!.isNotEmpty()) {
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
        if (course.tag!!.isEmpty()) {
            holder.tagCourse.visibility = View.GONE
        } else {
            holder.tagCourse.visibility = View.VISIBLE
            holder.tagCourse.text = course.tag
        }

        if (!course.fresh!!) {
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
            params.marginStart = getPixelFromDp(8F, context)
        } else {
            params.marginStart = 0
        }
        return params
    }

    fun getPixelFromDp(dpValue: Float, context: Context): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (scale * dpValue + 0.5F).toInt()
    }


}

class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tagNew:TextView = itemView.findViewById(R.id.new_course_tag)
    var author: TextView = itemView.findViewById(R.id.course_author)
    var tagCourse: TextView = itemView.findViewById(R.id.course_tag)
    var niceDate: TextView = itemView.findViewById(R.id.nice_date)
    var courseImg: RoundCornerImageView = itemView.findViewById(R.id.course_img)
    var courseTitle: TextView = itemView.findViewById(R.id.course_title)
    var courseDesc: TextView = itemView.findViewById(R.id.course_desc)
    var topTag: TextView = itemView.findViewById(R.id.top_tag)
    var courseChapter: TextView = itemView.findViewById(R.id.course_chapter)

}
