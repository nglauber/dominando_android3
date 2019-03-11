package dominando.android.hotel.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository

class HotelFormViewModel(
    private val repository: HotelRepository
) : ViewModel() {
    val photoUrl = MutableLiveData<String>()
    private val validator by lazy { HotelValidator() }

    fun loadHotel(id: Long): LiveData<Hotel> {
        return repository.hotelById(id)
    }

    fun saveHotel(hotel: Hotel): Boolean {
        return validator.validate(hotel)
            .also { validated ->
                if (validated) repository.save(hotel)
            }
    }
}
