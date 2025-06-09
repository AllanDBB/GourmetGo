package gourmetgo.client.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import gourmetgo.client.ui.screens.EditProfileScreen
import gourmetgo.client.ui.screens.LoginScreen
import gourmetgo.client.ui.screens.ExperiencesScreen
import gourmetgo.client.viewmodel.AuthViewModel
import gourmetgo.client.viewmodel.ExperiencesViewModel
import gourmetgo.client.viewmodel.ProfileViewModel
import gourmetgo.client.viewmodel.factories.AuthViewModelFactory
import gourmetgo.client.viewmodel.factories.ExperiencesViewModelFactory
import gourmetgo.client.viewmodel.factories.ProfileViewModelFactory
import gourmetgo.client.ui.screens.BookExperienceScreen
import gourmetgo.client.viewmodel.BookingViewModel
import gourmetgo.client.viewmodel.factories.BookingViewModelFactory

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val experiencesViewModel: ExperiencesViewModel = viewModel(factory = ExperiencesViewModelFactory(context))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(context))

    LaunchedEffect(Unit) {
        authViewModel.checkLoginStatus()
    }

    val startDestination = if (authViewModel.uiState.isLoggedIn) "experiences" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("experiences") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("experiences") {
            ExperiencesScreen(
                viewModel = experiencesViewModel,
                onNavigateToProfile = {
                    navController.navigate("edit_profile") {
                        launchSingleTop = true
                    }
                },
                onNavigateToBooking = { experienceId ->
                    navController.navigate("book_experience/$experienceId") {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            "book_experience/{experienceId}",
            arguments = listOf(navArgument("experienceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val experienceId = backStackEntry.arguments?.getString("experienceId") ?: ""
            val bookingViewModel: BookingViewModel = viewModel(
                factory = BookingViewModelFactory(context),
                key = "booking_$experienceId"
            )

            BookExperienceScreen(
                experienceId = experienceId,
                viewModel = bookingViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onBookingSuccess = {
                    navController.navigate("experiences") {
                        popUpTo("book_experience/{experienceId}") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}