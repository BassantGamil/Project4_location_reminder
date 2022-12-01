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
        return if(isReturnError){
            Result.Error("Error Retrieving Data " , 404)
        }
        else{
            Result.Success(ArrayList(reminders))
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (isReturnError) {
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