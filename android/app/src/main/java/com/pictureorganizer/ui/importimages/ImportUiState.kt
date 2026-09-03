package com.pictureorganizer.ui.importimages

import android.net.Uri

/** 导入失败项：保留原 Uri 以便单张重试 */
data class FailedImportItem(
    val uri: Uri,
    val displayName: String,
    val errorKind: ImportErrorKind,
    val errorDetail: String?,
)

/** 失败原因分类（本地化文案由 UI 层经 messageResId 映射） */
enum class ImportErrorKind(
    val messageResId: Int,
) {
    ReadFailed(com.pictureorganizer.R.string.import_error_read),
    DecodeFailed(com.pictureorganizer.R.string.import_error_decode),
    Unknown(com.pictureorganizer.R.string.import_error_unknown),
}

data class ImportUiState(
    val isImporting: Boolean = false,
    val current: Int = 0,
    val total: Int = 0,
    val compressEnabled: Boolean = true,
    /** 非空且不在导入中时展示失败明细弹窗 */
    val failedItems: List<FailedImportItem> = emptyList(),
)
