package dominando.android.orientacao

import android.content.Context
import android.widget.ListView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import kotlinx.android.synthetic.main.activity_main.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)
    lateinit var mainActivity: MainActivity
    lateinit var context: Context

    @Before fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        mainActivity = activityRule.activity
    }
    @Test fun add_text_test() {
        mainActivity.runOnUiThread {
            mainActivity.edtName.setText("Texto 1")
            mainActivity.btnAddClick(mainActivity.btnAdd)
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val listView = mainActivity.findViewById<ListView>(R.id.lstNames)
        assertThat(listView.count, `is`(1))
    }
}
