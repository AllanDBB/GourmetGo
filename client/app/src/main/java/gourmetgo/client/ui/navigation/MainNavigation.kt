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
import gourmetgo.client.ui.screens.BookingHistoryScreen
import gourmetgo.client.ui.screens.RatingScreen
import gourmetgo.client.viewmodel.AuthViewModel
import gourmetgo.client.viewmodel.ExperiencesViewModel
import gourmetgo.client.viewmodel.ProfileViewModel
import gourmetgo.client.viewmodel.BookingHistoryViewModel
import gourmetgo.client.viewmodel.RatingViewModel
import gourmetgo.client.viewmodel.factories.AuthViewModelFactory
import gourmetgo.client.viewmodel.factories.ExperiencesViewModelFactory
import gourmetgo.client.viewmodel.factories.ProfileViewModelFactory
import gourmetgo.client.viewmodel.factories.BookingHistoryViewModelFactory
import gourmetgo.client.viewmodel.factories.RatingViewModelFactory
import gourmetgo.client.ui.screens.BookExperienceScreen
import gourmetgo.client.viewmodel.BookingViewModel
import gourmetgo.client.viewmodel.factories.BookingViewModelFactory
import gourmetgo.client.viewmodel.MyExperiencesChefViewModel
import gourmetgo.client.viewmodel.factories.MyExperiencesChefViewModelFactory
import gourmetgo.client.viewmodel.ExperienceDetailsViewModel
import gourmetgo.client.viewmodel.factories.ExperienceDetailsViewModelFactory
import gourmetgo.client.ui.screens.MyExperiencesChefScreen
import gourmetgo.client.ui.screens.ExperienceDetailsScreen
import gourmetgo.client.ui.screens.UpdateExperienceScreen
import gourmetgo.client.viewmodel.factories.UpdateExperienceViewModelFactory
import gourmetgo.client.viewmodel.UpdateExperienceViewModel
import gourmetgo.client.viewmodel.ViewAssistanceViewModel
import gourmetgo.client.viewmodel.factories.ViewAssistanceViewModelFactory
import gourmetgo.client.ui.screens.ViewAssistanceScreen
import gourmetgo.client.ui.screens.RegisterUserScreen
import gourmetgo.client.ui.screens.RegisterChefScreen
import gourmetgo.client.viewmodel.RegisterUserViewModel
import gourmetgo.client.viewmodel.RegisterChefViewModel
import gourmetgo.client.viewmodel.factories.RegisterUserViewModelFactory
import gourmetgo.client.viewmodel.factories.RegisterChefViewModelFactory
import gourmetgo.client.data.models.dtos.BookingSummary
import com.google.gson.Gson
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val experiencesViewModel: ExperiencesViewModel = viewModel(factory = ExperiencesViewModelFactory(context))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(context))
    val myExperiencesChefViewModel: MyExperiencesChefViewModel = viewModel(factory = MyExperiencesChefViewModelFactory(context))
    val experienceDetailsViewModel: ExperienceDetailsViewModel = viewModel(
        factory = ExperienceDetailsViewModelFactory(context, "")
    )
    val updateExperienceViewModel: UpdateExperienceViewModel = viewModel(
        factory = UpdateExperienceViewModelFactory(context, "")
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
                    navController.navigate("booking_history") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
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

        composable("booking_history") {
            val bookingHistoryViewModel: BookingHistoryViewModel = viewModel(
                factory = BookingHistoryViewModelFactory(context)
            )

            BookingHistoryScreen(
                viewModel = bookingHistoryViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToRating = { booking ->
                    val gson = Gson()
                    val bookingJson = gson.toJson(booking)
                    val encodedJson = URLEncoder.encode(bookingJson, StandardCharsets.UTF_8.toString())
                    navController.navigate("rating/$encodedJson")
                }
            )
        }

        composable(
            "rating/{bookingJson}",
            arguments = listOf(navArgument("bookingJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedBookingJson = backStackEntry.arguments?.getString("bookingJson") ?: return@composable
            val bookingJson = URLDecoder.decode(encodedBookingJson, StandardCharsets.UTF_8.toString())

            val booking = try {
                Gson().fromJson(bookingJson, BookingSummary::class.java)
            } catch (e: Exception) {
                navController.popBackStack()
                return@composable
            }

            val ratingViewModel: RatingViewModel = viewModel(
                factory = RatingViewModelFactory(context, booking),
                key = "rating_${booking._id}"
            )

            RatingScreen(
                viewModel = ratingViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRatingSuccess = {
                    navController.navigate("booking_history") {
                        popUpTo("rating/{bookingJson}") { inclusive = true }
                        launchSingleTop = true
                    }
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

        composable("my_experiences_chef") {
            MyExperiencesChefScreen(
                viewModel = myExperiencesChefViewModel,
                onNavigateToCreate = { /* ... */ },
                onNavigateToExperienceDetails = { id ->
                    navController.navigate("experiences/$id")
                },
                onNavigateToAssistance = { id ->
                    navController.navigate("assistance/$id")
                }
            )
        }

        composable("experiences/{id}") { backStackEntry ->
            val experienceId = backStackEntry.arguments?.getString("id") ?: return@composable
            val detailsViewModel: ExperienceDetailsViewModel = viewModel(
                factory = ExperienceDetailsViewModelFactory(context, experienceId)
            )
            ExperienceDetailsScreen(
                viewModel = detailsViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onEdit = { id ->
                    navController.navigate("edit_experience/$id")
                }
            )
        }

        composable("edit_experience/{id}") { backStackEntry ->
            val experienceId = backStackEntry.arguments?.getString("id") ?: return@composable
            val updateExperienceViewModel: UpdateExperienceViewModel = viewModel(
                factory = UpdateExperienceViewModelFactory(context, experienceId)
            )
            UpdateExperienceScreen(
                viewModel = updateExperienceViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onDelete = {
                    // Handle delete action
                },
                onUpdateSuccess = {
                    navController.navigate("my_experiences_chef") {
                        popUpTo("edit_experience/{id}") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("assistance/{experienceId}") { backStackEntry ->
            val experienceId = backStackEntry.arguments?.getString("experienceId") ?: return@composable
            val assistanceViewModel: ViewAssistanceViewModel = viewModel(
                factory = ViewAssistanceViewModelFactory(context, experienceId)
            )

            ViewAssistanceScreen(
                viewModel = assistanceViewModel,
                onDownloadPdf = {
                    // dsp
                },
                onDownloadCsv = {
                    // dsp
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}