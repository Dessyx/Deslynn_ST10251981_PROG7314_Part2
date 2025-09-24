package com.example.deflate

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun subtraction_isCorrect() {
        assertEquals(2, 4 - 2)
    }

    @Test
    fun multiplication_isCorrect() {
        assertEquals(8, 4 * 2)
    }

    @Test
    fun division_isCorrect() {
        assertEquals(2, 4 / 2)
    }

    @Test
    fun string_concatenation_isCorrect() {
        val result = "Hello" + " " + "World"
        assertEquals("Hello World", result)
    }

    @Test
    fun boolean_logic_isCorrect() {
        assertTrue(true)
        assertFalse(false)
    }
}