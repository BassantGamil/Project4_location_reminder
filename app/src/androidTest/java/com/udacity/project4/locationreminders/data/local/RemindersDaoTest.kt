package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    val reminder = ReminderDTO("Home", "Fav place", "Egy", 3.2132, 6.9076)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private lateinit var dao: RemindersDao

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        dao = database.reminderDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertLocationAndGetById() = runBlockingTest {
        database.reminderDao().saveReminder(reminder)
        val rms = database.reminderDao().getReminderById(reminder.id)
        assertThat(rms as ReminderDTO, notNullValue())
        assertThat(rms.id, `is`(reminder.id))
        assertThat(rms.title, `is`(reminder.title))
        assertThat(rms.description, `is`(reminder.description))
        assertThat(rms.location, `is`(reminder.location))
        assertThat(rms.latitude, `is`(reminder.latitude))
        assertThat(rms.longitude, `is`(reminder.longitude))


    }

}