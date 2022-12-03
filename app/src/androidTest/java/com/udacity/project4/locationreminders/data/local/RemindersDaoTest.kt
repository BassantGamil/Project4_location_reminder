package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull
import org.junit.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    //make sample of fake data
    val reminder = ReminderDTO("Home", "Fav place", "Egy", 3.2132, 6.9076)

    @get:Rule
    // Executes each task synchronously using Architecture Components.
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

    //insert data details in reminder item and save it
    @Test
    fun insertReminderByIdTest() = runBlockingTest {
        dao.saveReminder(reminder)
        val getdata = database.reminderDao().getReminderById(reminder.id)
        Assert.assertThat<ReminderDTO>(getdata as ReminderDTO, IsNull.notNullValue())
        Assert.assertThat(getdata.title, `is`(reminder.title))
        Assert.assertThat(getdata.description, `is`(reminder.description))
        Assert.assertThat(getdata.location, `is`(reminder.location))
        Assert.assertThat(getdata.latitude, `is`(reminder.latitude))
        Assert.assertThat(getdata.longitude, `is`(reminder.longitude))

    }

    //get data details from reminder item by id
    @Test
    fun getDataByReminderByIdTest() = runBlockingTest {
        dao.saveReminder(reminder)
        val rms = database.reminderDao().getReminderById(reminder.id)
        Assert.assertThat<ReminderDTO>(rms as ReminderDTO, IsNull.notNullValue())
        Assert.assertThat(rms.title, `is`(reminder.title))
        Assert.assertThat(rms.description, `is`(reminder.description))
        Assert.assertThat(rms.location, `is`(reminder.location))
        Assert.assertThat(rms.latitude, `is`(reminder.latitude))
        Assert.assertThat(rms.longitude, `is`(reminder.longitude))
    }
}