package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import java.util.Locale

@Composable
fun CurrencyItem(rate: CurrencyRate) {
    val value = rate.totalValue

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
