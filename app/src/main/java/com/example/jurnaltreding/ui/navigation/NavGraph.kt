package com.example.jurnaltreding.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jurnaltreding.ui.AppViewModelProvider
import com.example.jurnaltreding.ui.auth.LoginScreen
import com.example.jurnaltreding.ui.auth.RegisterScreen
import com.example.jurnaltreding.ui.components.LoadingState
import com.example.jurnaltreding.ui.dashboard.DashboardScreen
import com.example.jurnaltreding.ui.journal.JournalDetailScreen
import com.example.jurnaltreding.ui.journal.JournalFormScreen
import com.example.jurnaltreding.ui.journal.JournalListScreen
import com.example.jurnaltreding.ui.profile.EditProfileScreen
import com.example.jurnaltreding.ui.profile.ProfileScreen

private val authRoutes = setOf(Routes.LOGIN, Routes.REGISTER)

/** Akar UI: menunggu sesi terbaca, lalu menampilkan Login atau isi aplikasi. */
@Composable
fun JurnalApp() {
    val sessionViewModel: SessionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()

    when (sessionState) {
        SessionState.LOADING -> Surface { LoadingState(message = "Menyiapkan aplikasi...") }
        // Satu pemanggilan untuk kedua kondisi supaya NavController tetap sama saat sesi berubah.
        else -> AppNavHost(isLoggedIn = sessionState == SessionState.LOGGED_IN)
    }
}

@Composable
private fun AppNavHost(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    // Layar awal ditentukan sekali dari sesi saat aplikasi dibuka.
    val startDestination = rememberSaveable { if (isLoggedIn) Routes.DASHBOARD else Routes.LOGIN }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = topLevelDestinations.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute,
                    onNavigate = navController::navigateToTopLevel
                )
            }
        },
        // Setiap layar punya Scaffold sendiri yang mengurus inset status bar dan navigasi sistem.
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER) { launchSingleTop = true }
                    }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    onNavigateBack = { navController.popBackStack(Routes.LOGIN, inclusive = false) }
                )
            }

            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    onAddEntry = { navController.navigate(Routes.journalForm()) },
                    onOpenEntry = { id -> navController.navigate(Routes.journalDetail(id)) },
                    onSeeAllEntries = { navController.navigateToTopLevel(Routes.JOURNAL_LIST) }
                )
            }
            composable(Routes.JOURNAL_LIST) {
                JournalListScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    onAddEntry = { navController.navigate(Routes.journalForm()) },
                    onOpenEntry = { id -> navController.navigate(Routes.journalDetail(id)) }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) }
                )
            }

            composable(
                route = Routes.JOURNAL_DETAIL,
                arguments = listOf(navArgument(Routes.ARG_ENTRY_ID) { type = NavType.LongType })
            ) {
                JournalDetailScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    onNavigateBack = { navController.popBackStack() },
                    onEditEntry = { id -> navController.navigate(Routes.journalForm(id)) }
                )
            }
            composable(
                route = Routes.JOURNAL_FORM,
                arguments = listOf(
                    navArgument(Routes.ARG_ENTRY_ID) {
                        type = NavType.LongType
                        defaultValue = Routes.NO_ENTRY_ID
                    }
                )
            ) {
                JournalFormScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Routes.EDIT_PROFILE) {
                EditProfileScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    // Sesi berubah = pindah alur dan kosongkan back stack, supaya tombol back tidak bisa
    // kembali ke Login setelah login, atau ke dalam aplikasi setelah logout.
    LaunchedEffect(isLoggedIn) {
        val onAuthScreen = navController.currentDestination?.route in authRoutes
        if (isLoggedIn && onAuthScreen) {
            navController.navigateAndClearBackStack(Routes.DASHBOARD)
        } else if (!isLoggedIn && !onAuthScreen) {
            navController.navigateAndClearBackStack(Routes.LOGIN)
        }
    }
}

private fun NavHostController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Pindah tab bottom bar: satu salinan per tab, dan state tiap tab disimpan. */
private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(Routes.DASHBOARD) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
