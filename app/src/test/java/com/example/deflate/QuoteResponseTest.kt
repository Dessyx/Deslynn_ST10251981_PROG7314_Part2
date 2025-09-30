package com.example.deflate

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for QuoteResponse data class
 */
class QuoteResponseTest {

    @Test
    fun `QuoteResponse should be created with empty quotes list`() {
        // Given
        val emptyQuotes = emptyList<Quote>()

        // When
        val response = QuoteResponse(emptyQuotes)

        // Then
        assertTrue(response.quotes.isEmpty())
        assertEquals(0, response.quotes.size)
    }

    @Test
    fun `QuoteResponse should be created with single quote`() {
        // Given
        val quote = Quote("Test quote", "Test Author")
        val quotes = listOf(quote)

        // When
        val response = QuoteResponse(quotes)

        // Then
        assertEquals(1, response.quotes.size)
        assertEquals(quote, response.quotes[0])
        assertEquals("Test quote", response.quotes[0].body)
        assertEquals("Test Author", response.quotes[0].author)
    }

    @Test
    fun `QuoteResponse should be created with multiple quotes`() {
        // Given
        val quote1 = Quote("First quote", "First Author")
        val quote2 = Quote("Second quote", "Second Author")
        val quote3 = Quote("Third quote", "Third Author")
        val quotes = listOf(quote1, quote2, quote3)

        // When
        val response = QuoteResponse(quotes)

        // Then
        assertEquals(3, response.quotes.size)
        assertEquals(quote1, response.quotes[0])
        assertEquals(quote2, response.quotes[1])
        assertEquals(quote3, response.quotes[2])
    }
}
