package com.example.jurnaltreding.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jurnaltreding.JurnalApplication
import com.example.jurnaltreding.data.AppContainer
import com.example.jurnaltreding.ui.auth.LoginViewModel
import com.example.jurnaltreding.ui.auth.RegisterViewModel
import com.example.jurnaltreding.ui.dashboard.DashboardViewModel
import com.example.jurnaltreding.ui.journal.JournalDetailViewModel
import com.example.jurnaltreding.ui.journal.JournalFormViewModel
import com.example.jurnaltreding.ui.journal.JournalListViewModel
import com.example.jurnaltreding.ui.navigation.SessionViewModel
import com.example.jurnaltreding.ui.profile.EditProfileViewModel
import com.example.jurnaltreding.ui.profile.ProfileViewModel

/** Factory semua ViewModel; repository diambil dari AppContainer (dependency injection manual). */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { SessionViewModel(container().userRepository) }
        initializer { LoginViewModel(container().userRepository) }
        initializer { RegisterViewModel(container().userRepository) }
        initializer { DashboardViewModel(container().userRepository, container().journalRepository) }
        initializer { JournalListViewModel(container().userRepository, container().journalRepository) }
        initializer {
            JournalDetailViewModel(
                createSavedStateHandle(),
                container().userRepository,
                container().journalRepository
            )
        }
        initializer {
            JournalFormViewModel(
                createSavedStateHandle(),
                container().userRepository,
                container().journalRepository
            )
        }
        initializer { ProfileViewModel(container().userRepository) }
        initializer { EditProfileViewModel(container().userRepository) }
    }
}

private fun CreationExtras.container(): AppContainer =
    (checkNotNull(this[APPLICATION_KEY]) as JurnalApplication).container
