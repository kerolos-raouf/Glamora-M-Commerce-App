package com.example.glamora.di

import com.apollographql.apollo.ApolloClient
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.network.RetrofitHandler
import com.example.glamora.data.network.RetrofitInterface
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.data.sharedPref.SharedPrefHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofitInterface: RetrofitInterface) : RemoteDataSource {
        return RetrofitHandler(retrofitInterface)
    }

    @Provides
    @Singleton
    fun provideRepository(
        apolloClient: ApolloClient,
        remoteDataSource: RemoteDataSource,
        sharedPrefHandler: SharedPrefHandler
    ): Repository {
        return RepositoryImpl(apolloClient,remoteDataSource,sharedPrefHandler)
    }

}