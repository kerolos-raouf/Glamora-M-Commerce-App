package com.example.glamora.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.example.glamora.data.apolloHandler.ApolloClientHandler
import com.example.glamora.data.contracts.IApolloClient
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.firebase.FirebaseHandler
import com.example.glamora.data.firebase.IFirebaseHandler
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.internetStateObserver.InternetStateObserver
import com.example.glamora.data.network.CitiesSearchApi
import com.example.glamora.data.network.CurrencyApi
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
    fun provideFirebaseHandler() : IFirebaseHandler = FirebaseHandler()


    @Provides
    @Singleton
    fun provideApolloClientHandler(apolloClient: ApolloClient) : IApolloClient = ApolloClientHandler(apolloClient)

    @Provides
    @Singleton
    fun provideRepository(
        apolloClientHandler: IApolloClient,
        remoteDataSource: RemoteDataSource,
        sharedPrefHandler: SharedPrefHandler,
        connectivityObserver: ConnectivityObserver,
        firebaseHandler: IFirebaseHandler
    ): Repository {
        return RepositoryImpl(apolloClientHandler,remoteDataSource,sharedPrefHandler,connectivityObserver,firebaseHandler)
    }

}