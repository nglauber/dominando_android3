package dominando.android.hotel

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dominando.android.hotel.RatingBarAction.Companion.setRating
import dominando.android.hotel.RatingBarMatcher.Companion.withRatingValue
import dominando.android.hotel.common.HotelActivity
import dominando.android.hotel.repository.HotelRepository
import dominando.android.hotel.repository.room.HotelDatabase
import dominando.android.hotel.repository.room.RoomRepository
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasToString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules

@RunWith(AndroidJUnit4::class)
class HotelCrudTest {
    @get:Rule
    val activityRule = ActivityTestRule(HotelActivity::class.java)

    init {
        loadKoinModules(module {
            single(override = true) {
                val db = Room.inMemoryDatabaseBuilder(
                    InstrumentationRegistry.getInstrumentation().context,
                    HotelDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()
                RoomRepository(db) as HotelRepository
            }
        })
    }

    @Test
    fun crudTest() {
        add()
        edit()
        delete()
    }

    private fun add() {
        onView(withId(R.id.fabAdd)).perform(click())
        fillHotelForm(NEW_HOTEL_NAME, NEW_HOTEL_ADDRESS, NEW_HOTEL_RATING)
        listViewHasHotelWithName(NEW_HOTEL_NAME)
    }

    private fun edit() {
        clickOnHotelName(NEW_HOTEL_NAME)
        onView(withId(R.id.action_edit)).perform(click())
        fillHotelForm(CHANGED_HOTEL_NAME, CHANGED_HOTEL_ADDRESS, CHANGED_HOTEL_RATING)
        onView(withId(R.id.txtName)).check(matches(withText(CHANGED_HOTEL_NAME)))
        onView(withId(R.id.txtAddress)).check(matches(withText(CHANGED_HOTEL_ADDRESS)))
        onView(withId(R.id.rtbRating)).check(matches(withRatingValue(CHANGED_HOTEL_RATING)))
        pressBack()
        listViewHasHotelWithName(CHANGED_HOTEL_NAME)
    }

    private fun delete() {
        onData(hasToString(containsString(CHANGED_HOTEL_NAME)))
            .inAdapterView(withId(android.R.id.list)).atPosition(0)
            .perform(longClick())
        onView(withId(R.id.action_delete)).perform(click())
    }

    private fun fillHotelForm(name: String, address: String, rating: Float) {
        onView(withId(R.id.edtName)).perform(replaceText(name))
        onView(withId(R.id.edtAddress)).perform(replaceText(address))
        onView(withId(R.id.edtAddress)).perform(pressImeActionButton())
        onView(withId(R.id.rtbRating)).perform(setRating(rating))
        closeSoftKeyboard()
    }

    private fun listViewHasHotelWithName(name: String) {
        onData(hasToString(containsString(name)))
            .inAdapterView(withId(android.R.id.list))
            .atPosition(0)
            .onChildView(withId(R.id.txtName))
            .check(matches(withText(containsString(name))))
    }

    private fun clickOnHotelName(name: String) {
        onData(hasToString(containsString(name)))
            .inAdapterView(withId(android.R.id.list))
            .atPosition(0)
            .perform(click())
    }

    companion object {
        private const val NEW_HOTEL_NAME = "Hotel de Teste"
        private const val NEW_HOTEL_ADDRESS = "Rua tal"
        private const val CHANGED_HOTEL_NAME = "Hotel Modificado"
        private const val CHANGED_HOTEL_ADDRESS = "Rua modificada"
        private const val NEW_HOTEL_RATING = 3.5f
        private const val CHANGED_HOTEL_RATING = 4.5f
    }
}
