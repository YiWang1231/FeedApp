package com.peter.feedapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.feedapp.R
import com.peter.feedapp.adapter.BannerPageAdapter
import com.peter.feedapp.adapter.PaginationAdapter
import com.peter.feedapp.adapter.PaginationScrollListener
import com.peter.feedapp.bean.Course
import com.peter.feedapp.biz.CourseBiz

private const val PAGE_TITLE = "title"
private const val PAGE_START = 0

class FragmentMain : Fragment() {
    private var title: String? = null
    private lateinit var recycleView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var paginationAdapter: PaginationAdapter
    private lateinit var bannerPageAdapter: BannerPageAdapter
    // 当前页面
    private var currentPage = PAGE_START
    // 全部页数 需请求接口后初始化
    private var totalPages = 0
    // 最后一页则停止加载
    private var isLastPage = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取bundle数据
        arguments?.let {
            title = it.getString(PAGE_TITLE)
        }
        // 监听事件
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        // 初始化
        recycleView = view.findViewById(R.id.article_list)
        paginationAdapter = PaginationAdapter(requireActivity())
        bannerPageAdapter = BannerPageAdapter(requireActivity())
        // 初始化BannerView
        // 获取课程数据
        loadFirstPage()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycleView.layoutManager = linearLayoutManager
        recycleView.adapter = ConcatAdapter(
            bannerPageAdapter,
            paginationAdapter
        )
        recycleView.addOnScrollListener(object: PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                currentPage += 1
                loadNextPage()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

        })
        return view
    }

    private fun loadNextPage() {
//        val courseList: MutableList<Course>
        CourseBiz.getCourses(currentPage, object: CourseBiz.Callback {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(courseList: MutableList<Course>) {
                paginationAdapter.courseList.addAll(courseList)
                paginationAdapter.notifyDataSetChanged()
                Log.e("Fragment", "successful")
            }
            override fun onFailed(exception: Exception) {
                Log.e("Fragment", "fail")
                return
            }
        })
        if (currentPage == totalPages) {
            isLastPage = true
        }
    }


    private fun loadFirstPage() {
//        val courseList: MutableList<Course>
        CourseBiz.getCourses(currentPage, object: CourseBiz.Callback {
            @SuppressLint("NotifyDataSetChanged")
            override fun onSuccess(courseList: MutableList<Course>) {
                paginationAdapter.courseList.addAll(courseList)
                totalPages = CourseBiz.totalPage
                paginationAdapter.notifyDataSetChanged()
                Log.e("Fragment", "successful")
            }
            override fun onFailed(exception: Exception) {
                Log.e("Fragment", "fail")
                return
            }

        })
    }

    companion object {
        /**
         * @param title Parameter 1.
         * @return A new instance of fragment FragmentMain.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(title: String) =
            FragmentMain().apply {
                arguments = Bundle().apply {
                    putString(PAGE_TITLE, title)
                }
            }
    }
}