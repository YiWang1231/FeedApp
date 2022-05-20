package com.peter.feedapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.peter.feedapp.CourseActivity
import com.peter.feedapp.R
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.view.BannerView

class BannerPageAdapter(var context: Context): RecyclerView.Adapter<BannerPageViewHolder>() {
    private lateinit var bannerView: BannerView
    private lateinit var viewPager2: ViewPager2
    private lateinit var dotGroup: ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerPageViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.banner_container, parent, false)
        viewPager2 = itemView.findViewById(R.id.banner)
        dotGroup = itemView.findViewById(R.id.dots)
        bannerView = BannerView(context, viewPager2, dotGroup, object: ClickListener {
            override fun onClick(banner: Banner) {
                val intent = Intent(context, CourseActivity::class.java)
                intent.putExtra("url", banner.url)
                context.startActivity(intent)
            }

        })

        bannerView.init()
        return BannerPageViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: BannerPageViewHolder, position: Int) {
        holder.banner = viewPager2
        holder.dots = dotGroup
    }
}

class BannerPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var banner: ViewPager2
    lateinit var dots: ViewGroup
}