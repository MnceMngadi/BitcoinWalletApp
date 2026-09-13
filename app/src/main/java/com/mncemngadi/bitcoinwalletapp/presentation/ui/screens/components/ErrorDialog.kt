package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
                Text(text = "Wallet Update Failed")
            },
            text = {
                Text(text = errorMessage)
            },
            confirmButton = {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                ) {
                    Text("Retry", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = Color.Gray)
                }
            },
        )
    }
}
