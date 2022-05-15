package com.peter.feedapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.peter.feedapp.adapter.FragmentAdapter
import com.peter.feedapp.fragment.FragmentMain
import com.peter.feedapp.fragment.FragmentMe
import com.peter.feedapp.fragment.FragmentQuestion
import com.peter.feedapp.fragment.FragmentSystem
import com.peter.feedapp.model.TabGroup
import com.peter.feedapp.model.TabItem

class MainActivity : AppCompatActivity() {
    private val titleIDs = intArrayOf(
        R.string.main_page_title,
        R.string.question_page_title,
        R.string.system_page_title,
        R.string.me_page_title)
    private val iconResourcesIDs = intArrayOf(
        R.drawable.ic_bottom_bar_home,
        R.drawable.ic_bottom_bar_ques,
        R.drawable.ic_bottom_bar_navi,
        R.drawable.ic_bottom_bar_mine
    )
    private  var tabList: MutableList<View> = ArrayList()
    private lateinit var tabGroup: TabGroup
    private lateinit var mainFragment: FragmentMain
    private lateinit var questionFragment : FragmentQuestion
    private lateinit var systemFragment : FragmentSystem
    private lateinit var meFragment : FragmentMe
    private  var fragments: MutableList<Fragment> = ArrayList()
    private lateinit var containerContent : ViewPager
    private lateinit var mainNaviTab : LinearLayout
    private lateinit var questionNaviTab : LinearLayout
    private lateinit var systemNaviTab : LinearLayout
    private lateinit var meNaviTab : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setTabClickListener()
    }

    /**
     * 初始化界面view
     */
    private fun initView() {
        containerContent = findViewById(R.id.container_content)
        initTabLayout()
        initFragments()
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
        containerContent.adapter = adapter
        // TODO 设定监听实现与tab联动
        containerContent.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //TODO("Not yet implemented")
            }

            override fun onPageSelected(position: Int) {
                tabGroup.selectTab(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                //TODO("Not yet implemented")
            }

        })
    }

    /***
     * 初始化Fragment
     */
    private fun initFragments() {
        // 初始化Fragment
        mainFragment = FragmentMain.newInstance(getString(titleIDs[0]))
        questionFragment = FragmentQuestion.newInstance(getString(titleIDs[1]))
        systemFragment = FragmentSystem.newInstance(getString(titleIDs[2]))
        meFragment = FragmentMe.newInstance(getString(titleIDs[3]))
        fragments.add(mainFragment)
        fragments.add(questionFragment)
        fragments.add(systemFragment)
        fragments.add(meFragment)
    }

    private fun initTabLayout() {
        tabGroup = TabGroup(this)
        tabGroup.group = findViewById(R.id.page_navi)
        for (index in titleIDs.indices) {
            val tabItem: TabItem = TabItem(titleIDs[index], iconResourcesIDs[index])
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
                containerContent.setCurrentItem(index, true)
            }
        }
    }
}

