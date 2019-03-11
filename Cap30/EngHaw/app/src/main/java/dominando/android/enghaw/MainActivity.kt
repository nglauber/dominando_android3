package dominando.android.enghaw

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        viewPager.adapter = AlbumPagerAdapter(supportFragmentManager)
        tabs.setupWithViewPager(viewPager)
    }

    inner class AlbumPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return if (position == 0) {
                AlbumListWebFragment()
            } else {
                AlbumListDbFragment()
            }
        }
        override fun getCount() = 2

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) {
                getString(R.string.tab_all)
            } else {
                getString(R.string.tab_favorites)
            }
        }
    }
}