package com.peter.feedapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.peter.feedapp.databinding.ActivityCourseBinding

class CourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val url = intent.getStringExtra("url")
        if (url != null) {
            binding.web.loadUrl(url)
        } else {
            finish()
        }
    }
}