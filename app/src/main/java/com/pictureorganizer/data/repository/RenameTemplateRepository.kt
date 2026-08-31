package com.pictureorganizer.data.repository

import com.pictureorganizer.model.RenameTemplate
import kotlinx.coroutines.flow.Flow

interface RenameTemplateRepository {
    fun observeAll(): Flow<List<RenameTemplate>>

    suspend fun getAll(): List<RenameTemplate>

    suspend fun getById(id: String): RenameTemplate?

    suspend fun getDefault(): RenameTemplate?

    /** @throws IllegalArgumentException 空名或空 pattern */
    suspend fun insert(template: RenameTemplate)

    /** @throws IllegalArgumentException 空名或空 pattern */
    suspend fun update(template: RenameTemplate)

    suspend fun delete(id: String)

    suspend fun setDefault(id: String)
}
