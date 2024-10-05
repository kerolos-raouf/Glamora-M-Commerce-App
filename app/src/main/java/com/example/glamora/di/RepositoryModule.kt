package com.example.glamora.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.internetStateObserver.InternetStateObserver
import com.example.glamora.data.network.CitiesSearchApi
import com.example.glamora.data.network.RetrofitHandler
import com.example.glamora.data.network.RetrofitInterface
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.data.sharedPref.SharedPrefHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideInternetStateObserver(@ApplicationContext context : Context) : ConnectivityObserver = InternetStateObserver(context)


    @Provides
    @Singleton
    fun provideRemoteDataSource(retrofitInterface: RetrofitInterface,citiesSearchApi: CitiesSearchApi,currencyApi: CurrencyApi) : RemoteDataSource {
        return RetrofitHandler(retrofitInterface,citiesSearchApi,currencyApi)
    }

    @Provides
    @Singleton
    fun provideRepository(
        apolloClient: ApolloClient,
        remoteDataSource: RemoteDataSource,
        sharedPrefHandler: SharedPrefHandler,
        connectivityObserver: ConnectivityObserver
    ): Repository {
        return RepositoryImpl(apolloClient,remoteDataSource,sharedPrefHandler,connectivityObserver)
    }

}