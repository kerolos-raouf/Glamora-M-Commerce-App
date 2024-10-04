package com.example.glamora.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.example.glamora.BuildConfig
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.network.ApolloClientInterceptor
import com.example.glamora.data.network.RetrofitHandler
import com.example.glamora.data.network.RetrofitInterface
import com.example.glamora.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    @Singleton
    fun provideApolloClient() : ApolloClient{

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ApolloClientInterceptor(BuildConfig.ADMIN_API_ACCESS_TOKEN))
            .build()
        return ApolloClient.Builder()
            .serverUrl(Constants.GRAPHQL_BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitInterface() : RetrofitInterface {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ApolloClientInterceptor(BuildConfig.ADMIN_API_ACCESS_TOKEN))
            .build()
        return Retrofit.Builder()
                .baseUrl(Constants.RETROFIT_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitInterface::class.java)
    }




}