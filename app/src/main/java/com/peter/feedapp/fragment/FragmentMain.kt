package com.peter.feedapp.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.feedapp.CourseActivity
import com.peter.feedapp.adapter.BannerPageAdapter
import com.peter.feedapp.adapter.LoadingAdapter
import com.peter.feedapp.adapter.LoadingStatusEnum
import com.peter.feedapp.adapter.PaginationAdapter
import com.peter.feedapp.adapter.PaginationScrollListener
import com.peter.feedapp.bean.ApiResponse
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.bean.Course
import com.peter.feedapp.bean.CourseDataBase
import com.peter.feedapp.biz.CourseBiz
import com.peter.feedapp.databinding.FragmentMainBinding
import com.peter.feedapp.utils.ApiCallback
import com.peter.feedapp.utils.HttpUtils
import com.peter.feedapp.utils.HttpUtils.Companion.typeToken
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
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        bannerView = BannerView(requireContext(), object : BannerView.OnBannerClickListener {
            override fun onClick(banner: Banner) {
                val intent = Intent(requireContext(), CourseActivity::class.java)
                intent.putExtra("url", banner.url)
                requireContext().startActivity(intent)
            }

        })
        loadingAdapter = LoadingAdapter(requireContext())
        paginationAdapter = PaginationAdapter(requireContext())
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
        binding.articleList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun onScrollToBottom() {
                if (!isLastPage && currentPage != 0) {
                    loadCourse()
                } else if (isLastPage) {
                    loadingAdapter.setLoadingStatus(
                        LoadingStatusEnum.STATUS_FINISHED.statusCode,
                        null
                    )
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
        HttpUtils.get().url(TOP_COURSE_LIST_API).build()
            .enqueue(typeToken<ApiResponse<MutableList<Course>>>(), object :
                ApiCallback<MutableList<Course>>() {
                override fun onReqSuccess(ret: MutableList<Course>) {
                    courseList.addAll(CourseBiz.parseCourseContent(ret))
                    paginationAdapter.addCourses(courseList)
                    loadCourse()
                }

                override fun onFailed(exception: Exception) {

                }
            })
    }

    private fun loadCourse() {
        val courseList: MutableList<Course> = ArrayList()
        val courseListApi = "https://www.wanandroid.com/article/list/$currentPage/json"
        HttpUtils.get().url(courseListApi).build()
            .enqueue(typeToken<ApiResponse<CourseDataBase>>(), object :
                ApiCallback<CourseDataBase>() {

                override fun onFailed(exception: Exception) {
                    loadingAdapter.setLoadingStatus(
                        LoadingStatusEnum.STATUS_FAILED.statusCode,
                        null
                    )
                    loadingAdapter.styleView?.setOnClickListener(View.OnClickListener {
                        loadCourse()
                    })
                }

                override fun onReqSuccess(ret: CourseDataBase) {
                    loadingAdapter.setLoadingShowState(true)
                    loadingAdapter.setLoadingStatus(
                        LoadingStatusEnum.STATUS_LOADING.statusCode,
                        null
                    )
                    courseTotalPages = ret.pageCount ?: 0
                    if (currentPage < courseTotalPages) {
                        currentPage += 1
                    } else {
                        isLastPage = true
                    }
                    courseList.addAll(CourseBiz.parseCourseContent(ret.datas))
                    paginationAdapter.addCourses(courseList)
                    paginationAdapter.notifyDataSetChanged()
                    loadingAdapter.notifyDataSetChanged()
//                loadingAdapter.setLoadingShowState(false)
                }

            })
    }

    private fun loadBanners() {
        HttpUtils.get().url(BANNER_API).build().enqueue(typeToken<ApiResponse<List<Banner>>>(), object : ApiCallback<List<Banner>>() {

            override fun onFailed(exception: Exception) {
            }

            override fun onReqSuccess(ret: List<Banner>) {
                bannerView.setBannerData(ret)
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