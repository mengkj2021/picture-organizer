package com.pictureorganizer.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences",
)

class UserPreferencesRepository(
    private val context: Context,
) {
    private val dataStore = context.userPreferencesDataStore

    val tutorialCompleted: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[KEY_TUTORIAL_COMPLETED] ?: false
        }

    val defaultTagNames: Flow<Set<String>> =
        dataStore.data.map { prefs ->
            parseTagNames(prefs[KEY_DEFAULT_TAGS])
        }

    val importCompressEnabled: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[KEY_IMPORT_COMPRESS] ?: true
        }

    /** F11：导入重名是否询问（默认开） */
    val importDuplicateAskEnabled: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[KEY_IMPORT_DUPLICATE_ASK] ?: true
        }

    suspend fun isTutorialCompleted(): Boolean = tutorialCompleted.first()

    suspend fun setTutorialCompleted(completed: Boolean = true) {
        dataStore.edit { prefs ->
            prefs[KEY_TUTORIAL_COMPLETED] = completed
        }
    }

    suspend fun getDefaultTagNames(): Set<String> = defaultTagNames.first()

    suspend fun setDefaultTagNames(names: Set<String>) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_TAGS] =
                names
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .joinToString(SEPARATOR)
        }
    }

    suspend fun isImportCompressEnabled(): Boolean = importCompressEnabled.first()

    suspend fun setImportCompressEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IMPORT_COMPRESS] = enabled
        }
    }

    suspend fun isImportDuplicateAskEnabled(): Boolean = importDuplicateAskEnabled.first()

    suspend fun setImportDuplicateAskEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IMPORT_DUPLICATE_ASK] = enabled
        }
    }

    companion object {
        private const val SEPARATOR = "\u001f"
        private val KEY_TUTORIAL_COMPLETED = booleanPreferencesKey("tutorial_completed")
        private val KEY_DEFAULT_TAGS = stringPreferencesKey("default_tag_names")
        private val KEY_IMPORT_COMPRESS = booleanPreferencesKey("import_compress_enabled")
        private val KEY_IMPORT_DUPLICATE_ASK = booleanPreferencesKey("import_duplicate_ask_enabled")

        private fun parseTagNames(raw: String?): Set<String> {
            if (raw.isNullOrBlank()) return emptySet()
            return raw
                .split(SEPARATOR)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
        }
    }
}
