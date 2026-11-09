package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.data.models.LibraryItem
import com.unifiedserver.dashboard.data.models.LibraryResponse
import com.unifiedserver.dashboard.data.repository.DownloadsMonitor
import com.unifiedserver.dashboard.data.repository.LibraryRepository
import com.unifiedserver.dashboard.ui.utils.LibraryFilter
import com.unifiedserver.dashboard.ui.utils.LibrarySort
import com.unifiedserver.dashboard.ui.utils.filterLibrary
import com.unifiedserver.dashboard.ui.utils.formatBytes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StorageUiState(
    val library: LibraryResponse? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val isRescanning: Boolean = false,
    val filter: LibraryFilter = LibraryFilter.ALL,
    val sort: LibrarySort = LibrarySort.SIZE,
    val query: String = "",
    /** Item whose detail sheet is open (list summary until [detail] arrives). */
    val selected: LibraryItem? = null,
    val detail: LibraryItem? = null,
    val detailError: String? = null,
    val isDeleting: Boolean = false,
    val isCleaning: Boolean = false,
    val snackbarMessage: String? = null
) {
    val visibleItems: List<LibraryItem>
        get() = library?.items?.let { filterLibrary(it, filter, sort, query) }.orEmpty()
}

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val repository: LibraryRepository,
    private val downloads: DownloadsMonitor
) : ViewModel() {
    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var detailJob: Job? = null

    /** [rescan] bypasses the server's 30 s scan cache. */
    fun load(rescan: Boolean = false) {
        if (loadJob?.isActive == true && !rescan) return
        loadJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = _uiState.value.library == null, isRescanning = rescan)
        loadJob = viewModelScope.launch {
            repository.getLibrary(rescan).onSuccess {
                _uiState.value = _uiState.value.copy(library = it, error = null, isLoading = false, isRescanning = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message ?: "Couldn't read the media library.",
                    isLoading = false, isRescanning = false)
            }
        }
    }

    fun setFilter(filter: LibraryFilter) { _uiState.value = _uiState.value.copy(filter = filter) }
    fun setSort(sort: LibrarySort) { _uiState.value = _uiState.value.copy(sort = sort) }
    fun setQuery(query: String) { _uiState.value = _uiState.value.copy(query = query) }

    fun open(item: LibraryItem) {
        detailJob?.cancel()
        _uiState.value = _uiState.value.copy(selected = item, detail = null, detailError = null)
        detailJob = viewModelScope.launch {
            repository.getItem(item.id).onSuccess {
                if (_uiState.value.selected?.id == item.id) _uiState.value = _uiState.value.copy(detail = it)
            }.onFailure {
                if (_uiState.value.selected?.id == item.id) _uiState.value = _uiState.value.copy(detailError = it.message)
            }
        }
    }

    fun close() {
        if (_uiState.value.isDeleting) return
        detailJob?.cancel()
        _uiState.value = _uiState.value.copy(selected = null, detail = null, detailError = null)
    }

    fun delete(item: LibraryItem) {
        if (_uiState.value.isDeleting || !item.deletable) return
        _uiState.value = _uiState.value.copy(isDeleting = true)
        viewModelScope.launch {
            repository.deleteItem(item.id).onSuccess {
                _uiState.value = _uiState.value.copy(isDeleting = false, selected = null, detail = null,
                    snackbarMessage = "Deleted ${item.displayTitle} · freed ${formatBytes(it.freed)}")
                if (it.removedTorrent) downloads.refresh()
                load(rescan = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isDeleting = false, snackbarMessage = "Delete failed: ${it.message}")
            }
        }
    }

    fun cleanup() {
        if (_uiState.value.isCleaning) return
        _uiState.value = _uiState.value.copy(isCleaning = true)
        viewModelScope.launch {
            repository.cleanup().onSuccess {
                _uiState.value = _uiState.value.copy(isCleaning = false,
                    snackbarMessage = "Removed ${it.removed} junk item(s) · freed ${formatBytes(it.freed)}")
                load(rescan = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isCleaning = false, snackbarMessage = "Cleanup failed: ${it.message}")
            }
        }
    }

    fun clearSnackbar() { _uiState.value = _uiState.value.copy(snackbarMessage = null) }
}
