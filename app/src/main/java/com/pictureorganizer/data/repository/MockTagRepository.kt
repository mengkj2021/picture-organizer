package com.pictureorganizer.data.repository

import com.pictureorganizer.model.Tag
import com.pictureorganizer.model.TagTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Preview / 测试用空标签库 */
object MockTagRepository : TagRepository {
    private val tags = MutableStateFlow<List<Tag>>(emptyList())
    private val templates = MutableStateFlow<List<TagTemplate>>(emptyList())

    override fun observeTags(): Flow<List<Tag>> = tags.asStateFlow()

    override fun observeTemplates(): Flow<List<TagTemplate>> = templates.asStateFlow()

    override suspend fun getTags(): List<Tag> = tags.value

    override suspend fun getTemplates(): List<TagTemplate> = templates.value

    override suspend fun getTag(id: String): Tag? = tags.value.find { it.id == id }

    override suspend fun getTemplate(id: String): TagTemplate? = templates.value.find { it.id == id }

    override suspend fun insertTag(tag: Tag) = Unit

    override suspend fun updateTag(tag: Tag) = Unit

    override suspend fun deleteTag(id: String) = Unit

    override suspend fun insertTemplate(template: TagTemplate) = Unit

    override suspend fun updateTemplate(template: TagTemplate) = Unit

    override suspend fun deleteTemplate(id: String) = Unit

    override suspend fun setDefaultTemplate(id: String) = Unit
}
