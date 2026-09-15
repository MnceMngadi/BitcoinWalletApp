package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    onRefreshClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Black),
            )
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                // containerColor = Color.Transparent,
                titleContentColor = Color.White,
            ),
        modifier = Modifier.shadow(elevation = 5.dp),
    )
}
