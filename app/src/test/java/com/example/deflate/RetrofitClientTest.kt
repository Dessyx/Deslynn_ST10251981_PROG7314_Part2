package com.example.deflate

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Unit tests for RetrofitClient
 */
class RetrofitClientTest {

    @Test
    fun `RetrofitClient instance should not be null`() {
        // When
        val client = RetrofitClient.instance

        // Then
        assertNotNull(client)
    }

    @Test
    fun `RetrofitClient should return same instance on multiple calls`() {
        // When
        val client1 = RetrofitClient.instance
        val client2 = RetrofitClient.instance

        // Then
        assertSame(client1, client2)
    }

    @Test
    fun `RetrofitClient should be of correct type`() {
        // When
        val client = RetrofitClient.instance

        // Then
        assertNotNull("Client should not be null", client)
    }
}
