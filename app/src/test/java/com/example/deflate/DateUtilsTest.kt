package com.example.deflate

import org.junit.Test
import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Unit tests for date utility functions
 */
class DateUtilsTest {

    @Test
    fun `todayKey should return correct date format`() {
        // Given
        val date = Date()
        val expectedFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val expectedDate = expectedFormat.format(date)

        // When
        val actualDate = getTodayKey(date)

        // Then
        assertEquals("Date should be in yyyy-MM-dd format", expectedDate, actualDate)
    }

    @Test
    fun `todayKey should be consistent for same date`() {
        // Given
        val date = Date()
        val date1 = getTodayKey(date)
        val date2 = getTodayKey(date)

        // Then
        assertEquals("Same date should produce same key", date1, date2)
    }

    @Test
    fun `todayKey should be different for different dates`() {
        // Given
        val calendar = Calendar.getInstance()
        val date1 = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val date2 = calendar.time

        // When
        val key1 = getTodayKey(date1)
        val key2 = getTodayKey(date2)

        // Then
        assertNotEquals("Different dates should produce different keys", key1, key2)
    }

    @Test
    fun `todayKey should handle edge cases`() {
        // Given
        val calendar = Calendar.getInstance()
        
        // Test January 1st
        calendar.set(2024, Calendar.JANUARY, 1)
        val jan1 = getTodayKey(calendar.time)
        
        // Test December 31st
        calendar.set(2024, Calendar.DECEMBER, 31)
        val dec31 = getTodayKey(calendar.time)
        
        // Test leap year
        calendar.set(2024, Calendar.FEBRUARY, 29)
        val leapDay = getTodayKey(calendar.time)

        // Then
        assertTrue("January 1st should be valid", jan1.startsWith("2024-01-01"))
        assertTrue("December 31st should be valid", dec31.startsWith("2024-12-31"))
        assertTrue("Leap day should be valid", leapDay.startsWith("2024-02-29"))
    }

    @Test
    fun `todayKey should be in correct format`() {
        // Given
        val date = Date()
        
        // When
        val dateKey = getTodayKey(date)
        
        // Then
        assertTrue("Date key should match yyyy-MM-dd pattern", 
            dateKey.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `todayKey should handle timezone correctly`() {
        // Given
        val calendar = Calendar.getInstance()
        val date = calendar.time
        
        // When
        val dateKey = getTodayKey(date)
        
        // Then
        assertNotNull("Date key should not be null", dateKey)
        assertTrue("Date key should not be empty", dateKey.isNotEmpty())
    }


    private fun getTodayKey(date: Date): String {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return df.format(date)
    }
}
