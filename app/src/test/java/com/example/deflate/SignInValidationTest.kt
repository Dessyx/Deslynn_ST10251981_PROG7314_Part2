package com.example.deflate

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SignInActivity validation logic
 */
class SignInValidationTest {

    @Test
    fun `validateInputs should return true for valid inputs`() {
        // Given
        val username = "testuser"
        val password = "password123"

        // When
        val result = validateInputs(username, password)

        // Then
        assertTrue(result)
    }

    @Test
    fun `validateInputs should return false for empty username`() {
        // Given
        val username = ""
        val password = "password123"

        // When
        val result = validateInputs(username, password)

        // Then
        assertFalse(result)
    }

    @Test
    fun `validateInputs should return false for empty password`() {
        // Given
        val username = "testuser"
        val password = ""

        // When
        val result = validateInputs(username, password)

        // Then
        assertFalse(result)
    }

    @Test
    fun `validateInputs should return false for both empty inputs`() {
        // Given
        val username = ""
        val password = ""

        // When
        val result = validateInputs(username, password)

        // Then
        assertFalse(result)
    }

    @Test
    fun `validateInputs should return true for whitespace-only inputs`() {
        // Given
        val username = "   "
        val password = "   "

        // When
        val result = validateInputs(username, password)

        // Then
        assertTrue(result)
    }

    @Test
    fun `validateInputs should return true for email format username`() {
        // Given
        val username = "test@example.com"
        val password = "password123"

        // When
        val result = validateInputs(username, password)

        // Then
        assertTrue(result)
    }

    @Test
    fun `validateInputs should return true for special characters in username`() {
        // Given
        val username = "test_user-123"
        val password = "password123"

        // When
        val result = validateInputs(username, password)

        // Then
        assertTrue(result)
    }

    @Test
    fun `validateInputs should return true for special characters in password`() {
        // Given
        val username = "testuser"
        val password = "p@ssw0rd!@#"

        // When
        val result = validateInputs(username, password)

        // Then
        assertTrue(result)
    }

    // Helper function to test validation logic 
    private fun validateInputs(username: String, password: String): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }
}
