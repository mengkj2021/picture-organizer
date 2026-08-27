package com.pictureorganizer.data.repository

import com.pictureorganizer.data.mock.MockImageListData
import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus

object MockImageRepository : ImageRepository {

    private val pendingItems =
        MockImageListData.pendingItems().toMutableList()
    private val confirmedItems =
        MockImageListData.confirmedItems().toMutableList()
    private val noModifyItems =
        MockImageListData.noModifyItems().toMutableList()

    override fun getItems(status: ImageStatus): List<ImageListItem> = when (status) {
        ImageStatus.Pending -> pendingItems.toList()
        ImageStatus.Confirmed -> confirmedItems.toList()
        ImageStatus.NoModify -> noModifyItems.toList()
    }

    override fun moveItems(ids: Set<String>, from: ImageStatus, to: ImageStatus) {
        if (ids.isEmpty() || from == to) return
        val source = listFor(status = from)
        val target = listFor(status = to)
        val moved = source.filter { it.id in ids }.map { it.withStatus(to) }
        source.removeAll { it.id in ids }
        target.addAll(moved)
    }

    private fun listFor(status: ImageStatus): MutableList<ImageListItem> = when (status) {
        ImageStatus.Pending -> pendingItems
        ImageStatus.Confirmed -> confirmedItems
        ImageStatus.NoModify -> noModifyItems
    }
}
