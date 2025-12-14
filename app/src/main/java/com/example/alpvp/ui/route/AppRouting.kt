package com.example.alpvp.ui.route

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alpvp.data.container.AppContainer
import com.example.alpvp.ui.view.DashboardScreen
import com.example.alpvp.ui.view.FriendsScreen
import com.example.alpvp.ui.viewModel.DashboardViewModel

enum class AppScreens (val title: String, val icon: ImageVector?= null) {
    HOME("Home", Icons.Filled.Home),
    FOOD("Food", Icons.Filled.Fastfood),
    FRIENDS("Friends", Icons.Filled.Groups),
    PROFILE("Profile", Icons.Filled.Person),
    ERROR("ERROR")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    navController: NavController,
    currentDestination: NavDestination?,
    items: List<AppScreens>
) {
    if (items.any {it.title == currentDestination?.route}) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(
                        item.icon!!,
                        contentDescription = item.title
                    )},
                    label = {Text(item.title)},
                    selected = currentDestination?.hierarchy?.any{it.route==item.title} == true,
                    onClick = {
                        navController.navigate(item.title) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun AppRouting() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Create AppContainer and DashboardViewModel
    val appContainer = remember { AppContainer() }
    val dashboardViewModel = remember { 
        DashboardViewModel(appContainer.dashboardRepository) 
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentDestination = currentDestination,
                items = listOf(
                    AppScreens.HOME,
                    AppScreens.FOOD,
                    AppScreens.FRIENDS,
                    AppScreens.PROFILE
                )
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = AppScreens.HOME.title
        ) {
            composable(AppScreens.HOME.title) {
                // TODO: Get token from proper auth flow - using placeholder for now
                LaunchedEffect(Unit) {
                    // dashboardViewModel.loadDashboardData("your-token-here")
                }
                DashboardScreen(
                    dashboardViewModel = dashboardViewModel,
                    onOpenFood = {
                        navController.navigate(AppScreens.FOOD.title)
                    }
                )
            }
            composable(AppScreens.FOOD.title) {
                Text("Food Screen - Coming Soon")
            }
            composable(AppScreens.FRIENDS.title) {
                FriendsScreen(dashboardViewModel = dashboardViewModel)
            }
            composable(AppScreens.PROFILE.title) {
                Text("Profile Screen - Coming Soon")
            }
        }
    }
}
