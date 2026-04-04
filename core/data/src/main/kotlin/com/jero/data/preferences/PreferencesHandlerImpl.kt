package com.jero.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.domain.preferences.PreferencesHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesHandlerImpl(context: Context) : PreferencesHandler {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    private val _isLoggedFlow = MutableStateFlow(sharedPreferences.getBoolean(IS_LOGGED, false))
    override val isLoggedFlow: Flow<Boolean> = _isLoggedFlow.asStateFlow()

    override var isLogged: Boolean
        get() = sharedPreferences.getBoolean(IS_LOGGED, false)
        set(value) {
            sharedPreferences.edit { putBoolean(IS_LOGGED, value) }
            _isLoggedFlow.value = value
        }

    override fun clear() {
        sharedPreferences.edit { clear() }
        _isLoggedFlow.value = false
    }

    companion object {
        private const val IS_LOGGED = "isLogged"
    }
}
