package com.pratclot.burp.ui.dashboard

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pratclot.core.YELP_API_KEY
import com.pratclot.di.TokenInterceptor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(val sharedPreferences: SharedPreferences, val tokenInterceptor: TokenInterceptor) :
    ViewModel() {

    fun setApiKey(it: String) {
        sharedPreferences.edit()
            .putString(YELP_API_KEY, it)
            .apply()

        tokenInterceptor.apiToken = it
        _keySaved.value = true
    }

    private val _keySaved = MutableLiveData<Boolean>()
    val keySaved: LiveData<Boolean> = _keySaved
}