package ru.gb.veber.newsapi

import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test


class ExampleAssertTest {

    private val nullString: String? = null
    private val notNullString = "notNullString"

    @Test(expected = AssertionError::class)
    fun example_NullString_NotNull() {
        assertNotNull(nullString)
    }

    @Test
    fun example_NullString_Null() {
        assertNull(nullString)
    }

    @Test(expected = AssertionError::class)
    fun example_String_Null() {
        assertNull(notNullString)
    }

    @Test
    fun example_String_NotNull() {
        assertNotNull(notNullString)
    }

    @Test
    fun example_ArrayEquals() {
        Assert.assertArrayEquals(arrayOf(1, 2, 3), arrayOf(1, 2, 3))
    }
}