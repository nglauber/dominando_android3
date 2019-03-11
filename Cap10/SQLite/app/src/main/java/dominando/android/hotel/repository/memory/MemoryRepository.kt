package dominando.android.hotel.repository.memory

import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository

object MemoryRepository : HotelRepository {
    private var nextId = 1L
    private val hotelsList = mutableListOf<Hotel>()
    init {
        save(
            Hotel(
                0,
                "New Beach Hotel",
                "Av. Boa Viagem",
                4.5f
            )
        )
        save(
            Hotel(
                0,
                "Recife Hotel",
                "Av. Boa Viagem",
                4.0f
            )
        )
        save(
            Hotel(
                0,
                "Canario Hotel",
                "Rua dos Navegantes",
                3.0f
            )
        )
        save(
            Hotel(
                0,
                "Byanca Beach Hotel",
                "Rua Mamanguape",
                4.0f
            )
        )
        save(
            Hotel(
                0,
                "Grand Hotel Dor",
                "Av. Bernardo",
                3.5f
            )
        )
        save(
            Hotel(
                0,
                "Hotel Cool",
                "Av. Conselheiro Aguiar",
                4.0f
            )
        )
        save(
            Hotel(
                0,
                "Hotel Infinito",
                "Rua Ribeiro de Brito",
                5.0f
            )
        )
        save(
            Hotel(
                0,
                "Hotel Tulipa",
                "Av. Boa Viagem",
                5.0f
            )
        )
    }
    override fun save(hotel: Hotel) {
        if (hotel.id == 0L) {
            hotel.id = nextId++
            hotelsList.add(hotel)
        } else {
            val index = hotelsList.indexOfFirst { it.id == hotel.id }
            if (index > -1) {
                hotelsList[index] = hotel
            } else {
                hotelsList.add(hotel)
            }
        }
    }
    override fun remove(vararg hotels: Hotel) {
        hotelsList.removeAll(hotels)
    }
    override fun hotelById(id: Long, callback: (Hotel?) -> Unit) {
        callback(hotelsList.find { it.id == id })
    }
    override fun search(term: String, callback: (List<Hotel>) -> Unit) {
        val resultList =
            if (term.isEmpty()) hotelsList
            else hotelsList.filter {
                it.name.toUpperCase().contains(term.toUpperCase())
            }
        callback(resultList.sortedBy { it.name })
    }
}
