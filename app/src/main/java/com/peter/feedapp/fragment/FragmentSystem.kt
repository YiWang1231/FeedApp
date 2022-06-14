package com.peter.feedapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peter.feedapp.databinding.FragmentSystemBinding

class FragmentSystem : Fragment() {
    private var _binding: FragmentSystemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSystemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * @return A new instance of fragment FragmentSystem.
         */
        @JvmStatic
        fun newInstance() = FragmentSystem().apply {}
    }
}