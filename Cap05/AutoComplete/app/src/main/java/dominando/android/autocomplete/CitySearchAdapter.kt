package dominando.android.autocomplete

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

class CitySearchAdapter(ctx: Context,
                        layout: Int,
                        private val fullList: List<String>
) : ArrayAdapter<String>(ctx, layout, fullList), Filterable {

    private var results: List<String>
    private val cityFilter: Filter

    init {
        this.results = fullList
        this.cityFilter = CityFilter()
    }

    override fun getCount(): Int = results.size

    override fun getItem(position: Int): String? =
        if (results.isNotEmpty() && position < results.size) {
            results[position]
        } else {
            null
        }

    override fun getFilter(): Filter = cityFilter

    private inner class CityFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val filterResults = Filter.FilterResults()
            var temp: List<String> = ArrayList()
            if (constraint != null) {
                val term = constraint.toString().trim().toLowerCase()
                temp = fullList.filter {
                    it.removeAccents().indexOf(term) > -1
                }
            }
            filterResults.values = temp
            filterResults.count = temp.size
            return filterResults
        }
        override fun publishResults(constraint: CharSequence?,
                                    filterResults: Filter.FilterResults) {
            results = filterResults.values as List<String>
            notifyDataSetChanged()
        }
    }
}
