package dominando.android.hotel.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository

class HotelDetailsViewModel(
    private val repository: HotelRepository
) : ViewModel() {
    fun loadHotelDetails(id: Long): LiveData<Hotel> {
        return repository.hotelById(id)
    }
}
