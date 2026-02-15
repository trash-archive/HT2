package com.example.ht2.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ht2_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        private val CUSTOM_QUESTIONS_KEY    = stringPreferencesKey("custom_questions")
        private val LIKED_QUESTIONS_KEY     = stringPreferencesKey("liked_questions")
        private val ASKED_QUESTIONS_KEY     = stringPreferencesKey("asked_questions")
        private val DISLIKED_QUESTIONS_KEY  = stringPreferencesKey("disliked_questions")
        private val ENDEARMENT_KEY          = stringPreferencesKey("endearment")
        private val NEXT_CUSTOM_ID_KEY      = intPreferencesKey("next_custom_id")
        // Persists the content URI of the couple photo chosen from the gallery
        private val COUPLE_PHOTO_URI_KEY    = stringPreferencesKey("couple_photo_uri")

        private const val QUESTION_DELIMITER = "|||"
        private const val FIELD_DELIMITER    = ":::"
    }

    // ── Custom questions ──────────────────────────────────────────────────────

    suspend fun saveCustomQuestions(questions: List<Question>) {
        val dataString = questions.joinToString(QUESTION_DELIMITER) { question ->
            "${question.id}$FIELD_DELIMITER${question.text}"
        }
        context.dataStore.edit { preferences ->
            preferences[CUSTOM_QUESTIONS_KEY] = dataString
        }
    }

    fun getCustomQuestions(): Flow<List<CustomQuestionData>> =
        context.dataStore.data.map { preferences ->
            val dataString = preferences[CUSTOM_QUESTIONS_KEY] ?: ""
            if (dataString.isEmpty()) {
                emptyList()
            } else {
                try {
                    dataString.split(QUESTION_DELIMITER).mapNotNull { questionString ->
                        val parts = questionString.split(FIELD_DELIMITER)
                        if (parts.size == 2) CustomQuestionData(id = parts[0].toInt(), text = parts[1])
                        else null
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }

    // ── Liked questions ───────────────────────────────────────────────────────

    suspend fun saveLikedQuestions(likedIds: Set<Int>) {
        context.dataStore.edit { preferences ->
            preferences[LIKED_QUESTIONS_KEY] = likedIds.joinToString(",")
        }
    }

    fun getLikedQuestions(): Flow<Set<Int>> =
        context.dataStore.data.map { preferences ->
            val dataString = preferences[LIKED_QUESTIONS_KEY] ?: ""
            if (dataString.isEmpty()) emptySet()
            else try { dataString.split(",").map { it.toInt() }.toSet() } catch (e: Exception) { emptySet() }
        }

    // ── Asked questions ───────────────────────────────────────────────────────

    suspend fun saveAskedQuestions(askedIds: Set<Int>) {
        context.dataStore.edit { preferences ->
            preferences[ASKED_QUESTIONS_KEY] = askedIds.joinToString(",")
        }
    }

    fun getAskedQuestions(): Flow<Set<Int>> =
        context.dataStore.data.map { preferences ->
            val dataString = preferences[ASKED_QUESTIONS_KEY] ?: ""
            if (dataString.isEmpty()) emptySet()
            else try { dataString.split(",").map { it.toInt() }.toSet() } catch (e: Exception) { emptySet() }
        }

    // ── Disliked questions ────────────────────────────────────────────────────

    suspend fun saveDislikedQuestions(dislikedIds: Set<Int>) {
        context.dataStore.edit { preferences ->
            preferences[DISLIKED_QUESTIONS_KEY] = dislikedIds.joinToString(",")
        }
    }

    fun getDislikedQuestions(): Flow<Set<Int>> =
        context.dataStore.data.map { preferences ->
            val dataString = preferences[DISLIKED_QUESTIONS_KEY] ?: ""
            if (dataString.isEmpty()) emptySet()
            else try { dataString.split(",").map { it.toInt() }.toSet() } catch (e: Exception) { emptySet() }
        }

    // ── Endearment / nickname ─────────────────────────────────────────────────

    suspend fun saveEndearment(endearment: String) {
        context.dataStore.edit { preferences ->
            preferences[ENDEARMENT_KEY] = endearment
        }
    }

    fun getEndearment(): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[ENDEARMENT_KEY] ?: "HT²"
        }

    // ── Next custom question ID ───────────────────────────────────────────────

    suspend fun saveNextCustomId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[NEXT_CUSTOM_ID_KEY] = id
        }
    }

    fun getNextCustomId(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[NEXT_CUSTOM_ID_KEY] ?: 1000
        }

    // ── Couple photo URI ──────────────────────────────────────────────────────
    // Stores the content URI string returned by the system image picker.
    // An empty string means no photo has been chosen yet.

    suspend fun saveCouplePhotoUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[COUPLE_PHOTO_URI_KEY] = uri
        }
    }

    fun getCouplePhotoUri(): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[COUPLE_PHOTO_URI_KEY] ?: ""
        }
}

data class CustomQuestionData(
    val id: Int,
    val text: String
)