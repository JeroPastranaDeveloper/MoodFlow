package com.example.domain.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesHandler {
    var isLogged: Boolean
    val isLoggedFlow: Flow<Boolean>
    fun clear()
}
