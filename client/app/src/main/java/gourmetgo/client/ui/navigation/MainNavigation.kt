@file:OptIn(ExperimentalMaterial3Api::class)
package gourmetgo.client.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import gourmetgo.client.ui.screens.LoginScreen
import gourmetgo.client.ui.screens.ExperiencesScreen
import gourmetgo.client.ui.screens.BookingHistoryScreen
import gourmetgo.client.ui.screens.RatingScreen
import gourmetgo.client.ui.screens.EditProfileScreen
import gourmetgo.client.ui.screens.HomeScreen
import gourmetgo.client.viewmodel.AuthViewModel
import gourmetgo.client.viewmodel.ExperiencesViewModel
import gourmetgo.client.viewmodel.ProfileViewModel
import gourmetgo.client.viewmodel.BookingHistoryViewModel
import gourmetgo.client.viewmodel.RatingViewModel
import gourmetgo.client.viewmodel.HomeViewModel
import gourmetgo.client.viewmodel.factories.AuthViewModelFactory
import gourmetgo.client.viewmodel.factories.ExperiencesViewModelFactory
import gourmetgo.client.viewmodel.factories.ProfileViewModelFactory
import gourmetgo.client.viewmodel.factories.BookingHistoryViewModelFactory
import gourmetgo.client.viewmodel.factories.RatingViewModelFactory
import gourmetgo.client.viewmodel.factories.HomeViewModelFactory
import gourmetgo.client.ui.screens.BookExperienceScreen
import gourmetgo.client.ui.screens.MyExperiencesChefScreen
import gourmetgo.client.ui.screens.ExperienceDetailsScreen
import gourmetgo.client.ui.screens.UpdateExperienceScreen
import gourmetgo.client.viewmodel.BookingViewModel
import gourmetgo.client.viewmodel.factories.BookingViewModelFactory
import gourmetgo.client.viewmodel.MyExperiencesChefViewModel
import gourmetgo.client.viewmodel.factories.MyExperiencesChefViewModelFactory
import gourmetgo.client.viewmodel.ExperienceDetailsViewModel
import gourmetgo.client.viewmodel.factories.ExperienceDetailsViewModelFactory
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
import gourmetgo.client.viewmodel.DeleteExperienceViewModel
import gourmetgo.client.viewmodel.factories.DeleteExperienceViewModelFactory
import gourmetgo.client.viewmodel.CreateExperienceViewModel
import gourmetgo.client.viewmodel.factories.CreateExperienceViewModelFactory
import gourmetgo.client.ui.screens.CreateExperienceScreen


