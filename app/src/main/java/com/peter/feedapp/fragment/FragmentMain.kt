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
import com.peter.feedapp.adapter.*
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
private const val LOADING_VISIBLE_CHANGED = 900000

class FragmentMain : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var paginationAdapter: PaginationAdapter
    private lateinit var bannerPageAdapter: BannerPageAdapter
    private lateinit var loadingAdapter: LoadingAdapter
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
        loadingAdapter = LoadingAdapter(requireContext())
        paginationAdapter = PaginationAdapter(requireContext())
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.articleList.layoutManager = linearLayoutManager
        // 配置adapter
        bannerPageAdapter = BannerPageAdapter(bannerView)
        binding.articleList.adapter = ConcatAdapter(
            bannerPageAdapter,
            paginationAdapter,
            loadingAdapter
        )
        // 获取课程数据
        loadFirstPageCourse()
        // 获取banner数据
        loadBanners()
        // 为recycleView绑定滚动监听器
        binding.articleList.addOnScrollListener(object: PaginationScrollListener(linearLayoutManager) {
            override fun onScrollToBottom() {
                if (!isLastPage && currentPage != 0) {
                    loadCourse()
                } else if (isLastPage) {
                    loadingAdapter.setLoadingStatus(LoadingStatusEnum.STATUS_FINISHED.statusCode, null)
                }
            }
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // fragment存在时间比其他视图长， 需要清除对绑定实例的所有引用
        _binding = null
    }

    private fun loadFirstPageCourse() {
        val courseList: MutableList<Course> = ArrayList()
        HttpUtils.get().url(TOP_COURSE_LIST_API).build().enqueue(object : JsonDataBaseCallback {
            override fun onSuccess(result: String) {
                val jsonArrayBase = GsonUtils.newInstance().fromJsonArray(result, Course::class.java)
                if (jsonArrayBase.errorCode == 0) {
                    courseList.addAll(CourseBiz.parseCourseContent(jsonArrayBase))
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
        HttpUtils.get().url(courseListApi).build().enqueue(object : JsonDataBaseCallback {
            override fun onSuccess(result: String) {
                loadingAdapter.setLoadingShowState(true)
                loadingAdapter.setLoadingStatus(LoadingStatusEnum.STATUS_LOADING.statusCode, null)
                val messageLoad: Message = Message.obtain()
                messageLoad.what = LOADING_VISIBLE_CHANGED
                handler.sendMessage(messageLoad)
                val database = GsonUtils.newInstance().gson2Bean(result, JsonObjectBase::class.java)
                if (database.errorCode == 0) {
                    val jsonArrayBase = GsonUtils.newInstance().fromJsonArray(database.data.toString(), Course::class.java)
                    courseTotalPages = jsonArrayBase.pageCount
                    if (currentPage < courseTotalPages) {
                        currentPage += 1
                    } else {
                        isLastPage = true
                    }
                    courseList.addAll(CourseBiz.parseCourseContent(jsonArrayBase))
                    paginationAdapter.addCourses(courseList)
                    val messageCourse = Message.obtain()
                    messageCourse.what = COURSE_LIST_IS_READY
                    handler.sendMessage(messageCourse)
                }
//                loadingAdapter.setLoadingShowState(false)
            }

            override fun onFailed(exception: Exception, request: Request) {
                loadingAdapter.setLoadingStatus(LoadingStatusEnum.STATUS_FAILED.statusCode, null)
                loadingAdapter.styleView?.setOnClickListener(View.OnClickListener {
                    loadCourse()
                })
            }

        })
    }

    private fun loadBanners() {
        val bannerList: MutableList<Banner> = ArrayList()
        HttpUtils.get().url(BANNER_API).build().enqueue(object : JsonDataBaseCallback {
            override fun onSuccess(result: String) {
                val jsonArrayBase = GsonUtils.newInstance().fromJsonArray(result, Banner::class.java)
                if (jsonArrayBase.errorCode == 0) {
                    bannerList.addAll(jsonArrayBase.data)
                }
                bannerView.addBanners(bannerList)
                val messageBanner: Message = Message.obtain()
                messageBanner.what = BANNER_VIEW_IS_READY
                handler.sendMessage(messageBanner)
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
                    COURSE_LIST_IS_READY -> {
                        paginationAdapter.notifyDataSetChanged()
                    }
                    LOADING_VISIBLE_CHANGED -> {
                        println("change")
                        loadingAdapter.notifyDataSetChanged()
                    }
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