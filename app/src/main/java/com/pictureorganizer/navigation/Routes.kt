package com.pictureorganizer.navigation

object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val IMPORT_IMAGES = "import-images"
    const val IMAGE_DETAIL = "image-detail/{imageId}"

    fun imageDetail(imageId: String): String = "image-detail/$imageId"
}
