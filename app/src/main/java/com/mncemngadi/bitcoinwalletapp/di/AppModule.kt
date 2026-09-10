package com.mncemngadi.bitcoinwalletapp.di

import com.mncemngadi.bitcoinwalletapp.data.repository.WalletRepositoryImpl
import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: WalletRepositoryImpl
    ): WalletRepository
}
