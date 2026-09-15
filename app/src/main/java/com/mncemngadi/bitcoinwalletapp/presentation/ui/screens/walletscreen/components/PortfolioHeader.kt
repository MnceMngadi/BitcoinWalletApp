package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mncemngadi.bitcoinwalletapp.presentation.ui.theme.TextSecondary

/**
 * Component for the portfolio balance header.
 */
@Composable
fun PortfolioHeader(btcAmount: String) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Your portfolio",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = TextSecondary,
            )
        }
        Text(
            text = "BTC $btcAmount",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
