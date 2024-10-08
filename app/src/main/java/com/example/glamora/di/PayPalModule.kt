package com.example.glamora.di

import com.example.glamora.data.network.payPalService.PayPalService
import com.example.glamora.data.repository.payPalRepo.IPayPalRepository
import com.example.glamora.data.repository.payPalRepo.PayPalRepository
import com.example.glamora.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PayPalModule {

    @Provides
    @Singleton
    fun providePayPalService(): PayPalService {
        return Retrofit.Builder()
            .baseUrl(Constants.PAYPAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PayPalService::class.java)
    }

    @Provides
    @Singleton
    fun providePayPalRepository(payPalService: PayPalService): IPayPalRepository {
        return PayPalRepository(payPalService)
    }


}