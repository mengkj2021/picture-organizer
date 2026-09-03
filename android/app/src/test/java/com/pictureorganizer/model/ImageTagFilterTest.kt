package com.pictureorganizer.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageTagFilterTest {
    private fun item(
        description: String = "desc.jpg",
        tags: List<String> = emptyList(),
        filePath: String = "pending/desc.jpg",
        importedAt: Long = 0L,
    ) = ImageListItem(
        id = description,
        description = description,
        tags = tags,
        status = ImageStatus.Pending,
        filePath = filePath,
        importedAt = importedAt,
    )

    @Test
    fun matchesFilter_noCriteria_alwaysTrue() {
        assertTrue(item().matchesFilter(TagFilterCriteria()))
    }

    @Test
    fun matchesFilter_tagOr_hitsAnySelected() {
        val photo = item(tags = listOf("旅行"))
        assertTrue(
            photo.matchesFilter(
                TagFilterCriteria(selectedTagNames = setOf("旅行", "家人")),
            ),
        )
        assertFalse(
            photo.matchesFilter(
                TagFilterCriteria(selectedTagNames = setOf("家人")),
            ),
        )
    }

    @Test
    fun matchesFilter_includeUntagged_ignoresStatusWordsAsUserTags() {
        val onlyStatus = item(tags = listOf("待处理"))
        assertTrue(
            onlyStatus.matchesFilter(
                TagFilterCriteria(includeUntagged = true),
            ),
        )
        val withUser = item(tags = listOf("风景"))
        assertFalse(
            withUser.matchesFilter(
                TagFilterCriteria(includeUntagged = true),
            ),
        )
    }

    @Test
    fun matchesFilter_nameContains_caseInsensitive_onDescriptionOrFileName() {
        val photo = item(description = "Holiday.JPG", filePath = "pending/Holiday.JPG")
        assertTrue(
            photo.matchesFilter(TagFilterCriteria(nameContains = "holiday")),
        )
        assertFalse(
            photo.matchesFilter(TagFilterCriteria(nameContains = "winter")),
        )
        val byPath =
            item(description = "", filePath = "pending/WinterTrip.png")
        assertTrue(
            byPath.matchesFilter(TagFilterCriteria(nameContains = "trip")),
        )
    }

    @Test
    fun matchesFilter_blankNameContains_doesNotRestrict() {
        assertTrue(
            item(description = "x.jpg").matchesFilter(
                TagFilterCriteria(nameContains = "   "),
            ),
        )
    }

    @Test
    fun sortedByFilter_importedAtDescAsc_andNameAsc() {
        val a = item(description = "b.jpg", importedAt = 10)
        val b = item(description = "A.jpg", importedAt = 30)
        val c = item(description = "c.jpg", importedAt = 20)
        val list = listOf(a, b, c)

        assertEquals(
            listOf(b, c, a),
            list.sortedByFilter(ImageListSort.ImportedAtDesc),
        )
        assertEquals(
            listOf(a, c, b),
            list.sortedByFilter(ImageListSort.ImportedAtAsc),
        )
        assertEquals(
            listOf(b, a, c).map { it.description },
            list.sortedByFilter(ImageListSort.NameAsc).map { it.description },
        )
    }
}
