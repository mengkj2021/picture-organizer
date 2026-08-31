package com.pictureorganizer.ui.importimages

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.R
import com.pictureorganizer.data.repository.ImageRepository
import com.pictureorganizer.data.repository.UserPreferencesRepository
import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import com.pictureorganizer.util.image.ImageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface ImportUiEffect {
    data object ImportFinished : ImportUiEffect

    data class ShowError(
        val resId: Int,
        val args: List<Any> = emptyList(),
    ) : ImportUiEffect
}

class ImportViewModel(
    private val repository: ImageRepository,
    private val imageManager: ImageManager,
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    private val _effects = Channel<ImportUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val enabled = userPreferences.isImportCompressEnabled()
            _uiState.update { it.copy(compressEnabled = enabled) }
        }
    }

    fun setCompressEnabled(enabled: Boolean) {
        _uiState.update { it.copy(compressEnabled = enabled) }
        viewModelScope.launch {
            userPreferences.setImportCompressEnabled(enabled)
        }
    }

    fun importUris(uris: List<Uri>) {
        if (uris.isEmpty() || _uiState.value.isImporting) return
        val compressEnabled = _uiState.value.compressEnabled
        viewModelScope.launch {
            _uiState.update {
                it.copy(isImporting = true, current = 0, total = uris.size)
            }
            var failures = 0
            uris.forEachIndexed { index, uri ->
                _uiState.update {
                    it.copy(current = index + 1, total = uris.size)
                }
                runCatching {
                    withContext(Dispatchers.IO) {
                        val prepared = imageManager.prepareForImport(uri, compressEnabled)
                        val now = System.currentTimeMillis()
                        val defaultTags =
                            userPreferences
                                .getDefaultTagNames()
                                .filter { it !in ImageListItem.STATUS_TAGS }
                        val item =
                            ImageListItem(
                                id = prepared.id,
                                description = prepared.fileName,
                                status = ImageStatus.Pending,
                                tags = defaultTags,
                            )
                        repository.insert(
                            item = item,
                            filePath = prepared.filePath,
                            fileName = prepared.fileName,
                            importedAt = now,
                        )
                    }
                }.onFailure {
                    failures++
                }
            }
            _uiState.update {
                it.copy(isImporting = false, current = 0, total = 0)
            }
            val successCount = uris.size - failures
            val effect =
                when {
                    failures == 0 -> ImportUiEffect.ImportFinished
                    successCount == 0 -> ImportUiEffect.ShowError(R.string.import_fail_all)
                    else ->
                        ImportUiEffect.ShowError(
                            R.string.import_fail_partial,
                            listOf(successCount, failures),
                        )
                }
            _effects.send(effect)
        }
    }

    class Factory(
        private val repository: ImageRepository,
        private val imageManager: ImageManager,
        private val userPreferences: UserPreferencesRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ImportViewModel(repository, imageManager, userPreferences) as T
    }
}
