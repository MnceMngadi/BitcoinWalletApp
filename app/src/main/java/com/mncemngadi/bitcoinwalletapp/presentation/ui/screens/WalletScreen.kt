package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components.ErrorDialog
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components.LoadingDialog
import com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components.MyTopAppBar
import com.mncemngadi.bitcoinwalletapp.presentation.ui.viewmodel.WalletViewModel
import java.util.Locale

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
                btcAmount = uiState.btcAmount.toDoubleOrNull() ?: 0.0,
            )
        }
    }
}

@Composable
fun BtcInputSection(
    amount: String,
    onAmountChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        label = { Text("Bitcoin Amount (BTC)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
    )
}

@Composable
fun CurrencyList(
    rates: List<CurrencyRate>,
    btcAmount: Double,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(rates) { rate ->
            CurrencyItem(rate = rate, btcAmount = btcAmount)
        }
    }
}

@Composable
fun CurrencyItem(
    rate: CurrencyRate,
    btcAmount: Double,
) {
    val value = btcAmount * rate.rate

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = rate.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${rate.code} ${String.format(Locale.US, "%.2f", value)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (rate.fluctuation != null) {
                FluctuationIndicator(change = rate.fluctuation)
            }
        }
    }
}

@Composable
fun FluctuationIndicator(change: Double) {
    val color =
        when {
            change > 0 -> Color(0xFF4CAF50)
            change < 0 -> Color(0xFFF44336)
            else -> Color.Gray
        }

    val icon =
        when {
            change > 0 -> Icons.Default.ArrowUpward
            change < 0 -> Icons.Default.ArrowDownward
            else -> null
        }

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = "${String.format(Locale.US, "%.2f", change)}%",
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}
