package com.peter.feedapp.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.peter.feedapp.CourseActivity
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.view.BannerView

class BannerPageAdapter(private val bannerView: BannerView): RecyclerView.Adapter<BannerPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerPageViewHolder {

        return BannerPageViewHolder(bannerView)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: BannerPageViewHolder, position: Int) {
        holder.banner = bannerView.viewPager2
        holder.dots = bannerView.dotGroup
    }
}

class BannerPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var banner: ViewPager2
    lateinit var dots: ViewGroup
}