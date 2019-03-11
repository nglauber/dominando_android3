package dominando.android.hotel.form

import dominando.android.hotel.model.Hotel

class HotelValidator {
    fun validate(info: Hotel) = with(info) {
        checkName(name) && checkAddress(address)
    }
    private fun checkName(name: String) = name.length in 2..20
    private fun checkAddress(address: String) = address.length in 4..80
}