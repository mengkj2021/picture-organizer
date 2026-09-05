package com.pictureorganizer.util.image

/**
 * F11：导入原图名判重（纯 JVM）。
 * 比较口径：trim 后大小写不敏感（含扩展名）；空白名永不冲突。
 */
object ImportDuplicateLogic {
    fun normalize(name: String): String = name.trim().lowercase()

    data class PartitionResult<T>(
        /** 可直接导入（不与库、也不与本批已入 auto 的同名冲突） */
        val autoImport: List<T>,
        /** 需询问（与库或本批 auto 中已有同名） */
        val conflicts: List<T>,
    )

    /**
     * 按选择顺序分流：先占位无冲突项（计入 known），冲突项进询问队列。
     * [libraryNormalized] 须已是 [normalize] 后的集合。
     */
    fun <T> partition(
        candidates: List<T>,
        nameOf: (T) -> String,
        libraryNormalized: Set<String>,
    ): PartitionResult<T> {
        val known = libraryNormalized.toMutableSet()
        val auto = mutableListOf<T>()
        val conflicts = mutableListOf<T>()
        for (c in candidates) {
            val raw = nameOf(c)
            val key = normalize(raw)
            if (raw.isBlank() || key !in known) {
                auto += c
                if (raw.isNotBlank()) known += key
            } else {
                conflicts += c
            }
        }
        return PartitionResult(autoImport = auto, conflicts = conflicts)
    }
}
