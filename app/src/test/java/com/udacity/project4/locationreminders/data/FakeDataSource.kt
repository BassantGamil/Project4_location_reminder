package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource(
    private val reminders: MutableList<ReminderDTO> = mutableListOf()
) : ReminderDataSource {
    var isReturnError = false

    //get list of data after check on this if success or failed
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (isReturnError) {
            Result.Error("Error Retrieving Data ", 404)
        } else {
            Result.Success(ArrayList(reminders))
        }
    }

    //save data in mutable list of reminder
    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    //get item of reminder by id
    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (isReturnError) {
            //If there is Exception.
            Result.Error("Error Exception")
        } else {
            val reminder = reminders.find { it.id == id }
            if (reminder == null) {
                //Error to get Data , not found this
                Result.Error("Not found")
            } else {
                //Get Data Successfully
                Result.Success(reminder)
            }
        }
    }


    override suspend fun deleteAllReminders() {
        //Clear all reminders data
        reminders.clear()
    }
}