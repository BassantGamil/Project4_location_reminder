package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

//Provide testing to the RemindersListViewModel and its live data objects
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    //make sample of fake data
    val reminder = ReminderDTO("Home", "Fav place", "Egy", 3.2132, 6.9076)

    @get:Rule
    // Executes each task synchronously using Architecture Components.
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher roles for unit testing.
    var mainCoroutineRule = MainCoroutineRule()

    // Use a fake repository to be injected to the viewModel
    var fakeDataSource = FakeDataSource()

    //instance from remindersListViewModel to use it in test
    private lateinit var viewModel: RemindersListViewModel

    //get reference from fakeDataSource
    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    //stop for koin
    @After
    fun tearDown() {
        stopKoin()
    }

    //show data when saved successfully
    @Test
    fun loadReminders_remainderListNotEmpty() = mainCoroutineRule.runBlockingTest {
        fakeDataSource.saveReminder(reminder)
        viewModel.loadReminders()
        assertThat(
            viewModel.showSnackBar.value,
            `is`("Success Data")
        )
    }

    //show error when use fake repo will return error message because data is failed to load
    @Test
    fun shouldReturnError() = runBlockingTest {
        fakeDataSource.isReturnError = true
        viewModel.loadReminders()
        MatcherAssert.assertThat(
            viewModel.showSnackBar.value,
            CoreMatchers.`is`("Error Exception Retrieving Data")
        )
    }

    //assert data when loading it using dispatcher
    @Test
    fun loadReminders_showLoading() = mainCoroutineRule.runBlockingTest {
        fakeDataSource.saveReminder(reminder)
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertThat((viewModel.showNoData.toString()), `is`("True"))
        mainCoroutineRule.resumeDispatcher()
        assertThat((viewModel.showNoData.toString()), `is`("false"))
    }
}