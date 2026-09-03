package com.pictureorganizer.util.file

import org.junit.Assert.assertEquals
import org.junit.Test

class RenamePatternApplierTest {
    @Test
    fun apply_replacesNameDateTag_andKeepsExtension() {
        val result =
            RenamePatternApplier.apply(
                pattern = "{name}_{date}_{tag}",
                fileName = "photo.JPG",
                dateYmd = "20260903",
                userTags = listOf("旅行", "家人"),
            )
        assertEquals("photo_20260903_旅行.JPG", result)
    }

    @Test
    fun apply_emptyPattern_fallsBackToBaseName() {
        val result =
            RenamePatternApplier.apply(
                pattern = "   ",
                fileName = "a.png",
                dateYmd = "20260101",
                userTags = emptyList(),
            )
        assertEquals("a.png", result)
    }

    @Test
    fun apply_noExtension_returnsStemOnly() {
        val result =
            RenamePatternApplier.apply(
                pattern = "{tag}-{name}",
                fileName = "noext",
                dateYmd = "20260101",
                userTags = listOf("风景"),
            )
        assertEquals("风景-noext", result)
    }

    @Test
    fun apply_emptyUserTags_tagPlaceholderBecomesEmpty() {
        val result =
            RenamePatternApplier.apply(
                pattern = "{name}{tag}",
                fileName = "x.jpg",
                dateYmd = "20260101",
                userTags = emptyList(),
            )
        assertEquals("x.jpg", result)
    }

    @Test
    fun apply_usesFirstUserTagOnly() {
        val result =
            RenamePatternApplier.apply(
                pattern = "{tag}",
                fileName = "f.webp",
                dateYmd = "20260101",
                userTags = listOf("一", "二"),
            )
        assertEquals("一.webp", result)
    }
}
