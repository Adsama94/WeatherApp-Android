package com.adsama.weatherapp.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@BindingAdapter("iconUrl")
fun setIconUrl(imageView: ImageView, iconUrl: String?) {
    if (iconUrl != null) {
        val updatedIconUrl = "https:$iconUrl"
        Glide.with(imageView.context).load(updatedIconUrl).into(imageView)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("tempInCelsius")
fun setTempInCelsius(textView: TextView, temp: Double) {
    val tempInInt = temp.toInt()
    textView.text = "$tempInInt°c"
}

@BindingAdapter("tempInCelsiusWithoutSymbol")
fun tempInCelsiusWithoutSymbol(textView: TextView, temp: Double) {
    val tempInInt = temp.toInt()
    textView.text = tempInInt.toString()
}

@BindingAdapter("feelsLikeTemp")
fun feelsLikeTemp(textView: TextView, temp: Double) {
    val tempToInt = temp.toInt()
    textView.text = "feels like $tempToInt°c"
}

@BindingAdapter("setRainInMm")
fun setRainInMm(textView: TextView, precip: Double) {
    val precipInInt = precip.toInt()
    textView.text = "$precipInInt mm"
}

@BindingAdapter("setWindInKph")
fun setWindInKph(textView: TextView, wind: Double) {
    val windToString = wind.toString()
    textView.text = "$windToString kph"
}

@BindingAdapter("setDayFromDate")
fun setDayFromDate(textView: TextView, inputDate: String) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val calendar = Calendar.getInstance()
    try {
        val currentDate = inputFormat.parse(inputDate)
        if (currentDate != null) {
            calendar.time = currentDate
        }
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> textView.text = "Sunday"
            Calendar.MONDAY -> textView.text = "Monday"
            Calendar.TUESDAY -> textView.text = "Tuesday"
            Calendar.WEDNESDAY -> textView.text = "Wednesday"
            Calendar.THURSDAY -> textView.text = "Thursday"
            Calendar.FRIDAY -> textView.text = "Friday"
            Calendar.SATURDAY -> textView.text = "Saturday"
            else -> textView.text = "Unknown"
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@BindingAdapter("hourFromDate")
fun setHourFromDate(textView: TextView, inputTime: Long) {
    val currentTime = System.currentTimeMillis() / 1000
    val closestTime = abs(inputTime - currentTime)
    if (closestTime <= 3600) {
        textView.text = "Now"
    } else {
        val timeFormat = SimpleDateFormat("HH")
        val calendar = Calendar.getInstance()
        calendar.time = Date(inputTime * 1000)
        val hour = timeFormat.format(calendar.time)
        textView.text = hour
    }
}