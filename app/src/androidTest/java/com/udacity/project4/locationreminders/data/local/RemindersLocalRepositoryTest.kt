package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsNull.notNullValue
import org.junit.*
import org.junit.Assert.assertThat
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLoRemindersDaoTestLocalRepositoryTest {

    //make sample of fake data
    val reminder = ReminderDTO("Home", "Fav place", "Egy", 3.2132, 6.9076)

    @get:Rule
    // Executes each task synchronously using Architecture Components.
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()

    //insert data details in reminder item and save it
    @Test
    fun insertReminder() = runBlockingTest {
        repository.saveReminder(reminder)
        val getdata = repository.getReminders()
        assertThat<ReminderDTO>(getdata as ReminderDTO, notNullValue())
        assertThat(getdata.title, `is`(reminder.title))
        assertThat(getdata.description, `is`(reminder.description))
        assertThat(getdata.location, `is`(reminder.location))
        assertThat(getdata.latitude, `is`(reminder.latitude))
        assertThat(getdata.longitude, `is`(reminder.longitude))

    }

    //get data details from reminder item by id
    @Test
    fun getDataByReminderSuccess() = runBlockingTest {
        repository.saveReminder(reminder)
        val rms = repository.getReminder(reminder.id)
        assertThat<ReminderDTO>(rms as ReminderDTO, notNullValue())
        assertThat(rms.title, `is`(reminder.title))
        assertThat(rms.description, `is`(reminder.description))
        assertThat(rms.location, `is`(reminder.location))
        assertThat(rms.latitude, `is`(reminder.latitude))
        assertThat(rms.longitude, `is`(reminder.longitude))
    }
}