package com.pictureorganizer.ui.importimages

data class ImportUiState(
    val isImporting: Boolean = false,
    val current: Int = 0,
    val total: Int = 0
)
