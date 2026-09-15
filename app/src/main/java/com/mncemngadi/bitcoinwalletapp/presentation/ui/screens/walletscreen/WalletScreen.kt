package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen.components.CurrencyItem
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen.components.PortfolioHeader
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
        LazyColumn(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
        ) {
            item {
                PortfolioHeader(btcAmount = uiState.btcAmount)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                BtcInputSection(
                    amount = uiState.btcAmount,
                    onAmountChange = { viewModel.onBtcAmountChange(it) },
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Currency Values",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.rates) { rate ->
                CurrencyItem(rate = rate)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
