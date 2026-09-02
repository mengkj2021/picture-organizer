package com.pictureorganizer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pictureorganizer.data.repository.ImageRepository
import com.pictureorganizer.data.repository.TagRepository
import com.pictureorganizer.model.ImageListSort
import com.pictureorganizer.model.ImageStatus
import com.pictureorganizer.model.TagFilterCriteria
import com.pictureorganizer.model.matchesFilter
import com.pictureorganizer.model.sortedByFilter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max

class MainViewModel(
    private val repository: ImageRepository,
    private val tagRepository: TagRepository,
) : ViewModel() {
    private val _effects = Channel<MainUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val listUiState = MutableStateFlow(ListUiState())

    val uiState: StateFlow<MainUiState> =
        combine(
            repository.observeItems(ImageStatus.Pending),
            repository.observeItems(ImageStatus.Confirmed),
            repository.observeItems(ImageStatus.NoModify),
            tagRepository.observeTags(),
            listUiState,
        ) { pending, confirmed, noModify, tags, ui ->
            val tabFilter = ui.filterFor(ui.selectedTab)
            val source =
                when (ui.selectedTab) {
                    MainTab.Pending -> pending
                    MainTab.Confirmed -> confirmed
                    MainTab.NoModify -> noModify
                }
            val criteria =
                TagFilterCriteria(
                    selectedTagNames = tabFilter.selectedTagNames,
                    includeUntagged = tabFilter.includeUntagged,
                    nameContains = tabFilter.nameContains,
                    sort = tabFilter.sort,
                )
            val filtered =
                source
                    .filter { it.matchesFilter(criteria) }
                    .sortedByFilter(criteria.sort)
            val totalCount = filtered.size
            val totalPages = max(1, (totalCount + MAIN_PAGE_SIZE - 1) / MAIN_PAGE_SIZE)
            val pageIndex = tabFilter.pageIndex.coerceIn(0, totalPages - 1)
            val pageItems =
                filtered
                    .drop(pageIndex * MAIN_PAGE_SIZE)
                    .take(MAIN_PAGE_SIZE)

            MainUiState(
                selectedTab = ui.selectedTab,
                isEditMode = ui.isEditMode,
                selectedIds = ui.selectedIds,
                availableTags = tags.map { it.name },
                filter = criteria,
                pageItems = pageItems,
                pageIndex = pageIndex,
                totalCount = totalCount,
                totalPages = totalPages,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState(),
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
            is MainUiEvent.SetTagFilter -> setTagFilter(event.criteria)
            MainUiEvent.ClearTagFilter -> clearTagFilter()
            MainUiEvent.PrevPage -> changePage(-1)
            MainUiEvent.NextPage -> changePage(1)
        }
    }

    private fun selectTab(tab: MainTab) {
        listUiState.value =
            listUiState.value.copy(
                selectedTab = tab,
                isEditMode = false,
                selectedIds = emptySet(),
            )
    }

    private fun toggleEditMode() {
        val current = listUiState.value
        listUiState.value =
            if (current.isEditMode) {
                current.copy(isEditMode = false, selectedIds = emptySet())
            } else {
                current.copy(isEditMode = true)
            }
    }

    private fun selectAll() {
        val state = uiState.value
        if (!state.isEditMode) return
        listUiState.value =
            listUiState.value.copy(
                selectedIds = state.pageItems.map { it.id }.toSet(),
            )
    }

    private fun toggleSelect(id: String) {
        val current = listUiState.value
        if (!current.isEditMode) return
        val newIds =
            if (id in current.selectedIds) {
                current.selectedIds - id
            } else {
                current.selectedIds + id
            }
        listUiState.value = current.copy(selectedIds = newIds)
    }

    private fun setTagFilter(criteria: TagFilterCriteria) {
        val current = listUiState.value
        listUiState.value =
            current
                .withFilter(
                    current.selectedTab,
                    TabFilterState(
                        selectedTagNames = criteria.selectedTagNames,
                        includeUntagged = criteria.includeUntagged,
                        nameContains = criteria.nameContains,
                        sort = criteria.sort,
                        pageIndex = 0,
                    ),
                ).copy(selectedIds = emptySet())
    }

    private fun clearTagFilter() {
        setTagFilter(TagFilterCriteria())
    }

    private fun changePage(delta: Int) {
        val state = uiState.value
        val newIndex = (state.pageIndex + delta).coerceIn(0, state.totalPages - 1)
        if (newIndex == state.pageIndex) return
        val current = listUiState.value
        val filter = current.filterFor(current.selectedTab).copy(pageIndex = newIndex)
        listUiState.value =
            current
                .withFilter(current.selectedTab, filter)
                .copy(selectedIds = emptySet())
    }

    private fun moveSelectedTo(targetStatus: ImageStatus) {
        val state = uiState.value
        if (!state.isEditMode || state.selectedIds.isEmpty()) return
        val fromStatus = statusForTab(state.selectedTab)
        if (fromStatus == targetStatus) return
        val ids = state.selectedIds
        viewModelScope.launch {
            repository.moveItems(ids, fromStatus, targetStatus)
            listUiState.value = listUiState.value.copy(selectedIds = emptySet())
        }
    }

    private fun deleteSelected() {
        val state = uiState.value
        if (!state.isEditMode || state.selectedIds.isEmpty()) return
        if (state.selectedTab != MainTab.NoModify) return
        val ids = state.selectedIds
        viewModelScope.launch {
            repository.deleteItems(ids)
            listUiState.value =
                listUiState.value.copy(
                    isEditMode = false,
                    selectedIds = emptySet(),
                )
        }
    }

    private fun navigateToImport() {
        viewModelScope.launch {
            _effects.send(MainUiEffect.NavigateToImport)
        }
    }

    private fun statusForTab(tab: MainTab): ImageStatus =
        when (tab) {
            MainTab.Pending -> ImageStatus.Pending
            MainTab.Confirmed -> ImageStatus.Confirmed
            MainTab.NoModify -> ImageStatus.NoModify
        }

    private data class TabFilterState(
        val selectedTagNames: Set<String> = emptySet(),
        val includeUntagged: Boolean = false,
        val nameContains: String = "",
        val sort: ImageListSort = ImageListSort.ImportedAtDesc,
        val pageIndex: Int = 0,
    )

    private data class ListUiState(
        val selectedTab: MainTab = MainTab.Pending,
        val isEditMode: Boolean = false,
        val selectedIds: Set<String> = emptySet(),
        val pendingFilter: TabFilterState = TabFilterState(),
        val confirmedFilter: TabFilterState = TabFilterState(),
        val noModifyFilter: TabFilterState = TabFilterState(),
    ) {
        fun filterFor(tab: MainTab): TabFilterState =
            when (tab) {
                MainTab.Pending -> pendingFilter
                MainTab.Confirmed -> confirmedFilter
                MainTab.NoModify -> noModifyFilter
            }

        fun withFilter(
            tab: MainTab,
            filter: TabFilterState,
        ): ListUiState =
            when (tab) {
                MainTab.Pending -> copy(pendingFilter = filter)
                MainTab.Confirmed -> copy(confirmedFilter = filter)
                MainTab.NoModify -> copy(noModifyFilter = filter)
            }
    }

    class Factory(
        private val repository: ImageRepository,
        private val tagRepository: TagRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(repository, tagRepository) as T
    }
}
