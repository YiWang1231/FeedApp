package com.peter.feedapp.view

import androidx.fragment.app.Fragment
import com.peter.feedapp.R
import com.peter.feedapp.fragment.FragmentMain
import com.peter.feedapp.fragment.FragmentMe
import com.peter.feedapp.fragment.FragmentQuestion
import com.peter.feedapp.fragment.FragmentSystem

enum class PageEnum(val titleId: Int, val iconId: Int, val fragmentLazy: () -> Fragment) {
    Main(R.string.main_page_title, R.drawable.ic_bottom_bar_home, { FragmentMain.newInstance() }),
    Ques(R.string.question_page_title, R.drawable.ic_bottom_bar_ques, { FragmentQuestion.newInstance() }),
    System(R.string.system_page_title, R.drawable.ic_bottom_bar_navi, { FragmentSystem.newInstance() }),
    Me(R.string.me_page_title, R.drawable.ic_bottom_bar_mine, { FragmentMe.newInstance() })
}