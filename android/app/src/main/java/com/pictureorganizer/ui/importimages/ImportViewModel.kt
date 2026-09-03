package com.pictureorganizer.ui.importimages

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import java.io.FileNotFoundException
import java.io.IOException

sealed interface ImportUiEffect {
    /** 全部导入（含重试）成功，回主画面 */
    data object ImportFinished : ImportUiEffect
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
                it.copy(
                    isImporting = true,
                    current = 0,
                    total = uris.size,
                    failedItems = emptyList(),
                )
            }
            val failures = mutableListOf<FailedImportItem>()
            uris.forEachIndexed { index, uri ->
                _uiState.update {
                    it.copy(current = index + 1, total = uris.size)
                }
                importSingle(uri, compressEnabled).onFailure { e ->
                    failures +=
                        FailedImportItem(
                            uri = uri,
                            displayName = imageManager.displayNameOf(uri),
                            errorKind = errorKindOf(e),
                            errorDetail = errorDetailOf(e),
                        )
                }
            }
            _uiState.update {
                it.copy(isImporting = false, current = 0, total = 0)
            }
            if (failures.isEmpty()) {
                _effects.send(ImportUiEffect.ImportFinished)
            } else {
                _uiState.update { it.copy(failedItems = failures) }
            }
        }
    }

    /** 单张重试：成功则从失败列表移除；列表清空后回主画面 */
    fun retryItem(uri: Uri) {
        if (_uiState.value.isImporting) return
        val target = _uiState.value.failedItems.firstOrNull { it.uri == uri } ?: return
        val compressEnabled = _uiState.value.compressEnabled
        viewModelScope.launch {
            _uiState.update {
                it.copy(isImporting = true, current = 1, total = 1)
            }
            importSingle(target.uri, compressEnabled)
                .onSuccess {
                    removeFailure(uri)
                }.onFailure { e ->
                    updateFailure(uri, e)
                }
            _uiState.update {
                it.copy(isImporting = false, current = 0, total = 0)
            }
            if (_uiState.value.failedItems.isEmpty()) {
                _effects.send(ImportUiEffect.ImportFinished)
            }
        }
    }

    /** 全部重试：逐张处理，全部成功后回主画面 */
    fun retryAll() {
        if (_uiState.value.isImporting) return
        val targets = _uiState.value.failedItems
        if (targets.isEmpty()) return
        val compressEnabled = _uiState.value.compressEnabled
        viewModelScope.launch {
            _uiState.update {
                it.copy(isImporting = true, current = 0, total = targets.size)
            }
            var done = 0
            targets.forEach { item ->
                done++
                _uiState.update {
                    it.copy(current = done, total = targets.size)
                }
                importSingle(item.uri, compressEnabled)
                    .onSuccess { removeFailure(item.uri) }
                    .onFailure { e -> updateFailure(item.uri, e) }
            }
            _uiState.update {
                it.copy(isImporting = false, current = 0, total = 0)
            }
            if (_uiState.value.failedItems.isEmpty()) {
                _effects.send(ImportUiEffect.ImportFinished)
            }
        }
    }

    /** 关闭失败明细（留在导入画面空闲态，可继续导入） */
    fun dismissFailures() {
        _uiState.update { it.copy(failedItems = emptyList()) }
    }

    private fun removeFailure(uri: Uri) {
        _uiState.update { state ->
            state.copy(failedItems = state.failedItems.filterNot { it.uri == uri })
        }
    }

    private fun updateFailure(
        uri: Uri,
        e: Throwable,
    ) {
        _uiState.update { state ->
            state.copy(
                failedItems =
                    state.failedItems.map {
                        if (it.uri == uri) {
                            it.copy(
                                errorKind = errorKindOf(e),
                                errorDetail = errorDetailOf(e),
                            )
                        } else {
                            it
                        }
                    },
            )
        }
    }

    private suspend fun importSingle(
        uri: Uri,
        compressEnabled: Boolean,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
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
        }

    private fun errorKindOf(e: Throwable): ImportErrorKind =
        when (e) {
            is SecurityException, is FileNotFoundException, is IOException -> ImportErrorKind.ReadFailed
            is IllegalStateException -> ImportErrorKind.DecodeFailed
            else -> ImportErrorKind.Unknown
        }

    private fun errorDetailOf(e: Throwable): String? = e.message?.takeIf { it.isNotBlank() } ?: e::class.java.simpleName

    class Factory(
        private val repository: ImageRepository,
        private val imageManager: ImageManager,
        private val userPreferences: UserPreferencesRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ImportViewModel(repository, imageManager, userPreferences) as T
    }
}
