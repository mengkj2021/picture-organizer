package com.pictureorganizer.util.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageTagMetadataTest {
    @Test
    fun mergeImportTags_union_preservesDefaultOrder_thenExifExtras() {
        val merged =
            ImageTagMetadata.mergeImportTags(
                defaultTags = listOf("默认A", "默认B"),
                exifTags = listOf("旅行", "默认A", "家人"),
            )
        assertEquals(listOf("默认A", "默认B", "旅行", "家人"), merged)
    }

    @Test
    fun mergeImportTags_stripsStatusReservedWords() {
        val merged =
            ImageTagMetadata.mergeImportTags(
                defaultTags = listOf("待归档", "待处理", "风景"),
                exifTags = listOf("已归档", "已确认", "工作", "回收站"),
            )
        assertEquals(listOf("风景", "工作"), merged)
    }

    @Test
    fun mergeImportTags_trimsAndDropsBlanks() {
        val merged =
            ImageTagMetadata.mergeImportTags(
                defaultTags = listOf("  家居  ", ""),
                exifTags = listOf("  ", "旅行"),
            )
        assertEquals(listOf("家居", "旅行"), merged)
    }

    @Test
    fun mergeImportTags_bothEmpty_yieldsEmpty() {
        assertTrue(ImageTagMetadata.mergeImportTags(emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun mergeImportTags_onlyExif_works() {
        assertEquals(
            listOf("A", "B"),
            ImageTagMetadata.mergeImportTags(emptyList(), listOf("A", "B")),
        )
    }
}
