package com.example.dbstest.utils

import java.text.SimpleDateFormat
import java.util.*


object GeneralUtil {
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy/MM/dd")
        return format.format(date)
    }
}