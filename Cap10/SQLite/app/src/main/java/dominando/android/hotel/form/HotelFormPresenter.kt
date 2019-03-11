package dominando.android.hotel.form

import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository

class HotelFormPresenter(
    private val view: HotelFormView,
    private val repository: HotelRepository
) {
    private val validator = HotelValidator()

    fun loadHotel(id: Long) {
        repository.hotelById(id) { hotel ->
            if (hotel != null) {
                view.showHotel(hotel)
            }
        }
    }
    fun saveHotel(hotel: Hotel): Boolean {
        return if (validator.validate(hotel)) {
            try {
                repository.save(hotel)
                true
            } catch (e: Exception) {
                view.errorSaveHotel()
                false
            }
        } else {
            view.errorInvalidHotel()
            false
        }
    }
}
