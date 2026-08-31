package com.pictureorganizer.ui.imagedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.R
import com.pictureorganizer.data.repository.ImageRepository
import com.pictureorganizer.data.repository.RenameTemplateRepository
import com.pictureorganizer.data.repository.TagRepository
import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.util.file.AppFileManager
import com.pictureorganizer.util.file.RenamePatternApplier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class ImageDetailViewModel(
    initialImageId: String,
    private val repository: ImageRepository,
    private val tagRepository: TagRepository,
    private val renameTemplateRepository: RenameTemplateRepository,
    private val fileManager: AppFileManager,
) : ViewModel() {
    private val currentId = MutableStateFlow(initialImageId)
    private val editor = MutableStateFlow(EditorState())
    private val busy = MutableStateFlow(false)

    private val _effects = Channel<ImageDetailUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val currentItemFlow =
        currentId.flatMapLatest { id ->
            repository.observeItem(id)
        }

    private val siblingsFlow =
        currentItemFlow.flatMapLatest { item ->
            if (item == null) {
                flowOf(emptyList())
            } else {
                repository.observeItems(item.status)
            }
        }

    private val imageBundle =
        combine(
            currentId,
            currentItemFlow,
            siblingsFlow,
        ) { id, item, siblings ->
            Triple(id, item, siblings)
        }

    private val tagsAndTemplates =
        combine(
            tagRepository.observeTags(),
            tagRepository.observeTemplates(),
            renameTemplateRepository.observeAll(),
        ) { libraryTags, templates, renameTemplates ->
            Triple(libraryTags, templates, renameTemplates)
        }

    val uiState: StateFlow<ImageDetailUiState> =
        combine(
            imageBundle,
            editor,
            busy,
            tagsAndTemplates,
        ) { bundle, ed, isBusy, tagBundle ->
            val (id, item, siblings) = bundle
            val (libraryTags, templates, renameTemplates) = tagBundle
            ImageDetailUiState(
                currentId = id,
                current = item,
                siblings = siblings,
                renameDraft =
                    ed.renameDraft.takeIf { ed.renameTouched }
                        ?: (item?.fileNameFromPath() ?: item?.description.orEmpty()),
                tagDraft = ed.tagDraft,
                editingUserTagIndex = ed.editingUserTagIndex,
                libraryTags = libraryTags,
                templates = templates,
                renameTemplates = renameTemplates,
                isBusy = isBusy,
                notFound = item == null,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ImageDetailUiState(currentId = initialImageId),
        )

    fun absoluteFile(item: ImageListItem): File = fileManager.absoluteFile(item.filePath)

    fun onEvent(event: ImageDetailUiEvent) {
        when (event) {
            is ImageDetailUiEvent.SelectSibling -> selectSibling(event.id)
            is ImageDetailUiEvent.RenameDraftChanged ->
                editor.update {
                    it.copy(renameDraft = event.value, renameTouched = true)
                }
            ImageDetailUiEvent.SaveRename -> saveRename()
            is ImageDetailUiEvent.TagDraftChanged ->
                editor.update {
                    it.copy(tagDraft = event.value)
                }
            is ImageDetailUiEvent.StartEditTag -> startEditTag(event.userTagIndex)
            ImageDetailUiEvent.CancelEditTag ->
                editor.update {
                    it.copy(tagDraft = "", editingUserTagIndex = null)
                }
            ImageDetailUiEvent.SaveTag -> saveTag()
            is ImageDetailUiEvent.DeleteTag -> deleteTag(event.userTagIndex)
            is ImageDetailUiEvent.ToggleLibraryTag -> toggleLibraryTag(event.name)
            is ImageDetailUiEvent.ApplyTemplate -> applyTemplate(event.templateId)
            is ImageDetailUiEvent.ApplyRenameTemplate -> applyRenameTemplate(event.templateId)
        }
    }

    private fun selectSibling(id: String) {
        if (id == currentId.value) return
        currentId.value = id
        editor.value = EditorState()
    }

    private fun startEditTag(userTagIndex: Int) {
        val item = uiState.value.current ?: return
        val userTags = ImageListItem.userTagsOf(item.tags)
        val tag = userTags.getOrNull(userTagIndex) ?: return
        editor.update {
            it.copy(tagDraft = tag, editingUserTagIndex = userTagIndex)
        }
    }

    private fun saveRename() {
        val id = currentId.value
        val name =
            editor.value.renameDraft
                .trim()
                .ifEmpty { uiState.value.renameDraft.trim() }
        if (name.isEmpty()) {
            viewModelScope.launch {
                _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_rename_empty))
            }
            return
        }
        viewModelScope.launch {
            busy.value = true
            runCatching { repository.rename(id, name) }
                .onSuccess {
                    editor.update { it.copy(renameTouched = false) }
                    _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_rename_ok))
                }.onFailure { e ->
                    _effects.send(
                        ImageDetailUiEffect.ShowMessageText(
                            e.message ?: "重命名失败",
                        ),
                    )
                }
            busy.value = false
        }
    }

    private fun saveTag() {
        val item = uiState.value.current ?: return
        val draft = editor.value.tagDraft.trim()
        if (draft.isEmpty()) {
            viewModelScope.launch {
                _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_tag_empty))
            }
            return
        }
        if (draft in ImageListItem.STATUS_TAGS) {
            viewModelScope.launch {
                _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_tag_reserved))
            }
            return
        }
        val userTags = ImageListItem.userTagsOf(item.tags).toMutableList()
        val editIndex = editor.value.editingUserTagIndex
        if (editIndex != null) {
            if (editIndex !in userTags.indices) return
            userTags[editIndex] = draft
        } else {
            if (draft in userTags) {
                viewModelScope.launch {
                    _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_tag_exists))
                }
                return
            }
            userTags.add(draft)
        }
        persistTags(item, userTags, clearEditor = true)
    }

    private fun deleteTag(userTagIndex: Int) {
        val item = uiState.value.current ?: return
        val userTags = ImageListItem.userTagsOf(item.tags).toMutableList()
        if (userTagIndex !in userTags.indices) return
        userTags.removeAt(userTagIndex)
        persistTags(item, userTags, clearEditor = true)
    }

    private fun toggleLibraryTag(name: String) {
        val item = uiState.value.current ?: return
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed in ImageListItem.STATUS_TAGS) return
        val userTags = ImageListItem.userTagsOf(item.tags).toMutableList()
        if (trimmed in userTags) {
            userTags.removeAll { it == trimmed }
        } else {
            userTags.add(trimmed)
        }
        persistTags(item, userTags, clearEditor = false)
    }

    private fun applyTemplate(templateId: String) {
        val item = uiState.value.current ?: return
        val template = uiState.value.templates.find { it.id == templateId } ?: return
        val userTags = ImageListItem.userTagsOf(item.tags).toMutableList()
        var changed = false
        template.tagNames.forEach { raw ->
            val name = raw.trim()
            if (name.isEmpty() || name in ImageListItem.STATUS_TAGS) return@forEach
            if (name !in userTags) {
                userTags.add(name)
                changed = true
            }
        }
        if (!changed) {
            viewModelScope.launch {
                _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_template_no_change))
            }
            return
        }
        persistTags(item, userTags, clearEditor = false)
    }

    private fun applyRenameTemplate(templateId: String) {
        val item = uiState.value.current ?: return
        val template = uiState.value.renameTemplates.find { it.id == templateId } ?: return
        val currentName =
            uiState.value.renameDraft.ifBlank {
                item.fileNameFromPath().ifBlank { item.description }
            }
        val next = RenamePatternApplier.applyForItem(template.pattern, item, currentName)
        editor.update { it.copy(renameDraft = next, renameTouched = true) }
        viewModelScope.launch {
            busy.value = true
            runCatching { repository.rename(item.id, next) }
                .onSuccess {
                    editor.update { it.copy(renameTouched = false) }
                    _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_rename_ok))
                }.onFailure { e ->
                    _effects.send(
                        ImageDetailUiEffect.ShowMessageText(e.message ?: "重命名失败"),
                    )
                }
            busy.value = false
        }
    }

    private fun persistTags(
        item: ImageListItem,
        userTags: List<String>,
        clearEditor: Boolean,
    ) {
        val fullTags = listOf(ImageListItem.statusTagFor(item.status)) + userTags
        viewModelScope.launch {
            busy.value = true
            runCatching { repository.updateTags(item.id, fullTags) }
                .onSuccess { exifOk ->
                    if (clearEditor) {
                        editor.value =
                            EditorState(
                                renameDraft = editor.value.renameDraft,
                                renameTouched = editor.value.renameTouched,
                            )
                    }
                    if (exifOk) {
                        _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_tags_ok))
                    } else {
                        _effects.send(ImageDetailUiEffect.ShowMessage(R.string.detail_exif_warn))
                    }
                }.onFailure { e ->
                    _effects.send(
                        ImageDetailUiEffect.ShowMessageText(e.message ?: "标签保存失败"),
                    )
                }
            busy.value = false
        }
    }

    private data class EditorState(
        val renameDraft: String = "",
        val renameTouched: Boolean = false,
        val tagDraft: String = "",
        val editingUserTagIndex: Int? = null,
    )

    class Factory(
        private val imageId: String,
        private val repository: ImageRepository,
        private val tagRepository: TagRepository,
        private val renameTemplateRepository: RenameTemplateRepository,
        private val fileManager: AppFileManager,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ImageDetailViewModel(
                imageId,
                repository,
                tagRepository,
                renameTemplateRepository,
                fileManager,
            ) as T
    }
}

private fun ImageListItem.fileNameFromPath(): String {
    if (filePath.isNotEmpty()) return filePath.substringAfterLast('/')
    return description
}
