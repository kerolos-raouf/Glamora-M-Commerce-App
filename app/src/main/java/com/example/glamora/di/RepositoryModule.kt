package com.example.glamora.di

import com.apollographql.apollo.ApolloClient
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.repository.RepositoryImpl
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
    fun provideRepository(apolloClient: ApolloClient): Repository {
        return RepositoryImpl(apolloClient)
    }

}