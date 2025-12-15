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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.example.alpvp.data.container.AppContainer
import com.example.alpvp.ui.view.FoodScreen
import com.example.alpvp.ui.view.DashboardScreen
import com.example.alpvp.ui.view.LoginScreen
import com.example.alpvp.ui.view.ProfileScreen
import com.example.alpvp.ui.view.RegisterScreen
import com.example.alpvp.ui.viewModel.DashboardViewModel
import com.example.alpvp.ui.viewModel.AuthViewModel
import com.example.alpvp.ui.viewModel.FoodViewModel
import com.example.alpvp.ui.viewModel.AARViewModel

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

    val container = AppContainer(
        context = LocalContext.current.applicationContext
    )

    // AuthViewModel to observe login token
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(
                        container.authRepository,
                        container.userPreferencesRepository,
                        container.userRepository
                    ) as T
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
                    return DashboardViewModel(container.dashboardRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    // AARViewModel for activity and attention recognition
    val aarViewModel: AARViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AARViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AARViewModel(container.aarService) as T
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
                     onNavigateToSignUp = {
                         navController.navigate("Register") {
                             launchSingleTop = true
                         }
                     }
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

                 DashboardScreen(
                     dashboardViewModel = dashboardViewModel,
                     onOpenFood = { navController.navigate(AppScreens.FOOD.title) }
                 )
             }

            composable(AppScreens.FOOD.title) {
                val authUiState by authViewModel.uiState.collectAsState()
                if (authUiState.token == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate("Login") {
                            launchSingleTop = true
                        }
                    }
                    Text("Redirecting to Login...")
                } else {
                    val foodViewModel: FoodViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return FoodViewModel(
                                    container.foodRepository,
                                    authUiState.token!!,
                                    authUiState.userId!!
                                ) as T
                            }
                        }
                    )
                    FoodScreen(foodViewModel = foodViewModel)
                }
            }


            composable(AppScreens.FRIENDS.title) {
                val authUiState by authViewModel.uiState.collectAsState()
                if (authUiState.token == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate("Login") {
                            launchSingleTop = true
                        }
                    }
                    Text("Redirecting to Login...")
                } else {
                    Text("Friends Screen")
                }
            }

            composable(AppScreens.PROFILE.title) {
                val authUiState by authViewModel.uiState.collectAsState()
                if (authUiState.token == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate("Login") {
                            launchSingleTop = true
                        }
                    }
                    Text("Redirecting to Login...")
                } else {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        dashboardViewModel = dashboardViewModel
                    )
                }
            }

            composable("Register") {
                val authUiState by authViewModel.uiState.collectAsState()
                if (authUiState.token != null) {
                    LaunchedEffect(authUiState.token) {
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

