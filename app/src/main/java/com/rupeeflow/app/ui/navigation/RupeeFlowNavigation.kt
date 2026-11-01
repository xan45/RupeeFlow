package com.rupeeflow.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rupeeflow.app.RupeeFlowApp
import com.rupeeflow.app.ui.screens.analytics.AnalyticsScreen
import com.rupeeflow.app.ui.screens.analytics.AnalyticsViewModel
import com.rupeeflow.app.ui.screens.dashboard.DashboardScreen
import com.rupeeflow.app.ui.screens.dashboard.DashboardViewModel
import com.rupeeflow.app.ui.screens.importstatement.ImportStatementScreen
import com.rupeeflow.app.ui.screens.importstatement.ImportStatementViewModel
import com.rupeeflow.app.ui.screens.settings.AboutScreen
import com.rupeeflow.app.ui.screens.settings.SettingsScreen
import com.rupeeflow.app.ui.screens.transactions.TransactionsScreen
import com.rupeeflow.app.ui.screens.transactions.TransactionsViewModel
import com.rupeeflow.app.ui.theme.Indigo500
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate900
import com.rupeeflow.app.ui.theme.Slate950

@Composable
fun RupeeFlowNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val app = LocalContext.current.applicationContext as RupeeFlowApp
    val container = app.container

    // ViewModels
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(container.transactionRepository)
    )
    val transactionsViewModel: TransactionsViewModel = viewModel(
        factory = TransactionsViewModel.provideFactory(container.transactionRepository)
    )
    val analyticsViewModel: AnalyticsViewModel = viewModel(
        factory = AnalyticsViewModel.provideFactory(container.transactionRepository)
    )
    val importViewModel: ImportStatementViewModel = viewModel(
        factory = ImportStatementViewModel.provideFactory(container.statementRepository)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val showBottomBar = Screen.bottomNavItems.any { it.route == currentRoute }
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Slate900,
                    contentColor = Color.White
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(it, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title, fontSize = 10.sp) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Indigo500,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400,
                                indicatorColor = Indigo600
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .padding(innerPadding)
                .background(Slate950)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToImport = { navController.navigate(Screen.ImportStatement.route) },
                    onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                    onPartyDrillDown = { partyName ->
                        transactionsViewModel.updatePartyFilter(partyName)
                        navController.navigate(Screen.Transactions.route)
                    }
                )
            }

            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    viewModel = transactionsViewModel,
                    onPartySelected = { partyName ->
                        transactionsViewModel.updatePartyFilter(partyName)
                    }
                )
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    viewModel = analyticsViewModel,
                    onPartyDrillDown = { partyName ->
                        transactionsViewModel.updatePartyFilter(partyName)
                        navController.navigate(Screen.Transactions.route)
                    }
                )
            }

            composable(Screen.ImportStatement.route) {
                ImportStatementScreen(
                    viewModel = importViewModel
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToAbout = { navController.navigate(Screen.About.route) },
                    onClearAllData = { transactionsViewModel.clearAllTransactions() }
                )
            }

            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
