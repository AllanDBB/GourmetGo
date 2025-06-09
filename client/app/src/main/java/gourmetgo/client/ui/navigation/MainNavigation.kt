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
import gourmetgo.client.ui.screens.RegisterUserScreen
import gourmetgo.client.ui.screens.RegisterChefScreen
import gourmetgo.client.viewmodel.AuthViewModel
import gourmetgo.client.viewmodel.ExperiencesViewModel
import gourmetgo.client.viewmodel.RegisterUserViewModel
import gourmetgo.client.viewmodel.RegisterChefViewModel
import gourmetgo.client.viewmodel.factories.AuthViewModelFactory
import gourmetgo.client.viewmodel.factories.ExperiencesViewModelFactory
import gourmetgo.client.viewmodel.factories.RegisterUserViewModelFactory
import gourmetgo.client.viewmodel.factories.RegisterChefViewModelFactory
/* 
import gourmetgo.client.viewmodel.BookingViewModel
import gourmetgo.client.viewmodel.factories.BookingViewModelFactory
import gourmetgo.client.ui.screens.BookExperienceScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument*/

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val experiencesViewModel: ExperiencesViewModel = viewModel(
        factory = ExperiencesViewModelFactory(context)
    )
    val registerUserViewModel: RegisterUserViewModel = viewModel(
        factory = RegisterUserViewModelFactory(context)
    )
    val registerChefViewModel: RegisterChefViewModel = viewModel(
        factory = RegisterChefViewModelFactory(context)
    )

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
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            // Resetear el estado cuando se entra a la pantalla de registro
            LaunchedEffect(Unit) {
                registerUserViewModel.resetState()
            }
            
            RegisterUserScreen(
                viewModel = registerUserViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToRegisterChef = {
                    navController.navigate("register-chef")
                }
            )
        }

        composable("register-chef") {
            // Resetear el estado cuando se entra a la pantalla de registro chef
            LaunchedEffect(Unit) {
                registerChefViewModel.resetState()
            }
            
            RegisterChefScreen(
                viewModel = registerChefViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register-chef") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("experiences") {
            ExperiencesScreen(
                viewModel = experiencesViewModel,
                onNavigateToProfile = {
                    // Por ahora navega a login, cambiar más tarde por edit_profile
                    navController.navigate("login")
                },
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
        
/* 
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
        }*/
