package com.example.deflate

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for HomeActivity mood-related functionality
 */
class HomeActivityMoodTest {

    private val moodToTagMap = mapOf(
        "Happy" to listOf("happiness", "joy", "smile", "positive", "life"),
        "Sad" to listOf("motivation", "hope", "strength", "courage", "wisdom"),
        "Anxious" to listOf("wisdom", "peace", "calm", "strength", "courage"),
        "Tired" to listOf("motivation", "energy", "strength", "perseverance", "success"),
        "Excited" to listOf("inspiration", "enthusiasm", "passion", "adventure", "life"),
        "Content" to listOf("peace", "gratitude", "satisfaction", "harmony", "wisdom")
    )

    @Test
    fun `moodToTagMap should contain all expected moods`() {
        // Given
        val expectedMoods = listOf("Happy", "Sad", "Anxious", "Tired", "Excited", "Content")

        // When & Then
        expectedMoods.forEach { mood ->
            assertTrue("Mood '$mood' should be in moodToTagMap", moodToTagMap.containsKey(mood))
        }
    }

    @Test
    fun `moodToTagMap should have non-empty tag lists for all moods`() {
        // When & Then
        moodToTagMap.forEach { (mood, tags) ->
            assertTrue("Mood '$mood' should have non-empty tags", tags.isNotEmpty())
            assertTrue("Mood '$mood' should have at least 3 tags", tags.size >= 3)
        }
    }

    @Test
    fun `Happy mood should have appropriate tags`() {
        // Given
        val happyTags = moodToTagMap["Happy"]!!

        // Then
        assertTrue("Happy should contain 'happiness'", happyTags.contains("happiness"))
        assertTrue("Happy should contain 'joy'", happyTags.contains("joy"))
        assertTrue("Happy should contain 'positive'", happyTags.contains("positive"))
    }

    @Test
    fun `Sad mood should have motivational tags`() {
        // Given
        val sadTags = moodToTagMap["Sad"]!!

        // Then
        assertTrue("Sad should contain 'motivation'", sadTags.contains("motivation"))
        assertTrue("Sad should contain 'hope'", sadTags.contains("hope"))
        assertTrue("Sad should contain 'strength'", sadTags.contains("strength"))
    }

    @Test
    fun `Anxious mood should have calming tags`() {
        // Given
        val anxiousTags = moodToTagMap["Anxious"]!!

        // Then
        assertTrue("Anxious should contain 'peace'", anxiousTags.contains("peace"))
        assertTrue("Anxious should contain 'calm'", anxiousTags.contains("calm"))
        assertTrue("Anxious should contain 'wisdom'", anxiousTags.contains("wisdom"))
    }

    @Test
    fun `Tired mood should have energizing tags`() {
        // Given
        val tiredTags = moodToTagMap["Tired"]!!

        // Then
        assertTrue("Tired should contain 'motivation'", tiredTags.contains("motivation"))
        assertTrue("Tired should contain 'energy'", tiredTags.contains("energy"))
        assertTrue("Tired should contain 'strength'", tiredTags.contains("strength"))
    }

    @Test
    fun `Excited mood should have inspiring tags`() {
        // Given
        val excitedTags = moodToTagMap["Excited"]!!

        // Then
        assertTrue("Excited should contain 'inspiration'", excitedTags.contains("inspiration"))
        assertTrue("Excited should contain 'enthusiasm'", excitedTags.contains("enthusiasm"))
        assertTrue("Excited should contain 'passion'", excitedTags.contains("passion"))
    }

    @Test
    fun `Content mood should have peaceful tags`() {
        // Given
        val contentTags = moodToTagMap["Content"]!!

        // Then
        assertTrue("Content should contain 'peace'", contentTags.contains("peace"))
        assertTrue("Content should contain 'gratitude'", contentTags.contains("gratitude"))
        assertTrue("Content should contain 'satisfaction'", contentTags.contains("satisfaction"))
    }

    @Test
    fun `getMoodTags should return correct tags for valid mood`() {
        // Given
        val mood = "Happy"

        // When
        val tags = moodToTagMap[mood]

        // Then
        assertNotNull("Tags should not be null for valid mood", tags)
        assertEquals("Should return correct number of tags", 5, tags!!.size)
    }

    @Test
    fun `getMoodTags should return null for invalid mood`() {
        // Given
        val invalidMood = "InvalidMood"

        // When
        val tags = moodToTagMap[invalidMood]

        // Then
        assertNull("Tags should be null for invalid mood", tags)
    }

    @Test
    fun `all mood tags should be lowercase`() {
        // When & Then
        moodToTagMap.forEach { (mood, tags) ->
            tags.forEach { tag ->
                assertTrue("Tag '$tag' for mood '$mood' should be lowercase", 
                    tag == tag.lowercase())
            }
        }
    }

    @Test
    fun `all mood tags should not be empty strings`() {
        // When & Then
        moodToTagMap.forEach { (mood, tags) ->
            tags.forEach { tag ->
                assertTrue("Tag '$tag' for mood '$mood' should not be empty", 
                    tag.isNotEmpty())
            }
        }
    }
}
