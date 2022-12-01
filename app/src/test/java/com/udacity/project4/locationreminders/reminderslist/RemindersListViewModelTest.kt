package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    val reminder = ReminderDTO("Home", "Fav place", "Egy", 3.2132, 6.9076)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    var mainCoroutineRule = MainCoroutineRule()

    var fakeDataSource = FakeDataSource()

    private lateinit var viewModel: RemindersListViewModel

    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_showLoading() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertThat((viewModel.showNoData.toString()), `is`("True"))
        mainCoroutineRule.resumeDispatcher()
        assertThat((viewModel.showNoData.toString()), `is`("false"))
    }

    @Test
    fun loadReminders_remainderListNotEmpty() = mainCoroutineRule.runBlockingTest {
        fakeDataSource.saveReminder(reminder)
        viewModel.loadReminders()
        assertThat((viewModel.remindersList.toString()), `is`(""))
    }

    @Test
    fun loadReminders_updateSnackBarValue() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        mainCoroutineRule.resumeDispatcher()
        assertThat((viewModel.showSnackBar.toString()), `is`("Error getting reminders"))
    }
}