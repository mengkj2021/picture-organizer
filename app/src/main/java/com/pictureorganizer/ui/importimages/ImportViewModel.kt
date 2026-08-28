package com.pictureorganizer.ui.importimages

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.R
import com.pictureorganizer.data.repository.ImageRepository
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
        val args: List<Any> = emptyList()
    ) : ImportUiEffect
}

class ImportViewModel(
    private val repository: ImageRepository,
    private val imageManager: ImageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    private val _effects = Channel<ImportUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun importUris(uris: List<Uri>) {
        if (uris.isEmpty() || _uiState.value.isImporting) return
        viewModelScope.launch {
            _uiState.value = ImportUiState(isImporting = true, current = 0, total = uris.size)
            var failures = 0
            uris.forEachIndexed { index, uri ->
                _uiState.update {
                    it.copy(current = index + 1, total = uris.size)
                }
                runCatching {
                    withContext(Dispatchers.IO) {
                        val prepared = imageManager.prepareForImport(uri)
                        val now = System.currentTimeMillis()
                        // date / tags / placeholderColorArgb 由 data/mapper 在写库读库时统一派生
                        val item = ImageListItem(
                            id = prepared.id,
                            description = prepared.fileName,
                            status = ImageStatus.Pending
                        )
                        repository.insert(
                            item = item,
                            filePath = prepared.filePath,
                            fileName = prepared.fileName,
                            importedAt = now
                        )
                    }
                }.onFailure {
                    failures++
                }
            }
            _uiState.value = ImportUiState(isImporting = false)
            val successCount = uris.size - failures
            val effect = when {
                failures == 0 -> ImportUiEffect.ImportFinished
                successCount == 0 -> ImportUiEffect.ShowError(R.string.import_fail_all)
                else -> ImportUiEffect.ShowError(
                    R.string.import_fail_partial,
                    listOf(successCount, failures)
                )
            }
            _effects.send(effect)
        }
    }

    class Factory(
        private val repository: ImageRepository,
        private val imageManager: ImageManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ImportViewModel(repository, imageManager) as T
        }
    }
}
