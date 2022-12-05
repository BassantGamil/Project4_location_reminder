package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {
    //make sample of fake data
    val reminder = ReminderDTO("Home", "Fav place", "Egy", 3.2132, 6.9076)

    // Use a fake repository to be injected to the viewModel
    private lateinit var repository: ReminderDataSource

    //app context from application
    private lateinit var appContext: Application

    // An idling resource that waits for Data Binding to have no pending bindings
    private val dbBinding = DataBindingIdlingResource()

    @get:Rule
    // Executes each task synchronously using Architecture Components
    private val instantExecutor = InstantTaskExecutorRule()


    //As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
    // at this step we will initialize Koin related code to be able to use it in out testing.
    @Before
    fun init() {
        stopKoin()
        appContext = getApplicationContext()
        val myModule = module {
            //Declaring a ViewModel be later injected into Fragment with dedicated injector using by viewModel()
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

        //Declaring koin module
        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }
        //Get the Repository
        repository = get()
        //reset the fake dataSource
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    //verify navigate to launch reminderListFragment
    @Test
    fun clickFABToNavigateToSaveReminderFragment() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    //load data in repo and launch ui of reminder container in List Fragment with data details( title, description and location)
    // after checking on them that matches Views that are currently displayed on the screen to the user.
    @Test
    fun dataDisplayedOnUi() = runBlockingTest {
        repository.saveReminder(reminder)
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        onView(withText(reminder.title)).check(matches(isDisplayed()))
        onView(withText(reminder.description)).check(matches(isDisplayed()))
        onView(withText(reminder.location)).check(matches(isDisplayed()))
    }

    //check when save data that get from reminder
    // Create mock object of given navController::class to Navigation flows and destinations are determined by the navigation graph owned by the controller
    //Create ViewInteraction for button fab that save data of reminder using perform and given action click()
    //navigate to right direction with saved data of reminder
    @Test
    fun checkSaveButton() {
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    //show error message when bundle is empty from get data from reminder
    // after Creating mock object of given navController::class to Navigation flows and destinations are determined by the navigation graph owned by the controller
    // monitor fragment of scenario that contain on launch reminder list fragment to data binding
    // use it if not found data in fragment
    @Test
    fun errorMessageShowNoData() {
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        val navController = mock(NavController::class.java)
        dbBinding.monitorFragment(scenario)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }
        onView(withText("No Data")).check(matches(isDisplayed()))
    }
}
