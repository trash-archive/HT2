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

// Extension property to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ht2_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        private val CUSTOM_QUESTIONS_KEY = stringPreferencesKey("custom_questions")
        private val LIKED_QUESTIONS_KEY = stringPreferencesKey("liked_questions")
        private val NEXT_CUSTOM_ID_KEY = intPreferencesKey("next_custom_id")

        // Delimiters for manual parsing
        private const val QUESTION_DELIMITER = "|||"
        private const val FIELD_DELIMITER = ":::"
    }

    // Save custom questions using simple string format
    suspend fun saveCustomQuestions(questions: List<Question>) {
        // Format: "id:::text|||id:::text|||..."
        val dataString = questions.joinToString(QUESTION_DELIMITER) { question ->
            "${question.id}$FIELD_DELIMITER${question.text}"
        }

        context.dataStore.edit { preferences ->
            preferences[CUSTOM_QUESTIONS_KEY] = dataString
        }
    }

    // Load custom questions
    fun getCustomQuestions(): Flow<List<CustomQuestionData>> = context.dataStore.data.map { preferences ->
        val dataString = preferences[CUSTOM_QUESTIONS_KEY] ?: ""
        if (dataString.isEmpty()) {
            emptyList()
        } else {
            try {
                dataString.split(QUESTION_DELIMITER).mapNotNull { questionString ->
                    val parts = questionString.split(FIELD_DELIMITER)
                    if (parts.size == 2) {
                        CustomQuestionData(
                            id = parts[0].toInt(),
                            text = parts[1]
                        )
                    } else null
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Save liked question IDs
    suspend fun saveLikedQuestions(likedIds: Set<Int>) {
        // Format: "1,2,3,4,5"
        val dataString = likedIds.joinToString(",")

        context.dataStore.edit { preferences ->
            preferences[LIKED_QUESTIONS_KEY] = dataString
        }
    }

    // Load liked question IDs
    fun getLikedQuestions(): Flow<Set<Int>> = context.dataStore.data.map { preferences ->
        val dataString = preferences[LIKED_QUESTIONS_KEY] ?: ""
        if (dataString.isEmpty()) {
            emptySet()
        } else {
            try {
                dataString.split(",").map { it.toInt() }.toSet()
            } catch (e: Exception) {
                emptySet()
            }
        }
    }

    // Save next custom ID
    suspend fun saveNextCustomId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[NEXT_CUSTOM_ID_KEY] = id
        }
    }

    // Load next custom ID
    fun getNextCustomId(): Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[NEXT_CUSTOM_ID_KEY] ?: 1000
    }
}

// Simple data class (no serialization needed)
data class CustomQuestionData(
    val id: Int,
    val text: String
)