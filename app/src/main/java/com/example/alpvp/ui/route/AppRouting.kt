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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alpvp.data.container.AppContainer
import com.example.alpvp.ui.view.DashboardScreen
import com.example.alpvp.ui.view.LoginScreen
import com.example.alpvp.ui.viewModel.DashboardViewModel
import com.example.alpvp.ui.viewModel.AuthViewModel

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

    // create shared app container and view models here so login & home can both access them
    val appContainer = remember { AppContainer() }

    // AuthViewModel to observe login token
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(appContainer.authRepository, appContainer.userRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    // DashboardViewModel which needs a DashboardRepository
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DashboardViewModel(appContainer.dashboardRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

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
             // start at login screen
             startDestination = "Login"
         ) {
             // Login route
             composable("Login") {
                 // observe auth state and navigate to home on successful login
                 val authUiState by authViewModel.uiState.collectAsState()
                 LaunchedEffect(authUiState.token) {
                     authUiState.token?.let {
                         navController.navigate(AppScreens.HOME.title) {
                             popUpTo("Login") { inclusive = true }
                         }
                     }
                 }

                 LoginScreen(
                     authViewModel = authViewModel,
                     onNavigateToSignUp = { /* TODO: navigate to SignUp route if implemented */ }
                 )
             }

             // Home route
             composable(AppScreens.HOME.title) {
                 // When auth token becomes available, load dashboard data
                 val authUiState by authViewModel.uiState.collectAsState()
                 LaunchedEffect(authUiState.token) {
                     authUiState.token?.let { token ->
                         dashboardViewModel.loadDashboardData(token)
                     }
                 }

                 DashboardScreen(dashboardViewModel = dashboardViewModel, onOpenFood = {})
             }

             composable(AppScreens.FOOD.title) {
                 Text("Food Screen")
             }

             composable(AppScreens.FRIENDS.title) {
                 Text("Friends Screen")
             }

             composable(AppScreens.PROFILE.title) {
                 Text("Profile Screen")
             }
         }
    }
}
