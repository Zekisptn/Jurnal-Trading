package com.example.jurnaltreding.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class SessionState {
    /** Sesi belum selesai dibaca. */
    LOADING,
    LOGGED_IN,
    LOGGED_OUT
}

/** Mengamati sesi login supaya navigasi tahu harus menampilkan Login atau isi aplikasi. */
class SessionViewModel(
    userRepository: UserRepository
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = userRepository.currentUserId
        .map { userId -> if (userId == null) SessionState.LOGGED_OUT else SessionState.LOGGED_IN }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionState.LOADING)
}
