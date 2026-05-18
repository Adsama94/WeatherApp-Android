package com.adsama.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun setFormattedDate(inputDate: String): String? {
    val formattedDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        date?.let {
            outputFormat.format(date)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "NA"
    }
    return formattedDate
}

fun setDayFromDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val dayOfWeek = try {
        val currentDate = inputFormat.parse(inputDate)
        if (currentDate != null) {
            calendar.time = currentDate
        }
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Unknown"
        }
    } catch (e: Exception) {
        "Unknown error: ${e.message}"
    }
    return dayOfWeek
}