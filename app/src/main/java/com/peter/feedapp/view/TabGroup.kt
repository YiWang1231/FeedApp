package com.peter.feedapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.peter.feedapp.R

class TabGroup(private val context: Context) {
    lateinit var group: ViewGroup
    var tabList: MutableList<View> = ArrayList()
    private set

    fun addTabItem(tabItem: TabItem) {
        val view: View = initTab(tabItem)
        group.addView(view)
        tabList.add(view)
    }

    @SuppressLint("InflateParams")
    private fun initTab(tabItem: TabItem): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.tab_item, null)
        val imageView: ImageView = view.findViewById(R.id.tab_icon)
        val textView: TextView = view.findViewById(R.id.tab_title)
        imageView.setImageResource(tabItem.iconId)
        imageView.maxHeight = 36
        textView.setText(tabItem.titleId)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(120, 180)
        layoutParams.width = 0
        layoutParams.weight = 1F
        view.layoutParams = layoutParams
        return view
    }

    fun selectTab(index: Int) {
        for (i in tabList.indices) {
            tabList[i].isSelected = i == index
        }
    }
}