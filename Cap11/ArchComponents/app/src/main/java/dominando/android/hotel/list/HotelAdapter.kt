package dominando.android.hotel.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import dominando.android.hotel.R
import dominando.android.hotel.model.Hotel
import kotlinx.android.synthetic.main.item_hotel.view.*

class HotelAdapter(context: Context, hotels: List<Hotel>):
    ArrayAdapter<Hotel>(context, 0, hotels) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val hotel = getItem(position)
        val viewHolder = if (convertView == null) {
            val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_hotel, parent, false)
            val holder = ViewHolder(view)
            view.tag = holder
            holder
        } else {
            convertView.tag as ViewHolder
        }
        viewHolder.txtName.text = hotel?.name
        viewHolder.rtbRating.rating = hotel?.rating ?: 0f
        return viewHolder.view
    }

    class ViewHolder(val view: View) {
        val txtName: TextView = view.txtName
        val rtbRating: RatingBar = view.rtbRating
    }
}
