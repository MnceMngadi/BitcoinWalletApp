package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.walletscreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Component for the Bitcoin amount input field.
 * Allows the user to enter and update their BTC balance.
 */
@Composable
fun BtcInputSection(
    amount: String,
    onAmountChange: (String) -> Unit,
) {
    Column {
        Text(
            text = "BTC AMOUNT",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        TextField(
            value = amount,
            onValueChange = onAmountChange,
            placeholder = {
                Text(
                    text = "Enter Bitcoin amount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE5E7EB),
                    unfocusedContainerColor = Color(0xFFE5E7EB),
                    disabledContainerColor = Color(0xFFE5E7EB),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
        )
    }
}
