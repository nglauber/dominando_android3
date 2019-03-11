package dominando.android.hotel.repository

import androidx.lifecycle.LiveData
import dominando.android.hotel.model.Hotel

interface HotelRepository {
    fun save(hotel: Hotel)
    fun insert(hotel: Hotel): Long
    fun update(hotel: Hotel)
    fun remove(vararg hotels: Hotel)
    fun hotelById(id: Long): LiveData<Hotel>
    fun search(term: String): LiveData<List<Hotel>>
    fun pending(): List<Hotel>
    fun hotelByServerId(serverId: Long): Hotel?
}