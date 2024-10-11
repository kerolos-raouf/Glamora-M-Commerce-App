package com.example.glamora.di

import android.content.Context
import android.content.SharedPreferences
import com.example.glamora.data.contracts.SettingsHandler
import com.example.glamora.data.sharedPref.SharedPrefHandler
import com.example.glamora.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SharedPrefModule {

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context) : SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)


    @Provides
    @Singleton
    fun provideSharedPrefHandler(sharedPreferences: SharedPreferences) : SettingsHandler {
        return SharedPrefHandler(sharedPreferences)
    }

}