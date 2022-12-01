package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(
    private val reminders: MutableList<ReminderDTO> = mutableListOf()
) : ReminderDataSource {


    private var isReturnFalse = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (isReturnFalse) {
            Result.Error("Error occurred")
        } else {
            Result.Success(reminders)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (isReturnFalse) {
            Result.Error("Error")
        } else {
            val reminder = reminders.find { it.id == id }

            if (reminder == null) {
                Result.Error("Not found")
            } else {
                Result.Success(reminder)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }


}