package com.mncemngadi.bitcoinwalletapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mncemngadi.bitcoinwalletapp.ui.screens.WalletScreen

sealed class Screen(val route: String) {
    object Wallet : Screen("wallet")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Wallet.route
    ) {
        composable(Screen.Wallet.route) {
            WalletScreen()
        }
    }
}
