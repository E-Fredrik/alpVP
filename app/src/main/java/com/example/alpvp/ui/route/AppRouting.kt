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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alpvp.data.container.AppContainer
//import com.example.alpvp.ui.view.FoodScreen
import com.example.alpvp.ui.view.LoginScreen
import com.example.alpvp.ui.view.RegisterScreen
import com.example.alpvp.ui.viewModel.AuthViewModel

enum class AppScreens (val title: String, val icon: ImageVector?= null) {
    HOME("Home", Icons.Filled.Home),
    FOOD("Food", Icons.Filled.Fastfood),
    FRIENDS("Friends", Icons.Filled.Groups),
    PROFILE("Profile", Icons.Filled.Person),
    LOGIN("Login"),
    REGISTER("Register"),
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
                    icon = { item.icon?.let { Icon(it, contentDescription = item.title) } },
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

    val container = AppContainer()

    // inline factory: no separate file required
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(container.userRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val currentView = AppScreens.entries.find({it.title == currentRoute}) ?: AppScreens.HOME

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
        // observe auth state
        val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = AppScreens.HOME.title
        ) {
            composable(AppScreens.HOME.title) {
                Text("Home Screen")
            }

            composable(AppScreens.FOOD.title) {
                if (uiState.token == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate(AppScreens.LOGIN.title) {
                            launchSingleTop = true
                        }
                    }
                    Text("Redirecting to Login...")
                } else {
//                    FoodScreen
                }
            }

            composable(AppScreens.FRIENDS.title) {
                if (uiState.token == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate(AppScreens.LOGIN.title) {
                            launchSingleTop = true
                        }
                    }
                    Text("Redirecting to Login...")
                } else {
                    Text("Friends Screen")
                }
            }

            composable(AppScreens.PROFILE.title) {
                if (uiState.token == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate(AppScreens.LOGIN.title) {
                            launchSingleTop = true
                        }
                    }
                    Text("Redirecting to Login...")
                } else {
                    Text("Profile Screen")
                }
            }

            composable(AppScreens.LOGIN.title) {
                if (uiState.token != null) {
                    LaunchedEffect(uiState.token) {
                        navController.navigate(AppScreens.HOME.title) {
                            popUpTo(AppScreens.LOGIN.title) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    Text("Redirecting to Home...")
                } else {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onNavigateToSignUp = {
                            navController.navigate("Register") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable("Register") {
                if (uiState.token != null) {
                    LaunchedEffect(uiState.token) {
                        navController.navigate(AppScreens.HOME.title) {
                            popUpTo("Register") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    Text("Redirecting to Home...")
                } else {
                    RegisterScreen(authViewModel = authViewModel)
                }
            }
        }
    }
}

