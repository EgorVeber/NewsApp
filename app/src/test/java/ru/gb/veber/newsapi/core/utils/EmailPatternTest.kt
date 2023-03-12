package ru.gb.veber.newsapi.core.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.gb.veber.newsapi.common.extentions.EMAIL_PATTERN

class EmailPatternTest {

    @Test
    fun emailPattern_EmptyString_ReturnsFalse() {
        assertFalse(EMAIL_PATTERN.matcher("").matches())
    }

    @Test
    fun emailPattern_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(EMAIL_PATTERN.matcher("name@email.com").matches())
    }

    @Test
    fun emailPattern_CorrectEmailSubDomain_ReturnsTrue() {
        assertFalse(EMAIL_PATTERN.matcher("name@email.co.uk").matches())
    }

    @Test
    fun emailPattern_InvalidEmailNoTld_ReturnsFalse() {
        assertFalse(EMAIL_PATTERN.matcher("name@email").matches())
    }

    @Test
    fun emailPattern_InvalidEmailDoubleDot_ReturnsFalse() {
        assertFalse(EMAIL_PATTERN.matcher("name@email..com@email").matches())
    }

    @Test
    fun emailPattern_InvalidEmailNoUsername_ReturnsFalse() {
        assertFalse(EMAIL_PATTERN.matcher("@email.com").matches())
    }

    @Test
    fun emailPattern_InvalidEmailNoDomain_ReturnsFalse() {
        assertFalse(EMAIL_PATTERN.matcher("Jamesyandex.ru").matches())
    }
}
