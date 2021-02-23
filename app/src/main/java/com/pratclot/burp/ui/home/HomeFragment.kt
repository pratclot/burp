package com.pratclot.burp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingSource
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratclot.burp.FindSomeQuery
import com.pratclot.burp.databinding.FragmentHomeBinding
import com.pratclot.core.HandyFragment
import com.pratclot.core.PERMISSIONS_REQUEST_CODE_FINE_LOCATION
import com.pratclot.core.recycler.BusinessAdapter
import com.pratclot.core.shorten
import com.pratclot.di.LocationKeeper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : HandyFragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val businessAdapter = BusinessAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.requestLocationPermissions.observe(viewLifecycleOwner) {
            if (it) {
                homeViewModel.fragmentHandledPermissionRequest()
                requireActivity().requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_CODE_FINE_LOCATION
                )
            }
        }

        binding.recyclerViewFragmentHome.apply {
            adapter = businessAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

//        homeViewModel.currentLocation.observe(viewLifecycleOwner) {
//            homeViewModel.getBusinesses(it)
//        }

        lifecycleScope.launch {
            homeViewModel.flow
                .collectLatest { pagingData ->
                    if (pagingData is PagingSource.LoadResult.Error<*, *>) showToast("Token is no good!")
                    businessAdapter.submitData(pagingData)
                }
        }

        LocationKeeper.currentLocation.let {
            binding.textViewFragmentHomeLocation.text =
                "Location is ${it.latitude.shorten()}, ${it.longitude.shorten()}"
        }

        homeViewModel.showTokenAbsentMessage.observe(viewLifecycleOwner) {
            if (it) showToast("Please supply your Yelp token via Dashboard Fragment.")
        }

        return root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() and (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                    showToast("Location permission is missing!")
                }
            }
            else -> {
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel.getCurrentLocation(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}