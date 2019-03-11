package dominando.android.navegacao

import android.content.res.TypedArray
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bottom_nav.*
import kotlinx.android.synthetic.main.toolbar.*

class BottomNavActivity : AppCompatActivity() {

    private val tabTitles: Array<String> by lazy {
        resources.getStringArray(R.array.sections)
    }
    private val bgColors: TypedArray by lazy {
        resources.obtainTypedArray(R.array.bg_colors)
    }
    private val textColors: TypedArray by lazy {
        resources.obtainTypedArray(R.array.text_colors)
    }
    private val tabIds = listOf(
        R.id.action_favorites, R.id.action_clock, R.id.action_people
    )
    private var currentTabIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        currentTabIndex = savedInstanceState?.getInt(TAB_SELECTED) ?: 0
        showFragment(currentTabIndex)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val index = tabIds.indexOf(item.itemId)
            showFragment(index)
            true
        }
    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(TAB_SELECTED, currentTabIndex)
    }
    private fun showFragment(position: Int) {
        val oldTag = "aba_$currentTabIndex"
        val newTag = "aba_$position"
        currentTabIndex = position
        val transaction = supportFragmentManager.beginTransaction()
        val oldFragment = supportFragmentManager.findFragmentByTag(oldTag)
        if (oldFragment != null) {
            transaction.hide(oldFragment)
        }
        var fragment = supportFragmentManager.findFragmentByTag(newTag)
        if (fragment == null) {
            fragment = SecondLevelFragment.newInstance(
                tabTitles[position],
                bgColors.getColor(position, 0),
                textColors.getColor(position, 0))
            transaction.add(R.id.container, fragment, newTag)
        }
        rootView.setBackgroundColor(bgColors.getColor(currentTabIndex, 0))
        transaction.show(fragment).commit()
    }
    companion object {
        const val TAB_SELECTED = "tabSelected"
    }
}
