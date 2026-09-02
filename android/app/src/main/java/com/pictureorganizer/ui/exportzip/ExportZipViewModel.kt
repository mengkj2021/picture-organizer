package com.pictureorganizer.ui.exportzip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.R
import com.pictureorganizer.data.repository.ImageRepository
import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import com.pictureorganizer.model.TagFilterCriteria
import com.pictureorganizer.model.matchesFilter
import com.pictureorganizer.model.sortedByFilter
import com.pictureorganizer.util.file.AppFileManager
import com.pictureorganizer.util.file.ZipExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExportZipViewModel(
    private val imageRepository: ImageRepository,
    private val fileManager: AppFileManager,
) : ViewModel() {
    private val filterState = MutableStateFlow(TagFilterCriteria())
    private val session = MutableStateFlow(SessionState())

    private val _effects = Channel<ExportZipUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<ExportZipUiState> =
        combine(
            imageRepository.observeItems(ImageStatus.Confirmed),
            filterState,
            session,
        ) { items, filter, sess ->
            val matched =
                items
                    .filter { it.matchesFilter(filter) }
                    .sortedByFilter(filter.sort)
            ExportZipUiState(
                filter = filter,
                confirmedCount = items.size,
                matchedCount = matched.size,
                isExporting = sess.isExporting,
                progressCurrent = sess.progressCurrent,
                progressTotal = sess.progressTotal,
                results = sess.results,
                errorMessage = sess.errorMessage,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExportZipUiState(),
        )

    fun onEvent(event: ExportZipUiEvent) {
        when (event) {
            is ExportZipUiEvent.SetFilter -> {
                filterState.value = event.filter
                session.update { it.copy(results = emptyList(), errorMessage = null) }
            }
            ExportZipUiEvent.ClearFilter -> {
                filterState.value = TagFilterCriteria()
                session.update { it.copy(results = emptyList(), errorMessage = null) }
            }
            ExportZipUiEvent.StartExport -> startExport()
            ExportZipUiEvent.ClearError -> session.update { it.copy(errorMessage = null) }
        }
    }

    fun requestShare(result: ExportZipResult) {
        viewModelScope.launch {
            _effects.send(ExportZipUiEffect.ShareFile(result.absolutePath, result.fileName))
        }
    }

    private fun startExport() {
        if (session.value.isExporting) return
        viewModelScope.launch {
            session.update {
                it.copy(
                    isExporting = true,
                    progressCurrent = 0,
                    progressTotal = 0,
                    results = emptyList(),
                    errorMessage = null,
                )
            }
            runCatching {
                withContext(Dispatchers.IO) {
                    val all = imageRepository.getItems(ImageStatus.Confirmed)
                    val filter = filterState.value
                    val matched =
                        all
                            .filter { it.matchesFilter(filter) }
                            .sortedByFilter(filter.sort)
                    val groups = buildGroups(matched, filter)
                    if (groups.isEmpty()) {
                        error("没有可打包的图片")
                    }
                    session.update { it.copy(progressTotal = groups.size) }
                    val dateToken = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                    val out = mutableListOf<ExportZipResult>()
                    groups.forEachIndexed { index, (label, images) ->
                        session.update { it.copy(progressCurrent = index + 1) }
                        val token = ZipExporter.sanitizeFileToken(label)
                        val zipName = "export_${token}_$dateToken.zip"
                        val zipFile = File(fileManager.exportsDir(), zipName)
                        val entries =
                            images.map { item ->
                                val name =
                                    item.filePath.substringAfterLast('/').ifBlank {
                                        item.description
                                    }
                                name to fileManager.absoluteFile(item.filePath)
                            }
                        val count = ZipExporter.zipFiles(zipFile, entries)
                        if (count == 0) {
                            zipFile.delete()
                        } else {
                            out.add(
                                ExportZipResult(
                                    label = label,
                                    fileName = zipName,
                                    absolutePath = zipFile.absolutePath,
                                    entryCount = count,
                                ),
                            )
                        }
                    }
                    if (out.isEmpty()) error("打包结果为空")
                    out
                }
            }.onSuccess { results ->
                session.update {
                    it.copy(isExporting = false, results = results, progressCurrent = 0)
                }
                _effects.send(ExportZipUiEffect.ShowMessage(R.string.export_zip_done))
            }.onFailure { e ->
                session.update {
                    it.copy(
                        isExporting = false,
                        errorMessage = e.message ?: "打包失败",
                        progressCurrent = 0,
                    )
                }
                _effects.send(
                    ExportZipUiEffect.ShowMessageText(e.message ?: "打包失败"),
                )
            }
        }
    }

    /**
     * 无筛选：每个出现过的用户标签一包 + 未打标签包（若有）。
     * 有筛选：每个选中标签一包；勾了未打标签则再加未打标签包。
     */
    private fun buildGroups(
        matched: List<ImageListItem>,
        filter: TagFilterCriteria,
    ): List<Pair<String, List<ImageListItem>>> {
        if (matched.isEmpty()) return emptyList()
        val groups = linkedMapOf<String, MutableList<ImageListItem>>()
        if (!filter.isActive) {
            matched.forEach { item ->
                val user = ImageListItem.userTagsOf(item.tags)
                if (user.isEmpty()) {
                    groups.getOrPut(UNTAGGED_LABEL) { mutableListOf() }.add(item)
                } else {
                    user.forEach { tag ->
                        groups.getOrPut(tag) { mutableListOf() }.add(item)
                    }
                }
            }
        } else {
            filter.selectedTagNames.sorted().forEach { tag ->
                val list = matched.filter { tag in ImageListItem.userTagsOf(it.tags) }
                if (list.isNotEmpty()) groups[tag] = list.toMutableList()
            }
            if (filter.includeUntagged) {
                val list = matched.filter { ImageListItem.userTagsOf(it.tags).isEmpty() }
                if (list.isNotEmpty()) groups[UNTAGGED_LABEL] = list.toMutableList()
            }
        }
        return groups.map { it.key to it.value }
    }

    private data class SessionState(
        val isExporting: Boolean = false,
        val progressCurrent: Int = 0,
        val progressTotal: Int = 0,
        val results: List<ExportZipResult> = emptyList(),
        val errorMessage: String? = null,
    )

    class Factory(
        private val imageRepository: ImageRepository,
        private val fileManager: AppFileManager,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ExportZipViewModel(imageRepository, fileManager) as T
    }

    companion object {
        const val UNTAGGED_LABEL = "未打标签"
    }
}
