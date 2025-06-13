package gourmetgo.client.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gourmetgo.client.ui.screens.LoginScreen
import gourmetgo.client.ui.screens.ExperiencesScreen
import gourmetgo.client.ui.screens.HomeScreen
import gourmetgo.client.viewmodel.AuthViewModel
import gourmetgo.client.viewmodel.ExperiencesViewModel
import gourmetgo.client.viewmodel.HomeViewModel
import gourmetgo.client.viewmodel.factories.AuthViewModelFactory
import gourmetgo.client.viewmodel.factories.ExperiencesViewModelFactory
import gourmetgo.client.viewmodel.factories.HomeViewModelFactory

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val experiencesViewModel: ExperiencesViewModel = viewModel(factory = ExperiencesViewModelFactory(context))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))

    LaunchedEffect(Unit) {
        authViewModel.checkLoginStatus()
    }

    val startDestination = if (authViewModel.uiState.isLoggedIn) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {                        popUpTo("login") { inclusive = true }
                    }                }
            )
        }
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onGoToExperiences = {
                    navController.navigate("experiences")
                },
                onNavigateToExperienceDetails = { experienceId ->
                    // TODO: Navigate to experience details screen
                    // navController.navigate("experience_details/$experienceId")
                },
                onNavigateToProfile = {
                    // TODO: Navigate to profile screen
                    // navController.navigate("profile")
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("experiences") {
            ExperiencesScreen(
                viewModel = experiencesViewModel,
                onNavigateToProfile = {
                    navController.navigate("login")
                },
                onNavigateToBooking = { /* TODO: Navegación a booking */ },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}