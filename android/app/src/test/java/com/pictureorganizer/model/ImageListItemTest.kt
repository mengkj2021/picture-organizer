package com.pictureorganizer.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageListItemTest {
    @Test
    fun userTagsOf_stripsStatusReservedWords() {
        val input = listOf("待处理", "旅行", "已确认", "家人", "不修改")
        assertEquals(listOf("旅行", "家人"), ImageListItem.userTagsOf(input))
    }

    @Test
    fun userTagsOf_empty_staysEmpty() {
        assertTrue(ImageListItem.userTagsOf(emptyList()).isEmpty())
    }

    @Test
    fun userTagsOf_onlyStatusTags_yieldsEmpty() {
        assertTrue(
            ImageListItem.userTagsOf(listOf("待处理", "已确认", "不修改")).isEmpty(),
        )
    }

    @Test
    fun statusTags_containsThreeReservedNames() {
        assertEquals(setOf("待处理", "已确认", "不修改"), ImageListItem.STATUS_TAGS)
    }

    @Test
    fun withStatus_updatesStatus_andStripsStatusWordsFromTags() {
        val item =
            ImageListItem(
                id = "1",
                description = "a.jpg",
                tags = listOf("待处理", "风景"),
                status = ImageStatus.Pending,
            )
        val moved = item.withStatus(ImageStatus.Confirmed)
        assertEquals(ImageStatus.Confirmed, moved.status)
        assertEquals(listOf("风景"), moved.tags)
        assertFalse(moved.tags.any { it in ImageListItem.STATUS_TAGS })
    }
}
