package com.example.bitechecktest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    // LiveData to hold the log that the user wants to undo.
    // It's nullable so we can clear it after it's been used.
    private val _undoneLog = MutableLiveData<DailyLog?>()
    val undoneLog: LiveData<DailyLog?> = _undoneLog

    /**
     * Called by FoodLogFragment when the user presses "Undo".
     */
    fun setUndoneLog(log: DailyLog) {
        _undoneLog.value = log
    }

    /**
     * Called by HomeFragment after it has restored the data, to prevent
     * the action from happening again on screen rotation.
     */
    fun consumeUndoneLog() {
        _undoneLog.value = null
    }
}