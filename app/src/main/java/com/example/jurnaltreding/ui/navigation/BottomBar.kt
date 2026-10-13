package com.example.jurnaltreding.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/** Layar utama yang menampilkan bottom bar. */
val topLevelDestinations = listOf(
    TopLevelDestination(Routes.DASHBOARD, "Dashboard", Icons.Filled.Dashboard),
    TopLevelDestination(Routes.JOURNAL_LIST, "Jurnal", Icons.AutoMirrored.Filled.MenuBook),
    TopLevelDestination(Routes.PROFILE, "Profil", Icons.Filled.Person)
)

@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(destination.label) }
            )
        }
    }
}
