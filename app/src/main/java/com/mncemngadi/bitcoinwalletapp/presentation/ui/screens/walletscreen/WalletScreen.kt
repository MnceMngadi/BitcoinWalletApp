package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components.ErrorDialog
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components.LoadingDialog
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components.MyTopAppBar
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen.components.BtcInputSection
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen.components.CurrencyList
import com.mncemngadi.bitcoinwalletapp.presentation.ui.viewmodel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(viewModel: WalletViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LoadingDialog(isLoading = uiState.isLoading)

    ErrorDialog(
        errorMessage = uiState.error,
        onDismiss = { viewModel.clearError() },
        onRetry = { viewModel.refresh() },
    )

    Scaffold(
        topBar = {
            MyTopAppBar(title = "Bitcoin Wallet", onRefreshClick = { viewModel.refresh() })
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            BtcInputSection(
                amount = uiState.btcAmount,
                onAmountChange = { viewModel.onBtcAmountChange(it) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Currency Values",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            CurrencyList(
                rates = uiState.rates,
            )
        }
    }
}
