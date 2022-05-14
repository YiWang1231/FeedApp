package com.peter.feedapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peter.feedapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PAGE_TITLE = "title"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMe.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentMe : Fragment() {
    // TODO: Rename and change types of parameters
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(PAGE_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param title 页面名称
         * @return A new instance of fragment FragmentMe.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(title: String) =
            FragmentMe().apply {
                arguments = Bundle().apply {
                    putString(PAGE_TITLE, title)
                }
            }
    }
}