@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val experiencesViewModel: ExperiencesViewModel = viewModel(factory = ExperiencesViewModelFactory(context))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))
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

    val startDestination = when (authViewModel.uiState.userType) {
        "chef" -> "my_experiences_chef"
        "user" -> "home"
        else -> if (authViewModel.uiState.isLoggedIn) "home" else "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,                onLoginSuccess = {
                    when (authViewModel.uiState.userType) {
                        "chef" -> navController.navigate("my_experiences_chef") {
                            popUpTo("login") { inclusive = true }
                        }
                        "user" -> navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                        else -> navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }            )
        }

        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onGoToExperiences = {
                    navController.navigate("experiences")
                },                onNavigateToExperienceDetails = { experienceId ->
                    navController.navigate("experience_details/$experienceId")
                },
                onNavigateToProfile = {
                    navController.navigate("edit_profile") {
                        launchSingleTop = true
                    }
                },
                onNavigateToHistory = {
                    navController.navigate("booking_history") {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
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
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
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
                }            )
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
                    navController.navigate("book_experience/$experienceId")
                },
                onNavigateToRating = { experienceId ->
                    navController.navigate("rating/$experienceId") {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
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
                },                onNavigateToRating = { booking ->
                    booking.experience?._id?.let { experienceId ->
                        navController.navigate("rating/$experienceId")
                    }
                }
            )
        }

        composable(
            "rating/{experienceId}",
            arguments = listOf(navArgument("experienceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val experienceId = backStackEntry.arguments?.getString("experienceId") ?: return@composable
            val ratingViewModel: RatingViewModel = viewModel(
                factory = RatingViewModelFactory(context, experienceId),
                key = "rating_$experienceId"
            )

            RatingScreen(
                viewModel = ratingViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },                onRatingSuccess = {
                    // Navegar al menú principal (home) después de una reseña exitosa
                    navController.navigate("home") {
                        popUpTo("rating/{experienceId}") { inclusive = true }
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
                },                onBookingSuccess = {
                    // Navegar al HomeScreen después de una reserva exitosa
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true                    }
                }
            )
        }
        
        composable("my_experiences_chef") {
            MyExperiencesChefScreen(
                viewModel = myExperiencesChefViewModel,
                onNavigateToCreate = {
                    navController.navigate("create_experience")
                },
                onNavigateToExperienceDetails = { id ->
                    navController.navigate("chef_experience_details/$id") 
                },
                onNavigateToAssistance = { id ->
                    navController.navigate("assistance/$id")
                },
                onNavigateToProfile = {
                    navController.navigate("edit_profile") {
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

        composable(
            "experience_details/{experienceId}",
            arguments = listOf(navArgument("experienceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val experienceId = backStackEntry.arguments?.getString("experienceId") ?: return@composable
            val detailsViewModel: ExperienceDetailsViewModel = viewModel(
                factory = ExperienceDetailsViewModelFactory(context, experienceId),
                key = "experience_details_$experienceId"
            )
            
            ExperienceDetailsScreen(
                viewModel = detailsViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onEdit = { id ->
                    navController.navigate("edit_experience/$id")
                },
                onReserve = { expId ->
                    navController.navigate("book_experience/$expId")
                },
                onViewReviews = { expId ->
                    navController.navigate("experience_reviews/$expId")
                },
                isChefView = authViewModel.uiState.userType == "chef"
            )
        }

        composable(
            "chef_experience_details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
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
                },
                onReserve = { experienceId ->
                    navController.navigate("book_experience/$experienceId")
                },
                onViewReviews = { experienceId ->
                    navController.navigate("experience_reviews/$experienceId")
                },
                isChefView = true // Vista para chefs
            )
        }

        composable("edit_experience/{id}") { backStackEntry ->
            val experienceId = backStackEntry.arguments?.getString("id") ?: return@composable
            val updateExperienceViewModel: UpdateExperienceViewModel = viewModel(
                factory = UpdateExperienceViewModelFactory(context, experienceId)
            )
            val deleteExperienceViewModel: DeleteExperienceViewModel = viewModel(
                factory = DeleteExperienceViewModelFactory(context, experienceId)
            )
            UpdateExperienceScreen(
                viewModel = updateExperienceViewModel,
                onNavigateBack = { navController.popBackStack() },
                onDelete = {
                    navController.navigate("my_experiences_chef") {
                        popUpTo("edit_experience/{id}") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onUpdateSuccess = {
                    navController.navigate("my_experiences_chef") {
                        popUpTo("edit_experience/{id}") { inclusive = true }
                        launchSingleTop = true
                    }
                },                deleteExperienceViewModel = deleteExperienceViewModel
            )
        }

        // Nueva ruta para ver reseñas de una experiencia
        composable(
            "experience_reviews/{experienceId}",
            arguments = listOf(navArgument("experienceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val experienceId = backStackEntry.arguments?.getString("experienceId") ?: return@composable
            
            // Pantalla simple para mostrar reseñas (por ahora)
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Reseñas de la Experiencia") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🌟",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = "Reseñas de la Experiencia",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Próximamente podrás ver todas las reseñas y calificaciones de otros usuarios aquí.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "💡 Mientras tanto...",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Puedes hacer tu reserva y después dejar tu propia reseña para ayudar a otros usuarios.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
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

        composable("create_experience") {
            val createExperienceViewModel: CreateExperienceViewModel = viewModel(
                factory = CreateExperienceViewModelFactory(context)
            )
            CreateExperienceScreen(
                viewModel = createExperienceViewModel,
                onNavigateBack = { navController.popBackStack() },
                onCreateSuccess = {
                    navController.navigate("my_experiences_chef") {
                        popUpTo("create_experience") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}