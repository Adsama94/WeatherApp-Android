package com.adsama.weatherapp.utils

import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DebouncedTextWatcher(
    private val delayMillis: Long,
    private val callback: (String) -> Unit
) : TextWatcher {

    private var debounceJob: Job? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(editable: Editable?) {
        debounceJob?.cancel()
        debounceJob = CoroutineScope(Dispatchers.Main).launch {
            delay(delayMillis)
            val input = editable?.toString() ?: ""
            callback.invoke(input)
        }
    }

}