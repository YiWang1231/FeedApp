package com.peter.feedapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class CourseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        val url = intent.getStringExtra("url")
        val webView: WebView = findViewById(R.id.web)
        webView.loadUrl(url!!)
    }
}