package com.pictureorganizer.navigation

import android.net.Uri
import com.pictureorganizer.model.TagFilterCriteria

object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val IMPORT_IMAGES = "import-images"
    const val IMAGE_DETAIL = "image-detail/{imageId}"
    const val SETTINGS = "settings"
    const val TAG_MANAGE = "tag-manage"
    const val FILTER = "filter?tags={tags}&untagged={untagged}&q={q}&sort={sort}"
    const val TUTORIAL = "tutorial?fromSettings={fromSettings}"
    const val RENAME_TEMPLATE_MANAGE = "rename-template-manage"
    const val DEFAULT_TAGS = "default-tags"
    const val EXPORT_ZIP = "export-zip"
    const val OSS_LICENSES = "oss-licenses"

    fun imageDetail(imageId: String): String = "image-detail/$imageId"

    fun filter(criteria: TagFilterCriteria = TagFilterCriteria()): String {
        val tags = Uri.encode(criteria.encodeTagsParam())
        val q = Uri.encode(criteria.nameContains)
        return "filter?tags=$tags&untagged=${criteria.includeUntagged}&q=$q&sort=${criteria.sort.name}"
    }

    fun tutorial(fromSettings: Boolean = false): String = "tutorial?fromSettings=$fromSettings"
}
