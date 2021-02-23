package com.pratclot.burp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pratclot.burp.R
import com.pratclot.burp.databinding.FragmentDashboardBinding
import com.pratclot.core.HandyFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : HandyFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            buttonFragmentDashboardSaveKey.setOnClickListener {
                dashboardViewModel.setApiKey(textInputFragmentDashboardApiKey.text.toString())
            }
        }

        dashboardViewModel.keySaved.observe(viewLifecycleOwner) {
            if (it) {
                showToast("Key saved!")
                binding.textInputFragmentDashboardApiKey.editableText.clear()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}