package dominando.android.enghaw

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlbumUITest {
    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before fun setUp() {
        activityRule.activity.deleteDatabase("engHawDb")
    }
    @Test fun test_add_favorite() {
        val tagRecyclerWeb = "web"
        val tagRecyclerFavorite = "fav"
        val albumTitleToBeClicked = "Minuano"
        IdlingRegistry.getInstance().register(IdleResource.instance)
        onView(withTagValue(`is`(tagRecyclerWeb as Any)))
            .perform(scrollTo<RecyclerView.ViewHolder>(
                hasDescendant(withText(albumTitleToBeClicked))
            ))
        onView(withTagValue(`is`(tagRecyclerWeb as Any)))
            .perform(actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText(albumTitleToBeClicked)),
                click()
            ))
        onView(withId(R.id.txtTitle))
            .check(matches(withText(albumTitleToBeClicked)))
        onView(withId(R.id.fabFavorite))
            .perform(click())
        pressBack()
        onView(withId(R.id.viewPager))
            .perform(swipeLeft())
        onView(withTagValue(`is`(tagRecyclerFavorite as Any)))
            .check(matches(hasDescendant(withText(albumTitleToBeClicked))))
    }
}
