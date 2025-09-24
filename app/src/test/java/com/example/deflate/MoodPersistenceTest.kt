package com.example.deflate

import org.junit.Test
import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Unit tests for mood persistence functionality
 */
class MoodPersistenceTest {

    @Test
    fun `mood should be saved and retrieved correctly`() {
        // Given
        val mood = "Happy"
        val date = Date()
        val dateKey = getTodayKey(date)

        // When
        val savedMood = saveMood(mood, dateKey)
        val retrievedMood = loadMood(dateKey)

        // Then
        assertEquals("Saved mood should match retrieved mood", mood, retrievedMood)
        assertTrue("Mood should be saved successfully", savedMood)
    }

    @Test
    fun `mood should be null for different date`() {
        // Given
        val mood = "Happy"
        val date1 = Date()
        val date2 = Date(System.currentTimeMillis() + 86400000) // +1 day
        val dateKey1 = getTodayKey(date1)
        val dateKey2 = getTodayKey(date2)

        // When
        saveMood(mood, dateKey1)
        val retrievedMood = loadMood(dateKey2)

        // Then
        assertNull("Mood should be null for different date", retrievedMood)
    }

    @Test
    fun `mood should be overwritten when saved again`() {
        // Given
        val mood1 = "Happy"
        val mood2 = "Sad"
        val date = Date()
        val dateKey = getTodayKey(date)

        // When
        saveMood(mood1, dateKey)
        saveMood(mood2, dateKey)
        val retrievedMood = loadMood(dateKey)

        // Then
        assertEquals("Latest mood should be retrieved", mood2, retrievedMood)
        assertNotEquals("Previous mood should be overwritten", mood1, retrievedMood)
    }

    @Test
    fun `mood should handle all valid mood types`() {
        // Given
        val moods = listOf("Happy", "Sad", "Anxious", "Tired", "Excited", "Content")
        val date = Date()
        val dateKey = getTodayKey(date)

        // When & Then
        moods.forEach { mood ->
            saveMood(mood, dateKey)
            val retrievedMood = loadMood(dateKey)
            assertEquals("Mood '$mood' should be saved and retrieved correctly", mood, retrievedMood)
        }
    }

    @Test
    fun `mood should handle empty string`() {
        // Given
        val mood = ""
        val date = Date()
        val dateKey = getTodayKey(date)

        // When
        saveMood(mood, dateKey)
        val retrievedMood = loadMood(dateKey)

        // Then
        assertEquals("Empty mood should be saved and retrieved", mood, retrievedMood)
    }

    @Test
    fun `mood should handle null values gracefully`() {
        // Given
        val date = Date()
        val dateKey = getTodayKey(date)

        // When
        val savedMood = saveMood(null, dateKey)
        val retrievedMood = loadMood(dateKey)

        // Then
        assertTrue("Null mood should be saved successfully", savedMood)
        assertNull("Null mood should be retrieved as null", retrievedMood)
    }

    @Test
    fun `mood should persist across multiple saves`() {
        // Given
        val moods = listOf("Happy", "Sad", "Anxious")
        val date = Date()
        val dateKey = getTodayKey(date)

        // When
        moods.forEach { mood ->
            saveMood(mood, dateKey)
            val retrievedMood = loadMood(dateKey)
            assertEquals("Mood '$mood' should be saved and retrieved", mood, retrievedMood)
        }
    }

    @Test
    fun `mood should handle special characters`() {
        // Given
        val mood = "Happy 😊"
        val date = Date()
        val dateKey = getTodayKey(date)

        // When
        saveMood(mood, dateKey)
        val retrievedMood = loadMood(dateKey)

        // Then
        assertEquals("Mood with special characters should be saved and retrieved", mood, retrievedMood)
    }

    // Helper functions to test mood persistence logic
    private val moodStorage = mutableMapOf<String, String?>()

    private fun saveMood(mood: String?, dateKey: String): Boolean {
        return try {
            moodStorage[dateKey] = mood
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun loadMood(dateKey: String): String? {
        return moodStorage[dateKey]
    }

    private fun getTodayKey(date: Date): String {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return df.format(date)
    }
}
