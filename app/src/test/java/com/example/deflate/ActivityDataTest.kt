package com.example.deflate

import org.junit.Test
import org.junit.Assert.*
import java.util.Date

/**
 * Unit tests for ActivityData data class
 */
class ActivityDataTest {

    @Test
    fun `ActivityData should be created with default values`() {
        // When
        val activityData = ActivityData()

        // Then
        assertEquals("", activityData.userId)
        assertNull(activityData.weight)
        assertNull(activityData.steps)
        assertNull(activityData.date)
        assertEquals(0L, activityData.timestamp)
    }

    @Test
    fun `ActivityData should be created with provided values`() {
        // Given
        val userId = "user123"
        val weight = 70.5
        val steps = 10000
        val date = Date()
        val timestamp = System.currentTimeMillis()

        // When
        val activityData = ActivityData(
            userId = userId,
            weight = weight,
            steps = steps,
            date = date,
            timestamp = timestamp
        )

        // Then
        assertEquals(userId, activityData.userId)
        assertEquals(weight, activityData.weight!!, 0.01)
        assertEquals(steps, activityData.steps)
        assertEquals(date, activityData.date)
        assertEquals(timestamp, activityData.timestamp)
    }

    @Test
    fun `ActivityData should handle null values correctly`() {
        // Given
        val userId = "user456"
        val timestamp = 1234567890L

        // When
        val activityData = ActivityData(
            userId = userId,
            weight = null,
            steps = null,
            date = null,
            timestamp = timestamp
        )

        // Then
        assertEquals(userId, activityData.userId)
        assertNull(activityData.weight)
        assertNull(activityData.steps)
        assertNull(activityData.date)
        assertEquals(timestamp, activityData.timestamp)
    }

    @Test
    fun `ActivityData should handle zero values`() {
        // Given
        val userId = "user789"
        val weight = 0.0
        val steps = 0
        val timestamp = 0L

        // When
        val activityData = ActivityData(
            userId = userId,
            weight = weight,
            steps = steps,
            timestamp = timestamp
        )

        // Then
        assertEquals(userId, activityData.userId)
        assertEquals(weight, activityData.weight!!, 0.01)
        assertEquals(steps, activityData.steps)
        assertEquals(timestamp, activityData.timestamp)
    }
}
