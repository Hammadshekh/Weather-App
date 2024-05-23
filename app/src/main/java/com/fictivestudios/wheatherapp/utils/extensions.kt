package com.fictivestudios.wheatherapp.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

fun checkIfDateIsToday(dateTimeString: String): Boolean {
    // Define the format of the original date string
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Parse the date string to a LocalDateTime object
    val dateTime = LocalDateTime.parse(dateTimeString, formatter)

    // Extract the date part from the LocalDateTime object and format it as yyyy-MM-dd
    val date = dateTime.toLocalDate()

    // Format current date as yyyy-MM-dd
    val currentDate = LocalDate.now()

    // Convert both dates to yyyy-MM-dd format for comparison
    val formattedDate = formatterDate(date)
    val formattedCurrentDate = formatterDate(currentDate)

    // Compare the formatted dates and return true if they match
    return formattedDate == formattedCurrentDate
}
fun extractTime(dateTimeString: String): String {
    // Define the format of the original date string
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Parse the date string to a LocalDateTime object
    val dateTime = LocalDateTime.parse(dateTimeString, formatter)

    // Extract the time part from the LocalDateTime object and format it as HH:mm
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(timeFormatter)
}

fun checkIfDateIsTomorrow(dateTimeString: String): Boolean {
    // Define the format of the original date string
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Parse the date string to a LocalDateTime object
    val dateTime = LocalDateTime.parse(dateTimeString, formatter)

    // Extract the date part from the LocalDateTime object and format it as yyyy-MM-dd
    val date = dateTime.toLocalDate()

    // Get tomorrow's date
    val tomorrowDate = LocalDate.now().plusDays(1)

    // Convert both dates to yyyy-MM-dd format for comparison
    val formattedDate = formatterDate(date)
    val formattedTomorrowDate = formatterDate(tomorrowDate)

    // Compare the formatted dates and return true if they match
    return formattedDate == formattedTomorrowDate
}


private fun formatterDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return date.format(formatter)
}

fun getDayName(dateTimeString: String): String {
    // Define the format of the date string
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Parse the date string to a LocalDateTime object
    val dateTime = LocalDateTime.parse(dateTimeString, formatter)

    // Get the day of the week from LocalDateTime
    val dayOfWeek = dateTime.dayOfWeek

    // Return the name of the day as a string
    return dayOfWeek.toString()
}

fun getFormattedDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun kelvinToCelsius(temperatureKelvin: Int): Int {
    return temperatureKelvin - 273
}