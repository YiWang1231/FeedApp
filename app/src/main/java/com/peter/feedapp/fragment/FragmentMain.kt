package com.peter.feedapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.feedapp.CourseActivity
import com.peter.feedapp.adapter.BannerPageAdapter
import com.peter.feedapp.adapter.PaginationAdapter
import com.peter.feedapp.adapter.PaginationScrollListener
import com.peter.feedapp.bean.*
import com.peter.feedapp.biz.CourseBiz
import com.peter.feedapp.databinding.FragmentMainBinding
import com.peter.feedapp.utils.GsonUtils
import com.peter.feedapp.utils.HttpUtils
import com.peter.feedapp.utils.JsonDataBaseCallback
import com.peter.feedapp.view.BannerView
import okhttp3.Request
import java.lang.ref.WeakReference

private const val PAGE_START = 0
private const val BANNER_API = "https://www.wanandroid.com/banner/json"
private const val TOP_COURSE_LIST_API = "https://www.wanandroid.com/article/top/json"
private const val BANNER_VIEW_IS_READY = 3000000
private const val COURSE_LIST_IS_READY = 5000000

class FragmentMain : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var paginationAdapter: PaginationAdapter
    private lateinit var bannerPageAdapter: BannerPageAdapter
    private lateinit var bannerView: BannerView
    // 当前页面
    private var currentPage = PAGE_START
    // 全部页数 需请求接口后初始化
    private var courseTotalPages = 0
    // 最后一页则停止加载
    private var isLastPage = false
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        // 初始化
        handler = MyHandler(WeakReference(this))
        bannerView = BannerView(requireContext(), object : BannerView.OnBannerClickListener {
            override fun onClick(banner: Banner) {
                val intent = Intent(requireContext(), CourseActivity::class.java)
                intent.putExtra("url", banner.url)
                requireContext().startActivity(intent)
            }

        })
        paginationAdapter = PaginationAdapter(requireContext())
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.articleList.layoutManager = linearLayoutManager
        // 配置adapter
        bannerPageAdapter = BannerPageAdapter(bannerView)
        binding.articleList.adapter = ConcatAdapter(
            bannerPageAdapter,
            paginationAdapter
        )

        binding.articleList.addOnScrollListener(object: PaginationScrollListener(linearLayoutManager) {
            override fun onScrollToBottom() {
                if (!isLastPage) {
                    println("加载新数据了")
                    loadCourse()
                }
            }
        })
        // 获取课程数据
        loadFirstPageCourse()
        // 获取banner数据
        loadBanners()
        // 为recycleView绑定adapter
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // fragment存在时间比其他视图长， 需要清除对绑定实例的所有引用
        _binding = null
    }

    private fun loadFirstPageCourse() {
        val courseList: MutableList<Course> = ArrayList()
        HttpUtils.get().url(TOP_COURSE_LIST_API).build().enqueue(HttpUtils.classOf(), object : JsonDataBaseCallback<JsonArrayBase<Course>> {
            override fun onSuccess(database: JsonArrayBase<Course>) {
                if (database.errorCode == 0) {
                    courseList.addAll(CourseBiz.parseCourseContent(database))
                    paginationAdapter.addCourses(courseList)
                    loadCourse()
                }
            }

            override fun onFailed(exception: Exception, request: Request) {
                //
            }

        })
    }

    private fun loadCourse() {
        val courseList: MutableList<Course> = ArrayList()
        val courseListApi = "https://www.wanandroid.com/article/list/$currentPage/json"
        HttpUtils.get().url(courseListApi).build().enqueue(HttpUtils.classOf(), object : JsonDataBaseCallback<JsonObjectBase> {
            override fun onSuccess(database: JsonObjectBase) {
                if (database.errorCode == 0) {
                    currentPage += 1
                    val jsonArrayBase = GsonUtils.newInstance().gson2Bean<JsonArrayBase<Course>>(database.data.toString(), HttpUtils.classOf())
                    courseTotalPages = jsonArrayBase.pageCount
                    courseList.addAll(CourseBiz.parseCourseContent(jsonArrayBase))
                    paginationAdapter.addCourses(courseList)
                    val message = Message.obtain()
                    message.what = COURSE_LIST_IS_READY
                    handler.sendMessage(message)
                }
            }

            override fun onFailed(exception: Exception, request: Request) {
                //
            }

        })
    }

    private fun loadBanners() {
        val bannerList: MutableList<Banner> = ArrayList()
        HttpUtils.get().url(BANNER_API).build().enqueue(HttpUtils.classOf(), object : JsonDataBaseCallback<JsonArrayBase<Banner>> {
            override fun onSuccess(database: JsonArrayBase<Banner>) {
                if (database.errorCode == 0) {
                    val bannerListJson = GsonUtils.newInstance().bean2Json(database.data)
                    bannerList.addAll(GsonUtils.newInstance().gson2List(bannerListJson, Banner::class.java))
                }
                bannerView.addBanners(bannerList)
                val message: Message = Message.obtain()
                message.what = BANNER_VIEW_IS_READY
                handler.sendMessage(message)
            }

            override fun onFailed(exception: Exception, request: Request) {
                //
            }
        })
    }


    private class MyHandler(val weakActivity: WeakReference<FragmentMain>) : Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            weakActivity.get()?.run {
                when (msg.what) {
                    BANNER_VIEW_IS_READY -> {
                        bannerView.initView()
                    }
                    COURSE_LIST_IS_READY -> paginationAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        /**
         * @return A new instance of fragment FragmentMain.
         */
        @JvmStatic
        fun newInstance() = FragmentMain()
    }
}