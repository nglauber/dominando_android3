package dominando.android.navegacao

import android.content.Context
import android.content.res.TypedArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabsPagerAdapter(ctx: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val tabTitles: Array<String> = ctx.resources.getStringArray(R.array.sections)
    private val bgColors: TypedArray = ctx.resources.obtainTypedArray(R.array.bg_colors)
    private val textColors: TypedArray = ctx.resources.obtainTypedArray(R.array.text_colors)

    override fun getItem(position: Int): Fragment {
        return SecondLevelFragment.newInstance(
            tabTitles[position],
            bgColors.getColor(position, 0),
            textColors.getColor(position, 0))
    }
    override fun getCount(): Int {
        return 3
    }
    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }
}
