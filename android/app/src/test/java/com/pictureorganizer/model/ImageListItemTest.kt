package com.pictureorganizer.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageListItemTest {
    @Test
    fun userTagsOf_stripsStatusReservedWords() {
        val input = listOf("待处理", "旅行", "已确认", "家人", "回收站")
        assertEquals(listOf("旅行", "家人"), ImageListItem.userTagsOf(input))
    }

    @Test
    fun userTagsOf_empty_staysEmpty() {
        assertTrue(ImageListItem.userTagsOf(emptyList()).isEmpty())
    }

    @Test
    fun userTagsOf_onlyStatusTags_yieldsEmpty() {
        assertTrue(
            ImageListItem.userTagsOf(listOf("待处理", "已确认", "回收站")).isEmpty(),
        )
    }

    @Test
    fun statusTags_containsThreeReservedNames() {
        assertEquals(setOf("待处理", "已确认", "回收站"), ImageListItem.STATUS_TAGS)
    }

    @Test
    fun userTagsOf_oldNoModifyWord_isNotReserved() {
        assertEquals(listOf("不修改"), ImageListItem.userTagsOf(listOf("不修改")))
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

    @Test
    fun removeTagName_removesExactMatch_keepsOthers() {
        assertEquals(
            listOf("旅行", "家人"),
            ImageListItem.removeTagName(listOf("旅行", "工作", "家人"), "工作"),
        )
    }

    @Test
    fun removeTagName_missingName_unchanged() {
        val tags = listOf("旅行", "家人")
        assertEquals(tags, ImageListItem.removeTagName(tags, "工作"))
    }

    @Test
    fun removeTagName_trimsTarget_andDropsAllExactHits() {
        assertEquals(
            listOf("A"),
            ImageListItem.removeTagName(listOf("工作", "A", "工作"), "  工作  "),
        )
    }

    @Test
    fun removeTagName_blankTarget_unchanged() {
        val tags = listOf("旅行")
        assertEquals(tags, ImageListItem.removeTagName(tags, "  "))
    }

    @Test
    fun countImagesWithTag_countsExactElementMatch() {
        val images =
            listOf(
                listOf("旅行", "家人"),
                listOf("工作"),
                listOf("旅行"),
                emptyList(),
            )
        assertEquals(2, ImageListItem.countImagesWithTag(images, "旅行"))
        assertEquals(0, ImageListItem.countImagesWithTag(images, "不存在"))
    }
}
