package com.pratclot.di

import android.content.SharedPreferences
import com.apollographql.apollo.ApolloClient
import com.pratclot.core.YELP_API_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object ApolloModule {

    @ViewModelScoped
    @Provides
    fun provideSongsterrApi(okHttpClient: OkHttpClient): ApolloClient {
        return ApolloClient.builder()
            .serverUrl("https://api.yelp.com/v3/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }

    @ViewModelScoped
    @Provides
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(tokenInterceptor)
            .build()
    }
}

@Singleton
class TokenInterceptor @Inject constructor(val sharedPreferences: SharedPreferences) : Interceptor {
    var apiToken = sharedPreferences.getString(YELP_API_KEY, "") ?: ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("authorization", "bearer $apiToken")
            .build()

        return chain.proceed(request)
    }
}
