package com.example.generalattendance

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ClockingUnitTest {
    private val defaultClocking = Clocking(
        employeeNum = "123456",
        phoneNum = "1234567890",
        workNumList = listOf("101", "111", "112", "113", "117", "202", "203", "204", "205", "411", "502", "511")
    )
    @Test
    fun onClock_uri_isCorrect() {
        val expectedUri = Uri.parse("tel:" + Uri.encode("1234567890,,,,1,,123456,,,1"))
        val result = defaultClocking.getFullOnClockUriCode()
        assertEquals(expectedUri, result)
    }

    @Test
    fun offClock_uri_isCorrect() {
        val expectedUri = Uri.parse("tel:" + Uri.encode("1234567890,,,,2,,123456,,,1,,101#,,111#,,112#,,113#,,117#,,202#,,203#,,204#,,205#,,411#,,502#,,511#,,000#"))
        assertEquals(expectedUri, defaultClocking.getFullOffClockUriCode())
    }
}