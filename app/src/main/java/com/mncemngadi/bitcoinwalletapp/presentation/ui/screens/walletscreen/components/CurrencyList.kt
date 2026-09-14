package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate

@Composable
fun CurrencyList(rates: List<CurrencyRate>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(rates) { rate ->
            CurrencyItem(rate = rate)
        }
    }
}
