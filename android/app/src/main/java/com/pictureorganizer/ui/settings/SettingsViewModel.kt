package com.pictureorganizer.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** F11：设置画面偏好（导入重名询问开关） */
class SettingsViewModel(
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {
    val importDuplicateAskEnabled: StateFlow<Boolean> =
        userPreferences.importDuplicateAskEnabled.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    fun setImportDuplicateAskEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setImportDuplicateAskEnabled(enabled)
        }
    }

    class Factory(
        private val userPreferences: UserPreferencesRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(userPreferences) as T
    }
}
