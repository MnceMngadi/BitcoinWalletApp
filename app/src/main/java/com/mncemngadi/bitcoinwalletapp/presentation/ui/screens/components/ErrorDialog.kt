package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mncemngadi.bitcoinwalletapp.presentation.ui.theme.TextSecondary

@Composable
fun ErrorDialog(
    errorMessage: String?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Wallet Update Failed",
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                ) {
                    Text(
                        "Retry",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        "Close",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
        )
    }
}
