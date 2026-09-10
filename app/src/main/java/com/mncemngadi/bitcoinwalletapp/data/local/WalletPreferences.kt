package com.mncemngadi.bitcoinwalletapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wallet_prefs")

@Singleton
class WalletPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val btcAmountKey = doublePreferencesKey("btc_amount")

    val btcAmount: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[btcAmountKey] ?: 0.0
    }

    suspend fun saveBtcAmount(amount: Double) {
        context.dataStore.edit { preferences ->
            preferences[btcAmountKey] = amount
        }
    }
}
