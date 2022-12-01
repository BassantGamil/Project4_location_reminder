package com.udacity.project4.locationreminders.savereminder

import android.opengl.Visibility
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.ArgumentMatchers.matches

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    val reminder = ReminderDataItem("Home", "Fav place", "Egy", 3.2132, 6.9076)

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun validateEnteredData() {
        assertThat(viewModel.validateEnteredData(reminder)).isFalse()
    }

    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        viewModel.validateAndSaveReminder(reminder)

    }

}