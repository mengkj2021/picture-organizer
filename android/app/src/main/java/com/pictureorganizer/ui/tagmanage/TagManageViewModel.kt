package com.pictureorganizer.ui.tagmanage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.R
import com.pictureorganizer.data.repository.TagRepository
import com.pictureorganizer.model.Tag
import com.pictureorganizer.model.TagTemplate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class TagManageViewModel(
    private val tagRepository: TagRepository,
) : ViewModel() {
    private val editor = MutableStateFlow(EditorUi())
    private val _effects = Channel<TagManageUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<TagManageUiState> =
        combine(
            tagRepository.observeTags(),
            tagRepository.observeTemplates(),
            editor,
        ) { tags, templates, ed ->
            TagManageUiState(
                selectedTab = ed.selectedTab,
                tags = tags,
                templates = templates,
                tagDialog = ed.tagDialog,
                templateDialog = ed.templateDialog,
                confirmDeleteTagId = ed.confirmDeleteTagId,
                confirmDeleteTemplateId = ed.confirmDeleteTemplateId,
                isBusy = ed.isBusy,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TagManageUiState(),
        )

    fun onEvent(event: TagManageUiEvent) {
        when (event) {
            is TagManageUiEvent.SelectTab -> editor.update { it.copy(selectedTab = event.tab) }
            TagManageUiEvent.OpenAddTag ->
                editor.update {
                    it.copy(tagDialog = TagDialogState())
                }
            is TagManageUiEvent.OpenEditTag ->
                editor.update {
                    it.copy(tagDialog = TagDialogState(editingId = event.tag.id, name = event.tag.name))
                }
            is TagManageUiEvent.TagNameChanged ->
                editor.update { ed ->
                    ed.copy(tagDialog = ed.tagDialog?.copy(name = event.value))
                }
            TagManageUiEvent.SaveTag -> saveTag()
            TagManageUiEvent.DismissTagDialog -> editor.update { it.copy(tagDialog = null) }
            is TagManageUiEvent.RequestDeleteTag ->
                editor.update {
                    it.copy(confirmDeleteTagId = event.id)
                }
            TagManageUiEvent.ConfirmDeleteTag -> confirmDeleteTag()
            TagManageUiEvent.CancelDeleteTag -> editor.update { it.copy(confirmDeleteTagId = null) }

            TagManageUiEvent.OpenAddTemplate ->
                editor.update {
                    it.copy(templateDialog = TemplateDialogState())
                }
            is TagManageUiEvent.OpenEditTemplate ->
                editor.update {
                    it.copy(
                        templateDialog =
                            TemplateDialogState(
                                editingId = event.template.id,
                                name = event.template.name,
                                selectedTagNames = event.template.tagNames.toSet(),
                                isDefault = event.template.isDefault,
                            ),
                    )
                }
            is TagManageUiEvent.TemplateNameChanged ->
                editor.update { ed ->
                    ed.copy(templateDialog = ed.templateDialog?.copy(name = event.value))
                }
            is TagManageUiEvent.ToggleTemplateTag ->
                editor.update { ed ->
                    val dialog = ed.templateDialog ?: return@update ed
                    val next =
                        if (event.name in dialog.selectedTagNames) {
                            dialog.selectedTagNames - event.name
                        } else {
                            dialog.selectedTagNames + event.name
                        }
                    ed.copy(templateDialog = dialog.copy(selectedTagNames = next))
                }
            is TagManageUiEvent.TemplateDefaultChanged ->
                editor.update { ed ->
                    ed.copy(templateDialog = ed.templateDialog?.copy(isDefault = event.value))
                }
            TagManageUiEvent.SaveTemplate -> saveTemplate()
            TagManageUiEvent.DismissTemplateDialog -> editor.update { it.copy(templateDialog = null) }
            is TagManageUiEvent.SetDefaultTemplate -> setDefault(event.id)
            is TagManageUiEvent.RequestDeleteTemplate ->
                editor.update {
                    it.copy(confirmDeleteTemplateId = event.id)
                }
            TagManageUiEvent.ConfirmDeleteTemplate -> confirmDeleteTemplate()
            TagManageUiEvent.CancelDeleteTemplate ->
                editor.update {
                    it.copy(confirmDeleteTemplateId = null)
                }
        }
    }

    private fun saveTag() {
        val dialog = editor.value.tagDialog ?: return
        val name = dialog.name.trim()
        if (name.isEmpty()) {
            viewModelScope.launch {
                _effects.send(TagManageUiEffect.ShowMessage(R.string.tag_manage_name_empty))
            }
            return
        }
        viewModelScope.launch {
            editor.update { it.copy(isBusy = true) }
            runCatching {
                if (dialog.editingId == null) {
                    tagRepository.insertTag(
                        Tag(id = UUID.randomUUID().toString(), name = name),
                    )
                } else {
                    val existing =
                        tagRepository.getTag(dialog.editingId)
                            ?: error("标签不存在")
                    tagRepository.updateTag(existing.copy(name = name))
                }
            }.onSuccess {
                editor.update { it.copy(tagDialog = null) }
                _effects.send(TagManageUiEffect.ShowMessage(R.string.tag_manage_saved))
            }.onFailure { e ->
                _effects.send(
                    TagManageUiEffect.ShowMessageText(e.message ?: "保存失败"),
                )
            }
            editor.update { it.copy(isBusy = false) }
        }
    }

    private fun confirmDeleteTag() {
        val id = editor.value.confirmDeleteTagId ?: return
        viewModelScope.launch {
            editor.update { it.copy(isBusy = true, confirmDeleteTagId = null) }
            runCatching { tagRepository.deleteTag(id) }
                .onSuccess {
                    _effects.send(TagManageUiEffect.ShowMessage(R.string.tag_manage_tag_deleted))
                }.onFailure { e ->
                    _effects.send(
                        TagManageUiEffect.ShowMessageText(e.message ?: "删除失败"),
                    )
                }
            editor.update { it.copy(isBusy = false) }
        }
    }

    private fun saveTemplate() {
        val dialog = editor.value.templateDialog ?: return
        val name = dialog.name.trim()
        if (name.isEmpty()) {
            viewModelScope.launch {
                _effects.send(TagManageUiEffect.ShowMessage(R.string.tag_manage_name_empty))
            }
            return
        }
        viewModelScope.launch {
            editor.update { it.copy(isBusy = true) }
            runCatching {
                val tagNames = dialog.selectedTagNames.toList().sorted()
                if (dialog.editingId == null) {
                    tagRepository.insertTemplate(
                        TagTemplate(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            tagNames = tagNames,
                            isDefault = dialog.isDefault,
                        ),
                    )
                } else {
                    val existing =
                        tagRepository.getTemplate(dialog.editingId)
                            ?: error("模板不存在")
                    tagRepository.updateTemplate(
                        existing.copy(
                            name = name,
                            tagNames = tagNames,
                            isDefault = dialog.isDefault,
                        ),
                    )
                }
            }.onSuccess {
                editor.update { it.copy(templateDialog = null) }
                _effects.send(TagManageUiEffect.ShowMessage(R.string.tag_manage_saved))
            }.onFailure { e ->
                _effects.send(
                    TagManageUiEffect.ShowMessageText(e.message ?: "保存失败"),
                )
            }
            editor.update { it.copy(isBusy = false) }
        }
    }

    private fun setDefault(id: String) {
        viewModelScope.launch {
            editor.update { it.copy(isBusy = true) }
            runCatching { tagRepository.setDefaultTemplate(id) }
                .onSuccess {
                    _effects.send(TagManageUiEffect.ShowMessage(R.string.tag_manage_default_set))
                }.onFailure { e ->
                    _effects.send(
                        TagManageUiEffect.ShowMessageText(e.message ?: "设置失败"),
                    )
                }
            editor.update { it.copy(isBusy = false) }
        }
    }

    private fun confirmDeleteTemplate() {
        val id = editor.value.confirmDeleteTemplateId ?: return
        viewModelScope.launch {
            editor.update { it.copy(isBusy = true, confirmDeleteTemplateId = null) }
            runCatching { tagRepository.deleteTemplate(id) }
                .onSuccess {
                    _effects.send(TagManageUiEffect.ShowMessage(R.string.tag_manage_template_deleted))
                }.onFailure { e ->
                    _effects.send(
                        TagManageUiEffect.ShowMessageText(e.message ?: "删除失败"),
                    )
                }
            editor.update { it.copy(isBusy = false) }
        }
    }

    private data class EditorUi(
        val selectedTab: TagManageTab = TagManageTab.Tags,
        val tagDialog: TagDialogState? = null,
        val templateDialog: TemplateDialogState? = null,
        val confirmDeleteTagId: String? = null,
        val confirmDeleteTemplateId: String? = null,
        val isBusy: Boolean = false,
    )

    class Factory(
        private val tagRepository: TagRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = TagManageViewModel(tagRepository) as T
    }
}
