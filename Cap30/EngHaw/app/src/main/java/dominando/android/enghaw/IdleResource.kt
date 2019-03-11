package dominando.android.enghaw

import androidx.test.espresso.idling.CountingIdlingResource

object IdleResource {
    val instance = CountingIdlingResource("ALBUMS")
}