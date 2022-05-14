package com.peter.feedapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.peter.feedapp.adapter.FragmentAdapter
import com.peter.feedapp.fragment.FragmentMain
import com.peter.feedapp.fragment.FragmentMe
import com.peter.feedapp.fragment.FragmentQuestion
import com.peter.feedapp.fragment.FragmentSystem

class MainActivity : AppCompatActivity() {
    private val titleIDs = intArrayOf(
        R.string.main_page_title,
        R.string.question_page_title,
        R.string.system_page_title,
        R.string.me_page_title)
    private lateinit var mainFragment : FragmentMain
    private lateinit var questionFragment : FragmentQuestion
    private lateinit var systemFragment : FragmentSystem
    private lateinit var meFragment : FragmentMe
    private  var fragments : MutableList<Fragment> = ArrayList()
    private lateinit var containerContent : ViewPager
    private lateinit var mainNaviTab : LinearLayout
    private lateinit var questionNaviTab : LinearLayout
    private lateinit var systemNaviTab : LinearLayout
    private lateinit var meNaviTab : LinearLayout
    private  var tabGroup : MutableList<LinearLayout> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initFragments()
        initContent()
        setClickListener()
    }

    /**
     * 初始化界面view
     */
    private fun initView() {
        containerContent = findViewById(R.id.container_content)
        mainNaviTab = findViewById(R.id.navi_main)
        questionNaviTab = findViewById(R.id.navi_question)
        systemNaviTab = findViewById(R.id.navi_system)
        meNaviTab = findViewById(R.id.navi_me)
        tabGroup.add(mainNaviTab)
        tabGroup.add(questionNaviTab)
        tabGroup.add(systemNaviTab)
        tabGroup.add(meNaviTab)
        // 初始化选定主页tab
        selectItem(0)
    }

    /**
     * 1. 填充页面内容
     * 2. 监听viewPager滑动事件并修改tab选中状态
     */
    private fun initContent() {
        val adapter = FragmentAdapter(supportFragmentManager, fragments)
        containerContent.adapter = adapter
        // TODO 设定监听实现与tab联动
    }

    /***
     * 选中tab 修改样式
     */
    private fun selectItem(pos: Int) {
        for ( index in tabGroup.indices) {
            tabGroup[index].isSelected = index == pos
        }
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

    /***
     * 点击监听
     */
    private fun setClickListener() {
        for ((index, tabItem) in tabGroup.withIndex()) {
            tabItem.setOnClickListener {
                selectItem(index)
                containerContent.setCurrentItem(index, true)
            }
        }
    }
}

