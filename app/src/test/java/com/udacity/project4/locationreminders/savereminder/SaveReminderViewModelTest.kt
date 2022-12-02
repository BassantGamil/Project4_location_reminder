package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

//Provide testing to the RemindersListViewModel and its live data objects
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    //make sample of fake data
    val reminder = ReminderDataItem("Home", "Fav place", "Egy", 3.2132, 6.9076)

    // Use a fake repository to be injected to the viewModel
    private lateinit var fakeDataSource: FakeDataSource

    //instance from remindersListViewModel to use it in test
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule

    // Executes each task synchronously using Architecture Components.
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher roles for unit testing.
    var mainCoroutineRule = MainCoroutineRule()

    //get reference from fakeDataSource
    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    //stop for koin
    @After
    fun tearDown() {
        stopKoin()
    }

    //validate method to check on data in viewModel
    @Test
    fun validateEnteredData() {
        viewModel.validateAndSaveReminder(reminder)
        MatcherAssert.assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(), CoreMatchers.notNullValue()
        )
    }

    //
    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        viewModel.validateAndSaveReminder(reminder)

    }


    //assert data when loading it using dispatcher
    @Test
    fun loadReminders_showLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(reminder)
        Assert.assertThat((viewModel.showNoData.toString()), Matchers.`is`("True"))
        mainCoroutineRule.resumeDispatcher()
        Assert.assertThat((viewModel.showNoData.toString()), Matchers.`is`("false"))
    }

}