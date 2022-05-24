package com.peter.feedapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.peter.feedapp.adapter.FragmentAdapter
import com.peter.feedapp.view.TabGroup
import com.peter.feedapp.view.TabItem
import com.peter.feedapp.databinding.ActivityMainBinding
import com.peter.feedapp.view.PageEnum

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tabGroup: TabGroup
    private  var fragments = PageEnum.values().map { it.fragmentLazy }
    private val pageEnumValues = PageEnum.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()
        setTabClickListener()
    }

    /**
     * 初始化界面view
     */
    private fun initView() {
        initPages()
        initContent()
        // 初始化选定主页tab
        tabGroup.selectTab(0)
    }

    /**
     * 1. 填充页面内容
     * 2. 监听viewPager滑动事件并修改tab选中状态
     */
    private fun initContent() {
        val adapter = FragmentAdapter(supportFragmentManager, fragments)
        binding.containerContent.adapter = adapter
        // TODO 设定监听实现与tab联动
        binding.containerContent.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                tabGroup.selectTab(position)
                binding.pageTitle.text = getString(PageEnum.values()[position].titleId)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

    /***
     * 初始化Fragment及tabGroup
     */
    private fun initPages() {
        tabGroup = TabGroup(this)
        tabGroup.group = binding.pageNavi
        pageEnumValues.forEach {
//            fragments.add(it.fragmentLazy)
            val tabItem = TabItem(it.titleId, it.iconId)
            tabGroup.addTabItem(tabItem)
        }
    }

    /***
     * 点击监听
     */
    private fun setTabClickListener() {
        for ((index, tabItem) in tabGroup.tabList.withIndex()) {
            tabItem.setOnClickListener {
                tabGroup.selectTab(index)
                binding.pageTitle.text = getString(pageEnumValues[index].titleId)
                binding.containerContent.setCurrentItem(index, true)
            }
        }
    }
}

