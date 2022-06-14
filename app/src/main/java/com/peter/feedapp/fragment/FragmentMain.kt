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
import com.peter.feedapp.adapter.BannerPageAdapter
import com.peter.feedapp.adapter.PaginationAdapter
import com.peter.feedapp.adapter.PaginationScrollListener
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.bean.BannerDataBase
import com.peter.feedapp.bean.Course
import com.peter.feedapp.bean.NetDataBase
import com.peter.feedapp.biz.BannerBiz
import com.peter.feedapp.biz.Callback
import com.peter.feedapp.biz.CourseBiz
import com.peter.feedapp.databinding.FragmentMainBinding
import com.peter.feedapp.utils.GsonUtils
import com.peter.feedapp.view.BannerView

private const val PAGE_START = 0

class FragmentMain : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var paginationAdapter: PaginationAdapter
    private lateinit var bannerPageAdapter: BannerPageAdapter
    private lateinit var bannerDataBase: BannerDataBase
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
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        // 初始化
        paginationAdapter = PaginationAdapter(requireContext())
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.articleList.layoutManager = linearLayoutManager
        // 获取课程数据
        loadCourseData()
        // 获取banner数据并为recycleView绑定adapter
        loadBannerData()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // fragment存在时间比其他视图长， 需要清除对绑定实例的所有引用
        _binding = null
    }


    private fun loadCourseData() {
        CourseBiz().getCourses(currentPage, object: CourseBiz.Callback {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(courseList: MutableList<Course>, totalPages: Int) {
                // 请求成功页数 + 1
                currentPage += 1
                paginationAdapter.addCourses(courseList)
                if (courseTotalPages == 0) {
                    courseTotalPages = totalPages
                }
                // 判断是否最后一页
                if (currentPage > courseTotalPages) {
                    isLastPage = true
                }
                paginationAdapter.notifyDataSetChanged()
            }
            override fun onFailed(exception: Exception) {
                println(exception)
                return
            }
        })
    }

    private fun loadBannerData() {
        BannerBiz().getBannersTask(object: Callback<BannerDataBase> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(database: BannerDataBase) {
                bannerDataBase = database
                bannerView = BannerView(requireContext(), bannerDataBase, object : BannerView.OnBannerClickListener {
                    override fun onClick(banner: Banner) {
                        val intent = Intent(requireContext(), CourseActivity::class.java)
                        intent.putExtra("url", banner.url)
                        requireContext().startActivity(intent)
                    }

                })
                bannerPageAdapter = BannerPageAdapter(bannerView)
                binding.articleList.adapter = ConcatAdapter(
                    bannerPageAdapter,
                    paginationAdapter
                )
                binding.articleList.addOnScrollListener(object: PaginationScrollListener(linearLayoutManager) {
                    override fun onScrollToBottom() {
                        if (!isLastPage) {
                            println("加载新数据了")
                            loadCourseData()
                        }
                    }
                })
            }

            override fun onFailed(exception: Exception) {

            }

            override fun parseContent(content: String): BannerDataBase {
                val bannerList: MutableList<Banner> = ArrayList()
                val netDataBase: NetDataBase<Banner> =
                    GsonUtils.gsonProvider.fromJson<NetDataBase<Banner>>(content, NetDataBase::class.java)
                if (netDataBase.errorCode == 0) {
                    val dataArray = GsonUtils.newInstance().bean2Json(netDataBase.data)
                    bannerList.addAll(GsonUtils.newInstance().gson2List(dataArray, Banner::class.java))
                }
                return  BannerDataBase(bannerList)
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