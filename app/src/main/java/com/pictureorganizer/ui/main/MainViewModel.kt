package com.pictureorganizer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.R
import com.pictureorganizer.data.repository.ImageRepository
import com.pictureorganizer.data.repository.MockImageRepository
import com.pictureorganizer.model.ImageStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ImageRepository = MockImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _effects = Channel<MainUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        refreshLists()
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.SelectTab -> selectTab(event.tab)
            MainUiEvent.ToggleEditMode -> toggleEditMode()
            MainUiEvent.SelectAll -> selectAll()
            is MainUiEvent.ToggleSelect -> toggleSelect(event.id)
            is MainUiEvent.MoveSelectedTo -> moveSelectedTo(event.status)
            MainUiEvent.ImportImages -> showComingSoon()
            MainUiEvent.DeleteSelected -> showComingSoon()
        }
    }

    private fun selectTab(tab: MainTab) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                isEditMode = false,
                selectedIds = emptySet()
            )
        }
    }

    private fun toggleEditMode() {
        _uiState.update { state ->
            if (state.isEditMode) {
                state.copy(isEditMode = false, selectedIds = emptySet())
            } else {
                state.copy(isEditMode = true)
            }
        }
    }

    private fun selectAll() {
        _uiState.update { state ->
            if (!state.isEditMode) return@update state
            state.copy(
                selectedIds = state.itemsForTab(state.selectedTab).map { it.id }.toSet()
            )
        }
    }

    private fun toggleSelect(id: String) {
        _uiState.update { state ->
            if (!state.isEditMode) return@update state
            val newIds = if (id in state.selectedIds) {
                state.selectedIds - id
            } else {
                state.selectedIds + id
            }
            state.copy(selectedIds = newIds)
        }
    }

    private fun moveSelectedTo(targetStatus: ImageStatus) {
        val state = _uiState.value
        if (!state.isEditMode || state.selectedIds.isEmpty()) return

        val fromStatus = statusForTab(state.selectedTab)
        if (fromStatus == targetStatus) return

        repository.moveItems(state.selectedIds, fromStatus, targetStatus)
        refreshLists()
        _uiState.update {
            it.copy(selectedIds = emptySet())
        }
    }

    private fun showComingSoon() {
        viewModelScope.launch {
            _effects.send(MainUiEffect.ShowSnackbar(R.string.feature_coming_soon))
        }
    }

    private fun refreshLists() {
        _uiState.update { state ->
            state.copy(
                pendingItems = repository.getItems(ImageStatus.Pending),
                confirmedItems = repository.getItems(ImageStatus.Confirmed),
                noModifyItems = repository.getItems(ImageStatus.NoModify)
            )
        }
    }

    private fun statusForTab(tab: MainTab): ImageStatus = when (tab) {
        MainTab.Pending -> ImageStatus.Pending
        MainTab.Confirmed -> ImageStatus.Confirmed
        MainTab.NoModify -> ImageStatus.NoModify
    }
}
