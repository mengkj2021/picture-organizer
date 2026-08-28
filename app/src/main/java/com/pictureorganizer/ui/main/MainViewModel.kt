package com.pictureorganizer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.data.repository.ImageRepository
import com.pictureorganizer.model.ImageStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ImageRepository
) : ViewModel() {

    private val _effects = Channel<MainUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val editState = kotlinx.coroutines.flow.MutableStateFlow(EditUiState())

    val uiState: StateFlow<MainUiState> = combine(
        repository.observeItems(ImageStatus.Pending),
        repository.observeItems(ImageStatus.Confirmed),
        repository.observeItems(ImageStatus.NoModify),
        editState
    ) { pending, confirmed, noModify, edit ->
        MainUiState(
            selectedTab = edit.selectedTab,
            isEditMode = edit.isEditMode,
            selectedIds = edit.selectedIds,
            pendingItems = pending,
            confirmedItems = confirmed,
            noModifyItems = noModify
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiState()
    )

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.SelectTab -> selectTab(event.tab)
            MainUiEvent.ToggleEditMode -> toggleEditMode()
            MainUiEvent.SelectAll -> selectAll()
            is MainUiEvent.ToggleSelect -> toggleSelect(event.id)
            is MainUiEvent.MoveSelectedTo -> moveSelectedTo(event.status)
            MainUiEvent.ImportImages -> navigateToImport()
            MainUiEvent.DeleteSelected -> deleteSelected()
        }
    }

    private fun selectTab(tab: MainTab) {
        editState.value = editState.value.copy(
            selectedTab = tab,
            isEditMode = false,
            selectedIds = emptySet()
        )
    }

    private fun toggleEditMode() {
        val current = editState.value
        editState.value = if (current.isEditMode) {
            current.copy(isEditMode = false, selectedIds = emptySet())
        } else {
            current.copy(isEditMode = true)
        }
    }

    private fun selectAll() {
        val state = uiState.value
        if (!state.isEditMode) return
        editState.value = editState.value.copy(
            selectedIds = state.itemsForTab(state.selectedTab).map { it.id }.toSet()
        )
    }

    private fun toggleSelect(id: String) {
        val current = editState.value
        if (!current.isEditMode) return
        val newIds = if (id in current.selectedIds) {
            current.selectedIds - id
        } else {
            current.selectedIds + id
        }
        editState.value = current.copy(selectedIds = newIds)
    }

    private fun moveSelectedTo(targetStatus: ImageStatus) {
        val state = uiState.value
        if (!state.isEditMode || state.selectedIds.isEmpty()) return
        val fromStatus = statusForTab(state.selectedTab)
        if (fromStatus == targetStatus) return
        val ids = state.selectedIds
        viewModelScope.launch {
            repository.moveItems(ids, fromStatus, targetStatus)
            editState.value = editState.value.copy(selectedIds = emptySet())
        }
    }

    private fun deleteSelected() {
        val state = uiState.value
        if (!state.isEditMode || state.selectedIds.isEmpty()) return
        if (state.selectedTab != MainTab.NoModify) return
        val ids = state.selectedIds
        viewModelScope.launch {
            repository.deleteItems(ids)
            editState.value = editState.value.copy(
                isEditMode = false,
                selectedIds = emptySet()
            )
        }
    }

    private fun navigateToImport() {
        viewModelScope.launch {
            _effects.send(MainUiEffect.NavigateToImport)
        }
    }

    private fun statusForTab(tab: MainTab): ImageStatus = when (tab) {
        MainTab.Pending -> ImageStatus.Pending
        MainTab.Confirmed -> ImageStatus.Confirmed
        MainTab.NoModify -> ImageStatus.NoModify
    }

    private data class EditUiState(
        val selectedTab: MainTab = MainTab.Pending,
        val isEditMode: Boolean = false,
        val selectedIds: Set<String> = emptySet()
    )

    class Factory(
        private val repository: ImageRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
