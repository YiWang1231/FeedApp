package com.peter.feedapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.peter.feedapp.R
import com.peter.feedapp.adapter.BannerAdapter
import com.peter.feedapp.adapter.ClickListener
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.biz.BannerBiz

private const val PAGE_START = 1

class BannerView(context: Context, var viewPager2: ViewPager2, private var dotGroup: ViewGroup?, var clickListener: ClickListener?): FrameLayout(context){
    private lateinit var adapter: BannerAdapter
    private var banners: MutableList<Banner> = ArrayList()
    private var currentPage = PAGE_START
    private var totalPage = 0
    private var dotList: MutableList<ImageView> = ArrayList()
    private val mLooper = object :Runnable {
        override fun run() {
            viewPager2.currentItem = ++viewPager2.currentItem
            viewPager2.postDelayed(this, 5000)
        }

    }

    constructor(context: Context): this(context, ViewPager2(context), null, null)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val action = ev?.action
        Log.e("Touch", "somethingHappened")
        if (action == MotionEvent.ACTION_DOWN) {
            Log.e("down", "remove")
            // 移除mLooper
            viewPager2.removeCallbacks(mLooper)
        }
        if (action == MotionEvent.ACTION_UP) {
            viewPager2.postDelayed(mLooper, 5000)
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun init() {
        loadData()
        adapter = BannerAdapter(context, banners, object: ClickListener {
            override fun onClick(banner: Banner) {
                clickListener?.onClick(banner)
            }
        })
        viewPager2.adapter = adapter
        viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    viewPager2.setCurrentItem(1,false)
                    selectDot(0)
                } else {
                    currentPage = position
                    Log.e("currentPage", "" + currentPage)
                    selectDot(currentPage - 1)
                }

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (currentPage == 0) {
                        viewPager2.setCurrentItem(adapter.itemCount - 2, false)
                    } else if (currentPage == adapter.itemCount - 1) {
                        viewPager2.setCurrentItem(1, false)
                    }
                }
            }

        })

        // 自动滑动
        viewPager2.postDelayed(mLooper, 5000)
        // 触动停止
//        viewPager2?.getChildAt(0)?.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    Log.e("touch", "down")
//                    viewPager2?.removeCallbacks(mLooper)
//                }
//                MotionEvent.ACTION_UP -> {
//                    Log.e("touch", "up")
//                    viewPager2?.postDelayed(mLooper,10000)
//                }
//            }
//            return@setOnTouchListener false
//        }
    }

    @SuppressLint("InflateParams")
    private fun buildDots() {
        for (int in 0 until totalPage) {
            val view: ImageView = LayoutInflater.from(context).inflate(R.layout.dot_item, null) as ImageView
            val layoutParams = LinearLayout.LayoutParams(60, 60)
            view.layoutParams = layoutParams
            dotGroup?.addView(view)
            dotList.add(view)
        }
    }

    fun selectDot(int: Int) {
        for ((index, dot) in dotList.withIndex()) {
            dot.isSelected = index == int
        }
    }

    private fun loadData() {
        BannerBiz.getBanners(object: BannerBiz.CallBack {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(bannerList: MutableList<Banner>) {
                totalPage = bannerList.size
                buildDots()
                // 第一位添加最后一张
                banners.add(0, bannerList.last())
                // 添加正常数据
                banners.addAll(bannerList)
                // 最后一位添加第一张
                banners.add(bannerList.first())
                adapter.notifyDataSetChanged()
            }

            override fun onFailed(exception: Exception) {
                return
            }

        })
    }
}