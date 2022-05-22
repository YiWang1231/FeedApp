package com.peter.feedapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager2.widget.ViewPager2
import com.peter.feedapp.R
import com.peter.feedapp.adapter.BannerAdapter
import com.peter.feedapp.adapter.ClickListener
import com.peter.feedapp.adapter.PaginationAdapter
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.biz.BannerBiz

private const val PAGE_START = 1

class BannerView(context: Context, var viewPager2: ViewPager2, private var dotGroup: ViewGroup?, var clickListener: ClickListener?): RelativeLayout(context){
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
        if (action == MotionEvent.ACTION_DOWN) {
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
        // 初始化样式
        val bannerParams = LayoutParams(LayoutParams.MATCH_PARENT, PaginationAdapter(context).getPixelFromDp(200F, context))
        this.layoutParams = bannerParams
        viewPager2.layoutParams = bannerParams
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
        this.addView(viewPager2)
        // 自动滑动
        viewPager2.postDelayed(mLooper, 5000)
    }

    @SuppressLint("InflateParams")
    private fun buildDots() {
        for (int in 0 until totalPage) {
            val view: ImageView = LayoutInflater.from(context).inflate(R.layout.dot_item, null) as ImageView
            val layoutParams = LayoutParams(60, 60)
            view.layoutParams = layoutParams
            dotGroup?.addView(view)
            dotList.add(view)
        }
        val dotsParams = LayoutParams(LayoutParams.WRAP_CONTENT, PaginationAdapter(context).getPixelFromDp(30F, context))
        dotsParams.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        dotsParams.addRule(CENTER_HORIZONTAL, TRUE)
//        dotsParams.bottomMargin = PaginationAdapter(context).getPixelFromDp(15F, context)
        this.addView(dotGroup, dotsParams)
    }

    fun selectDot(int: Int) {
        for ((index, dot) in dotList.withIndex()) {
            dot.isSelected = index == int
        }
    }

    private fun loadData() {
        BannerBiz().getBanners(object: BannerBiz.CallBack {
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