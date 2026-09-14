package com.mncemngadi.bitcoinwalletapp.di

import com.mncemngadi.bitcoinwalletapp.data.repository.WalletRepositoryImpl
import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository implementations to their interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    /**
     * Binds the WalletRepository interface to its implementation.
     */
    @Binds
    @Singleton
    abstract fun bindWalletRepository(walletRepositoryImpl: WalletRepositoryImpl): WalletRepository
}
