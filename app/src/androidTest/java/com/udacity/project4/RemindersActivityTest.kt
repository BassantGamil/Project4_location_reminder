package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.annotation.StringRes
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import java.lang.Math.random

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    val reminder = ReminderDTO("Home", "Fav place", "Egy", 3.2132, 6.9076)

    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    //pass args to function to make check on item and show message
    fun checkSnackBarTextMatches(@StringRes stringRes: Int) {
        onView(withId(R.id.snackbar_text))
            .check(matches(withText(stringRes)))
    }

    //implement data from reminder and check on title,description and location
    @Test
    fun startTest() {
        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.addReminderFAB)).perform(click())
        val title = "My work location ${random()}"
        val description = "This is my work location ${random()}"
        onView(withId(R.id.reminderTitle)).perform(typeText(title))
        onView(withId(R.id.reminderDescription)).perform(typeText(description))
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.save_button)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(description)).check(matches(isDisplayed()))
    }

    fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity {
        lateinit var activity: Activity
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    //check on validation for title filed and show error message on by pass message in params for function snackbar
    @Test
    fun testSaveReminderWithEmptyTitle() {
        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.addReminderFAB)).perform(click())
        val title = "This is my work location ${random()}"
        onView(withId(R.id.reminderDescription)).perform(typeText(title))
        Espresso.pressBack()
        onView(withId(R.id.saveReminder)).perform(click())
        checkSnackBarTextMatches(R.string.err_enter_title)
    }

    //check on validation for set location filed and show error message on by pass message in params for function snackbar
    @Test
    fun testSaveReminderWithNotSelectedLocation() {
        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.addReminderFAB)).perform(click())
        val title = "My work location ${random()}"
        val description = "This is my work location ${random()}"
        onView(withId(R.id.reminderTitle)).perform(typeText(title))
        onView(withId(R.id.reminderDescription)).perform(typeText(description))
        Espresso.pressBack()
        onView(withId(R.id.saveReminder)).perform(click())
        checkSnackBarTextMatches(R.string.err_select_location)
    }
}