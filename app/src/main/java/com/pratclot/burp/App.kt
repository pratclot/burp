package com.pratclot.burp

import android.app.Application
import android.content.SharedPreferences
import com.pratclot.core.YELP_API_KEY
import com.pratclot.core.YELP_CLIENT_ID
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate() {
        super.onCreate()

        val savedKey = sharedPrefs.getString(YELP_API_KEY, "") ?: ""

        if (savedKey.isBlank()) {
            sharedPrefs.edit()
                .putString(YELP_API_KEY, "")
                .apply()
        }
    }
}
