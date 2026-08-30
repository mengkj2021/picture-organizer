package com.pictureorganizer.data.repository

import com.pictureorganizer.model.Tag
import com.pictureorganizer.model.TagTemplate
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun observeTags(): Flow<List<Tag>>
    fun observeTemplates(): Flow<List<TagTemplate>>
    suspend fun getTags(): List<Tag>
    suspend fun getTemplates(): List<TagTemplate>
    suspend fun getTag(id: String): Tag?
    suspend fun getTemplate(id: String): TagTemplate?

    /** @throws IllegalArgumentException 空名或重名 */
    suspend fun insertTag(tag: Tag)

    /** @throws IllegalArgumentException 空名或与其它行重名 */
    suspend fun updateTag(tag: Tag)

    /** 不修改 images.tagsJson；模板内同名字符串保留 */
    suspend fun deleteTag(id: String)

    suspend fun insertTemplate(template: TagTemplate)
    suspend fun updateTemplate(template: TagTemplate)
    suspend fun deleteTemplate(id: String)

    /** 将指定模板设为默认，并清除其它默认 */
    suspend fun setDefaultTemplate(id: String)
}
