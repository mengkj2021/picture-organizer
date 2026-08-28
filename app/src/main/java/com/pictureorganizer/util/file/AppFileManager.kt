package com.pictureorganizer.util.file

import android.content.Context
import android.net.Uri
import com.pictureorganizer.model.ImageStatus
import java.io.File
import java.io.FileOutputStream

/**
 * App 私有目录：`filesDir/images/{pending,confirmed,no_modify}/`。
 */
class AppFileManager(private val context: Context) {

    fun imagesRoot(): File {
        val dir = File(context.filesDir, IMAGES_DIR_NAME)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun dirFor(status: ImageStatus): File {
        val dir = File(imagesRoot(), status.toDirName())
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun ensureAllDirs() {
        ImageStatus.entries.forEach { dirFor(it) }
    }

    fun createDestFile(status: ImageStatus, fileName: String): File {
        return File(dirFor(status), fileName)
    }

    fun absoluteFile(relativePath: String): File {
        return File(imagesRoot(), relativePath)
    }

    fun relativePath(status: ImageStatus, fileName: String): String {
        return "${status.toDirName()}/$fileName"
    }

    fun copyFromUri(uri: Uri, destFile: File) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        } ?: error("无法读取所选文件")
    }

    /**
     * 将文件从当前相对路径搬到目标状态目录，返回新的相对路径。
     */
    fun moveToStatus(currentRelativePath: String, fileName: String, to: ImageStatus): String {
        val source = absoluteFile(currentRelativePath)
        val dest = createDestFile(to, fileName)
        if (source.absolutePath == dest.absolutePath) {
            return relativePath(to, fileName)
        }
        if (!source.exists()) {
            error("源文件不存在: $currentRelativePath")
        }
        dest.parentFile?.mkdirs()
        if (dest.exists()) dest.delete()
        val moved = source.renameTo(dest)
        if (!moved) {
            source.copyTo(dest, overwrite = true)
            source.delete()
        }
        return relativePath(to, fileName)
    }

    fun deleteRelative(relativePath: String) {
        val file = absoluteFile(relativePath)
        if (file.exists()) {
            file.delete()
        }
    }

    companion object {
        const val IMAGES_DIR_NAME = "images"
        const val DIR_PENDING = "pending"
        const val DIR_CONFIRMED = "confirmed"
        const val DIR_NO_MODIFY = "no_modify"
    }
}

fun ImageStatus.toDirName(): String = when (this) {
    ImageStatus.Pending -> AppFileManager.DIR_PENDING
    ImageStatus.Confirmed -> AppFileManager.DIR_CONFIRMED
    ImageStatus.NoModify -> AppFileManager.DIR_NO_MODIFY
}
