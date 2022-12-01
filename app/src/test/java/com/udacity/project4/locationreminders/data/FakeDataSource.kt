package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource(
    private val reminders: MutableList<ReminderDTO> = mutableListOf()
) : ReminderDataSource {
var isReturnError = false
    fun setIsdReturnError(_isReturnError:Boolean){
        this.isReturnError=_isReturnError
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllReminders() {
        TODO("Not yet implemented")
    }
}