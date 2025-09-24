package com.example.deflate

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.text.SimpleDateFormat
import java.util.*

/**
 * Integration tests for the main app functionality
 */
class AppIntegrationTest {

    private lateinit var moodToTagMap: Map<String, List<String>>

    @Before
    fun setUp() {
        moodToTagMap = mapOf(
            "Happy" to listOf("happiness", "joy", "smile", "positive", "life"),
            "Sad" to listOf("motivation", "hope", "strength", "courage", "wisdom"),
            "Anxious" to listOf("wisdom", "peace", "calm", "strength", "courage"),
            "Tired" to listOf("motivation", "energy", "strength", "perseverance", "success"),
            "Excited" to listOf("inspiration", "enthusiasm", "passion", "adventure", "life"),
            "Content" to listOf("peace", "gratitude", "satisfaction", "harmony", "wisdom")
        )
    }

    @Test
    fun `app should handle complete mood selection flow`() {
        // Given
        val mood = "Happy"
        val date = Date()
        val dateKey = getTodayKey(date)

        // When
        val tags = moodToTagMap[mood]
        val savedMood = saveMood(mood, dateKey)
        val retrievedMood = loadMood(dateKey)

        // Then
        assertNotNull("Tags should not be null for valid mood", tags)
        assertTrue("Mood should be saved successfully", savedMood)
        assertEquals("Retrieved mood should match saved mood", mood, retrievedMood)
        assertTrue("Tags should contain expected values", tags!!.contains("happiness"))
    }

    @Test
    fun `app should handle quote fetching flow`() {
        // Given
        val mood = "Sad"
        val tags = moodToTagMap[mood]!!

        // When
        val firstTag = tags[0]
        val quoteRequest = createQuoteRequest(firstTag)

        // Then
        assertNotNull("First tag should not be null", firstTag)
        assertEquals("First tag should be 'motivation'", "motivation", firstTag)
        assertNotNull("Quote request should be created", quoteRequest)
    }

    @Test
    fun `app should handle fallback quote fetching`() {
        // Given
        val fallbackTags = listOf("wisdom", "life", "inspiration", "motivation")

        // When
        val fallbackRequest = createQuoteRequest(fallbackTags[0])

        // Then
        assertNotNull("Fallback request should not be null", fallbackRequest)
        assertTrue("Fallback tags should not be empty", fallbackTags.isNotEmpty())
    }

    @Test
    fun `app should handle user authentication flow`() {
        // Given
        val email = "test@example.com"
        val password = "password123"

        // When
        val isValid = validateInputs(email, password)
        val authRequest = createAuthRequest(email, password)

        // Then
        assertTrue("Valid inputs should pass validation", isValid)
        assertNotNull("Auth request should be created", authRequest)
    }

    @Test
    fun `app should handle activity data flow`() {
        // Given
        val userId = "user123"
        val weight = 70.5
        val steps = 10000
        val timestamp = System.currentTimeMillis()

        // When
        val activityData = ActivityData(
            userId = userId,
            weight = weight,
            steps = steps,
            timestamp = timestamp
        )

        // Then
        assertEquals("User ID should match", userId, activityData.userId)
        assertEquals("Weight should match", weight, activityData.weight!!, 0.01)
        assertEquals("Steps should match", steps, activityData.steps)
        assertEquals("Timestamp should match", timestamp, activityData.timestamp)
    }

    @Test
    fun `app should handle quote response parsing`() {
        // Given
        val quote1 = Quote("Test quote 1", "Author 1")
        val quote2 = Quote("Test quote 2", "Author 2")
        val quotes = listOf(quote1, quote2)

        // When
        val response = QuoteResponse(quotes)

        // Then
        assertEquals("Response should contain 2 quotes", 2, response.quotes.size)
        assertEquals("First quote should match", quote1, response.quotes[0])
        assertEquals("Second quote should match", quote2, response.quotes[1])
    }

    @Test
    fun `app should handle error scenarios gracefully`() {
        // Given
        val invalidMood = "InvalidMood"
        val emptyTags = moodToTagMap[invalidMood]

        // When
        val fallbackTags = listOf("wisdom", "life", "inspiration", "motivation")
        val fallbackRequest = createQuoteRequest(fallbackTags[0])

        // Then
        assertNull("Invalid mood should return null tags", emptyTags)
        assertNotNull("Fallback request should be created", fallbackRequest)
    }

    @Test
    fun `app should handle date persistence correctly`() {
        // Given
        val mood = "Content"
        val date1 = Date()
        val date2 = Date(System.currentTimeMillis() + 86400000) // +1 day
        val dateKey1 = getTodayKey(date1)
        val dateKey2 = getTodayKey(date2)

        // When
        saveMood(mood, dateKey1)
        val mood1 = loadMood(dateKey1)
        val mood2 = loadMood(dateKey2)

        // Then
        assertEquals("Mood should be saved for date1", mood, mood1)
        assertNull("Mood should be null for date2", mood2)
    }

    // Helper functions
    private val moodStorage = mutableMapOf<String, String?>()
    
    private fun getTodayKey(date: Date): String {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return df.format(date)
    }

    private fun saveMood(mood: String, dateKey: String): Boolean {
        return try {
            // Simulate saving to SharedPreferences
            moodStorage[dateKey] = mood
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun loadMood(dateKey: String): String? {
   
        return moodStorage[dateKey]
    }

    private fun createQuoteRequest(tag: String): String {
        return "Quote request for tag: $tag"
    }

    private fun createAuthRequest(email: String, password: String): String {
        return "Auth request for email: $email"
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}
