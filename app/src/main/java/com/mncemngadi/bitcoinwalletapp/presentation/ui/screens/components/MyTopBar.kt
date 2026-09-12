package com.mncemngadi.bitcoinwalletapp.presentation.ui.screens.components

import android.R.attr.fontFamily
import android.R.attr.fontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mncemngadi.bitcoinwalletapp.R

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
                style =
                    TextStyle(
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                    ),
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
