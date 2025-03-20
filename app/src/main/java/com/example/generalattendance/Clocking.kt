package com.example.generalattendance

import android.net.Uri
import android.util.Log


class Clocking(private val employeeNum: String,
               private val phoneNum: String,
               private val workNumList: List<String>) {

    fun getFullOnClockUriCode(): Uri {
        return Uri.parse("tel:$phoneNum" + pause(8) + "1" + pause(4) + employeeNum + pause(6) + "1")
    }

    fun getFullOffClockUriCode(): Uri {
        return Uri.parse(
            ("tel:$phoneNum" + pause(8) + "2" + pause(4) + employeeNum + pause(6) + "1"
                    + pause(4) + workNum())
        )
    }

    fun calculateTotalWaitTime(onClock: Boolean = true): Int {
        val initialWaitTime = 38
        if (onClock)
            return initialWaitTime
        return initialWaitTime + (workNumList.size + 1) * 7   // add 1 for 000 number set
    }

    private fun pound(): String {
        return Uri.encode("#")
    }

    private fun workNum(): String {
        Log.i("workNum Debug2", (workNumList + "000").joinToString(separator = pound() + pause(4)) + pound())
        return (workNumList + "000").joinToString(separator = pound() + pause(4)) + pound()
    }

    private fun pause(second: Int): String {
        val result = StringBuilder()
        for (i in 0 until second / 2) {
            result.append(",")
        }
        return Uri.encode(result.toString())
    }
}