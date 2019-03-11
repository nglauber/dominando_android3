package dominando.android.navegacao

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    private val drawerToggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(this,
            drawerLayout, toolbar, R.string.app_name, R.string.app_name)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectMenuOption(menuItem)
            true
        }
        if (savedInstanceState == null) {
            selectMenuOption(navigationView.menu.findItem(R.id.action_tab))
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun selectMenuOption(menuItem: MenuItem) {
        menuItem.isChecked = true
        drawerLayout.closeDrawers()
        val title = menuItem.title.toString()
        val fragment = FirstLevelFragment.newInstance(title)
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment, title)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (content.childCount > 0) { // não adicionamos o primeiro fragment na backstack
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

}
