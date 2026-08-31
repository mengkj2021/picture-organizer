package com.pictureorganizer.ui.renametemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.R
import com.pictureorganizer.data.repository.RenameTemplateRepository
import com.pictureorganizer.model.RenameTemplate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RenameTemplateManageViewModel(
    private val repository: RenameTemplateRepository,
) : ViewModel() {
    private val editor = MutableStateFlow(Editor())
    private val _effects = Channel<RenameTemplateManageUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<RenameTemplateManageUiState> =
        combine(
            repository.observeAll(),
            editor,
        ) { templates, ed ->
            RenameTemplateManageUiState(
                templates = templates,
                dialog = ed.dialog,
                confirmDeleteId = ed.confirmDeleteId,
                isBusy = ed.isBusy,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RenameTemplateManageUiState(),
        )

    fun onEvent(event: RenameTemplateManageUiEvent) {
        when (event) {
            RenameTemplateManageUiEvent.OpenAdd ->
                editor.update {
                    it.copy(dialog = RenameTemplateDialogState())
                }
            is RenameTemplateManageUiEvent.OpenEdit ->
                editor.update {
                    it.copy(
                        dialog =
                            RenameTemplateDialogState(
                                editingId = event.template.id,
                                name = event.template.name,
                                pattern = event.template.pattern,
                                isDefault = event.template.isDefault,
                            ),
                    )
                }
            is RenameTemplateManageUiEvent.NameChanged ->
                editor.update { ed ->
                    ed.copy(dialog = ed.dialog?.copy(name = event.value))
                }
            is RenameTemplateManageUiEvent.PatternChanged ->
                editor.update { ed ->
                    ed.copy(dialog = ed.dialog?.copy(pattern = event.value))
                }
            is RenameTemplateManageUiEvent.DefaultChanged ->
                editor.update { ed ->
                    ed.copy(dialog = ed.dialog?.copy(isDefault = event.value))
                }
            RenameTemplateManageUiEvent.Save -> save()
            RenameTemplateManageUiEvent.DismissDialog -> editor.update { it.copy(dialog = null) }
            is RenameTemplateManageUiEvent.SetDefault -> setDefault(event.id)
            is RenameTemplateManageUiEvent.RequestDelete ->
                editor.update {
                    it.copy(confirmDeleteId = event.id)
                }
            RenameTemplateManageUiEvent.ConfirmDelete -> confirmDelete()
            RenameTemplateManageUiEvent.CancelDelete ->
                editor.update {
                    it.copy(confirmDeleteId = null)
                }
        }
    }

    private fun save() {
        val dialog = editor.value.dialog ?: return
        viewModelScope.launch {
            editor.update { it.copy(isBusy = true) }
            runCatching {
                if (dialog.editingId == null) {
                    repository.insert(
                        RenameTemplate(
                            id = "",
                            name = dialog.name,
                            pattern = dialog.pattern,
                            isDefault = dialog.isDefault,
                        ),
                    )
                } else {
                    val existing =
                        repository.getById(dialog.editingId)
                            ?: error("模板不存在")
                    repository.update(
                        existing.copy(
                            name = dialog.name,
                            pattern = dialog.pattern,
                            isDefault = dialog.isDefault,
                        ),
                    )
                }
            }.onSuccess {
                editor.update { it.copy(dialog = null, isBusy = false) }
                _effects.send(RenameTemplateManageUiEffect.ShowMessage(R.string.tag_manage_saved))
            }.onFailure { e ->
                editor.update { it.copy(isBusy = false) }
                _effects.send(
                    RenameTemplateManageUiEffect.ShowMessageText(e.message ?: "保存失败"),
                )
            }
        }
    }

    private fun setDefault(id: String) {
        viewModelScope.launch {
            runCatching { repository.setDefault(id) }
                .onSuccess {
                    _effects.send(
                        RenameTemplateManageUiEffect.ShowMessage(R.string.tag_manage_default_set),
                    )
                }.onFailure { e ->
                    _effects.send(
                        RenameTemplateManageUiEffect.ShowMessageText(e.message ?: "设置失败"),
                    )
                }
        }
    }

    private fun confirmDelete() {
        val id = editor.value.confirmDeleteId ?: return
        viewModelScope.launch {
            editor.update { it.copy(isBusy = true, confirmDeleteId = null) }
            runCatching { repository.delete(id) }
                .onSuccess {
                    _effects.send(
                        RenameTemplateManageUiEffect.ShowMessage(
                            R.string.rename_template_deleted,
                        ),
                    )
                }.onFailure { e ->
                    _effects.send(
                        RenameTemplateManageUiEffect.ShowMessageText(e.message ?: "删除失败"),
                    )
                }
            editor.update { it.copy(isBusy = false) }
        }
    }

    private data class Editor(
        val dialog: RenameTemplateDialogState? = null,
        val confirmDeleteId: String? = null,
        val isBusy: Boolean = false,
    )

    class Factory(
        private val repository: RenameTemplateRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = RenameTemplateManageViewModel(repository) as T
    }
}
