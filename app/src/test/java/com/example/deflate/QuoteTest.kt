package com.example.deflate

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Quote data class
 */
class QuoteTest {

    @Test
    fun `Quote should be created with correct properties`() {
        // Given
        val body = "Life is what happens when you're busy making other plans."
        val author = "John Lennon"

        // When
        val quote = Quote(body, author)

        // Then
        assertEquals(body, quote.body)
        assertEquals(author, quote.author)
    }

    @Test
    fun `Quote should handle empty strings`() {
        // Given
        val emptyBody = ""
        val emptyAuthor = ""

        // When
        val quote = Quote(emptyBody, emptyAuthor)

        // Then
        assertEquals(emptyBody, quote.body)
        assertEquals(emptyAuthor, quote.author)
    }

    @Test
    fun `Quote should handle special characters`() {
        // Given
        val bodyWithSpecialChars = "Don't worry, be happy! 🎵"
        val authorWithSpecialChars = "Bobby McFerrin"

        // When
        val quote = Quote(bodyWithSpecialChars, authorWithSpecialChars)

        // Then
        assertEquals(bodyWithSpecialChars, quote.body)
        assertEquals(authorWithSpecialChars, quote.author)
    }
}
