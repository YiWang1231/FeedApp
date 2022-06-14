package com.peter.feedapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peter.feedapp.databinding.FragmentMeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMe.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentMe : Fragment() {
    private var _binding: FragmentMeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * @return A new instance of fragment FragmentMe.
         */
        @JvmStatic
        fun newInstance() = FragmentMe().apply {}
    }
}