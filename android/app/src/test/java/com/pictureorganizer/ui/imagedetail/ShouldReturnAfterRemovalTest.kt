package com.pictureorganizer.ui.imagedetail

import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShouldReturnAfterRemovalTest {
    private fun item(id: String) =
        ImageListItem(
            id = id,
            description = id,
            status = ImageStatus.Pending,
        )

    @Test
    fun aloneInStatus_returnsTrue() {
        assertTrue(shouldReturnAfterRemoval(listOf(item("a")), "a", visibleSiblingCount = 1))
    }

    @Test
    fun visibleBecameEmpty_returnsTrue_evenIfOtherStatusItemsExist() {
        val siblings = listOf(item("a"), item("b"), item("c"))
        assertTrue(shouldReturnAfterRemoval(siblings, "a", visibleSiblingCount = 1))
    }

    @Test
    fun visibleStillHasOthers_keepsF30Behavior() {
        val siblings = listOf(item("a"), item("b"), item("c"))
        assertFalse(shouldReturnAfterRemoval(siblings, "a", visibleSiblingCount = 2))
    }

    @Test
    fun unknownVisibleCount_fallsBackToFullStatusRule() {
        val siblings = listOf(item("a"), item("b"))
        assertFalse(shouldReturnAfterRemoval(siblings, "a", visibleSiblingCount = 0))
        assertTrue(shouldReturnAfterRemoval(listOf(item("a")), "a", visibleSiblingCount = 0))
    }
}
