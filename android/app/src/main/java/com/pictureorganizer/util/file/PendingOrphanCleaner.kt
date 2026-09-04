package com.pictureorganizer.util.file

/**
 * F10：识别 `images/pending/` 下无 DB 记录的孤儿文件名（纯 JVM，可单测）。
 */
object PendingOrphanCleaner {
    private const val PENDING_PREFIX = "${AppFileManager.DIR_PENDING}/"

    /**
     * @param pendingFileNames `pending/` 目录下的文件名（不含路径）
     * @param knownRelativePaths DB 中全部 `filePath`（相对 `images/`，如 `pending/a.jpg`）
     * @return 应删除的孤儿文件名
     */
    fun orphanFileNames(
        pendingFileNames: Collection<String>,
        knownRelativePaths: Collection<String>,
    ): List<String> {
        val knownInPending =
            knownRelativePaths
                .asSequence()
                .filter { it.startsWith(PENDING_PREFIX) }
                .map { it.removePrefix(PENDING_PREFIX) }
                .filter { it.isNotEmpty() && '/' !in it }
                .toHashSet()
        return pendingFileNames.filter { name ->
            name.isNotEmpty() && '/' !in name && name !in knownInPending
        }
    }
}
