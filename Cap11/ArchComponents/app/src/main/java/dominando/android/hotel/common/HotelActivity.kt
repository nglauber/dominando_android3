package dominando.android.hotel.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import dominando.android.hotel.*
import dominando.android.hotel.details.HotelDetailsActivity
import dominando.android.hotel.details.HotelDetailsFragment
import dominando.android.hotel.form.HotelFormFragment
import dominando.android.hotel.list.HotelListFragment
import dominando.android.hotel.list.HotelListViewModel
import dominando.android.hotel.model.Hotel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_hotel.*

class HotelActivity: AppCompatActivity(),
    HotelListFragment.OnHotelClickListener,
    SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    private val viewModel: HotelListViewModel by viewModel()
    private var searchView: SearchView? = null
    private val listFragment: HotelListFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentList) as HotelListFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel)
        fabAdd.setOnClickListener {
            listFragment.hideDeleteMode()
            HotelFormFragment.newInstance().open(supportFragmentManager)
        }
    }

    override fun onHotelClick(hotel: Hotel) {
        if (isTablet()) {
            viewModel.hotelIdSelected = hotel.id
            showDetailsFragment(hotel.id)
        } else {
            showDetailsActivity(hotel.id)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.hotel, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        searchItem?.setOnActionExpandListener(this)
        searchView = searchItem?.actionView as SearchView
        searchView?.queryHint = getString(R.string.hint_search)
        searchView?.setOnQueryTextListener(this)
        if (viewModel.getSearchTerm()?.value?.isNotEmpty() == true) {
            Handler().post {
                val query = viewModel.getSearchTerm()?.value
                searchItem.expandActionView()
                searchView?.setQuery(query, true)
                searchView?.clearFocus()
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_info ->
                AboutDialogFragment().show(supportFragmentManager, "sobre")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        listFragment.search(newText ?: "")
        return true
    }

    override fun onMenuItemActionExpand(item: MenuItem?) = true // para expandir a view

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        listFragment.search()
        return true
    }

    private fun isTablet()  = resources.getBoolean(R.bool.tablet)

    private fun isSmartphone() = resources.getBoolean(R.bool.smartphone)

    private fun showDetailsFragment(hotelId: Long) {
        searchView?.setOnQueryTextListener(null)
        val fragment = HotelDetailsFragment.newInstance(hotelId)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.details, fragment,
                HotelDetailsFragment.TAG_DETAILS
            )
            .commit()
    }

    private fun showDetailsActivity(hotelId: Long) {
        HotelDetailsActivity.open(this, hotelId)
    }
}
