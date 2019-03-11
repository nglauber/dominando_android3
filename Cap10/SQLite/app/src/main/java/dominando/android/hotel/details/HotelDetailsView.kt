package dominando.android.hotel.details

import dominando.android.hotel.model.Hotel

interface HotelDetailsView {
    fun showHotelDetails(hotel: Hotel)
    fun errorHotelNotFound()
}
