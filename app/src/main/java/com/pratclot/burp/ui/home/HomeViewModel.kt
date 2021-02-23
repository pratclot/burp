package com.pratclot.burp.ui.home

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.apollographql.apollo.ApolloClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.pratclot.burp.FindSomeQuery
import com.pratclot.core.YELP_API_KEY
import com.pratclot.di.LocationKeeper
import com.pratclot.repo.YelpPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(sharedPreferences: SharedPreferences) : ViewModel() {
    companion object {
        const val TAG = "HomeViewModel"
    }

    @Inject
    lateinit var apolloClient: ApolloClient

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _requestLocationPermissions = MutableLiveData<Boolean>()
    val requestLocationPermissions: LiveData<Boolean>
        get() = _requestLocationPermissions

    private val _showTokenAbsentMessage = MutableLiveData<Boolean>()
    val showTokenAbsentMessage: LiveData<Boolean>
        get() = _showTokenAbsentMessage

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = _currentLocation

    private val _businesses = MutableLiveData<List<FindSomeQuery.Business>>()
    val businesses: LiveData<List<FindSomeQuery.Business>>
        get() = _businesses

    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 1)
    ) {
        YelpPagingSource(apolloClient, 3, LocationKeeper.currentLocation)
    }
        .flow
        .cachedIn(viewModelScope)

    init {
        _currentLocation.value = LocationKeeper.currentLocation

             val token = sharedPreferences.getString(YELP_API_KEY, "")
        if (token.isNullOrBlank()) _showTokenAbsentMessage.value = true
    }

    fun getCurrentLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            _requestLocationPermissions.value = true
        }
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun isCancellationRequested(): Boolean {
                    return true
                }

                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                    return this
                }

            }
        ).addOnSuccessListener {
            LocationKeeper.currentLocation = it
        }
    }

    fun fragmentHandledPermissionRequest() {
        _requestLocationPermissions.value = false
    }

//    fun getBusinesses(it: Location) {
//        uiScope.launch {
//            val response = apolloClient.query(FindSomeQuery())
//                .await().data?.search?.business?.filterNotNull()?.let {
//                    _businesses.value = it
//                }
//        }
//    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}