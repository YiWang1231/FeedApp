package com.peter.feedapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.peter.feedapp.utils.ApiCallback
import com.peter.feedapp.utils.HttpUtils
import com.peter.feedapp.view.BannerView

private const val PAGE_START = 0
private const val BANNER_API = "https://www.wanandroid.com/banner/json"
private const val TOP_COURSE_LIST_API = "https://www.wanandroid.com/article/top/json"

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        // 初始化
        bannerView = BannerView(requireContext(), object : BannerView.OnBannerClickListener {
            override fun onClick(banner: Banner) {
                val intent = Intent(requireContext(), CourseActivity::class.java)
                intent.putExtra("url", banner.url)
                requireContext().startActivity(intent)
            }

        })
        loadingAdapter = LoadingAdapter(requireContext(), object : LoadingAdapter.FailedCallback {
            override fun onClick() {
                loadCourse()
            }
        })
        // 初始化隐藏loadingBar
        loadingAdapter.setState(LoadingStatusEnum.EMPTY_VIEW)
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
        HttpUtils.get().url(TOP_COURSE_LIST_API).build().enqueue(HttpUtils.typeToken<ApiResponse<MutableList<Course>>>(), object : ApiCallback<MutableList<Course>>() {
            override fun onFiled(exception: Exception) {
                //
            }

            override fun onReqSuccess(ret: MutableList<Course>) {
                courseList.addAll(CourseBiz.parseCourseContent(ret))
                paginationAdapter.addCourses(courseList)
                loadCourse()
            }

        })
    }

    private fun loadCourse() {
        loadingAdapter.setState(LoadingStatusEnum.STATUS_LOADING)
        val courseList: MutableList<Course> = ArrayList()
        val courseListApi = "https://www.wanandroid.com/article/list/$currentPage/json"
        HttpUtils.get().url(courseListApi).build().enqueue(HttpUtils.typeToken<ApiResponse<CourseResponse>>(), object : ApiCallback<CourseResponse>() {
            override fun onFiled(exception: Exception) {
                loadingAdapter.setState(LoadingStatusEnum.STATUS_FAILED)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onReqSuccess(ret: CourseResponse) {
                courseTotalPages = ret.pageCount?:0
                if (currentPage < courseTotalPages) {
                    currentPage += 1
                } else {
                    isLastPage = true
                }
                if (!isLastPage) {
                    courseList.addAll(CourseBiz.parseCourseContent(ret.datas))
                    paginationAdapter.addCourses(courseList)
                    loadingAdapter.setState(LoadingStatusEnum.EMPTY_VIEW)
                } else {
                    loadingAdapter.setState(LoadingStatusEnum.STATUS_FINISHED)
                }
                paginationAdapter.notifyDataSetChanged()
            }

        })
    }

    private fun loadBanners() {
        HttpUtils.get().url(BANNER_API).build().enqueue(HttpUtils.typeToken<ApiResponse<List<Banner>>>(), object : ApiCallback<List<Banner>>() {
            override fun onFiled(exception: Exception) {
                //
            }

            override fun onReqSuccess(ret: List<Banner>) {
                bannerView.addBanners(ret)
                bannerView.initView()
            }

        })
    }

    companion object {
        /**
         * @return A new instance of fragment FragmentMain.
         */
        @JvmStatic
        fun newInstance() = FragmentMain()
    }
}