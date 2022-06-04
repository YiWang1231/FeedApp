package com.peter.feedapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.peter.feedapp.R
import com.peter.feedapp.adapter.PaginationAdapter
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.databinding.BannerImgBinding
import com.squareup.picasso.Picasso

private const val PAGE_START = 1

class BannerView(context: Context, private var bannerList: MutableList<Banner>, var clickListener: OnBannerClickListener?): RelativeLayout(context){
    private var adapter: BannerAdapter
    lateinit var viewPager2: ViewPager2
        private set
    var dotGroup: ViewGroup
        private set
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

    constructor(context: Context): this(context, ArrayList(), null)

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


    init {
        // 初始化样式
        viewPager2 = ViewPager2(context)
        dotGroup = LinearLayout(context)
        val bannerParams = LayoutParams(LayoutParams.MATCH_PARENT, PaginationAdapter(context).getPixelFromDp(200F))
        this.layoutParams = bannerParams
        viewPager2.layoutParams = bannerParams
        initData()
        adapter = BannerAdapter(context, banners, object: OnBannerClickListener {
            override fun onClick(banner: Banner) {
                println("点击Banner")
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

        // 自动滑动
        this.addView(viewPager2)
        // 初始化dots
        buildDots()
        viewPager2.postDelayed(mLooper, 5000)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        totalPage = bannerList.size
        // 第一位添加最后一张
        banners.add(0, bannerList.last())
        // 添加正常数据
        banners.addAll(bannerList)
        // 最后一位添加第一张
        banners.add(bannerList.first())

    }

    @SuppressLint("InflateParams")
    private fun buildDots() {
        for (int in 0 until totalPage) {
            val view: ImageView = LayoutInflater.from(context).inflate(R.layout.dot_item, null) as ImageView
            val layoutParams = LayoutParams(60, 60)
            view.layoutParams = layoutParams
            dotGroup.addView(view)
            dotList.add(view)
        }
        val dotsParams = LayoutParams(LayoutParams.WRAP_CONTENT, PaginationAdapter(context).getPixelFromDp(30F))
        dotsParams.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        dotsParams.addRule(CENTER_HORIZONTAL, TRUE)
        this.addView(dotGroup, dotsParams)
    }

    fun selectDot(int: Int) {
        for ((index, dot) in dotList.withIndex()) {
            dot.isSelected = index == int
        }
    }

    private class BannerAdapter(var context: Context, var bannerList: MutableList<Banner>, var clickListener: OnBannerClickListener): RecyclerView.Adapter<BannerViewHolder>() {
        private lateinit var binding: BannerImgBinding
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
            binding = BannerImgBinding.inflate(LayoutInflater.from(context), parent, false)
            return BannerViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
            val banner = bannerList[position]
            holder.bannerImg.setOnClickListener {
                clickListener.onClick(banner)
            }
            // 绑定数据
            Picasso.get()
                .load(banner.imagePath)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(holder.bannerImg)
        }

        override fun getItemCount(): Int {
            return bannerList.size
        }

    }

    private class BannerViewHolder(private val binding: BannerImgBinding): RecyclerView.ViewHolder(binding.root) {
        var bannerImg: ImageView = binding.bannerImg
    }

    interface OnBannerClickListener{
        fun  onClick(banner:Banner)
    }
}